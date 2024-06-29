package com.techyourchance.android.apkupdate

import com.google.gson.Gson
import com.techyourchance.android.common.Constants
import com.techyourchance.android.common.application.AppVersionInfo
import com.techyourchance.android.common.application.AppVersionInfoProvider
import com.techyourchance.android.common.usecases.UseCaseResult
import com.techyourchance.android.networking.LatestApkInfoResponseSchema
import com.techyourchance.android.networking.TechYourChanceApi
import com.techyourchance.android.common.settings.SettingsManager
import com.techyourchance.android.testdoubles.DateTimeProviderImplTd
import com.techyourchance.android.testdoubles.SettingsEntriesFactoryTd
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@Suppress("PrivatePropertyName")
class FetchLatestApkInfoUseCaseTest {

    // region constants ----------------------------------------------------------------------------
    private val BUILD_TYPE = "test"

    private val CURRENT_VERSION_NAME = "0.3"
    private val CURRENT_VERSION_CODE = 7
    private val CURRENT_VERSION_FILE_NAME = "current.apk"

    private val NEW_VERSION_NAME = "0.5"
    private val NEW_VERSION_CODE = 10
    private val NEW_VERSION_FILE_NAME = "new.apk"
    // endregion constants -------------------------------------------------------------------------

    // region helper fields ------------------------------------------------------------------------
    private val tycApi = mockk<TechYourChanceApi>()
    private val appVersionInfoProvider = mockk<AppVersionInfoProvider>()
    private val settingsManager = SettingsManager(SettingsEntriesFactoryTd(), Gson())
    private val dateTimeProvider = DateTimeProviderImplTd()
    // endregion helper fields ---------------------------------------------------------------------

    private lateinit var SUT: FetchLatestApkInfoUseCase

    @Before
    fun setup() {
        SUT = FetchLatestApkInfoUseCase(tycApi, appVersionInfoProvider, settingsManager, dateTimeProvider)
        every { appVersionInfoProvider.getAppVersionInfo() } returns AppVersionInfo(BUILD_TYPE, CURRENT_VERSION_NAME, CURRENT_VERSION_CODE)
    }

    @Test
    fun `new version available returns correct data`() = runTest {
        // Arrange
        coEvery { tycApi.latestApkInfo(any()) } returns Response.success(
            200,
            LatestApkInfoResponseSchema(NEW_VERSION_NAME, NEW_VERSION_CODE, NEW_VERSION_FILE_NAME)
        )
        // Act
        val result = SUT.fetchLatestApkInfo()
        // Assert
        result.shouldBe(
            UseCaseResult.Success(ApkInfo(true, NEW_VERSION_NAME, NEW_VERSION_CODE, NEW_VERSION_FILE_NAME))
        )
    }

    @Test
    fun `no new version returns correct data`() = runTest {
        // Arrange
        coEvery { tycApi.latestApkInfo(any()) } returns Response.success(
            200,
            LatestApkInfoResponseSchema(CURRENT_VERSION_NAME, CURRENT_VERSION_CODE, CURRENT_VERSION_FILE_NAME)
        )
        // Act
        val result = SUT.fetchLatestApkInfo()
        // Assert
        result.shouldBe(UseCaseResult.Success(
            ApkInfo(false, CURRENT_VERSION_NAME, CURRENT_VERSION_CODE, CURRENT_VERSION_FILE_NAME))
        )
    }

    @Test
    fun `repeated check before min time interval new version available returns null data`() = runTest {
        // Arrange
        coEvery { tycApi.latestApkInfo(any()) } returns Response.success(
            200,
            LatestApkInfoResponseSchema(NEW_VERSION_NAME, NEW_VERSION_CODE, NEW_VERSION_FILE_NAME)
        )
        val initialZonedDateTime = ZonedDateTime.now()
        dateTimeProvider.stubZonedDateTime = initialZonedDateTime
        // Act
        SUT.fetchLatestApkInfo()
        dateTimeProvider.stubZonedDateTime = initialZonedDateTime.plus(Constants.APK_VERSION_CHECK_MIN_INTERVAL_MS - 1, ChronoUnit.MILLIS)
        val result = SUT.fetchLatestApkInfo()
        // Assert
        result.shouldBe(
            UseCaseResult.Success(ApkInfo.NULL_APK_INFO)
        )
    }

    @Test
    fun `repeated check after min time interval new version available returns correct data`() = runTest {
        // Arrange
        coEvery { tycApi.latestApkInfo(any()) } returns Response.success(
            200,
            LatestApkInfoResponseSchema(NEW_VERSION_NAME, NEW_VERSION_CODE, NEW_VERSION_FILE_NAME)
        )
        val initialZonedDateTime = ZonedDateTime.now()
        dateTimeProvider.stubZonedDateTime = initialZonedDateTime
        // Act
        SUT.fetchLatestApkInfo()
        dateTimeProvider.stubZonedDateTime = initialZonedDateTime.plus(Constants.APK_VERSION_CHECK_MIN_INTERVAL_MS + 1, ChronoUnit.MILLIS)
        val result = SUT.fetchLatestApkInfo()
        // Assert
        result.shouldBe(
            UseCaseResult.Success(ApkInfo(true, NEW_VERSION_NAME, NEW_VERSION_CODE, NEW_VERSION_FILE_NAME))
        )
    }

    @Test
    fun `network request fails returns failure`() = runTest {
        // Arrange
        coEvery { tycApi.latestApkInfo(any()) } returns Response.error(400, "".toResponseBody("application/json".toMediaType()))
        // Act
        val result = SUT.fetchLatestApkInfo()
        // Assert
        result.shouldBeInstanceOf<UseCaseResult.Failure<ApkInfo>>()
    }

    @Test
    fun `network request fails with exception returns failure`() = runTest {
        // Arrange
        coEvery { tycApi.latestApkInfo(any()) } throws RuntimeException()
        // Act
        val result = SUT.fetchLatestApkInfo()
        // Assert
        result.shouldBeInstanceOf<UseCaseResult.Failure<ApkInfo>>()
    }

    // region helper methods -----------------------------------------------------------------------
    // endregion helper methods --------------------------------------------------------------------

    // region helper classes -----------------------------------------------------------------------
    // endregion helper classes --------------------------------------------------------------------

}
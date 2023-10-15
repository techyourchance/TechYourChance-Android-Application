package com.techyourchance.android.apkupdate

import com.techyourchance.android.common.application.AppVersionInfo
import com.techyourchance.android.common.application.AppVersionInfoProvider
import com.techyourchance.android.common.usecases.UseCaseResult
import com.techyourchance.android.networking.LatestApkInfoResponseSchema
import com.techyourchance.android.networking.TechYourChanceApi
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldHave
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.EMPTY_RESPONSE
import org.junit.Before
import org.junit.Test
import retrofit2.Response

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
    // endregion helper fields ---------------------------------------------------------------------

    private lateinit var SUT: FetchLatestApkInfoUseCase

    @Before
    fun setup() {
        SUT = FetchLatestApkInfoUseCase(tycApi, appVersionInfoProvider)
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
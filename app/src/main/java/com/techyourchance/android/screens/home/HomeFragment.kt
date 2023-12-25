package com.techyourchance.android.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techyourchance.android.R
import com.techyourchance.android.apkupdate.ApkInfo
import com.techyourchance.android.apkupdate.FetchLatestApkInfoUseCase
import com.techyourchance.android.apkupdate.UpdateApkUseCase
import com.techyourchance.android.common.eventbus.EventBusSubscriber
import com.techyourchance.android.common.usecases.UseCaseResult
import com.techyourchance.android.screens.common.ScreenSpec
import com.techyourchance.android.screens.common.ScreensNavigator
import com.techyourchance.android.screens.common.dialogs.DialogsNavigator
import com.techyourchance.android.screens.common.dialogs.prompt.PromptDialogButton
import com.techyourchance.android.screens.common.dialogs.prompt.PromptDialogDismissedEvent
import com.techyourchance.android.screens.common.fragments.BaseFragment
import com.techyourchance.android.screens.common.mvcviews.ViewMvcFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class HomeFragment : BaseFragment(), HomeViewMvc.Listener {

    @Inject lateinit var viewMvcFactory: ViewMvcFactory
    @Inject lateinit var dialogsNavigator: DialogsNavigator
    @Inject lateinit var screensNavigator: ScreensNavigator
    @Inject lateinit var eventBusSubscriber: EventBusSubscriber
    @Inject lateinit var fetchLatestApkInfoUseCase: FetchLatestApkInfoUseCase
    @Inject lateinit var updateApkUseCase: UpdateApkUseCase

    private lateinit var viewMvc: HomeViewMvc

    private var apkUpdateJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (!this::viewMvc.isInitialized) {
            viewMvc = viewMvcFactory.newHomeViewMvc(container)
            viewMvc.bindDestinations(getDestinations())
        }
        return viewMvc.getRootView()
    }

    private fun getDestinations(): List<FromHomeDestination> {
        return listOf(
            FromHomeDestination(
                getString(R.string.screen_user_interfaces),
                ScreenSpec.UserInterfaces,
                FromHomeDestinationType.LIST_OF_SCREENS
            ),
            FromHomeDestination(
                getString(R.string.screen_benchmarks),
                ScreenSpec.Benchmarks,
                FromHomeDestinationType.LIST_OF_SCREENS
            ),
            FromHomeDestination(
                getString(R.string.screen_stackoverflow_client),
                ScreenSpec.StackOverflowQuestionsList,
                FromHomeDestinationType.GROUP_OF_SCREENS
            ),
            FromHomeDestination(
                getString(R.string.screen_biometric_auth),
                ScreenSpec.BiometricLock,
                FromHomeDestinationType.SINGLE_SCREEN
            ),
            FromHomeDestination(
                getString(R.string.screen_ndk_basics),
                ScreenSpec.NdkBasics,
                FromHomeDestinationType.SINGLE_SCREEN
            ),
            FromHomeDestination(
                getString(R.string.screen_foreground_service),
                ScreenSpec.ForegroundService,
                FromHomeDestinationType.SINGLE_SCREEN
            ),
            FromHomeDestination(
                getString(R.string.screen_work_manager),
                ScreenSpec.WorkManager,
                FromHomeDestinationType.SINGLE_SCREEN
            ),
            FromHomeDestination(
                getString(R.string.screen_compose_overlay),
                ScreenSpec.ComposeOverlay,
                FromHomeDestinationType.SINGLE_SCREEN
            ),
        )
    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)

        eventBusSubscriber.register(this)

        coroutineScope.launch {
            val latestApkInfoResult = fetchLatestApkInfoUseCase.fetchLatestApkInfo()
            if (latestApkInfoResult is UseCaseResult.Success && latestApkInfoResult.data.isNewerVersion) {
                dialogsNavigator.showUpdateApkPromptDialog(DIALOG_ID_UPDATE_APK, latestApkInfoResult.data)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewMvc.unregisterListener(this)
        eventBusSubscriber.unregister(this)

        apkUpdateJob?.cancel()
    }

    override fun onDestinationClicked(destination: FromHomeDestination) {
        screensNavigator.toScreen(destination.screenSpec)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: PromptDialogDismissedEvent) {
        val isPositiveButtonClicked = event.clickedButton == PromptDialogButton.POSITIVE
        when(event.id) {
            DIALOG_ID_UPDATE_APK -> {
                if (isPositiveButtonClicked) {
                    dialogsNavigator.showProgressDialog(null, DIALOG_ID_UPDATE_APK_PROGRESS)
                    apkUpdateJob = coroutineScope.launch {
                        val result = updateApkUseCase.updateApk(event.payload as ApkInfo)
                        dialogsNavigator.dismissCurrentlyShownDialog()
                    }
                }
            }
        }
    }

    companion object {

        private const val DIALOG_ID_UPDATE_APK = "DIALOG_ID_UPDATE_APK"
        private const val DIALOG_ID_UPDATE_APK_PROGRESS = "DIALOG_ID_UPDATE_APK_PROGRESS"

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}
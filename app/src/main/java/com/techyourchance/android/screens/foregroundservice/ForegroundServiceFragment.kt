package com.techyourchance.android.screens.foregroundservice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techyourchance.android.R
import com.techyourchance.android.backgroundwork.foregroundservice.ForegroundService
import com.techyourchance.android.backgroundwork.foregroundservice.ForegroundServiceState
import com.techyourchance.android.backgroundwork.foregroundservice.ForegroundServiceStateManager
import com.techyourchance.android.common.device.DeviceOsInfoProvider
import com.techyourchance.android.common.eventbus.EventBusSubscriber
import com.techyourchance.android.common.permissions.MyPermission
import com.techyourchance.android.common.permissions.PermissionsHelper
import com.techyourchance.android.common.toasts.ToastsHelper
import com.techyourchance.android.screens.common.ScreenSpec
import com.techyourchance.android.screens.common.ScreensNavigator
import com.techyourchance.android.screens.common.dialogs.DialogsNavigator
import com.techyourchance.android.screens.common.dialogs.info.InfoDialogDismissedEvent
import com.techyourchance.android.screens.common.fragments.BaseFragment
import com.techyourchance.android.screens.common.mvcviews.ViewMvcFactory
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class ForegroundServiceFragment : BaseFragment(),
    ForegroundServiceViewMvc.Listener,
    PermissionsHelper.Listener,
    ForegroundServiceStateManager.Listener
{

    @Inject lateinit var viewMvcFactory: ViewMvcFactory
    @Inject lateinit var dialogsNavigator: DialogsNavigator
    @Inject lateinit var screensNavigator: ScreensNavigator
    @Inject lateinit var toastsHelper: ToastsHelper
    @Inject lateinit var permissionsHelper: PermissionsHelper
    @Inject lateinit var eventBusSubscriber: EventBusSubscriber
    @Inject lateinit var deviceOsInfoProvider: DeviceOsInfoProvider
    @Inject lateinit var foregroundServiceStateManager: ForegroundServiceStateManager

    private lateinit var viewMvc: ForegroundServiceViewMvc

    private lateinit var currentServiceState: ForegroundServiceState

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewMvc = viewMvcFactory.newForegroundServiceViewMvc(container)
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)
        eventBusSubscriber.register(this)
        permissionsHelper.registerListener(this)
        foregroundServiceStateManager.registerListener(this)

        handleForegroundServiceState(foregroundServiceStateManager.getState())
    }

    override fun onStop() {
        super.onStop()
        viewMvc.unregisterListener(this)
        eventBusSubscriber.unregister(this)
        permissionsHelper.unregisterListener(this)
        foregroundServiceStateManager.unregisterListener(this)
    }

    private fun handleForegroundServiceState(state: ForegroundServiceState) {
        currentServiceState = state
        viewMvc.bindServiceState(state)
    }

    override fun onForegroundServiceStateChanged(state: ForegroundServiceState) {
        handleForegroundServiceState(state)
    }

    override fun onToggleServiceClicked() {
        if (currentServiceState !is ForegroundServiceState.Started) {
            if (deviceOsInfoProvider.isAtLeastApi33()) {
                if (permissionsHelper.hasPermission(MyPermission.POST_NOTIFICATIONS)) {
                    startForegroundService()
                } else {
                    permissionsHelper.requestPermission(MyPermission.POST_NOTIFICATIONS, REQUEST_CODE_POST_NOTIFICATIONS)
                }
            } else {
                startForegroundService()
            }
        } else {
            stopForegroundService()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, result: PermissionsHelper.PermissionsResult) {
        if (deviceOsInfoProvider.isAtLeastApi33() && requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (result.granted.contains(MyPermission.POST_NOTIFICATIONS)) {
                startForegroundService()
            } else {
                dialogsNavigator.showForegroundServiceWithoutNotificationInfoDialog(DIALOG_ID_FG_SERVICE_WITHOUT_NOTIFICATION)
            }
        }
    }

    override fun onPermissionsRequestCancelled(requestCode: Int) {
        toastsHelper.showToast(getString(R.string.operation_cancelled))
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: InfoDialogDismissedEvent) {
        when(event.id) {
            DIALOG_ID_FG_SERVICE_WITHOUT_NOTIFICATION -> {
                startForegroundService()
            }
        }
    }

    private fun startForegroundService() {
        ForegroundService.startService(requireContext(), ScreenSpec.ForegroundService)
    }

    private fun stopForegroundService() {
        ForegroundService.stopService(requireContext())
    }

    override fun onBackClicked() {
        screensNavigator.navigateBack()
    }

    companion object {

        private const val DIALOG_ID_FG_SERVICE_WITHOUT_NOTIFICATION = "DIALOG_ID_BIOMETRIC_AUTH_NOT_SUPPORTED"

        private const val REQUEST_CODE_POST_NOTIFICATIONS = 1001

        fun newInstance(): ForegroundServiceFragment {
            return ForegroundServiceFragment()
        }
    }
}
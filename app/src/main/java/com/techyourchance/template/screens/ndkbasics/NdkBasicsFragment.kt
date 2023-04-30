package com.techyourchance.template.screens.ndkbasics

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import com.techyourchance.template.R
import com.techyourchance.template.biometric.BiometricAuthUseCase
import com.techyourchance.template.common.toasts.ToastsHelper
import com.techyourchance.template.ndk.NdkManager
import com.techyourchance.template.screens.common.ScreensNavigator
import com.techyourchance.template.screens.common.dialogs.DialogsNavigator
import com.techyourchance.template.screens.common.dialogs.info.InfoDialogDismissedEvent
import com.techyourchance.template.screens.common.fragments.BaseFragment
import com.techyourchance.template.screens.common.mvcviews.ViewMvcFactory
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class NdkBasicsFragment : BaseFragment(), NdkBasicsViewMvc.Listener {

    @Inject lateinit var viewMvcFactory: ViewMvcFactory
    @Inject lateinit var dialogsNavigator: DialogsNavigator
    @Inject lateinit var screensNavigator: ScreensNavigator
    @Inject lateinit var toastsHelper: ToastsHelper
    @Inject lateinit var ndkManager: NdkManager

    private lateinit var viewMvc: NdkBasicsViewMvc

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewMvc = viewMvcFactory.newNdkBasicsViewMvc(container)
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)
    }

    override fun onStop() {
        super.onStop()
        viewMvc.unregisterListener(this)
    }

    override fun onComputeFibonacciClicked() {
        coroutineScope.launch {
            val arg = viewMvc.getArgument()
            if (arg >= 0) {
                val result = ndkManager.computeFibonacci(arg)
                toastsHelper.showToast("Fibonacci result: ${result.result}")
            } else {
                dialogsNavigator.showInvalidFibonacciArgumentDialogDialog(arg, null)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: InfoDialogDismissedEvent) {
        when(event.id) {
            DIALOG_ID_BIOMETRIC_AUTH_NOT_SUPPORTED -> {
                screensNavigator.navigateBack()
            }
        }
    }

    override fun onBackClicked() {
        screensNavigator.navigateBack()
    }

    companion object {

        private const val DIALOG_ID_BIOMETRIC_AUTH_NOT_SUPPORTED = "DIALOG_ID_BIOMETRIC_AUTH_NOT_SUPPORTED"

        fun newInstance(): NdkBasicsFragment {
            return NdkBasicsFragment()
        }
    }
}
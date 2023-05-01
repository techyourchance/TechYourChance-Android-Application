package com.techyourchance.android.screens.biometricauth

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
import com.techyourchance.android.R
import com.techyourchance.android.biometric.BiometricAuthUseCase
import com.techyourchance.android.common.toasts.ToastsHelper
import com.techyourchance.android.screens.common.ScreensNavigator
import com.techyourchance.android.screens.common.dialogs.DialogsNavigator
import com.techyourchance.android.screens.common.dialogs.info.InfoDialogDismissedEvent
import com.techyourchance.android.screens.common.fragments.BaseFragment
import com.techyourchance.android.screens.common.mvcviews.ViewMvcFactory
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class BiometricAuthFragment : BaseFragment(), BiometricAuthViewMvc.Listener, BiometricAuthUseCase.Listener {

    @Inject lateinit var viewMvcFactory: ViewMvcFactory
    @Inject lateinit var dialogsNavigator: DialogsNavigator
    @Inject lateinit var screensNavigator: ScreensNavigator
    @Inject lateinit var toastsHelper: ToastsHelper
    @Inject lateinit var biometricManager: BiometricManager
    @Inject lateinit var biometricAuthUseCase: BiometricAuthUseCase

    private val activityResultHandler = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_CANCELED) {
            dialogsNavigator.showBiometricEnrollmentCancelledDialog(null)
        } else {
            authenticate()
        }
    }

    private lateinit var viewMvc: BiometricAuthViewMvc

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewMvc = viewMvcFactory.newBiometricLockViewMvc(container)
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)
        biometricAuthUseCase.registerListener(this)
    }

    override fun onStop() {
        super.onStop()
        viewMvc.unregisterListener(this)
        biometricAuthUseCase.unregisterListener(this)
    }

    override fun onAuthenticateClicked() {
        authenticate()
    }

    private fun authenticate() {
        biometricAuthUseCase.authenticate(
            getString(R.string.biometric_auth_title),
            getString(R.string.biometric_auth_description),
            getString(R.string.cancel),
        )
    }

    override fun onBiometricAuthResult(result: BiometricAuthUseCase.AuthResult) {
        when(result) {
            is BiometricAuthUseCase.AuthResult.NotEnrolled -> {
                launchBiometricEnrollment()
            }
            is BiometricAuthUseCase.AuthResult.NotSupported -> {
                dialogsNavigator.showBiometricAuthNotSupportedInfoDialog(DIALOG_ID_BIOMETRIC_AUTH_NOT_SUPPORTED)
            }
            is BiometricAuthUseCase.AuthResult.Success -> {
                dialogsNavigator.showBiometricAuthSuccessDialog(null)
            }
            is BiometricAuthUseCase.AuthResult.Cancelled -> {
                dialogsNavigator.showBiometricAuthCancelDialog(null)
            }
            is BiometricAuthUseCase.AuthResult.Failed -> {
                dialogsNavigator.showBiometricAuthErrorDialog(null, result.errorCode, result.errorMessage)
            }
        }
    }

    private fun launchBiometricEnrollment() {
        val intent: Intent = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                Intent(Settings.ACTION_BIOMETRIC_ENROLL).putExtra(
                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BiometricManager.Authenticators.BIOMETRIC_STRONG
                )
            }
            else -> Intent(Settings.ACTION_SECURITY_SETTINGS)
        }
        activityResultHandler.launch(intent)
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

        fun newInstance(): BiometricAuthFragment {
            return BiometricAuthFragment()
        }
    }
}
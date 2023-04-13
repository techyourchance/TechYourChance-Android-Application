package com.techyourchance.template.screens.biometricauth

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
import androidx.biometric.BiometricPrompt
import com.techyourchance.template.R
import com.techyourchance.template.common.logs.MyLogger
import com.techyourchance.template.common.toasts.ToastsHelper
import com.techyourchance.template.screens.common.ScreensNavigator
import com.techyourchance.template.screens.common.dialogs.DialogsNavigator
import com.techyourchance.template.screens.common.dialogs.info.InfoDialogDismissedEvent
import com.techyourchance.template.screens.common.fragments.BaseFragment
import com.techyourchance.template.screens.common.mvcviews.ViewMvcFactory
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class BiometricAuthFragment : BaseFragment(), BiometricAuthViewMvc.Listener {

    @Inject lateinit var viewMvcFactory: ViewMvcFactory
    @Inject lateinit var dialogsNavigator: DialogsNavigator
    @Inject lateinit var screensNavigator: ScreensNavigator
    @Inject lateinit var toastsHelper: ToastsHelper
    @Inject lateinit var biometricManager: BiometricManager

    private val activityResultHandler = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_CANCELED) {
            dialogsNavigator.showBiometricEnrollmentCancelledDialog(null)
        } else {
            handleBiometricAuthState()
        }
    }

    private lateinit var viewMvc: BiometricAuthViewMvc

    private var biometricPrompt: BiometricPrompt? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)

        initBiometricPrompt()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewMvc = viewMvcFactory.newBiometricLockViewMvc(container)
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

    private fun initBiometricPrompt() {
        val biometricPrompt = BiometricPrompt(
            requireActivity(),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    MyLogger.i("biometric auth error ($errorCode): $errString")
                    val cancelled = errorCode in arrayListOf<Int>(
                        BiometricPrompt.ERROR_CANCELED,
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON
                    )
                    if (cancelled) {
                        dialogsNavigator.showBiometricAuthCancelDialog(null)
                    } else {
                        dialogsNavigator.showBiometricAuthErrorDialog(null, errorCode, errString.toString())
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    MyLogger.i("biometric auth succeeded")
                    dialogsNavigator.showBiometricAuthSuccessDialog(null)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    MyLogger.i("biometric auth failed")
                }
            }
        )
    }

    override fun onAuthenticateClicked() {
        handleBiometricAuthState()
    }

    private fun handleBiometricAuthState() {
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> authenticate()
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> launchBiometricEnrollment()
            else -> dialogsNavigator.showBiometricAuthNotSupportedInfoDialog(DIALOG_ID_BIOMETRIC_AUTH_NOT_POSSIBLE)
        }
    }

    private fun authenticate() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_auth_title))
            .setSubtitle("")
            .setDescription(getString(R.string.biometric_auth_description))
            .setNegativeButtonText(getString(R.string.cancel))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        biometricPrompt?.authenticate(promptInfo)
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
            DIALOG_ID_BIOMETRIC_AUTH_NOT_POSSIBLE -> {
                screensNavigator.navigateBack()
            }
        }
    }

    override fun onBackClicked() {
        screensNavigator.navigateBack()
    }

    companion object {

        private const val DIALOG_ID_BIOMETRIC_AUTH_NOT_POSSIBLE = "DIALOG_ID_BIOMETRIC_AUTH_NOT_POSSIBLE"

        fun newInstance(): BiometricAuthFragment {
            return BiometricAuthFragment()
        }
    }
}
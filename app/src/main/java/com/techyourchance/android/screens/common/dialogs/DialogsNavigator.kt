package com.techyourchance.android.screens.common.dialogs

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import androidx.fragment.app.DialogFragment
import com.techyourchance.dialoghelper.DialogHelper
import com.techyourchance.android.R
import com.techyourchance.android.apkupdate.ApkInfo
import com.techyourchance.android.screens.common.dialogs.info.InfoDialog
import com.techyourchance.android.screens.common.dialogs.progress.ProgressDialog
import com.techyourchance.android.screens.common.dialogs.prompt.PromptDialog
import java.io.Serializable

@UiThread
class DialogsNavigator(
    private val context: Context,
    private val dialogHelper: DialogHelper,
) {

    private val uiHandler = Handler(Looper.getMainLooper())

    /**
     * @return a reference to currently shown dialog, or null if no dialog is shown.
     */
    fun getCurrentlyShownDialog(): DialogFragment? {
        return dialogHelper.currentlyShownDialog
    }

    /**
     * Obtain the id of the currently shown dialog.
     * @return the id of the currently shown dialog; null if no dialog is shown, or the currently
     * shown dialog has no id
     */
    fun getCurrentlyShownDialogId(): String? {
     return dialogHelper.currentlyShownDialogId
    }

    /**
     * Check whether a dialog with a specified id is currently shown
     * @param id dialog id to query
     * @return true if a dialog with the given id is currently shown; false otherwise
     */
    fun isDialogCurrentlyShown(id: String): Boolean {
        return id == dialogHelper.currentlyShownDialogId
    }

    /**
     * Dismiss the currently shown dialog. Has no effect if no dialog is shown.
     */
    fun dismissCurrentlyShownDialog() {
        dialogHelper.dismissCurrentlyShownDialog()
    }

    fun getDialogId(dialog: DialogFragment): String? {
        return dialogHelper.getDialogId(dialog)
    }

    private fun getString(@StringRes id: Int, vararg formatArgs: Any): String {
        return context.getString(id, *formatArgs)
    }

    private fun showPromptDialog(
        message: String,
        positiveButtonCaption: String,
        negativeButtonCaption: String,
        id: String?,
        payload: Serializable?,
    ) {
        uiHandler.post {
            dialogHelper.showDialog(
                PromptDialog.newInstance(
                    message,
                    positiveButtonCaption,
                    negativeButtonCaption,
                    payload
                ),
                id
            )
        }
    }

    private fun showInfoDialog(
        message: String,
        buttonCaption: String,
        id: String?,
        payload: Serializable?,
    ) {
        uiHandler.post {
            dialogHelper.showDialog(
                InfoDialog.newInstance(
                    message,
                    buttonCaption,
                    payload
                ),
                id
            )
        }
    }

    fun showServerErrorDialog(id: String?) {
        showInfoDialog(
            getString(R.string.server_error_dialog_message),
            getString(R.string.server_error_dialog_button_caption),
            id,
            null
        )
    }

    fun showBiometricAuthNotSupportedInfoDialog(id: String?) {
        showInfoDialog(
            getString(R.string.biometric_auth_not_supported),
            getString(R.string.server_error_dialog_button_caption),
            id,
            null
        )
    }

    fun showBiometricAuthSuccessDialog(id: String?) {
        showInfoDialog(
            getString(R.string.biometric_auth_success),
            getString(R.string.server_error_dialog_button_caption),
            id,
            null
        )
    }

    fun showBiometricAuthCancelDialog(id: String?) {
        showInfoDialog(
            getString(R.string.biometric_auth_cancel),
            getString(R.string.server_error_dialog_button_caption),
            id,
            null
        )
    }

    fun showBiometricAuthErrorDialog(id: String?, errorCode: Int, errorMessage: String) {
        showInfoDialog(
            getString(R.string.biometric_auth_error, errorMessage, errorCode),
            getString(R.string.server_error_dialog_button_caption),
            id,
            null
        )
    }

    fun showBiometricEnrollmentCancelledDialog(id: String?) {
        showInfoDialog(
            getString(R.string.biometric_auth_enrollment_cancel),
            getString(R.string.server_error_dialog_button_caption),
            id,
            null
        )
    }

    fun showInvalidFibonacciArgumentDialog(argument: Int, id: String?) {
        showInfoDialog(
            getString(R.string.ndk_basics_invalid_argument_error, argument),
            getString(R.string.server_error_dialog_button_caption),
            id,
            null
        )
    }

    fun showForegroundServiceWithoutNotificationInfoDialog(id: String?) {
        showInfoDialog(
            getString(R.string.foreground_service_without_notification_dialog_message),
            getString(R.string.server_error_dialog_button_caption),
            id,
            null
        )
    }

    fun showUpdateApkPromptDialog(id: String?, latestApkInfo: ApkInfo) {
        showPromptDialog(
            getString(R.string.update_apk_dialog_message),
            getString(R.string.ok),
            getString(R.string.cancel),
            id,
            latestApkInfo
        )
    }

    fun showProgressDialog(message: CharSequence?, id: String?) {
        dialogHelper.showDialog(
            ProgressDialog.newInstance(message),
            id
        )
    }

    fun showCancellableProgressDialog(message: CharSequence?, id: String?) {
        dialogHelper.showDialog(
            ProgressDialog.newInstance(message, true),
            id
        )
    }
}
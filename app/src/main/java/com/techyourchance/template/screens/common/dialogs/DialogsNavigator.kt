package com.techyourchance.template.screens.common.dialogs

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import androidx.fragment.app.DialogFragment
import com.techyourchance.dialoghelper.DialogHelper
import com.techyourchance.template.R
import com.techyourchance.template.screens.common.dialogs.info.InfoDialog
import com.techyourchance.template.screens.common.dialogs.prompt.PromptDialog

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

    private fun getString(@StringRes stringId: Int): String {
        return context.getString(stringId)
    }

    private fun showPromptDialog(
        message: String,
        positiveButtonCaption: String,
        negativeButtonCaption: String,
        id: String?
    ) {
        dialogHelper.showDialog(
            PromptDialog.newInstance(
                message,
                positiveButtonCaption,
                negativeButtonCaption,
            ),
            id
        )
    }

    private fun showInfoDialog(
        message: String,
        buttonCaption: String,
        id: String?
    ) {
        uiHandler.post {
            dialogHelper.showDialog(
                InfoDialog.newInstance(
                    message,
                    buttonCaption,
                ),
                id
            )
        }
    }

    fun showServerErrorDialog(id: String?) {
        showInfoDialog(
            getString(R.string.server_error_dialog_message),
            getString(R.string.server_error_dialog_button_caption),
            id
        )
    }
}
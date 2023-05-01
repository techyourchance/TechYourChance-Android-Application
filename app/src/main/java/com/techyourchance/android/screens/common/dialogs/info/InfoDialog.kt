package com.techyourchance.android.screens.common.dialogs.info

import android.os.Bundle
import com.techyourchance.android.common.eventbus.EventBusPoster
import com.techyourchance.android.screens.common.dialogs.BaseOneOrTwoButtonDialog
import javax.inject.Inject

class InfoDialog : BaseOneOrTwoButtonDialog() {

    @Inject lateinit var eventBusPoster: EventBusPoster

    override fun onCreate(savedInstanceState: Bundle?) {
        buttonsConfig = ButtonsConfig.SINGLE_BUTTON
        super.onCreate(savedInstanceState)
        controllerComponent.inject(this)
    }

    override fun getDialogImageUri(): String? {
        return null
    }

    override fun getMessage(): CharSequence {
        return arguments!!.getString(ARG_MESSAGE)!!
    }

    override fun getPositiveButtonCaption(): CharSequence {
        return arguments!!.getString(ARG_BUTTON_CAPTION)!!
    }

    override fun getNegativeButtonCaption(): CharSequence {
        return ""
    }

    override fun onPositiveButtonClicked() {
        eventBusPoster.post(InfoDialogDismissedEvent(dialogId))
        dismiss()
    }

    override fun onNegativeButtonClicked() {
        dismiss()
    }

    companion object {

        private const val ARG_MESSAGE = "ARG_MESSAGE"
        private const val ARG_BUTTON_CAPTION = "ARG_BUTTON_CAPTION"

        fun newInstance(message: String, buttonCaption: String): InfoDialog {
            val args = Bundle(4)
            args.putString(ARG_MESSAGE, message)
            args.putString(ARG_BUTTON_CAPTION, buttonCaption)

            val infoDialog = InfoDialog()
            infoDialog.arguments = args

            return infoDialog
        }
    }
}
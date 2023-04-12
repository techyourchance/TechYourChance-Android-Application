package com.techyourchance.template.screens.common.dialogs.prompt

import android.os.Bundle
import com.techyourchance.template.common.eventbus.EventBusPoster
import com.techyourchance.template.screens.common.dialogs.BaseOneOrTwoButtonDialog
import javax.inject.Inject

class PromptDialog : BaseOneOrTwoButtonDialog() {

    @Inject lateinit var eventBusPoster: EventBusPoster

    override fun onCreate(savedInstanceState: Bundle?) {
        buttonsConfig = ButtonsConfig.TWO_BUTTONS
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
        return arguments!!.getString(ARG_POSITIVE_BUTTON_CAPTION)!!
    }

    override fun getNegativeButtonCaption(): CharSequence {
        return arguments!!.getString(ARG_NEGATIVE_BUTTON_CAPTION)!!
    }

    override fun onPositiveButtonClicked() {
        eventBusPoster.post(PromptDialogDismissedEvent(dialogId, PromptDialogButton.POSITIVE))
        dismiss()
    }

    override fun onNegativeButtonClicked() {
        eventBusPoster.post(PromptDialogDismissedEvent(dialogId, PromptDialogButton.NEGATIVE))
        dismiss()
    }

    companion object {

        private const val ARG_MESSAGE = "ARG_MESSAGE"
        private const val ARG_POSITIVE_BUTTON_CAPTION = "ARG_POSITIVE_BUTTON_CAPTION"
        private const val ARG_NEGATIVE_BUTTON_CAPTION = "ARG_NEGATIVE_BUTTON_CAPTION"


        fun newInstance(message: String, positiveButton: String, negativeButton: String): PromptDialog {
            val args = Bundle(4)
            args.putString(ARG_MESSAGE, message)
            args.putString(ARG_POSITIVE_BUTTON_CAPTION, positiveButton)
            args.putString(ARG_NEGATIVE_BUTTON_CAPTION, negativeButton)

            val promptDialog = PromptDialog()
            promptDialog.arguments = args

            return promptDialog
        }
    }
}
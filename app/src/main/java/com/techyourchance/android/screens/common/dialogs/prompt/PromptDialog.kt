package com.techyourchance.android.screens.common.dialogs.prompt

import android.os.Bundle
import com.techyourchance.android.apkupdate.ApkInfo
import com.techyourchance.android.common.eventbus.EventBusPoster
import com.techyourchance.android.common.logs.MyLogger
import com.techyourchance.android.screens.common.dialogs.BaseOneOrTwoButtonDialog
import com.techyourchance.android.screens.home.HomeFragment
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

class PromptDialog : BaseOneOrTwoButtonDialog() {

    @Inject lateinit var eventBusPoster: EventBusPoster

    override fun onCreate(savedInstanceState: Bundle?) {
        buttonsConfig = ButtonsConfig.TWO_BUTTONS
        super.onCreate(savedInstanceState)
        controllerComponent.inject(this)
    }


    override fun onStart() {
        MyLogger.i("onStart()")
        super.onStart()
    }

    override fun onStop() {
        MyLogger.i("onStop()")
        super.onStop()
    }

    override fun onResume() {
        MyLogger.i("onResume()")
        super.onResume()
    }

    override fun onPause() {
        MyLogger.i("onPause()")
        super.onPause()
    }

    override fun getDialogImageUri(): String? {
        return null
    }

    override fun getMessage(): CharSequence {
        return requireArguments().getString(ARG_MESSAGE)!!
    }

    override fun getPositiveButtonCaption(): CharSequence {
        return requireArguments().getString(ARG_POSITIVE_BUTTON_CAPTION)!!
    }

    override fun getNegativeButtonCaption(): CharSequence {
        return requireArguments().getString(ARG_NEGATIVE_BUTTON_CAPTION)!!
    }

    override fun getPayload(): Serializable? {
        return requireArguments().getSerializable(ARG_PAYLOAD)
    }

    override fun onPositiveButtonClicked() {
        eventBusPoster.post(PromptDialogDismissedEvent(dialogId, PromptDialogButton.POSITIVE, getPayload()))
        dismiss()
    }

    override fun onNegativeButtonClicked() {
        eventBusPoster.post(PromptDialogDismissedEvent(dialogId, PromptDialogButton.NEGATIVE, getPayload()))
        dismiss()
    }

    companion object {

        private const val ARG_MESSAGE = "ARG_MESSAGE"
        private const val ARG_POSITIVE_BUTTON_CAPTION = "ARG_POSITIVE_BUTTON_CAPTION"
        private const val ARG_NEGATIVE_BUTTON_CAPTION = "ARG_NEGATIVE_BUTTON_CAPTION"
        private const val ARG_PAYLOAD = "ARG_PAYLOAD"


        fun newInstance(
            message: String,
            positiveButton: String,
            negativeButton: String,
            payload: Serializable?
        ): PromptDialog {
            val args = Bundle(4)
            args.putString(ARG_MESSAGE, message)
            args.putString(ARG_POSITIVE_BUTTON_CAPTION, positiveButton)
            args.putString(ARG_NEGATIVE_BUTTON_CAPTION, negativeButton)
            args.putSerializable(ARG_PAYLOAD, payload)

            val promptDialog = PromptDialog()
            promptDialog.arguments = args

            return promptDialog
        }
    }
}
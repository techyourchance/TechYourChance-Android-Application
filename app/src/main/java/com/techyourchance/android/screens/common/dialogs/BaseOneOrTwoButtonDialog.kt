package com.techyourchance.android.screens.common.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.techyourchance.android.R
import com.techyourchance.android.common.imageloader.ImageLoader
import com.techyourchance.android.screens.common.widgets.ViewExtensions.showBorder
import java.io.Serializable
import javax.inject.Inject

abstract class BaseOneOrTwoButtonDialog() : BaseDialog() {

    enum class DialogButton {
        POSITIVE, NEGATIVE
    }

    protected enum class ButtonsConfig {
        SINGLE_BUTTON, TWO_BUTTONS
    }

    @Inject lateinit var imageLoader: ImageLoader

    private lateinit var imgDialog: ImageView
    private lateinit var txtMessage: TextView
    private lateinit var btnPositive: Button
    private lateinit var btnNegative: Button

    protected var buttonsConfig: ButtonsConfig = ButtonsConfig.SINGLE_BUTTON

    /**
     * Return the URI of the image that should be loaded inside the dialog,
     * or null if there is no image
     */
    abstract fun getDialogImageUri(): String?
    abstract fun getMessage(): CharSequence
    abstract fun getPositiveButtonCaption(): CharSequence
    abstract fun getNegativeButtonCaption(): CharSequence
    abstract fun getPayload(): Serializable?
    abstract fun onPositiveButtonClicked()
    abstract fun onNegativeButtonClicked()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialogInternal(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.layout_dialog_one_or_two_buttons)

        dialog.apply {
            imgDialog = findViewById(R.id.imgDialog)
            txtMessage = findViewById(R.id.txtDialogMessage)
            btnPositive = findViewById(R.id.btnPositive)
            btnNegative = findViewById(R.id.btnNegative)
        }

        txtMessage.text = getMessage()
        btnPositive.text = getPositiveButtonCaption()
        btnNegative.text = getNegativeButtonCaption()

        val imageUri = getDialogImageUri()
        if (imageUri != null && imageUri.isNotBlank()) {
            imgDialog.isVisible = true
            //imgDialog.setImageURI(Uri.parse(imageUri))
            imageLoader.loadImage(imageUri, imgDialog, object : ImageLoader.OnCompletionListener {
                override fun onLoadCompleted() {
                }
                override fun onLoadFailed() {
                    // no-op
                }
            })
        } else {
            imgDialog.isVisible = false
        }

        btnPositive.showBorder(ContextCompat.getColor(requireContext(), R.color.button_border))
        btnNegative.showBorder(ContextCompat.getColor(requireContext(), R.color.button_border))

        btnPositive.setOnClickListener {
            onPositiveButtonClicked()
        }

        btnNegative.setOnClickListener {
            onNegativeButtonClicked()
        }

        if (buttonsConfig == ButtonsConfig.SINGLE_BUTTON) {
            btnNegative.isVisible = false
        }

        return dialog
    }
}
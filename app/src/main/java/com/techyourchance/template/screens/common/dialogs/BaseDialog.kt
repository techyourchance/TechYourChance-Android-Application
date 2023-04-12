package com.techyourchance.template.screens.common.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.techyourchance.template.R
import com.techyourchance.template.common.Constants
import com.techyourchance.template.common.dependencyinjection.controller.ControllerComponent
import com.techyourchance.template.common.dependencyinjection.controller.ControllerModule
import com.techyourchance.template.common.dependencyinjection.controller.ViewMvcModule
import com.techyourchance.template.common.device.DeviceScreenMetricsProvider
import com.techyourchance.template.screens.common.activities.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.lang.IllegalStateException
import javax.inject.Inject

/**
 * Base class for all dialogs
 */
abstract class BaseDialog : DialogFragment() {

    @Inject lateinit var deviceScreenMetricsProvider: DeviceScreenMetricsProvider

    protected val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    private var onStartCalled = false
    private var targetWidth: Int = 0
    private var customTargetWidth = false
    private var targetHeight: Int = 0
    private var customTargetHeight = false

    protected val controllerComponent: ControllerComponent by lazy {
        (activity as BaseActivity).activityComponent
            .newControllerComponent(ControllerModule(this, this), ViewMvcModule())
    }

    /**
     * Get this dialog's ID that was supplied with a call to one of [DialogsNavigator]'s
     * methods.
     * @return dialog's ID, or null if none was set
     */
    protected val dialogId: String?
        get() = controllerComponent.dialogNavigator.getDialogId(this)

    override fun onStart() {
        super.onStart()
        onStartCalled = true
        adjustDialogWindowSize()
    }

    /**
     * Override this method instead of onCreateDialog.
     * The reason for this hack is that we want to execute some logic when every dialog is shown.
     */
    abstract fun onCreateDialogInternal(savedInstanceState: Bundle?): Dialog

    final override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = onCreateDialogInternal(savedInstanceState)
        // the reason onCreateDialog was made 'final' is to ensure that these methods are called
        preventSystemUiReappearingWhenDialogShown(dialog)
        removeDefaultFrameFromDialog(dialog)
        return dialog
    }

    /**
     * This method is a workaround for an annoying problem that appears if you use full-screen
     * mode and hide system navigation controls: when DialogFragment is shown, system controls reappear.
     * The solution was taken from here: https://stackoverflow.com/a/24549869/2463035
     */
    private fun preventSystemUiReappearingWhenDialogShown(dialog: Dialog) {
        val dialogWindow = dialog.window!!

        dialogWindow.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )
        dialogWindow.decorView.systemUiVisibility = requireActivity().window.decorView.systemUiVisibility
        dialog.setOnShowListener {
            // clear the not focusable flag from the window
            dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        }
    }

    private fun removeDefaultFrameFromDialog(dialog: Dialog) {
        val window = dialog.window!!
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * Set the width of this dialog (0 to reset). This method MUST be called before the first onStart().
     */
    protected fun setTargetWidthPx(targetWidth: Int) {
        if (onStartCalled) {
            throw IllegalStateException("mustn't be called after onStart()")
        }

        this.targetWidth = targetWidth
        customTargetWidth = targetWidth > 0
    }

    /**
     * Set the width of this dialog (0 to reset). This method MUST be called before the first onStart().
     */
    protected fun setTargetHeightPx(targetHeight: Int) {
        if (onStartCalled) {
            throw IllegalStateException("mustn't be called after onStart()")
        }
        this.targetHeight = targetHeight
        customTargetHeight = targetHeight > 0
    }

    private fun adjustDialogWindowSize() {
        val params = dialog!!.window!!.attributes;
        params.width = if (customTargetWidth) {
            targetWidth
        } else {
            resources.getDimensionPixelSize(getDefaultWidth())
        };
        params.height = if (customTargetHeight) {
            targetHeight
        } else {
            resources.getDimensionPixelSize(getDefaultHeight())
        }
        dialog!!.window!!.attributes = params as android.view.WindowManager.LayoutParams;
    }

    private fun getDefaultWidth(): Int {
        val screenWidth = deviceScreenMetricsProvider.getScreenMetrics().width
        return (screenWidth * Constants.DEFAULT_DIALOG_WIDTH_RATIO).toInt()
    }

    private fun getDefaultHeight(): Int {
        val screenHeight = deviceScreenMetricsProvider.getScreenMetrics().height
        return (screenHeight * Constants.DEFAULT_DIALOG_HEIGHT_RATIO).toInt()
    }

}
package com.techyourchance.android.screens.composeoverlay

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techyourchance.android.common.eventbus.EventBusSubscriber
import com.techyourchance.android.common.toasts.ToastsHelper
import com.techyourchance.android.ndk.NdkManager
import com.techyourchance.android.overlay.ComposeOverlayService
import com.techyourchance.android.overlay.OverlayManager
import com.techyourchance.android.screens.common.ScreensNavigator
import com.techyourchance.android.screens.common.dialogs.DialogsNavigator
import com.techyourchance.android.screens.common.dialogs.progress.ProgressDialogDismissedEvent
import com.techyourchance.android.screens.common.fragments.BaseFragment
import com.techyourchance.android.screens.common.mvcviews.ViewMvcFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class ComposeOverlayFragment : BaseFragment(),
    ComposeOverlayViewMvc.Listener,
    OverlayManager.Listener {

    @Inject lateinit var viewMvcFactory: ViewMvcFactory
    @Inject lateinit var dialogsNavigator: DialogsNavigator
    @Inject lateinit var screensNavigator: ScreensNavigator
    @Inject lateinit var toastsHelper: ToastsHelper
    @Inject lateinit var overlayManager: OverlayManager

    private lateinit var viewMvc: ComposeOverlayViewMvc

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewMvc = viewMvcFactory.newComposeOverlayViewMvc()
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)
        overlayManager.registerListener(this)

        viewMvc.bindIsOverlayShown(overlayManager.isComposeOverlayShown())
    }

    override fun onStop() {
        super.onStop()
        viewMvc.unregisterListener(this)
        overlayManager.unregisterListener(this)
    }

    override fun onToggleOverlayClicked() {
        if (overlayManager.isComposeOverlayShown()) {
            overlayManager.hideOverlay()
        } else {
            if (Settings.canDrawOverlays(requireContext())) {
                overlayManager.showOverlay()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${requireContext().packageName}"))
                startActivityForResult(intent, 100)
            }
        }
    }

    override fun onBackClicked() {
        screensNavigator.navigateBack()
    }

    override fun onComposeOverlayStateChanged(isShown: Boolean) {
        viewMvc.bindIsOverlayShown(isShown)
    }

    companion object {

        fun newInstance(): ComposeOverlayFragment {
            return ComposeOverlayFragment()
        }
    }
}
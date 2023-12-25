package com.techyourchance.android.overlay

import android.content.Context
import com.techyourchance.android.common.Observable

class OverlayManager(
    private val context: Context,
): Observable<OverlayManager.Listener>() {

    interface Listener {
        fun onComposeOverlayStateChanged(isShown: Boolean)
    }

    private var isComposeOverlayShown = false

    internal fun setComposeOverlayShown(isShown: Boolean) {
        if (isComposeOverlayShown != isShown) {
            isComposeOverlayShown = isShown
            listeners.forEach { it.onComposeOverlayStateChanged(isShown)}
        }
    }

    fun isComposeOverlayShown(): Boolean {
        return isComposeOverlayShown
    }

    fun showOverlay() {
        ComposeOverlayService.showOverlay(context)
    }

    fun hideOverlay() {
        ComposeOverlayService.hideOverlay(context)
    }
}
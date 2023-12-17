package com.techyourchance.android.backgroundwork.foregroundservice

import androidx.annotation.UiThread
import com.techyourchance.android.common.Observable

@UiThread
class ForegroundServiceStateManager: Observable<ForegroundServiceStateManager.Listener>() {

    interface Listener {
        fun onForegroundServiceStateChanged(state: ForegroundServiceState)
    }

    private var state: ForegroundServiceState = ForegroundServiceState.Idle

    fun getState(): ForegroundServiceState {
        return state
    }

    internal fun setState(newState: ForegroundServiceState) {
        if (newState != state) {
            state = newState
            listeners.map { it.onForegroundServiceStateChanged(newState) }
        }
    }

}
package com.techyourchance.android.backgroundwork.foregroundservice

sealed class ForegroundServiceState {
    object Idle: ForegroundServiceState()
    data class Started(val secondsStarted: Int): ForegroundServiceState()
    object Stopped: ForegroundServiceState()
}
package com.techyourchance.android.backgroundwork.workmanager

sealed class MyWorkerState {
    object Idle: MyWorkerState()
    data class Working(val currentAttempt: Int, val myWorkerConfig: MyWorkerConfig): MyWorkerState()
    data class Waiting(val currentAttempt: Int, val myWorkerConfig: MyWorkerConfig): MyWorkerState()
    object Succeeded: MyWorkerState()
    object Stopped: MyWorkerState()
}
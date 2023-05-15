package com.techyourchance.android.backgroundwork.workmanager

import androidx.work.*
import com.techyourchance.android.common.Observable
import com.techyourchance.android.common.logs.MyLogger
import com.techyourchance.android.common.settings.SettingsManager
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class MyWorkerManager(
    private val workManager: WorkManager,
    private val settingsManager: SettingsManager,
): Observable<MyWorkerManager.Listener>() {

    interface Listener {
        fun onMyWorkerStateChanged(state: MyWorkerState)
    }

    private val lock = ReentrantLock()

    private var state: MyWorkerState = MyWorkerState.Idle

    init {
        MyLogger.i("init; current thread: ${Thread.currentThread().name}")
        observeMyWorkerState()
    }

    fun getState(): MyWorkerState {
        lock.withLock {
            return state
        }
    }

    fun startWorker(myWorkerConfig: MyWorkerConfig) {
        stopWorker()

        settingsManager.myWorkerConfig().value = myWorkerConfig

        val data = Data.Builder()
            .putInt(MyWorker.DATA_KEY_MAX_RETRIES, myWorkerConfig.maxRetries)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<MyWorker>()
            .setInputData(data)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                myWorkerConfig.backoffSeconds.toLong(),
                TimeUnit.SECONDS)
            .also {
                if (myWorkerConfig.isExpedited) {
                    it.setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                }
            }
            .build()

        workManager.enqueueUniqueWork(UNIQUE_NAME, ExistingWorkPolicy.REPLACE, workRequest)
    }

    fun stopWorker() {
        workManager.cancelUniqueWork(UNIQUE_NAME)
    }

    private fun observeMyWorkerState() {
        val workInfosLiveData = workManager.getWorkInfosForUniqueWorkLiveData(UNIQUE_NAME)

        workInfosLiveData.observeForever { workInfos ->
            if (workInfos.isEmpty()) {
                setState(MyWorkerState.Idle)
            } else if (workInfos.size > 1) {
                MyLogger.e("there shouldn't be more than one worker at any instant")
            } else {
                val workInfo = workInfos[0]
                val myWorkerState = when (workInfo.state) {
                    WorkInfo.State.ENQUEUED, WorkInfo.State.BLOCKED, WorkInfo.State.RUNNING -> {
                        MyWorkerState.Working(
                            workInfo.runAttemptCount,
                            settingsManager.myWorkerConfig().value,
                        )
                    }
                    WorkInfo.State.CANCELLED, WorkInfo.State.FAILED -> MyWorkerState.Stopped
                    WorkInfo.State.SUCCEEDED -> MyWorkerState.Succeeded
                }
                setState(myWorkerState)
            }
        }
    }

    internal fun setState(newState: MyWorkerState) {
        lock.withLock {
            if (newState != state) {
                state = newState
            } else {
                return
            }
        }
        listeners.map { it.onMyWorkerStateChanged(newState) }
    }

    companion object {
        const val UNIQUE_NAME = "MY_WORKER"
    }

}
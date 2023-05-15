package com.techyourchance.android.backgroundwork.workmanager

import android.app.NotificationManager
import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Provider

class MyWorkerFactory @Inject constructor(
    private val myWorkerManagerProvider: Provider<MyWorkerManager>,
    private val notificationManagerProvider: Provider<NotificationManager>,
): WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        if(workerClassName == MyWorker::class.java.name) {
            return MyWorker(appContext, workerParameters, myWorkerManagerProvider.get(), notificationManagerProvider.get())
        } else {
            throw RuntimeException("unsupported worker class: $workerClassName")
        }
    }
}
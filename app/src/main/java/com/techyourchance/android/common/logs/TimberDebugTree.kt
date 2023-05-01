package com.techyourchance.android.common.logs

import timber.log.Timber
import javax.inject.Inject

class TimberDebugTree @Inject constructor(): Timber.DebugTree() {

    override fun createStackElementTag(element: StackTraceElement): String? {
        val stackElement = Throwable().stackTrace
            .first {
                !it.className.contains("Timber") && !it.className.contains(MyLogger.javaClass.simpleName)
            }
        return super.createStackElementTag(stackElement)
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, tag, "[${Thread.currentThread().name}] $message", t)
    }
}
package com.techyourchance.android.common.coroutines

import android.os.Looper
import java.lang.IllegalStateException
import javax.inject.Inject

open class UiThreadDetector @Inject constructor() {

    open fun isOnUiThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }

    open fun assertNotOnUiThread() {
        if (isOnUiThread()) {
            throw IllegalStateException("mustn't be called on UI thread")
        }
    }

    fun assertOnUiThread() {
        if (!isOnUiThread()) {
            throw IllegalStateException("must be called on UI thread exclusively")
        }
    }
}
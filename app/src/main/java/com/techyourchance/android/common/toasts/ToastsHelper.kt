package com.techyourchance.android.common.toasts

import android.app.Application
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ToastsHelper(
    private val application: Application,
) {

    fun showToast(message: String) {
        executeOnUiThread {
            Toast.makeText(application, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun showToastLong(message: String) {
        executeOnUiThread {
            Toast.makeText(application, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun executeOnUiThread(function: () -> Unit) {
        @Suppress("OPT_IN_USAGE")
        GlobalScope.launch(Dispatchers.Main.immediate) {
            function.invoke()
        }
    }

}
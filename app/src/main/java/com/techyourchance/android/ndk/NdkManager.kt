package com.techyourchance.android.ndk

import com.techyourchance.android.common.coroutines.BackgroundDispatcher.Background
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NdkManager {

    suspend fun computeFibonacci(argument: Int): FibonacciResult {
        return withContext(Dispatchers.Background) {
            computeFibonacciNative(argument)
        }
    }

    private external fun computeFibonacciNative(argument: Int): FibonacciResult

    private companion object {
        init {
            System.loadLibrary("my-native-code")
        }
    }
}
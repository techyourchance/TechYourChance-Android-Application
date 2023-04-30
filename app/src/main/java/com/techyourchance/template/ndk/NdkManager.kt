package com.techyourchance.template.ndk

import androidx.annotation.WorkerThread
import com.techyourchance.template.common.coroutines.BackgroundDispatcher.Background
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.nio.charset.Charset
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import kotlin.concurrent.withLock

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
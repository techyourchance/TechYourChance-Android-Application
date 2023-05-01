package com.techyourchance.android.common.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

/**
 * Background CoroutineDispatcher for Android applications which replaces both
 * [Dispatchers.Default] and [Dispatchers.IO].
 * More info: https://www.techyourchance.com/coroutines-dispatchers-default-and-dispatchers-io-considered-harmful/
 */
object BackgroundDispatcher: CoroutineDispatcher() {

    private val threadFactory = object: ThreadFactory {
        private val threadCount = AtomicInteger(0)
        private val nextThreadName get() = "BgDispatcher-${threadCount.incrementAndGet()}"

        override fun newThread(runnable: java.lang.Runnable): Thread {
            return Thread(runnable, nextThreadName)
        }
    }

    private val threadPool = ThreadPoolExecutor(
            3,
            Integer.MAX_VALUE,
            60L,
            TimeUnit.SECONDS,
            SynchronousQueue<Runnable>(),
            threadFactory
    );

    private val dispatcher = threadPool.asCoroutineDispatcher()

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatcher.dispatch(context, block)
    }

    /**
     * Background CoroutineDispatcher for Android applications which replaces both
     * [Dispatchers.Default] and [Dispatchers.IO].
     */
    val Dispatchers.Background get() = BackgroundDispatcher

}
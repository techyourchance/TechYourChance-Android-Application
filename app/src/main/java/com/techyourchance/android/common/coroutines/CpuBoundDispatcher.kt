package com.techyourchance.android.common.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext
import kotlin.math.max

/**
 * Dispatcher that should be used for CPU bound tasks
 */
object CpuBoundDispatcher: CoroutineDispatcher() {

    private val threadFactory = object: ThreadFactory {
        private val threadCount = AtomicInteger(0)
        private val nextThreadName get() = "CpuBoundDispatcher-${threadCount.incrementAndGet()}"

        override fun newThread(runnable: java.lang.Runnable): Thread {
            return Thread(runnable, nextThreadName)
        }
    }

    // intentionally claiming less CPUs to avoid degrading "monitored" app perf
    private val maxPoolSize = max(2, Runtime.getRuntime().availableProcessors() - 2)

    private val threadPool = ThreadPoolExecutor(
        2,
        maxPoolSize,
        60L,
        TimeUnit.SECONDS,
        LinkedBlockingQueue(),
        threadFactory
    );

    private val dispatcher = threadPool.asCoroutineDispatcher()

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatcher.dispatch(context, block)
    }

    /**
     * Dispatcher that should be used for CPU bound tasks
     */
    val Dispatchers.CpuBound get() = CpuBoundDispatcher

}
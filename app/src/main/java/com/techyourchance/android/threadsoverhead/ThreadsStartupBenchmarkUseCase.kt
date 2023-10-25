package com.techyourchance.android.threadsoverhead

import com.techyourchance.android.common.coroutines.BackgroundDispatcher.Background
import com.techyourchance.android.common.datetime.DateTimeProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
import kotlin.math.sqrt

class ThreadsStartupBenchmarkUseCase @Inject constructor(
    private val dateTimeProvider: DateTimeProvider,
) {

    data class Result(
        val threadsStartupResult: BackgroundTasksStartupResult,
        val coroutinesStartupResult: BackgroundTasksStartupResult,
    )

    // using the "standard" background dispatcher for this benchmark
    private val coroutinesScope = CoroutineScope(Dispatchers.Default)

    suspend fun runBenchmark(): Result {
        return withContext(Dispatchers.Background) {
            val threadsStartupResult = benchmarkThreads()
            val coroutinesStartupResult = benchmarkCoroutines()
            Result(threadsStartupResult, coroutinesStartupResult)
        }
    }

    private suspend fun benchmarkThreads(): BackgroundTasksStartupResult {
        val threadsTimings = ConcurrentHashMap<Int, BackgroundTaskStartupData>(NUM_TASKS)
        for (i in 0 until NUM_TASKS) {
            coroutineContext.ensureActive()
            benchmarkSingleThread(i, threadsTimings)
        }
        return computeResult(threadsTimings)
    }

    private fun benchmarkSingleThread(i: Int, threadsTimings: MutableMap<Int, BackgroundTaskStartupData>) {
        val startedNano = dateTimeProvider.getNanoTime()
        val threadName = "benchmark-thread-$i"
        val activatedNano = AtomicLong(0)
        val thread = Thread(
            {
                activatedNano.set(dateTimeProvider.getNanoTime())
            },
            threadName
        ).apply {
            start()
            join()
        }
        val terminatedNano = dateTimeProvider.getNanoTime()
        threadsTimings[i] = BackgroundTaskStartupData(activatedNano.get() - startedNano)
    }

    private suspend fun benchmarkCoroutines(): BackgroundTasksStartupResult {
        val coroutinesTimings: ConcurrentHashMap<Int, BackgroundTaskStartupData> = ConcurrentHashMap<Int, BackgroundTaskStartupData>(NUM_TASKS)
        for (i in 0 until NUM_TASKS) {
            coroutineContext.ensureActive()
            benchmarkSingleCoroutine(i, coroutinesTimings)
        }
        return computeResult(coroutinesTimings)
    }

    private suspend fun benchmarkSingleCoroutine(i: Int, coroutinesTimings: MutableMap<Int, BackgroundTaskStartupData>) {
        val startedNano = dateTimeProvider.getNanoTime()
        val activatedNano = AtomicLong(0)
        val job = coroutinesScope.launch {
            activatedNano.set(dateTimeProvider.getNanoTime())
        }.apply {
            join()
        }
        val terminatedNano = dateTimeProvider.getNanoTime()
        coroutinesTimings[i] = BackgroundTaskStartupData(activatedNano.get() - startedNano)
    }

    private fun computeResult(backgroundTasksTimings: Map<Int, BackgroundTaskStartupData>): BackgroundTasksStartupResult {
        val averageStartupDurationNano = computeAverageStartupDurationNano(backgroundTasksTimings)
        val stdStartupDurationNano = computeStdStartupDurationNano(averageStartupDurationNano, backgroundTasksTimings)
        return BackgroundTasksStartupResult(
            averageStartupDurationNano,
            stdStartupDurationNano,
            backgroundTasksTimings
        )
    }

    private fun computeAverageStartupDurationNano(backgroundTasksTimings: Map<Int, BackgroundTaskStartupData>): Long {
        val sumOfStartupTimes = backgroundTasksTimings.values.sumOf { it.startupDurationNano }
        return sumOfStartupTimes / backgroundTasksTimings.size
    }

    private fun computeStdStartupDurationNano(
        averageStartupDurationNano: Long,
        backgroundTasksTimings: Map<Int, BackgroundTaskStartupData>
    ): Long {
        val sumOfSquaredDifferences = backgroundTasksTimings.values.sumOf {
            val diff = it.startupDurationNano - averageStartupDurationNano
            diff * diff
        }
        val meanOfSquaredDifferences = sumOfSquaredDifferences / backgroundTasksTimings.size
        return sqrt(meanOfSquaredDifferences.toDouble()).toLong()
    }

    companion object {
        const val NUM_TASKS = 1000
    }
}
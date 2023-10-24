package com.techyourchance.android.threadsoverhead

import com.techyourchance.android.common.coroutines.BackgroundDispatcher.Background
import com.techyourchance.android.common.datetime.DateTimeProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import kotlin.math.sqrt

class ThreadsStartupBenchmarkUseCase @Inject constructor(
    private val dateTimeProvider: DateTimeProvider,
) {

    suspend fun runBenchmark(): ThreadsStartupResult {
        return withContext(Dispatchers.Background) {
            val threadsTimings = ConcurrentHashMap<Int, ThreadStartupData>(NUM_THREADS)
            for(i in 0 until NUM_THREADS) {
                if (!isActive) {
                    break
                }
                benchmarkSingleThread(i, threadsTimings)
            }
            val averageStartupDurationNano = computeAverageThreadStartupDurationNano(threadsTimings)
            val stdStartupDurationNano = computeStdThreadStartupDurationNano(averageStartupDurationNano, threadsTimings)
            ThreadsStartupResult(
                averageStartupDurationNano,
                stdStartupDurationNano,
                threadsTimings
            )
        }
    }

    private fun benchmarkSingleThread(i: Int, threadsTimings: MutableMap<Int, ThreadStartupData>) {
        val threadStartedNano = dateTimeProvider.getNanoTime()
        val threadName = "benchmark-thread-$i"
        val threadActivatedNano = AtomicLong(0)
        val thread = Thread(
            {
                threadActivatedNano.set(dateTimeProvider.getNanoTime())
            },
            threadName
        ).apply {
            start()
            join()
        }
        val threadTerminatedNano = dateTimeProvider.getNanoTime()
        threadsTimings[i] = ThreadStartupData(threadName, threadActivatedNano.get() - threadStartedNano)
    }

    private fun computeAverageThreadStartupDurationNano(threadsTimings: Map<Int, ThreadStartupData>): Long {
        val sumOfStartupTimes = threadsTimings.values.sumOf { it.startupDurationNano }
        return sumOfStartupTimes / threadsTimings.size
    }

    private fun computeStdThreadStartupDurationNano(
        averageStartupDurationNano: Long,
        threadsTimings: Map<Int, ThreadStartupData>
    ): Long {
        val sumOfSquaredDifferences = threadsTimings.values.sumOf {
            val diff = it.startupDurationNano - averageStartupDurationNano
            diff * diff
        }
        val meanOfSquaredDifferences = sumOfSquaredDifferences / threadsTimings.size
        return sqrt(meanOfSquaredDifferences.toDouble()).toLong()
    }

    companion object {
        const val NUM_THREADS = 10000
    }
}
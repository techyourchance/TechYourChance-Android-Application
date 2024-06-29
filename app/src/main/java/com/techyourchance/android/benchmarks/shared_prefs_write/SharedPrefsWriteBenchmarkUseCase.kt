package com.techyourchance.android.benchmarks.shared_prefs_write

import android.content.Context
import android.content.SharedPreferences
import com.techyourchance.android.common.datetime.DateTimeProvider
import com.techyourchance.android.common.maths.LinearFitCalculator
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import javax.inject.Inject

class SharedPrefsWriteBenchmarkUseCase @Inject constructor(
    private val context: Context,
    private val dateTimeProvider: DateTimeProvider,
    private val linearFitCalculator: LinearFitCalculator,
) {

    data class Result(
        val resultWithCommit: SharedPrefsWriteResult,
        val resultWithApply: SharedPrefsWriteResult,
    )

    private val coroutinesDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    suspend fun runBenchmark(valueToWrite: String): Result {
        return withContext(coroutinesDispatcher) {

            val sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

            val commitRawTimingsNano = runBenchmarkWithWriteFunction(sharedPrefs, valueToWrite) {
                it.commit()
            }

            val applyRawTimingsNano = runBenchmarkWithWriteFunction(sharedPrefs, valueToWrite) {
                it.apply()
            }

            val resultWithCommit = computeResult(commitRawTimingsNano)

            val resultWithApply = computeResult(applyRawTimingsNano)

            Result(resultWithCommit, resultWithApply)
        }
    }

    private fun runBenchmarkWithWriteFunction(
        sharedPrefs: SharedPreferences,
        sharedPrefValue: String,
        editorWriteFunction: (SharedPreferences.Editor) -> Unit
    ): MutableMap<Int, MutableMap<Int, Long>> {

        var sharedPrefsEditor: SharedPreferences.Editor? = null
        var sharedPrefKey = ""
        var startNano = 0L
        var endNano = 0L

        val rawTimingsNano = mutableMapOf<Int, MutableMap<Int, Long>>()

        for (benchmarkIteration in 0 until NUM_ITERATIONS) {
            sharedPrefs.edit().clear().commit()

            rawTimingsNano[benchmarkIteration] = mutableMapOf()
            for (prefEntryIndex in 0 until NUM_OF_WRITES_PER_ITERATION) {

                sharedPrefKey =
                    PREF_KEY_PREFIX + prefEntryIndex.toString().padEnd(NUM_OF_WRITES_PER_ITERATION.toString().length)

                startNano = dateTimeProvider.getNanoTime()
                sharedPrefsEditor = sharedPrefs.edit().putString(sharedPrefKey, sharedPrefValue)
                editorWriteFunction(sharedPrefsEditor)
                endNano = dateTimeProvider.getNanoTime()

                rawTimingsNano[benchmarkIteration]!![prefEntryIndex] = endNano - startNano

            }
        }
        return rawTimingsNano
    }


    private fun computeResult(rawTimingsNano: Map<Int, MutableMap<Int, Long>>): SharedPrefsWriteResult {
        val averageWriteDurationsWithCommit = computeAverageWriteDurationsNano(rawTimingsNano)
        return SharedPrefsWriteResult(
            averageWriteDurationsWithCommit,
            linearFitCalculator.calculateLinearFit(
                averageWriteDurationsWithCommit.map { entry -> Pair(entry.key.toDouble(), entry.value.toDouble()) }
            )
        )
    }

    private fun computeAverageWriteDurationsNano(sharedPrefsWriteTimings: Map<Int, Map<Int, Long>>): Map<Int, Long> {
        val sumsOfWriteTimes = computeSumsWithInternalKeys(sharedPrefsWriteTimings)
        return sumsOfWriteTimes.mapValues { entry ->
            (entry.value.toDouble() / sharedPrefsWriteTimings.size).toLong()
        }
    }

    private fun computeSumsWithInternalKeys(externalMap: Map<Int, Map<Int, Long>>): Map<Int, Long> {
        val sums = mutableMapOf<Int, Long>()
        for ((externalKey, internalMap) in externalMap) {
            for ((internalKey, value) in internalMap) {
                sums[internalKey] = sums.getOrDefault(internalKey, 0) + value
            }
        }
        return sums
    }

    companion object {

        private const val SHARED_PREFS_NAME = "benchmark_shared_prefs"
        private const val NUM_OF_WRITES_PER_ITERATION = 100
        private const val PREF_KEY_PREFIX = "key"
        private const val NUM_ITERATIONS = 10
    }
}
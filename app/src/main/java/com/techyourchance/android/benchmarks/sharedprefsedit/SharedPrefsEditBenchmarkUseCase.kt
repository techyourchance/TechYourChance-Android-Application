package com.techyourchance.android.benchmarks.sharedprefsedit

import android.content.Context
import android.content.SharedPreferences
import com.techyourchance.android.common.datetime.DateTimeProvider
import com.techyourchance.android.common.random.RandomStringsGenerator
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import javax.inject.Inject

class SharedPrefsEditBenchmarkUseCase @Inject constructor(
    private val context: Context,
    private val randomStringsGenerator: RandomStringsGenerator,
    private val dateTimeProvider: DateTimeProvider,
) {

    data class Result(
        val resultWithCommit: SharedPrefsEditResult,
        val resultWithApply: SharedPrefsEditResult,
    )

    private val coroutinesDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    suspend fun runBenchmark(): Result {
        return withContext(coroutinesDispatcher) {

            val sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            val sharedPrefValue = randomStringsGenerator.getRandomAlphanumericString(PREF_VALUE_LENGTH)

            val commitRawTimingsNano = runBenchmarkWithWriteFunction(sharedPrefs, sharedPrefValue) {
                it.commit()
            }

            val applyRawTimingsNano = runBenchmarkWithWriteFunction(sharedPrefs, sharedPrefValue) {
                it.apply()
            }

            val resultWithCommit = SharedPrefsEditResult(computeAverageEditDurationsNano(commitRawTimingsNano))
            val resultWithApply = SharedPrefsEditResult(computeAverageEditDurationsNano(applyRawTimingsNano))

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
            for (prefEntryIndex in 0 until NUM_PREF_KEYS_PER_ITERATION) {

                sharedPrefKey =
                    PREF_KEY_PREFIX + prefEntryIndex.toString().padEnd(NUM_PREF_KEYS_PER_ITERATION.toString().length)

                startNano = dateTimeProvider.getNanoTime()
                sharedPrefsEditor = sharedPrefs.edit().putString(sharedPrefKey, sharedPrefValue)
                editorWriteFunction(sharedPrefsEditor)
                endNano = dateTimeProvider.getNanoTime()

                rawTimingsNano[benchmarkIteration]!![prefEntryIndex] = endNano - startNano

            }
        }
        return rawTimingsNano
    }

    private fun computeAverageEditDurationsNano(sharedPrefsEditTimings: Map<Int, Map<Int, Long>>): Map<Int, Long> {
        val sumsOfEditTimes = computeSumsWithInternalKeys(sharedPrefsEditTimings)
        return sumsOfEditTimes.mapValues { entry ->
            (entry.value.toDouble() / sharedPrefsEditTimings.size).toLong()
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
        private const val NUM_PREF_KEYS_PER_ITERATION = 100
        private const val PREF_KEY_PREFIX = "key"
        private const val PREF_VALUE_LENGTH = 100
        private const val NUM_ITERATIONS = 10
    }
}
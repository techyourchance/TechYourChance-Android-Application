package com.techyourchance.android.screens.common

import com.techyourchance.android.backgroundtasksbenchmark.memory.BackgroundTasksMemoryBenchmarkPhase
import java.io.Serializable

@Suppress("ClassName")
sealed class ScreenSpec(val activityName: ActivityName): Serializable {
    object Home: ScreenSpec(ActivityName.MAIN)
    object StackOverflowQuestionsList: ScreenSpec(ActivityName.MAIN)
    data class StackOverflowQuestionDetails(val questionId: String): ScreenSpec(ActivityName.MAIN)
    object BiometricLock: ScreenSpec(ActivityName.MAIN)
    object NdkBasics: ScreenSpec(ActivityName.MAIN)
    object ForegroundService: ScreenSpec(ActivityName.MAIN)
    object WorkManager: ScreenSpec(ActivityName.MAIN)
    object UserInterfaces: ScreenSpec(ActivityName.USER_INTERFACES)
    object Animations: ScreenSpec(ActivityName.USER_INTERFACES)
    object StackedCardsAnimation: ScreenSpec(ActivityName.USER_INTERFACES)
    object Benchmarks: ScreenSpec(ActivityName.MAIN)
    object BackgroundTasksStartupBenchmark: ScreenSpec(ActivityName.MAIN)
    data class BackgroundTasksMemoryBenchmark(
        val startBenchmark: Boolean = false,
        val startBenchmarkPhase: BackgroundTasksMemoryBenchmarkPhase = BackgroundTasksMemoryBenchmarkPhase.THREADS,
        val startBenchmarkIteration: Int = 0,
    ): ScreenSpec(ActivityName.MAIN)

    companion object {
        /**
         * Intent extra key for Serialized ScreenSpec's
         */
        const val INTENT_EXTRA_SCREEN_SPEC = "INTENT_EXTRA_SCREEN_SPEC"
    }

}
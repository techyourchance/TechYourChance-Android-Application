package com.techyourchance.android.backgroundtasksbenchmark.memory

import com.techyourchance.android.common.application.AppMemoryInfoProvider
import com.techyourchance.android.common.coroutines.BackgroundDispatcher.Background
import com.techyourchance.android.common.datetime.DateTimeProvider
import com.techyourchance.android.common.logs.MyLogger
import com.techyourchance.android.common.restart.RestartAppUseCase
import com.techyourchance.android.screens.common.ScreenSpec
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BackgroundTasksMemoryBenchmarkUseCase @Inject constructor(
    private val appMemoryInfoProvider: AppMemoryInfoProvider,
    private val saveBackgroundTaskMemoryDataUseCase: SaveBackgroundTaskMemoryDataUseCase,
    private val clearBackgroundTaskMemoryDataUseCase: ClearBackgroundTaskMemoryDataUseCase,
    private val fetchBackgroundTaskMemoryDataUseCase: FetchBackgroundTaskMemoryDataUseCase,
    private val restartAppUseCase: RestartAppUseCase,
    private val dateTimeProvider: DateTimeProvider,
) {

    data class Result(
        val isCompleteResult: Boolean,
        val threadsResult: BackgroundTasksMemoryResult,
        val coroutinesResult: BackgroundTasksMemoryResult,
        val threadPoolResult: BackgroundTasksMemoryResult,
    )

    suspend fun runBenchmark(benchmarkPhase: BackgroundTasksMemoryBenchmarkPhase, benchmarkIterationNum: Int): Result {
        return withContext(Dispatchers.Background) {

            val iterationData = preAllocateIterationData()

            val isBenchmarkCompleted = when(benchmarkPhase) {
                BackgroundTasksMemoryBenchmarkPhase.THREADS -> {
                    if (benchmarkIterationNum == 0) {
                        clearBackgroundTaskMemoryDataUseCase.clearData()
                    }
                    benchmarkThreads(iterationData, benchmarkIterationNum)
                }
                BackgroundTasksMemoryBenchmarkPhase.COROUTINES ->  {
                    benchmarkCoroutines(iterationData, benchmarkIterationNum)
                }
                BackgroundTasksMemoryBenchmarkPhase.THREAD_POOL -> {
                    benchmarkThreadPool(iterationData, benchmarkIterationNum)
                }
            }

            return@withContext if (isBenchmarkCompleted) {
                Result(
                    true,
                    fetchBackgroundTaskMemoryDataUseCase.fetchData(LABEL_THREADS),
                    fetchBackgroundTaskMemoryDataUseCase.fetchData(LABEL_COROUTINES),
                    fetchBackgroundTaskMemoryDataUseCase.fetchData(LABEL_THREAD_POOL),
                )
            } else {
                Result(
                    false,
                    BackgroundTasksMemoryResult.NULL_OBJECT,
                    BackgroundTasksMemoryResult.NULL_OBJECT,
                    BackgroundTasksMemoryResult.NULL_OBJECT,
                )
            }
        }
    }

    private fun preAllocateIterationData(): ConcurrentHashMap<Int, BackgroundTaskMemoryData> {
        val iterationData = ConcurrentHashMap<Int, BackgroundTaskMemoryData>(NUM_TASKS)
        for (taskNum in 0 until NUM_TASKS) {
            iterationData[taskNum] = BackgroundTaskMemoryData.NULL_OBJECT
        }
        return iterationData
    }

    private suspend fun benchmarkThreads(
        iterationData: MutableMap<Int, BackgroundTaskMemoryData>,
        iterationNum: Int
    ): Boolean {
        MyLogger.i("benchmarkThreads(); iteration: $iterationNum")
        val startedThreads = mutableListOf<Thread>()
        val threadBarrier = CyclicBarrier(NUM_TASKS + 1)
        for (taskNum in 0 until NUM_TASKS) {
            coroutineContext.ensureActive()
            val thread = benchmarkSingleThread(
                iterationNum, taskNum, iterationData, threadBarrier
            )
            startedThreads.add(thread)
        }
        threadBarrier.await()
        startedThreads.forEach { it.join() }
        saveBackgroundTaskMemoryDataUseCase.saveData(LABEL_THREADS, iterationNum, iterationData)
        return !restartAppForNextIteration(BackgroundTasksMemoryBenchmarkPhase.THREADS, iterationNum)
    }

    private suspend fun benchmarkSingleThread(
        iterationNum: Int,
        taskNum: Int,
        threadsData: MutableMap<Int, BackgroundTaskMemoryData>,
        threadBarrier: CyclicBarrier
    ): Thread {
        return suspendCoroutine { continuation ->
            val thread = Thread {
                MyLogger.d("Thread started; iteration: $iterationNum; task $taskNum")
                threadsData[taskNum] = getBackgroundTaskMemoryData()
                continuation.resume(Thread.currentThread())
                threadBarrier.await()
                MyLogger.d("Thread terminates; iteration: $iterationNum; task $taskNum")
            }.apply {
                start()
            }
        }
    }

    private suspend fun benchmarkCoroutines(
        iterationData: MutableMap<Int, BackgroundTaskMemoryData>,
        iterationNum: Int
    ): Boolean {
        val coroutinesExecutor = ThreadPoolExecutor(
            0, Int.MAX_VALUE,
            1L, TimeUnit.SECONDS,
            SynchronousQueue()
        )

        val coroutinesScope = CoroutineScope(coroutinesExecutor.asCoroutineDispatcher())

        MyLogger.i("benchmarkCoroutines(); iteration: $iterationNum")
        val awaitFlow = MutableSharedFlow<Unit>()
        for (taskNum in 0 until NUM_TASKS) {
            coroutineContext.ensureActive()
            benchmarkSingleCoroutine(
                iterationNum, taskNum, iterationData, awaitFlow, coroutinesScope
            )
        }
        awaitFlow.emit(Unit)

        coroutinesScope.cancel()
        coroutinesExecutor.shutdownNow()

        saveBackgroundTaskMemoryDataUseCase.saveData(LABEL_COROUTINES, iterationNum, iterationData)
        return !restartAppForNextIteration(BackgroundTasksMemoryBenchmarkPhase.COROUTINES, iterationNum)
    }

    private suspend fun benchmarkSingleCoroutine(
        iterationNum: Int,
        taskNum: Int,
        iterationData: MutableMap<Int, BackgroundTaskMemoryData>,
        awaitFlow: MutableSharedFlow<Unit>,
        coroutinesScope: CoroutineScope,
    ) {
        suspendCoroutine { continuation ->
            coroutinesScope.launch {
                MyLogger.d("Coroutine launched; iteration: $iterationNum; task $taskNum")
                iterationData[taskNum] = getBackgroundTaskMemoryData()
                continuation.resume(Unit)
                try {
                    awaitFlow.collect {
                        throw CancellationException()
                    }
                } catch (e: CancellationException) {
                    //no-op
                }
                MyLogger.d("Coroutine terminates; iteration: $iterationNum; task $taskNum")
            }
        }
    }

    private suspend fun benchmarkThreadPool(
        iterationData: MutableMap<Int, BackgroundTaskMemoryData>,
        iterationNum:Int,
    ): Boolean {
        // same configuration as with coroutines
        val threadPool = ThreadPoolExecutor(
            0, Int.MAX_VALUE,
            1L, TimeUnit.SECONDS,
            SynchronousQueue()
        )

        MyLogger.i("benchmarkThreadPool(); iteration: $iterationNum")
        val threadsBarrier = CyclicBarrier(NUM_TASKS + 1)
        for (taskNum in 0 until NUM_TASKS) {
            coroutineContext.ensureActive()
            benchmarkSingleThreadPoolThread(iterationNum, taskNum, iterationData, threadPool, threadsBarrier)
        }
        threadsBarrier.await()

        while (threadPool.activeCount != 0) {
            delay(20)
        }
        threadPool.shutdownNow()

        saveBackgroundTaskMemoryDataUseCase.saveData(LABEL_THREAD_POOL, iterationNum, iterationData)
        return !restartAppForNextIteration(BackgroundTasksMemoryBenchmarkPhase.THREAD_POOL, iterationNum)
    }

    private suspend fun benchmarkSingleThreadPoolThread(
        iterationNum: Int,
        taskNum: Int,
        iterationData:MutableMap<Int, BackgroundTaskMemoryData>,
        threadPool: ThreadPoolExecutor,
        threadsBarrier: CyclicBarrier
    ) {
        suspendCoroutine { continuation ->
            threadPool.execute {
                MyLogger.d("Thread pool started; iteration: $iterationNum; task $taskNum")
                iterationData[taskNum] = getBackgroundTaskMemoryData()
                continuation.resume(Unit)
                threadsBarrier.await()
                MyLogger.d("Thread pool terminates; iteration: $iterationNum; task $taskNum")
            }
        }
    }

    private fun getBackgroundTaskMemoryData(): BackgroundTaskMemoryData {
        val timestamp = dateTimeProvider.getTimestampUtc()
        val consumedMemory =  appMemoryInfoProvider.getAppMemoryConsumption().run {
            heapMemoryKb + nativeMemoryKb + stackMemoryKb
        }
        return BackgroundTaskMemoryData(timestamp, consumedMemory)
    }

    private fun restartAppForNextIteration(
        currentBenchmarkPhase: BackgroundTasksMemoryBenchmarkPhase,
        currentIterationNum: Int
    ): Boolean {
        val nextPhaseAndIterationNum: Pair<BackgroundTasksMemoryBenchmarkPhase, Int>? = when(currentBenchmarkPhase) {
            BackgroundTasksMemoryBenchmarkPhase.THREADS -> {
                if (currentIterationNum == NUM_ITERATIONS - 1) {
                    Pair(BackgroundTasksMemoryBenchmarkPhase.COROUTINES, 0)
                } else {
                    Pair(BackgroundTasksMemoryBenchmarkPhase.THREADS, currentIterationNum + 1)
                }
            }
            BackgroundTasksMemoryBenchmarkPhase.COROUTINES -> {
                if (currentIterationNum == NUM_ITERATIONS - 1) {
                    Pair(BackgroundTasksMemoryBenchmarkPhase.THREAD_POOL, 0)
                } else {
                    Pair(BackgroundTasksMemoryBenchmarkPhase.COROUTINES, currentIterationNum + 1)
                }
            }
            BackgroundTasksMemoryBenchmarkPhase.THREAD_POOL -> {
                if (currentIterationNum == NUM_ITERATIONS - 1) {
                    null
                } else {
                    Pair(BackgroundTasksMemoryBenchmarkPhase.THREAD_POOL, currentIterationNum + 1)
                }
            }
        }
        return if (nextPhaseAndIterationNum != null) {
            restartAppUseCase.restartAppOnScreen(
                ScreenSpec.BackgroundTasksMemoryBenchmark(
                    startBenchmark = true,
                    startBenchmarkPhase = nextPhaseAndIterationNum.first,
                    startBenchmarkIteration = nextPhaseAndIterationNum.second
                )
            )
            true
        } else {
            false
        }
    }

    companion object {
        const val NUM_TASKS = 100
        const val NUM_ITERATIONS = 3

        const val LABEL_THREADS = "threads"
        const val LABEL_COROUTINES = "coroutines"
        const val LABEL_THREAD_POOL = "thread_pool"
    }
}
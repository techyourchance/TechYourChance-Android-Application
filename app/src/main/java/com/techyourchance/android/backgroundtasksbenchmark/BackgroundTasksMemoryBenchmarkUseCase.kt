package com.techyourchance.android.backgroundtasksbenchmark

import com.techyourchance.android.common.application.AppMemoryInfoProvider
import com.techyourchance.android.common.coroutines.BackgroundDispatcher.Background
import com.techyourchance.android.common.logs.MyLogger
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
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BackgroundTasksMemoryBenchmarkUseCase @Inject constructor(
    private val appMemoryInfoProvider: AppMemoryInfoProvider,
) {

    data class Result(
        val threadsResult: BackgroundTasksMemoryResult,
        val coroutinesResult: BackgroundTasksMemoryResult,
        val threadPoolResult: BackgroundTasksMemoryResult,
    )

    suspend fun runBenchmark(): Result {
        return withContext(Dispatchers.Background) {

            // pre-allocated all data structures to avoid difference in memory overhead between benchmarks
            val threadsData = preAllocatedDataStructures()
            val coroutinesData = preAllocatedDataStructures()
            val threadPoolData = preAllocatedDataStructures()

            askForGarbageCollection()

            benchmarkThreads(threadsData)

            askForGarbageCollection()

            benchmarkCoroutines(coroutinesData)

            askForGarbageCollection()

            benchmarkThreadPool(threadPoolData)

            Result(
                BackgroundTasksMemoryResult(computeAverage(threadsData), threadsData),
                BackgroundTasksMemoryResult(computeAverage(coroutinesData), coroutinesData),
                BackgroundTasksMemoryResult(computeAverage(threadPoolData), threadPoolData),
            )
        }
    }

    private fun preAllocatedDataStructures(): MutableMap<Int, MutableMap<Int, BackgroundTaskMemoryData>> {
        val data = ConcurrentHashMap<Int, MutableMap<Int, BackgroundTaskMemoryData>>(NUM_ITERATIONS)
        for (iterationNum in 0 until NUM_ITERATIONS) {
            val iterationData = ConcurrentHashMap<Int, BackgroundTaskMemoryData>(NUM_TASK_GROUPS)
            for (taskNum in 0 until NUM_TASK_GROUPS) {
                iterationData[taskNum] = BackgroundTaskMemoryData.NULL_OBJECT
            }
            data[iterationNum] = iterationData
        }
        return data
    }

    private suspend fun benchmarkThreads(threadsData: MutableMap<Int, MutableMap<Int, BackgroundTaskMemoryData>>) {
        for (iterationNum in 0 until NUM_ITERATIONS) {
            MyLogger.i("benchmarkThreads(); iteration: $iterationNum")
            askForGarbageCollection()
            val startedThreads = mutableListOf<Thread>()
            val threadBarrier = CyclicBarrier(NUM_TASK_GROUPS * NUM_TASKS_IN_GROUP + 1)
            for (taskGroupNum in 0 until NUM_TASK_GROUPS) {
                coroutineContext.ensureActive()
                val groupStartedThreads = benchmarkSingleThreadsGroup(iterationNum, taskGroupNum, threadsData, threadBarrier)
                startedThreads.addAll(groupStartedThreads)
            }
            threadBarrier.await()
            startedThreads.forEach { it.join() }
        }
    }

    private suspend fun benchmarkSingleThreadsGroup(
        iterationNum: Int,
        taskGroupNum: Int,
        threadsData: MutableMap<Int, MutableMap<Int, BackgroundTaskMemoryData>>,
        threadBarrier: CyclicBarrier
    ): List<Thread> {
        val groupThreads = mutableListOf<Thread>()
        suspendCoroutine { continuation ->
            val groupThreadBarrier = CyclicBarrier(NUM_TASKS_IN_GROUP)
            for (taskInGroup in 0 until NUM_TASKS_IN_GROUP) {
                Thread {
                    MyLogger.d("Thread started; iteration: $iterationNum; group $taskGroupNum; task $taskInGroup")
                    groupThreadBarrier.await() // make sure all threads started
                    if (taskInGroup == NUM_TASKS_IN_GROUP - 1) {
                        val appMemoryConsumption = appMemoryInfoProvider.getAppMemoryConsumption()
                        threadsData[iterationNum]!![taskGroupNum] = BackgroundTaskMemoryData(appMemoryConsumption)
                        continuation.resume(Unit)
                    }
                    threadBarrier.await()
                    MyLogger.d("Thread terminates; iteration: $iterationNum; group $taskGroupNum; task $taskInGroup")
                }.apply {
                    start()
                    groupThreads.add(this)
                }
            }
        }
        return groupThreads
    }

    private suspend fun benchmarkCoroutines(coroutinesData: MutableMap<Int, MutableMap<Int, BackgroundTaskMemoryData>>) {
        val coroutinesExecutor = ThreadPoolExecutor(
            0, Int.MAX_VALUE,
            1L, TimeUnit.SECONDS,
            SynchronousQueue()
        )

        val coroutinesScope = CoroutineScope(coroutinesExecutor.asCoroutineDispatcher())

        for (iterationNum in 0 until NUM_ITERATIONS) {
            MyLogger.i("benchmarkCoroutines(); iteration: $iterationNum")
            askForGarbageCollection()
            val awaitFlow = MutableSharedFlow<Unit>()
            for (taskGroupNum in 0 until NUM_TASK_GROUPS) {
                coroutineContext.ensureActive()
                benchmarkSingleCoroutineGroup(iterationNum, taskGroupNum, coroutinesData, awaitFlow, coroutinesScope)
            }
            awaitFlow.emit(Unit)
        }

        coroutinesScope.cancel()
        delay(100)
        coroutinesExecutor.shutdownNow()
    }

    private suspend fun benchmarkSingleCoroutineGroup(
        iterationNum: Int,
        taskGroupNum: Int,
        coroutinesData: MutableMap<Int, MutableMap<Int, BackgroundTaskMemoryData>>,
        awaitFlow: MutableSharedFlow<Unit>,
        coroutinesScope: CoroutineScope,
    ) {
        suspendCoroutine { continuation ->
            val groupLaunchedCounter = AtomicInteger(0)
            val groupAwaitFlow = MutableSharedFlow<Unit>()
            for (taskInGroup in 0 until NUM_TASKS_IN_GROUP) {
                coroutinesScope.launch {
                    MyLogger.d("Coroutine launched; iteration: $iterationNum; group $taskGroupNum; task $taskInGroup")
                    if(groupLaunchedCounter.incrementAndGet() == NUM_TASKS_IN_GROUP) {
                        groupAwaitFlow.emit(Unit)
                    } else {
                        try {
                            groupAwaitFlow.collect {
                                throw CancellationException()
                            }
                        } catch (e: CancellationException) {
                            // no-op
                        }
                    }
                    if (taskInGroup == NUM_TASKS_IN_GROUP - 1) {
                        val appMemoryConsumption = appMemoryInfoProvider.getAppMemoryConsumption()
                        coroutinesData[iterationNum]!![taskGroupNum] = BackgroundTaskMemoryData(appMemoryConsumption)
                        continuation.resume(Unit)
                    }
                    try {
                        awaitFlow.collect {
                            throw CancellationException()
                        }
                    } catch (e: CancellationException) {
                        //no-op
                    }
                    MyLogger.d("Coroutine terminates; iteration: $iterationNum; group $taskGroupNum; task $taskInGroup")
                }
            }
        }
    }

    private suspend fun benchmarkThreadPool(threadPoolData: MutableMap<Int, MutableMap<Int, BackgroundTaskMemoryData>>) {
        // same configuration as with coroutines
        val threadPool = ThreadPoolExecutor(
            0, Int.MAX_VALUE,
            1L, TimeUnit.SECONDS,
            SynchronousQueue()
        )

        for (iterationNum in 0 until NUM_ITERATIONS) {
            MyLogger.i("benchmarkThreadPool(); iteration: $iterationNum")
            askForGarbageCollection()
            val threadsBarrier = CyclicBarrier(NUM_TASK_GROUPS * NUM_TASKS_IN_GROUP + 1)
            for (taskGroupNum in 0 until NUM_TASK_GROUPS) {
                coroutineContext.ensureActive()
                benchmarkSingleThreadPoolGroup(iterationNum, taskGroupNum, threadPoolData, threadPool, threadsBarrier)
            }
            threadsBarrier.await()
        }
        while (threadPool.activeCount != 0) {
            delay(20)
        }
        threadPool.shutdownNow()
    }

    private suspend fun benchmarkSingleThreadPoolGroup(
        iterationNum: Int,
        taskGroupNum: Int,
        threadPoolData: MutableMap<Int, MutableMap<Int, BackgroundTaskMemoryData>>,
        threadPool: ThreadPoolExecutor,
        threadsBarrier: CyclicBarrier
    ) {
        suspendCoroutine { continuation ->
            val groupThreadBarrier = CyclicBarrier(NUM_TASKS_IN_GROUP)
            for (taskInGroup in 0 until NUM_TASKS_IN_GROUP) {
                threadPool.execute {
                    MyLogger.d("Thread pool started; iteration: $iterationNum; group $taskGroupNum; task $taskInGroup")
                    groupThreadBarrier.await() // make sure all threads started
                    if (taskInGroup == NUM_TASKS_IN_GROUP - 1) {
                        val appMemoryConsumption = appMemoryInfoProvider.getAppMemoryConsumption()
                        threadPoolData[iterationNum]!![taskGroupNum] = BackgroundTaskMemoryData(appMemoryConsumption)
                        continuation.resume(Unit)
                    }
                    threadsBarrier.await()
                    MyLogger.d("Thread pool terminates; iteration: $iterationNum; group $taskGroupNum; task $taskInGroup")
                }
            }
        }
    }

    private suspend fun askForGarbageCollection() {
        delay(1000)
        System.gc()
        delay(1000)
    }

    private fun computeAverage(tasksData: MutableMap<Int, MutableMap<Int, BackgroundTaskMemoryData>>): Map<Int, BackgroundTaskMemoryData> {
        val averageTasksMemoryConsumptions = ConcurrentHashMap<Int, BackgroundTaskMemoryData>(NUM_TASK_GROUPS)
        for (taskNum in 0 until NUM_TASK_GROUPS) {
            var sumTaskHeapMemoryConsumption = 0f
            var sumTaskNativeMemoryConsumption = 0f
            for (iterationNum in 0 until NUM_ITERATIONS) {
                sumTaskHeapMemoryConsumption += tasksData[iterationNum]!![taskNum]!!.appMemoryConsumption.heapMemoryKb
                sumTaskNativeMemoryConsumption += tasksData[iterationNum]!![taskNum]!!.appMemoryConsumption.nativeMemoryKb
            }
            val averageHeapTaskMemoryConsumption = sumTaskHeapMemoryConsumption / NUM_TASK_GROUPS
            val averageNativeTaskMemoryConsumption = sumTaskNativeMemoryConsumption / NUM_TASK_GROUPS
            averageTasksMemoryConsumptions[taskNum] = BackgroundTaskMemoryData(
                AppMemoryInfoProvider.AppMemoryInfo(
                    averageHeapTaskMemoryConsumption,
                    averageNativeTaskMemoryConsumption
                )
            )
        }
        return averageTasksMemoryConsumptions
    }

    companion object {
        const val NUM_TASK_GROUPS = 100
        const val NUM_TASKS_IN_GROUP = 5
        const val NUM_ITERATIONS = 1
    }
}
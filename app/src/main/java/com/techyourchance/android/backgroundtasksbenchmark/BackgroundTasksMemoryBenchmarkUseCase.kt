package com.techyourchance.android.backgroundtasksbenchmark

import com.techyourchance.android.common.application.AppMemoryInfoProvider
import com.techyourchance.android.common.coroutines.BackgroundDispatcher.Background
import com.techyourchance.android.common.logs.MyLogger
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
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

    // equivalent thread pools for thread pool and coroutines benchmarks
    private val threadPool = Executors.newCachedThreadPool()
    private val coroutinesDispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
    private val coroutinesScope = CoroutineScope(coroutinesDispatcher)

    suspend fun runBenchmark(): Result {
        return withContext(Dispatchers.Background) {

            // pre-allocated all data structures to avoid difference in memory overhead between benchmarks
            val threadsData = preAllocatedDataStructures()
            val coroutinesData = preAllocatedDataStructures()
            val threadPoolData = preAllocatedDataStructures()

            benchmarkThreads(threadsData)
            benchmarkCoroutines(coroutinesData)
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
            System.gc()
            val countDownLatch = CountDownLatch(NUM_TASK_GROUPS * NUM_TASKS_IN_GROUP + 1)
            for (taskGroupNum in 0 until NUM_TASK_GROUPS) {
                coroutineContext.ensureActive()
                benchmarkSingleThreadsGroup(iterationNum, taskGroupNum, threadsData, countDownLatch)
            }
            countDownLatch.countDown()
        }
    }

    private suspend fun benchmarkSingleThreadsGroup(
        iterationNum: Int,
        taskGroupNum: Int,
        threadsData: MutableMap<Int, MutableMap<Int, BackgroundTaskMemoryData>>,
        countDownLatch: CountDownLatch
    ) {
        suspendCoroutine { continuation ->
            val groupCountDownLatch = CountDownLatch(NUM_TASKS_IN_GROUP)
            for (taskInGroup in 0 until NUM_TASKS_IN_GROUP) {
                val thread = Thread {
                    MyLogger.d("Thread started; iteration: $iterationNum; group $taskGroupNum; task $taskInGroup")
                    groupCountDownLatch.countDown() // make sure all threads started
                    groupCountDownLatch.await()
                    if (taskInGroup == NUM_TASKS_IN_GROUP - 1) {
                        val appMemoryConsumption = appMemoryInfoProvider.getAppMemoryConsumption()
                        threadsData[iterationNum]!![taskGroupNum] = BackgroundTaskMemoryData(appMemoryConsumption)
                        continuation.resume(Unit)
                    }
                    countDownLatch.countDown()
                    countDownLatch.await()
                    MyLogger.d("Thread terminates; iteration: $iterationNum; group $taskGroupNum; task $taskInGroup")
                }.apply {
                    start()
                }
            }
        }
    }

    private suspend fun benchmarkCoroutines(coroutinesData: MutableMap<Int, MutableMap<Int, BackgroundTaskMemoryData>>) {
        for (iterationNum in 0 until NUM_ITERATIONS) {
            MyLogger.i("benchmarkCoroutines(); iteration: $iterationNum")
            System.gc()
            val awaitFlow = MutableSharedFlow<Unit>()
            for (taskGroupNum in 0 until NUM_TASK_GROUPS) {
                coroutineContext.ensureActive()
                benchmarkSingleCoroutineGroup(iterationNum, taskGroupNum, coroutinesData, awaitFlow)
            }
            awaitFlow.emit(Unit)
        }
    }

    private suspend fun benchmarkSingleCoroutineGroup(
        iterationNum: Int,
        taskGroupNum: Int,
        coroutinesData: MutableMap<Int, MutableMap<Int, BackgroundTaskMemoryData>>,
        awaitFlow: MutableSharedFlow<Unit>,
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
//        for (i in 0 until NUM_TASKS) {
//            coroutineContext.ensureActive()
//            benchmarkSingleThreadPoolTask(i, threadPoolTimings)
//        }
//        return computeResult(threadPoolTimings)
    }

//    private suspend fun benchmarkSingleThreadPoolTask(i: Int, threadPoolTimings: MutableMap<Int, BackgroundTaskStartupData>) {
//        val startedNano = dateTimeProvider.getNanoTime()
//        val activatedNano = AtomicLong(0)
//        suspendCoroutine { continuation ->
//            threadPool.submit {
//                activatedNano.set(dateTimeProvider.getNanoTime())
//                continuation.resume(Unit)
//            }
//        }
//        val terminatedNano = dateTimeProvider.getNanoTime()
//        threadPoolTimings[i] = BackgroundTaskStartupData(activatedNano.get() - startedNano)
//    }

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
        const val NUM_TASKS_IN_GROUP = 50
        const val NUM_ITERATIONS = 5
    }
}
package com.techyourchance.android.screens.backgroundtasksmemorybenchmark

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.techyourchance.android.R
import com.techyourchance.android.backgroundtasksbenchmark.BackgroundTasksMemoryResult
import com.techyourchance.android.screens.common.toolbar.MyToolbar
import com.techyourchance.android.screens.common.widgets.MyButton
import com.techyourchance.android.backgroundtasksbenchmark.BackgroundTasksStartupResult


class BackgroundTasksMemoryBenchmarkViewMvcImpl(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?
): BackgroundTasksMemoryBenchmarkViewMvc() {

    private val toolbar: MyToolbar
    private val btnToggleBenchmark: MyButton
    private val scatterChart: ScatterChart

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_background_tasks_memory_benchmark, parent, false))

        toolbar = findViewById(R.id.toolbar)
        btnToggleBenchmark = findViewById(R.id.btnToggleBenchmark)
        scatterChart = findViewById(R.id.scatterChart)

        scatterChart.axisRight.isEnabled = false
        scatterChart.axisLeft.axisMinimum = 0f
        scatterChart.description.isEnabled = false
        scatterChart.xAxis.isEnabled = false


        val l: Legend = scatterChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(true)
        l.xOffset = 5f

        toolbar.setNavigateUpListener {
            listeners.map { it.onBackClicked() }
        }

        btnToggleBenchmark.setOnClickListener{
            listeners.map { it.onToggleBenchmarkClicked() }
        }
    }

    override fun bindBenchmarkResults(
        threadsResult: BackgroundTasksMemoryResult,
        coroutinesResult: BackgroundTasksMemoryResult,
        threadPoolResult: BackgroundTasksMemoryResult,
    ) {
        val threadsDataSet = toScatterChartDataSet(
            threadsResult,
            getString(R.string.background_tasks_memory_benchmark_threads_chart_label),
            getColor(R.color.green),
        )
        val coroutinesDataSet = toScatterChartDataSet(
            coroutinesResult,
            getString(R.string.background_tasks_memory_benchmark_coroutines_chart_label),
            getColor(R.color.blue),
        )
        val threadPoolDataSet = toScatterChartDataSet(
            threadPoolResult,
            getString(R.string.background_tasks_memory_benchmark_thread_pool_chart_label),
            getColor(R.color.orange),
        )
        scatterChart.data = ScatterData(
            threadsDataSet,
            coroutinesDataSet,
            threadPoolDataSet
        )
        scatterChart.axisLeft.axisMaximum = scatterChart.data.yMax * 1.1f
        scatterChart.invalidate()
    }

    private fun toScatterChartDataSet(
        result: BackgroundTasksMemoryResult,
        dataSetName: String,
        color: Int,
    ): ScatterDataSet {
        val resultEntries = result.averageAppMemoryConsumption.entries.sortedBy { it.key }
        val chartEntries = resultEntries.map {
            Entry(it.key.toFloat(), it.value.appMemoryConsumption.nativeMemoryKb + it.value.appMemoryConsumption.heapMemoryKb)
        }
        val dataSet = ScatterDataSet(chartEntries, dataSetName)
        dataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE)
        dataSet.color = color
        dataSet.scatterShapeSize = 6f
        dataSet.setDrawValues(false)
        return dataSet
    }

    override fun showBenchmarkStarted() {
        btnToggleBenchmark.text = getString(R.string.background_tasks_startup_benchmark_stop)
        scatterChart.clear()
    }

    override fun showBenchmarkStopped() {
        btnToggleBenchmark.text = getString(R.string.background_tasks_startup_benchmark_start)
    }
}
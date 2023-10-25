package com.techyourchance.android.screens.threadsoverhead

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.techyourchance.android.R
import com.techyourchance.android.screens.common.toolbar.MyToolbar
import com.techyourchance.android.screens.common.widgets.MyButton
import com.techyourchance.android.threadsoverhead.BackgroundTasksStartupResult


class ThreadsOverheadViewMvcImpl(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?
): ThreadsOverheadViewMvc() {

    private val toolbar: MyToolbar
    private val btnToggleBenchmark: MyButton
    private val txtAverageStartupTime: TextView
    private val scatterChart: ScatterChart

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_threads_overhead, parent, false))

        toolbar = findViewById(R.id.toolbar)
        btnToggleBenchmark = findViewById(R.id.btnToggleBenchmark)
        txtAverageStartupTime = findViewById(R.id.txtAverageStartupTime)
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
        threadsResult: BackgroundTasksStartupResult,
        coroutinesResult: BackgroundTasksStartupResult,
    ) {
        val threadsString = getString(
            R.string.background_tasks_startup_benchmark_threads_average,
            threadsResult.averageStartupDurationNano.toFloat() / 1_000,
            threadsResult.stdStartupTimeNano.toFloat() / 1_000,
        )
        val coroutinesString = getString(
            R.string.background_tasks_startup_benchmark_coroutines_average,
            coroutinesResult.averageStartupDurationNano.toFloat() / 1_000,
            coroutinesResult.stdStartupTimeNano.toFloat() / 1_000,
        )
        txtAverageStartupTime.text = threadsString + "\n" + coroutinesString
        bindBenchmarkResultsToScatterChart(threadsResult, coroutinesResult)
    }
    
    private fun bindBenchmarkResultsToScatterChart(
        threadsResult: BackgroundTasksStartupResult,
        coroutinesResult: BackgroundTasksStartupResult,
    ) {
        val threadsDataSet = toScatterChartDataSet(
            threadsResult,
            getString(R.string.background_tasks_startup_benchmark_threads_chart_label),
            getColor(R.color.green),
        )
        val coroutinesDataSet = toScatterChartDataSet(
            coroutinesResult,
            getString(R.string.background_tasks_startup_benchmark_coroutines_chart_label),
            getColor(R.color.blue),
        )
        scatterChart.data = ScatterData(
            threadsDataSet,
            coroutinesDataSet,
        )
        scatterChart.axisLeft.axisMaximum = scatterChart.data.yMax * 1.1f
        scatterChart.invalidate()
    }

    private fun toScatterChartDataSet(
        result: BackgroundTasksStartupResult,
        dataSetName: String,
        color: Int,
    ): ScatterDataSet {
        val resultEntries = result.threadsTimings.entries.sortedBy { it.key }
        val chartEntries = resultEntries.map {
            Entry(it.key.toFloat(), it.value.startupDurationNano.toFloat() / 1_000)
        }
        val dataSet = ScatterDataSet(chartEntries, dataSetName)
        dataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE)
        dataSet.color = color
        dataSet.scatterShapeSize = 6f
        return dataSet
    }

    override fun showBenchmarkStarted() {
        btnToggleBenchmark.text = getString(R.string.background_tasks_startup_benchmark_stop)
        scatterChart.clear()
        txtAverageStartupTime.text = ""
    }

    override fun showBenchmarkStopped() {
        btnToggleBenchmark.text = getString(R.string.background_tasks_startup_benchmark_start)
    }
}
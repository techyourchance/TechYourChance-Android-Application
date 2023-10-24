package com.techyourchance.android.screens.threadsoverhead

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.techyourchance.android.R
import com.techyourchance.android.screens.common.toolbar.MyToolbar
import com.techyourchance.android.screens.common.widgets.MyButton
import com.techyourchance.android.threadsoverhead.ThreadsStartupResult


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

        toolbar.setNavigateUpListener {
            listeners.map { it.onBackClicked() }
        }

        btnToggleBenchmark.setOnClickListener{
            listeners.map { it.onToggleBenchmarkClicked() }
        }
    }

    override fun bindBenchmarkResults(result: ThreadsStartupResult) {
        val averageDurationUs = result.averageStartupDurationNano.toFloat() / 1_000
        val stdDurationUs = result.stdStartupTimeNano.toFloat() / 1_000
        txtAverageStartupTime.text = getString(R.string.threads_overhead_average_startup_duration, averageDurationUs, stdDurationUs)
        bindBenchmarkResultsToScatterChart(result)
    }

    private fun bindBenchmarkResultsToScatterChart(result: ThreadsStartupResult) {
        scatterChart.data = toScatterChartData(result)
        scatterChart.axisLeft.axisMaximum = scatterChart.data.yMax * 1.1f
        scatterChart.invalidate()
    }

    private fun toScatterChartData(result: ThreadsStartupResult): ScatterData {
        val resultEntries = result.threadsTimings.entries.sortedBy { it.key }
        val chartEntries = resultEntries.map {
            Entry(it.key.toFloat(), it.value.startupDurationNano.toFloat() / 1_000)
        }
        val dataSet = ScatterDataSet(chartEntries, getString(R.string.threads_overhead_chart_label))
        dataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE)
        dataSet.color = ColorTemplate.COLORFUL_COLORS[0]
        dataSet.scatterShapeSize = 6f
        return ScatterData(dataSet)
    }

    override fun showBenchmarkStarted() {
        btnToggleBenchmark.text = getString(R.string.threads_overhead_stop_benchmark)
    }

    override fun showBenchmarkStopped() {
        btnToggleBenchmark.text = getString(R.string.threads_overhead_start_benchmark)
    }
}
package com.techyourchance.android.screens.benchmarks.sharedprefs

import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.techyourchance.android.R
import com.techyourchance.android.benchmarks.sharedprefsedit.SharedPrefsEditResult
import com.techyourchance.android.screens.common.toolbar.MyToolbar
import com.techyourchance.android.screens.common.widgets.MyButton


class SharedPrefsBenchmarkViewMvcImpl(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?
): SharedPrefsBenchmarkViewMvc() {

    private val toolbar: MyToolbar
    private val btnToggleBenchmark: MyButton
    private val scatterChart: ScatterChart

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_shared_prefs_edit_benchmark, parent, false))

        toolbar = findViewById(R.id.toolbar)
        btnToggleBenchmark = findViewById(R.id.btnToggleBenchmark)
        scatterChart = findViewById(R.id.scatterChart)

        scatterChart.axisRight.isEnabled = false
        scatterChart.axisLeft.axisMinimum = 0f
        scatterChart.description.isEnabled = false
        scatterChart.xAxis.isEnabled = true
        scatterChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        val l: Legend = scatterChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(true)
        l.xOffset = 5f
        l.yOffset = 20f

        toolbar.setNavigateUpListener {
            listeners.map { it.onBackClicked() }
        }

        btnToggleBenchmark.setOnClickListener{
            listeners.map { it.onToggleBenchmarkClicked() }
        }
    }

    override fun bindBenchmarkResults(
        resultWithCommit: SharedPrefsEditResult,
        resultWithApply: SharedPrefsEditResult,
    ) {
        val commitDataSet = toScatterChartDataSet(
            resultWithCommit,
            getString(R.string.shared_prefs_edit_benchmark_commit_chart_label),
            getColor(R.color.green),
        )
        val applyDataSet = toScatterChartDataSet(
            resultWithApply,
            getString(R.string.shared_prefs_edit_benchmark_apply_chart_label),
            getColor(R.color.blue),
        )
        scatterChart.data = ScatterData(
            commitDataSet,
            applyDataSet,
        )
        val chartYRange = scatterChart.data.yMax - scatterChart.data.yMin
        scatterChart.axisLeft.axisMaximum = scatterChart.data.yMax + 0.2f * chartYRange
        scatterChart.axisLeft.axisMinimum = scatterChart.data.yMin - 0.2f * chartYRange
        scatterChart.invalidate()
    }

    private fun toScatterChartDataSet(
        result: SharedPrefsEditResult,
        dataSetName: String,
        color: Int,
    ): ScatterDataSet {
        val resultEntries = result.entryIndexToAverageEditDurationsNano.entries.sortedBy { it.key }
        val chartEntries = resultEntries.map {
            Entry(
                it.key.toFloat(),
                it.value.toFloat() / 1_000_000, // convert to ms
            )
        }
        val dataSet = ScatterDataSet(chartEntries, dataSetName)
        dataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE)
        dataSet.color = color
        dataSet.scatterShapeSize = 10f
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
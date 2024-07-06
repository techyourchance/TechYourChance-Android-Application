package com.techyourchance.android.screens.benchmarks.sharedprefs

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.techyourchance.android.R
import com.techyourchance.android.benchmarks.shared_prefs.SharedPrefsWriteResult
import com.techyourchance.android.screens.common.toolbar.MyToolbar
import com.techyourchance.android.screens.common.widgets.MyButton


class SharedPrefsBenchmarkViewMvcImpl(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?
): SharedPrefsBenchmarkViewMvc() {

    private val toolbar: MyToolbar
    private val btnToggleBenchmark: MyButton
    private val scatterChart: ScatterChart
    private val txtResult: TextView

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_shared_prefs_edit_benchmark, parent, false))

        toolbar = findViewById(R.id.toolbar)
        btnToggleBenchmark = findViewById(R.id.btnToggleBenchmark)
        scatterChart = findViewById(R.id.scatterChart)
        txtResult = findViewById(R.id.txtResults)

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
        prefValueLength: Int,
        resultWithCommit: SharedPrefsWriteResult,
        resultWithApply: SharedPrefsWriteResult,
    ) {
        bindChartResults(resultWithCommit, resultWithApply)
        bindOtherResults(prefValueLength, resultWithCommit, resultWithApply)
    }

    private fun bindChartResults(
        resultWithCommit: SharedPrefsWriteResult,
        resultWithApply: SharedPrefsWriteResult,
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

    private fun bindOtherResults(
        prefValueLength: Int,
        resultWithCommit: SharedPrefsWriteResult,
        resultWithApply: SharedPrefsWriteResult
    ) {
        val sb = StringBuilder()
        sb.appendLine("Shared prefs value length (chars): $prefValueLength").appendLine()
        val commitConstantOverheadMs = resultWithCommit.linearFitCoefficients.intercept / 1_000_000
        sb.appendLine("Committed write constant time : ${String.format("%.2f", commitConstantOverheadMs)} [ms]").appendLine()
        val commitIncrementPerEntry = resultWithCommit.linearFitCoefficients.slope
        sb.appendLine("Commit time increment per extra entry: $commitIncrementPerEntry [ns]").appendLine()
        val commitMaxDuration = resultWithCommit.maxEditDurationNano.toDouble() / 1_000_000
        sb.appendLine("Commit max duration: ${String.format("%.2f", commitMaxDuration)} [ms]").appendLine()
        val applyConstantOverheadMs = resultWithApply.linearFitCoefficients.intercept / 1_000_000
        sb.appendLine("Applied write constant time : ${String.format("%.2f", applyConstantOverheadMs)} [ms]").appendLine()
        val applyIncrementPerEntry = resultWithApply.linearFitCoefficients.slope
        sb.appendLine("Apply time increment per extra entry: $applyIncrementPerEntry [ns]").appendLine()
        val applyMaxDuration = resultWithApply.maxEditDurationNano.toDouble() / 1_000_000
        sb.appendLine("Apply max duration: ${String.format("%.2f", applyMaxDuration)} [ms]").appendLine()
        txtResult.text = sb.toString()
    }

    private fun toScatterChartDataSet(
        result: SharedPrefsWriteResult,
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
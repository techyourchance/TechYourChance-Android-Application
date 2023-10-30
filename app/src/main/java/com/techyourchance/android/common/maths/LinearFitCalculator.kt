package com.techyourchance.android.common.maths

import javax.inject.Inject

class LinearFitCalculator @Inject constructor() {

    fun calculateLinearFit(input: List<Pair<Double, Double>>): LinearFitCoefficients {
        val n = input.size
        var sumX = 0.0
        var sumY = 0.0
        var sumXY = 0.0
        var sumXX = 0.0

        for (i in 0 until n) {
            sumX += input[i].first
            sumY += input[i].second
            sumXY += input[i].first * input[i].second
            sumXX += input[i].first * input[i].first
        }

        var slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX)
        if (slope.isNaN()) {
            slope = 0.0
        }
        val intercept = (sumY - slope * sumX) / n

        return LinearFitCoefficients(slope, intercept)
    }
}
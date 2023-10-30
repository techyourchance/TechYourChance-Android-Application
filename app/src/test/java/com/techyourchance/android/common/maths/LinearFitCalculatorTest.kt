package com.techyourchance.android.common.maths

import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.junit.Before
import org.junit.Test

class LinearFitCalculatorTest {

    // region constants ----------------------------------------------------------------------------
    private val DOUBLE_COMPARISON_TOLERANCE = 0.000001
    // endregion constants -------------------------------------------------------------------------

    // region helper fields ------------------------------------------------------------------------
    // endregion helper fields ---------------------------------------------------------------------

    private lateinit var SUT: LinearFitCalculator

    @Before
    fun setup() {
        SUT = LinearFitCalculator()
    }

    @Test
    fun zeros_input_zeros_output() {
        // Arrange
        val input = (0..3).map { x -> Pair(x.toDouble(), 0.0) }
        // Act
        val result = SUT.calculateLinearFit(input)
        // Assert
        result.shouldBe(LinearFitCoefficients(0.0, 0.0))
    }

    @Test
    fun ones_input_correct_output() {
        // Arrange
        val input = (0..3).map { x -> Pair(x.toDouble(), 1.0) }
        // Act
        val result = SUT.calculateLinearFit(input)
        // Assert
        result.shouldBe(LinearFitCoefficients(0.0, 1.0))
    }

    @Test
    fun line_with_slope_input_correct_output() {
        // Arrange
        val input = listOf(-1.5, 2.0, 4.1, 6.8).map { x -> Pair(x, -5.3 * x + 4.12) }
        // Act
        val result = SUT.calculateLinearFit(input)
        // Assert
        result.slope shouldBe (-5.3 plusOrMinus DOUBLE_COMPARISON_TOLERANCE)
        result.intercept shouldBe (4.12 plusOrMinus DOUBLE_COMPARISON_TOLERANCE)
    }

    // region helper methods -----------------------------------------------------------------------
    // endregion helper methods --------------------------------------------------------------------

    // region helper classes -----------------------------------------------------------------------
    // endregion helper classes --------------------------------------------------------------------

}
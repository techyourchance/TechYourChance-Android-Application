package com.techyourchance.android.screens.animations.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.ColorUtils
import kotlin.math.min

class VisitCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val cardColor: Int = Color.BLUE
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply { isAntiAlias = true }
    private val rectF = RectF()
    private val blendColor = ColorUtils.blendARGB(Color.GRAY, cardColor, 0.3f)

    private var circleRadius: Float = 0f
    private var circleMargin: Float = 0f
    private var rectangleDistance: Float = 0f
    private var textRectangleHeight: Float = 0f
    private var topTextRectangleWidth: Float = 0f
    private var bottomTextRectangleWidth: Float = 0f
    private var circleX: Float = 0f
    private var circleY: Float = 0f
    private var rectangleX: Float = 0f
    private var rectangleCenterY: Float = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Perform calculations when size is changed
        val minDim = min(w, h).toFloat()
        circleRadius = minDim * 0.13f
        circleMargin = circleRadius * 0.7f
        rectangleDistance = circleMargin * 0.5f
        textRectangleHeight = circleRadius * 0.4f
        topTextRectangleWidth = width * 0.15f
        bottomTextRectangleWidth = topTextRectangleWidth * 0.7f
        circleX = circleRadius + circleMargin
        circleY = height - circleMargin - circleRadius
        rectangleX = circleX + circleRadius + rectangleDistance
        rectangleCenterY = circleY
    }

    override fun onDraw(canvas: Canvas) {
        // Draw the card with rounded corners
        paint.color = cardColor
        rectF.set(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(rectF, circleRadius, circleRadius, paint)

        // Draw the circle symbolizing a photo
        paint.color = blendColor
        canvas.drawCircle(circleX, circleY, circleRadius, paint)

        // Draw the two rectangles symbolizing text

        // Top rectangle
        rectF.set(rectangleX, rectangleCenterY - textRectangleHeight - rectangleDistance / 2, rectangleX + topTextRectangleWidth, rectangleCenterY - rectangleDistance / 2)
        canvas.drawRoundRect(rectF, circleRadius, circleRadius, paint)

        // Bottom rectangle
        rectF.set(rectangleX, rectangleCenterY + rectangleDistance / 2, rectangleX + bottomTextRectangleWidth, rectangleCenterY + textRectangleHeight + rectangleDistance / 2)
        canvas.drawRoundRect(rectF, circleRadius, circleRadius, paint)
    }
}
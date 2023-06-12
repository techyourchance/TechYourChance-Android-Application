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

class CardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val cardColor: Int = Color.BLUE
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply { isAntiAlias = true }
    private val blendColor = ColorUtils.blendARGB(Color.GRAY, cardColor, 0.3f)

    private val cardRect = RectF()
    private val textDummyTopRect = RectF()
    private val textDummyBottomRect = RectF()

    private var circleRadius: Float = 0f
    private var circleMargin: Float = 0f
    private var circleCenterX: Float = 0f
    private var circleCenterY: Float = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        cardRect.set(0f, 0f, width.toFloat(), height.toFloat())

        val minDim = min(w, h).toFloat()
        circleRadius = minDim * 0.13f
        circleMargin = circleRadius * 0.7f
        circleCenterX = circleRadius + circleMargin
        circleCenterY = height - circleMargin - circleRadius

        val textDummyToCircleDist = circleMargin * 0.5f
        val textDummyX = circleCenterX + circleRadius + textDummyToCircleDist
        val textDummyHeight = circleRadius * 0.4f
        val textDummyTopWidth = width * 0.15f

        textDummyTopRect.set(
            textDummyX,
            circleCenterY - textDummyHeight - textDummyToCircleDist / 2,
            textDummyX + textDummyTopWidth,
            circleCenterY - textDummyToCircleDist / 2
        )
        textDummyBottomRect.set(
            textDummyX,
            circleCenterY + textDummyToCircleDist / 2,
            textDummyX + textDummyTopWidth * 0.7f,
            circleCenterY + textDummyHeight + textDummyToCircleDist / 2
        )
    }

    override fun onDraw(canvas: Canvas) {
        // Draw the card with rounded corners
        paint.color = cardColor
        canvas.drawRoundRect(cardRect, circleRadius, circleRadius, paint)

        paint.color = blendColor // Use this color for next elements

        // Photo dummy
        canvas.drawCircle(circleCenterX, circleCenterY, circleRadius, paint)

        // Top text dummy
        canvas.drawRoundRect(textDummyTopRect, circleRadius, circleRadius, paint)

        // Bottom text dummy
        canvas.drawRoundRect(textDummyBottomRect, circleRadius, circleRadius, paint)
    }
}
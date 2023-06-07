package com.techyourchance.android.screens.animations.widgets

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.FrameLayout

class StackedCardsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private var numCards: Int = 3
) : FrameLayout(context, attrs, defStyleAttr) {


    private var cardShift: Float = 0f // Initialize to zero, will be calculated in onSizeChanged

    private val colors = arrayOf(
        Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.YELLOW,
        Color.MAGENTA
    ) // Extend this as needed

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cardShift = width * 0.05f // Shift is 5% of the width
        post { updateCards() } // postpone until after layout is complete
    }

    fun setNumberOfCards(num: Int) {
        numCards = num
        updateCards()
    }

    private fun updateCards() {
        removeAllViews()

        val topCardWidth = width * 0.9f
        val topCardHeight = topCardWidth * 0.6f
        val topCardBottomMargin = (height * 0.02f).toInt() // Bottom margin is 2% of the view's height
        val topCardLeftMargin = ((width - topCardWidth) / 2).toInt() // Center horizontally
        val topCardTranslationX = topCardLeftMargin
        val topCardTranslationY = this@StackedCardsView.height - topCardHeight.toInt() - topCardBottomMargin

        for (i in 0 until numCards) {
            val numCard = (numCards - 1 - i)
            val cardColor = colors[i % colors.size] // Cycle through colors if there are more cards than colors
            val scaleFactor = 1 - 0.1f * numCard

            val cardView = VisitCardView(context, cardColor = cardColor)
            addView(cardView)

            cardView.apply {
                layoutParams = LayoutParams(topCardWidth.toInt(), topCardHeight.toInt())
                setCardScaleFactor(scaleFactor) // decrease size for each subsequent card
                // Calculate the position
                translationY = topCardTranslationY - numCard * cardShift
                translationX = topCardTranslationX.toFloat()
            }
        }
    }

}

package com.techyourchance.android.screens.animations.widgets

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.animation.doOnEnd
import com.techyourchance.android.common.logs.MyLogger
import kotlin.math.sqrt

class StackedCardsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val numCards = NUM_CARDS_DEFAULT
    private val cards = mutableListOf<MyCard>()

    private var cardShift: Float = 0f

    private var velocityTracker: VelocityTracker? = null
    private var topCardWidth: Float = 0f
    private var topCardHeight: Float = 0f
    private var topCardTranslationX: Float = 0f
    private var topCardTranslationY: Float = 0f

    private val colors = arrayOf(
        Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.YELLOW,
        Color.MAGENTA
    ) // Extend this as needed

    fun setNumberOfCards(numCards: Int) {
        initCards()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        topCardWidth = width * 0.9f
        topCardHeight = topCardWidth * 0.6f
        cardShift = topCardHeight * 0.15f
        val topCardBottomMargin = height * 0.02f // Bottom margin is 2% of the view's height
        val topCardLeftMargin = (width - topCardWidth) / 2 // Center horizontally
        topCardTranslationX = topCardLeftMargin
        topCardTranslationY = this@StackedCardsView.height - topCardHeight.toInt() - topCardBottomMargin

        post { initCards() } // postpone until after layout is complete
    }

    private fun initCards() {
        cards.clear()
        for (i in 0 until numCards) {
            val cardColor = colors[i % colors.size]
            val cardView = CardView(context, cardColor = cardColor)
            cards.add(MyCard(i, cardView))
        }
        updateCards(shouldAnimate = false)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun updateCards(shouldAnimate: Boolean) {
        removeAllViews()
        
        for (i in 0 until numCards) {
            val card = cards.first { it.stackIndex == i }

            card.view.layoutParams = LayoutParams(topCardWidth.toInt(), topCardHeight.toInt())

            card.view.setOnTouchListener { _, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        velocityTracker?.recycle()
                        velocityTracker = VelocityTracker.obtain().also {
                            it.addMovement(event)
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        velocityTracker?.apply {
                            addMovement(event)
                            computeCurrentVelocity(VELOCITY_COMPUTATION_UNIT)
                        }
                    }
                }
                card.handleMotionEvent(event)
                true
            }

            card.update(shouldAnimate)
        }

        // First card should be added last to be on top
        cards.sortedBy { numCards - it.stackIndex }.map {
            addView(it.view)
        }
    }

    private fun transferCardToBack(transferredCard: MyCard) {
        val transferredCardIndex = transferredCard.stackIndex
        cards.forEach { card ->
            when {
                card.stackIndex < transferredCardIndex -> {
                    // no-op
                }
                card.stackIndex == transferredCardIndex -> {
                    card.stackIndex = numCards - 1
                }
                else -> {
                    card.stackIndex = card.stackIndex - 1
                }
            }
        }
    }

    private fun getCardTranslationXForIndex(cardIndex: Int): Float {
        return topCardTranslationX
    }

    private fun getCardTranslationYForIndex(cardIndex: Int): Float {
        return topCardTranslationY - cardIndex * cardShift
    }

    private fun getCardScaleForIndex(numCard: Int): Float {
        return 1 - 0.1f * numCard
    }

    companion object {
        private const val NUM_CARDS_DEFAULT = 4
        private const val SNAP_ANIMATION_DURATION_MS = 500L
        private const val VELOCITY_COMPUTATION_UNIT = 100
        private const val VELOCITY_THRESHOLD = 100f
    }

    private enum class MyCardState {
        SETTLED, DRAGGED, SNAPPING_BACK, THROWN, POSITION_SHIFT,
    }

    private inner class MyCard(var stackIndex: Int, var view: CardView) {

        var state = MyCardState.SETTLED

        private var firstDrag = false
        private var lastActionDownX: Float = 0f
        private var lastActionDownY: Float = 0f

        fun handleMotionEvent(event: MotionEvent) {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    handleActionDownEvent(event)
                }
                MotionEvent.ACTION_MOVE -> {
                    handleActionMoveEvent(event)
                }
                MotionEvent.ACTION_UP -> {
                    handleActionUpEvent(event)
                    // animateCardBackToPosition(card, cardTranslationXForIndex, cardTranslationYForIndex)
                }
            }
        }

        private fun handleActionDownEvent(event: MotionEvent) {
            if (state != MyCardState.SETTLED) {
                return // card isn't interactive until settled
            }
            firstDrag = true
            lastActionDownX = event.rawX
            lastActionDownY = event.rawY
            state = MyCardState.DRAGGED
        }

        private fun handleActionUpEvent(event: MotionEvent) {
            if (state != MyCardState.DRAGGED) {
                throw RuntimeException("received up event when not dragged")
            }

            var xVelocity = 0f
            var yVelocity = 0f

            velocityTracker?.let { vt ->
                xVelocity = vt.xVelocity
                yVelocity = vt.yVelocity
            } ?: return

            val speed = sqrt(xVelocity * xVelocity + yVelocity * yVelocity)

            state = if (speed > VELOCITY_THRESHOLD) {
                transferCardToBack(this)
                MyCardState.THROWN
            } else {
                MyCardState.SNAPPING_BACK
            }

            updateCards(shouldAnimate = true)
        }

        private fun handleActionMoveEvent(event: MotionEvent) {
            if (state != MyCardState.DRAGGED) {
                throw RuntimeException("received move event when not dragged")
            }

            val dX = event.rawX - lastActionDownX
            val dY = event.rawY - lastActionDownY

            view.translationX = getCardTranslationXForIndex(stackIndex) + dX
            view.translationY = getCardTranslationYForIndex(stackIndex) + dY

            // Calculate the distance from the center of the card
            val centerX = view.width / 2
            val touchXRelativeToCenter = centerX - event.x
            val maxRotation = 5f // Max rotation in degrees

            // Calculate the rotation degree
            val rotation = maxRotation * (touchXRelativeToCenter / centerX)

            // Animate rotation on first touch
            if (dX != 0f && dY != 0f && firstDrag) {
                view.animate()
                    .rotation(rotation)
                    .setDuration(SNAP_ANIMATION_DURATION_MS / 4)
                    .start()
                firstDrag = false
            }
        }

        fun animationEnded() {
            state = MyCardState.SETTLED
        }

        fun update(shouldAnimate: Boolean) {
            val cardScaleFactorForIndex = getCardScaleForIndex(stackIndex)
            val cardTranslationYForIndex = getCardTranslationYForIndex(stackIndex)
            val cardTranslationXForIndex = getCardTranslationXForIndex(stackIndex)

            view.apply {
                if (shouldAnimate) {
                    val startScaleX = scaleX
                    val startScaleY = scaleY
                    val startTranslationX = translationX
                    val startTranslationY = translationY
                    val startRotation = rotation
                    val animator = ValueAnimator.ofFloat(0f, 1f).also {
                        it.duration = SNAP_ANIMATION_DURATION_MS
                    }
                    val interpolator = DecelerateInterpolator()

                    animator.addUpdateListener {
                        var xVelocity = 0f
                        var yVelocity = 0f

                        velocityTracker?.let { vt ->
                            xVelocity = vt.xVelocity
                            yVelocity = vt.yVelocity
                        } ?: return@addUpdateListener

                        val fraction = it.animatedValue as Float
                        val flingEndX = startTranslationX + xVelocity
                        val flingEndY = startTranslationY + yVelocity

                        scaleX = startScaleX + (cardScaleFactorForIndex - startScaleX) * fraction
                        scaleY = startScaleY + (cardScaleFactorForIndex - startScaleY) * fraction
                        translationX = startTranslationX + (cardTranslationXForIndex - startTranslationX) * fraction
                        translationY = startTranslationY + (cardTranslationYForIndex - startTranslationY) * fraction
                        rotation = startRotation * (1 - fraction)

                        if (state == MyCardState.THROWN) {
                            val interpolation = interpolator.getInterpolation(fraction)
                            MyLogger.i("interpolation: $interpolation")
                            translationX += xVelocity * (1 - interpolation)
                            translationY += yVelocity * (1 - interpolation)
                        }
                    }
                    animator.doOnEnd {
                        animationEnded()
                    }
                    animator.start()
                } else {
                    scaleX = cardScaleFactorForIndex
                    scaleY = cardScaleFactorForIndex
                    translationY = cardTranslationYForIndex
                    translationX = cardTranslationXForIndex
                    rotation = 0f
                }
            }
        }
    }

}

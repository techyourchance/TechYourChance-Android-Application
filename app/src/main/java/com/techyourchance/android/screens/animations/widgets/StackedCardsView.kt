package com.techyourchance.android.screens.animations.widgets

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.animation.doOnEnd
import com.techyourchance.android.common.logs.MyLogger
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

class StackedCardsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val numOfCardsInStack = NUM_CARDS_DEFAULT
    private val cards = mutableListOf<MyCard>()
    private var touchTargetCard: MyCard? = null

    private var velocityTracker: VelocityTracker? = null
    private var topCardWidth: Float = 0f
    private var topCardHeight: Float = 0f
    private var topCardTranslationX: Float = 0f
    private var topCardTranslationY: Float = 0f
    private var cardShift: Float = 0f

    private val colors = linkedMapOf(
        "red" to Color.RED,
        "green" to Color.GREEN,
        "blue" to Color.BLUE,
        "yellow" to Color.YELLOW,
        "magenta" to Color.MAGENTA
    )

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
        removeAllViews()
        for (i in 0 until numOfCardsInStack) {
            val card = initCard(i)
            cards.add(card)
            addView(card.view)
        }
        updateAllCards()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initCard(i: Int): MyCard {
        val cardColorName = colors.keys.toList()[i % colors.keys.size]
        val cardColor = colors.values.toList()[i % colors.size]
        val cardView = CardView(context, cardColor = cardColor)
        val card = MyCard(i, cardColorName, cardView)
        cardView.layoutParams = LayoutParams(topCardWidth.toInt(), topCardHeight.toInt())
        return card
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
            touchTargetCard = null
            // Iterate over all cards to find the target for the touch event (if any)
            for (i in numOfCardsInStack - 1 downTo 0) {
                val card = cards[i]
                val cardView = card.view
                if (isPointInView(ev.x, ev.y, cardView)) {
                    // This card could potentially handle the touch event,
                    // but we'll check the rest of the cards to see if
                    // there's a card with a higher Z position that also
                    // contains the point
                    if (touchTargetCard == null || cardView.translationZ >= touchTargetCard!!.view.translationZ) {
                        touchTargetCard = card
                    }
                }
            }
        }
        return touchTargetCard != null
    }

    private fun isPointInView(x: Float, y: Float, view: View): Boolean {
        val point = floatArrayOf(x, y)
        val inverseMatrix = Matrix()
        view.matrix.invert(inverseMatrix)
        inverseMatrix.mapPoints(point)
        val rectF = RectF(view.left.toFloat(), view.top.toFloat(), view.right.toFloat(), view.bottom.toFloat())
        return rectF.contains(point[0], point[1])
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
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
                    computeCurrentVelocity(VELOCITY_COMPUTATION_UNIT, MAX_VELOCITY)
                }
            }
        }
        // Delegate the touch event to the selected card
        return touchTargetCard?.let {
            it.view.dispatchTouchEvent(event)
            it.handleMotionEvent(event)
            true
        } ?: false
    }

    private fun updateAllCards() {
        for (i in numOfCardsInStack - 1 downTo  0) {
            val card = cards.first { it.stackIndex == i }
            card.view.translationZ = numOfCardsInStack - 1f - i
        }
    }

    private fun transferCardToStackIndex(transferredCard: MyCard, newTransferredStackIndex: Int) {
        val previousTransferredStackIndex = transferredCard.stackIndex

        // we assume transfer always from lower to higher
        val affectedIndices = previousTransferredStackIndex + 1 .. newTransferredStackIndex

        cards.forEach { card ->
            val newStackIndex = when(card.stackIndex) {
                in affectedIndices -> card.stackIndex - 1
                previousTransferredStackIndex -> newTransferredStackIndex
                else -> card.stackIndex
            }
            card.transferToPositionInStack(newStackIndex)
        }
        updateAllCards()
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

    private fun isCardAboveTheStack(card: MyCard): Boolean {
        val stackTopY = topCardTranslationY - (numOfCardsInStack - 1) * cardShift
        return isViewAboveY(stackTopY, card.view)
    }

    private fun isViewAboveY(y: Float, view: View): Boolean {
        val viewRect = RectF(0f, 0f, view.width.toFloat(), view.height.toFloat())
        val transformedViewRect = RectF()
        view.matrix.mapRect(transformedViewRect, viewRect)
        return transformedViewRect.bottom <= y
    }

    companion object {
        private const val NUM_CARDS_DEFAULT = 4

        private const val VELOCITY_COMPUTATION_UNIT = 1000 // pixels per second
        private const val MAX_VELOCITY = 500f // pixels per second
        private const val VELOCITY_THRESHOLD = 300f // pixels per second

        private const val DRAG_ROTATION_ANIMATION_DURATION_MS = 50L
        private const val THROW_ANIMATION_DURATION_MS = 1000L
        private const val SNAP_ANIMATION_DURATION_MS = 250L
        private const val POSITION_SHIFT_ANIMATION_DURATION_MS = 150L
        private const val POSITION_SHIFT_ANIMATION_DELAY_AFTER_THROW_MS = 200L

    }

    private enum class MyCardState {
        SETTLED, DRAGGED, ANIMATE_SNAP_BACK, ANIMATE_THROW, ANIMATE_POSITION_SHIFT,
    }

    private inner class MyCard(var stackIndex: Int, val colorName: String, val view: CardView) {

        var state = MyCardState.SETTLED
        private var stateAnimator: ValueAnimator? = null
        private var expectedStackIndexAtAnimationEnd = stackIndex
        private var stateAnimationInProgress = false

        private var firstDrag = false
        private var lastActionDownRawX: Float = 0f // screen coordinates
        private var lastActionDownRawY: Float = 0f // screen coordinates
        private var lastMotionEventX: Float = 0f // card view coordinates

        init {
            toState(MyCardState.SETTLED)
        }


        private fun cancelStateAnimation() {
            if (stateAnimationInProgress) {
                MyLogger.v(getTag(), "cancelStateTransition(); cancelling state transition during $state")
                stateAnimator?.cancel()
                stateAnimationInProgress = false
            }
        }

        fun handleMotionEvent(event: MotionEvent) {
            lastMotionEventX = event.x
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    handleActionDownEvent(event)
                }
                MotionEvent.ACTION_MOVE -> {
                    handleActionMoveEvent(event)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    handleActionUpAndCancelEvent(event)
                }
            }
        }

        private fun handleActionDownEvent(event: MotionEvent) {
            if (state !in arrayOf(MyCardState.SETTLED, MyCardState.ANIMATE_POSITION_SHIFT)) {
                MyLogger.v(getTag(), "handleActionDownEvent(); received down event when $state - ignoring")
                return
            }
            firstDrag = true
            lastActionDownRawX = event.rawX
            lastActionDownRawY = event.rawY
            toState(MyCardState.DRAGGED)
        }

        private fun handleActionUpAndCancelEvent(event: MotionEvent) {
            if (state != MyCardState.DRAGGED) {
                MyLogger.v(getTag(), "handleActionUpEvent(); received up/cancel event when $state - ignoring")
                return
            }

            var xVelocity = 0f
            var yVelocity = 0f

            velocityTracker?.let { vt ->
                xVelocity = vt.xVelocity
                yVelocity = vt.yVelocity
            } ?: return

            val speed = sqrt(xVelocity * xVelocity + yVelocity * yVelocity)

            val nextState = if (speed > VELOCITY_THRESHOLD && isCardAboveTheStack(this)) {
                MyCardState.ANIMATE_THROW
            } else {
                MyCardState.ANIMATE_SNAP_BACK
            }

            toState(nextState)
        }

        private fun handleActionMoveEvent(event: MotionEvent) {
            if (state != MyCardState.DRAGGED) {
                MyLogger.v(getTag(),"handleActionMoveEvent(); received move event when not dragged - ignoring")
                return
            }
            val dX = event.rawX - lastActionDownRawX
            val dY = event.rawY - lastActionDownRawY
            view.translationX = getCardTranslationXForIndex(stackIndex) + dX
            view.translationY = getCardTranslationYForIndex(stackIndex) + dY
        }

        fun transferToPositionInStack(newStackIndex: Int) {
            if (newStackIndex == stackIndex) {
                return
            }
            MyLogger.v(getTag(),"transferToPositionInStack(); $stackIndex -> $newStackIndex")
            stackIndex = newStackIndex
            // if the position is changed when the card is not settled, then we assume that
            // it will be handled after the current transition completes
            if (state == MyCardState.SETTLED) {
                toState(MyCardState.ANIMATE_POSITION_SHIFT)
            }
        }

        private fun toState(nextState: MyCardState) {
            MyLogger.i(getTag(), "toState(); current state: $state; next state: $nextState")

            var isValidTransition = true

            when (state) {
                MyCardState.SETTLED -> {
                    when (nextState) {
                        MyCardState.SETTLED -> {
                            setToIndexPosition()
                        }
                        MyCardState.DRAGGED -> {
                            animateDragRotation()
                        }
                        MyCardState.ANIMATE_POSITION_SHIFT -> {
                            animateToDefaultPosition(POSITION_SHIFT_ANIMATION_DURATION_MS)
                        }
                        else -> isValidTransition = false
                    }
                }
                MyCardState.DRAGGED -> {
                    when (nextState) {
                        MyCardState.ANIMATE_SNAP_BACK -> {
                            animateToDefaultPosition(SNAP_ANIMATION_DURATION_MS)
                        }
                        MyCardState.ANIMATE_THROW -> {
                            animateThrowAndTransferToBack()
                        }
                        else -> isValidTransition = false
                    }
                }
                MyCardState.ANIMATE_SNAP_BACK -> {
                    when (nextState) {
                        MyCardState.SETTLED -> {
                            // no-op
                        }
                        MyCardState.ANIMATE_POSITION_SHIFT -> {
                            animateToDefaultPosition(POSITION_SHIFT_ANIMATION_DURATION_MS)
                        }
                        else -> isValidTransition = false
                    }
                }
                MyCardState.ANIMATE_THROW -> {
                    when (nextState) {
                        MyCardState.SETTLED -> {
                            // no-op
                        }
                        MyCardState.ANIMATE_POSITION_SHIFT -> {
                            animateToDefaultPosition(POSITION_SHIFT_ANIMATION_DURATION_MS)
                        }
                        else -> isValidTransition = false
                    }
                }
                MyCardState.ANIMATE_POSITION_SHIFT -> {
                    when (nextState) {
                        MyCardState.SETTLED -> {
                            // no-op
                        }
                        MyCardState.DRAGGED -> {
                            cancelStateAnimation()
                        }
                        MyCardState.ANIMATE_POSITION_SHIFT -> {
                            // there can be multiple back to back position shifts
                            animateToDefaultPosition(POSITION_SHIFT_ANIMATION_DURATION_MS)
                        }
                        else -> isValidTransition = false
                    }
                }
            }

            if (!isValidTransition) {
                throw IllegalStateException("invalid transition from $state to $nextState")
            }

            state = nextState
        }

        private fun setToIndexPosition() {
            val cardScaleFactorForIndex = getCardScaleForIndex(stackIndex)
            val cardTranslationYForIndex = getCardTranslationYForIndex(stackIndex)
            val cardTranslationXForIndex = getCardTranslationXForIndex(stackIndex)
            view.apply {
                scaleX = cardScaleFactorForIndex
                scaleY = cardScaleFactorForIndex
                translationY = cardTranslationYForIndex
                translationX = cardTranslationXForIndex
                rotation = 0f
            }
        }

        private fun animateDragRotation() {
            stateAnimationInProgress = true

            val maxRotation = 10f
            val targetRotation = maxRotation * getLastTouchHorizontalOffsetFraction()

            // Animate rotation on first touch
            if (firstDrag) {
                view.apply {
                    val startRotation = rotation
                    stateAnimator = ValueAnimator.ofFloat(0f, 1f).also { animator ->
                        animator.duration = DRAG_ROTATION_ANIMATION_DURATION_MS
                        animator.addUpdateListener {
                            val fraction = it.animatedValue as Float
                            rotation = startRotation + (targetRotation - startRotation) * fraction
                        }
                        animator.doOnEnd {
                            stateAnimationInProgress = false
                            updateAllCards()
                        }
                        animator.start()
                    }
                }
                firstDrag = false
            }
        }

        private fun animateToDefaultPosition(duration: Long) {
            MyLogger.v(getTag(), "animateToDefaultPosition()")

            stateAnimationInProgress = true

            val cardScaleFactorForIndex = getCardScaleForIndex(stackIndex)
            val cardTranslationYForIndex = getCardTranslationYForIndex(stackIndex)
            val cardTranslationXForIndex = getCardTranslationXForIndex(stackIndex)

            view.apply {
                val startScaleX = scaleX
                val startScaleY = scaleY
                val startTranslationX = translationX
                val startTranslationY = translationY
                val startRotation = rotation
                stateAnimator = ValueAnimator.ofFloat(0f, 1f).also { animator ->
                    animator.duration = duration
                    animator.addUpdateListener {
                        val fraction = it.animatedValue as Float
                        scaleX = startScaleX + (cardScaleFactorForIndex - startScaleX) * fraction
                        scaleY = startScaleY + (cardScaleFactorForIndex - startScaleY) * fraction
                        translationX = startTranslationX + (cardTranslationXForIndex - startTranslationX) * fraction
                        translationY = startTranslationY + (cardTranslationYForIndex - startTranslationY) * fraction
                        rotation = startRotation * (1 - fraction)
                    }
                    animator.doOnEnd {
                        stateAnimationInProgress = false
                        // there is a chance that right before this animation completes the
                        // card becomes dragged and the cancellation doesn't reach this animator
                        if (state != MyCardState.DRAGGED) {
                            if (stackIndex == expectedStackIndexAtAnimationEnd) {
                                toState(MyCardState.SETTLED)
                            } else {
                                toState(MyCardState.ANIMATE_POSITION_SHIFT)
                            }
                        }
                        updateAllCards()
                    }
                    expectedStackIndexAtAnimationEnd = stackIndex
                    animator.start()
                }
            }
        }

        private fun animateThrowAndTransferToBack() {
            stateAnimationInProgress = true
            view.isEnabled = false

            val stackIndexBack = numOfCardsInStack - 1
            val cardScaleFactorForIndex = getCardScaleForIndex(stackIndexBack)
            val cardTranslationYForIndex = getCardTranslationYForIndex(stackIndexBack)
            val cardTranslationXForIndex = getCardTranslationXForIndex(stackIndexBack)

            view.apply {
                val startScaleX = scaleX
                val startScaleY = scaleY
                val startTranslationX = translationX
                val startTranslationY = translationY
                val startRotation = rotation
                val startStackIndex = stackIndex

                var xVelocity = 0f
                var yVelocity = 0f
                velocityTracker?.let { vt ->
                    xVelocity = vt.xVelocity
                    yVelocity = vt.yVelocity
                } ?: return

                val throwInterpolator = ThrowInterpolator()

                stateAnimator = ValueAnimator.ofFloat(0f, 1f).also { animator ->
                    animator.duration = THROW_ANIMATION_DURATION_MS
                    animator.interpolator = LinearInterpolator()
                    animator.addUpdateListener {

                        val fraction = it.animatedValue as Float

                        scaleX = startScaleX + (cardScaleFactorForIndex - startScaleX) * fraction
                        scaleY = startScaleY + (cardScaleFactorForIndex - startScaleY) * fraction

                        val fractionForTranslation = throwInterpolator.getInterpolation(fraction)
                        translationX = startTranslationX + (cardTranslationXForIndex - startTranslationX) * fraction + xVelocity * fractionForTranslation
                        translationY = startTranslationY + (cardTranslationYForIndex - startTranslationY) * fraction + yVelocity * fractionForTranslation

                        val rotationDirection = if(getLastTouchHorizontalOffsetFraction() > 0) {
                            -1f
                        } else {
                            1f
                        }
                        val totalRotationDegrees = (360 * 2) * rotationDirection - startRotation
                        rotation = startRotation + totalRotationDegrees * fraction

                        val newStackIndex = ceil(startStackIndex + (expectedStackIndexAtAnimationEnd - startStackIndex) * fraction).toInt()
                        if (newStackIndex != stackIndex) {
                            transferCardToStackIndex(this@MyCard, newStackIndex)
                        }
                    }
                    animator.doOnEnd {
                        rotation = 0f
                        stateAnimationInProgress = false
                        isEnabled = true
                        if (stackIndex == expectedStackIndexAtAnimationEnd) {
                            toState(MyCardState.SETTLED)
                        } else {
                            toState(MyCardState.ANIMATE_POSITION_SHIFT)
                        }
                        updateAllCards()
                    }
                    expectedStackIndexAtAnimationEnd = numOfCardsInStack - 1
                    animator.start()
                }
            }
        }

        private fun getLastTouchHorizontalOffsetFraction(): Float {
            // Calculate the distance from the center of the card
            val centerX = view.width / 2
            val touchXRelativeToCenter = centerX - lastMotionEventX
            return touchXRelativeToCenter / centerX
        }

        private fun getTag(): String {
            return "MyCard($colorName)($stackIndex)"
        }
    }

    private class ThrowInterpolator(): android.view.animation.Interpolator {
        private val accelerateInterpolator = AccelerateInterpolator()
        private val decelerateInterpolator = DecelerateInterpolator()
        private val linearInterpolator = LinearInterpolator()
        override fun getInterpolation(input: Float): Float {
            return if (input <= 0.5f) {
                linearInterpolator.getInterpolation(input * 2)
            } else {
                linearInterpolator.getInterpolation(1 - (input - 0.5f) * 2)
            }
        }
    }
}

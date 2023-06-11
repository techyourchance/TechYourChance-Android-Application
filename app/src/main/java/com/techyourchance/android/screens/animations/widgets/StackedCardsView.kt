package com.techyourchance.android.screens.animations.widgets

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.animation.doOnEnd
import com.techyourchance.android.common.logs.MyLogger
import kotlin.math.ceil
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

    fun setNumberOfCards(numCards: Int) {
        initCards()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        topCardWidth = width * CARD_WIDTH_TO_TOTAL_WIDTH_RATIO
        topCardHeight = topCardWidth * CARD_WIDTH_TO_HEIGHT_RATIO
        cardShift = topCardHeight * CARD_SHIFT_TO_HEIGHT_RATIO
        val topCardBottomMargin = height * TOP_CARD_BOTTOM_MARGIN_TO_HEIGHT_RATIO
        val topCardLeftMargin = (width - topCardWidth) / 2 // Center horizontally
        topCardTranslationX = topCardLeftMargin
        topCardTranslationY = height - topCardHeight - topCardBottomMargin
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

    private fun initCard(i: Int): MyCard {
        val colors = CARD_COLORS
        val cardColorName = colors.keys.toList()[i % colors.keys.size]
        val cardColor = colors.values.toList()[i % colors.size]
        val cardView = CardView(context, cardColor = cardColor)
        val card = MyCard(i, cardColorName, cardView)
        cardView.layoutParams = LayoutParams(topCardWidth.toInt(), topCardHeight.toInt())
        return card
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // We need this method because changing the Z translation of a View in Android doesn't
        // affect how the system routes touch events, so we need to account for Z translation
        // to find the target for the event ourselves
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

    private fun isCardAboveTheThrowThreshold(card: MyCard): Boolean {
        val thresholdY = topCardTranslationY - (numOfCardsInStack - 1) * cardShift + card.view.height * 0.75f
        return isViewAboveY(thresholdY, card.view)
    }

    private fun isViewAboveY(y: Float, view: View): Boolean {
        val viewRect = RectF(0f, 0f, view.width.toFloat(), view.height.toFloat())
        val transformedViewRect = RectF()
        view.matrix.mapRect(transformedViewRect, viewRect)
        return transformedViewRect.bottom <= y
    }

    companion object {
        private const val NUM_CARDS_DEFAULT = 4
        private const val CARD_SHIFT_TO_HEIGHT_RATIO = 0.2f
        private const val TOP_CARD_BOTTOM_MARGIN_TO_HEIGHT_RATIO = 0.1f
        private const val CARD_WIDTH_TO_TOTAL_WIDTH_RATIO = 0.7f
        private const val CARD_WIDTH_TO_HEIGHT_RATIO = 0.6f

        private val CARD_COLORS = linkedMapOf(
            "red" to Color.RED,
            "green" to Color.GREEN,
            "blue" to Color.BLUE,
            "yellow" to Color.YELLOW,
            "magenta" to Color.MAGENTA
        )

        private const val VELOCITY_COMPUTATION_UNIT = 1000 // pixels per second
        private const val MAX_VELOCITY = 800f // pixels per second
        private const val VELOCITY_THRESHOLD = 300f // pixels per second

        private const val THROW_ANIMATION_NUM_OF_ROTATIONS = 1
        private const val DRAG_ANIMATION_ROTATION_MAX_DEGREE = 10f

        private const val DRAG_ROTATION_ANIMATION_DURATION_MS = 50L
        private const val THROW_ANIMATION_DURATION_MS = 800L
        private const val SNAP_ANIMATION_DURATION_MS = THROW_ANIMATION_DURATION_MS / 2
        private const val POSITION_SHIFT_ANIMATION_DURATION_MS = THROW_ANIMATION_DURATION_MS / 5
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
        private var lastActionDownEventX: Float = 0f // card view coordinates

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
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    lastActionDownEventX = event.x
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

            val nextState = if (speed > VELOCITY_THRESHOLD && isCardAboveTheThrowThreshold(this)) {
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
            view.apply {
                scaleX = getCardScaleForIndex(stackIndex)
                scaleY = getCardScaleForIndex(stackIndex)
                translationY = getCardTranslationYForIndex(stackIndex)
                translationX = getCardTranslationXForIndex(stackIndex)
                rotation = 0f
            }
        }

        private fun animateDragRotation() {
            stateAnimationInProgress = true
            val targetRotation = DRAG_ANIMATION_ROTATION_MAX_DEGREE * getLastTouchHorizontalOffsetFraction()
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
            val bc = getBoundaryConditions()
            view.apply {
                stateAnimator = ValueAnimator.ofFloat(0f, 1f).also { animator ->
                    animator.duration = duration
                    animator.addUpdateListener {
                        val fraction = it.animatedValue as Float
                        scaleX = bc.startScaleX + (cardScaleFactorForIndex - bc.startScaleX) * fraction
                        scaleY = bc.startScaleY + (cardScaleFactorForIndex - bc.startScaleY) * fraction
                        translationX = bc.startTranslationX + (cardTranslationXForIndex - bc.startTranslationX) * fraction
                        translationY = bc.startTranslationY + (cardTranslationYForIndex - bc.startTranslationY) * fraction
                        rotation = bc.startRotation * (1 - fraction)
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
            val stackIndexBack = numOfCardsInStack - 1
            val cardScaleFactorForIndex = getCardScaleForIndex(stackIndexBack)
            val cardTranslationYForIndex = getCardTranslationYForIndex(stackIndexBack)
            val cardTranslationXForIndex = getCardTranslationXForIndex(stackIndexBack)
            val bc = getBoundaryConditions()
            view.apply {
                val throwInterpolator = ThrowInterpolator()

                stateAnimator = ValueAnimator.ofFloat(0f, 1f).also { animator ->
                    animator.duration = THROW_ANIMATION_DURATION_MS
                    animator.interpolator = LinearInterpolator()
                    animator.addUpdateListener {

                        val fraction = it.animatedValue as Float

                        scaleX = bc.startScaleX + (cardScaleFactorForIndex - bc.startScaleX) * fraction
                        scaleY = bc.startScaleY + (cardScaleFactorForIndex - bc.startScaleY) * fraction

                        val fractionForTranslation = throwInterpolator.getInterpolation(fraction)
                        translationX = bc.startTranslationX + (cardTranslationXForIndex - bc.startTranslationX) * fraction + bc.xVelocity * fractionForTranslation
                        translationY = bc.startTranslationY + (cardTranslationYForIndex - bc.startTranslationY) * fraction + bc.yVelocity * fractionForTranslation

                        val rotationDirection = if(getLastTouchHorizontalOffsetFraction() > 0) {
                            1f
                        } else {
                            -1f
                        }
                        val totalRotationDegrees = 360 * THROW_ANIMATION_NUM_OF_ROTATIONS * rotationDirection
                        rotation = bc.startRotation + (totalRotationDegrees - bc.startRotation) * fraction

                        val newStackIndex = ceil(bc.startStackIndex + (expectedStackIndexAtAnimationEnd - bc.startStackIndex) * fraction).toInt()
                        if (newStackIndex != stackIndex) {
                            transferCardToStackIndex(this@MyCard, newStackIndex)
                        }
                    }
                    animator.doOnEnd {
                        rotation = 0f // reset if we end up with multiples of 360
                        stateAnimationInProgress = false
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
            val centerX = view.width / 2
            val touchXRelativeToCenter = centerX - lastActionDownEventX
            return touchXRelativeToCenter / centerX
        }

        private fun getBoundaryConditions(): BoundaryConditions {
            return with(view) {
                BoundaryConditions(
                    scaleX,
                    scaleY,
                    translationX,
                    translationY,
                    rotation,
                    velocityTracker!!.xVelocity,
                    velocityTracker!!.yVelocity,
                    stackIndex,
                )
            }
        }

        private fun getTag(): String {
            return "MyCard($colorName)($stackIndex)"
        }
    }
    
    private data class BoundaryConditions(
        val startScaleX: Float,
        val startScaleY: Float,
        val startTranslationX: Float,
        val startTranslationY: Float,
        val startRotation: Float,
        val xVelocity: Float,
        val yVelocity: Float,
        val startStackIndex: Int,
    )

    private class ThrowInterpolator(): android.view.animation.Interpolator {
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

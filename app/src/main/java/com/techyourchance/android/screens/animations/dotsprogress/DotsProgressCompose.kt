package com.techyourchance.android.screens.animations.dotsprogress

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.KeyframesSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.times
import com.techyourchance.android.screens.common.composables.pxToDp


@Preview
@Composable
fun DotsProgressCompose(
    modifier: Modifier = Modifier,
    color: Color = Color.Gray,
) {

    val durationMs = 1000

    val infiniteTransition = rememberInfiniteTransition("progress")

    val dot1OffsetY by animateDot(
        infiniteTransition,
        keyframes {
            durationMillis = durationMs
            0.5f at 0 with LinearEasing
            0f at (0.2 * durationMs).toInt() with LinearEasing
            1f at (0.4 * durationMs).toInt() with LinearEasing
            0.5f at (0.6 * durationMs).toInt() with LinearEasing
            0.5f at (0.8 * durationMs).toInt() with LinearEasing
            0.5f at (1f * durationMs).toInt() with LinearEasing
        }
    )

    val dot2OffsetY by animateDot(
        infiniteTransition,
        keyframes {
            durationMillis = durationMs
            0.5f at 0 with LinearEasing
            0.5f at (0.2 * durationMs).toInt() with LinearEasing
            0f at (0.4 * durationMs).toInt() with LinearEasing
            1f at (0.6 * durationMs).toInt() with LinearEasing
            0.5f at (0.8 * durationMs).toInt() with LinearEasing
            0.5f at (1f * durationMs).toInt() with LinearEasing
        }
    )

    val dot3OffsetY by animateDot(
        infiniteTransition,
        keyframes {
            durationMillis = durationMs
            0.5f at 0 with LinearEasing
            0.5f at (0.2 * durationMs).toInt() with LinearEasing
            0.5f at (0.4 * durationMs).toInt() with LinearEasing
            0f at (0.6 * durationMs).toInt() with LinearEasing
            1f at (0.8 * durationMs).toInt() with LinearEasing
            0.5f at (1f * durationMs).toInt() with LinearEasing
        }
    )

    val rowSize = remember { mutableStateOf(IntSize.Zero) }
    val rowHeight = rowSize.value.height.pxToDp()
    Row(
        modifier = modifier
            .aspectRatio(3f)
            .background(Color.Transparent)
            .onGloballyPositioned { coordinates ->
                rowSize.value = coordinates.size
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Dot(offsetY = dot1OffsetY * rowHeight, size = rowHeight * 0.5f, color = color)
        Dot(offsetY = dot2OffsetY * rowHeight, size = rowHeight * 0.5f, color = color)
        Dot(offsetY = dot3OffsetY * rowHeight, size = rowHeight * 0.5f, color = color)
    }
}

@Composable
fun animateDot(infiniteTransition: InfiniteTransition, keyframesSpec: KeyframesSpec<Float>): State<Float> {
    return infiniteTransition.animateFloat(
        label = "",
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframesSpec,
            repeatMode = RepeatMode.Restart
        )
    )
}

@Composable
fun Dot(offsetY: Dp, size: Dp, color: Color) {
    Box(
        modifier = Modifier
            .offset(y = offsetY)
            .background(color, shape = CircleShape)
            .size(size)
    )
}
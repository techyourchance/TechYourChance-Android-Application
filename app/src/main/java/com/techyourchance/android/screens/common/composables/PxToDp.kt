package com.techyourchance.android.screens.common.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Int.pxToDp(): Dp {
    val density = LocalDensity.current
    return with(density) { this@pxToDp.toDp() }
}
package com.techyourchance.android.screens.common.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp


@Composable
fun getDimen(id: Int): Dp {
    return LocalContext.current.resources.getDimension(id).pxToDp()
}
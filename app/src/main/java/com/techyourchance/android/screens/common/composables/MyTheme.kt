package com.techyourchance.android.screens.common.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable

@Composable
fun MyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MyColorScheme,
        typography = androidx.compose.material3.Typography(),
        shapes = Shapes(),
        content = content
    )
}
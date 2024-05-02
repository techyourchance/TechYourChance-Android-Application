package com.techyourchance.android.screens.composeui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomTab(var route: String, val icon: ImageVector?, var title: String) {
    data object Home : BottomTab("home/{num}", Icons.Rounded.Home, "Home")
    data object Settings : BottomTab("settings/{num}", Icons.Rounded.Settings, "Settings")
}
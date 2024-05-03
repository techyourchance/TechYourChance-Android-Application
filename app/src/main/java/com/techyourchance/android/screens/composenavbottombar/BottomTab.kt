package com.techyourchance.android.screens.composenavbottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomTab(val title: String, val icon: ImageVector?, val rootRoute: Route) {
    data object Home : BottomTab("Home", Icons.Rounded.Home, Route.HomeRoot)
    data object Settings : BottomTab("Settings", Icons.Rounded.Settings, Route.SettingsRoot)
}
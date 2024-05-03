package com.techyourchance.android.screens.composeui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Route(var route: String, var title: String) {
    data object HomeRoot : Route("home", "Home root screen")
    data object HomeChild : Route("home/{num}", "Home child screen")
    data object SettingsRoot : Route("settings", "Settings root screen")
    data object SettingsChild : Route("settings/{num}", "Settings child screen")
}
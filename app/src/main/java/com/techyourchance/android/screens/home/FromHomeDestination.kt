package com.techyourchance.android.screens.home

import com.techyourchance.android.screens.common.ScreenSpec

data class FromHomeDestination(
    val title: String,
    val screenSpec: ScreenSpec,
    val destinationType: FromHomeDestinationType
)

enum class FromHomeDestinationType {
    SINGLE_SCREEN, GROUP_OF_SCREENS, LIST_OF_SCREENS
}

package com.techyourchance.template.screens.common

import java.io.Serializable

@Suppress("ClassName")
sealed class ScreenSpec(val activityName: ActivityName): Serializable {
    object Home: ScreenSpec(ActivityName.MAIN)
    object StackOverflowQuestionsList: ScreenSpec(ActivityName.MAIN)
    data class StackOverflowQuestionDetails(val questionId: String): ScreenSpec(ActivityName.MAIN)

}
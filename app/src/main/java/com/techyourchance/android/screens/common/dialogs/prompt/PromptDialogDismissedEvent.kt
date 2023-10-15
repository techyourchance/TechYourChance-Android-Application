package com.techyourchance.android.screens.common.dialogs.prompt

import com.techyourchance.android.screens.common.dialogs.BaseDialogEvent
import java.io.Serializable

enum class PromptDialogButton {
    POSITIVE, NEGATIVE
}

data class PromptDialogDismissedEvent(
    val id: String?,
    val clickedButton: PromptDialogButton,
    val payload: Serializable?
) : BaseDialogEvent(id)
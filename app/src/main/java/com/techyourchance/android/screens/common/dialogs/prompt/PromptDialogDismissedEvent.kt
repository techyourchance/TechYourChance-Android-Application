package com.techyourchance.android.screens.common.dialogs.prompt

import com.techyourchance.android.screens.common.dialogs.BaseDialogEvent

enum class PromptDialogButton {
    POSITIVE, NEGATIVE
}

data class PromptDialogDismissedEvent(
    val id: String?,
    val clickedButton: PromptDialogButton
) : BaseDialogEvent(id)
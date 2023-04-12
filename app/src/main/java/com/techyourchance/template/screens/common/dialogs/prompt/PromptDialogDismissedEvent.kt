package com.techyourchance.template.screens.common.dialogs.prompt

import com.techyourchance.template.screens.common.dialogs.BaseDialogEvent

enum class PromptDialogButton {
    POSITIVE, NEGATIVE
}

data class PromptDialogDismissedEvent(
    val id: String?,
    val clickedButton: PromptDialogButton
) : BaseDialogEvent(id)
package com.techyourchance.template.screens.common.dialogs.info

import com.techyourchance.template.screens.common.dialogs.BaseDialogEvent

data class InfoDialogDismissedEvent(
    val id: String?,
) : BaseDialogEvent(id)
package com.techyourchance.android.screens.common.dialogs.info

import com.techyourchance.android.screens.common.dialogs.BaseDialogEvent

data class InfoDialogDismissedEvent(
    val id: String?,
) : BaseDialogEvent(id)
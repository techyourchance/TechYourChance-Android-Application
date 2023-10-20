package com.techyourchance.android.screens.common.dialogs.progress

import com.techyourchance.android.screens.common.dialogs.BaseDialogEvent


data class ProgressDialogDismissedEvent(val id: String?, val isCancelled: Boolean = false) : BaseDialogEvent(id)
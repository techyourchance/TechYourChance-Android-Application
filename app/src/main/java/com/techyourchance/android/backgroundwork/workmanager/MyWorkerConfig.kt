package com.techyourchance.android.backgroundwork.workmanager

data class MyWorkerConfig(
    val isExpedited: Boolean,
    val maxRetries: Int,
    val backoffSeconds: Int,
)

package com.techyourchance.android.backgroundwork.workmanager

data class MyWorkerConfig(
    val isNetworkConstrained: Boolean,
    val isExpedited: Boolean,
    val maxRetries: Int,
    val backoffSeconds: Int,
) {
    companion object {
        val NULL_CONFIG = MyWorkerConfig(
            isNetworkConstrained = false,
            isExpedited = false,
            maxRetries = -1,
            backoffSeconds = -1
        )
    }
}

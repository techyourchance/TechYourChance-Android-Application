package com.techyourchance.android.common.eventbus

import org.greenrobot.eventbus.EventBus

class EventBusPoster(private val eventBus: EventBus) {
    fun post(event: Any) {
        eventBus.post(event)
    }

    fun postSticky(event: Any) {
        eventBus.postSticky(event)
    }
}
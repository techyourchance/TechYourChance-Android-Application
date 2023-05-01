package com.techyourchance.android.common.eventbus

import org.greenrobot.eventbus.EventBus

class EventBusSubscriber(private val eventBus: EventBus) {

    fun register(listener: Any) {
        eventBus.register(listener)
    }

    fun unregister(listener: Any) {
        eventBus.unregister(listener)
    }
}
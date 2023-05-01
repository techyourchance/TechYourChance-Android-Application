package com.techyourchance.android.screens.common.mvcviews

import java.util.*

abstract class BaseObservableViewMvc<LISTENER_CLASS> : BaseViewMvc(), ObservableViewMvc<LISTENER_CLASS> {

    private val _listeners: MutableSet<LISTENER_CLASS> = HashSet()

    protected val listeners: Set<LISTENER_CLASS> get() = Collections.unmodifiableSet(_listeners)

    override fun registerListener(listener: LISTENER_CLASS) {
        _listeners.add(listener)
    }

    override fun unregisterListener(listener: LISTENER_CLASS) {
        _listeners.remove(listener)
    }

}
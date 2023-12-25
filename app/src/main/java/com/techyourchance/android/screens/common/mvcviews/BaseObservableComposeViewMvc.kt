package com.techyourchance.android.screens.common.mvcviews

import android.view.View
import java.util.Collections

abstract class BaseObservableComposeViewMvc<LISTENER_CLASS>: ObservableViewMvc<LISTENER_CLASS> {

    private val _listeners: MutableSet<LISTENER_CLASS> = HashSet()

    protected val listeners: Set<LISTENER_CLASS> get() = Collections.unmodifiableSet(_listeners)

    override fun registerListener(listener: LISTENER_CLASS) {
        _listeners.add(listener)
    }

    override fun unregisterListener(listener: LISTENER_CLASS) {
        _listeners.remove(listener)
    }

    private lateinit var rootView: View

    protected fun setRootView(view: View) {
        rootView = view
    }

    override fun getRootView(): View {
        return rootView
    }

}
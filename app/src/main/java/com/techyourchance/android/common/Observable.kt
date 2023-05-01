package com.techyourchance.android.common

import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

abstract class Observable<LISTENER_CLASS> {

    private val lock = ReentrantLock()

    val listeners: Set<LISTENER_CLASS> get() {
        lock.withLock {
            return Collections.unmodifiableSet<LISTENER_CLASS>(java.util.HashSet(_listeners))
        }
    }
    private val _listeners: MutableSet<LISTENER_CLASS> = HashSet()


    fun registerListener(listener: LISTENER_CLASS) {
        lock.withLock {
            val hadNoListeners = _listeners.isEmpty()
            _listeners.add(listener)
            if (hadNoListeners && _listeners.size == 1) {
                onFirstListenerRegistered()
            }
        }
    }

    fun unregisterListener(listener: LISTENER_CLASS) {
        lock.withLock {
            val hadOneListener = _listeners.size == 1
            _listeners.remove(listener)
            if (hadOneListener && _listeners.isEmpty()) {
                onLastListenerUnregistered()
            }
        }
    }

    protected open fun onFirstListenerRegistered() {}

    protected open fun onLastListenerUnregistered() {}
}
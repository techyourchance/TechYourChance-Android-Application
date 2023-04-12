package com.techyourchance.template.common

import java.util.concurrent.atomic.AtomicBoolean

abstract class BusyObservable<LISTENER_CLASS> : Observable<LISTENER_CLASS>() {

    private val isBusy = AtomicBoolean(false)

    fun isBusy(): Boolean {
        return isBusy.get()
    }

    /**
     * Atomically assert not busy and become busy
     * @throws IllegalStateException if wasn't busy when this method was called
     */
    protected fun assertNotBusyAndBecomeBusy() {
        check(isBusy.compareAndSet(false, true)) { "assertion violation: mustn't be busy" }
    }

    /**
     * Atomically check whether not busy and become busy
     * @return true if was "free" when this method was called; false if was busy
     */
    protected fun isFreeAndBecomeBusy(): Boolean {
        return isBusy.compareAndSet(false, true)
    }

    /**
     * Unconditionally become not busy
     */
    protected fun becomeNotBusy() {
        isBusy.set(false)
    }
}
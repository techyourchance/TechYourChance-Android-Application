package com.techyourchance.android.common.logs

import timber.log.Timber

object MyLogger {

    @JvmStatic
    fun v(message: String) {
        Timber.d(message)
    }

    @JvmStatic
    fun v(tag: String, message: String) {
        Timber.tag(tag).d(message)
    }

    @JvmStatic
    fun d(message: String) {
        Timber.d(message)
    }

    @JvmStatic
    fun d(tag: String, message: String) {
        Timber.tag(tag).d(message)
    }

    @JvmStatic
    fun i(message: String) {
        Timber.i(message)
    }

    @JvmStatic
    fun i(tag: String, message: String) {
        Timber.tag(tag).i(message)
    }

    @JvmStatic
    fun e(message: String) {
        Timber.e(message)
    }

    @JvmStatic
    fun e(message: String, t: Throwable?) {
        Timber.e(t, message)
    }

    @JvmStatic
    fun e(tag: String, message: String, t: Throwable?) {
        Timber.tag(tag).e(t, message)
    }

    @JvmStatic
    fun e(tag: String, message: String) {
        Timber.tag(tag).e(message)
    }

}
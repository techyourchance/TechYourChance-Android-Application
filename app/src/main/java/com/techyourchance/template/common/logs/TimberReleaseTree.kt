package com.techyourchance.template.common.logs

import timber.log.Timber
import javax.inject.Inject

class TimberReleaseTree @Inject constructor() : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // implement this method according to logging requirements in release builds
    }
}
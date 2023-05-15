package com.techyourchance.android.common.service

import android.app.Service
import com.techyourchance.android.MyApplication
import com.techyourchance.android.common.dependencyinjection.service.ServiceComponent
import com.techyourchance.android.common.dependencyinjection.service.ServiceModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class BaseService: Service() {

    protected val coroutineScope = CoroutineScope(Dispatchers.Main.immediate);

    protected val serviceComponent: ServiceComponent by lazy {
        (application as MyApplication)
            .applicationComponent
            .newServiceComponent(ServiceModule(this))
    }
}
package com.techyourchance.template.screens.common.activities

import androidx.appcompat.app.AppCompatActivity
import com.techyourchance.template.MyApplication
import com.techyourchance.template.common.dependencyinjection.activity.ActivityComponent
import com.techyourchance.template.common.dependencyinjection.activity.ActivityModule
import com.techyourchance.template.common.dependencyinjection.controller.ControllerComponent
import com.techyourchance.template.common.dependencyinjection.controller.ControllerModule
import com.techyourchance.template.common.dependencyinjection.controller.ViewMvcModule
import com.techyourchance.template.screens.common.ActivityName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class BaseActivity: AppCompatActivity() {

    protected val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    val activityComponent: ActivityComponent by lazy {
        (application as MyApplication)
            .applicationComponent
            .newActivityComponent(ActivityModule(this))
    }

    private var isFirstAccessToControllerComponent = true

    protected val controllerComponent: ControllerComponent
        get() {
            check(isFirstAccessToControllerComponent) { "must not use ControllerComponent more than once" }
            isFirstAccessToControllerComponent = false
            return activityComponent
                .newControllerComponent(ControllerModule(this, this), ViewMvcModule())
        }

    abstract fun getActivityName(): ActivityName
}
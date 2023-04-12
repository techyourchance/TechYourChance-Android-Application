package com.techyourchance.template.screens.common.fragments

import androidx.fragment.app.Fragment
import com.techyourchance.template.common.dependencyinjection.controller.ControllerComponent
import com.techyourchance.template.common.dependencyinjection.controller.ControllerModule
import com.techyourchance.template.common.dependencyinjection.controller.ViewMvcModule
import com.techyourchance.template.screens.common.activities.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class BaseFragment: Fragment() {

    protected val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    private var isFirstAccessToControllerComponent = true

    protected val controllerComponent: ControllerComponent
        get() {
            check(isFirstAccessToControllerComponent) { "must not use ControllerComponent more than once" }
            isFirstAccessToControllerComponent = false
            return (activity as BaseActivity)
                .activityComponent
                .newControllerComponent(ControllerModule(this, this), ViewMvcModule())
        }
}
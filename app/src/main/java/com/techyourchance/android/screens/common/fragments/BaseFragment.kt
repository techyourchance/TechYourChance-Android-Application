package com.techyourchance.android.screens.common.fragments

import androidx.fragment.app.Fragment
import com.techyourchance.android.common.dependencyinjection.controller.ControllerComponent
import com.techyourchance.android.common.dependencyinjection.controller.ControllerModule
import com.techyourchance.android.common.dependencyinjection.controller.ViewMvcModule
import com.techyourchance.android.screens.common.activities.BaseViewsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class BaseFragment: Fragment() {

    protected val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    private var isFirstAccessToControllerComponent = true

    protected val controllerComponent: ControllerComponent
        get() {
            check(isFirstAccessToControllerComponent) { "must not use ControllerComponent more than once" }
            isFirstAccessToControllerComponent = false
            return (activity as BaseViewsActivity)
                .activityComponent
                .newControllerComponent(ControllerModule(this, this), ViewMvcModule())
        }
}
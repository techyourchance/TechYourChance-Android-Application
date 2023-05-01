package com.techyourchance.android.common.dependencyinjection.activity

import com.techyourchance.android.common.dependencyinjection.controller.ControllerComponent
import com.techyourchance.android.common.dependencyinjection.controller.ControllerModule
import com.techyourchance.android.common.dependencyinjection.controller.ViewMvcModule
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {
    
    fun newControllerComponent(controllerModule: ControllerModule, viewMvcModule: ViewMvcModule): ControllerComponent
}
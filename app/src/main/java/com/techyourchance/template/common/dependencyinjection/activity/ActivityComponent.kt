package com.techyourchance.template.common.dependencyinjection.activity

import com.techyourchance.template.common.dependencyinjection.controller.ControllerComponent
import com.techyourchance.template.common.dependencyinjection.controller.ControllerModule
import com.techyourchance.template.common.dependencyinjection.controller.ViewMvcModule
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {
    
    fun newControllerComponent(controllerModule: ControllerModule, viewMvcModule: ViewMvcModule): ControllerComponent
}
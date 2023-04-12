package com.techyourchance.template.common.dependencyinjection.service

import dagger.Subcomponent

@Subcomponent(modules = [ServiceModule::class])
interface ServiceComponent {
}
package com.techyourchance.template.common.dependencyinjection.application

import com.techyourchance.template.MyApplication
import com.techyourchance.template.common.dependencyinjection.activity.ActivityComponent
import com.techyourchance.template.common.dependencyinjection.activity.ActivityModule
import com.techyourchance.template.common.dependencyinjection.service.ServiceComponent
import com.techyourchance.template.common.dependencyinjection.service.ServiceModule
import dagger.Component

@ApplicationScope
@Component(
    modules = [
        ApplicationModule::class,
        SettingsModule::class,
    ]
)
interface ApplicationComponent {
    fun newActivityComponent(activityModule: ActivityModule): ActivityComponent
    fun newServiceComponent(serviceModule: ServiceModule): ServiceComponent
    fun inject(myApplication: MyApplication)
}
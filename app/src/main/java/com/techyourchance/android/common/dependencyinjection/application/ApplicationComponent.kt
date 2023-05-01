package com.techyourchance.android.common.dependencyinjection.application

import com.techyourchance.android.MyApplication
import com.techyourchance.android.common.dependencyinjection.activity.ActivityComponent
import com.techyourchance.android.common.dependencyinjection.activity.ActivityModule
import com.techyourchance.android.common.dependencyinjection.service.ServiceComponent
import com.techyourchance.android.common.dependencyinjection.service.ServiceModule
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
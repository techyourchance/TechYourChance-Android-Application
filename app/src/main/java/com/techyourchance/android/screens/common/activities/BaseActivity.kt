package com.techyourchance.android.screens.common.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.techyourchance.android.BuildConfig
import com.techyourchance.android.MyApplication
import com.techyourchance.android.R
import com.techyourchance.android.common.dependencyinjection.activity.ActivityComponent
import com.techyourchance.android.common.dependencyinjection.activity.ActivityModule
import com.techyourchance.android.common.dependencyinjection.controller.ControllerComponent
import com.techyourchance.android.common.dependencyinjection.controller.ControllerModule
import com.techyourchance.android.common.dependencyinjection.controller.ViewMvcModule
import com.techyourchance.android.common.logs.MyLogger
import com.techyourchance.android.common.permissions.PermissionsHelperDelegate
import com.techyourchance.android.screens.common.ActivityName
import com.techyourchance.android.screens.common.ScreenSpec
import com.techyourchance.android.screens.common.ScreensNavigator
import com.techyourchance.android.screens.common.dialogs.DialogsNavigator
import com.techyourchance.android.screens.debugdrawer.DebugDrawerFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

abstract class BaseActivity: AppCompatActivity() {
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
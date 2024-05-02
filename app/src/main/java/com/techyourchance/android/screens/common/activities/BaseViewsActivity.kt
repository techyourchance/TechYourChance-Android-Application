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

abstract class BaseViewsActivity: BaseActivity() {

    @Inject lateinit var screensNavigator: ScreensNavigator
    @Inject lateinit var dialogsNavigator: DialogsNavigator
    @Inject lateinit var permissionsHelperDelegate: PermissionsHelperDelegate

    private val defaultOnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (dialogsNavigator.getCurrentlyShownDialog() != null) {
                return // let dialogs handle the back presses when shown
            }
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
                return
            }
            screensNavigator.navigateBack()
        }
    }

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_base_activity)

        drawerLayout = findViewById(R.id.drawerLayout)

        addDebugDrawerIfNeeded()

        screensNavigator.init(savedInstanceState)

        if (savedInstanceState == null) {
            var startingScreen = intent.getSerializableExtra(ScreenSpec.INTENT_EXTRA_SCREEN_SPEC) as ScreenSpec?
            if (startingScreen == null) {
                startingScreen = ScreenSpec.Home
            }
            screensNavigator.toScreen(startingScreen)
        }

        onBackPressedDispatcher.addCallback(defaultOnBackPressedCallback)
    }

    private fun addDebugDrawerIfNeeded() {
        if (BuildConfig.DEBUG) {
            supportFragmentManager.beginTransaction().add(
                R.id.fragmentContainerViewDebugDrawer,
                DebugDrawerFragment.newInstance()
            ).commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        defaultOnBackPressedCallback.remove()
    }

    override fun onNewIntent(intent: Intent) {
        MyLogger.i("onNewIntent()")
        super.onNewIntent(intent)
        val startingScreen = intent.getSerializableExtra(ScreenSpec.INTENT_EXTRA_SCREEN_SPEC) as ScreenSpec?
        if (startingScreen != null) {
            screensNavigator.toScreen(startingScreen)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        screensNavigator.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // must delegate to PermissionsHelper because this object functions as a central "hub"
        // for permissions management in the scope of this Activity
        permissionsHelperDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
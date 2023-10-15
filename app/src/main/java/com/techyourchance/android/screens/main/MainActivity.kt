package com.techyourchance.android.screens.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.techyourchance.android.BuildConfig
import com.techyourchance.android.R
import com.techyourchance.android.apkupdate.ApkInfo
import com.techyourchance.android.apkupdate.FetchLatestApkInfoUseCase
import com.techyourchance.android.apkupdate.UpdateApkUseCase
import com.techyourchance.android.common.eventbus.EventBusSubscriber
import com.techyourchance.android.common.logs.MyLogger
import com.techyourchance.android.common.permissions.PermissionsHelperDelegate
import com.techyourchance.android.common.toasts.ToastsHelper
import com.techyourchance.android.common.usecases.UseCaseResult
import com.techyourchance.android.screens.common.ActivityName
import com.techyourchance.android.screens.common.ScreenSpec
import com.techyourchance.android.screens.common.ScreensNavigator
import com.techyourchance.android.screens.common.activities.BaseActivity
import com.techyourchance.android.screens.common.dialogs.DialogsNavigator
import com.techyourchance.android.screens.common.dialogs.prompt.PromptDialogButton
import com.techyourchance.android.screens.common.dialogs.prompt.PromptDialogDismissedEvent
import com.techyourchance.android.screens.common.mvcviews.ViewMvcFactory
import com.techyourchance.android.screens.debugdrawer.DebugDrawerFragment
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.Serializable
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject lateinit var screensNavigator: ScreensNavigator
    @Inject lateinit var viewMvcFactory: ViewMvcFactory
    @Inject lateinit var permissionsHelperDelegate: PermissionsHelperDelegate
    @Inject lateinit var toastsHelper: ToastsHelper
    @Inject lateinit var dialogsNavigator: DialogsNavigator

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

    override fun getActivityName() = ActivityName.MAIN

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_main)

        drawerLayout = findViewById(R.id.drawerLayout)

        screensNavigator.init(savedInstanceState)

        addDebugDrawerIfNeeded()

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

    override fun onNewIntent(intent: Intent?) {
        MyLogger.i("onNewIntent()")
        super.onNewIntent(intent)
        if (intent != null) {
            val startingScreen = intent.getSerializableExtra(ScreenSpec.INTENT_EXTRA_SCREEN_SPEC) as ScreenSpec?
            if (startingScreen != null) {
                screensNavigator.toScreen(startingScreen)
            }
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

    companion object {
        private const val TAG = "MainActivity"

        fun start(context: Context, screenSpec: ScreenSpec) {
            MyLogger.i(TAG, "start() $screenSpec")
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(ScreenSpec.INTENT_EXTRA_SCREEN_SPEC, screenSpec as Serializable)
            context.startActivity(intent)
        }

        fun startClearTask(context: Context, screenSpec: ScreenSpec) {
            MyLogger.i(TAG, "start() $screenSpec")
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(ScreenSpec.INTENT_EXTRA_SCREEN_SPEC, screenSpec as Serializable)
            context.startActivity(intent)
        }
    }

}
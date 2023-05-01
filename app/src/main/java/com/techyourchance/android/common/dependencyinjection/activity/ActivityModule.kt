package com.techyourchance.android.common.dependencyinjection.activity

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.ncapdevi.fragnav.FragNavController
import com.techyourchance.android.R
import com.techyourchance.android.common.imageloader.ImageLoader
import com.techyourchance.android.common.imageloader.ImageLoaderImpl
import com.techyourchance.android.common.permissions.PermissionsHelper
import com.techyourchance.android.screens.common.ScreensNavigator
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private val activity: AppCompatActivity) {
    
    @Provides
    fun context(): Context {
        return activity
    }

    @Provides
    fun fragmentActivity(): FragmentActivity {
        return activity
    }

    @Provides
    fun appCompatActivity(): AppCompatActivity {
        return activity
    }

    @Provides
    fun activity(): Activity {
        return activity
    }

    @Provides
    fun fragmentManager(activity: AppCompatActivity): FragmentManager {
        return activity.supportFragmentManager
    }

    @Provides
    @ActivityScope
    fun permissionsHelper(activity: Activity): PermissionsHelper {
        return PermissionsHelper(activity)
    }

    @Provides
    @ActivityScope
    fun fragNavController(fragmentManager: FragmentManager): FragNavController {
        return FragNavController(fragmentManager, R.id.fragmentContainerViewMain)
    }

    @Provides
    @ActivityScope
    fun screensNavigator(
        activity: Activity,
        fragNavController: FragNavController
    ): ScreensNavigator {
        return ScreensNavigator(activity, fragNavController)
    }

    @Provides
    @ActivityScope
    fun imageLoader(): ImageLoader {
        return ImageLoaderImpl(activity)
    }

}
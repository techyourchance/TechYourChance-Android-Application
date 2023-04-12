package com.techyourchance.template.common.dependencyinjection.controller

import android.content.Context
import androidx.activity.result.ActivityResultCaller
import androidx.fragment.app.FragmentManager
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryOwner
import com.techyourchance.template.screens.common.dialogs.DialogsNavigator
import com.techyourchance.dialoghelper.DialogHelper
import dagger.Module
import dagger.Provides

@Module
class ControllerModule(
    private val savedStateRegistryOwner: SavedStateRegistryOwner,
    private val activityResultCaller: ActivityResultCaller,
) {

    @Provides
    fun dialogsNavigator(fragmentManager: FragmentManager, context: Context): DialogsNavigator {
        return DialogsNavigator(context, DialogHelper(fragmentManager))
    }

    @Provides
    fun activityResultCaller(): ActivityResultCaller {
        return activityResultCaller
    }

    @Provides
    fun savedStateRegistryOwner(): SavedStateRegistryOwner {
        return savedStateRegistryOwner
    }

}
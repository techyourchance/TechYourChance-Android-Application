package com.techyourchance.android.common.dependencyinjection.controller

import com.techyourchance.android.screens.biometricauth.BiometricAuthFragment
import com.techyourchance.android.screens.common.dialogs.DialogsNavigator
import com.techyourchance.android.screens.common.dialogs.info.InfoDialog
import com.techyourchance.android.screens.common.dialogs.prompt.PromptDialog
import com.techyourchance.android.screens.debugdrawer.DebugDrawerFragment
import com.techyourchance.android.screens.foregroundservice.ForegroundServiceFragment
import com.techyourchance.android.screens.home.HomeFragment
import com.techyourchance.android.screens.main.MainActivity
import com.techyourchance.android.screens.ndkbasics.NdkBasicsFragment
import com.techyourchance.android.screens.questiondetails.QuestionDetailsFragment
import com.techyourchance.android.screens.questionslist.QuestionsListFragment
import com.techyourchance.android.screens.workmanager.WorkManagerFragment
import dagger.Subcomponent

@Subcomponent(modules = [ControllerModule::class, ViewMvcModule::class])
interface ControllerComponent {

    // Activities
    fun inject(activity: MainActivity)

    // Fragments
    fun inject(fragment: HomeFragment)
    fun inject(fragment: DebugDrawerFragment)
    fun inject(fragment: QuestionsListFragment)
    fun inject(fragment: QuestionDetailsFragment)
    fun inject(fragment: BiometricAuthFragment)
    fun inject(fragment: NdkBasicsFragment)
    fun inject(fragment: ForegroundServiceFragment)
    fun inject(fragment: WorkManagerFragment)

    // Dialogs
    fun inject(dialog: PromptDialog)
    fun inject(dialog: InfoDialog)

    // this method is added only to be able to retrieve dialog ID in BaseDialog class
    val dialogNavigator: DialogsNavigator
}
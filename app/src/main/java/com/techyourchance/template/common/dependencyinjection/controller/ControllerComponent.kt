package com.techyourchance.template.common.dependencyinjection.controller

import com.techyourchance.template.screens.common.dialogs.DialogsNavigator
import com.techyourchance.template.screens.common.dialogs.info.InfoDialog
import com.techyourchance.template.screens.common.dialogs.prompt.PromptDialog
import com.techyourchance.template.screens.debugdrawer.DebugDrawerFragment
import com.techyourchance.template.screens.home.HomeFragment
import com.techyourchance.template.screens.main.MainActivity
import com.techyourchance.template.screens.questiondetails.QuestionDetailsFragment
import com.techyourchance.template.screens.questionslist.QuestionsListFragment
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

    // Dialogs
    fun inject(dialog: PromptDialog)
    fun inject(dialog: InfoDialog)

    // this method is added only to be able to retrieve dialog ID in BaseDialog class
    val dialogNavigator: DialogsNavigator
}
package com.techyourchance.android.common.dependencyinjection.controller

import com.techyourchance.android.screens.animations.AnimationsFragment
import com.techyourchance.android.screens.animations.stackedcards.StackedCardsAnimationFragment
import com.techyourchance.android.screens.biometricauth.BiometricAuthFragment
import com.techyourchance.android.screens.common.dialogs.DialogsNavigator
import com.techyourchance.android.screens.common.dialogs.info.InfoDialog
import com.techyourchance.android.screens.common.dialogs.progress.ProgressDialog
import com.techyourchance.android.screens.common.dialogs.prompt.PromptDialog
import com.techyourchance.android.screens.debugdrawer.DebugDrawerFragment
import com.techyourchance.android.screens.foregroundservice.ForegroundServiceFragment
import com.techyourchance.android.screens.home.HomeFragment
import com.techyourchance.android.screens.MainActivity
import com.techyourchance.android.screens.ndkbasics.NdkBasicsFragment
import com.techyourchance.android.screens.questiondetails.QuestionDetailsFragment
import com.techyourchance.android.screens.questionslist.QuestionsListFragment
import com.techyourchance.android.screens.UserInterfacesActivity
import com.techyourchance.android.screens.animations.animatedmessages.AnimatedMessagesFragment
import com.techyourchance.android.screens.animations.dotsprogress.DotsProgressAnimationFragment
import com.techyourchance.android.screens.benchmarks.backgroundtasksmemorybenchmark.BackgroundTasksMemoryBenchmarkFragment
import com.techyourchance.android.screens.benchmarks.benchmarkslist.BenchmarksListFragment
import com.techyourchance.android.screens.benchmarks.backgroundtasksstartupbenchmark.BackgroundTasksStartupBenchmarkFragment
import com.techyourchance.android.screens.composeoverlay.ComposeOverlayFragment
import com.techyourchance.android.screens.composeui.ComposeActivity
import com.techyourchance.android.screens.userinterfaces.UserInterfacesFragment
import com.techyourchance.android.screens.workmanager.WorkManagerFragment
import dagger.Subcomponent

@Subcomponent(modules = [ControllerModule::class, ViewMvcModule::class])
interface ControllerComponent {

    // Activities
    fun inject(activity: MainActivity)
    fun inject(activity: UserInterfacesActivity)
    fun inject(activity: ComposeActivity)

    // Fragments
    fun inject(fragment: HomeFragment)
    fun inject(fragment: DebugDrawerFragment)
    fun inject(fragment: QuestionsListFragment)
    fun inject(fragment: QuestionDetailsFragment)
    fun inject(fragment: BiometricAuthFragment)
    fun inject(fragment: NdkBasicsFragment)
    fun inject(fragment: ForegroundServiceFragment)
    fun inject(fragment: WorkManagerFragment)
    fun inject(fragment: UserInterfacesFragment)
    fun inject(fragment: AnimationsFragment)
    fun inject(fragment: StackedCardsAnimationFragment)
    fun inject(fragment: DotsProgressAnimationFragment)
    fun inject(fragment: BenchmarksListFragment)
    fun inject(fragment: BackgroundTasksStartupBenchmarkFragment)
    fun inject(fragment: BackgroundTasksMemoryBenchmarkFragment)
    fun inject(fragment: ComposeOverlayFragment)
    fun inject(fragment: AnimatedMessagesFragment)

    // Dialogs
    fun inject(dialog: PromptDialog)
    fun inject(dialog: InfoDialog)
    fun inject(dialog: ProgressDialog)

    // this method is added only to be able to retrieve dialog ID in BaseDialog class
    val dialogNavigator: DialogsNavigator
}
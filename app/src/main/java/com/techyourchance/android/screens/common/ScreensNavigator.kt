package com.techyourchance.android.screens.common

import android.content.Intent
import android.os.Bundle
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavLogger
import com.ncapdevi.fragnav.FragNavTransactionOptions
import com.techyourchance.android.common.Observable
import com.techyourchance.android.screens.animations.AnimationsFragment
import com.techyourchance.android.screens.animations.stackedcards.StackedCardsAnimationFragment
import com.techyourchance.android.screens.biometricauth.BiometricAuthFragment
import com.techyourchance.android.screens.common.activities.BaseViewsActivity
import com.techyourchance.android.screens.common.fragments.DummyRootFragment
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
import com.techyourchance.android.screens.benchmarks.sharedprefs.SharedPrefsBenchmarkFragment
import com.techyourchance.android.screens.composeoverlay.ComposeOverlayFragment
import com.techyourchance.android.screens.composenavbottombar.ComposeNavBottomBarActivity
import com.techyourchance.android.screens.handlerlooper.HandlerLooperFragment
import com.techyourchance.android.screens.userinterfaces.UserInterfacesFragment
import com.techyourchance.android.screens.workmanager.WorkManagerFragment
import timber.log.Timber


@UiThread
class ScreensNavigator constructor(
        private val activity: AppCompatActivity,
        private val fragNavController: FragNavController,
): Observable<ScreensNavigator.Listener>() {

    interface Listener {
        fun onScreenChanged()
    }

    fun init(savedInstanceState: Bundle?) {
        fragNavController.rootFragments = listOf(getRootFragment())
        fragNavController.createEager = true
        fragNavController.fragNavLogger = object: FragNavLogger {
            override fun error(message: String, throwable: Throwable) {
                Timber.e(throwable, "ScreensNavigator: %s", message)
            }
        }
        fragNavController.defaultTransactionOptions = FragNavTransactionOptions.newBuilder().build()
        fragNavController.initialize(FragNavController.TAB1, savedInstanceState)

        fragNavController.transactionListener = object : FragNavController.TransactionListener {
            override fun onFragmentTransaction(
                fragment: Fragment?,
                transactionType: FragNavController.TransactionType) {
                listeners.map { it.onScreenChanged() }
            }

            override fun onTabTransaction(fragment: Fragment?, index: Int) {
                throw RuntimeException("assuming no tabs for now")
            }
        }
    }

    private fun getRootFragment(): Fragment {
        return DummyRootFragment()
    }

    fun onSaveInstanceState(saveInstanceState: Bundle) {
        fragNavController.onSaveInstanceState(saveInstanceState)
    }

    fun toScreen(screenSpec: ScreenSpec, clearBackStack: Boolean = false) {
        if (getCurrentActivityName() != screenSpec.activityName) {
            // change Activity
            when(screenSpec.activityName) {
                ActivityName.MAIN -> {
                    MainActivity.startClearTask(activity, screenSpec)
                }
                ActivityName.USER_INTERFACES -> {
                    UserInterfacesActivity.start(activity, screenSpec)
                }
                ActivityName.COMPOSE_NAV_BOTTOM_BAR -> {
                    ComposeNavBottomBarActivity.start(activity, screenSpec)
                }
            }
        } else {
            // change just Fragment
            val nextFragment = when(screenSpec) {
                is ScreenSpec.Home -> HomeFragment.newInstance()
                is ScreenSpec.StackOverflowQuestionsList -> QuestionsListFragment.newInstance()
                is ScreenSpec.StackOverflowQuestionDetails -> QuestionDetailsFragment.newInstance(screenSpec)
                is ScreenSpec.BiometricLock -> BiometricAuthFragment.newInstance()
                is ScreenSpec.NdkBasics -> NdkBasicsFragment.newInstance()
                is ScreenSpec.ForegroundService -> ForegroundServiceFragment.newInstance()
                is ScreenSpec.WorkManager -> WorkManagerFragment.newInstance()
                is ScreenSpec.UserInterfaces -> UserInterfacesFragment.newInstance()
                is ScreenSpec.Animations -> AnimationsFragment.newInstance()
                is ScreenSpec.StackedCardsAnimation -> StackedCardsAnimationFragment.newInstance()
                is ScreenSpec.DotsProgressAnimation -> DotsProgressAnimationFragment.newInstance()
                is ScreenSpec.Benchmarks -> BenchmarksListFragment.newInstance()
                is ScreenSpec.BackgroundTasksStartupBenchmark -> BackgroundTasksStartupBenchmarkFragment.newInstance()
                is ScreenSpec.BackgroundTasksMemoryBenchmark -> BackgroundTasksMemoryBenchmarkFragment.newInstance(screenSpec)
                is ScreenSpec.SharedPrefsBenchmark -> SharedPrefsBenchmarkFragment.newInstance()
                is ScreenSpec.ComposeOverlay -> ComposeOverlayFragment.newInstance()
                is ScreenSpec.HandlerLooper -> HandlerLooperFragment.newInstance()
                is ScreenSpec.AnimatedMessages -> AnimatedMessagesFragment.newInstance()
                is ScreenSpec.ComposeNavBottomBar -> throw RuntimeException("compose navigation is not handled here (currently)")
            }
            toFragment(nextFragment, clearBackStack)
        }
    }

    fun navigateBack() {
        if (fragNavController.isRootFragment) {
            if (activity.isTaskRoot) {
                val homeIntent = Intent(Intent.ACTION_MAIN)
                homeIntent.addCategory(Intent.CATEGORY_HOME)
                homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                activity.startActivity(homeIntent)
            } else {
                activity.finish()
            }
        } else {
            fragNavController.popFragment()
        }
    }

    private fun toFragment(fragment: Fragment, clearBackStack: Boolean) {
        if (clearBackStack || isFragmentMustBeRoot(fragment)) {
            fragNavController.clearStack()
            fragNavController.replaceFragment(fragment)
        } else if (shouldReplaceCurrentFragment(fragment)) {
            fragNavController.replaceFragment(fragment)
        } else {
            fragNavController.pushFragment(fragment)
        }
    }

    private fun isFragmentMustBeRoot(nextFragment: Fragment): Boolean {
        val currentFragment = fragNavController.currentFrag ?: return false
        return currentFragment is DummyRootFragment
                || nextFragment is HomeFragment
    }

    private fun shouldReplaceCurrentFragment(nextFragment: Fragment): Boolean {
        val currentFragment = fragNavController.currentFrag ?: return false
        return false
        // return true if current fragment should be removed from nav backstack
    }

    private fun getCurrentActivityName(): ActivityName {
        return (activity as BaseViewsActivity).getActivityName()
    }


}
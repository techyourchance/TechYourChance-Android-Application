package com.techyourchance.template.screens.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavLogger
import com.ncapdevi.fragnav.FragNavTransactionOptions
import com.techyourchance.template.common.Observable
import com.techyourchance.template.screens.common.activities.BaseActivity
import com.techyourchance.template.screens.common.fragments.DummyRootFragment
import com.techyourchance.template.screens.home.HomeFragment
import com.techyourchance.template.screens.main.MainActivity
import com.techyourchance.template.screens.questiondetails.QuestionDetailsFragment
import com.techyourchance.template.screens.questionslist.QuestionsListFragment
import timber.log.Timber


@UiThread
class ScreensNavigator constructor(
        private val activity: Activity,
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

    fun toScreen(screenSpec: ScreenSpec) {
        if (getCurrentActivityName() != screenSpec.activityName) {
            // change Activity
            when(screenSpec.activityName) {
                ActivityName.MAIN -> {
                    MainActivity.startClearTask(activity, screenSpec)
                }
            }
        } else {
            // change just Fragment
            when(screenSpec) {
                is ScreenSpec.Home -> toFragment(HomeFragment.newInstance())
                is ScreenSpec.StackOverflowQuestionsList -> toFragment(QuestionsListFragment.newInstance())
                is ScreenSpec.StackOverflowQuestionDetails -> toFragment(QuestionDetailsFragment.newInstance(screenSpec))
                else -> throw RuntimeException("unsupported screen: $screenSpec")
            }
        }
    }

    fun navigateBack() {
        if (fragNavController.isRootFragment) {
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            activity.startActivity(homeIntent)
        } else {
            fragNavController.popFragment()
        }
    }

    private fun toFragment(fragment: Fragment) {
        if (shouldClearFragmentsStack(fragment)) {
            fragNavController.clearStack()
            fragNavController.replaceFragment(fragment)
        } else if (shouldReplaceCurrentFragment(fragment)) {
            fragNavController.replaceFragment(fragment)
        } else {
            fragNavController.pushFragment(fragment)
        }
    }

    private fun shouldClearFragmentsStack(nextFragment: Fragment): Boolean {
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
        return (activity as BaseActivity).getActivityName()
    }


}
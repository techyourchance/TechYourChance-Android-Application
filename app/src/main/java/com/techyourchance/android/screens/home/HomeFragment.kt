package com.techyourchance.android.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techyourchance.android.R
import com.techyourchance.android.screens.common.ScreenSpec
import com.techyourchance.android.screens.common.ScreensNavigator
import com.techyourchance.android.screens.common.dialogs.DialogsNavigator
import com.techyourchance.android.screens.common.fragments.BaseFragment
import com.techyourchance.android.screens.common.mvcviews.ViewMvcFactory
import javax.inject.Inject

class HomeFragment : BaseFragment(), HomeViewMvc.Listener {

    @Inject lateinit var viewMvcFactory: ViewMvcFactory
    @Inject lateinit var dialogsNavigator: DialogsNavigator
    @Inject lateinit var screensNavigator: ScreensNavigator

    private lateinit var viewMvc: HomeViewMvc

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (!this::viewMvc.isInitialized) {
            viewMvc = viewMvcFactory.newHomeViewMvc(container)
            viewMvc.bindDestinations(getDestinations())
        }
        return viewMvc.getRootView()
    }

    private fun getDestinations(): List<FromHomeDestination> {
        return listOf(
            FromHomeDestination(
                getString(R.string.from_home_destination_stackoverflow),
                ScreenSpec.StackOverflowQuestionsList
            ),
            FromHomeDestination(
                getString(R.string.from_home_destination_biometric_auth),
                ScreenSpec.BiometricLock
            ),
            FromHomeDestination(
                getString(R.string.from_home_destination_ndk_basics),
                ScreenSpec.NdkBasics
            ),
            FromHomeDestination(
                getString(R.string.from_home_destination_foreground_service),
                ScreenSpec.ForegroundService
            ),
        )
    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)
    }

    override fun onStop() {
        super.onStop()
        viewMvc.unregisterListener(this)
    }

    override fun onDestinationClicked(destination: FromHomeDestination) {
        screensNavigator.toScreen(destination.screenSpec)
    }

    companion object {

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}
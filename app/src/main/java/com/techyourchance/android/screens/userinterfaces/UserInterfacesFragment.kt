package com.techyourchance.android.screens.userinterfaces

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techyourchance.android.R
import com.techyourchance.android.screens.common.ScreenSpec
import com.techyourchance.android.screens.common.ScreensNavigator
import com.techyourchance.android.screens.common.fragments.BaseFragment
import com.techyourchance.android.screens.common.mvcviews.ViewMvcFactory
import javax.inject.Inject

class UserInterfacesFragment : BaseFragment(), UserInterfacesViewMvc.Listener {

    @Inject lateinit var viewMvcFactory: ViewMvcFactory
    @Inject lateinit var screensNavigator: ScreensNavigator

    private lateinit var viewMvc: UserInterfacesViewMvc

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (!this::viewMvc.isInitialized) {
            viewMvc = viewMvcFactory.newUserInterfacesViewMvc(container)
            viewMvc.bindDestinations(getDestinations())
        }
        return viewMvc.getRootView()
    }

    private fun getDestinations(): List<FromUserInterfacesDestination> {
        return listOf(
            FromUserInterfacesDestination(
                getString(R.string.screen_animations),
                ScreenSpec.Animations
            ),
            FromUserInterfacesDestination(
                getString(R.string.screen_stacked_cards_animation),
                ScreenSpec.StackedCardsAnimation
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

    override fun onDestinationClicked(destination: FromUserInterfacesDestination) {
        screensNavigator.toScreen(destination.screenSpec)
    }

    override fun onBackClicked() {
        screensNavigator.navigateBack()
    }

    companion object {
        fun newInstance(): UserInterfacesFragment {
            return UserInterfacesFragment()
        }
    }
}
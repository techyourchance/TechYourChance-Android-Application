package com.techyourchance.android.screens.animations.stackedcards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techyourchance.android.backgroundwork.ForegroundServiceState
import com.techyourchance.android.screens.common.ScreensNavigator
import com.techyourchance.android.screens.common.fragments.BaseFragment
import com.techyourchance.android.screens.common.mvcviews.ViewMvcFactory
import javax.inject.Inject

class StackedCardsAnimationFragment : BaseFragment(), StackedCardsAnimationViewMvc.Listener {

    @Inject lateinit var viewMvcFactory: ViewMvcFactory
    @Inject lateinit var screensNavigator: ScreensNavigator

    private lateinit var viewMvc: StackedCardsAnimationViewMvc

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewMvc = viewMvcFactory.newStackedCardsAnimationViewMvc(container)
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)
    }

    override fun onStop() {
        super.onStop()
        viewMvc.unregisterListener(this)
    }

    override fun onBackClicked() {
        screensNavigator.navigateBack()
    }

    companion object {

        fun newInstance(): StackedCardsAnimationFragment {
            return StackedCardsAnimationFragment()
        }
    }
}
package com.techyourchance.android.screens.animations.stackedcards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.techyourchance.android.screens.common.ScreensNavigator
import com.techyourchance.android.screens.common.fragments.BaseFragment
import com.techyourchance.android.screens.common.mvcviews.ViewMvcFactory
import com.techyourchance.android.screens.common.mvcviews.ViewMvcType
import com.techyourchance.android.settings.SettingsManager
import com.techyourchance.settingshelper.SettingEntryListener
import javax.inject.Inject

class StackedCardsAnimationFragment : BaseFragment(), StackedCardsAnimationViewMvc.Listener {

    @Inject lateinit var viewMvcFactory: ViewMvcFactory
    @Inject lateinit var screensNavigator: ScreensNavigator
    @Inject lateinit var settingsManager: SettingsManager
    @Inject lateinit var injectedFragmentManager: FragmentManager

    private val composeBasedViewMvcListener = SettingEntryListener<Boolean> { _, isUseCompose ->
        if (isUseCompose) {
            viewMvc.setType(ViewMvcType.COMPOSE_BASED)
        } else {
            viewMvc.setType(ViewMvcType.VIEW_BASED)
        }
    }

    private lateinit var viewMvc: StackedCardsAnimationViewMvc

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        viewMvc = viewMvcFactory.newStackedCardsAnimationViewMvc(container)
        val viewMvcType = if (settingsManager.stackedCardAnimationUseCompose().value) {
            ViewMvcType.COMPOSE_BASED
        } else {
            ViewMvcType.VIEW_BASED
        }
        viewMvc.setType(viewMvcType)
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)
        settingsManager.stackedCardAnimationUseCompose().registerListener(composeBasedViewMvcListener)
    }

    override fun onStop() {
        super.onStop()
        viewMvc.unregisterListener(this)
        settingsManager.stackedCardAnimationUseCompose().unregisterListener(composeBasedViewMvcListener)
    }

    override fun onToggleComposeClicked() {
        settingsManager.stackedCardAnimationUseCompose().value = !settingsManager.stackedCardAnimationUseCompose().value
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
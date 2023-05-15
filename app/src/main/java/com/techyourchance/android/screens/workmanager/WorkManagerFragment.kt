package com.techyourchance.android.screens.workmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techyourchance.android.backgroundwork.workmanager.MyWorkerConfig
import com.techyourchance.android.backgroundwork.workmanager.MyWorkerManager
import com.techyourchance.android.backgroundwork.workmanager.MyWorkerState
import com.techyourchance.android.screens.common.ScreensNavigator
import com.techyourchance.android.screens.common.fragments.BaseFragment
import com.techyourchance.android.screens.common.mvcviews.ViewMvcFactory
import javax.inject.Inject

class WorkManagerFragment : BaseFragment(),
    WorkManagerViewMvc.Listener,
    MyWorkerManager.Listener
{

    @Inject lateinit var viewMvcFactory: ViewMvcFactory
    @Inject lateinit var screensNavigator: ScreensNavigator
    @Inject lateinit var myWorkerManager: MyWorkerManager

    private lateinit var viewMvc: WorkManagerViewMvc

    private lateinit var currentState: MyWorkerState

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewMvc = viewMvcFactory.newWorkManagerViewMvc(container)
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)
        myWorkerManager.registerListener(this)

        handleMyWorkerState(myWorkerManager.getState())
    }

    override fun onStop() {
        super.onStop()
        viewMvc.unregisterListener(this)
        myWorkerManager.unregisterListener(this)
    }

    private fun handleMyWorkerState(state: MyWorkerState) {
        currentState = state
        viewMvc.bindWorkerState(state)
    }

    override fun onMyWorkerStateChanged(state: MyWorkerState) {
        handleMyWorkerState(state)
    }

    override fun onToggleWorkClicked() {
        if (currentState !is MyWorkerState.Working) {
            myWorkerManager.startWorker(
                MyWorkerConfig(viewMvc.getIsExpedited(), MAX_RETRIES, BACKOFF_DURATION_SEC)
            )
        } else {
            myWorkerManager.stopWorker()
        }
    }

    override fun onBackClicked() {
        screensNavigator.navigateBack()
    }

    companion object {

        const val MAX_RETRIES = 3
        const val BACKOFF_DURATION_SEC = 3

        fun newInstance(): WorkManagerFragment {
            return WorkManagerFragment()
        }
    }
}
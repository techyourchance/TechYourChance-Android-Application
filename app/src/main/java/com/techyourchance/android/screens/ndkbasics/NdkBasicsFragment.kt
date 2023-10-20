package com.techyourchance.android.screens.ndkbasics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techyourchance.android.apkupdate.ApkInfo
import com.techyourchance.android.common.eventbus.EventBusSubscriber
import com.techyourchance.android.common.toasts.ToastsHelper
import com.techyourchance.android.ndk.NdkManager
import com.techyourchance.android.screens.common.ScreensNavigator
import com.techyourchance.android.screens.common.dialogs.DialogsNavigator
import com.techyourchance.android.screens.common.dialogs.progress.ProgressDialogDismissedEvent
import com.techyourchance.android.screens.common.dialogs.prompt.PromptDialogButton
import com.techyourchance.android.screens.common.dialogs.prompt.PromptDialogDismissedEvent
import com.techyourchance.android.screens.common.fragments.BaseFragment
import com.techyourchance.android.screens.common.mvcviews.ViewMvcFactory
import com.techyourchance.android.screens.home.HomeFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class NdkBasicsFragment : BaseFragment(), NdkBasicsViewMvc.Listener {

    @Inject lateinit var viewMvcFactory: ViewMvcFactory
    @Inject lateinit var dialogsNavigator: DialogsNavigator
    @Inject lateinit var screensNavigator: ScreensNavigator
    @Inject lateinit var eventBusSubscriber: EventBusSubscriber
    @Inject lateinit var toastsHelper: ToastsHelper
    @Inject lateinit var ndkManager: NdkManager

    private lateinit var viewMvc: NdkBasicsViewMvc

    private var computeJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewMvc = viewMvcFactory.newNdkBasicsViewMvc(container)
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)
        eventBusSubscriber.register(this)
    }

    override fun onStop() {
        super.onStop()
        viewMvc.unregisterListener(this)
        eventBusSubscriber.unregister(this)
        computeJob?.cancel()
    }

    override fun onComputeFibonacciClicked() {
        computeJob = coroutineScope.launch {
            val arg = viewMvc.getArgument()
            if (arg >= 0) {
                val result = try {
                    dialogsNavigator.showCancellableProgressDialog(null, DIALOG_ID_COMPUTE_PROGRESS)
                    ndkManager.computeFibonacci(arg)
                } finally {
                    dialogsNavigator.dismissCurrentlyShownDialog()
                }
                toastsHelper.showToast("Fibonacci result: ${result.result}")
            } else {
                dialogsNavigator.showInvalidFibonacciArgumentDialog(arg, null)
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: ProgressDialogDismissedEvent) {
        when(event.id) {
            DIALOG_ID_COMPUTE_PROGRESS -> {
                if (event.isCancelled) {
                    computeJob?.cancel()
                }
            }
        }
    }

    override fun onBackClicked() {
        screensNavigator.navigateBack()
    }

    companion object {
        const val DIALOG_ID_COMPUTE_PROGRESS = "DIALOG_ID_COMPUTE_PROGRESS"

        fun newInstance(): NdkBasicsFragment {
            return NdkBasicsFragment()
        }
    }
}
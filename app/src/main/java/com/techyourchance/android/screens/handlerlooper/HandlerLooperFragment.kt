package com.techyourchance.android.screens.handlerlooper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.techyourchance.android.R
import com.techyourchance.android.common.logs.MyLogger
import com.techyourchance.android.screens.common.ScreensNavigator
import com.techyourchance.android.screens.common.composables.MyTheme
import com.techyourchance.android.screens.common.composables.MyTopAppBar
import com.techyourchance.android.screens.common.fragments.BaseFragment
import javax.inject.Inject

class HandlerLooperFragment : BaseFragment() {

    @Inject lateinit var screensNavigator: ScreensNavigator

    private var numOfTasks = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MyTheme {
                    Scaffold(
                        topBar = {
                            MyTopAppBar(
                                title = stringResource(id = R.string.screen_handler_looper),
                                showBackButton = true,
                                onBackClicked = {
                                    screensNavigator.navigateBack()
                                },
                            )
                        },
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    PaddingValues(
                                        0.dp,
                                        0.dp,
                                        0.dp,
                                        innerPadding.calculateBottomPadding()
                                    )
                                )
                        ) {
                            Button(
                                modifier = Modifier.align(Alignment.Center),
                                onClick = { executeTaskInNewThread(numOfTasks++) }
                            ) {
                                Text(
                                    text = "Execute task in a new thread"
                                )
                            }
                        }
                    }

                }
            }
        }
    }

    private fun executeTaskInNewThread(taskNum: Int) {
        Thread {
            MyLogger.i("task started: $taskNum")
            val taskNumPadded = taskNum.toString().padEnd(3)
            for (count in 1..3) {
                Thread.sleep(1000)
                MyLogger.i("task $taskNumPadded count: $count")
            }
            MyLogger.i("task completed: $taskNum")
        }.start()
    }

    private fun onBackClicked() {
        screensNavigator.navigateBack()
    }

    companion object {
        fun newInstance(): HandlerLooperFragment {
            return HandlerLooperFragment()
        }
    }
}
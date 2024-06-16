package com.techyourchance.android.screens.handlerlooper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject

class HandlerLooperFragment : BaseFragment() {

    @Inject lateinit var screensNavigator: ScreensNavigator

    private var numOfTasks = 0

    private var myLooper: MyLooper? = null

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
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    PaddingValues(
                                        0.dp,
                                        0.dp,
                                        0.dp,
                                        innerPadding.calculateBottomPadding()
                                    )
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {

                            Button(
                                onClick = { executeTaskInNewThread(numOfTasks++) }
                            ) {
                                Text(
                                    text = "Execute task in a new thread"
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { executeTaskInLooperThread(numOfTasks++) }
                            ) {
                                Text(
                                    text = "Execute task in a looper thread"
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

    private fun executeTaskInLooperThread(taskNum: Int) {
        myLooper?.enqueueMessage(
            Runnable {
                MyLogger.i("task started: $taskNum")
                val taskNumPadded = taskNum.toString().padEnd(3)
                for (count in 1..3) {
                    Thread.sleep(1000)
                    MyLogger.i("task $taskNumPadded count: $count")
                }
                MyLogger.i("task completed: $taskNum")
            }
        )
    }

    override fun onStart() {
        super.onStart()
        myLooper = MyLooper()
        myLooper!!.prepare()
    }

    override fun onStop() {
        super.onStop()
        myLooper?.quit()
    }

    private fun onBackClicked() {
        screensNavigator.navigateBack()
    }

    companion object {
        fun newInstance(): HandlerLooperFragment {
            return HandlerLooperFragment()
        }
    }

    private class MyLooper {

        private var looperThread: Thread? = null
        private val looperQueue: BlockingQueue<Runnable> = LinkedBlockingQueue(20)
        private val poison = Runnable {}


        fun prepare() {
            if (looperThread != null) {
                throw RuntimeException("shouldn't be called more than once")
            }
            looperThread = Thread {
                while (true) {
                    val currentMessage = looperQueue.take()
                    if (currentMessage == poison) {
                        break
                    }
                    currentMessage.run()
                }
            }.apply {
                start()
            }
        }

        fun quit() {
            looperQueue.put(poison)
        }

        fun enqueueMessage(runnable: Runnable) {
            looperQueue.put(runnable)
        }
    }
}

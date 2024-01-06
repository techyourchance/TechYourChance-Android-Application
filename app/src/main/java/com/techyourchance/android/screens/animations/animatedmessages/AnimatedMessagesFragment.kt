package com.techyourchance.android.screens.animations.animatedmessages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.techyourchance.android.R
import com.techyourchance.android.screens.common.ScreensNavigator
import com.techyourchance.android.screens.common.composables.MyScreenTemplate
import com.techyourchance.android.screens.common.fragments.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.LinkedList
import javax.inject.Inject

class AnimatedMessagesFragment : BaseFragment() {

    @Inject lateinit var screensNavigator: ScreensNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        return ComposeView(requireContext()).apply {
            setContent {

                val animationDurationMs = 500
                val messageDisplayDurationMs = 1000

                val coroutineScope = rememberCoroutineScope()

                val messagesQueue = remember { LinkedList<Int>() }
                var messageCounter by remember { mutableIntStateOf(0) }
                var currentMessageNum by remember { mutableIntStateOf(0) }
                var showMessage by remember { mutableStateOf(false) }
                var processingMessages by remember { mutableStateOf(false) }

                LaunchedEffect(messageCounter) {
                    if (messageCounter > 0) {
                        messagesQueue.add(messageCounter)
                    }
                    if (!processingMessages) {
                        processingMessages = true
                        coroutineScope.launch(Dispatchers.Main.immediate) {
                            while (messagesQueue.isNotEmpty()) {
                                currentMessageNum = messagesQueue.remove()
                                showMessage = true
                                delay(animationDurationMs.toLong() + messageDisplayDurationMs)
                                showMessage = false
                                delay(animationDurationMs.toLong())
                            }
                            processingMessages = false
                        }
                    }
                }

                MyScreenTemplate(
                    onBackClicked = { screensNavigator.navigateBack() }
                ) {
                    Button(
                        modifier = Modifier
                            .align(Alignment.Center),
                        onClick = {
                            messageCounter = messageCounter.inc()
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.animated_message_send_message)
                        )
                    }

                    AnimatedVisibility(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = 80.dp),
                        visible = showMessage,
                        enter = fadeIn(animationSpec = tween(durationMillis = animationDurationMs)),
                        exit = fadeOut(animationSpec = tween(durationMillis = animationDurationMs))
                    ) {
                        Text(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 15.dp, vertical = 5.dp),
                            text = stringResource(id = R.string.animated_message_message, currentMessageNum),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    companion object {

        fun newInstance(): AnimatedMessagesFragment {
            return AnimatedMessagesFragment()
        }
    }
}
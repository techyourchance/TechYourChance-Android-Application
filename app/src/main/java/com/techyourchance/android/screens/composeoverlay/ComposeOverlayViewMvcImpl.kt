package com.techyourchance.android.screens.composeoverlay

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import com.techyourchance.android.R
import com.techyourchance.android.screens.common.widgets.Buttons

class ComposeOverlayViewMvcImpl(
    context: Context,
): ComposeOverlayViewMvc() {

    val isOverlayShown = mutableStateOf(false)

    init {
        setRootView(
            ComposeView(context).apply {
                setContent {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Buttons.DefaultButton(
                            modifier = Modifier.align(Alignment.Center),
                            onClick = { listeners.forEach { it.onToggleOverlayClicked() } }
                        ) {
                            Text(
                                text = if (isOverlayShown.value) {
                                    stringResource(id = R.string.compose_overlay_hide)
                                } else {
                                    stringResource(id = R.string.compose_overlay_show)
                                }
                            )
                        }
                    }
                }
            }
        )

    }

    override fun bindIsOverlayShown(isShown: Boolean) {
        isOverlayShown.value = isShown
    }
}
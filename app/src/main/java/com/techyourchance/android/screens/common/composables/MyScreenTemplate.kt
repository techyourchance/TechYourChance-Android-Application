package com.techyourchance.android.screens.common.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.techyourchance.android.R

@Composable
fun MyScreenTemplate(
    onBackClicked: () -> Unit,
    title: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    MyTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        LocalContext.current.resources
                            .getDimension(R.dimen.toolbar_height)
                            .pxToDp()
                    )
                    .background(color = MaterialTheme.colorScheme.primary)
            ) {
                Box(
                    modifier = Modifier
                        .size(getDimen(R.dimen.toolbar_button_size))
                        .align(Alignment.CenterStart)
                        .clickable {
                            onBackClicked()
                        }
                ) {
                    Image(
                        modifier = Modifier
                            .size(getDimen(id = R.dimen.toolbar_icon_size))
                            .align(Alignment.Center),
                        painter = painterResource(id = R.drawable.ic_navigate_up),
                        contentDescription = null,
                    )
                }

                if (title != null) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.Center),
                        text = title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White
                        )
                    )
                }

            }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                content()
            }
        }
    }
}
package com.techyourchance.android.screens.common.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.techyourchance.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    title: String,
    showBackButton: Boolean,
    onBackClicked: (() -> Unit)?,
) {
    CenterAlignedTopAppBar(
        modifier = Modifier.height(dimensionResource(id = R.dimen.toolbar_height)),
        title = {
            Row (
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = title,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = dimensionResource(id = R.dimen.toolbar_title_text_size).value.sp
                )
            }
        },
        navigationIcon = {
            if (showBackButton && onBackClicked != null) {
                IconButton(
                    onClick = onBackClicked
                ) {
                    Icon(
                        modifier = Modifier.size(dimensionResource(id = R.dimen.toolbar_icon_size)),
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = Color.White,
                        contentDescription = "back"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary),
    )
}

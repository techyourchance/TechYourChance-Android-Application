package com.techyourchance.android.screens.common.widgets

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.techyourchance.android.R
import com.techyourchance.android.screens.common.context.getAttrColor
import com.google.android.material.R as MaterialR

object Buttons {

    @Composable
    fun DefaultButton(
            onClick: () -> Unit,
            modifier: Modifier = Modifier,
            colors: ButtonColors = ButtonDefaults.elevatedButtonColors(
                    containerColor = colorResource(id = R.color.button),
                    contentColor = LocalContext.current.getAttrColor(MaterialR.attr.colorOnSurface)
            ),
            content: @Composable RowScope.() -> Unit
    ) {
        Button(
                onClick = onClick,
                modifier = modifier,
                colors = colors,
                content = content
        )
    }
}

@Preview(name = "Buttons.DefaultButton")
@Composable
private fun DefaultButtonPreview() {
    Buttons.DefaultButton(
            onClick = {}
    ) {
        Text("Button")
    }
}
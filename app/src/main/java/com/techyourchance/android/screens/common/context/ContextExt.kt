package com.techyourchance.android.screens.common.context

import android.content.Context
import android.util.TypedValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource


/*
  This is a temporary solution to fetch colors from theme attributes until full Compose MaterialTheme support.
  Later on, we'll be able to retrieve colors using MaterialTheme.colors.*.
*/
@Composable
fun Context.getAttrColor(attr: Int): Color {
    val id =  TypedValue().apply {
        theme.resolveAttribute(attr, this, true)
    }.resourceId

    return colorResource(id = id)
}
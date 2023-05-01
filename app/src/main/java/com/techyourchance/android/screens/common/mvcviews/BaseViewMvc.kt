package com.techyourchance.android.screens.common.mvcviews

import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

abstract class BaseViewMvc : ViewMvc {

   private lateinit var rootView: View

    protected fun setRootView(view: View) {
        rootView = view
    }

    override fun getRootView(): View {
        return rootView
    }

    protected fun <T : View?> findViewById(@IdRes id: Int): T {
        return getRootView().findViewById(id)
    }

    protected val context: Context get() = getRootView().context

    protected fun getString(@StringRes id: Int): String {
        return context.getString(id)
    }

    protected fun getString(@StringRes id: Int, vararg formatArgs: Any): String {
        return context.getString(id, *formatArgs)
    }

    @ColorInt
    protected fun getColor(@ColorRes colorId: Int): Int {
        return ContextCompat.getColor(context, colorId)
    }
}
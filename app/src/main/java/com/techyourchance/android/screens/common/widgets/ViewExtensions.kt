package com.techyourchance.android.screens.common.widgets

import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import com.ncapdevi.fragnav.BuildConfig
import com.techyourchance.android.common.logs.MyLogger
import java.lang.RuntimeException

object ViewExtensions {

    const val STROKE_WIDTH_DEFAULT = 2

    fun View.showBorder(color: Int) {
        this.showBorder(STROKE_WIDTH_DEFAULT, color)
    }

    fun View.showBorder(width: Int, color: Int) {
        val background: GradientDrawable = when (this.background) {
            null -> {
                GradientDrawable()
            }
            is GradientDrawable -> {
                this.background as GradientDrawable
            }
            else -> {
                val msg = "setBackgroundBorder() called on View that has background other than GradientDrawable"
                if (BuildConfig.DEBUG) {
                    throw RuntimeException(msg)
                } else {
                    MyLogger.e(msg)
                }
                return
            }
        }
        background.setStroke(width, color)
        this.background = background
    }

    fun TextView.strikethrough() {
        this.paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    fun ImageView.colorize(@ColorInt color: Int) {
        val porterDuffColorFilter = PorterDuffColorFilter(
            color,
            PorterDuff.Mode.SRC_IN
        )
        this.colorFilter = porterDuffColorFilter
    }

    fun Drawable.colorize(@ColorInt color: Int) {
        val porterDuffColorFilter = PorterDuffColorFilter(
            color,
            PorterDuff.Mode.SRC_IN
        )
        this.colorFilter = porterDuffColorFilter
    }

}
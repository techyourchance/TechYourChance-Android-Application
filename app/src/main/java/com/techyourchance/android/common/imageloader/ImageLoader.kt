package com.techyourchance.android.common.imageloader

import android.widget.ImageView
import androidx.annotation.DrawableRes

interface ImageLoader {

    interface OnCompletionListener {
        fun onLoadCompleted()
        fun onLoadFailed()
    }

    fun loadImage(uri: String, target: ImageView)

    fun loadImage(
        uri: String,
        target: ImageView,
        @DrawableRes inProgressDrawableId: Int,
        @DrawableRes errorDrawableId: Int
    )

    fun loadImageCircular(
        uri: String,
        target: ImageView,
        @DrawableRes inProgressDrawableId: Int,
        @DrawableRes errorDrawableId: Int
    )

    fun loadImage(
        uri: String,
        target: ImageView,
        listener: ImageLoader.OnCompletionListener
    )

}
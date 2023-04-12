package com.techyourchance.template.common.imageloader

import android.app.Activity
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.load.engine.GlideException
import androidx.annotation.DrawableRes
import com.techyourchance.template.common.logs.MyLogger
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.Target

class ImageLoaderImpl(private val activity: Activity): ImageLoader {


    private val defaultRequestOptions: RequestOptions = RequestOptions().centerInside()

    override fun loadImage(uri: String, target: ImageView) {
        Glide.with(activity)
            .load(uri)
            .apply(defaultRequestOptions)
            .into(target)
    }

    override fun loadImage(
        uri: String,
        target: ImageView,
        @DrawableRes inProgressDrawableId: Int,
        @DrawableRes errorDrawableId: Int
    ) {
        val requestOptions = defaultRequestOptions.clone()
            .placeholder(inProgressDrawableId)
            .error(errorDrawableId)
        Glide.with(activity)
            .load(uri).apply(requestOptions).transition(DrawableTransitionOptions.withCrossFade())
            .into(target)
    }

    override fun loadImage(
        uri: String,
        target: ImageView,
        listener: ImageLoader.OnCompletionListener,
    ) {
        Glide.with(activity)
            .load(Uri.parse(uri))
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    MyLogger.e("failed to load image from uri: $uri", e)
                    listener.onLoadFailed()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    listener.onLoadCompleted()
                    return false
                }
            })
            .apply(defaultRequestOptions)
            .into(target)
    }

    override fun loadImageCircular(
        uri: String,
        target: ImageView,
        inProgressDrawableId: Int,
        errorDrawableId: Int
    ) {
        val requestOptions = defaultRequestOptions.clone()
            .placeholder(inProgressDrawableId)
            .error(errorDrawableId)
            .circleCrop()
        Glide.with(activity).load(uri).apply(requestOptions).into(target)
    }



}
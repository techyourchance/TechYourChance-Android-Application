package com.techyourchance.template.common.dependencyinjection.controller

import android.content.Context
import android.view.LayoutInflater
import com.techyourchance.template.common.imageloader.ImageLoader
import com.techyourchance.template.screens.common.mvcviews.ViewMvcFactory
import dagger.Module
import dagger.Provides

@Module
class ViewMvcModule {
    @Provides
    fun layoutInflater(context: Context): LayoutInflater {
        return LayoutInflater.from(context)
    }
}
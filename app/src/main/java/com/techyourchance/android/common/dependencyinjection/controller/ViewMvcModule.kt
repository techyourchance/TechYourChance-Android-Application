package com.techyourchance.android.common.dependencyinjection.controller

import android.content.Context
import android.view.LayoutInflater
import dagger.Module
import dagger.Provides

@Module
class ViewMvcModule {
    @Provides
    fun layoutInflater(context: Context): LayoutInflater {
        return LayoutInflater.from(context)
    }
}
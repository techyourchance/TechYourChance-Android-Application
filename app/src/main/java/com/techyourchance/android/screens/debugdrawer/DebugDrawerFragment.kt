package com.techyourchance.android.screens.debugdrawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.techyourchance.android.R
import com.techyourchance.android.common.restart.RestartAppUseCase
import com.techyourchance.android.common.settings.SettingsManager
import com.techyourchance.android.common.toasts.ToastsHelper
import com.techyourchance.android.screens.common.fragments.BaseFragment
import javax.inject.Inject


class DebugDrawerFragment: BaseFragment() {

    @Inject lateinit var settingsManager: SettingsManager
    @Inject lateinit var restartAppUseCase: RestartAppUseCase
    @Inject lateinit var toastsHelper: ToastsHelper

    private lateinit var viewGroupExample: ViewGroup
    private lateinit var checkExample: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_debug_drawer, container, false)
        with(view) {
            viewGroupExample = findViewById(R.id.viewGroupExample)
            checkExample = findViewById(R.id.checkExample)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        checkExample.isChecked = settingsManager.exampleBooleanSetting().value
        registerListeners()
    }

    override fun onStop() {
        super.onStop()
        unregisterListeners()
    }

    private fun registerListeners() {
        checkExample.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.exampleBooleanSetting().value = isChecked
        }
    }

    private fun unregisterListeners() {
        checkExample.setOnCheckedChangeListener(null)
    }


    companion object {
        fun newInstance(): DebugDrawerFragment {
            return DebugDrawerFragment()
        }
    }
}
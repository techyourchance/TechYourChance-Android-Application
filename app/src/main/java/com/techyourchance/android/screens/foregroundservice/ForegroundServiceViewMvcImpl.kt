package com.techyourchance.android.screens.foregroundservice

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.techyourchance.android.R
import com.techyourchance.android.backgroundwork.foregroundservice.ForegroundServiceState
import com.techyourchance.android.screens.common.toolbar.MyToolbar
import com.techyourchance.android.screens.common.widgets.MyButton

class ForegroundServiceViewMvcImpl(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?
): ForegroundServiceViewMvc() {

    private val toolbar: MyToolbar
    private val btnStartService: MyButton
    private val txtServiceState: TextView

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_foreground_service, parent, false))

        toolbar = findViewById(R.id.toolbar)
        btnStartService = findViewById(R.id.btnToggleBenchmark)
        txtServiceState = findViewById(R.id.txtServiceState)

        toolbar.setNavigateUpListener {
            listeners.map { it.onBackClicked() }
        }

        btnStartService.setOnClickListener{
            listeners.map { it.onToggleServiceClicked() }
        }
    }

    override fun bindServiceState(state: ForegroundServiceState) {
        when(state) {
            is ForegroundServiceState.Idle -> {
                btnStartService.text = getString(R.string.foreground_service_start)
                txtServiceState.text = getString(R.string.foreground_service_idle)
            }
            is ForegroundServiceState.Started -> {

                btnStartService.text = getString(R.string.foreground_service_stop)
                txtServiceState.text = getString(R.string.foreground_service_started, state.secondsStarted)
            }
            is ForegroundServiceState.Stopped -> {
                btnStartService.text = getString(R.string.foreground_service_start)
                txtServiceState.text = getString(R.string.foreground_service_stopped)
            }
        }
    }
}
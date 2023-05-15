package com.techyourchance.android.screens.workmanager

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.techyourchance.android.R
import com.techyourchance.android.backgroundwork.workmanager.MyWorkerState
import com.techyourchance.android.screens.common.toolbar.MyToolbar
import com.techyourchance.android.screens.common.widgets.MyButton

class WorkManagerViewMvcImpl(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?
): WorkManagerViewMvc() {

    private val toolbar: MyToolbar
    private val checkExpedited: CheckBox
    private val btnToggleWork: MyButton
    private val txtWorkState: TextView

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_work_manager, parent, false))

        toolbar = findViewById(R.id.toolbar)
        checkExpedited = findViewById(R.id.checkExpedited)
        btnToggleWork = findViewById(R.id.btnToggleWork)
        txtWorkState = findViewById(R.id.txtWorkState)

        toolbar.setNavigateUpListener {
            listeners.map { it.onBackClicked() }
        }

        btnToggleWork.setOnClickListener{
            listeners.map { it.onToggleWorkClicked() }
        }
    }

    override fun bindWorkerState(state: MyWorkerState) {
        when(state) {
            is MyWorkerState.Idle -> {
                btnToggleWork.text = getString(R.string.work_manager_worker_start)
                txtWorkState.text = getString(R.string.work_manager_worker_idle)
                checkExpedited.isEnabled = true
            }
            is MyWorkerState.Working -> {
                btnToggleWork.text = getString(R.string.work_manager_worker_stop)
                txtWorkState.text = getString(
                    R.string.work_manager_worker_working,
                    state.currentAttempt,
                    state.myWorkerConfig.maxRetries
                )
                checkExpedited.isEnabled = false
                checkExpedited.isChecked = state.myWorkerConfig.isExpedited
            }
            is MyWorkerState.Succeeded -> {
                btnToggleWork.text = getString(R.string.work_manager_worker_start)
                txtWorkState.text = getString(R.string.work_manager_worker_succeeded)
                checkExpedited.isEnabled = true
            }
            is MyWorkerState.Stopped -> {
                btnToggleWork.text = getString(R.string.work_manager_worker_start)
                txtWorkState.text = getString(R.string.work_manager_worker_stopped)
                checkExpedited.isEnabled = true
            }
        }
    }

    override fun getIsExpedited(): Boolean {
        return checkExpedited.isChecked
    }
}
package com.techyourchance.android.screens.ndkbasics

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import com.techyourchance.android.R
import com.techyourchance.android.screens.common.toolbar.MyToolbar
import com.techyourchance.android.screens.common.widgets.MyButton

class NdkBasicsViewMvcImpl(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?
): NdkBasicsViewMvc() {

    private val toolbar: MyToolbar
    private val edtArgument: EditText
    private val btnCompute: MyButton

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_ndk_basics, parent, false))

        toolbar = findViewById(R.id.toolbar)
        edtArgument = findViewById(R.id.edtArgument)
        btnCompute = findViewById(R.id.btnCompute)


        toolbar.setNavigateUpListener {
            listeners.map { it.onBackClicked() }
        }

        btnCompute.setOnClickListener{
            listeners.map { it.onComputeFibonacciClicked() }
        }
    }

    override fun getArgument(): Int {
        return try {
            edtArgument.text.toString().toInt()
        } catch (e: NumberFormatException) {
            -1
        }
    }
}
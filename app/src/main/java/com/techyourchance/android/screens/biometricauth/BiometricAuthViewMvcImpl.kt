package com.techyourchance.android.screens.biometricauth

import android.view.LayoutInflater
import android.view.ViewGroup
import com.techyourchance.android.R
import com.techyourchance.android.screens.common.toolbar.MyToolbar
import com.techyourchance.android.screens.common.widgets.MyButton

class BiometricAuthViewMvcImpl(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?
): BiometricAuthViewMvc() {

    private val toolbar: MyToolbar
    private val btnAuthenticate: MyButton

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_biometric_auth, parent, false))

        toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigateUpListener {
            listeners.map { it.onBackClicked() }
        }

        btnAuthenticate = findViewById(R.id.btnAuthenticate)

        btnAuthenticate.setOnClickListener{
            listeners.map { it.onAuthenticateClicked() }
        }
    }

}
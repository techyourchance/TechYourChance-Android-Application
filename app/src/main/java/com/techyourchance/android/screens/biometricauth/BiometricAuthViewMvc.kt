package com.techyourchance.android.screens.biometricauth

import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc

abstract class BiometricAuthViewMvc(): BaseObservableViewMvc<BiometricAuthViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
        fun onAuthenticateClicked()
    }


}
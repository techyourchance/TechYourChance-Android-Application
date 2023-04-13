package com.techyourchance.template.screens.biometricauth

import com.techyourchance.template.screens.common.mvcviews.BaseObservableViewMvc

abstract class BiometricAuthViewMvc(): BaseObservableViewMvc<BiometricAuthViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
        fun onAuthenticateClicked()
    }


}
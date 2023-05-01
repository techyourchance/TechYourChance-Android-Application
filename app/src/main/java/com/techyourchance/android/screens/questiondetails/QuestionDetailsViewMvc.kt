package com.techyourchance.android.screens.questiondetails

import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc

abstract class QuestionDetailsViewMvc(): BaseObservableViewMvc<QuestionDetailsViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
    }

    abstract fun bindQuestionBody(questionBody: String)
    abstract fun showProgressIndication()
    abstract fun hideProgressIndication()
}
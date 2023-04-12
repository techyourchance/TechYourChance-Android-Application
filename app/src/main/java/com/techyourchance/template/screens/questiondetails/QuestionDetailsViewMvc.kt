package com.techyourchance.template.screens.questiondetails

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.techyourchance.template.R
import com.techyourchance.template.screens.common.mvcviews.BaseObservableViewMvc
import com.techyourchance.template.screens.common.toolbar.MyToolbar
import com.techyourchance.template.screens.common.mvcviews.BaseViewMvc

abstract class QuestionDetailsViewMvc(): BaseObservableViewMvc<QuestionDetailsViewMvc.Listener>() {

    interface Listener {
        fun onBackClicked()
    }

    abstract fun bindQuestionBody(questionBody: String)
    abstract fun showProgressIndication()
    abstract fun hideProgressIndication()
}
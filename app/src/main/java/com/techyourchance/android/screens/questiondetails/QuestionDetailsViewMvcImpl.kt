package com.techyourchance.android.screens.questiondetails

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.techyourchance.android.R
import com.techyourchance.android.screens.common.toolbar.MyToolbar

class QuestionDetailsViewMvcImpl(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?
): QuestionDetailsViewMvc() {

    private val toolbar: MyToolbar
    private val swipeRefresh: SwipeRefreshLayout
    private val txtQuestionBody: TextView

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_question_details, parent, false))

        txtQuestionBody = findViewById(R.id.txt_question_body)

        // init toolbar
        toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigateUpListener {
            for (listener in listeners) {
                listener.onBackClicked()
            }
        }

        // init pull-down-to-refresh (used as a progress indicator)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        swipeRefresh.isEnabled = false
    }

    override fun bindQuestionBody(questionBody: String) {
        txtQuestionBody.text = Html.fromHtml(questionBody, Html.FROM_HTML_MODE_LEGACY)
    }

    override fun showProgressIndication() {
        swipeRefresh.isRefreshing = true
    }

    override fun hideProgressIndication() {
        if (swipeRefresh.isRefreshing) {
            swipeRefresh.isRefreshing = false
        }
    }
}
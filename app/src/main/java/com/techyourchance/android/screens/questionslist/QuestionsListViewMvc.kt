package com.techyourchance.android.screens.questionslist

import com.techyourchance.android.questions.Question
import com.techyourchance.android.screens.common.mvcviews.BaseObservableViewMvc

abstract class QuestionsListViewMvc(): BaseObservableViewMvc<QuestionsListViewMvc.Listener>() {

    interface Listener {
        fun onRefreshClicked()
        fun onQuestionClicked(clickedQuestion: Question)
    }

    abstract fun bindQuestions(questions: List<Question>)
    abstract fun showProgressIndication()
    abstract fun hideProgressIndication()
}
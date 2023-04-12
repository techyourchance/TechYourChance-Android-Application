package com.techyourchance.template.screens.questionslist

import com.techyourchance.template.questions.Question
import com.techyourchance.template.screens.common.mvcviews.BaseObservableViewMvc

abstract class QuestionsListViewMvc(): BaseObservableViewMvc<QuestionsListViewMvc.Listener>() {

    interface Listener {
        fun onRefreshClicked()
        fun onQuestionClicked(clickedQuestion: Question)
    }

    abstract fun bindQuestions(questions: List<Question>)
    abstract fun showProgressIndication()
    abstract fun hideProgressIndication()
}
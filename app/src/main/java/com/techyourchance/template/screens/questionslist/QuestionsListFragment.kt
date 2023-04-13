package com.techyourchance.template.screens.questionslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techyourchance.template.questions.FetchQuestionsUseCase
import com.techyourchance.template.questions.Question
import com.techyourchance.template.screens.common.ScreenSpec
import com.techyourchance.template.screens.common.ScreensNavigator
import com.techyourchance.template.screens.common.dialogs.DialogsNavigator
import com.techyourchance.template.screens.common.fragments.BaseFragment
import com.techyourchance.template.screens.common.mvcviews.ViewMvcFactory
import kotlinx.coroutines.*
import javax.inject.Inject

class QuestionsListFragment : BaseFragment(), QuestionsListViewMvc.Listener {

    @Inject lateinit var viewMvcFactory: ViewMvcFactory
    @Inject lateinit var fetchQuestionsUseCase: FetchQuestionsUseCase
    @Inject lateinit var dialogsNavigator: DialogsNavigator
    @Inject lateinit var screensNavigator: ScreensNavigator

    private lateinit var viewMvc: QuestionsListViewMvc

    private var loadedQuestions: List<Question>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewMvc = viewMvcFactory.newQuestionsListViewMvc(container)
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)
        if (loadedQuestions == null) {
            fetchQuestions()
        } else {
            viewMvc.bindQuestions(loadedQuestions!!)
        }
    }

    override fun onStop() {
        super.onStop()
        viewMvc.unregisterListener(this)
    }

    override fun onRefreshClicked() {
        fetchQuestions()
    }

    private fun fetchQuestions() {
        coroutineScope.launch {
            viewMvc.showProgressIndication()
            try {
                val result = fetchQuestionsUseCase.fetchLatestQuestions()
                when (result) {
                    is FetchQuestionsUseCase.Result.Success -> {
                        viewMvc.bindQuestions(result.questions)
                        loadedQuestions = result.questions
                    }
                    is FetchQuestionsUseCase.Result.Failure -> onFetchFailed()
                }
            } finally {
                viewMvc.hideProgressIndication()
            }
        }
    }

    private fun onFetchFailed() {
        dialogsNavigator.showServerErrorDialog(null)
    }

    override fun onQuestionClicked(clickedQuestion: Question) {
        screensNavigator.toScreen(ScreenSpec.StackOverflowQuestionDetails(clickedQuestion.id))
    }

    companion object {

        fun newInstance(): QuestionsListFragment {
            return QuestionsListFragment()
        }
    }
}
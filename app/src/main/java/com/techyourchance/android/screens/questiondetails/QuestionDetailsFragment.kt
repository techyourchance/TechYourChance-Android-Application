package com.techyourchance.android.screens.questiondetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techyourchance.android.questions.FetchQuestionDetailsUseCase
import com.techyourchance.android.screens.common.ScreenSpec
import com.techyourchance.android.screens.common.ScreensNavigator
import com.techyourchance.android.screens.common.dialogs.DialogsNavigator
import com.techyourchance.android.screens.common.fragments.BaseFragment
import com.techyourchance.android.screens.common.mvcviews.ViewMvcFactory
import kotlinx.coroutines.*
import javax.inject.Inject

class QuestionDetailsFragment : BaseFragment(), QuestionDetailsViewMvc.Listener {

    @Inject lateinit var viewMvcFactory: ViewMvcFactory
    @Inject lateinit var fetchQuestionDetailsUseCase: FetchQuestionDetailsUseCase
    @Inject lateinit var dialogsNavigator: DialogsNavigator
    @Inject lateinit var screensNavigator: ScreensNavigator

    private lateinit var viewMvc: QuestionDetailsViewMvc

    private lateinit var questionId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        controllerComponent.inject(this)
        super.onCreate(savedInstanceState)
        questionId = requireArguments().getString(ARG_QUESTION_ID)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewMvc = viewMvcFactory.newQuestionDetailsViewMvc(container)
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)
        fetchQuestionDetails()
    }

    override fun onStop() {
        super.onStop()
        viewMvc.unregisterListener(this)
    }

    private fun fetchQuestionDetails() {
        coroutineScope.launch {
            viewMvc.showProgressIndication()
            try {
                val result = fetchQuestionDetailsUseCase.fetchQuestion(questionId)
                when(result) {
                    is FetchQuestionDetailsUseCase.Result.Success -> {
                        viewMvc.bindQuestionBody(result.question.body)
                    }
                    is FetchQuestionDetailsUseCase.Result.Failure -> onFetchFailed()
                }
            } finally {
                viewMvc.hideProgressIndication()
            }

        }
    }

    private fun onFetchFailed() {
        dialogsNavigator.showServerErrorDialog(null)
    }

    override fun onBackClicked() {
        screensNavigator.navigateBack()
    }

    companion object {

        private const val ARG_QUESTION_ID = "ARG_QUESTION_ID"

        fun newInstance(screenSpec: ScreenSpec.StackOverflowQuestionDetails): QuestionDetailsFragment {
            val args = Bundle(2)
            args.putSerializable(ARG_QUESTION_ID, screenSpec.questionId)
            val fragment = QuestionDetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
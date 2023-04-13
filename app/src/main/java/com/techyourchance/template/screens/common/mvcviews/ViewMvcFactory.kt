package com.techyourchance.template.screens.common.mvcviews

import android.view.LayoutInflater
import android.view.ViewGroup
import com.techyourchance.template.common.imageloader.ImageLoader
import com.techyourchance.template.screens.biometricauth.BiometricAuthViewMvc
import com.techyourchance.template.screens.biometricauth.BiometricAuthViewMvcImpl
import com.techyourchance.template.screens.home.HomeViewMvc
import com.techyourchance.template.screens.home.HomeViewMvcImpl
import com.techyourchance.template.screens.questiondetails.QuestionDetailsViewMvc
import com.techyourchance.template.screens.questiondetails.QuestionDetailsViewMvcImpl
import com.techyourchance.template.screens.questionslist.QuestionsListViewMvc
import com.techyourchance.template.screens.questionslist.QuestionsListViewMvcImpl
import javax.inject.Inject
import javax.inject.Provider

class ViewMvcFactory @Inject constructor(
    private val layoutInflaterProvider: Provider<LayoutInflater>,
    private val imageLoaderProvider: Provider<ImageLoader>
) {

    private val inflater get() = layoutInflaterProvider.get()
    private val imageLoader get() = imageLoaderProvider.get()


    fun newHomeViewMvc(container: ViewGroup?): HomeViewMvc {
        return HomeViewMvcImpl(inflater, container)
    }

    fun newQuestionsListViewMvc(container: ViewGroup?): QuestionsListViewMvc {
        return QuestionsListViewMvcImpl(inflater, container)
    }

    fun newQuestionDetailsViewMvc(container: ViewGroup?): QuestionDetailsViewMvc {
        return QuestionDetailsViewMvcImpl(inflater, container)
    }

    fun newBiometricLockViewMvc(container: ViewGroup?): BiometricAuthViewMvc {
        return BiometricAuthViewMvcImpl(inflater, container)
    }
}
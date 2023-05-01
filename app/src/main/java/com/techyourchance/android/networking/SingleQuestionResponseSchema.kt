package com.techyourchance.android.networking

import com.google.gson.annotations.SerializedName
import com.techyourchance.android.questions.QuestionWithBody

data class SingleQuestionResponseSchema(@SerializedName("items") val questions: List<QuestionWithBody>) {
    val question: QuestionWithBody get() = questions[0]
}
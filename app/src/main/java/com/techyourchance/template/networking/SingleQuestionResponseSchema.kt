package com.techyourchance.template.networking

import com.google.gson.annotations.SerializedName
import com.techyourchance.template.questions.QuestionWithBody

data class SingleQuestionResponseSchema(@SerializedName("items") val questions: List<QuestionWithBody>) {
    val question: QuestionWithBody get() = questions[0]
}
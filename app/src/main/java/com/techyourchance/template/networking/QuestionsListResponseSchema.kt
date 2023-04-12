package com.techyourchance.template.networking

import com.google.gson.annotations.SerializedName
import com.techyourchance.template.questions.Question

data class QuestionsListResponseSchema(@SerializedName("items") val questions: List<Question>)
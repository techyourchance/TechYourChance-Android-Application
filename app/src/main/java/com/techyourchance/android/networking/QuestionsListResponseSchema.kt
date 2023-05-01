package com.techyourchance.android.networking

import com.google.gson.annotations.SerializedName
import com.techyourchance.android.questions.Question

data class QuestionsListResponseSchema(@SerializedName("items") val questions: List<Question>)
package com.techyourchance.template.questions

import com.google.gson.annotations.SerializedName

data class QuestionWithBody(
        @SerializedName("title") val title: String,
        @SerializedName("question_id") val id: String,
        @SerializedName("body") val body: String
)
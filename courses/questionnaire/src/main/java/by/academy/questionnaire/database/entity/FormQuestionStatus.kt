package by.academy.questionnaire.database.entity

data class FormQuestionStatus(
        val id: Long,
        val title: String,
        val questionCount: Int,
        var passedQuestionCount: Int,
)

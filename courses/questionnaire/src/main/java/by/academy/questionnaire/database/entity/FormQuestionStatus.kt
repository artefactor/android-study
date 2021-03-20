package by.academy.questionnaire.database.entity

data class FormQuestionStatus(
        val formId: Long,
        val title: String,
        val questionCount: Int,

        var passedQuestionCount: Int,
        var mainResultId: Long,
        var userId: Long,
        var countPasses:Int
)

package by.academy.questionnaire.database.entity

data class FormQuestionStatus(
        val formId: Long,
        val title: String,
        val icon: String,
        val questionCount: Int,

        var passedQuestionCount: Int,
        var mainResultId: Long,
        var userId: Long,
        var countPasses: Int,
) {
    fun clear() {
        passedQuestionCount = 0
        countPasses = 0
        mainResultId = 0
        userId = 0
    }
}

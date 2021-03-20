package by.academy.questionnaire.domain

import by.academy.questionnaire.database.entity.FormQuestionStatus

enum class FormStatus {
    NOT_STARTED,
    IN_PROCESS,
    FINISHED
}

fun convertToFormStatus(item: FormQuestionStatus): FormStatus {
    val inProcess = item.mainResultId > 0L
    if (inProcess) {
        return FormStatus.IN_PROCESS
    }
    val notStarted = item.countPasses == 0
    if (notStarted) {
        return FormStatus.NOT_STARTED
    }
    return FormStatus.FINISHED
}
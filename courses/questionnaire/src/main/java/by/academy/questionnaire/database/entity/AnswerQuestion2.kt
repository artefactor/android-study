package by.academy.questionnaire.database.entity

import androidx.room.Embedded

data class AnswerQuestion(
        @Embedded
        val question: QuestionEntity,

        @Embedded
        var answerEntity: AnswerEntity?,
)

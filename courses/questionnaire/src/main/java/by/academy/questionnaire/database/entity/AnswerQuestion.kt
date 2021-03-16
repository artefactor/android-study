package by.academy.questionnaire.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class AnswerQuestionRelation(
        @Embedded
        val question: QuestionEntity,

        @Relation(parentColumn = "q_id", entityColumn = "fk_q_id" )
        var answerEntity: AnswerEntity?,

)

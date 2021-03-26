package by.academy.questionnaire.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ResultUser(
        @Embedded
        val resultEntity: ResultEntity,

        @Relation(parentColumn = "fk_ur_id", entityColumn = "u_id", entity = UserEntity::class,
                projection = ["name"])
        val userName: String,
)
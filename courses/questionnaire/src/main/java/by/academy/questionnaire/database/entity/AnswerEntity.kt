package by.academy.questionnaire.database.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "answer",
        foreignKeys = [ForeignKey(
                entity = QuestionEntity::class,
                parentColumns = ["q_id"],
                childColumns = ["fk_q_id"],
                onDelete = ForeignKey.CASCADE
        ),
            ForeignKey(
                    entity = UserEntity::class,
                    parentColumns = ["u_id"],
                    childColumns = ["fk_u_id"],
                    onDelete = ForeignKey.CASCADE
            )
        ]
)

class AnswerEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "a_id")
        private var id: Long,
        @ColumnInfo(name = "fk_q_id")
        var questionId: Long = 0,
        val option: Int,
        val attempt: Int = 1,
        @ColumnInfo(name = "fk_u_id")
        var userId: Long = 1,
) : Parcelable, InfoEntity {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readLong(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeLong(id)
            writeLong(questionId)
            writeInt(option)
            writeInt(attempt)
            writeLong(userId)
        }
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<AnswerEntity> {
        override fun createFromParcel(parcel: Parcel): AnswerEntity {
            return AnswerEntity(parcel)
        }

        override fun newArray(size: Int): Array<AnswerEntity?> {
            return arrayOfNulls(size)
        }
    }

    override fun getId(): Long = id

    fun setId(id: Long) {
        this.id = id
    }
}
package by.academy.questionnaire.database.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "question",
        foreignKeys = [ForeignKey(
                entity = FormEntity::class,
                parentColumns = ["f_id"],
                childColumns = ["fk_f_id"],
                onDelete = ForeignKey.CASCADE
        )]
)
class QuestionEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "q_id")
        private var id: Long,
        @ColumnInfo(name = "fk_f_id")
        var formId: Long = 0,
        var index: Int = 0,
        val title: String,

        ) : Parcelable, InfoEntity {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readString().toString(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeLong(id)
            writeLong(formId)
            writeInt(index)
            writeString(title)
        }
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<QuestionEntity> {
        override fun createFromParcel(parcel: Parcel): QuestionEntity {
            return QuestionEntity(parcel)
        }

        override fun newArray(size: Int): Array<QuestionEntity?> {
            return arrayOfNulls(size)
        }
    }

    override fun getId(): Long = id

}
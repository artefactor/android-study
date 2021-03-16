package by.academy.questionnaire.database.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "result",
        foreignKeys = [ForeignKey(
                entity = FormEntity::class,
                parentColumns = ["f_id"],
                childColumns = ["fk_f_id"],
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
class ResultEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "r_id")
        private var id: Long,
        @ColumnInfo(name = "fk_f_id")
        var formId: Long = 0,
        // может блоб какой-нибудь в дальнейшем
        val result: String,
        // пока не использую но если будет юзер несколько раз проходить, то будет нужно
        val attempt: Int = 1,
        @ColumnInfo(name = "fk_u_id")
        var userId: Long = 1,
) : Parcelable, InfoEntity {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readLong(),
            parcel.readString().toString(),
            parcel.readInt(),
            parcel.readLong(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeLong(id)
            writeLong(formId)
            writeString(result)
            writeInt(attempt)
            writeLong(userId)
        }
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ResultEntity> {
        override fun createFromParcel(parcel: Parcel): ResultEntity {
            return ResultEntity(parcel)
        }

        override fun newArray(size: Int): Array<ResultEntity?> {
            return arrayOfNulls(size)
        }
    }

    override fun getId(): Long = id

    fun setId(id: Long) {
        this.id = id
    }
}
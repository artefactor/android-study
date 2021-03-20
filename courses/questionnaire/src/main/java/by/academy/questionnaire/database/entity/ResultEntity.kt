package by.academy.questionnaire.database.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import by.academy.questionnaire.database.Converters
import java.util.Date

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
                    childColumns = ["fk_ur_id"],
                    onDelete = ForeignKey.CASCADE
            )
        ]
)
class ResultEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "r_id")
        private var id: Long,
        @ColumnInfo(name = "fk_f_id")
        var formId: Long,
        @ColumnInfo(name = "fk_ur_id")
        var userId: Long,
        // может блоб какой-нибудь в дальнейшем
        var result: String,
        var dateStart: Date = Date(),
        var dateEnd: Date? = null,

) : Parcelable, InfoEntity {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readString().toString(),
            Converters().fromTimestamp(parcel.readLong())!!,
            Converters().fromTimestamp(parcel.readLong()),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeLong(id)
            writeLong(formId)
            writeLong(userId)
            writeString(result)
            writeLong(Converters().dateToTimestamp(dateStart) ?: -1L)
            writeLong(Converters().dateToTimestamp(dateEnd) ?: -1L)
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
package by.academy.lesson7.part2.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
        tableName = "work_info",
        foreignKeys = [ForeignKey(
                entity = CarInfoEntity::class,
                parentColumns = ["id"],
                childColumns = ["car_id"],
                onDelete = CASCADE
        )]
)
class WorkInfoEntity(
        @PrimaryKey(autoGenerate = true)
        private var id: Long,
        val date: Date,
        val title: String,
        val status: Int,
        val cost: Double,
        val description: String,

        @ColumnInfo(name = "car_id")
        var carId: Long = 0,
) : Parcelable, InfoEntity {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            Date(parcel.readLong()),
            parcel.readString().toString(),
            parcel.readInt(),
            parcel.readDouble(),
            parcel.readString().toString(),
            parcel.readLong()) {
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeLong(id)
            writeLong(date.time)
            writeString(title)
            writeInt(status)
            writeDouble(cost)
            writeString(description)
            writeLong(carId)
        }
    }

    companion object CREATOR : Parcelable.Creator<WorkInfoEntity> {
        override fun createFromParcel(parcel: Parcel): WorkInfoEntity {
            return WorkInfoEntity(parcel)
        }

        override fun newArray(size: Int): Array<WorkInfoEntity?> {
            return arrayOfNulls(size)
        }
    }

    override fun getId(): Long = id

    fun setId(newId: Long) {
        this.id = newId
    }
}
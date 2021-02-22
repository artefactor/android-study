package by.academy.lesson8.part2.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "car_info")
class CarInfoEntity(
        @PrimaryKey(autoGenerate = true)
        private var id: Long,
        val ownerName: String,
        val producer: String,
        val model: String,
        @ColumnInfo(name = "plate_number") val plateNumber: String,
        val imagePath: String?,

        ) : Parcelable, InfoEntity {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeLong(id)
            writeString(ownerName)
            writeString(producer)
            writeString(model)
            writeString(plateNumber)
            writeString(imagePath)
        }
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<CarInfoEntity> {
        override fun createFromParcel(parcel: Parcel): CarInfoEntity {
            return CarInfoEntity(parcel)
        }

        override fun newArray(size: Int): Array<CarInfoEntity?> {
            return arrayOfNulls(size)
        }
    }

    override fun getId(): Long = id

    fun setId(newId: Long) {
        this.id = newId
    }

}
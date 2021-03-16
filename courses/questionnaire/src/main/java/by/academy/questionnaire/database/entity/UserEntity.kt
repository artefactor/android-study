package by.academy.questionnaire.database.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")

class UserEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "u_id")
        private var id: Long,
        val name: String,
) : Parcelable, InfoEntity {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeLong(id)
            writeString(name)
        }
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<UserEntity> {
        override fun createFromParcel(parcel: Parcel): UserEntity {
            return UserEntity(parcel)
        }

        override fun newArray(size: Int): Array<UserEntity?> {
            return arrayOfNulls(size)
        }
    }

    override fun getId(): Long = id

    fun setId(id: Long) {
        this.id = id
    }
}
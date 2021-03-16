package by.academy.questionnaire.database.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "form")
class FormEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "f_id")
        private var id: Long,
        val title: String,
) : Parcelable, InfoEntity {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString().toString(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeLong(id)
            writeString(title)
        }
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<FormEntity> {
        override fun createFromParcel(parcel: Parcel): FormEntity {
            return FormEntity(parcel)
        }

        override fun newArray(size: Int): Array<FormEntity?> {
            return arrayOfNulls(size)
        }
    }

    override fun getId(): Long = id

}
package by.academy.lesson5.cars

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class DataItem(
        var ownerName: String?,
        var producer: String?,
        var model: String?,
        var plateNumber: String?,

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ownerName)
        parcel.writeString(producer)
        parcel.writeString(model)
        parcel.writeString(plateNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun copyFrom(item: DataItem) {
        this.ownerName = item.ownerName;
        this.producer = item.producer;
        this.model = item.model;
        this.plateNumber = item.plateNumber;
    }


    companion object CREATOR : Parcelable.Creator<DataItem> {
        override fun createFromParcel(parcel: Parcel): DataItem {
            return DataItem(parcel)
        }

        override fun newArray(size: Int): Array<DataItem?> {
            return arrayOfNulls(size)
        }
    }


}
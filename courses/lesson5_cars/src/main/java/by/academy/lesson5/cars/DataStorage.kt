package by.academy.lesson5.cars

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class DataStorage() : Parcelable {
    var items: List<DataItem?>? = object : ArrayList<DataItem?>() {
        init {
            add(DataItem("Ann", "Rolls Royce", "Phantom", "1112 HA-8"))
            add(DataItem("Bob", "Rolls Royce", "Phantom", "2222 HA-8"))
            add(DataItem("Conrad", "Ford", "Mustang", "3333 HA-8"))
            add(DataItem("Dorry", "Tesla", "Model Y", "4444 HA-8"))
//            add(DataItem("Ennio", "Toyota", "Corolla", "5555 HA-8"))
//            add(DataItem("Frank", "Volkswagen", "Golf", "6612 HA-8"))
        }
    }

    constructor(parcel: Parcel) : this() {
        items = parcel.createTypedArrayList(DataItem)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeList(items)
    }

    companion object CREATOR : Parcelable.Creator<DataStorage> {
        override fun createFromParcel(parcel: Parcel): DataStorage {
            return DataStorage(parcel)
        }

        override fun newArray(size: Int): Array<DataStorage?> {
            return arrayOfNulls(size)
        }
    }


}
package by.academy.lesson5.cars.data

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class MemoryDataStorage() : Parcelable, AbstractCarDataStorage {
    var items: List<CarInfoEntity?>? = object : ArrayList<CarInfoEntity?>() {
        init {
            add(CarInfoEntity(1L, "Ann", "Rolls Royce", "Phantom", "1112 HA-8", null))
            add(CarInfoEntity(2L, "Bob", "Rolls Royce", "Phantom", "2222 HA-8", null))
            add(CarInfoEntity(3L, "Conrad", "Ford", "Mustang", "3333 HA-8", null))
            add(CarInfoEntity(4L, "Dorry", "Tesla", "Model Y", "4444 HA-8", null))
            add(CarInfoEntity(5L, "Ennio", "Toyota", "Corolla", "5555 HA-8", null))
            add(CarInfoEntity(6L, "Frank", "Volkswagen", "Golf", "6612 HA-8", null))
        }
    }

    constructor(parcel: Parcel) : this() {
        items = parcel.createTypedArrayList(CarInfoEntity)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeList(items)
    }

    companion object CREATOR : Parcelable.Creator<MemoryDataStorage> {
        override fun createFromParcel(parcel: Parcel): MemoryDataStorage {
            return MemoryDataStorage(parcel)
        }

        override fun newArray(size: Int): Array<MemoryDataStorage?> {
            return arrayOfNulls(size)
        }
    }

    override fun getAllItems(): List<CarInfoEntity?> {
        return items!!.sortedBy { it?.producer?.toLowerCase() };
    }

    override fun add(item: CarInfoEntity?) {
        items.apply { add(item) }
    }

    override fun remove(item: CarInfoEntity?) {
        items.apply { remove(item) }
    }

    override fun update(item: CarInfoEntity?) {
        // its update itself
    }
}
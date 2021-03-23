package by.academy.questionnaire.domain

import android.os.Parcel
import android.os.Parcelable

data class FURContext(
        val formId: Long,
        var userId: Long,
        val resultId: Long,
): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readLong(),
            parcel.readLong()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(formId)
        parcel.writeLong(userId)
        parcel.writeLong(resultId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FURContext> {
        override fun createFromParcel(parcel: Parcel): FURContext {
            return FURContext(parcel)
        }

        override fun newArray(size: Int): Array<FURContext?> {
            return arrayOfNulls(size)
        }
    }
}

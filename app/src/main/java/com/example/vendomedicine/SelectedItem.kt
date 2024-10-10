package com.example.vendomedicine

import android.os.Parcel
import android.os.Parcelable

data class SelectedItem(
    val name: String,
    var quantity: Int // Change this from val to var
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(quantity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SelectedItem> {
        override fun createFromParcel(parcel: Parcel): SelectedItem {
            return SelectedItem(parcel)
        }

        override fun newArray(size: Int): Array<SelectedItem?> {
            return arrayOfNulls(size)
        }
    }
}

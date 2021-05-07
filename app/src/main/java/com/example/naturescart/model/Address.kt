package com.example.naturescart.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Address(
    @SerializedName("id") @Expose var id: Int? = null,
    @SerializedName("user_id") @Expose var userId: Int? = null,
    @SerializedName("address_nick") @Expose var addressNick: String? = null,
    @SerializedName("phone") @Expose var phone: String? = null,
    @SerializedName("city") @Expose var city: String? = null,
    @SerializedName("latitude") @Expose var latitude: Double? = null,
    @SerializedName("longitude") @Expose var longitude: Double? = null,
    @SerializedName("area") @Expose var area: String? = "",
    @SerializedName("appartment") @Expose var apartment: String? = "",
    @SerializedName("nearest_landmark") @Expose var nearestLandmark: String? = "",
    @SerializedName("building_name") @Expose var buildingName: String? = "",
    @SerializedName("first_name") @Expose var firstName: String? = "",
    @SerializedName("last_name") @Expose var lastName: String? = "",
    @SerializedName("default_address") @Expose var defaultAddress: Boolean = false,


    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as Boolean
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeValue(userId)
        parcel.writeString(addressNick)
        parcel.writeString(phone)
        parcel.writeString(city)
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
        parcel.writeString(area)
        parcel.writeString(apartment)
        parcel.writeString(nearestLandmark)
        parcel.writeString(buildingName)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeValue(defaultAddress)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(parcel)
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }
}
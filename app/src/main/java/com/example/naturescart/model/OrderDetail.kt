package com.example.naturescart.model

import android.os.Parcel
import android.os.Parcelable
import com.example.naturescart.model.CartDetail.Item.Summary

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class OrderDetail
    (
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("orderId")
    @Expose
    var orderId: String? = null,

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null,

    @SerializedName("subtotal")
    @Expose
    var subtotal: Float? = null,

    @SerializedName("instructions")
    @Expose
    var instructions: String = "",

    @SerializedName("total")
    @Expose
    var total: Float? = null,

    @SerializedName("payment_type")
    @Expose
    var paymentType: String? = null,

    @SerializedName("status") @Expose var status: String? = null,

    @SerializedName("order_date") @Expose var orderData: String? = null,
    @SerializedName("delivery_date") @Expose var deliveryDate: String? = null,
    @SerializedName("delivery_time") @Expose var deliveryTime: String? = null,
    @SerializedName("contactless_delivery") @Expose var contactLessDelivery: Boolean = false,
    @SerializedName("items") @Expose var items: ArrayList<CartDetail.Item>? = null,
    @SerializedName("address") @Expose var address: ArrayList<Address>? = ArrayList(),

    @SerializedName("summary")
    @Expose
    var summary: Summary? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readString()!!,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as Boolean,
        parcel.createTypedArrayList(CartDetail.Item),
        parcel.createTypedArrayList(Address),
        parcel.readParcelable(Summary::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(orderId)
        parcel.writeValue(userId)
        parcel.writeValue(subtotal)
        parcel.writeString(instructions)
        parcel.writeValue(total)
        parcel.writeString(paymentType)
        parcel.writeString(status)
        parcel.writeString(orderData)
        parcel.writeString(deliveryDate)
        parcel.writeString(deliveryTime)
        parcel.writeValue(contactLessDelivery)
        parcel.writeTypedList(items)
        parcel.writeTypedList(address)
        parcel.writeParcelable(summary, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderDetail> {
        override fun createFromParcel(parcel: Parcel): OrderDetail {
            return OrderDetail(parcel)
        }

        override fun newArray(size: Int): Array<OrderDetail?> {
            return arrayOfNulls(size)
        }
    }
}
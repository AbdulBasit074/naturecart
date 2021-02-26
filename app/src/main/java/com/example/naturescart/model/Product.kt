package com.example.naturescart.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


@Entity
class Product(

    @SerializedName("category_id")
    @Expose
    var categoryID: Long? = 0,

    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Long? = null,

    @SerializedName("item_id")
    @Expose
    var itemId: Long? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("purchase_rate")
    @Expose
    var purchaseRate: Int? = null,

    @SerializedName("sell_price")
    @Expose
    var sellPrice: Int? = null,

    @SerializedName("quantity")
    @Expose
    var quantity: Int? = null,

    @SerializedName("sku")
    @Expose
    var sku: String? = null,

    @SerializedName("unit")
    @Expose
    var unit: String? = null,

    @SerializedName("image")
    @Expose
    var image: String? = null,

    @SerializedName("description")
    @Expose
    var description: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(categoryID)
        parcel.writeValue(id)
        parcel.writeValue(itemId)
        parcel.writeString(name)
        parcel.writeValue(purchaseRate)
        parcel.writeValue(sellPrice)
        parcel.writeValue(quantity)
        parcel.writeString(sku)
        parcel.writeString(unit)
        parcel.writeString(image)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}
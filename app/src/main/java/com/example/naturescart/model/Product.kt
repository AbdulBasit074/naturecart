package com.example.naturescart.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
class Product(
    @PrimaryKey @SerializedName("id") var id: Long? = null,
    @SerializedName("item_id") var itemId: Long? = null,
    @SerializedName("category_id") var categoryID: Long? = 0,
    @SerializedName("category_name") var categoryName: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("purchase_rate") var purchaseRate: Float? = null,
    @SerializedName("sell_price") var sellPrice: Float? = null,
    @SerializedName("quantity") var quantity: Float? = null,
    @SerializedName("sku") var sku: String? = null,
    @SerializedName("unit") var unit: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("long_description") var long_description: String? = null,
    @SerializedName("country_code") var country_code: String? = null,
    @SerializedName("country_name") var country_name: String? = null,
    @SerializedName("factor") var factor: Float? = null,


    ):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Float::class.java.classLoader) as? Float
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeValue(itemId)
        parcel.writeValue(categoryID)
        parcel.writeString(categoryName)
        parcel.writeString(name)
        parcel.writeValue(purchaseRate)
        parcel.writeValue(sellPrice)
        parcel.writeValue(quantity)
        parcel.writeString(sku)
        parcel.writeString(unit)
        parcel.writeString(image)
        parcel.writeString(description)
        parcel.writeString(long_description)
        parcel.writeString(country_code)
        parcel.writeString(country_name)
        parcel.writeValue(factor)
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
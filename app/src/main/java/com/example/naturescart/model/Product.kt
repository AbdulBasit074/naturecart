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
    @SerializedName("quantity") var quantity: Int? = null,
    @SerializedName("sku") var sku: String? = null,
    @SerializedName("unit") var unit: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("description") var description: String? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(categoryID)
        parcel.writeValue(id)
        parcel.writeValue(itemId)
        parcel.writeString(categoryName)
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
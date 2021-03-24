package com.example.naturescart.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class CartDetail(
    @SerializedName("id")
    @Expose
    var id: Long? = null,

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null,

    @SerializedName("sub_total")
    @Expose
    var subTotal: Float? = null,

    @SerializedName("items")
    @Expose
    var items: List<Item>? = null,

    @SerializedName("summary")
    @Expose
    var summary: Item.Summary? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.createTypedArrayList(Item),
        parcel.readParcelable(Item.Summary::class.java.classLoader)
    )

    class Item(
        @SerializedName("id") var id: Long? = null,
        @SerializedName("cart_id") var cartId: Int? = null,
        @SerializedName("quantity") var quantity: Int? = null,
        @SerializedName("price") var price: Float? = null,
        @SerializedName("product") var product: Product? = null
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Float::class.java.classLoader) as? Float,
            parcel.readParcelable(Product::class.java.classLoader)
        ) {
        }

        class Summary(
            @SerializedName("sub_total")
            @Expose
            var subTotal: Float? = null,

            @SerializedName("delivery_charges")
            @Expose
            var deliveryChanges: Float? = null,

            @SerializedName("total_items")
            @Expose
            var totalItems: Int? = null

        ) : Parcelable {
            constructor(parcel: Parcel) : this(
                parcel.readValue(Float::class.java.classLoader) as? Float,
                parcel.readValue(Float::class.java.classLoader) as? Float,
                parcel.readValue(Int::class.java.classLoader) as? Int
            ) {
            }

            override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeValue(subTotal)
                parcel.writeValue(deliveryChanges)
                parcel.writeValue(totalItems)
            }

            override fun describeContents(): Int {
                return 0
            }

            companion object CREATOR : Parcelable.Creator<Summary> {
                override fun createFromParcel(parcel: Parcel): Summary {
                    return Summary(parcel)
                }

                override fun newArray(size: Int): Array<Summary?> {
                    return arrayOfNulls(size)
                }
            }

        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeValue(id)
            parcel.writeValue(cartId)
            parcel.writeValue(quantity)
            parcel.writeValue(price)
            parcel.writeParcelable(product, flags)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Item> {
            override fun createFromParcel(parcel: Parcel): Item {
                return Item(parcel)
            }

            override fun newArray(size: Int): Array<Item?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeValue(userId)
        parcel.writeValue(subTotal)
        parcel.writeTypedList(items)
        parcel.writeParcelable(summary, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartDetail> {
        override fun createFromParcel(parcel: Parcel): CartDetail {
            return CartDetail(parcel)
        }

        override fun newArray(size: Int): Array<CartDetail?> {
            return arrayOfNulls(size)
        }
    }


}
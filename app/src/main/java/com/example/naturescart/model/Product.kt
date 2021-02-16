package com.example.naturescart.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Product(
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
) {}
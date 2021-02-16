package com.example.naturescart.model

import com.google.gson.annotations.SerializedName

class CollectionModel(
    @SerializedName("id") var id: Long,
    @SerializedName("name") var name: String,
    @SerializedName("image") var image: String = "",
    @SerializedName("description") var description: String = "",
    @SerializedName("products") var products: List<Product> = ArrayList()
) {
}
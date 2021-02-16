package com.example.naturescart.model

import com.google.gson.annotations.SerializedName


class Category(
    @SerializedName("id") var id: Long? = null,

    @SerializedName("zoho_category_id") var zohoCategoryId: Long? = null,

    @SerializedName("name") var name: String? = null,

    @SerializedName("image") var image: String? = null,

    @SerializedName("description") var description: String? = null
){}
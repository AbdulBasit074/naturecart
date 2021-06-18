package com.example.naturescart.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
class Category(
    @PrimaryKey
    @SerializedName("id") var id: Long? = 0,
    @SerializedName("zoho_category_id") var zohoCategoryId: Long? = null,
    @SerializedName("name") var name: String? = "All",
    @SerializedName("image") var image: String? = null,
    @SerializedName("icon") var icon: String? = null,
    @SerializedName("description") var description: String? = null,

    ) {

}
package com.example.naturescart.model

import com.google.gson.annotations.SerializedName

data class Banner(
    @SerializedName("id") var id: Int,
    @SerializedName("image") var image: String
)
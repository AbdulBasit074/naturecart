package com.example.naturescart.model

import com.google.gson.annotations.SerializedName

class OnBoarding(
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("image") var image: String,
    @SerializedName("description") var description: String
) {
}
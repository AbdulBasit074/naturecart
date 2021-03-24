package com.example.naturescart.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity
class User(
    @PrimaryKey
    @SerializedName("id") var id: Int,
    @SerializedName("first_name") var firstName: String,
    @SerializedName("avatar") var avatar: String,
    @SerializedName("email") var email: String,
    @SerializedName("phone") var phone: String,
    @SerializedName("user_type") var userType: String,
    @SerializedName("access_token") var accessToken: String
) {

}

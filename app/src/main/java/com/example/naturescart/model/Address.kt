package com.example.naturescart.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Address(
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null,

    @SerializedName("address_nick")
    @Expose
    var addressNick: String? = null,

    @SerializedName("address")
    @Expose
    var address: String? = null,

    @SerializedName("phone")
    @Expose
    var phone: String? = null,

    @SerializedName("city")
    @Expose
    var city: String? = null,

    @SerializedName("latitude")
    @Expose
    var latitude: Double? = null,
    @SerializedName("longitude")
    @Expose
    var longitude: Double? = null
) {}
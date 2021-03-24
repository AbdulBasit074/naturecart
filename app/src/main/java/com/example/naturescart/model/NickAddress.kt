package com.example.naturescart.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class NickAddress(
    @SerializedName("type") @Expose var type: String? = "",
    @SerializedName("key") @Expose var key: String? = ""
) {}
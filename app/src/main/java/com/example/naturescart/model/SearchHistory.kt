package com.example.naturescart.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class SearchHistory (
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("user_id")
    @Expose
   var userId: Int? = null,

    @SerializedName("keyword")
    @Expose
    var keyword: String? = null
){}
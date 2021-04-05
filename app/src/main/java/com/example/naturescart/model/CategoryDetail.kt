package com.example.naturescart.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class CategoryDetail(
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("zoho_category_id")
    @Expose
    var zohoCategoryId: Long? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("parent_id")
    @Expose
    var parentId: Int? = null,

    @SerializedName("image")
    @Expose
    var image: String? = null,

    @SerializedName("description")
    @Expose
    var description: String? = null,
    @SerializedName("icon") var icon: String? = null,


    @SerializedName("products")
    @Expose
    var products: ArrayList<Product>? = null,

    @SerializedName("childs")
    @Expose
    var childs: ArrayList<Child>? = null
) {
    class Child(
        @SerializedName("id")
        @Expose
        var id: Long? = 0,

        @SerializedName("zoho_category_id")
        @Expose
        var zohoCategoryId: Long? = 0,

        @SerializedName("name")
        @Expose
        var name: String? = "All",

        @SerializedName("parent_id")
        @Expose
        var parentId: Int? = 0,

        @SerializedName("image")
        @Expose
        var image: String? = "",

        @SerializedName("description")
        @Expose
        var description: String? = ""
    ) {

    }
}
package com.example.naturescart.services.product

import com.example.naturescart.services.BaseService
import com.example.naturescart.services.Results
import com.example.naturescart.services.RetrofitClient

class ProductService(requestCode: Int, callBack: Results) : BaseService(requestCode, callBack) {

    fun addToFavourite(authToken: String, productID: Long) {
        RetrofitClient.getInstance().create(ProductClient::class.java).addToFavourite("Bearer $authToken", productID).enqueue(this)
    }

    fun getFavorites(authToken: String) {
        RetrofitClient.getInstance().create(ProductClient::class.java).getFavorites("Bearer $authToken").enqueue(this)
    }

}
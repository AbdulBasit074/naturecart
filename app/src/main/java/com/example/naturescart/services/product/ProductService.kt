package com.example.naturescart.services.product

import com.example.naturescart.services.BaseService
import com.example.naturescart.services.Results
import com.example.naturescart.services.RetrofitClient
import com.example.naturescart.services.data.DataClient

class ProductService(requestCode: Int, callBack: Results) : BaseService(requestCode, callBack) {


    fun addToFavourite(authToken:String) {
            RetrofitClient.getInstance().create(ProductClient::class.java).onFavourite("Bearer $authToken").enqueue(this)
    }

}
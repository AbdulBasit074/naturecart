package com.example.naturescart.services.order

import com.example.naturescart.services.BaseService
import com.example.naturescart.services.Results
import com.example.naturescart.services.RetrofitClient
import com.example.naturescart.services.product.OrderClient

class OrderService(requestCode: Int, callBack: Results) : BaseService(requestCode, callBack) {


    fun getOrders(authToken: String, limit: Int, page: Int) {
        RetrofitClient.getInstance().create(OrderClient::class.java)
            .getOrders("Bearer $authToken", limit, page).enqueue(this)
    }

}
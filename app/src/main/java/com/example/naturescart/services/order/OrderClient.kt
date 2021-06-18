package com.example.naturescart.services.product

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface OrderClient {


    @GET("orders")
    fun getOrders(
        @Header("Authorization") authToken: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Call<ResponseBody>


}
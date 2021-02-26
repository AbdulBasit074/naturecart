package com.example.naturescart.services.product

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface ProductClient {


    @FormUrlEncoded
    @POST("favourite")
    fun onFavourite(
        @Header("Authorization") authToken: String,
        @Field("product_id") productID: Long
    ): Call<ResponseBody>


}
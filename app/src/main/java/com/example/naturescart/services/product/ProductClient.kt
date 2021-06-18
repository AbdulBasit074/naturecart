package com.example.naturescart.services.product

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ProductClient {


    @FormUrlEncoded
    @POST("favourite")
    fun addToFavourite(
        @Header("Authorization") authToken: String,
        @Field("product_id") productID: Long
    ): Call<ResponseBody>




    @GET("favourite-detail")
    fun getFavorites(
        @Header("Authorization") authToken: String
    ): Call<ResponseBody>


}
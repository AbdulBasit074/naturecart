package com.example.naturescart.services.product

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ProductClient {



    @POST("auth/favourite")
    fun onFavourite(
        @Header("Authorization") authToken: String
        ): Call<ResponseBody>


}
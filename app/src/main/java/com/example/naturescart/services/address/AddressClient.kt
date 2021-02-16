package com.example.naturescart.services.address


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface AddressClient {


    @GET("address-nick")
    fun getNick(
        @Header("Authorization") authToken: String
    ): Call<ResponseBody>

    @GET("cities")
    fun getCities(
        @Header("Authorization") authToken: String
    ): Call<ResponseBody>

    @GET("addresses")
    fun getAddresses(
        @Header("Authorization") authToken: String
    ): Call<ResponseBody>

    @DELETE("addresses/{id}")
    fun deleteAddress(
        @Header("Authorization") authToken: String,
        @Path("id") id: Int
    ): Call<ResponseBody>


}
package com.example.naturescart.services.address


import com.example.naturescart.model.Address
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

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


    @POST("addresses")
    fun addAddress(
        @Header("Authorization") authToken: String,
        @Body body: Address
    ): Call<ResponseBody>

    @PUT("addresses/{id}")
    fun updateAddress(
        @Path("id") id: Int,
        @Header("Authorization") authToken: String,
        @Body body: Address
    ): Call<ResponseBody>


}
package com.example.naturescart.services.cart

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface CartClient {
    @FormUrlEncoded
    @POST("carts")
    fun addToCart(
        @Field("product_id") productId: Long,
        @Field("quantity") quantity: Int,
        @Field("cart_id") cartId: Long?
    ): Call<ResponseBody>


    @GET("carts")
    fun getCartDetail(
        @Query("cart_id") cartId: Long?
    ): Call<ResponseBody>




}


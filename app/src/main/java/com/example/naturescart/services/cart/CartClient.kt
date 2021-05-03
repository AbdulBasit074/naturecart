package com.example.naturescart.services.cart

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface CartClient {

    @FormUrlEncoded
    @POST("carts")
    fun addToCart(
        @Field("product_id") productId: Long,
        @Field("quantity") quantity: Float,
        @Field("cart_id") cartId: Long?
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("coupon/add")
    fun applyCoupon(
        @Header("Authorization") authToken: String,
        @Field("code") codeCoupon: String,
        @Field("cart_id") cartId: Long
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("coupon/remove")
    fun removeCoupon(
        @Header("Authorization") authToken: String,
        @Field("code") codeCoupon: String,
        @Field("cart_id") cartId: Long?
    ): Call<ResponseBody>


    @GET("carts")
    fun getCartDetail(@Query("cart_id") cartId: Long?): Call<ResponseBody>


    @GET("delivery/date-time")
    fun getDeliveryDateTime(@Query("date") date: String?): Call<ResponseBody>


    @FormUrlEncoded
    @POST("delivery-date-time/add")
    fun addDeliveryDateTime(
        @Field("delivery_date") date: String,
        @Field("delivery_time") time: String,
        @Field("cart_id") cartId: Long?
    ): Call<ResponseBody>

    @DELETE("carts/{productId}")
    fun removeFromCart(@Path("productId") productId: Long): Call<ResponseBody>

}


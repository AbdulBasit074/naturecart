package com.example.naturescart.services.auth


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface AuthClient {


    @FormUrlEncoded
    @POST("auth/register")
    fun userRegister(
        @Field("first_name") firstName: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("phone") phone: String

    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("auth/login")
    fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("device_token") deviceToken: String,
        @Field("device_type") deviceType: String
    ): Call<ResponseBody>


    @DELETE("auth/logout")
    fun logout(
        @Header("Authorization") authToken: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @PUT("update-profile")
    fun updateProfile(
        @Header("Authorization") authToken: String,
        @Field("username") userName: String,
        @Field("email") email: String,
        @Field("phone") phone: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("change-password")
    fun changePassword(
        @Header("Authorization") authToken: String,
        @Field("current_password") currentPassword: String,
        @Field("new_password") newPassword: String,
        @Field("confirm_password") confirmPassword: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("auth/send-otp")
    fun forgotPassword(
        @Field("email") email: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("auth/confirm-otp")
    fun verifyOTP(
        @Field("email") email: String,
        @Field("otp") otp: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @PUT("auth/reset-password")
    fun resetPassword(
        @Field("email") email: String,
        @Field("new_password") newPassword: String,
        @Field("confirm_password") confirmPassword: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("devices/add")
    fun addDevice(
        @Field("device_token") deviceToken: String,
        @Field("send_notifications") sendNotification: Boolean
    ): Call<ResponseBody>


}
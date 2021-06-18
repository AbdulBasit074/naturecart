package com.example.naturescart.services.auth

import com.example.naturescart.helper.Constants
import com.example.naturescart.services.BaseService
import com.example.naturescart.services.Results
import com.example.naturescart.services.RetrofitClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody


class AuthService(requestCode: Int, callBack: Results) : BaseService(requestCode, callBack) {

    fun userRegister(first: String, last: String, email: String, password: String, phone: String, gender: String?, nationality: String?) {
        RetrofitClient.getInstance().create(AuthClient::class.java)
            .userRegister(first,last, email, password, phone,gender,nationality).enqueue(this)
    }

    fun userLogin(email: String, password: String, deviceToken: String) {
        RetrofitClient.getInstance().create(AuthClient::class.java).userLogin(email, password, deviceToken, Constants.deviceType)
            .enqueue(this)
    }


    fun userLogout(authToken: String) {
        RetrofitClient.getInstance().create(AuthClient::class.java).logout("Bearer $authToken")
            .enqueue(this)
    }

    fun editProfile(authToken: String,first: String, last: String, email: String, phone: String, gender: String?, nationality: String?) {
        RetrofitClient.getInstance().create(AuthClient::class.java).updateProfile("Bearer $authToken", first,last, email, phone,gender,nationality)
            .enqueue(this)
    }

    fun changePassword(authToken: String, current: String, newPassword: String, confirmPassword: String) {
        RetrofitClient.getInstance().create(AuthClient::class.java).changePassword("Bearer $authToken", current, newPassword, confirmPassword)
            .enqueue(this)
    }

    fun forgotPassword(email: String) {
        RetrofitClient.getInstance().create(AuthClient::class.java).forgotPassword(email)
            .enqueue(this)
    }

    fun verifyOtp(email: String, otp: Int) {
        RetrofitClient.getInstance().create(AuthClient::class.java).verifyOTP(email, otp)
            .enqueue(this)
    }

    fun resetPassword(email: String, newPassword: String, confirmPassword: String) {
        RetrofitClient.getInstance().create(AuthClient::class.java).resetPassword(email, newPassword, confirmPassword)
            .enqueue(this)
    }

    fun addDevice(deviceToken: String, sendNotification: Boolean) {
        RetrofitClient.getInstance().create(AuthClient::class.java).addDevice(deviceToken, sendNotification)
            .enqueue(this)

    }

    fun uploadAvatar(byteArray: ByteArray, authToken: String) {
        val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/jpeg"), byteArray)
        val body = MultipartBody.Part.createFormData("avatar", "image.jpg", requestFile)

        RetrofitClient.getInstance().create(AuthClient::class.java).updateAvatar("Bearer $authToken", body)
            .enqueue(this)
    }


}
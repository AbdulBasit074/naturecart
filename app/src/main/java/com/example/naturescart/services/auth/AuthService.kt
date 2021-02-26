package com.example.naturescart.services.auth

import com.example.naturescart.helper.Constants
import com.example.naturescart.services.BaseService
import com.example.naturescart.services.Results
import com.example.naturescart.services.RetrofitClient


class AuthService(requestCode: Int, callBack: Results) : BaseService(requestCode, callBack) {

    fun userRegister(fullName: String, email: String, password: String, phone: String) {
        RetrofitClient.getInstance().create(AuthClient::class.java)
            .userRegister(fullName, email, password, phone).enqueue(this)
    }

    fun userLogin(email: String, password: String) {
        RetrofitClient.getInstance().create(AuthClient::class.java).userLogin(email, password,Constants.fcmToken,Constants.deviceType)
            .enqueue(this)
    }


    fun userLogout(authToken:String) {
        RetrofitClient.getInstance().create(AuthClient::class.java).logout("Bearer $authToken")
            .enqueue(this)
    }
    fun editProfile(authToken:String, name:String,email: String,phone:String) {
        RetrofitClient.getInstance().create(AuthClient::class.java).updateProfile("Bearer $authToken",name,email,phone)
            .enqueue(this)
    }
    fun changePassword(authToken:String, current:String,newPassword: String,confirmPassword:String) {
        RetrofitClient.getInstance().create(AuthClient::class.java).changePassword("Bearer $authToken",current,newPassword,confirmPassword)
            .enqueue(this)
    }

}
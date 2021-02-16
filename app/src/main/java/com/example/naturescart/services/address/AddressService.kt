package com.example.naturescart.services.address

import com.example.naturescart.services.BaseService
import com.example.naturescart.services.Results
import com.example.naturescart.services.RetrofitClient

class AddressService(requestCode: Int, callBack: Results) : BaseService(requestCode, callBack) {


    fun getNickAddress(authToken: String) {
        RetrofitClient.getInstance().create(AddressClient::class.java).getNick("Bearer $authToken")
            .enqueue(this)
    }

    fun getCities(authToken: String) {
        RetrofitClient.getInstance().create(AddressClient::class.java)
            .getCities("Bearer $authToken").enqueue(this)
    }

    fun updateAddress(authToken: String) {
        RetrofitClient.getInstance().create(AddressClient::class.java)
            .getCities("Bearer $authToken").enqueue(this)
    }

    fun addAddress(authToken: String) {
        RetrofitClient.getInstance().create(AddressClient::class.java)
            .getCities("Bearer $authToken").enqueue(this)
    }

    fun getAddress(authToken: String) {
        RetrofitClient.getInstance().create(AddressClient::class.java)
            .getAddresses("Bearer $authToken").enqueue(this)
    }

    fun deleteAddress(authToken: String, id: Int) {
        RetrofitClient.getInstance().create(AddressClient::class.java)
            .deleteAddress("Bearer $authToken", id).enqueue(this)
    }


}
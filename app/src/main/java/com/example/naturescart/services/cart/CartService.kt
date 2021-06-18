package com.example.naturescart.services.cart

import com.example.naturescart.services.BaseService
import com.example.naturescart.services.Results
import com.example.naturescart.services.RetrofitClient

class CartService(requestCode: Int, callBack: Results) : BaseService(requestCode, callBack) {

    fun addToCart(productId: Long, quantity: Float, cartId: Long?) {
        RetrofitClient.getInstance().create(CartClient::class.java)
            .addToCart(productId, quantity, cartId)
            .enqueue(this)
    }

    fun applyCoupon(authToken: String, couponCode: String, cartId: Long) {
        RetrofitClient.getInstance().create(CartClient::class.java)
            .applyCoupon("Bearer $authToken", couponCode, cartId)
            .enqueue(this)
    }

    fun removeCoupon(authToken: String, couponCode: String, cartId: Long) {
        RetrofitClient.getInstance().create(CartClient::class.java)
            .removeCoupon("Bearer $authToken", couponCode, cartId)
            .enqueue(this)
    }

    fun getCartDetail(cartId: Long?) {
        RetrofitClient.getInstance().create(CartClient::class.java)
            .getCartDetail(cartId)
            .enqueue(this)
    }

    fun getDeliveryDateTime(keyDate: String?) {
        RetrofitClient.getInstance().create(CartClient::class.java)
            .getDeliveryDateTime(keyDate)
            .enqueue(this)
    }
    fun addDeliveryDateTime(date: String,time:String,cartId: Long) {
        RetrofitClient.getInstance().create(CartClient::class.java)
            .addDeliveryDateTime(date,time,cartId)
            .enqueue(this)
    }
    fun addAddInstruction(instruction: String,cartId: Long) {
        RetrofitClient.getInstance().create(CartClient::class.java)
            .addInstruction(cartId,instruction)
            .enqueue(this)
    }



    fun removeFromCart(itemId: Long) {
        RetrofitClient.getInstance().create(CartClient::class.java)
            .removeFromCart(itemId)
            .enqueue(this)
    }
}
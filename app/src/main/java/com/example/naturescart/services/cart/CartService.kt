package com.example.naturescart.services.cart

import com.example.naturescart.services.BaseService
import com.example.naturescart.services.Results
import com.example.naturescart.services.RetrofitClient

class CartService(requestCode: Int, callBack: Results) : BaseService(requestCode, callBack) {

    fun addToCart(productId: Long, quantity: Int, cartId: Long?) {
        RetrofitClient.getInstance().create(CartClient::class.java)
            .addToCart(productId, quantity, cartId)
            .enqueue(this)
    }


    fun getCartDetail(cartId: Long?) {
        RetrofitClient.getInstance().create(CartClient::class.java)
            .getCartDetail(cartId)
            .enqueue(this)
    }


    fun removeFromCart(itemId: Long) {
        RetrofitClient.getInstance().create(CartClient::class.java)
            .removeFromCart(itemId)
            .enqueue(this)
    }


}
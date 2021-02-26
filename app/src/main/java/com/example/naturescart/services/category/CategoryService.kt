package com.example.naturescart.services.category

import com.example.naturescart.services.BaseService
import com.example.naturescart.services.Results
import com.example.naturescart.services.RetrofitClient

class CategoryService(requestCode: Int, callBack: Results) : BaseService(requestCode, callBack) {

    fun getCategory(categoryID: Long, limit: Int, withProducts: Boolean, isChild: Boolean,pageNo:Int) {
        RetrofitClient.getInstance().create(CategoryClient::class.java)
            .getCategory(categoryID, limit, withProducts, isChild,pageNo).enqueue(this)
    }
}
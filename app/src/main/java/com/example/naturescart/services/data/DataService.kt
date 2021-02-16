package com.example.naturescart.services.data

import com.example.naturescart.services.BaseService
import com.example.naturescart.services.Results
import com.example.naturescart.services.RetrofitClient

class DataService(requestCode: Int, callBack: Results) : BaseService(requestCode, callBack) {


    fun onBoarding() {
        RetrofitClient.getInstance().create(DataClient::class.java).onBoarding().enqueue(this)
    }

    fun getCollections(pageNo:Int, limit: Int) {
        RetrofitClient.getInstance().create(DataClient::class.java).getCollections(pageNo, limit).enqueue(this)
    }
    fun getCategories(limit: Int,withProduct:Boolean) {
        RetrofitClient.getInstance().create(DataClient::class.java).getCategories(limit,withProduct).enqueue(this)
    }
    fun getCategoryProducts(limit: Int,withProduct:Boolean) {
        RetrofitClient.getInstance().create(DataClient::class.java).getCategories(limit,withProduct).enqueue(this)
    }
    fun getUserHistory(authToken:String) {
        RetrofitClient.getInstance().create(DataClient::class.java).getUserHistory("Bearer $authToken").enqueue(this)
    }

    fun getSearchResult(authToken:String,keyWord:String,limit:Int,pageNo:Int) {
        RetrofitClient.getInstance().create(DataClient::class.java).getSearchResult("Bearer $authToken",keyWord,limit,pageNo).enqueue(this)
    }


}
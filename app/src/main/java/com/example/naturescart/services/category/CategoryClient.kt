package com.example.naturescart.services.category

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CategoryClient {


    @GET("category/{id}")
    fun getCategory(
        @Path("id") long: Long,
        @Query("limit") limit:Int,
        @Query("with_products") withProducts:Boolean,
        @Query("is_child")isChild: Boolean,
        @Query("page")pageNo: Int
    ): Call<ResponseBody>

}
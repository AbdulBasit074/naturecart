package com.example.naturescart.services.data

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface DataClient {

    @GET("onboardings")
    fun onBoarding(): Call<ResponseBody>

    @GET("collections")
    fun getCollections(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Call<ResponseBody>

    @GET("collection/{collectionId}")
    fun getCollectionProducts(
        @Path("collectionId") collectionId: Long,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Call<ResponseBody>

    @GET("categories")
    fun getCategories(
        @Query("limit") limit: Int,
        @Query("with_products") withProducts: Boolean
    ): Call<ResponseBody>

    @GET("categories")
    fun getCategoryProducts(
        @Query("limit") limit: Int,
        @Query("with_products") withProducts: Boolean
    ): Call<ResponseBody>

    @GET("search-history")
    fun getUserHistory(
        @Header("Authorization") authToken: String
    ): Call<ResponseBody>


    @GET("search")
    fun getSearchResult(
        @Header("Authorization") authToken: String?,
        @Query("keyword") keyWord: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Call<ResponseBody>


    @GET("new-arrival")
    fun getNewArrivalProducts(): Call<ResponseBody>



    @GET("frequently-purchased")
    fun getFrequentlyPurchasedProducts(
        @Header("Authorization") authToken: String?, ): Call<ResponseBody>


}
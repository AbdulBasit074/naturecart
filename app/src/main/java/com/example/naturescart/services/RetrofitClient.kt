package com.example.naturescart.services


import com.example.naturescart.helper.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient {

    companion object {
        private const val timeOut = 10L
        private const val extendedTimeOut = 20L
        private var instance: Retrofit? = null
        private var extendedTimeoutInstance: Retrofit? = null


        fun getInstance(): Retrofit {

            if (instance == null) {
                val clientBuilder = OkHttpClient.Builder()
                clientBuilder.connectTimeout(timeOut, TimeUnit.SECONDS)
                clientBuilder.readTimeout(timeOut, TimeUnit.SECONDS)
                clientBuilder.writeTimeout(timeOut, TimeUnit.SECONDS)
                clientBuilder.addInterceptor(HeaderInterceptor())
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                clientBuilder.addInterceptor(loggingInterceptor)
                instance =
                    Retrofit.Builder().baseUrl(Constants.baseUrl).client(clientBuilder.build())
                        .addConverterFactory(GsonConverterFactory.create()).build()
            }
            return instance!!
        }

        fun getInstanceWithExtendedTimeout(): Retrofit {
            if (extendedTimeoutInstance == null) {
                val clientBuilder = OkHttpClient.Builder()
                clientBuilder.connectTimeout(extendedTimeOut, TimeUnit.SECONDS)
                clientBuilder.readTimeout(extendedTimeOut, TimeUnit.SECONDS)
                clientBuilder.writeTimeout(extendedTimeOut, TimeUnit.SECONDS)

                clientBuilder.addInterceptor(HeaderInterceptor())

                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                clientBuilder.addInterceptor(loggingInterceptor)

                val genericHeader = Interceptor {
                    val request = it.request().newBuilder()
                        .addHeader("Accept", "application/json").build()
                    return@Interceptor it.proceed(request)
                }
                clientBuilder.addInterceptor(genericHeader)

                extendedTimeoutInstance =
                    Retrofit.Builder().baseUrl(Constants.baseUrl).client(clientBuilder.build())
                        .build()

            }
            return extendedTimeoutInstance!!
        }
    }

    class HeaderInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val newRequest = request.newBuilder()
                .addHeader("Content-type","application/json")
                .addHeader("Accept", "application/json")
                .addHeader("language", "en")
                .build()
            return chain.proceed(newRequest)
        }

    }


}
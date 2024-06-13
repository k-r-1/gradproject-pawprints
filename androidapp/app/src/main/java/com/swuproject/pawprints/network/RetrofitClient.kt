package com.swuproject.pawprints.network

import com.swuproject.pawprints.network.RetrofitService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://34.135.2.113:8080" // Spring Boot 서버 URL
    private const val FLASK_BASE_URL = "https://pawprintssightreport-3v3tlfcd4q-uc.a.run.app/" // Flask 서버 URL

    private val httpClient = OkHttpClient.Builder()
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()

    fun getRetrofitService(): RetrofitService {
        return retrofit.create(RetrofitService::class.java)
    }

    fun getFlaskRetrofitService(): RetrofitService {
        val flaskHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.MINUTES)
            .readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .build()

        val flaskRetrofit = Retrofit.Builder()
            .baseUrl(FLASK_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(flaskHttpClient)
            .build()
        return flaskRetrofit.create(RetrofitService::class.java)
    }
}

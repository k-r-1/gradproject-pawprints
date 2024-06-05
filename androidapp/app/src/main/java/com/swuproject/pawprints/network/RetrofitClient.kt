package com.swuproject.pawprints.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://34.135.2.113:8080" // Spring Boot 서버 URL
    private const val FLASK_BASE_URL = "https://pawprintssightreport-3v3tlfcd4q-uc.a.run.app/" // Flask 서버 URL

    fun getRetrofitService(): RetrofitService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(RetrofitService::class.java)
    }

    fun getFlaskRetrofitService(): RetrofitService {
        val retrofit = Retrofit.Builder()
            .baseUrl(FLASK_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(RetrofitService::class.java)
    }
}
package com.swuproject.pawprints.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://34.135.2.113:8080" // Spring Boot 서버 URL
    private const val FLASK_BASE_URL = "https://pawprintssightreport-3v3tlfcd4q-uc.a.run.app/" // Flask 서버 URL

    // Spring Boot 서버와 통신하기 위한 Retrofit 서비스 생성
    fun getRetrofitService(): RetrofitService {
        val ret
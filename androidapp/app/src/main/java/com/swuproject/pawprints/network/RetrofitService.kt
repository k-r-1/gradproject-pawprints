package com.swuproject.pawprints.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitService {
    @POST("/api/users/signup")
    fun signup(@Body requestBody: Map<String, String>): Call<Map<String, String>>

    @POST("/api/users/signin")
    fun signin(@Body requestBody: Map<String, String>): Call<Map<String, String>>
}
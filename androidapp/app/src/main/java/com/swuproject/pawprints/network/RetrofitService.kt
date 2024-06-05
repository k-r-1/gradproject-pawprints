package com.swuproject.pawprints.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService {
    @POST("/api/users/signup")
    fun signup(@Body requestBody: Map<String, String>): Call<Map<String, String>>

    @P
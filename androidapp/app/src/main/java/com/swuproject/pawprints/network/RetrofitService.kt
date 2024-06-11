package com.swuproject.pawprints.network

import com.swuproject.pawprints.dto.SightReportResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService {
    @POST("/api/users/signup")
    fun signup(@Body requestBody: Map<String, String>): Call<Map<String, String>>

    @POST("/api/users/signin")
    fun signin(@Body requestBody: Map<String, String>): Call<Map<String, String>>

    @PUT("/api/users/update")
    fun updateUser(@Body userUpdates: Map<String, String>): Call<Void>

    @GET("/api/users/check-id")
    fun checkUserId(@Query("userId") userId: String): Call<Map<String, String>>

    @GET("/api/users/check-email")
    fun checkUserEmail(@Query("userEmail") userEmail: String): Call<Map<String, String>>

    @GET("/api/pets/{userId}")
    fun getPetsByUserId(@Path("userId") userId: String): Call<List<Pet>>

    @GET("/api/lost_reports/{petId}")
    fun getLostReportByPetId(@Path("petId") petId: Int): Call<LostReports>

    @GET("/api/pets/{userId}/lost")
    fun getLostPetsByUserId(@Path("userId") userId: String): Call<List<Pet>>

    @POST("/api/matching/find_similar_sightings")
    fun findSimilarSightings(@Body requestBody: Map<String, String>): Call<List<SimilarSighting>>

    @GET("/api/lostReports")
    fun getLostReports(): Call<List<LostReportResponse>>

    @GET("/api/sightReports")
    fun getSightReports(): Call<List<SightReportResponse>>

    @POST("/api/pets/{petId}")
    fun updatePet(@Path("petId") petId: Int, @Body pet: Pet): Call<Void>

    @DELETE("/api/pets/{petId}")
    fun deletePet(@Path("petId") petId: Int): Call<Void>

    @POST("/api/pets")
    fun addPet(@Body pet: Pet): Call<Void>

}

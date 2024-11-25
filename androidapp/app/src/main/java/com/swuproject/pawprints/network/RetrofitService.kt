package com.swuproject.pawprints.network

import com.swuproject.pawprints.dto.LostReportEdit
import com.swuproject.pawprints.dto.LostReportResponse
import com.swuproject.pawprints.dto.SightReportEdit
import com.swuproject.pawprints.dto.SightReportResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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
    fun getLostReportByPetId(@Path("petId") petId: Int): Call<LostReportResponse>

    @GET("/api/pets/{userId}/lost")
    fun getLostPetsByUserId(@Path("userId") userId: String): Call<List<Pet>>

    //@POST("/api/matching/find_similar_sightings")
    //fun findSimilarSightings(@Body requestBody: Map<String, String>): Call<List<SimilarSighting>>

    @POST("/find_similar_sightings")
    fun findSimilarSightings(@Body requestBody: Map<String, String>): Call<List<SimilarSighting>>

    @GET("/api/lostReports")
    fun getLostReports(): Call<List<LostReportResponse>>

    @GET("/api/lostReports/{lostId}")
    fun getLostReportByLostId(@Path("lostId") lostId: Int): Call<List<LostReportResponse>>

    @DELETE("/api/lostReports/{lostId}")
    fun deleteLostReports(@Path("lostId") lostId: Int): Call<Void>

    @GET("/api/sightReports")
    fun getSightReports(): Call<List<SightReportResponse>>

    @GET("/api/sightReports/{sightId}")
    fun getSightReportBySightId(@Path("sightId") sightId: Int): Call<List<SightReportResponse>>

    @DELETE("/api/sightReports/{sightId}")
    fun deleteSightReports(@Path("sightId") sightId: Int): Call<Void>

    @Multipart
    @POST("/api/sightReportsPost")
    fun createSightReport(
        @Part files: MultipartBody.Part,
        @Part("userId") userId: RequestBody,
        @Part("sightTitle") sightTitle: RequestBody,
        @Part("sightType") sightType: RequestBody,
        @Part("sightBreed") sightBreed: RequestBody,
        @Part("sightAreaLat") sightAreaLat: RequestBody,
        @Part("sightAreaLng") sightAreaLng: RequestBody,
        @Part("sightDate") sightDate: RequestBody,
        @Part("sightLocation") sightLocation: RequestBody,
        @Part("sightDescription") sightDescription: RequestBody,
        @Part("sightContact") sightContact: RequestBody
    ): Call<SightReportResponse>

    @PUT("sight_reports/edit/{sightId}")
    fun updateSightReport(
        @Path("sightId") sightId: Int,
        @Body sightReportEdit: SightReportEdit
    ): Call<Void>

    @Multipart
    @POST("/api/lostReportsPost")
    fun createLostReport(
        @Part files: MultipartBody.Part,
        @Part("petId") petId: RequestBody,
        @Part("lostTitle") lostTitle: RequestBody,
        @Part("petType") petType: RequestBody,
        @Part("lostAreaLat") lostAreaLat: RequestBody,
        @Part("lostAreaLng") lostAreaLng: RequestBody,
        @Part("lostDate") lostDate: RequestBody,
        @Part("lostLocation") lostLocation: RequestBody,
        @Part("lostDescription") lostDescription: RequestBody,
        @Part("lostContact") lostContact: RequestBody
    ): Call<LostReportResponse>

    @PUT("lost_reports/edit/{lostId}")
    fun updateLostReport(
        @Path("lostId") lostId: Int,
        @Body lostReportEdit: LostReportEdit
    ): Call<Void>

    @POST("/api/pets/{petId}")
    fun updatePet(@Path("petId") petId: Int, @Body pet: Pet): Call<Void>

    @DELETE("/api/pets/{petId}")
    fun deletePet(@Path("petId") petId: Int): Call<Void>

    @POST("/api/pets")
    fun addPet(@Body pet: Pet): Call<Void>

    @POST("/api/users/confirm-password")
    fun confirmPassword(@Body requestBody: Map<String, String>): Call<Void>

    @DELETE("/api/users/{userId}")
    fun deleteUser(@Path("userId") userId: String): Call<Void>


}

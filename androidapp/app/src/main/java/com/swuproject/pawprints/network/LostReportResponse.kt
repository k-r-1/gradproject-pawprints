package com.swuproject.pawprints.network

data class LostReportResponse(
    val lostId: Long,
    val petId: Long,
    val lostTitle: String,
    val lostAreaLat: Double,
    val lostAreaLng: Double,
    val lostDate: String,
    val lostLocation: String,
    val lostDescription: String,
    val lostContact: String,
    val lostBreed: String,
    val lostGender: String,
    val images: List<LostReportsImage>
)

package com.swuproject.pawprints.network

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference

data class LostReports(
    val lostId: Long,
    val petId: Long,
    val lostTitle: String,
    val lostAreaLat: Double,
    val lostAreaLng: Double,
    val lostDate: String,
    val lostLocation: String,
    val lostFeature: String,
    val lostDescription: String,
    val lostContact: String,
    @JsonManagedReference
    val images: List<LostReportsImage>
)


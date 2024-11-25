package com.swuproject.pawprints.dto

data class SightReportEdit(
    val sightId: Int,
    val title: String,
    val breed: String,
    val date: String,
    val location: String,
    val feature: String,
    val contact: String,
    val sightAreaLat: Double?,
    val sightAreaLng: Double?
)

package com.swuproject.pawprints.dto

data class LostReportEdit(
    val lostId: Int,
    val title: String,
    val date: String,
    val location: String,
    val feature: String,
    val contact: String,
    val lostAreaLat: Double?,
    val lostAreaLng: Double?
)

package com.swuproject.pawprints.dto

data class LostReportResponse(
    val lostId: Int,
    val petID: Int,
    val lostTitle: String?,
    val lostAreaLat: Double?,
    val lostAreaLng: Double?,
    val lostDate: String?, // 날짜 형식이 다를 수 있으므로 문자열로 받습니다.
    val lostLocation: String?,
    val lostDescription: String?,
    val lostContact: String,
    val lostBreed: String,
    val lostGender: String,
    val lostAge: Int,
    val lostImages: List<LostReportImageResponse>
)

data class LostReportImageResponse(
    val lostImageId: Int,
    val lostImagePath: String?
)

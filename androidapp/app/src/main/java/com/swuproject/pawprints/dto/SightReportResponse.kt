package com.swuproject.pawprints.dto

data class SightReportResponse(
    val sightId: Int,
    val sightTitle: String?,
    val sightBreed: String?,
    val sightAreaLat: Double?,
    val sightAreaLng: Double?,
    val sightDate: String?, // 날짜 형식이 다를 수 있으므로 문자열로 받습니다.
    val sightLocation: String?,
    val sightDescription: String?,
    val sightImages: List<SightReportImageResponse>
)

data class SightReportImageResponse(
    val sightImageId: Int,
    val sightImagePath: String?
)

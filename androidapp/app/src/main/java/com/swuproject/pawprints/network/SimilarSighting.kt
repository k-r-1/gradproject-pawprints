package com.swuproject.pawprints.network

import java.io.Serializable

data class SimilarSighting(
    val detection_score: Float,
    val final_score: Float,
    val similarity_score: Float,
    val sight_breed: String,
    val sight_date: String,
    val sight_description: String,
    val sight_image_path: String,
    val sight_location: String,
    val sight_title: String,
    val sight_id: Int,
    val user_id: String,
    val latitude: Double?, // 추가
    val longitude: Double?, // 추가
    val sight_contact: String  // 2024.10.20 추가
) : Serializable

package com.swuproject.pawprints.network

data class LostReport(
    val id: Int,
    val petId: Int,
    val title: String,
    val location: String,
    val date: String,
    val description: String,
    val contact: String
)
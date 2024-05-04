package com.swuproject.pawprints.ui.home

import android.graphics.drawable.Drawable
import java.util.Date

data class LostRecyclerData(
    var lost_photo: Drawable?,
    var lost_title: String,
    val lost_breed: String,
    val lost_gender: String,
    val lost_area: String,
    val lost_date: String,
    val lost_location: String,
    val lost_feature:String,
    val lost_contact: String
)

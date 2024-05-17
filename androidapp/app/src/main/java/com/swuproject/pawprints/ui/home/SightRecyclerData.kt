package com.swuproject.pawprints.ui.home

import android.graphics.drawable.Drawable
import java.util.Date

data class SightRecyclerData(
    var sight_photo: Drawable?,
    var sight_title: String,
    val sight_breed: String,
    val sight_area: String,
    val sight_date: String,
    val sight_location: String,
    val sight_feature:String
)

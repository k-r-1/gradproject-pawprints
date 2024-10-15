package com.swuproject.pawprints.ui.poster

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PosterViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is predictedlocation Fragment"
    }
    val text: LiveData<String> = _text
}
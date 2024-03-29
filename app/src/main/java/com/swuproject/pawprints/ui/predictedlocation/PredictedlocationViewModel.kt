package com.swuproject.pawprints.ui.predictedlocation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PredictedlocationViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is predictedlocation Fragment"
    }
    val text: LiveData<String> = _text
}
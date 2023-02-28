package com.example.testdailycounter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// het viewmodel voor hilt. Hierin kun je de sensoren gebruiken voor data.
@HiltViewModel
class MainViewModel @Inject constructor(
    private val StepCounter: MeasurableSensor
): ViewModel(){

    var totalSteps by mutableStateOf(0)

    init {
        StepCounter.startListening()
        StepCounter.setOnSensorValuesChangedListener { values ->
            totalSteps = values[0].toInt()

        }
    }
}
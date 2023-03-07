package com.example.testdailycounter.stepcounter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// De viewmodel voor hilt. Hierin kun je de sensoren gebruiken voor data.
@HiltViewModel
class StepsViewModel @Inject constructor(
    StepCounter: MeasurableSensor
): ViewModel(){

    var totalSteps by mutableStateOf(0)

    init {
        StepCounter.startListening()
        StepCounter.setOnSensorValuesChangedListener { values ->
            totalSteps = values[0].toInt()

        }
    }
}
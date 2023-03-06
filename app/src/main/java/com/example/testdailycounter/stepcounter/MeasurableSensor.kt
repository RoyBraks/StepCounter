package com.example.testdailycounter.stepcounter

// hierin worden de functies voor de sensoren aangemaakt.

abstract class MeasurableSensor(
    // het sensor type
    protected var sensorType: Int
) {

    // hierin een lijst met alle waardes uit de sensor
    protected var onSensorValuesChanged: ((List<Float>) -> Unit)? = null

    // bestaat de sensor?
    abstract val doesSensorExist: Boolean

    // functies om te starten met afluisteren van sensor
    abstract fun startListening()
    abstract fun stopListening()

    fun setOnSensorValuesChangedListener(listener: (List<Float>) -> Unit) {
        onSensorValuesChanged = listener
    }
}
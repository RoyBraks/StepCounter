package com.example.testdailycounter.googlefit

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA
import com.google.android.gms.fitness.data.Field.FIELD_STEPS
import com.google.android.gms.fitness.request.DataReadRequest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit


class GetStepsFit {

    private val totalDailySteps = MutableLiveData<Int>()

    private val totalStepsMorning = MutableLiveData<Int>()

    val progressDailySteps = MutableLiveData<Float>()
    fun dailySteps(
        account: GoogleSignInAccount,
        activity: Activity
    ): MutableLiveData<Int> {
        Fitness.getHistoryClient(activity, account)
            .readDailyTotal(TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { response ->
                val totalSteps =
                    response.dataPoints.firstOrNull()?.getValue(FIELD_STEPS)?.asInt() ?: 0
                convertTotalSteps(totalSteps = totalSteps)
                calcProgressDailySteps(totalSteps = totalSteps)

            }.addOnFailureListener { e ->
                Log.i(TAG, "There was a problem getting steps.", e)
            }
        return totalDailySteps
    }
    fun morningSteps(
        account: GoogleSignInAccount,
        activity: Activity
    ): MutableLiveData<Int> {

        val zone = ZoneId.systemDefault()
        val startTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
        val endTime = LocalDateTime.of(
            LocalDate.now(zone).year,
            LocalDate.now(zone).monthValue,
            LocalDate.now(zone).dayOfMonth,
            12,
            0
        )

        val datasource = DataSource.Builder()
            .setAppPackageName("com.google.android.gms")
            .setDataType(TYPE_STEP_COUNT_DELTA)
            .setType(DataSource.TYPE_DERIVED)
            .setStreamName("estimated_steps")
            .build()

        val request = DataReadRequest.Builder()
            .aggregate(datasource)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(ZoneOffset.of("+00:00")), TimeUnit.SECONDS)
            .build()

        Fitness.getHistoryClient(activity, account)
            .readData(request)
            .addOnSuccessListener { response ->
                val totalSteps = response.buckets
                    .flatMap { it.dataSets }
                    .flatMap { it.dataPoints }
                    .sumOf { it.getValue(FIELD_STEPS).asInt() }
                convertMorningSteps(totalSteps = totalSteps)
            }.addOnFailureListener { e ->
                Log.i(TAG, "There was a problem getting steps.", e)
            }
        return totalStepsMorning
    }

    private fun convertTotalSteps(
        totalSteps: Int
    ) {
        totalDailySteps.value = totalSteps
    }

    private fun convertMorningSteps(
        totalSteps: Int
    ) {
        totalStepsMorning.value = totalSteps
    }

    private fun calcProgressDailySteps(
        totalSteps: Int
    ) {
        progressDailySteps.value = (totalSteps.toDouble() / 10000).toFloat()
    }
}
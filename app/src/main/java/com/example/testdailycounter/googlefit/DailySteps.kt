package com.example.testdailycounter.googlefit

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA
import com.google.android.gms.fitness.data.Field.FIELD_STEPS
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import java.util.*
import java.util.concurrent.TimeUnit

val totalDailySteps = MutableLiveData<Int>()
@RequiresApi(Build.VERSION_CODES.O)
fun dailySteps(
    context: Context,
    fitnessOptions: FitnessOptions,
    account: GoogleSignInAccount,
    activity: Activity
): MutableLiveData<Int> {
    Fitness.getHistoryClient(activity, account)
        .readDailyTotal(TYPE_STEP_COUNT_DELTA)
        .addOnSuccessListener{
            response ->
            val totalSteps =
                response.dataPoints.firstOrNull()?.getValue(FIELD_STEPS)?.asInt() ?: 0
            convertTotalSteps(totalSteps = totalSteps)

        }.addOnFailureListener { e ->
            Log.i(TAG, "There was a problem getting steps.", e)
        }
    return totalDailySteps
}

fun convertTotalSteps(totalSteps: Int) {
    totalDailySteps.value = totalSteps
}
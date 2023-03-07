package com.example.testdailycounter


import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testdailycounter.datastore.StoreStepsToday

import com.example.testdailycounter.googlefit.dailySteps
import com.example.testdailycounter.stepcounter.StepsViewModel
import com.example.testdailycounter.ui.theme.TestDailyCounterTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// main activity
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Google Fit Permissions

        var googleFitPermission = false

        val fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .build()

        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this, // your activity
                1, // e.g. 1
                account,
                fitnessOptions)
        } else {
            googleFitPermission = true
        }


        setContent {
            TestDailyCounterTheme {

                // context
                val context = LocalContext.current

                // scope
                val scope = rememberCoroutineScope()

                // data store Steps
                val dataStore = StoreStepsToday(context)

                // opgeslagen hoeveelheid steps
                val savedSteps = dataStore.getSteps.collectAsState(initial = 0)

                // laad de viewmodel in
                val stepsViewModel = viewModel<StepsViewModel>()

                // totale steps vanuit de viewmodel
                val totalSteps = stepsViewModel.totalSteps

                // Daily Steps
                val totalDailySteps = if (googleFitPermission) {
                    dailySteps(this, fitnessOptions, account, this)
                } else {
                    null
                }

                var stepsRightNow by remember { mutableStateOf(0) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center

                ){
                    Text(
                        text = "$totalSteps",
                        color = Color.White
                    )

                    Text(
                        text = "${totalDailySteps?.value}",
                        color = Color.White
                    )

                    Button(onClick = {
                        stepsRightNow = totalSteps
                        scope.launch {
                            dataStore.saveSteps(stepsRightNow)
                        }

                    }) {
                        Text(text = "Click To Save Steps")
                    }

                    Text(
                        text = "${savedSteps.value}",
                        color = Color.White
                    )
                }
            }
        }
    }
}
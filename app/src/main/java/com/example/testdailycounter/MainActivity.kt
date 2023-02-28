package com.example.testdailycounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testdailycounter.datastore.StoreStepsToday
import com.example.testdailycounter.ui.theme.TestDailyCounterTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// main activity
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                val viewModel = viewModel<MainViewModel>()
                // totale steps vanuit de viewmodel
                val totalSteps = viewModel.totalSteps

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
package com.example.testdailycounter


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testdailycounter.datastore.StoreStepsButton
import com.example.testdailycounter.googlefit.GetStepsFit
import com.example.testdailycounter.stepcounter.StepsViewModel
import com.example.testdailycounter.ui.theme.TestDailyCounterTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// main activity
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission", "Granted")
            } else {
                Log.i("Permission", "Denied")
            }

        }
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

        val requestPermission =
                RequestPermissions(
                    context = this,
                    this,
                    requestPermissionLauncher,
                    permissionType = android.Manifest.permission.ACTIVITY_RECOGNITION
                )

        requestPermission.requestPermission()


        setContent {

            TestDailyCounterTheme {

                // context
                val context = LocalContext.current

                // scope
                val scope = rememberCoroutineScope()

                // data store Steps
                val dataStore = StoreStepsButton(context)

                // opgeslagen hoeveelheid steps
                val savedSteps = dataStore.getSteps.collectAsState(initial = 0)

                // laad de viewmodel in
                val stepsViewModel = viewModel<StepsViewModel>()

                // totale steps vanuit de viewmodel
                val totalSteps = stepsViewModel.totalSteps

                val getStepsClass = GetStepsFit()
                // Daily Steps
                val totalDailySteps = if (googleFitPermission) {
                    getStepsClass.dailySteps(account, this).observeAsState()
                } else {
                    null
                }

                // Steps Morning
                val totalStepsMorning = if (googleFitPermission) {
                    getStepsClass.morningSteps(account, this).observeAsState()
                } else {
                    null
                }

                val progressValueDailySteps = getStepsClass.progressDailySteps.observeAsState(0.1F)
                val animatedProgress by animateFloatAsState(
                    targetValue = progressValueDailySteps.value,
                    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                )

                var stepsSaved by remember { mutableStateOf(0) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(text = "Fitness Tracker") },
                            backgroundColor = Color.DarkGray
                        )
                    },
                    bottomBar = {BottomBar()}
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                            .padding(padding),
                        horizontalAlignment = Alignment.CenterHorizontally,

                        ){

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 50.dp, bottom = 50.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(
                                modifier = Modifier,
                                onDraw = {
                                    drawCircle(
                                        color = Color(0xFF1A1A1A),
                                        radius = 115.dp.toPx()
                                    )
                                    drawCircle(
                                        color = Color.Black,
                                        radius = 95.dp.toPx()
                                    )
                                }
                            )
                            CircularProgressIndicator(
                                progress = animatedProgress,
                                modifier = Modifier
                                    .height(230.dp)
                                    .width(230.dp),
                                strokeWidth = 20.dp,
                                color = Color.Cyan
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "You have taken",
                                    color = Color.White.copy(alpha = 0.3f),
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${totalDailySteps?.value}",
                                    color = Color.White,
                                    fontSize = 42.sp
                                )
                                Text(
                                    text = "steps today",
                                    color = Color.White.copy(alpha = 0.3f),
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(0.5f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "You have taken",
                                    color = Color.White.copy(alpha = 0.3f)
                                )
                                Text(
                                    text = "${totalStepsMorning?.value}",
                                    color = Color.White,
                                    fontSize = 30.sp
                                )

                                Text(
                                    text = "before 12:00",
                                    color = Color.White.copy(alpha = 0.3f)
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(0.5f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "You have taken",
                                    color = Color.White.copy(alpha = 0.3f)
                                )
                                Text(
                                    text = "$totalSteps",
                                    color = Color.White,
                                    fontSize = 30.sp
                                )

                                Text(
                                    text = "since last reboot",
                                    color = Color.White.copy(alpha = 0.3f)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Saved steps",
                                color = Color.White.copy(alpha = 0.3F)
                            )
                            Text(
                                text = "${savedSteps.value}",
                                color = Color.White
                            )

                        }

                        Button(
                            onClick = {
                                stepsSaved = totalSteps
                                scope.launch {
                                    dataStore.saveSteps(stepsSaved)
                                }
                            }
                        ) {
                            Text(text = "Click To Save Steps")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomBar() {
    val context = LocalContext.current
    val selectedIndex = remember { mutableStateOf(0) }
    BottomNavigation {
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
            label = { Text(text = "Home") },
            selected = (selectedIndex.value == 0),
            onClick = {
                selectedIndex.value = 0
            })
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Camera,
                    contentDescription = "Camera"
                )
            },
            label = { Text(text = "Camera") },
            selected = (selectedIndex.value == 1),
            onClick = {
                context.startActivity(Intent(context, CameraActivity::class.java))
            })
    }
}

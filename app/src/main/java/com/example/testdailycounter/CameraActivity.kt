package com.example.testdailycounter

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import coil.compose.rememberImagePainter
import com.example.testdailycounter.ui.theme.TestDailyCounterTheme
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : ComponentActivity() {

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(true)

    private lateinit var photoUri: Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) {isGranted: Boolean ->
                if (isGranted) {
                    Log.i("Permission", "Granted")
                    shouldShowCamera.value = true
                } else {
                    Log.i("Permission", "Denied")
                    shouldShowCamera.value = false
                }

            }

        val requestPermission =
            RequestPermissions(
                context = this,
                this,
                requestPermissionLauncher,
                permissionType = android.Manifest.permission.CAMERA
            )

        setContent {

            requestPermission.requestPermission()

            TestDailyCounterTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(text = "Camera") },
                            backgroundColor = Color.DarkGray
                        )
                    },
                ) { padding ->

                    Box(
                        modifier = Modifier
                            .padding(padding)
                    ) {
                        if (shouldShowCamera.value) {
                            CameraView(
                                outputDirectory = outputDirectory,
                                executor = cameraExecutor,
                                onImageCaptured = ::handleImageCapture,
                                onError = {it}
                            )
                        }

                        if(shouldShowPhoto.value) {
                            Image(
                                painter = rememberImagePainter(photoUri),
                                contentDescription = "Picture",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }

        Log.d("show camera", "${shouldShowCamera.value}")

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    private fun handleImageCapture(uri: Uri) {
        shouldShowCamera.value = false

        photoUri = uri
        shouldShowPhoto.value = true

    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

}
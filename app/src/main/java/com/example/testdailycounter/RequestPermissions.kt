package com.example.testdailycounter

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class RequestPermissions(
    private val context: Context,
    private val activity: Activity,
    private val requestPermissionLauncher: ActivityResultLauncher<String>,
    private val permissionType: String
) {
    // ask permission for activity reading

    fun requestPermission() {
        when{
            ContextCompat.checkSelfPermission(
                context,
                permissionType
            ) == PackageManager.PERMISSION_GRANTED -> {
                // permission granted
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                permissionType
            ) -> {
                // additional rationale displayed
            } else -> {
                requestPermissionLauncher.launch(permissionType)
            }
        }
    }
}
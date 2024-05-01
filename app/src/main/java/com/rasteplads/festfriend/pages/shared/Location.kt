package com.rasteplads.festfriend.pages.shared

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.rasteplads.eventmeshandroid.REQUIRED_PERMISSIONS
import com.rasteplads.festfriend.Position
import kotlinx.coroutines.delay


@Composable
fun CheckPermissions(ctx: Context, checkPermissions: Boolean, granted: (Boolean) -> Unit){
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ grantedMaps ->
       granted(grantedMaps.values.all { it })
    }

    if (!checkPermissions)
        return
    if (REQUIRED_PERMISSIONS.any {
                permission -> (ContextCompat.checkSelfPermission(ctx, permission)
                != PackageManager.PERMISSION_GRANTED)
        }) {
        launcher.launch(REQUIRED_PERMISSIONS)
        return
    }
    granted(true)
}

@Composable
fun GetLocation(ctx: Context, onSuccess: (Position) -> Unit){
    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(ctx)
    }
    val permission = Manifest.permission.ACCESS_FINE_LOCATION
    val isGranted = ContextCompat.checkSelfPermission(ctx, permission)

    if (isGranted != PackageManager.PERMISSION_GRANTED)
        return

    LaunchedEffect(Unit) {
        while (true){
            locationClient.getCurrentLocation(
                Priority.PRIORITY_LOW_POWER,
                CancellationTokenSource().token
            ).addOnSuccessListener {
                if (it == null)
                    return@addOnSuccessListener
                onSuccess(Position(it.longitude.toFloat(), it.latitude.toFloat(), System.currentTimeMillis()))
            }
            delay(5000)
        }
    }
}
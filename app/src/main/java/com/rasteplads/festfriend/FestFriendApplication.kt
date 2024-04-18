package com.rasteplads.festfriend

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.rasteplads.festfriend.pages.CreateGroupPage
import com.rasteplads.festfriend.pages.FestFriendScreen
import com.rasteplads.festfriend.pages.JoinGroupPage
import com.rasteplads.festfriend.pages.LandingPage
import com.rasteplads.festfriend.pages.MapPage
import com.rasteplads.festfriend.ui.theme.FestFriendTheme
import kotlinx.coroutines.delay

enum class JoinOrCreate { Join, Create, None }

@Composable
fun FestFriendApplication(appViewModel: AppViewModel = viewModel()){

    val appState by appViewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val navToMap = { navController.navigate(FestFriendScreen.Map.name) }
    val context = LocalContext.current
    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    var action = JoinOrCreate.None
    val permission = Manifest.permission.ACCESS_FINE_LOCATION
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (action == JoinOrCreate.Join)
                appViewModel.joinGroup(navToMap)
            else if (action == JoinOrCreate.Create)
                appViewModel.createGroup(navToMap)
        } else {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("This app requires your location to work as expected")
                .setTitle("Permission Required")
                .setCancelable(true)
                .setPositiveButton("Settings") { dialog, which ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.setData(uri)
                    context.startActivity(intent)

                    dialog.dismiss()
                }
            builder.show()
        }
    }

    FestFriendTheme(dynamicColor = false) {
        Surface(Modifier.fillMaxSize()) {
            NavHost(navController, startDestination = FestFriendScreen.Landing.name){
                composable(FestFriendScreen.Landing.name){
                    LandingPage(
                        onCreateButtonClick = {navController.navigate(FestFriendScreen.CreateGroup.name)},
                        onJoinButtonClick = {navController.navigate(FestFriendScreen.JoinGroup.name)}
                    )
                }
                composable(FestFriendScreen.CreateGroup.name){
                    CreateGroupPage(
                        appState,
                        onUsernameChange = { appViewModel.updateUsername(it) },
                        onPasswordChange = { appViewModel.updatePassword(it) },
                        onCreateButtonClick = {
                            val permissionCheckResult = ContextCompat.checkSelfPermission(context, permission)
                            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED)
                                appViewModel.createGroup(navToMap)
                            else{
                                action = JoinOrCreate.Create
                                locationPermissionLauncher.launch(permission)
                            }},
                        onBackButtonClick = {
                            navController.popBackStack()
                            appViewModel.clearError(ErrorType.Generic)
                        }
                    )
                }
                composable(FestFriendScreen.JoinGroup.name) {
                    JoinGroupPage(
                        appState,
                        onGroupIDChange = { appViewModel.updateGroupID(it) },
                        onUsernameChange = { appViewModel.updateUsername(it) },
                        onPasswordChange = { appViewModel.updatePassword(it) },
                        onJoinButtonClick = {
                            val permissionCheckResult = ContextCompat.checkSelfPermission(context, permission)
                            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED)
                                appViewModel.createGroup(navToMap)
                            else{
                                action = JoinOrCreate.Join
                                locationPermissionLauncher.launch(permission)
                            }},
                        onBackButtonClick = {
                            navController.popBackStack()
                            appViewModel.clearError(ErrorType.Generic)
                            appViewModel.clearError(ErrorType.Group)
                        }
                    )
                }
                composable(FestFriendScreen.Map.name){
                    LaunchedEffect(Unit) {
                        while (true) {
                            appViewModel.updateFriendsList()
                            locationClient.getCurrentLocation(
                                Priority.PRIORITY_LOW_POWER,
                                CancellationTokenSource().token
                            ).addOnSuccessListener {
                                if (it != null) {
                                    appViewModel.updatePosition(
                                        Position(
                                            it.longitude.toFloat(),
                                            it.latitude.toFloat()
                                        )
                                    )
                                }
                            }
                            delay(5000)
                        }
                    }
                    MapPage(appState)
                }
            }
        }
    }
}
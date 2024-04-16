package com.rasteplads.festfriend

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.rasteplads.festfriend.pages.CreateGroupPage
import com.rasteplads.festfriend.pages.FestFriendScreen
import com.rasteplads.festfriend.pages.JoinGroupPage
import com.rasteplads.festfriend.pages.LandingPage
import com.rasteplads.festfriend.pages.MapPage
import com.rasteplads.festfriend.ui.theme.FestFriendTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import kotlinx.coroutines.delay

@Composable
fun FestFriendApplication(appViewModel: AppViewModel = viewModel()){

    val appState by appViewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val navToMap = {navController.navigate(FestFriendScreen.Map.name)}
    val context = LocalContext.current
    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    FestFriendTheme (dynamicColor = false){
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
                        appState.username,
                        appState.password,
                        appState.isError,
                        appState.error,
                        onUsernameChange = { appViewModel.updateUsername(it) },
                        onPasswordChange = { appViewModel.updatePassword(it) },
                        onCreateButtonClick = { appViewModel.createGroup(navToMap) },
                        onBackButtonClick = { navController.popBackStack() }
                    )
                }
                composable(FestFriendScreen.JoinGroup.name){
                    JoinGroupPage(
                        appState.username,
                        appState.groupID,
                        appState.password,
                        appState.isError,
                        appState.error,
                        onGroupIDChange = { appViewModel.updateGroupID(it) },
                        onUsernameChange = { appViewModel.updateUsername(it) },
                        onPasswordChange = { appViewModel.updatePassword(it)},
                        onJoinButtonClick = { appViewModel.joinGroup(navToMap)},
                        onBackButtonClick = { navController.popBackStack() }
                    )
                }
                composable(FestFriendScreen.Map.name){
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                            101)
                    }
                    MapPage(appState.groupID, appState.password, appState.username, appState.friends,
                        { appViewModel.updateFriendsList() })
                    LaunchedEffect(Unit) {
                        while (true){
                            appViewModel.updateFriendsList()
                            locationClient.getCurrentLocation(Priority.PRIORITY_LOW_POWER, CancellationTokenSource().token).addOnSuccessListener {
                                if (it == null) {
                                    Log.d("Location", "Could not find location")
                                } else {
                                    appViewModel.updatePosition(Position(it.longitude.toFloat(), it.latitude.toFloat()))
                                }
                            }
                            delay(5000)
                        }
                    }
                }
            }
        }
    }
}
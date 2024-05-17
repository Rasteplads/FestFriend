package com.rasteplads.festfriend

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rasteplads.festfriend.pages.CreateGroupPage
import com.rasteplads.festfriend.pages.FestFriendScreen
import com.rasteplads.festfriend.pages.JoinGroupPage
import com.rasteplads.festfriend.pages.LandingPage
import com.rasteplads.festfriend.pages.MapPage
import com.rasteplads.festfriend.pages.shared.CheckPermissions
import com.rasteplads.festfriend.pages.shared.GetLocation
import com.rasteplads.festfriend.ui.theme.FestFriendTheme
import kotlinx.coroutines.delay

fun PermissionNotGrantedMessage(context: Context) {
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

@Composable
fun FestFriendApplication(appViewModel: AppViewModel = viewModel()) {

    val appState by appViewModel.uiState.collectAsState()
    val navController = rememberNavController()

    val navToMap = { navController.navigate(FestFriendScreen.Map.name) }
    val ctx = LocalContext.current

    val locationPermissionChecker = @Composable {
        c: Context, b: Boolean, g: (Boolean) -> Unit ->
        CheckPermissions(c, b, g)
    }

    FestFriendTheme() {
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
                                    if (it)
                                        appViewModel.createGroup(ctx, navToMap)
                                    else
                                        PermissionNotGrantedMessage(ctx)
                        },
                        onBackButtonClick = {
                            navController.popBackStack()
                            appViewModel.clearError(ErrorType.Generic)
                        },
                        locationPermissionChecker = locationPermissionChecker
                    )

                }
                composable(FestFriendScreen.JoinGroup.name) {
                    JoinGroupPage(
                        appState,
                        onGroupIDChange = { appViewModel.updateGroupID(it) },
                        onUsernameChange = { appViewModel.updateUsername(it) },
                        onPasswordChange = { appViewModel.updatePassword(it) },
                        onJoinButtonClick = {
                            if (it)
                                appViewModel.joinGroup(ctx, navToMap)
                            else
                                PermissionNotGrantedMessage(ctx)
                        },
                        onBackButtonClick = {
                            navController.popBackStack()
                            appViewModel.clearError(ErrorType.Generic)
                            appViewModel.clearError(ErrorType.Group)
                        },
                        locationPermissionChecker = locationPermissionChecker
                    )

                }
                composable(FestFriendScreen.Map.name){
                    val getLocation = @Composable {
                        GetLocation(ctx) {
                            appViewModel.updatePosition(it)
                        }
                    }
                    LaunchedEffect(Unit){
                        while(true){
                        appViewModel.updateFriendsList()
                        delay(10000)
                        }
                    }
                    MapPage(
                        appState,
                        getLocation = getLocation,
                        getFriendsClick = {}
                    )
                }
            }
        }
    }
}
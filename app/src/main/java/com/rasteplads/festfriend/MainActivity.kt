package com.rasteplads.festfriend

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rasteplads.festfriend.api.FestFriendAPIClient
import com.rasteplads.festfriend.pages.CreateGroupPage
import com.rasteplads.festfriend.pages.FestFriendScreen
import com.rasteplads.festfriend.pages.FestFriendScreen.*
import com.rasteplads.festfriend.pages.JoinGroupPage
import com.rasteplads.festfriend.pages.LandingPage
import com.rasteplads.festfriend.pages.MapPage
import com.rasteplads.festfriend.ui.theme.FestFriendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FestFriendApplication()
        }
    }
}

@Composable
fun FestFriendApplication(appViewModel: AppViewModel = viewModel()){

    val appState by appViewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val navToMap = {navController.navigate(FestFriendScreen.Map.name)}

    FestFriendTheme (dynamicColor = false){
        Surface(Modifier.fillMaxSize()) {
            NavHost(navController, startDestination = Landing.name){
                composable(Landing.name){
                    LandingPage(
                        onCreateButtonClick = {navController.navigate(CreateGroup.name)},
                        onJoinButtonClick = {navController.navigate(JoinGroup.name)}
                    )
                }
                composable(CreateGroup.name){
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
                composable(JoinGroup.name){
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
                    MapPage(appState.groupID, appState.password, appState.username, appState.friends,
                        { appViewModel.updateFriendsList() })
                }
            }
        }
    }
}

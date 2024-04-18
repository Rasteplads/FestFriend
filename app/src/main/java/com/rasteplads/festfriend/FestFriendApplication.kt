package com.rasteplads.festfriend

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rasteplads.festfriend.pages.CreateGroupPage
import com.rasteplads.festfriend.pages.FestFriendScreen
import com.rasteplads.festfriend.pages.JoinGroupPage
import com.rasteplads.festfriend.pages.LandingPage
import com.rasteplads.festfriend.pages.MapPage
import com.rasteplads.festfriend.ui.theme.FestFriendTheme
import kotlinx.coroutines.delay

@Composable
fun FestFriendApplication(appViewModel: AppViewModel = viewModel()){

    val appState by appViewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val navToMap = {navController.navigate(FestFriendScreen.Map.name)}
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
                        appState,
                        onUsernameChange = { appViewModel.updateUsername(it) },
                        onPasswordChange = { appViewModel.updatePassword(it) },
                        onCreateButtonClick = { appViewModel.createGroup(navToMap) },
                        onBackButtonClick = {
                            navController.popBackStack()
                            appViewModel.clearError(ErrorType.Generic)
                        }
                    )
                }
                composable(FestFriendScreen.JoinGroup.name){
                    JoinGroupPage(
                        appState,
                        onGroupIDChange = { appViewModel.updateGroupID(it) },
                        onUsernameChange = { appViewModel.updateUsername(it) },
                        onPasswordChange = { appViewModel.updatePassword(it)},
                        onJoinButtonClick = { appViewModel.joinGroup(navToMap)},
                        onBackButtonClick = {
                            navController.popBackStack()
                            appViewModel.clearError(ErrorType.Generic)
                            appViewModel.clearError(ErrorType.Group)
                        }
                    )
                }
                composable(FestFriendScreen.Map.name){
                    LaunchedEffect(Unit) {
                        while (true){
                            appViewModel.updateFriendsList()
                            delay(5000)
                        }
                    }
                    MapPage(appState, friends = appState.friends, groupID = appState.groupID)
                }
            }
        }
    }
}
package com.rasteplads.festfriend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rasteplads.festfriend.pages.CreateGroupPage
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
fun FestFriendApplication(){
    FestFriendTheme (dynamicColor = false){
        MyApp(Modifier.fillMaxSize())
    }
}

enum class FestFriendScreen() {
    Landing,
    CreateGroup,
    JoinGroup,
    Map
}

@Composable
fun MyApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var groupID by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Surface(modifier) {
        NavHost(navController, startDestination = FestFriendScreen.Landing.name){
            composable(FestFriendScreen.Landing.name){
                LandingPage(
                    onCreateButtonClick = {navController.navigate(FestFriendScreen.CreateGroup.name)},
                    onJoinButtonClick = {navController.navigate(FestFriendScreen.JoinGroup.name)}
                )
            }
            composable(FestFriendScreen.CreateGroup.name){
                CreateGroupPage(
                    username,
                    password,
                    onUsernameChange = {username = it },
                    onPasswordChange = {password = it },
                    onCreateButtonClick = {navController.navigate(FestFriendScreen.Map.name)},
                    onBackButtonClick = {navController.popBackStack()}
                )
            }
            composable(FestFriendScreen.JoinGroup.name){
                JoinGroupPage(
                    username,
                    groupID,
                    password,
                    onGroupIDChange = {groupID = it},
                    onUsernameChange = {username = it },
                    onPasswordChange = {password = it },
                    onJoinButtonClick = {navController.navigate(FestFriendScreen.Map.name)},
                    onBackButtonClick = {navController.popBackStack()}
                )
            }
            composable(FestFriendScreen.Map.name){
                MapPage(navController, groupID, password, username)
            }
        }
    }
}


@Preview
@Composable
fun MyAppPreview() {
    FestFriendApplication()
}


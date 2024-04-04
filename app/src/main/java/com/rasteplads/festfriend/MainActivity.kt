package com.rasteplads.festfriend

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rasteplads.festfriend.pages.CreateGroupPage
import com.rasteplads.festfriend.pages.JoinGroupPage
import com.rasteplads.festfriend.pages.LandingPage
import com.rasteplads.festfriend.pages.MapPage
import com.rasteplads.festfriend.ui.theme.FestFriendTheme


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


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
                LandingPage(navController)
            }
            composable(FestFriendScreen.CreateGroup.name){
                CreateGroupPage(
                    navController,
                    username,
                    password,
                    onUsernameChange = {username = it },
                    onPasswordChange = {password = it }
                )
            }
            composable(FestFriendScreen.JoinGroup.name){
                JoinGroupPage(
                    navController,
                    username,
                    groupID,
                    password,
                    onGroupIDChange = {groupID = it},
                    onUsernameChange = {username = it },
                    onPasswordChange = {password = it }
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


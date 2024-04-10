package com.rasteplads.festfriend

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.ViewModelProvider
import com.rasteplads.festfriend.repository.Repository
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rasteplads.festfriend.pages.CreateGroupPage
import com.rasteplads.festfriend.pages.JoinGroupPage
import com.rasteplads.festfriend.pages.LandingPage
import com.rasteplads.festfriend.pages.MapPage
import com.rasteplads.festfriend.ui.theme.FestFriendTheme


class MainActivity : ComponentActivity() {

    lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        val req = Requests(viewModel)

        setContent {
            FestFriendApplication(req)
        }
    }
}

@Composable
fun FestFriendApplication(req: Requests){
    FestFriendTheme (dynamicColor = false){
        MyApp(req, Modifier.fillMaxSize())
    }
}

enum class FestFriendScreen() {
    Landing,
    CreateGroup,
    JoinGroup,
    Map
}

@Composable
fun MyApp(req: Requests, modifier: Modifier = Modifier) {
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
                    onCreateButtonClick = {
                        if (!CheckUsername(username))
                            return@CreateGroupPage
                        else
                            if (!CheckPassword(password))
                                return@CreateGroupPage
                            else{
                                req.onCreateGroupRequest(username, password)
                                navController.navigate(FestFriendScreen.Map.name)
                            }
                    },
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
                    onJoinButtonClick = {
                        if (!CheckUsername(username) && !CheckPassword(password)
                            && !CheckGroupID(groupID))
                            return@JoinGroupPage
                        else {
                            req.onJoinGroupRequest(groupID, username, password)
                            navController.navigate(FestFriendScreen.Map.name)
                        }
                    },
                    onBackButtonClick = {navController.popBackStack()}
                )
            }
            composable(FestFriendScreen.Map.name){
                MapPage(groupID, password, username)
            }
        }
    }
}

/*
@Preview
@Composable
fun MyAppPreview() {
    FestFriendApplication()
}
*/


fun CheckUsername(username: String): Boolean {
    if(username.length < 2){
        Log.e("UsernameConstraint", "Username is too short")
        // TODO: Call component that should say that the username is too short
        return true
    }
    else if(username.length > 64) {
        Log.e("UsernameConstraint", "Username is too long")
        // TODO: Call component that should say that the username is too long
        return false
    }
    else{
        return true
    }
}

fun CheckPassword(password: String): Boolean {
    if (password.length < 4){
        Log.e("PasswordConstraint", "Password is too short")
        // TODO: Call component that should say that the password is too short
        return false
    } else if (password.length > 64){
        Log.e("PasswordConstraint", "Password is too long")
        // TODO: Call component that should say that the password is too long
        return false
    } else
        return true
}

fun CheckGroupID(groupID: String): Boolean {
    val regex = "^[a-zA-Z0-9]{6}\$".toRegex()
    if (!regex.matches(groupID)) {
        Log.e("GroupIDConstraint", "GroupID contains invalid characters")
        return false
    }
    else {
        return true
    }
}

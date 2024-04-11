package com.rasteplads.festfriend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.core.text.isDigitsOnly
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

    var groupc = GroupCommunicator()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            var groupID by remember { mutableStateOf("No Group") }
            var friends = remember { groupc.friends }
            var error by remember { mutableStateOf("")}
            var isError by remember { mutableStateOf(false)}

            val fatalErrorHandler = { code: Int, msg: String ->
                error = "Error $code: $msg"
                isError = !isError
            }

            val errorCheck = { err: Pair<Boolean, String> ->
                isError = err.first
                error = err.second
                err.first
            }

            FestFriendApplication(
                groupID = groupID,
                friends = friends,
                isError = isError,
                error = error,
                onCreateButtonClick = { nav, user, pass ->
                    if (errorCheck(isInvalidUsername(user)))
                        return@FestFriendApplication
                    if (errorCheck(isInvalidPassword(pass)))
                        return@FestFriendApplication
                    FestFriendAPIClient.createGroup(user, pass, fatalErrorHandler) { id ->
                        FestFriendAPIClient.joinGroup(id, user, pass, fatalErrorHandler){
                            groupc.joinGroup(id, user, pass)
                            groupID = id.toString()
                            runOnUiThread{
                                navController.navigate(FestFriendScreen.Map.name)
                            }
                        }
                    }
                },
                onJoinButtonClick = { nav, _groupID, user, pass ->
                    if (errorCheck(isInvalidUsername(user)))
                        return@FestFriendApplication
                    if (errorCheck(isInvalidPassword(pass)))
                        return@FestFriendApplication
                    if (errorCheck(isInvalidGroupID(_groupID)))
                        return@FestFriendApplication

                    val id = _groupID.toUShort()

                    FestFriendAPIClient.joinGroup(id, user, pass, fatalErrorHandler){
                        groupc.joinGroup(id, user, pass)
                        groupID = id.toString()
                        runOnUiThread{
                            navController.navigate(FestFriendScreen.Map.name)
                        }
                    }

                },
                onUpdateFriendsListClick = {
                    friends.clear()
                    FestFriendAPIClient.getMembers(groupc.groupID, groupc.password, fatalErrorHandler){ members ->
                        groupc.updateFriendMap(members)
                    }
                },
                navController,
                Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun FestFriendApplication(
    groupID: String,
    friends: SnapshotStateMap<String, Position>,
    isError: Boolean,
    error: String,
    onCreateButtonClick: (NavHostController, String, String) -> Unit,
    onJoinButtonClick: (NavHostController, String, String, String) -> Unit,
    onUpdateFriendsListClick: () -> Unit,
    navController: NavHostController,
    modifier: Modifier){
    var userGroupID by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    FestFriendTheme (dynamicColor = false){
        Surface(modifier) {
            NavHost(navController, startDestination = Landing.name){
                composable(Landing.name){
                    LandingPage(
                        onCreateButtonClick = {navController.navigate(CreateGroup.name)},
                        onJoinButtonClick = {navController.navigate(JoinGroup.name)}
                    )
                }
                composable(CreateGroup.name){
                    CreateGroupPage(
                        username,
                        password,
                        isError,
                        error,
                        onUsernameChange = { username = it },
                        onPasswordChange = { password = it },
                        onCreateButtonClick = {onCreateButtonClick(navController, username, password)},
                        onBackButtonClick = { navController.popBackStack() }
                    )
                }
                composable(JoinGroup.name){
                    JoinGroupPage(
                        username,
                        userGroupID,
                        password,
                        isError,
                        error,
                        onGroupIDChange = { userGroupID = it },
                        onUsernameChange = { username = it },
                        onPasswordChange = { password = it },
                        onJoinButtonClick = { onJoinButtonClick(navController, userGroupID, username, password) },
                        onBackButtonClick = { navController.popBackStack() }
                    )
                }
                composable(FestFriendScreen.Map.name){
                    MapPage(groupID, password, username, friends, onUpdateFriendsListClick)
                }
            }
        }
    }
}

fun isInvalidUsername(username: String): Pair<Boolean, String> {
    if(username.length < 2)
        return Pair(true, "Username is too short")
    else if(username.length > 64)
        return Pair(true, "Username is too long")
    return Pair(false, "")

}

fun isInvalidPassword(password: String): Pair<Boolean, String> {
    if (password.length < 4)
        return Pair(true, "Password is too short")
    else if (password.length > 64)
        return Pair(true, "Password is too long")
    return Pair(false, "")
}

fun isInvalidGroupID(groupID: String): Pair<Boolean, String> {
    val error = !(groupID.isDigitsOnly() && groupID.length <=5)
    if (error)
        return Pair(true, "GroupID is not a UShort")
    return Pair(false, "")
}

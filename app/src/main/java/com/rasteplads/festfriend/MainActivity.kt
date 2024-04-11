package com.rasteplads.festfriend

import android.os.Bundle
import android.util.Log
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
            var groupID by remember { mutableStateOf(groupc.groupID.toString()) }
            var friends = remember { mutableStateMapOf<String, Position>()}
            FestFriendApplication(
                groupID = groupID,
                friends = friends,
                onCreateButtonClick = { nav, user, pass ->
                    FestFriendAPIClient.createGroup(user, pass, {c, m -> Log.e("FESTFRIEND $c", m)}) {id ->
                        if (!checkUsername(user))
                            return@createGroup

                        if (!checkPassword(pass))
                            return@createGroup

                        FestFriendAPIClient.joinGroup(id, user, pass, {c, m -> Log.e("FESTFRIEND $c", m)}){
                            groupc.joinGroup(id, user, pass)
                            groupID = id.toString()
                            runOnUiThread{
                                navController.navigate(FestFriendScreen.Map.name)
                            }
                        }
                    }
                },
                onJoinButtonClick = { nav, _groupID, user, pass ->
                    if (!checkUsername(user) && !checkPassword(pass) && !checkGroupID(_groupID))
                        return@FestFriendApplication

                    val id = _groupID.toUShort()

                    FestFriendAPIClient.joinGroup(id, user, pass, {c, m -> Log.e("FESTFRIEND $c", m)}){
                        groupc.joinGroup(id, user, pass)
                        groupID = id.toString()
                        runOnUiThread{
                            navController.navigate(FestFriendScreen.Map.name)
                        }
                    }

                },
                onUpdateFriendsListClick = {
                    friends.clear()
                    FestFriendAPIClient.getMembers(groupc.groupID, groupc.password, {c, m -> Log.e("FESTFRIEND $c", m)}){members ->
                        groupc.updateFriendMap(members)
                        friends.putAll(groupc.friends)
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

fun checkUsername(username: String): Boolean {
    if(username.length < 2){
        Log.e("UsernameConstraint", "Username is too short")
        return true
    }
    else if(username.length > 64) {
        Log.e("UsernameConstraint", "Username is too long")
        return false
    }
    else{
        return true
    }
}

fun checkPassword(password: String): Boolean {
    if (password.length < 4){
        Log.e("PasswordConstraint", "Password is too short")
        return false
    } else if (password.length > 64){
        Log.e("PasswordConstraint", "Password is too long")
        return false
    } else
        return true
}

fun checkGroupID(groupID: String): Boolean {
    val error = !(groupID.isDigitsOnly() && groupID.length <=5)
    if (error)
        Log.e("GroupIDConstraint", "GroupID contains invalid characters")
    return error
}

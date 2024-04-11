package com.rasteplads.festfriend

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.rasteplads.festfriend.repository.Repository
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rasteplads.festfriend.model.Resource
import com.rasteplads.festfriend.pages.CreateGroupPage
import com.rasteplads.festfriend.pages.FestFriendScreen
import com.rasteplads.festfriend.pages.FestFriendScreen.*
import com.rasteplads.festfriend.pages.JoinGroupPage
import com.rasteplads.festfriend.pages.LandingPage
import com.rasteplads.festfriend.pages.MapPage
import com.rasteplads.festfriend.ui.theme.FestFriendTheme

data class FriendPosition(var name: String, var pos: Position)

class MainActivity : ComponentActivity() {

    lateinit var viewModel: MainViewModel
    var groupc = GroupCommunicator()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        setContent {
            val navController = rememberNavController()
            var groupID by remember { mutableStateOf(groupc.groupID.toString()) }
            var friends = remember { mutableStateMapOf<String, Position>()}
            FestFriendApplication(
                groupID = groupID,
                friends = friends,
                onCreateButtonClick = { nav, user, pass ->
                    createGroup(nav, user, pass) { id ->
                        groupID = id
                    }
                },
                onJoinButtonClick = { nav, _groupID, user, pass ->
                    joinGroup(nav, _groupID, user, pass) { id ->
                        groupID = id
                    }
                },
                onUpdateFriendsListClick = {
                    friends.clear()
                    updateFriends { updatedFriends ->
                        friends.clear()
                        groupc.updateFriendMap(updatedFriends)
                        friends.putAll(groupc.friends)
                    }
                },
                navController,
                Modifier.fillMaxSize(),
            )
        }
    }

    fun updateFriends(f: (Array<String>) -> Unit){
        viewModel.getMembers(groupc.groupID, groupc.password)
        viewModel.getGetMembersResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    val members = response.data!!.members
                    f(members)
                }
                is Resource.ErrorResponse -> Log.e("CreateGroupResponse", response.errorResponse.toString())
                is Resource.Error -> Log.e("CreateGroupResponse", "An error has occured")
            }
        }
    }

    fun createGroup(navController: NavHostController, username: String, password: String, f: (String) -> Unit){
        if (!CheckUsername(username))
            return

        if (!CheckPassword(password))
            return

        viewModel.createGroup(username, password)
        viewModel.CreateGroupResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    val groupID = response.data!!.groupID
                    joinGroup(navController, groupID.toString(), username, password, f)
                }
                is Resource.ErrorResponse -> Log.e("CreateGroupResponse", response.errorResponse.toString())
                is Resource.Error -> Log.e("CreateGroupResponse", "An error has occured")
            }
        }
    }

    fun joinGroup(navController: NavHostController, groupID: String, username: String, password: String, f: (String) -> Unit){
        if (!CheckUsername(username) && !CheckPassword(password) && !CheckGroupID(groupID))
            return

        viewModel.joinGroup(groupID.toUShort(), username, password)
        viewModel.JoinGroupResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    groupc.joinGroup(groupID.toUShort(), username, password)
                    navController.navigate(FestFriendScreen.Map.name)
                    f(groupID)
                }
                is Resource.ErrorResponse -> Log.e("CreateGroupResponse", response.errorResponse.toString())
                is Resource.Error -> Log.e("CreateGroupResponse", "An error has occured")
            }
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

fun CheckUsername(username: String): Boolean {
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

fun CheckPassword(password: String): Boolean {
    if (password.length < 4){
        Log.e("PasswordConstraint", "Password is too short")
        return false
    } else if (password.length > 64){
        Log.e("PasswordConstraint", "Password is too long")
        return false
    } else
        return true
}

fun CheckGroupID(groupID: String): Boolean {
    val match = "^[a-zA-Z0-9]{6}\$".toRegex().matches(groupID)
    if (!match)
        Log.e("GroupIDConstraint", "GroupID contains invalid characters")
    return match
}

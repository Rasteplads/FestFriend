package com.rasteplads.festfriend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                LandingScreen(navController)
            }
            composable(FestFriendScreen.CreateGroup.name){
                CreateGroupScreen(
                    navController,
                    username,
                    password,
                    onUsernameChange = {username = it },
                    onPasswordChange = {password = it }
                )
            }
            composable(FestFriendScreen.JoinGroup.name){
                JoinGroupScreen(
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

@Composable
fun LandingScreen(navController: NavHostController){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.8f))
        Text(
            text = "FestFriend",
            modifier = Modifier.padding(10.dp),
            style = TextStyle(fontSize = 48.sp)
        )
        Spacer(modifier = Modifier.weight(0.2f))
        Button(
            onClick = { navController.navigate(FestFriendScreen.CreateGroup.name) },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth(0.8f)
        ){
            Text(text = "Create Group")
        }
        Button(
            onClick = { navController.navigate(FestFriendScreen.JoinGroup.name) },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
            modifier = Modifier.fillMaxWidth(0.8f)
        ){
            Text(text = "Join Group")
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun BackButton(onClick: () -> Unit){
    Row(
        horizontalArrangement = Arrangement.Absolute.Left,
        modifier = Modifier.fillMaxWidth(1f)
    ){
        TextButton(onClick = onClick) {
            Text(
                text = "👈😎",
                style = TextStyle(fontSize = 24.sp)
            )
        }
    }
}

@Composable
fun CreateGroupScreen(
    navController: NavHostController,
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton {navController.popBackStack()}
        Spacer(modifier = Modifier.weight(0.8f))
        Text(
            text = "FestFriend",
            modifier = Modifier.padding(10.dp),
            style = TextStyle(fontSize = 48.sp),
        )
        Spacer(modifier = Modifier.weight(0.2f))
        TextField(
            value = username,
            onValueChange = onUsernameChange,
            placeholder = { Text("Username") },
            modifier = Modifier
                .padding(bottom = 15.dp, top = 15.dp)
                .fillMaxWidth(0.8f),
        )
        Divider(Modifier.fillMaxWidth(0.8f) )
        TextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = { Text("Group Password") },
            modifier = Modifier
                .padding(bottom = 15.dp, top = 15.dp)
                .fillMaxWidth(0.8f)
        )
        Button(
            onClick = { navController.navigate(FestFriendScreen.Map.name) },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "Create Group")
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun JoinGroupScreen(
    navController: NavHostController,
    username: String,
    groupID: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onGroupIDChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton {navController.popBackStack()}
        Spacer(modifier = Modifier.weight(0.8f))
        Text(
            text = "FestFriend",
            modifier = Modifier.padding(10.dp),
            style = TextStyle(fontSize = 48.sp)
        )
        Spacer(modifier = Modifier.weight(0.2f))
        TextField(
            value = username,
            onValueChange = onUsernameChange,
            placeholder = { Text("Username") },
            modifier = Modifier
                .padding(bottom = 15.dp)
                .fillMaxWidth(0.8f)
        )
        Divider(Modifier.fillMaxWidth(0.8f) )
        TextField(
            value = groupID,
            onValueChange = onGroupIDChange,
            placeholder = { Text("Group ID") },
            modifier = Modifier
                .padding(bottom = 15.dp, top = 15.dp)
                .fillMaxWidth(0.8f)
        )
        TextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = { Text("Group Password") },
            modifier = Modifier
                .padding(bottom = 15.dp)
                .fillMaxWidth(0.8f)
        )
        Button(
            onClick = { navController.navigate(FestFriendScreen.Map.name) },
            modifier = Modifier.fillMaxWidth(0.8f)) {
            Text(text = "Join Group")
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun MapPage(
    navController: NavHostController,
    groupID: String,
    password: String,
    username: String,
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Group ID: #$groupID",
            modifier = Modifier.padding(10.dp)
        )
        Text(
            text = "Password: $password",
            modifier = Modifier.padding(10.dp)
        )
        Text(
            text = "username: $username",
            modifier = Modifier.padding(10.dp)
        )
    }
}


@Preview
@Composable
fun MyAppPreview() {
    FestFriendApplication()
}


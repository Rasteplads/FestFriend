package com.rasteplads.festfriend.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rasteplads.festfriend.FestFriendScreen
import com.rasteplads.festfriend.pages.shared.BackButton

@Composable
fun CreateGroupPage(
    navController: NavHostController,
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onCreateButtonClick: () -> Unit,
    onBackButtonClick: () -> Unit,
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton(onClick = onBackButtonClick)
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
            onClick = onCreateButtonClick,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "Create Group")
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
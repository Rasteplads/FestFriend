package com.rasteplads.festfriend.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

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
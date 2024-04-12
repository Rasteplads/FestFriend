package com.rasteplads.festfriend.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.rasteplads.festfriend.Friends
import com.rasteplads.festfriend.Position

@Composable
fun MapPage(
    groupID: String,
    password: String,
    username: String,
    friends: Friends,
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
        Divider(Modifier.fillMaxWidth(0.8f) )
        for ((name, pos) in friends){
            Text(text = "$name: lat:${pos.latitude}, long: ${pos.longitude}")
        }
    }
}
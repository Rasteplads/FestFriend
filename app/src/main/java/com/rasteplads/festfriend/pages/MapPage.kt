package com.rasteplads.festfriend.pages

import android.location.Location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.tasks.Task
import com.rasteplads.festfriend.Friends

@Composable
fun MapPage(
    groupID: String,
    password: String,
    username: String,
    friends: Friends,
    onUpdateFriendsListClick: () -> Unit,
    onUpdateLocation: () -> Unit
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
        Button(
            onClick = onUpdateFriendsListClick,
            modifier = Modifier.fillMaxWidth(0.8f)) {
            Text(text = "Update Friend List")
        }
        Button(
            onClick = onUpdateLocation,
            modifier = Modifier.fillMaxWidth(0.8f)) {
            Text(text = "Update Location")
        }
    }
}
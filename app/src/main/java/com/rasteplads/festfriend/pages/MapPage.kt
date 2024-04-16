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
import com.rasteplads.festfriend.AppState
import com.rasteplads.festfriend.Friends
import com.rasteplads.festfriend.Position

@Composable
fun MapPage(
    appState: AppState
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Group ID: #${appState.groupID}",
            modifier = Modifier.padding(10.dp)
        )
        Text(
            text = "Password: ${appState.password}",
            modifier = Modifier.padding(10.dp)
        )
        Text(
            text = "username: ${appState.username}",
            modifier = Modifier.padding(10.dp)
        )
        Divider(Modifier.fillMaxWidth(0.8f) )
        for ((name, pos) in appState.friends){
            Text(text = "$name: lat:${pos.latitude}, long: ${pos.longitude}")
        }
    }
}
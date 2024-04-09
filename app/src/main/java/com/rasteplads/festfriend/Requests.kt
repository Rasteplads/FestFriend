package com.rasteplads.festfriend

import android.util.Log
import androidx.activity.ComponentActivity

class Requests(var viewModel: MainViewModel) : ComponentActivity() {
    fun onCreateGroupRequest(
        username: String,
        password: String,
    ) {
        viewModel.createGroup(username, password)
        // TODO: Should check if the requests were successful.
    }

    fun onJoinGroupRequest(
        groupID: String,
        username: String,
        password: String
    ) {
        viewModel.joinGroup(groupID, username, password)
        // TODO: Should check if the requests were successful.
    }

    fun GetMembers(
        groupID: String,
        password: String
    ) {
        viewModel.getMembers(groupID, password)
        // TODO: Should check if the requests were successful.
    }




}
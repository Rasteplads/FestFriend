package com.rasteplads.festfriend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FestFriendApplication()
        }
    }

    override fun onBackPressed() {}  // Do nothing, because we do not want the user to accidentally swipe out of the map

}

package com.rasteplads.festfriend.utils

import java.time.Duration

class Constants {
    companion object{
        const val BASE_URL = "http://130.225.37.93:8000/"
        const val APP_TAG = "FestFriendApplication"
        const val MODEL_TAG = "AppViewModel"
        const val GROUP_TAG = "GroupCommunicator"
        // Phone location update delay (milis)
        const val USER_POSITION_UPDATE: Long = 5000
        val EVENT_MESH_SEND_INTERVAL: Duration = Duration.ofSeconds(3)
        val EVENT_MESH_SEND_TIMEOUT: Duration = Duration.ofSeconds((5))
        val EVENT_MESH_SCAN_INTERVAL: Duration = Duration.ofSeconds(5)
        val EVENT_MESH_SCAN_DURATION: Duration = Duration.ofSeconds(5)
    }
}
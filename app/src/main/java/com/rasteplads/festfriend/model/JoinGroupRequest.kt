package com.rasteplads.festfriend.model

data class JoinGroupRequest(
    val groupID: String,
    val username: String,
    val password: String
)
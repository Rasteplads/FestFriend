package com.rasteplads.festfriend.model

data class JoinGroupRequest(
    val groupID: UShort,
    val username: String,
    val password: String
)
package com.rasteplads.festfriend.model

data class GetMembersRequest(
    val groupID: String,
    val password: String
)
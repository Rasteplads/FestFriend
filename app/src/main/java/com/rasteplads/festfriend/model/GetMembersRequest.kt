package com.rasteplads.festfriend.model

data class GetMembersRequest(
    val groupID: UShort,
    val password: String
)
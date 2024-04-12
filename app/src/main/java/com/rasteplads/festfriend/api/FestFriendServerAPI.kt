package com.rasteplads.festfriend.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class JoinGroupRequest(val groupID: UShort, val username: String, val password: String)
data class JoinGroupResponse(val message: String)
data class CreateGroupRequest(val password: String)
data class CreateGroupResponse(val groupID: UShort)
data class GetMembersRequest(val groupID: UShort, val password: String)
data class GetMembersResponse (val members : Array<String>)

interface FestFriendServerAPI {
    @POST("/join")
    suspend fun joinGroup(@Body joinGroupRequest: JoinGroupRequest): Response<JoinGroupResponse>
    @POST("/group/create")
    suspend fun createGroup(@Body password: CreateGroupRequest): Response<CreateGroupResponse>
    @POST("/group/members")
    suspend fun getMembers(@Body getMembersRequest: GetMembersRequest) : Response<GetMembersResponse>
}
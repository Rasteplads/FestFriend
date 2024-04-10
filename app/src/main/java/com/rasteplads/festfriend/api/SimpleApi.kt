package com.rasteplads.festfriend.api

import com.rasteplads.festfriend.model.GetMembersRequest
import com.rasteplads.festfriend.model.CreateGroupResponse
import com.rasteplads.festfriend.model.JoinGroupRequest
import com.rasteplads.festfriend.model.GetMembersResponse
import com.rasteplads.festfriend.model.JoinGroupResponse
import com.rasteplads.festfriend.model.CreateGroupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SimpleApi {

    @POST("/join")
    suspend fun joinGroup(@Body joinGroupRequest: JoinGroupRequest): Response<JoinGroupResponse>

    @POST("/group/create")
    suspend fun createGroup(@Body password: CreateGroupRequest): Response<CreateGroupResponse>

    @POST("/group/members")
    suspend fun getMembers(@Body getMembersRequest: GetMembersRequest) : Response<GetMembersResponse>
}
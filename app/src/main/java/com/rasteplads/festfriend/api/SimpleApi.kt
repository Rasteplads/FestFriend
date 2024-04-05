package com.rasteplads.festfriend.api

import com.rasteplads.festfriend.model.GroupID
import com.rasteplads.festfriend.model.Message
import com.rasteplads.festfriend.model.Password
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SimpleApi {

    @POST("/join")
    suspend fun joinGroup(@Body groupID: String, username: String, password: String): Response<Message>

    @POST("/group/create")
    suspend fun createGroup(@Body password: Password): Response<GroupID>

    @POST("/group/members")
    suspend fun getMembers(@Body groupID: String, password: String) : HashMap<String, List<String>> // The list contains usernames for all the users in the room.
}
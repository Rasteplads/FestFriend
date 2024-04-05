package com.rasteplads.festfriend.repository

import com.rasteplads.festfriend.api.RetrofitInstance
import com.rasteplads.festfriend.model.GroupID
import com.rasteplads.festfriend.model.Message
import com.rasteplads.festfriend.model.Password
import retrofit2.Response

class Repository {
    suspend fun joinGroup(groupID: String, username: String, password: String) : Response<Message> {
        return RetrofitInstance.api.joinGroup(groupID, username, password)
    }

    suspend fun createGroup(password: String) : Response<GroupID> {
        val pas = Password(password)
        return RetrofitInstance.api.createGroup(pas)
    }

    suspend fun getMembers(groupID: String, password: String) : HashMap<String, List<String>> {
        return RetrofitInstance.api.getMembers(groupID, password)
    }
}
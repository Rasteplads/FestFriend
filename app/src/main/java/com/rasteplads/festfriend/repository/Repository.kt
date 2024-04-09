package com.rasteplads.festfriend.repository

import com.rasteplads.festfriend.api.RetrofitInstance
import com.rasteplads.festfriend.model.GetMembersRequest
import com.rasteplads.festfriend.model.CreateGroupResponse
import com.rasteplads.festfriend.model.JoinGroupRequest
import com.rasteplads.festfriend.model.GetMembersResponse
import com.rasteplads.festfriend.model.JoinGroupResponse
import com.rasteplads.festfriend.model.CreateGroupRequest
import retrofit2.Response

class Repository {
    suspend fun joinGroup(groupID: String, username: String, password: String) : Response<JoinGroupResponse> {
        val joingroup = JoinGroupRequest(groupID, username, password)
        return RetrofitInstance.api.joinGroup(joingroup)
    }

    suspend fun createGroup(password: String) : Response<CreateGroupResponse> {
        val pas = CreateGroupRequest(password)
        return RetrofitInstance.api.createGroup(pas)
    }

    suspend fun getMembers(groupID: String, password: String) : Response<GetMembersResponse> {
        val getMembersRequestRequestBody = GetMembersRequest(groupID, password)
        return RetrofitInstance.api.getMembers(getMembersRequestRequestBody)
    }
}
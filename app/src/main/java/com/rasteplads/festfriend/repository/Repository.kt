package com.rasteplads.festfriend.repository

import com.rasteplads.festfriend.api.RetrofitInstance
import com.rasteplads.festfriend.model.GetMembersRequest
import com.rasteplads.festfriend.model.CreateGroupResponse
import com.rasteplads.festfriend.model.JoinGroupRequest
import com.rasteplads.festfriend.model.GetMembersResponse
import com.rasteplads.festfriend.model.JoinGroupResponse
import com.rasteplads.festfriend.model.CreateGroupRequest
import com.rasteplads.festfriend.model.Resource
import java.lang.Exception

class Repository {
    suspend fun joinGroup(groupID: String, username: String, password: String) : Resource<JoinGroupResponse> {
        val joinGroupRequestElement = JoinGroupRequest(groupID, username, password)

        return try {
            val response = RetrofitInstance.api.joinGroup(joinGroupRequestElement)
            val result = response.body()

            if (response.isSuccessful && result != null){
                Resource.Success(result)
            }
            else{
                Resource.ErrorResponse("Code ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to contact server")
        }
    }

    suspend fun createGroup(password: String) : Resource<CreateGroupResponse> {
        val createGroupRequestElement = CreateGroupRequest(password)

        return try {
            val response = RetrofitInstance.api.createGroup(createGroupRequestElement)

            val result = response.body()

            if (response.isSuccessful && result != null){
                Resource.Success(result)
            }
            else{
                Resource.ErrorResponse("Code ${response.code()}: ${response.message()}")
            }
        }
        catch (e: Exception){
            Resource.Error(e.message ?: "Failed to contact server")
        }
    }

    suspend fun getMembers(groupID: String, password: String) : Resource<GetMembersResponse> {
        val getMembersRequestElement = GetMembersRequest(groupID, password)
        return try {
            val response = RetrofitInstance.api.getMembers(getMembersRequestElement)
            val result = response.body()

            if (response.isSuccessful && result != null){
                Resource.Success(result)
            }
            else{
                Resource.ErrorResponse("Code ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to contact server")
        }
    }
}
package com.rasteplads.festfriend

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasteplads.festfriend.model.CreateGroupResponse
import com.rasteplads.festfriend.model.GetMembersResponse
import com.rasteplads.festfriend.model.JoinGroupResponse
import com.rasteplads.festfriend.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: Repository): ViewModel() {
    val JoinGroupResponse: MutableLiveData<Response<JoinGroupResponse>> = MutableLiveData()
    val CreateGroupResponse: MutableLiveData<Response<CreateGroupResponse>> = MutableLiveData()
    val getGetMembersResponse: MutableLiveData<Response<GetMembersResponse>> = MutableLiveData()

    fun joinGroup(groupID: String, username: String, password: String) {
        viewModelScope.launch {
            val response = repository.joinGroup(groupID, username, password)
            JoinGroupResponse.value = response
            if (!response.isSuccessful)
                Log.e("RequestError", "Failed to join group. ${response.code()}: ${response.message()}")
        }
    }

    fun createGroup(username: String, password: String) {
        viewModelScope.launch {
            val response = repository.createGroup(password)
            CreateGroupResponse.value = response
            if (response.isSuccessful){
                joinGroup(response.body()?.groupID.toString(), username, password)
            }
            else
                Log.e("RequestError", "Failed to create group. ${response.code()}: ${response.message()}" )

        }
    }

    fun getMembers(groupID: String, password: String) {
        viewModelScope.launch {
            val response = repository.getMembers(groupID, password)
            getGetMembersResponse.value = response
            if (!response.isSuccessful){
                Log.e("RequestError", "Failed to get members. ${response.code()}: ${response.message()}")
            }
        }
    }
}
package com.rasteplads.festfriend

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasteplads.festfriend.model.CreateGroupResponse
import com.rasteplads.festfriend.model.GetMembersResponse
import com.rasteplads.festfriend.model.JoinGroupResponse
import com.rasteplads.festfriend.model.Resource
import com.rasteplads.festfriend.repository.Repository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository): ViewModel() {
    val JoinGroupResponse: MutableLiveData<Resource<JoinGroupResponse>> = MutableLiveData()
    val CreateGroupResponse: MutableLiveData<Resource<CreateGroupResponse>> = MutableLiveData()
    val getGetMembersResponse: MutableLiveData<Resource<GetMembersResponse>> = MutableLiveData()

    fun joinGroup(groupID: UShort, username: String, password: String) {
        viewModelScope.launch {
            val response = repository.joinGroup(groupID, username, password)
            JoinGroupResponse.value = response
        }
    }

    fun createGroup(username: String, password: String) {
        viewModelScope.launch {
            val response = repository.createGroup(password)
            CreateGroupResponse.value = response
        }
    }

    fun getMembers(groupID: UShort, password: String) {
        viewModelScope.launch {
            val response = repository.getMembers(groupID, password)
            getGetMembersResponse.value = response
        }
    }
}
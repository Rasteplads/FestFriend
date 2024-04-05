package com.rasteplads.festfriend

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasteplads.festfriend.model.GroupID
import com.rasteplads.festfriend.model.Message
import com.rasteplads.festfriend.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: Repository): ViewModel() {
    val GroupResponse: MutableLiveData<Response<Message>> = MutableLiveData()
    val MessageResponse: MutableLiveData<Response<GroupID>> = MutableLiveData()
    val GetMembersResponse: MutableLiveData<Response<HashMap<String, List<String>>>> = MutableLiveData()

    fun joinGroup(groupID: String, username: String, password: String) {
        viewModelScope.launch {
            val response = repository.joinGroup(groupID, username, password)
            GroupResponse.value = response
        }
    }

    fun createGroup(password: String) {
        viewModelScope.launch {
            val response = repository.createGroup(password)
            MessageResponse.value = response
        }
    }

    fun getMembers(groupID: String, password: String) {
        viewModelScope.launch {
            val response = repository.getMembers(groupID, password)

        }
    }


    /*

       suspend fun joinGroup(groupID: String, username: String, password: String) : Group {
        return RetrofitInstance.api.joinGroup(groupID, username, password)
    }

    suspend fun createGroup(password: String) : Group {
        return RetrofitInstance.api.createGroup(password)
    }

    suspend fun getMembers(groupID: String, password: String) : HashMap<String, List<String>> {
        return RetrofitInstance.api.getMembers(groupID, password)
    }


     */

}
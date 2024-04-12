package com.rasteplads.festfriend

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.rasteplads.festfriend.api.FestFriendAPIClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import rasteplads.api.EventMesh

typealias API = FestFriendAPIClient
typealias FestFriendMesh = EventMesh<MessageID, Body>

class AppViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(AppState())
    val uiState: StateFlow<AppState> = _uiState.asStateFlow()

    private val com: GroupCommunicator = GroupCommunicator(::friendUpdate)
    /*private val eventMesh: FestFriendMesh = EventMesh.builder<MessageID, Body>()
        .setMessageCallback(com::messageHandler)

        .setIDGenerator(com::messageID)
        .setDataGenerator(com::body)

        .setDataDecodeFunction(com::bodyFromBytes)
        .setDataEncodeFunction(com::bytesFromBody)
        .setIDDecodeFunction(com::messageIDFromBytes)
        .setIDEncodeFunction(com::bytesFromMessageID)

        .addFilterFunction { com.groupID == it.receiverID }.build()*/
        //TODO: Find out when to execute eventmesh and integrate eventmeshdevicetransmitter

    private fun friendUpdate(){
        _uiState.value = _uiState.value.getFrom(friends = com.friends)
    }

    private fun error(code: Int, msg: String){
        val error = "Error $code: $msg"
        val isError = true
        _uiState.value = _uiState.value.getFrom(error= error, isError = isError)
    }

    private fun checkInput(err: Pair<Boolean, String>): Boolean{
        val isError = err.first
        val error = err.second
        _uiState.value = _uiState.value.getFrom(error= error, isError = isError)
        return err.first
    }

    fun updateFriendsList(){
        API.getMembers(com.groupID, com.password, ::error){
            com.updateFriendMap(it)
            _uiState.value = _uiState.value.getFrom(friends = com.friends)
        }
    }

    fun joinGroup(uiHandler: () -> Unit = {}){
        val groupID = _uiState.value.groupID.toUShort()
        val user = _uiState.value.username
        val pass = _uiState.value.password

        if (checkInput(isInvalidGroupID(_uiState.value.groupID)) || checkInput(isInvalidUsername(user)) || checkInput(isInvalidPassword(pass)))
            return

        API.joinGroup(groupID, user, pass, ::error){
            com.joinGroup(groupID, user, pass)
            this.updateFriendsList()
            uiHandler()
        }
    }

    fun createGroup(uiHandler: () -> Unit = {}){
        val user = _uiState.value.username
        val pass = _uiState.value.password

        if (checkInput(isInvalidUsername(user)) || checkInput(isInvalidPassword(pass)))
            return

        API.createGroup(pass, ::error){
            com.joinGroup(it, user, pass)
            this.updateFriendsList()
            _uiState.value = _uiState.value.getFrom(groupID = it.toString())
            joinGroup(uiHandler)
        }
    }

    fun updatePosition(pos: Position){
        com.updatePosition(pos)
        _uiState.value = _uiState.value.getFrom(position = pos)
    }

    fun updateMessageType(messageType: MessageType){ com.updateMessageType(messageType) }
    fun updateUsername(username: String){ _uiState.value = _uiState.value.getFrom(username = username) }
    fun updatePassword(password: String){ _uiState.value = _uiState.value.getFrom(password = password) }
    fun updateGroupID (groupID: String){ _uiState.value = _uiState.value.getFrom(groupID = groupID)}
}

fun isInvalidUsername(username: String): Pair<Boolean, String> {
    if(username.length < 2)
        return Pair(true, "Username is too short")
    else if(username.length > 64)
        return Pair(true, "Username is too long")
    return Pair(false, "")

}

fun isInvalidPassword(password: String): Pair<Boolean, String> {
    if (password.length < 4)
        return Pair(true, "Password is too short")
    else if (password.length > 64)
        return Pair(true, "Password is too long")
    return Pair(false, "")
}

fun isInvalidGroupID(groupID: String): Pair<Boolean, String> {
    val error = !(groupID.isDigitsOnly() && groupID.length <=5)
    if (error)
        return Pair(true, "GroupID is not a UShort")
    return Pair(false, "")
}

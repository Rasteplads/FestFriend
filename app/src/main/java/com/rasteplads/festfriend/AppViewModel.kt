package com.rasteplads.festfriend

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.rasteplads.festfriend.api.FestFriendAPIClient
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import rasteplads.api.EventMesh

typealias API = FestFriendAPIClient
typealias FestFriendMesh = EventMesh<MessageID, Body>

enum class ErrorType() {
    User,
    Pass,
    Group,
    Generic
}

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

    private fun serverError(code: Int, msg: String){
        updateInputError(ErrorType.Generic, true, msg)
    }

    fun clearError(errorType: ErrorType){
        updateInputError(errorType, false, "")
    }
    private fun updateInputError(errorType: ErrorType, isError: Boolean, msg: String){
        val error = InputError(isError, msg)
        when(errorType){
            ErrorType.User -> _uiState.value = _uiState.value.getFrom(usernameError = error)
            ErrorType.Pass -> _uiState.value = _uiState.value.getFrom(passwordError = error)
            ErrorType.Group -> _uiState.value = _uiState.value.getFrom(groupIDError = error)
            ErrorType.Generic -> _uiState.value = _uiState.value.getFrom(genericError = error)
        }

    }

    private fun isInputError(): Boolean {
        val pass = _uiState.value.passwordError.isError
        val user = _uiState.value.usernameError.isError
        val group = _uiState.value.groupIDError.isError
        val generic = _uiState.value.genericError.isError
        return pass || user || group || generic
    }

    fun updateFriendsList(){
        API.getMembers(MainScope(), com.groupID, com.password, ::serverError){
            com.updateFriendMap(it)
            _uiState.value = _uiState.value.getFrom(friends = com.friends)
        }
    }

    fun joinGroup(uiHandler: () -> Unit = {}){
        clearError(ErrorType.Generic)
        val groupID = _uiState.value.groupID
        val user = _uiState.value.username
        val pass = _uiState.value.password

        updateGroupID(groupID)
        updateUsername(user)
        updatePassword(pass)

        if(isInputError())
            return

        val id = groupID.toUShort()

        API.joinGroup(MainScope(), id, user, pass, ::serverError){
            com.joinGroup(id, user, pass)
            this.updateFriendsList()
            uiHandler()
        }
    }

    fun createGroup(uiHandler: () -> Unit = {}){
        clearError(ErrorType.Generic)
        val user = _uiState.value.username
        val pass = _uiState.value.password

        updateUsername(user)
        updatePassword(pass)

        if (isInputError())
            return

        API.createGroup(MainScope(), pass, ::serverError){
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
    fun updateUsername(username: String){
        clearError(ErrorType.User,)
        if (username.length < 4)
            updateInputError(ErrorType.User, true, "Username is too short")
        if (username.length > 64)
            updateInputError(ErrorType.User, true, "Username is too long")

        _uiState.value = _uiState.value.getFrom(username = username)
    }
    fun updatePassword(password: String){
        clearError(ErrorType.Pass)

        if (password.length < 4)
            updateInputError(ErrorType.Pass, true, "Password is too short")
        if (password.length > 64)
            updateInputError(ErrorType.Pass, true, "Password is too long")


        _uiState.value = _uiState.value.getFrom(password = password)
    }
    fun updateGroupID (groupID: String){
        clearError(ErrorType.Group)

        if (!groupID.isDigitsOnly())
            return

        var id = groupID
        if (id.length > 5)
            id = id.substring(0, id.length - 1)

        try {
            groupID.toUShort()
        } catch (e: NumberFormatException){
            updateInputError(ErrorType.Group, true, "GroupID is not a valid representation of a UShort")
        }

        _uiState.value = _uiState.value.getFrom(groupID = id)
    }
}
package com.rasteplads.festfriend

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.rasteplads.eventmeshandroid.AndroidBluetoothTransportDevice
import com.rasteplads.festfriend.api.FestFriendAPIClient
import com.rasteplads.festfriend.utils.Constants.Companion.MODEL_TAG
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import rasteplads.api.EventMesh
import rasteplads.api.TransportDevice
import rasteplads.util.plus
import java.nio.ByteBuffer
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

typealias API = FestFriendAPIClient
typealias FestFriendMesh = EventMesh<MessageID, Body>

enum class ErrorType {
    User,
    Pass,
    Group,
    Generic
}
class AppViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(AppState())
    val uiState: StateFlow<AppState> = _uiState.asStateFlow()

    private val com: GroupCommunicator = GroupCommunicator(::friendUpdate)
    private val _device: AndroidBluetoothTransportDevice = AndroidBluetoothTransportDevice()
    private val eventMesh: FestFriendMesh = EventMesh.builder<MessageID, Body>(_device)
        .setMessageCallback(com::messageHandler)

        .setIDGenerator(com::messageID)
        .setDataGenerator(com::body)
        .setDataSize(Body.SIZE)
        .setDataDecodeFunction(com::bodyFromBytes)
        .setDataEncodeFunction(com::bytesFromBody)
        .setIDDecodeFunction(com::messageIDFromBytes)
        .setIDEncodeFunction(com::bytesFromMessageID)
        .withMsgSendInterval(Duration.ofSeconds(3))
        .addFilterFunction { com.groupID == it.receiverID }
        .build()

    override fun onCleared() {
        eventMesh.stop()
        super.onCleared()
    }
    private var friendTick = 0
    private fun friendUpdate(){
        friendTick++
        _uiState.value = _uiState.value.getFrom(friends = com.friends, friendTick = friendTick)
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
        Log.d(MODEL_TAG, "Update Friends List")
        API.getMembers(MainScope(), com.groupID, com.password, ::serverError){
            com.updateFriendMap(it)
            _uiState.value = _uiState.value.getFrom(friends = com.friends)
            Log.d(MODEL_TAG, "Update Friends List handled")
        }
    }

    fun joinGroup(ctx: Context, uiHandler: () -> Unit = {}){
        Log.d(MODEL_TAG, "Join Group")
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
            _device.contextProvider = { ctx }
            _device.bluetoothProvider = {ctx.getSystemService(BluetoothManager::class.java).adapter}
            eventMesh.start()

            GlobalScope.launch {
                var x = 0f
                while (true){
                    if (x > 11)
                        x = 0f
                    com.messageHandler(MessageID(com.groupID, userID = 69u), Body(longitude = x, latitude = x))
                    x++
                    delay(1000)
                }
            }

            uiHandler()
            Log.d(MODEL_TAG, "Join Group handled")
        }
    }

    fun createGroup(ctx: Context, uiHandler: () -> Unit = {}){
        Log.d(MODEL_TAG, "Create Group")
        clearError(ErrorType.Generic)
        val user = _uiState.value.username
        val pass = _uiState.value.password

        updateUsername(user)
        updatePassword(pass)

        if (isInputError())
            return

        API.createGroup(MainScope(), pass, ::serverError){
            com.joinGroup(it, user, pass)
            _uiState.value = _uiState.value.getFrom(groupID = it.toString())
            joinGroup(ctx, uiHandler)
            Log.d(MODEL_TAG, "Create Group handled")
        }
    }

    fun updatePosition(pos: Position){
        com.updatePosition(pos)
        _uiState.value = _uiState.value.getFrom(position = pos)
        Log.d(MODEL_TAG, "User position updated")
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
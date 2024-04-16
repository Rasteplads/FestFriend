package com.rasteplads.festfriend

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import java.nio.ByteBuffer
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

data class Position(var longitude: Float, var latitude: Float)
typealias Friends = HashMap<String, Position>
typealias FriendNameMap = HashMap<UByte, String>

class GroupCommunicator(private val friendPosUpdater: () -> Unit){
    private lateinit var _key: SecretKeySpec
    private var _myPassword: String = ""
    private var _myUsername: String = ""
    private var _id = MessageID()
    private var _body = Body()
    private var _groupID: UShort = 0u

    private var _friends: Friends = Friends()
    private var _friendMap: FriendNameMap = FriendNameMap()

    val groupID
        get () = _groupID

    val password
        get () = _myPassword

    val friends
        get () = _friends

    val username
        get () = _myUsername

    fun updateFriendMap(friends: Array<String>): GroupCommunicator{
        this._friends.clear()
        _friendMap.clear()

        friends.forEachIndexed { index, friend ->
            if (friend == _myUsername){
                _id.userID = index.toUByte()
                return@forEachIndexed
            }

            _friendMap[index.toUByte()] = friend
            this._friends[friend] = Position(0f, 0f)
        }
        return this
    }

    fun joinGroup(groupID: UShort, username: String, password: String): GroupCommunicator{
        this._groupID = groupID
        _myUsername = username
        _myPassword = password
        _key = getKey(groupID.toString() + password)
        return this
    }

    fun updatePosition(pos: Position): GroupCommunicator{
        _body.longitude = pos.longitude
        _body.latitude = pos.latitude
        return this
    }

    fun updateMessageType(messageType: MessageType): GroupCommunicator{
        _body.type = messageType
        return this
    }

    fun messageHandler(messageID: MessageID, body: Body){
        val friendID = messageID.userID
        val name = _friendMap[friendID] ?: friendID.toString()

        if (!_friends.containsKey(name))
            _friends[name] = Position(0f, 0f)


        val pos = _friends[name]
        pos?.longitude = body.longitude
        pos?.latitude = body.latitude
        friendPosUpdater()
    }

    fun messageIDFromBytes(bytes: ByteArray): MessageID{
        return MessageID.fromByteArray(decrypt(_key, bytes))
    }

    fun bytesFromMessageID(messageID: MessageID): ByteArray{
        return encrypt(_key, messageID.toByteArray())
    }

    fun bodyFromBytes(bytes: ByteArray): Body{
        return Body.fromByteArray(decrypt(_key, bytes))
    }

    fun bytesFromBody(body: Body): ByteArray{
        return encrypt(_key, body.toByteArray())
    }

    fun messageID(): MessageID{
        val id = _id.copy()
        _id++
        return id
    }

    fun body(): Body{
        return _body.copy()
    }
}



enum class MessageType(val value: UByte){
    POS(0.toUByte()),
    UNKOWN(255.toUByte());

    companion object {
        fun fromValue(value: UByte): MessageType {
            val entry = entries.firstOrNull { it.value == value }
            if (entry == null) return UNKOWN
            return entry
        }
    }
}

class MessageID(
    var receiverID: UShort = 0u,
    var incrementer: UByte = 0u,
    var userID: UByte = 0u
){

    operator fun inc(): MessageID{
        incrementer++
        return this
    }

    fun toByteArray(): ByteArray{
        return ByteBuffer.allocate(4).putShort(receiverID.toShort()).put(incrementer.toByte()).put(userID.toByte()).array()
    }

    fun copy(): MessageID{
        return MessageID(receiverID, incrementer, userID)
    }
    companion object {
        fun fromByteArray(bytes: ByteArray): MessageID{
            if (bytes.size != 4)
                throw  Exception("MessageID array size does not match 4 chars")
            val buffer = ByteBuffer.wrap(bytes)
            val rec = buffer.getShort().toUShort()
            val inc = buffer.get().toUByte()
            val userID = buffer.get().toUByte()

            return MessageID(rec, inc, userID)
        }
    }
}

class Body(
    var type: MessageType = MessageType.POS,
    var longitude: Float = 0f,
    var latitude: Float = 0f,
){
    fun toByteArray(): ByteArray{
        return ByteBuffer.allocate(24).put(type.value.toByte()).putFloat(longitude).putFloat(latitude).array()
    }

    fun copy(): Body{
        return Body(type, longitude, latitude)
    }

    override fun toString(): String {
        return "Message(type=$type, longitude=$longitude, latitude=$latitude)"
    }

    companion object {
        fun fromByteArray(bytes: ByteArray): Body{
            if (bytes.size != 24)
                throw  Exception("Body array size does not match 24 chars")
            val buffer = ByteBuffer.wrap(bytes)
            val type = MessageType.fromValue(buffer.get().toUByte())
            val longitude = buffer.getFloat()
            val latitude = buffer.getFloat()

            return Body(type, longitude, latitude)
        }
    }
}

fun getKey(password: String): SecretKeySpec {
    val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
    val bytes = password.toByteArray()
    digest.update(bytes, 0, bytes.size)
    val key = digest.digest()
    return SecretKeySpec(key, "RC4")
}

fun encrypt(key: SecretKeySpec, data: ByteArray): ByteArray{
    val cipher = Cipher.getInstance("RC4")
    cipher.init(Cipher.ENCRYPT_MODE, key)
    return cipher.doFinal(data)
}


fun decrypt(key: SecretKeySpec, data: ByteArray): ByteArray{
    val cipher = Cipher.getInstance("RC4")
    cipher.init(Cipher.DECRYPT_MODE, key)
    return cipher.doFinal(data)
}
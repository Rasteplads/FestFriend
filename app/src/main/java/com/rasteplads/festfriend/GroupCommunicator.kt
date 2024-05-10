package com.rasteplads.festfriend

import android.util.Log
import java.nio.ByteBuffer
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

data class Position(
    var longitude: Float = 0f,
    var latitude: Float = 0f,
    var sent: Long = System.currentTimeMillis(),
    var received: Long = System.currentTimeMillis()
)

data class UserData(
    var id: UByte,
    var username: String,
    var pos: Position = Position(),
    var incrementer: UByte = 0u
)

typealias Friends = HashMap<UByte, UserData>

class GroupCommunicator(
    private val friendPosUpdater: () -> Unit,
    private var _id: MessageID = MessageID(),
    private var _body: Body = Body()
){
    private lateinit var _key: SecretKeySpec
    private var _myPassword: String = ""
    private var _myUsername: String = ""

    private var _groupID: UShort = 0u

    private var _friends: Friends = Friends()

    val groupID
        get () = _groupID

    val password
        get () = _myPassword

    val friends
        get () = _friends

    val userID
        get () = _id.userID

    val username
        get () = _myUsername

    fun updateFriendMap(friends: Array<String>): GroupCommunicator{

        friends.forEachIndexed { index, friend ->
            if (friend == _myUsername){
                _id.userID = index.toUByte()
                return@forEachIndexed
            }
            val friendID = index.toUByte()
            if (!_friends.containsKey(friendID))
                this._friends[friendID] = UserData(friendID, friend)
            else {
                this._friends[friendID]?.username = friend
            }
        }
        return this
    }

    fun joinGroup(groupID: UShort, username: String, password: String): GroupCommunicator{
        this._groupID = groupID
        this._id.receiverID = this._groupID
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

        if (!_friends.containsKey(friendID) || friendID == _id.userID)
            return
            //_friends[friendID] = UserData(friendID, friendID.toString())

        val user = _friends[friendID]
        if (messageID.incrementer > (user?.incrementer ?: 0u)){
            user?.pos?.longitude = body.longitude
            user?.pos?.latitude = body.latitude
            user?.pos?.sent = body.timestamp
            user?.pos?.received = System.currentTimeMillis()
            user?.incrementer = messageID.incrementer
        }

        friendPosUpdater()
    }

    fun messageIDFromBytes(bytes: ByteArray): MessageID{
        return MessageID.fromByteArray(bytes)
    }

    fun bytesFromMessageID(messageID: MessageID): ByteArray{
        return messageID.toByteArray()
    }

    fun bodyFromBytes(bytes: ByteArray): Body{
        return Body.fromByteArray(decrypt(_key, bytes))
    }

    fun bytesFromBody(body: Body): ByteArray{
        return encrypt(_key, body.toByteArray())
    }

    fun messageID(): MessageID{
        val id = _id.copy()
        Log.d("MessageID", id.toString())
        _id++
        return id
    }

    fun body(): Body{
        _body.timestamp = System.currentTimeMillis()
        return _body.copy()
    }
}



enum class MessageType(val value: UByte){
    POS(0.toUByte()),
    UNKOWN(255.toUByte());

    companion object {
        fun fromValue(value: UByte): MessageType {
            return entries.firstOrNull { it.value == value } ?: return UNKOWN
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
        return ByteBuffer.allocate(MessageID.SIZE).putShort(receiverID.toShort()).put(incrementer.toByte()).put(userID.toByte()).array()
    }

    override fun toString(): String {
        return "MessageID(receiverID=$receiverID, incrementer=$incrementer, userID=$userID)"
    }

    fun copy(): MessageID{
        return MessageID(receiverID, incrementer, userID)
    }
    companion object {
        fun fromByteArray(bytes: ByteArray): MessageID{
            if (bytes.size != SIZE)
                throw  Exception("MessageID array size does not match $SIZE chars")
            val buffer = ByteBuffer.wrap(bytes)
            val rec = buffer.getShort().toUShort()
            val inc = buffer.get().toUByte()
            val userID = buffer.get().toUByte()

            return MessageID(rec, inc, userID)
        }

        val SIZE = 4
    }
}

class Body(
    var type: MessageType = MessageType.POS,
    var longitude: Float = 0f,
    var latitude: Float = 0f,
    var timestamp: Long = 0,
){
    fun toByteArray(): ByteArray{
        return ByteBuffer.allocate(Body.SIZE).put(type.value.toByte()).putFloat(longitude).putFloat(latitude).putLong(timestamp).array()
    }

    fun copy(): Body{
        return Body(type, longitude, latitude, timestamp)
    }

    override fun toString(): String {
        return "Body(type=$type, longitude=$longitude, latitude=$latitude, )"
    }

    companion object {
        fun fromByteArray(bytes: ByteArray): Body{
            if (bytes.size != SIZE)
                throw  Exception("Body array size does not match $SIZE chars")
            val buffer = ByteBuffer.wrap(bytes)
            val type = MessageType.fromValue(buffer.get().toUByte())
            val longitude = buffer.getFloat()
            val latitude = buffer.getFloat()
            val timestamp = buffer.getLong()

            return Body(type, longitude, latitude, timestamp)
        }
        val SIZE = 17
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
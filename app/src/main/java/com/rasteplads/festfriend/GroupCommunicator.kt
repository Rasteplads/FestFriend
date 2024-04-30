package com.rasteplads.festfriend

import android.util.Log
import com.rasteplads.festfriend.utils.Constants
import com.rasteplads.festfriend.utils.Constants.Companion.GROUP_TAG
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.time.Duration
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.math.cos
import kotlin.math.sin

data class Position(var longitude: Float, var latitude: Float)
typealias Friends = HashMap<String, Position>
typealias FriendNameMap = HashMap<UByte, String>

class GroupCommunicator(
    private val friendPosUpdater: () -> Unit,
    private var _id: MessageID = MessageID(),
    private var _body: Body = Body()
): GroupCommunicatorInterface{
    private lateinit var _key: SecretKeySpec
    private var _myPassword: String = ""
    private var _myUsername: String = ""

    private var _groupID: UShort = 0u

    private var _friends: Friends = hashMapOf(
        "John" to Position(2f, 2f)
    )
    private var _friendMap: FriendNameMap = FriendNameMap()

    override val groupID
        get () = _groupID

    override val password
        get () = _myPassword

    override val friends
        get () = _friends

    override val username
        get () = _myUsername

    init {
        GlobalScope.launch {
            var x = 0f
            var y = 0f
            while (true){
                if (x > 10)
                    x = 0f
                if (y > 10)
                    y = 0f
                x++
                y++
                _friends["John"] = Position(x, y)
                Log.d(GROUP_TAG, _friends["John"].toString())
                friendPosUpdater()
                delay(500)
            }
        }
    }

    override fun updateFriendMap(friends: Array<String>): GroupCommunicator{
        /*this._friends.clear()
        _friendMap.clear()

        friends.forEachIndexed { index, friend ->
            if (friend == _myUsername){
                _id.userID = index.toUByte()
                return@forEachIndexed
            }
            _friendMap[index.toUByte()] = friend
            this._friends[friend] = Position(0f, 0f)
        }*/
        return this
    }

    override fun joinGroup(groupID: UShort, username: String, password: String): GroupCommunicator{
        this._groupID = groupID
        this._id.receiverID = this._groupID
        _myUsername = username
        _myPassword = password
        _key = getKey(groupID.toString() + password)
        Log.d(GROUP_TAG, "Group joined")
        return this
    }

    override fun updatePosition(pos: Position): GroupCommunicator{
        _body.longitude = pos.longitude
        _body.latitude = pos.latitude
        return this
    }

    override fun updateMessageType(messageType: MessageType): GroupCommunicator{
        _body.type = messageType
        return this
    }

    override fun messageHandler(messageID: MessageID, body: Body){
        val friendID = messageID.userID
        val name = _friendMap[friendID] ?: friendID.toString()

        if (!_friends.containsKey(name))
            _friends[name] = Position(0f, 0f)


        val pos = _friends[name]
        pos?.longitude = body.longitude
        pos?.latitude = body.latitude
        friendPosUpdater()
        Log.d(GROUP_TAG, "Message Handled: $messageID, $body")
    }

    override fun messageIDFromBytes(bytes: ByteArray): MessageID{
        Log.d(GROUP_TAG, "MessageID From Bytes")
        return MessageID.fromByteArray(bytes)
    }

    override fun bytesFromMessageID(messageID: MessageID): ByteArray{
        Log.d(GROUP_TAG, "Bytes From MessageID")
        return messageID.toByteArray()
    }

    override fun bodyFromBytes(bytes: ByteArray): Body{
        Log.d(GROUP_TAG, "Body From Bytes")
        return Body.fromByteArray(decrypt(_key, bytes))
    }

    override fun bytesFromBody(body: Body): ByteArray{
        Log.d(GROUP_TAG, "Bytes From Body")
        return encrypt(_key, body.toByteArray())
    }

    override fun messageID(): MessageID{
        Log.d(GROUP_TAG, "MessageID Getter")
        val id = _id.copy()
        _id++
        return id
    }

    override fun body(): Body{
        Log.d(GROUP_TAG, "Body Getter")
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
){
    fun toByteArray(): ByteArray{
        return ByteBuffer.allocate(Body.SIZE).put(type.value.toByte()).putFloat(longitude).putFloat(latitude).array()
    }

    fun copy(): Body{
        return Body(type, longitude, latitude)
    }

    override fun toString(): String {
        return "Body(type=$type, longitude=$longitude, latitude=$latitude)"
    }

    companion object {
        fun fromByteArray(bytes: ByteArray): Body{
            if (bytes.size != SIZE)
                throw  Exception("Body array size does not match $SIZE chars")
            val buffer = ByteBuffer.wrap(bytes)
            val type = MessageType.fromValue(buffer.get().toUByte())
            val longitude = buffer.getFloat()
            val latitude = buffer.getFloat()

            return Body(type, longitude, latitude)
        }
        val SIZE = 9
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
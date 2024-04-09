package com.rasteplads.festfriend
import java.nio.ByteBuffer

enum class MessageType(val value: Short){
    POS(0),
    UNKOWN(255);
    companion object {
        fun fromValue(value: Short): MessageType {
            return entries.firstOrNull { it.value == value } ?: return UNKOWN
        }
    }
}

class Message(
    val type: MessageType,
    val longitude: Float,
    val latitude: Float,
){
    fun toByteArray(): ByteArray{
        return ByteBuffer.allocate(24).putShort(type.value).putFloat(longitude).putFloat(latitude).array()
    }

    override fun toString(): String {
        return "Message(type=$type, longitude=$longitude, latitude=$latitude)"
    }

    companion object {
        fun fromByteArray(bytes: ByteArray): Message{
            if (bytes.size != 24)
                throw  Exception("Message array size does not match 24 chars")
            val buffer = ByteBuffer.wrap(bytes)
            val type = MessageType.fromValue(buffer.getShort())
            val longitude = buffer.getFloat()
            val latitude = buffer.getFloat()

            return Message(type, longitude, latitude)
        }
    }
}
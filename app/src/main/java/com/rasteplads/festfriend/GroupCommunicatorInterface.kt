package com.rasteplads.festfriend

interface GroupCommunicatorInterface {
    val groupID: UShort
    val username: String
    val password: String
    val friends: Friends

    fun updateFriendMap(friends: Array<String>): GroupCommunicatorInterface
    fun joinGroup(groupID: UShort, username: String, password: String): GroupCommunicatorInterface
    fun updatePosition(pos: Position): GroupCommunicatorInterface
    fun updateMessageType(messageType: MessageType): GroupCommunicatorInterface
    fun messageHandler(messageID: MessageID, body: Body)
    fun messageIDFromBytes(bytes: ByteArray): MessageID
    fun bytesFromMessageID(messageID: MessageID): ByteArray
    fun bodyFromBytes(bytes: ByteArray): Body
    fun bytesFromBody(body: Body): ByteArray
    fun messageID(): MessageID
    fun body(): Body
}

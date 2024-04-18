package com.rasteplads.festfriend

import org.junit.Before
import org.junit.Test
import org.junit.Assert.*



class GroupCommunicatorTest {
    private lateinit var com: GroupCommunicator

    @Before
    fun setUp() {
        com = GroupCommunicator({})
    }
    //@After
    //fun tearDown() {}

    @Test
    fun testJoinGroup(){
        com.joinGroup(55u, "john", "pass")
        assertEquals("john", com.username)
        assertEquals("pass", com.password)
        assertEquals(55.toUShort(), com.groupID)
    }

    @Test
    fun testUpdatePosition(){
        com.joinGroup(55u, "john", "pass")
        val body = com.body()

        assertEquals(0f, body.latitude)
        assertEquals(0f, body.longitude)

        com.updatePosition(Position(34f, 11f))
        val bodyAfter = com.body()

        assertEquals(11f, bodyAfter.latitude)
        assertEquals(34f, bodyAfter.longitude)
    }

    @Test
    fun testUpdateFriendMap(){
        com.joinGroup(55u, "john", "pass")
        com.updateFriendMap(arrayOf("alice", "bob", "john"))

        val userID = com.messageID().userID
        assertEquals(2.toUByte(), userID)

        val friends = com.friends
        val expected = Friends().apply {
            put("alice",  Position(0f, 0f))
            put("bob",  Position(0f, 0f))
        }

        assertEquals(expected, friends)
    }

    @Test
    fun testUpdateMessageType(){
        com.joinGroup(55u, "john", "pass")
        val type = com.body().type

        assertEquals(MessageType.POS, type)

        com.updateMessageType(MessageType.UNKOWN)
        val typeAfter = com.body().type

        assertEquals(MessageType.UNKOWN, typeAfter)
    }

    @Test
    fun testMessageHandler(){
        com.joinGroup(55u, "john", "pass")
        com.updateFriendMap(arrayOf("alice", "bob", "john"))

        val friendID = MessageID(55.toUShort(), userID = 1.toUByte())
        val friendMsg = Body(longitude = 5f, latitude = 2f)
        com.messageHandler(friendID, friendMsg)

        val pos = com.friends["bob"]

        assertEquals(5f, pos?.longitude)
        assertEquals(2f, pos?.latitude)
    }

    @Test
    fun testMessageHandler_unlisted(){
        com.joinGroup(55u, "john", "pass")
        com.updateFriendMap(arrayOf("alice", "bob", "john"))

        val friendID = MessageID(55.toUShort(), userID = 5.toUByte())
        val friendMsg = Body(longitude = 5f, latitude = 2f)
        com.messageHandler(friendID, friendMsg)

        val pos = com.friends["5"]

        assertEquals(5f, pos?.longitude)
        assertEquals(2f, pos?.latitude)
    }

    @Test
    fun testMessageIDFromBytes(){
        com.joinGroup(55u, "john", "pass")
        com.updateFriendMap(arrayOf("alice", "bob", "john"))

        val msgID = com.messageID()
        val bytes = com.bytesFromMessageID(msgID)

        val converted = com.messageIDFromBytes(bytes)

        assertEquals(msgID.toString(), converted.toString())
    }

    @Test
    fun testBytesFromMessageID(){
        com.joinGroup(55u, "john", "pass")
        com.updateFriendMap(arrayOf("alice", "bob", "john"))


        val id = MessageID(55.toUShort(), userID = 2.toUByte())
        val expectedBytes = id.toByteArray()
        val actualBytes = com.bytesFromMessageID(com.messageID())

        assertArrayEquals(
            expectedBytes.toTypedArray(),
            actualBytes.toTypedArray()
        )
    }

    @Test
    fun testBodyFromBytes(){
        com.joinGroup(55u, "john", "pass")
        com.updateFriendMap(arrayOf("alice", "bob", "john"))

        val body = com.body()
        val bytes = com.bytesFromBody(body)

        val converted = com.bodyFromBytes(bytes)

        assertEquals(body.toString(), converted.toString())
    }

    @Test
    fun testBytesFromBody(){
        com.joinGroup(55u, "john", "pass")
        com.updateFriendMap(arrayOf("alice", "bob", "john"))
        com.updatePosition(Position(23f, 43f))

        val key = getKey("55pass")

        val body = Body(MessageType.POS, 23f, 43f)
        val expectedBytes = encrypt(key, body.toByteArray())
        val actualBytes = com.bytesFromBody(com.body())


        assertArrayEquals(
            expectedBytes.toTypedArray(),
            actualBytes.toTypedArray()
        )
    }

    @Test
    fun testMessageID(){
        com.joinGroup(55u, "john", "pass")
        val inc0 = com.messageID().incrementer
        assertEquals(0.toUByte(), inc0)
        val inc1 = com.messageID().incrementer
        assertEquals(1.toUByte(), inc1)
    }

    @Test
    fun testMessageIDOverflow(){
        com = GroupCommunicator({}, MessageID(incrementer = 255.toUByte()))
        com.joinGroup(55u, "john", "pass")
        val inc0 = com.messageID().incrementer
        assertEquals(255.toUByte(), inc0)
        val inc1 = com.messageID().incrementer
        assertEquals(0.toUByte(), inc1)
    }
}
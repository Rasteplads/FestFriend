package com.rasteplads.festfriend

import org.junit.Test
import org.junit.Assert.*
import java.nio.ByteBuffer

class MessageIDTest {
    @Test
    fun toByteArrayTest(){
        val expected = ByteBuffer
            .allocate(4)
            .putShort(55)
            .put(2.toByte())
            .put(128.toByte()).array()
        val bytes = MessageID(55.toUShort(), 2.toUByte(), 128.toUByte()).toByteArray()
        assertArrayEquals(
            expected.toTypedArray(),
            bytes.toTypedArray(),
        )
    }

    @Test
    fun copyTest(){
        val msgID = MessageID(4.toUShort(), 2.toUByte(), 128.toUByte())
        assertNotEquals(msgID, msgID.copy())
    }

    @Test
    fun fromByteArrayTest(){
        val bytes = ByteBuffer
            .allocate(4)
            .putShort(55)
            .put(2.toByte())
            .put(128.toByte()).array()

        val expected = MessageID(55.toUShort(), 2.toUByte(), 128.toUByte())
        val actual = MessageID.fromByteArray(bytes)
        assertEquals(expected.toString(), actual.toString())
    }

    @Test(expected = Exception::class)
    fun fromByteArrayError(){
        val bytes = ByteBuffer
            .allocate(6)
            .putShort(55)
            .put(2.toByte())
            .put(128.toByte()).array()

        MessageID.fromByteArray(bytes)
    }
}
package com.rasteplads.festfriend

import org.junit.Test
import org.junit.Assert.*
import java.nio.ByteBuffer

class BodyTest {
    @Test
    fun toByteArrayTest(){
        val expected = ByteBuffer
            .allocate(23)
            .put(0b0)
            .putFloat(4f)
            .putFloat(1f).array()
        val bytes = Body(longitude = 4f, latitude = 1f).toByteArray()
        assertArrayEquals(
            expected.toTypedArray(),
            bytes.toTypedArray(),
        )
    }

    @Test
    fun copyTest(){
        val body = Body(longitude = 4f, latitude = 6f)
        assertNotEquals(body, body.copy())
    }

    @Test
    fun fromByteArrayTest(){
        val bytes = ByteBuffer
            .allocate(23)
            .put(0b0)
            .putFloat(4f)
            .putFloat(1f).array()

        val expected = Body(longitude = 4f, latitude = 1f)
        val actual = Body.fromByteArray(bytes)
        assertEquals(expected.toString(), actual.toString())
    }

    @Test(expected = Exception::class)
    fun fromByteArrayError(){
        val bytes = ByteBuffer
            .allocate(26)
            .put(0b0)
            .putFloat(4f)
            .putFloat(1f).array()

        Body.fromByteArray(bytes)
    }
}
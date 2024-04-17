package com.rasteplads.festfriend

import org.junit.Test
import org.junit.Assert.*
import java.nio.ByteBuffer
import java.util.Base64


class EncryptionTest {
    @Test
    fun getKeyTest(){
        val expected = "nOMjNrII7j6tLbiPtetzsoSOXu1ywBZx7GxRjRfd/qc="
        val key = getKey("johnsSecretPassword")
        val actual = Base64.getEncoder().encodeToString(key.encoded)
        assertEquals(expected, actual)
    }

    @Test
    fun encryptTest(){
        val expected = "Hm4OmK5xaxgyxoMIeNZLpGyb/yhvMfE="
        val data = "Ben is friends with bob"
        val encrypted = encrypt(getKey("benspassword"), data.toByteArray())
        val actual = Base64.getEncoder().encodeToString(encrypted)

        assertEquals(23, encrypted.size)
        assertEquals(expected, actual)
    }

    @Test
    fun decryptTest(){
        val expected = "Ben is friends with bob"
        val encryptedData = "Hm4OmK5xaxgyxoMIeNZLpGyb/yhvMfE="
        val data = Base64.getDecoder().decode(encryptedData)
        val decrypted = decrypt(getKey("benspassword"), data)
        val actual = decrypted.decodeToString()

        assertEquals(expected, actual)
    }
}
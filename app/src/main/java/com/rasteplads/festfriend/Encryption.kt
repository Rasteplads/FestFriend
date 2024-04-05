package com.rasteplads.festfriend

import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

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
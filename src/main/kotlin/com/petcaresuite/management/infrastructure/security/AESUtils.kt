package com.petcaresuite.management.infrastructure.security

import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESUtils {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"

    //Move to environment variable
    private val IV = "1234567890abcdef".toByteArray(StandardCharsets.UTF_8)
    private val KEY: SecretKey = SecretKeySpec("your_hard_coded_key_here".toByteArray(StandardCharsets.UTF_8), "AES")

    fun encrypt(data: String): String {
        try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, KEY, IvParameterSpec(IV))
            val encryptedBytes = cipher.doFinal(data.toByteArray(StandardCharsets.UTF_8))
            return Base64.getEncoder().encodeToString(encryptedBytes)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun decrypt(encryptedData: String?): String {
        try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, KEY, IvParameterSpec(IV))
            val encryptedBytes = Base64.getDecoder().decode(encryptedData)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
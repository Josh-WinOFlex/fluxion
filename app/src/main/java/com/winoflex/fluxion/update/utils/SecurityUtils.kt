package com.winoflex.fluxion.update.utils

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

/**
 * Utility class for security-related operations, specifically file verification.
 */
object SecurityUtils {

    /**
     * Verifies the SHA-256 hash of a file against an expected hash string.
     *
     * @param file The file to verify.
     * @param expectedSha256 The expected SHA-256 hash in hexadecimal format.
     * @return True if the file's hash matches the expected hash, false otherwise.
     */
    fun verifySha256(file: File, expectedSha256: String): Boolean {
        if (!file.exists() || !file.isFile) return false

        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val buffer = ByteArray(8192)
            FileInputStream(file).use { fis ->
                var bytesRead: Int
                while (fis.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)
                }
            }
            val hashBytes = digest.digest()
            val actualSha256 = hashBytes.joinToString("") { "%02x".format(it) }
            actualSha256.equals(expectedSha256, ignoreCase = true)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

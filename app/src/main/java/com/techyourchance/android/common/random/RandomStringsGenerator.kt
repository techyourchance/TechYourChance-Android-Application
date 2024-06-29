package com.techyourchance.android.common.random

import javax.inject.Inject
import java.lang.StringBuilder
import java.security.SecureRandom
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class RandomStringsGenerator @Inject constructor() {

    fun getRandomAlphanumericString(length: Int): String {
        return getRandomStringFromAlphabet(ALPHANUMERIC, length)
    }

    fun getRandomNumericString(length: Int): String {
        return getRandomStringFromAlphabet(NUMERIC, length)
    }

    private fun getRandomStringFromAlphabet(alphabet: String, length: Int): String {
        val sb = StringBuilder(length)
        for (i in 0 until length) {
            var position: Int
            Companion.LOCK.withLock { // thread-safe protection of SecureRandom (just in case)
                position = SECURE_RANDOM.nextInt(alphabet.length)
            }
            sb.append(alphabet[position])
        }
        return sb.toString()
    }

    companion object {
        private val SECURE_RANDOM = SecureRandom()
        private val LOCK = ReentrantLock()

        private const val ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        private const val NUMERIC = "0123456789"
    }
}
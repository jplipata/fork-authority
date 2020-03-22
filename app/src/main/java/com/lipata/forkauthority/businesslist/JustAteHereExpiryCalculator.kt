package com.lipata.forkauthority.businesslist

import javax.inject.Inject

class JustAteHereExpiryCalculator @Inject constructor(
    private val expirationProvider: ExpirationProvider
) {
    fun isExpired(justAteHereClickDate: Long): Boolean {
        return isExpired(System.currentTimeMillis(), justAteHereClickDate)
    }

    fun isExpired(now: Long, justAteHereClickDate: Long): Boolean {
        val justAteHereThreshold: Int = expirationProvider.get() * 24 * 60 * 60 * 1000 // days to milliseconds
        return now - justAteHereThreshold > justAteHereClickDate
    }
}

interface ExpirationProvider {
    fun get(): Int
}


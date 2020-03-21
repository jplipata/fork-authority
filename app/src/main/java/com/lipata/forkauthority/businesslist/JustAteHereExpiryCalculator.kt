package com.lipata.forkauthority.businesslist

import com.lipata.forkauthority.di.JustAteHerePref
import javax.inject.Inject

class JustAteHereExpiryCalculator @Inject constructor(
    @JustAteHerePref val expirationPreferenceInDays: Int
) {
    fun isExpired(justAteHereClickDate: Long): Boolean {
        return isExpired(System.currentTimeMillis(), justAteHereClickDate)
    }

    fun isExpired(now: Long, justAteHereClickDate: Long): Boolean {
        val justAteHereThreshold: Int = expirationPreferenceInDays * 24 * 60 * 60 * 1000 // days to milliseconds
        return now - justAteHereThreshold > justAteHereClickDate
    }
}


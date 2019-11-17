package com.lipata.forkauthority.poll

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

/**
 * Remember this for Firestore data classes: Properties must have default values
 * https://stackoverflow.com/a/51134217/4840115
 *
 * @property created This is val because we probably don't want anyone changing it.  Other
 * properties are var for ease of making updates.
 */
data class Poll(
    @ServerTimestamp
    val created: Timestamp? = null,
    val participants: MutableList<String> = mutableListOf(),
    val restaurants: MutableList<VotableRestaurant> = mutableListOf()
)

data class VotableRestaurant(
    var name: String = "",
    val votesFor: MutableList<String> = mutableListOf(),
    val votesAgainst: MutableList<String> = mutableListOf()
) {
    fun totalVotes() = votesFor.size - votesAgainst.size
}
package com.lipata.forkauthority.poll

import com.google.firebase.firestore.FirebaseFirestore
import com.lipata.forkauthority.data.user.UserIdentityManager
import javax.inject.Inject

class PollEditor @Inject constructor(
    private val db: FirebaseFirestore,
    private val userIdentityManager: UserIdentityManager
) {
    fun voteFor(votableRestaurant: VotableRestaurant) {
        // update poll
        val email = userIdentityManager.email
        email?.let { votableRestaurant.votesFor.add(it) }

        // save to db

        //WRONG db.collection("polls").document("4z4zop9XAhkJ0dSeuVXm").set(votableRestaurant) // TODO temp poll
    }

}
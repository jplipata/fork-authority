package com.lipata.forkauthority.poll

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.lipata.forkauthority.data.user.UserIdentityManager
import kotlinx.coroutines.tasks.await
import timber.log.Timber
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

    suspend fun createPoll(): String? {
        val newPoll = Poll(
            created = Timestamp.now(),
            participants = mutableListOf( userIdentityManager.email!!) // TODO check for email first
        )

        return try {
            val documentReference = db.collection("polls").add(newPoll).await()
            documentReference.id
        } catch (error: Throwable) {
            Timber.e(error)
            null
        }
    }

}
package com.lipata.forkauthority.poll

import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.lipata.forkauthority.data.Lce
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class PollEditor @Inject constructor(
    private val db: FirebaseFirestore,
    private val userIdentityManager: UserIdentityManager
) {
    // TODO Are we using this?
    val createPollLiveData = MutableLiveData<Lce>()

    suspend fun vote(voteType: VoteType, documentId: String, position: Int) {
        // update poll
        val email = userIdentityManager.email
        if (email.isNullOrBlank()) {
            throw NoEmailException()
        }

        val docSnapshot = getDocument(documentId)
        docSnapshot.toObject(Poll::class.java)?.let { poll ->
            when (voteType) {
                VoteType.FOR -> tryVoteFor(poll, position, email)
                VoteType.AGAINST -> tryVoteAgainst(poll, position, email)
            }

            // save to db
            db.collection("polls").document(documentId).set(poll)
        } ?: throw ToObjectError()
    }

    private fun tryVoteFor(poll: Poll, position: Int,
                           email: String) {
        if (!poll.restaurants[position].votesFor.contains(email)) {
            poll.restaurants[position].votesFor.add(email)
        } else {
            throw AlreadyVotedForException()
        }
    }

    private fun tryVoteAgainst(poll: Poll, position: Int, email: String) {
        if (!poll.restaurants[position].votesAgainst.contains(email)) {
            poll.restaurants[position].votesAgainst.add(email)
        } else {
            throw AlreadyVotedAgainstException()
        }
    }

    private suspend fun getDocument(
        documentId: String) = db.collection("polls").document(documentId).get().await()

    suspend fun createPoll(): String? {
        createPollLiveData.value = Lce.Loading

        val newPoll = Poll(
            created = Timestamp.now(),
            participants = mutableListOf(userIdentityManager.email!!) // TODO check for email first
        )

        return try {
            val documentReference = db.collection("polls").add(newPoll).await()
            createPollLiveData.value = Lce.Content(Unit)
            documentReference.id
        } catch (error: Throwable) {
            Timber.e(error)
            createPollLiveData.value = Lce.Error(error)
            null
        }
    }

    suspend fun addRestaurant(documentId: String?, restaurantName: String) {
        val doc = getDocument(documentId!!)
        val poll = doc.toObject(Poll::class.java)
        poll?.restaurants?.add(
            VotableRestaurant(
                name = restaurantName,
                votesFor = mutableListOf(userIdentityManager.email!!)
            )
        )

        db.collection("polls").document(documentId).set(poll!!)
    }

    class NoEmailException : Exception()
    class AlreadyVotedForException : Exception()
    class AlreadyVotedAgainstException : Exception()

}
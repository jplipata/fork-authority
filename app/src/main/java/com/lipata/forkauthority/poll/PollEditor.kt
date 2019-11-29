package com.lipata.forkauthority.poll

import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.lipata.forkauthority.data.Lce
import com.lipata.forkauthority.data.user.UserIdentityManager
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class PollEditor @Inject constructor(
    private val db: FirebaseFirestore,
    private val userIdentityManager: UserIdentityManager
) {
    // TODO Are we using this?
    val createPollLiveData = MutableLiveData<Lce>()

    // TODO Block illegal votes -- here or in DB rules? Both?
    suspend fun vote(voteType: VoteType, documentId: String, position: Int) {
        // update poll
        val email = userIdentityManager.email

        val docSnapshot = getDocument(documentId)
        val poll = docSnapshot.toObject(Poll::class.java)

        when (voteType) {
            VoteType.FOR -> poll!!.restaurants[position].votesFor.add(email!!)
            VoteType.AGAINST -> poll!!.restaurants[position].votesAgainst.add(email!!)
        }

        // save to db
        db.collection("polls").document(documentId).set(poll)

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

}
package com.lipata.forkauthority.poll

import androidx.lifecycle.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.lipata.forkauthority.data.Lce
import timber.log.Timber
import javax.inject.Inject

/**
 * This is not an Android ViewModel
 */
class PollViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val pollEditor: PollEditor
) : LifecycleObserver {

    private val livedata = MutableLiveData<Lce>()

    private var registration: ListenerRegistration? = null

    var documentId: String? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun subscribeToPoll() {
        documentId?.let {
            val docRef = db.collection("polls")
                .document(it)

            registration = docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    livedata.value = Lce.Error(e)
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    livedata.value = Lce.Error(Exception("snapshot is null"))
                }

                if (snapshot!!.exists()) {
                    val poll = snapshot.toObject(Poll::class.java)
                    livedata.value = Lce.Content(
                        poll?.restaurants.orEmpty()
                            .sortedByDescending { it.totalVotes() }
                    )
                }
            }
        } ?: Timber.e(Exception("documentId is null"))

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun unsubscribe() {
        registration?.remove()
    }

    fun observeLiveData(lifecycleOwner: LifecycleOwner, observer: Observer<Lce>) {
        livedata.observe(lifecycleOwner, observer)
    }

    suspend fun voteFor(votableRestaurant: VotableRestaurant,
                position: Int) {
        pollEditor.voteFor(documentId!!, votableRestaurant, position)
    }

    suspend fun addVotableRestaurant(restaurantName: String) {
        pollEditor.addRestaurant(documentId, restaurantName)
    }
}

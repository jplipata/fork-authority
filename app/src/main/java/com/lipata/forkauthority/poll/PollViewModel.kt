package com.lipata.forkauthority.poll

import androidx.lifecycle.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.lipata.forkauthority.data.Lce
import javax.inject.Inject

class PollViewModel @Inject constructor(private val db: FirebaseFirestore, private val pollEditor: PollEditor) : LifecycleObserver {

    private val livedata = MutableLiveData<Lce>()

    private var registration: ListenerRegistration? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun subscribeToPoll() {
        val docRef = db.collection("polls")
            .document("4z4zop9XAhkJ0dSeuVXm") // TODO Hardcoded temporary poll

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
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun unsubscribe() {
        registration?.remove()
    }

    fun observeLiveData(lifecycleOwner: LifecycleOwner, observer: Observer<Lce>) {
        livedata.observe(lifecycleOwner, observer)
    }

    fun voteFor(votableRestaurant: VotableRestaurant) {
        pollEditor.voteFor(votableRestaurant)
    }
}


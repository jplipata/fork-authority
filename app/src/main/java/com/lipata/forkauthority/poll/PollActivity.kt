package com.lipata.forkauthority.poll

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.lipata.forkauthority.R
import kotlinx.android.synthetic.main.activity_poll.*

class PollActivity : AppCompatActivity() {
    private val adapter = PollAdapter()
    lateinit var viewModel: PollViewModel
    lateinit var registration: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poll)

        pollRecycler.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@PollActivity)
            adapter = this@PollActivity.adapter
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()

        val db = FirebaseFirestore.getInstance()
        subscribeToPoll(db)

    }

    private fun subscribeToPoll(db: FirebaseFirestore) {
        val docRef = db.collection("polls").document("4z4zop9XAhkJ0dSeuVXm") // TODO Hardcoded temporary poll
        registration = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Snackbar.make(pollRecycler, "Listen failed. $e", Snackbar.LENGTH_LONG)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val poll = snapshot.toObject(Poll::class.java)
                adapter.items = poll?.restaurants.orEmpty().sortedByDescending { votableRestaurant -> votableRestaurant.totalVotes() }
                adapter.notifyDataSetChanged()
            } else {
                Snackbar.make(pollRecycler, "Current data: null", Snackbar.LENGTH_LONG)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        registration.remove()
    }
}
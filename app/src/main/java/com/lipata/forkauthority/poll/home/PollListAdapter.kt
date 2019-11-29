package com.lipata.forkauthority.poll.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.lipata.forkauthority.R
import com.lipata.forkauthority.poll.Poll
import kotlinx.android.synthetic.main.poll_item.view.*

class PollListAdapter(private val listener: Listener) : RecyclerView.Adapter<PollViewHolder>() {
    var items: List<DocumentSnapshot> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.poll_item, parent, false)
        return PollViewHolder(view, listener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PollViewHolder, position: Int) {
        holder.bind(items[position])
    }

    interface Listener {
        fun onClick(documentId: String)
    }
}

class PollViewHolder(private val view: View,
                     private val listener: PollListAdapter.Listener) : RecyclerView.ViewHolder(view) {
    fun bind(documentSnapshot: DocumentSnapshot) {
        val poll = documentSnapshot.toObject(Poll::class.java)
        view.tvPollName.text = poll!!.created!!.toDate().toString()
        view.setOnClickListener {
            listener.onClick(documentSnapshot.id)
        }
    }
}


package com.lipata.forkauthority.poll

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lipata.forkauthority.R
import kotlinx.android.synthetic.main.poll_item.view.*

class PollAdapter : RecyclerView.Adapter<PollViewHolder>() {
    var items: List<VotableRestaurant> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.poll_item, parent, false)
        return PollViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PollViewHolder, position: Int) {
        holder.bind(items[position])
    }
}

class PollViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val restaurantName: TextView = view.tvRestaurantName
    private val votes: TextView = view.tvVoteCount

    fun bind(data: VotableRestaurant) {
        restaurantName.text = data.name
        votes.text = (data.votesFor.size - data.votesAgainst.size).toString()
    }
}

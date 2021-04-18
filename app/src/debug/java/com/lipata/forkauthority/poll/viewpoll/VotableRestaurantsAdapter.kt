package com.lipata.forkauthority.poll.viewpoll

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lipata.forkauthority.R
import com.lipata.forkauthority.poll.VotableRestaurant
import com.lipata.forkauthority.poll.VoteType
import kotlinx.android.synthetic.main.votable_restaurant_item.view.*

class VotableRestaurantsAdapter(private val listener: VotableRestaurantListener) :
    RecyclerView.Adapter<VotableRestaurantViewHolder>() {
    var items: List<UserVotableRestaurant> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VotableRestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.votable_restaurant_item, parent, false)
        return VotableRestaurantViewHolder(view, listener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: VotableRestaurantViewHolder, position: Int) {
        holder.bind(items[position], position)
    }
}

class VotableRestaurantViewHolder(
    private val view: View,
    private val listener: VotableRestaurantListener
) : RecyclerView.ViewHolder(view) {

    fun bind(data: UserVotableRestaurant, position: Int) {
        view.tvRestaurantName.text = data.votableRestaurant.name
        view.tvVoteCount.text = (data.votableRestaurant.totalVotes()).toString()

        if (data.userHasVotedFor) {
            // TODO Clicked state
        } else {
            // TODO Default state
        }

        if (data.userHasVotedAgainst) {
            // TODO Clicked state
        } else {
            // TODO Default state
        }

        view.tvVoteFor.setOnClickListener {
            listener.vote(VoteType.FOR, position)
        }
        view.tvVoteAgainst.setOnClickListener {
            listener.vote(VoteType.AGAINST, position)
        }
    }
}

interface VotableRestaurantListener {
    fun vote(voteType: VoteType, position: Int)
}




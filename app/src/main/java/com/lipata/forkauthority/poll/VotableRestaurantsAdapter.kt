package com.lipata.forkauthority.poll

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lipata.forkauthority.R
import kotlinx.android.synthetic.main.votable_restaurant_item.view.*

class VotableRestaurantsAdapter(private val listener: VotableRestaurantListener) :
    RecyclerView.Adapter<VotableRestaurantViewHolder>() {
    var items: List<VotableRestaurant> = emptyList()

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
    fun bind(data: VotableRestaurant, position: Int) {
        view.tvRestaurantName.text = data.name
        view.tvVoteCount.text = (data.votesFor.size - data.votesAgainst.size).toString()
        view.tvVoteFor.setOnClickListener { listener.voteFor(data, position) }
    }
}

interface VotableRestaurantListener {
    fun voteFor(data: VotableRestaurant, position: Int)
    fun voteAgainst()
}

// TODO
//        view.tvVoteAgainst.setOnClickListener {
//            viewModel.voteAgainst(data)
//        }



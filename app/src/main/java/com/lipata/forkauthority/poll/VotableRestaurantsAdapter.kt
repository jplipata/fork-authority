package com.lipata.forkauthority.poll

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lipata.forkauthority.R
import kotlinx.android.synthetic.main.votable_restaurant_item.view.*

class VotableRestaurantsAdapter(private val viewModel: PollViewModel) : RecyclerView.Adapter<VotableRestaurantViewHolder>() {
    var items: List<VotableRestaurant> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VotableRestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.votable_restaurant_item, parent, false)
        return VotableRestaurantViewHolder(view, viewModel)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: VotableRestaurantViewHolder, position: Int) {
        holder.bind(items[position])
    }
}

class VotableRestaurantViewHolder(
    private val view: View,
    private val viewModel: PollViewModel
) : RecyclerView.ViewHolder(view) {
    fun bind(data: VotableRestaurant) {
        view.tvRestaurantName.text = data.name
        view.tvVoteCount.text = (data.votesFor.size - data.votesAgainst.size).toString()

        view.tvVoteFor.setOnClickListener {
            viewModel.voteFor(data)
        }

        // TODO
//        view.tvVoteAgainst.setOnClickListener {
//            viewModel.voteAgainst(data)
//        }

    }
}

package com.lipata.forkauthority.poll

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lipata.forkauthority.R
import kotlinx.android.synthetic.main.poll_item.view.*

class PollAdapter(private val viewModel: PollViewModel) : RecyclerView.Adapter<PollViewHolder>() {
    var items: List<VotableRestaurant> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.poll_item, parent, false)
        return PollViewHolder(view, viewModel)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PollViewHolder, position: Int) {
        holder.bind(items[position])
    }
}

class PollViewHolder(
    private val view: View,
    private val viewModel: PollViewModel
) : RecyclerView.ViewHolder(view) {
    fun bind(data: VotableRestaurant) {
        view.tvRestaurantName.text = data.name
        view.tvVoteCount.text = (data.votesFor.size - data.votesAgainst.size).toString()

        view.tvVoteFor.setOnClickListener {
            viewModel.voteFor(data)
        }

//        view.tvVoteAgainst.setOnClickListener {
//            viewModel.voteAgainst(data)
//        }

    }
}

package com.lipata.forkauthority.poll

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.lipata.forkauthority.R
import kotlinx.android.synthetic.main.fragment_home_poll.view.*


class HomePollFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home_poll, container, false)

        view.view_poll.setOnClickListener {
            view.findNavController().navigate(R.id.action_homePollFragment_to_viewPollFragment)
        }

        return view
    }
}

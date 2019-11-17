package com.lipata.forkauthority.poll

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.navigation.findNavController
import com.lipata.forkauthority.ForkAuthorityApp
import com.lipata.forkauthority.R
import com.lipata.forkauthority.data.user.UserIdentityManager
import kotlinx.android.synthetic.main.fragment_poll_home.*
import kotlinx.android.synthetic.main.fragment_poll_home.view.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


class PollHomeFragment : Fragment() {
    @Inject lateinit var pollEditor: PollEditor
    @Inject lateinit var userIdentityManager: UserIdentityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as ForkAuthorityApp).appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_poll_home, container, false)

        view.tvViewPoll.setOnClickListener {
            view.findNavController().navigate(R.id.action_homePollFragment_to_viewPollFragment)
        }

        view.tvCreatePoll.setOnClickListener { createPoll(view) }

        return view
    }

    override fun onStart() {
        super.onStart()
        userIdentityManager.checkUserIdentity(requireContext()) { refreshEmail() }
    }

    override fun onResume() {
        super.onResume()
        refreshEmail()
    }

    private fun refreshEmail() {
        tvEmail.text = userIdentityManager.email.orEmpty()
    }

    private fun createPoll(view: View) {
        lifecycle.coroutineScope.launch {
            val documentId = pollEditor.createPoll()

            documentId?.let {
                val navigationAction = PollHomeFragmentDirections.actionHomePollFragmentToViewPollFragment(
                    documentId)
                view.findNavController().navigate(navigationAction)
            } ?: Timber.e(Exception("documentId is null"))
        }
    }

}

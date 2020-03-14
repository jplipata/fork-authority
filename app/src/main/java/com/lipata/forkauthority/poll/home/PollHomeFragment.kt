package com.lipata.forkauthority.poll.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.lipata.forkauthority.ForkAuthorityApp
import com.lipata.forkauthority.R
import com.lipata.forkauthority.data.user.UserIdentityManager
import com.lipata.forkauthority.poll.PollEditor
import kotlinx.android.synthetic.main.fragment_poll_home.*
import kotlinx.android.synthetic.main.fragment_poll_home.view.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


class PollHomeFragment : Fragment(), PollListAdapter.Listener {

    @Inject lateinit var pollEditor: PollEditor
    @Inject lateinit var userIdentityManager: UserIdentityManager
    @Inject lateinit var db: FirebaseFirestore

    private val pollListAdapter = PollListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as ForkAuthorityApp).appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_poll_home, container, false)

        view.rvPolls.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pollListAdapter
        }

        view.tvCreatePoll.setOnClickListener { createPoll(view) }

        view.tvEmail.setOnClickListener {
            userIdentityManager.promptUserForEmail(requireContext()) { refreshEmail() }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        userIdentityManager.checkUserIdentity(requireContext()) { refreshEmail() }
        fetchPolls()
    }

    private fun fetchPolls() {
        db.collection("polls").get()
            .addOnSuccessListener { querySnapshot ->
                pollListAdapter.items = querySnapshot.documents
                pollListAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                // TODO Standardize error handling
                Timber.e(it)
                showErrorDialog(it.localizedMessage)
            }
    }

    override fun onResume() {
        super.onResume()
        refreshEmail()
    }

    override fun onClick(documentId: String) {
        navToViewPollFragment(documentId, requireView())
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Something went wrong!")
            .setMessage(message)
            .setPositiveButton(R.string.ok) { dialog, which -> dialog.dismiss() }
            .show()
    }

    private fun refreshEmail() {
        tvEmail.text = userIdentityManager.email.orEmpty()
    }

    private fun createPoll(view: View) {
        // TODO clean this up
        lifecycle.coroutineScope.launch {
            view.tvCreatePoll.isEnabled = false
            view.progressBarCreatePoll.visibility = View.VISIBLE

            val documentId = pollEditor.createPoll()

            if (documentId != null) {
                navToViewPollFragment(documentId, view)
            } else {
                Snackbar.make(view, "Something went wrong", Snackbar.LENGTH_LONG).show()
            }

            view.progressBarCreatePoll.visibility = View.GONE
            view.tvCreatePoll.isEnabled = true
        }
    }

    private fun navToViewPollFragment(documentId: String, view: View) {
        val navigationAction = PollHomeFragmentDirections.actionHomePollFragmentToViewPollFragment(
            documentId)
        view.findNavController().navigate(navigationAction)
    }
}

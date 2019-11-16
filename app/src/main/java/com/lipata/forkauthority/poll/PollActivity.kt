package com.lipata.forkauthority.poll

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.lipata.forkauthority.ForkAuthorityApp
import com.lipata.forkauthority.data.Lce
import com.lipata.forkauthority.R
import com.lipata.forkauthority.data.user.UserIdentityManager
import kotlinx.android.synthetic.main.activity_poll.*
import javax.inject.Inject

class PollActivity : AppCompatActivity() {

    private val adapter = PollAdapter()

    @Inject
    lateinit var userIdentityManager: UserIdentityManager

    @Inject
    lateinit var viewModel: PollViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as ForkAuthorityApp).appComponent.inject(this)

        viewModel.run {
            lifecycle.addObserver(this)
            observeLiveData(this@PollActivity, Observer { onLce(it) })
        }

        setContentView(R.layout.activity_poll)

        pollRecycler.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@PollActivity)
            adapter = this@PollActivity.adapter
        }
    }

    private fun onLce(lce: Lce?) {
        when (lce) {
            is Lce.Loading -> {
            } // not used

            is Lce.Error -> {
                Snackbar.make(pollRecycler, "Error ${lce.error}", Snackbar.LENGTH_LONG).show()
            }

            is Lce.Content<*> -> {
                adapter.items = lce.content as? List<VotableRestaurant> ?: emptyList() // TODO these generics are not great
                adapter.notifyDataSetChanged()
            }

            null -> {
            } // do nothing
        }

    }

    override fun onStart() {
        super.onStart()
        userIdentityManager.checkUserIdentity(this)
        tvEmail.text = userIdentityManager.email.orEmpty()
    }

}
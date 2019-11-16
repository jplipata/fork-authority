package com.lipata.forkauthority.poll

import android.content.Context
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import javax.inject.Inject

class EmailDialog @Inject constructor() {
    fun showEmailPrompt(context: Context, listener: Listener) {
        val taskEditText = EditText(context)
        val dialog = AlertDialog.Builder(context)
            .setTitle("Email")
            .setMessage("We need to know who you are :)")
            .setView(taskEditText)
            .setPositiveButton("OK") { dialog, which ->
                listener.onSubmit(taskEditText.text.toString())
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    interface Listener {
        fun onSubmit(text: String)
    }
}
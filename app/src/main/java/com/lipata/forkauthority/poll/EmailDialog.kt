package com.lipata.forkauthority.poll

import android.content.Context
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import javax.inject.Inject

class EmailDialog @Inject constructor() {
    fun show(context: Context, action: (String) -> Unit) {
        val taskEditText = EditText(context)
        val dialog = AlertDialog.Builder(context)
            .setTitle("Email")
            .setMessage("We need to know who you are :)")
            .setView(taskEditText)
            .setPositiveButton("OK") { dialog, which ->
                action.invoke(taskEditText.text.toString())
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }
}
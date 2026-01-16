package com.linkpoint.ui.friends

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.world.FriendsManager
import kotlinx.coroutines.launch

/**
 * Dialog for adding a new friend
 */
class AddFriendDialog : DialogFragment() {

    private lateinit var nameEditText: TextInputEditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_friend, null)

        nameEditText = view.findViewById(R.id.friend_name)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.add_friend)
            .setView(view)
            .setPositiveButton(R.string.add) { _, _ ->
                addFriend()
            }
            .setNegativeButton(R.string.cancel) { _, _ -> dismiss() }
            .create()
    }

    private fun addFriend() {
        val name = nameEditText.text.toString().trim()
        if (name.isNotEmpty()) {
            val friendsManager = LinkpointApp.getInstance().friendsManager
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    friendsManager.findAndAddFriend(name)
                    dismiss()
                } catch (e: Exception) {
                    // Show error message
                }
            }
        }
    }

    companion object {
        fun newInstance(): AddFriendDialog {
            return AddFriendDialog()
        }
    }
}
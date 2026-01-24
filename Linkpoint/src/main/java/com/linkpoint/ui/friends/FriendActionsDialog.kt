package com.linkpoint.ui.friends

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.world.Friend
import kotlinx.coroutines.launch

/**
 * Dialog showing friend actions
 */
class FriendActionsDialog : DialogFragment() {

    private lateinit var friend: Friend

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        friend = requireArguments().getParcelable("friend")!!

        val options = arrayOf(
            getString(R.string.send_im),
            getString(R.string.view_profile),
            getString(R.string.teleport_to),
            getString(R.string.remove_friend)
        )

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(friend.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> sendIM()
                    1 -> viewProfile()
                    2 -> teleportTo()
                    3 -> removeFriend()
                }
            }
            .setNegativeButton(R.string.cancel) { _, _ -> dismiss() }
            .create()
    }

    private fun sendIM() {
        // Open IM with friend
        val friendsManager = LinkpointApp.getInstance().friendsManager
        lifecycleScope.launch {
            friendsManager.sendIM(friend.agentId, "")
        }
    }

    private fun viewProfile() {
        // View friend's profile
        val profileManager = LinkpointApp.getInstance().profileManager
        lifecycleScope.launch {
            profileManager.getAvatarProfile(friend.agentId)
        }
    }

    private fun teleportTo() {
        // Teleport to friend's location
        val friendsManager = LinkpointApp.getInstance().friendsManager
        lifecycleScope.launch {
            friendsManager.teleportTo(friend.agentId)
        }
    }

    private fun removeFriend() {
        // Remove friend
        val friendsManager = LinkpointApp.getInstance().friendsManager
        lifecycleScope.launch {
            friendsManager.removeFriend(friend.agentId)
        }
    }

    companion object {
        fun newInstance(friend: Friend): FriendActionsDialog {
            return FriendActionsDialog().apply {
                arguments = Bundle().apply {
                    putParcelable("friend", friend)
                }
            }
        }
    }
}
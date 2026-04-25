package com.linkpoint.ui.people

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.ui.chat.ChatActivity
import com.linkpoint.ui.profile.ProfileActivity
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Dialog showing user actions
 */
class UserActionsDialog : DialogFragment() {

    private lateinit var agentId: UUID
    private lateinit var userName: String
    private var isFriend: Boolean = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        agentId = requireArguments().getSerializable("agentId") as UUID
        userName = requireArguments().getString("userName") ?: "Unknown"
        isFriend = requireArguments().getBoolean("isFriend", false)

        val options = if (isFriend) {
            arrayOf(
                getString(R.string.send_im),
                getString(R.string.view_profile),
                getString(R.string.teleport_to),
                getString(R.string.remove_friend)
            )
        } else {
            arrayOf(
                getString(R.string.send_im),
                getString(R.string.view_profile),
                getString(R.string.teleport_to),
                getString(R.string.add_friend)
            )
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(userName)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> sendIM()
                    1 -> viewProfile()
                    2 -> teleportTo()
                    3 -> if (isFriend) removeFriend() else addFriend()
                }
            }
            .setNegativeButton(R.string.cancel) { _, _ -> dismiss() }
            .create()
    }

    private fun sendIM() {
        val app = LinkpointApp.getInstance()
        val sessionId = app.imManager.startP2PSession(agentId, userName)
        app.imManager.markAsRead(sessionId)

        val intent = Intent(requireContext(), ChatActivity::class.java).apply {
            putExtra(ChatActivity.EXTRA_IM_SESSION_ID, sessionId.toString())
        }
        startActivity(intent)
    }

    private fun viewProfile() {
        val intent = Intent(requireContext(), ProfileActivity::class.java).apply {
            putExtra(ProfileActivity.EXTRA_AGENT_ID, agentId.toString())
            putExtra(ProfileActivity.EXTRA_IS_OWN, false)
        }
        startActivity(intent)
    }

    private fun teleportTo() {
        val friendsManager = LinkpointApp.getInstance().friendsManager
        viewLifecycleOwner.lifecycleScope.launch {
            friendsManager.teleportTo(agentId)
        }
    }

    private fun addFriend() {
        val friendsManager = LinkpointApp.getInstance().friendsManager
        viewLifecycleOwner.lifecycleScope.launch {
            friendsManager.offerFriendship(agentId)
        }
    }

    private fun removeFriend() {
        val friendsManager = LinkpointApp.getInstance().friendsManager
        viewLifecycleOwner.lifecycleScope.launch {
            friendsManager.removeFriend(agentId)
        }
    }

    companion object {
        fun newInstance(agentId: UUID, userName: String, isFriend: Boolean): UserActionsDialog {
            return UserActionsDialog().apply {
                arguments = Bundle().apply {
                    putSerializable("agentId", agentId)
                    putString("userName", userName)
                    putBoolean("isFriend", isFriend)
                }
            }
        }
    }
}

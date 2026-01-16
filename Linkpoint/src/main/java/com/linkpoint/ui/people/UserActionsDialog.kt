package com.linkpoint.ui.people

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.world.FriendsManager
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
        val friendsManager = LinkpointApp.getInstance().friendsManager
        lifecycleScope.launch {
            friendsManager.sendIM(agentId, "")
        }
    }

    private fun viewProfile() {
        val profileManager = LinkpointApp.getInstance().profileManager
        lifecycleScope.launch {
            profileManager.loadProfile(agentId)
        }
    }

    private fun teleportTo() {
        val friendsManager = LinkpointApp.getInstance().friendsManager
        lifecycleScope.launch {
            friendsManager.teleportTo(agentId)
        }
    }

    private fun addFriend() {
        val friendsManager = LinkpointApp.getInstance().friendsManager
        lifecycleScope.launch {
            friendsManager.sendFriendshipOffer(agentId)
        }
    }

    private fun removeFriend() {
        val friendsManager = LinkpointApp.getInstance().friendsManager
        lifecycleScope.launch {
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
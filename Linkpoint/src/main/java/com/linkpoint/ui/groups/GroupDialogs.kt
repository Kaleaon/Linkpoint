package com.linkpoint.ui.groups

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.groups.Group
import com.linkpoint.ui.chat.ChatActivity
import kotlinx.coroutines.launch

/**
 * Dialog for showing group details.
 */
class GroupDetailsDialog : DialogFragment() {

    private lateinit var group: Group

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_group_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val nameView: TextView = view.findViewById(R.id.group_name)
        val charterView: TextView = view.findViewById(R.id.group_charter)
        val membersView: TextView = view.findViewById(R.id.group_members)
        val closeButton: Button = view.findViewById(R.id.btn_close)
        val chatButton: Button = view.findViewById(R.id.btn_chat)
        
        nameView.text = group.name
        charterView.text = group.charter.ifEmpty { "No charter" }
        membersView.text = "${group.memberCount} members"
        
        closeButton.setOnClickListener { dismiss() }
        chatButton.setOnClickListener {
            openGroupChat(group)
            dismiss()
        }
    }

    companion object {
        fun newInstance(group: Group): GroupDetailsDialog {
            return GroupDetailsDialog().apply {
                this.group = group
            }
        }
    }
}

/**
 * Open the group chat session for [group]. Starts the chat session via
 * the ChatSessionRequest capability (IMManager.startGroupSession) and
 * launches ChatActivity scoped to that session id. This is the wire that
 * was missing in the live debug capture — the buttons existed but had
 * empty handlers, so users tapping "Group Chat" saw the dialog dismiss
 * with nothing happening.
 */
private fun DialogFragment.openGroupChat(group: Group) {
    val app = LinkpointApp.getInstance()
    val ctx = requireContext()
    Toast.makeText(ctx, "Joining group chat…", Toast.LENGTH_SHORT).show()
    lifecycleScope.launch {
        val sessionId = try {
            app.imManager.startGroupSession(group.groupId, group.name)
        } catch (e: Exception) {
            null
        }
        // Even if startGroupSession fails (e.g. the cap isn't available
        // yet), fall back to opening the screen with the group id —
        // ChatActivity tolerates a missing session and the user can
        // retry once the session is established.
        val effectiveSessionId = sessionId ?: group.groupId
        val intent = Intent(ctx, ChatActivity::class.java).apply {
            putExtra(ChatActivity.EXTRA_IM_SESSION_ID, effectiveSessionId.toString())
        }
        ctx.startActivity(intent)
    }
}

/**
 * Dialog for group options (leave, set active, etc).
 */
class GroupOptionsDialog : DialogFragment() {

    private lateinit var group: Group

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_group_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val titleView: TextView = view.findViewById(R.id.dialog_title)
        val setActiveButton: Button = view.findViewById(R.id.btn_set_active)
        val groupChatButton: Button = view.findViewById(R.id.btn_group_chat)
        val leaveButton: Button = view.findViewById(R.id.btn_leave)
        val cancelButton: Button = view.findViewById(R.id.btn_cancel)
        
        titleView.text = group.name
        
        setActiveButton.setOnClickListener {
            val app = LinkpointApp.getInstance()
            lifecycleScope.launch {
                val ok = try { app.groupsManager.setActiveGroup(group.groupId) } catch (e: Exception) { false }
                Toast.makeText(
                    requireContext(),
                    if (ok) "${group.name} set as active" else "Failed to set active group",
                    Toast.LENGTH_SHORT
                ).show()
            }
            dismiss()
        }

        groupChatButton.setOnClickListener {
            openGroupChat(group)
            dismiss()
        }

        leaveButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Leave ${group.name}?")
                .setMessage("You'll need a new invitation to rejoin.")
                .setPositiveButton(R.string.ok) { _, _ ->
                    val app = LinkpointApp.getInstance()
                    lifecycleScope.launch {
                        val ok = try { app.groupsManager.leaveGroup(group.groupId) } catch (e: Exception) { false }
                        Toast.makeText(
                            requireContext(),
                            if (ok) "Left ${group.name}" else "Failed to leave group",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    dismiss()
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

        cancelButton.setOnClickListener { dismiss() }
    }

    companion object {
        fun newInstance(group: Group): GroupOptionsDialog {
            return GroupOptionsDialog().apply {
                this.group = group
            }
        }
    }
}

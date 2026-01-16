package com.linkpoint.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.linkpoint.R
import com.linkpoint.groups.Group

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
            // Start group chat
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
            // Set as active group
            dismiss()
        }
        
        groupChatButton.setOnClickListener {
            // Open group chat
            dismiss()
        }
        
        leaveButton.setOnClickListener {
            // Leave group - show confirmation
            dismiss()
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

package com.linkpoint.ui.groups

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linkpoint.R
import com.linkpoint.groups.Group

/**
 * Adapter for displaying groups in a RecyclerView.
 *
 * Legacy entry point retained during Compose migration.
 * Removal target: 2026.09.
 */
@Deprecated(
    message = "Legacy RecyclerView adapter. Use GroupsScreen list composables.",
    replaceWith = ReplaceWith("GroupsScreen")
)
class GroupsAdapter(
    private val onGroupClick: (Group) -> Unit,
    private val onGroupLongClick: (Group) -> Unit
) : ListAdapter<Group, GroupsAdapter.GroupViewHolder>(GroupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconView: ImageView = itemView.findViewById(R.id.group_icon)
        private val nameView: TextView = itemView.findViewById(R.id.group_name)
        private val roleView: TextView = itemView.findViewById(R.id.group_role)
        private val activeIndicator: View = itemView.findViewById(R.id.active_indicator)

        fun bind(group: Group) {
            nameView.text = group.name
            roleView.text = when {
                group.canSendNotices && group.canInvite -> "Officer"
                group.canSendNotices -> "Member+"
                else -> "Member"
            }
            
            // Show active indicator if this is the active group
            activeIndicator.visibility = View.GONE // Would check against active group

            itemView.setOnClickListener { onGroupClick(group) }
            itemView.setOnLongClickListener { 
                onGroupLongClick(group)
                true
            }
        }
    }

    class GroupDiffCallback : DiffUtil.ItemCallback<Group>() {
        override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem.groupId == newItem.groupId
        }

        override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem == newItem
        }
    }
}

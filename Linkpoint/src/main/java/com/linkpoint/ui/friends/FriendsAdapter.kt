package com.linkpoint.ui.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linkpoint.R
import com.linkpoint.world.Friend
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying friends in a RecyclerView
 */
class FriendsAdapter(
    private val onFriendClick: (Friend) -> Unit,
    private val onFriendLongClick: (Friend) -> Unit
) : ListAdapter<Friend, FriendsAdapter.FriendViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend, parent, false)
        return FriendViewHolder(view, onFriendClick, onFriendLongClick)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder for friends
     */
    class FriendViewHolder(
        itemView: View,
        private val onClick: (Friend) -> Unit,
        private val onLongClick: (Friend) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val friendAvatar: ImageView = itemView.findViewById(R.id.friend_avatar)
        private val friendName: TextView = itemView.findViewById(R.id.friend_name)
        private val friendStatus: TextView = itemView.findViewById(R.id.friend_status)
        private val onlineIndicator: View = itemView.findViewById(R.id.online_indicator)
        private val lastSeen: TextView = itemView.findViewById(R.id.last_seen)

        fun bind(friend: Friend) {
            friendName.text = friend.name
            
            if (friend.isOnline) {
                friendStatus.text = itemView.context.getString(R.string.online)
                friendStatus.setTextColor(itemView.context.getColor(R.color.online_status))
                onlineIndicator.visibility = View.VISIBLE
                lastSeen.visibility = View.GONE
            } else {
                friendStatus.text = itemView.context.getString(R.string.offline)
                friendStatus.setTextColor(itemView.context.getColor(R.color.offline_status))
                onlineIndicator.visibility = View.GONE
                lastSeen.visibility = View.VISIBLE
                lastSeen.text = formatLastSeen(friend.lastSeenTime)
            }

            itemView.setOnClickListener { onClick(friend) }
            itemView.setOnLongClickListener {
                onLongClick(friend)
                true
            }
        }

        private fun formatLastSeen(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp

            return when {
                diff < 3600000 -> {
                    val minutes = diff / 60000
                    itemView.context.getString(R.string.last_seen_minutes, minutes)
                }
                diff < 86400000 -> {
                    val hours = diff / 3600000
                    itemView.context.getString(R.string.last_seen_hours, hours)
                }
                diff < 604800000 -> {
                    val days = diff / 86400000
                    itemView.context.getString(R.string.last_seen_days, days)
                }
                else -> {
                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        .format(Date(timestamp))
                }
            }
        }
    }

    /**
     * DiffUtil callback for efficient updates
     */
    class DiffCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.agentId == newItem.agentId
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }
    }
}
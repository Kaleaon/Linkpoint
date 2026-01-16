package com.linkpoint.ui.people

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linkpoint.R
import com.linkpoint.world.NearbyUser
import java.text.DecimalFormat

/**
 * Adapter for displaying nearby people
 */
class NearbyPeopleAdapter(
    private val onUserClick: (NearbyUser) -> Unit,
    private val onUserLongClick: (NearbyUser) -> Unit
) : ListAdapter<NearbyUser, NearbyPeopleAdapter.UserViewHolder>(DiffCallback()) {

    private val distanceFormat = DecimalFormat("0.0")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nearby_user, parent, false)
        return UserViewHolder(view, onUserClick, onUserLongClick)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder for nearby users
     */
    class UserViewHolder(
        itemView: View,
        private val onClick: (NearbyUser) -> Unit,
        private val onLongClick: (NearbyUser) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val userAvatar: ImageView = itemView.findViewById(R.id.user_avatar)
        private val userName: TextView = itemView.findViewById(R.id.user_name)
        private val userDistance: TextView = itemView.findViewById(R.id.user_distance)
        private val friendBadge: View = itemView.findViewById(R.id.friend_badge)

        fun bind(user: NearbyUser) {
            userName.text = user.name
            userDistance.text = itemView.context.getString(
                R.string.distance_meters,
                distanceFormat.format(user.distance)
            )

            if (user.isFriend) {
                friendBadge.visibility = View.VISIBLE
            } else {
                friendBadge.visibility = View.GONE
            }

            itemView.setOnClickListener { onClick(user) }
            itemView.setOnLongClickListener {
                onLongClick(user)
                true
            }
        }
    }

    /**
     * DiffUtil callback for efficient updates
     */
    class DiffCallback : DiffUtil.ItemCallback<NearbyUser>() {
        override fun areItemsTheSame(oldItem: NearbyUser, newItem: NearbyUser): Boolean {
            return oldItem.agentId == newItem.agentId
        }

        override fun areContentsTheSame(oldItem: NearbyUser, newItem: NearbyUser): Boolean {
            return oldItem == newItem
        }
    }
}
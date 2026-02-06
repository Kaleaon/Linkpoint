package com.linkpoint.ui.avatar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linkpoint.R
import com.linkpoint.avatar.Wearable

/**
 * Adapter for displaying wearables in a RecyclerView
 */
class WearableAdapter(
    private val onWearableSelected: (Wearable) -> Unit
) : ListAdapter<Wearable, WearableAdapter.WearableViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WearableViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wearable, parent, false)
        return WearableViewHolder(view, onWearableSelected)
    }

    override fun onBindViewHolder(holder: WearableViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder for wearables
     */
    class WearableViewHolder(
        itemView: View,
        private val onClick: (Wearable) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val wearableIcon: ImageView = itemView.findViewById(R.id.wearable_icon)
        private val wearableName: TextView = itemView.findViewById(R.id.wearable_name)
        private val wearableDescription: TextView = itemView.findViewById(R.id.wearable_description)
        private val selectedIndicator: View = itemView.findViewById(R.id.selected_indicator)

        fun bind(wearable: Wearable) {
            wearableName.text = wearable.name
            wearableDescription.text = wearable.description.ifEmpty { "No description" }

            // Use placeholder icon (Glide not available)
            wearableIcon.setImageResource(R.drawable.ic_avatar_placeholder)

            selectedIndicator.visibility = if (wearable.isWorn) {
                View.VISIBLE
            } else {
                View.GONE
            }

            itemView.setOnClickListener { onClick(wearable) }
        }
    }

    /**
     * DiffUtil callback for efficient updates
     */
    class DiffCallback : DiffUtil.ItemCallback<Wearable>() {
        override fun areItemsTheSame(oldItem: Wearable, newItem: Wearable): Boolean {
            return oldItem.itemId == newItem.itemId
        }

        override fun areContentsTheSame(oldItem: Wearable, newItem: Wearable): Boolean {
            return oldItem == newItem
        }
    }
}
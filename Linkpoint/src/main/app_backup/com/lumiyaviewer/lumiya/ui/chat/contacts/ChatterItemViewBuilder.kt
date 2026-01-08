package com.lumiyaviewer.lumiya.ui.chat.contacts

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Nullable
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView
import com.lumiyaviewer.lumiya.ui.chat.TypingIndicatorView
import java.util.Locale

class ChatterItemViewBuilder {
    private var distance = 0f
    private var distanceSet = false
    private var isActiveGroup = false
    private var isOnline = false
    private var label: String? = null
    private var lastMessage: String? = null
    private var onlineVisible = false
    private var thumbnailChatterID: ChatterID? = null
    private var thumbnailDefaultIcon = 0
    private var thumbnailLabel: String? = null
    private var unreadCount = 0
    private var voiceActive = false

    constructor() {
        reset()
    }

    fun getView(inflater: LayoutInflater, convertView: View?, parent: ViewGroup?, isInline: Boolean): View {
        var view = convertView
        if (view == null || view.id != R.id.contactListItemLayout) {
            view = inflater.inflate(R.layout.contact_list_item, parent, false)
        }
        
        val itemView = view!!
        
        val userNameTextView = itemView.findViewById<TextView>(R.id.userNameTextView)
        if (userNameTextView != null) {
            userNameTextView.text = label
        }

        val onlineIcon = itemView.findViewById<View>(R.id.onlineUserIcon)
        onlineIcon?.visibility = if (onlineVisible) View.VISIBLE else View.GONE

        val activeVoiceIcon = itemView.findViewById<View>(R.id.activeVoiceIcon)
        activeVoiceIcon?.visibility = if (voiceActive) View.VISIBLE else View.GONE

        val distanceTextView = itemView.findViewById<TextView>(
            if (isInline) R.id.userDistanceInlineTextView else R.id.userDistanceTextView
        )
        
        if (distanceTextView != null) {
            if (distanceSet) {
                val distText = if (distance >= 9.5f) {
                    Math.round(distance).toString()
                } else {
                    String.format(Locale.US, "%.1f", distance)
                }
                distanceTextView.text = "$distText m"
                
                if (distance <= 20.0f) {
                    distanceTextView.setTypeface(distanceTextView.typeface, Typeface.BOLD)
                } else {
                    distanceTextView.setTypeface(Typeface.create(distanceTextView.typeface, Typeface.NORMAL))
                }
                distanceTextView.visibility = View.VISIBLE
            } else {
                distanceTextView.text = null
                distanceTextView.visibility = if (isInline) View.GONE else View.INVISIBLE
            }
        }

        val otherDistanceId = if (isInline) R.id.userDistanceTextView else R.id.userDistanceInlineTextView
        itemView.findViewById<View>(otherDistanceId)?.visibility = View.GONE

        val unreadCountTextView = itemView.findViewById<TextView>(R.id.unreadCountTextView)
        if (unreadCountTextView != null) {
            unreadCountTextView.text = unreadCount.toString()
            unreadCountTextView.visibility = if (unreadCount != 0) View.VISIBLE else View.GONE
        }

        val lastMessageText = itemView.findViewById<TextView>(R.id.lastMessageText)
        if (lastMessageText != null) {
            if (lastMessage != null) {
                lastMessageText.text = lastMessage
                lastMessageText.visibility = View.VISIBLE
            } else {
                lastMessageText.visibility = View.GONE
            }
        }

        val activeGroupIcon = itemView.findViewById<View>(R.id.activeGroupIcon)
        activeGroupIcon?.visibility = if (isActiveGroup) View.VISIBLE else View.GONE

        val chatterPicView = itemView.findViewById<ChatterPicView>(R.id.userPicView)
        if (chatterPicView != null) {
            chatterPicView.setDefaultIcon(thumbnailDefaultIcon, false)
            chatterPicView.setChatterID(thumbnailChatterID, thumbnailLabel)
            
            val visible = !(thumbnailChatterID == null && thumbnailDefaultIcon == -1)
            chatterPicView.visibility = if (visible) View.VISIBLE else View.GONE
        }

        val typingIndicator = itemView.findViewById<TypingIndicatorView>(R.id.typing_indicator)
        typingIndicator?.setChatterID(thumbnailChatterID)

        return itemView
    }

    fun reset() {
        label = null
        onlineVisible = false
        distanceSet = false
        unreadCount = 0
        lastMessage = null
        isActiveGroup = false
        thumbnailChatterID = null
        thumbnailLabel = null
        thumbnailDefaultIcon = -1
        voiceActive = false
    }

    fun setActiveGroup(active: Boolean) {
        isActiveGroup = active
    }

    fun setDistance(dist: Float) {
        if (java.lang.Float.isNaN(dist)) {
            distanceSet = false
            return
        }
        distanceSet = true
        distance = dist
    }

    fun setLabel(l: String?) {
        label = l
    }

    fun setLastMessage(msg: String?) {
        lastMessage = msg
    }

    fun setOnlineStatusIcon(visible: Boolean, online: Boolean) {
        onlineVisible = visible
        isOnline = online
    }

    fun setThumbnailChatterID(id: ChatterID?, label: String?) {
        thumbnailChatterID = id
        thumbnailLabel = label
    }

    fun setThumbnailDefaultIcon(icon: Int) {
        thumbnailDefaultIcon = icon
    }

    fun setUnreadCount(count: Int) {
        unreadCount = count
    }

    fun setVoiceActive(active: Boolean) {
        voiceActive = active
    }
}

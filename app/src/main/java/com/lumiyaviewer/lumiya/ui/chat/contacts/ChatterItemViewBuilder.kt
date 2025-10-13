package com.lumiyaviewer.lumiya.ui.chat.contacts

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView
import com.lumiyaviewer.lumiya.ui.chat.TypingIndicatorView

class ChatterItemViewBuilder {
    private var label: String? = null
    private var onlineVisible = false
    private var isOnline = false
    private var distanceSet = false
    private var distance: Float = 0f
    private var unreadCount = 0
    private var lastMessage: String? = null
    private var isActiveGroup = false
    private var thumbnailChatterID: ChatterID? = null
    private var thumbnailLabel: String? = null
    private var thumbnailDefaultIcon = -1
    private var voiceActive = false

    init {
        reset()
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

    fun setLabel(label: String?) {
        this.label = label
    }

    fun setOnlineStatusIcon(visible: Boolean, online: Boolean) {
        onlineVisible = visible
        isOnline = online
    }

    fun setDistance(distance: Float) {
        if (distance.isNaN()) {
            distanceSet = false
        } else {
            distanceSet = true
            this.distance = distance
        }
    }

    fun setUnreadCount(count: Int) {
        unreadCount = count
    }

    fun setLastMessage(message: String?) {
        lastMessage = message
    }

    fun setActiveGroup(active: Boolean) {
        isActiveGroup = active
    }

    fun setThumbnailChatterID(chatterID: ChatterID?, label: String?) {
        thumbnailChatterID = chatterID
        thumbnailLabel = label
    }

    fun setThumbnailDefaultIcon(icon: Int) {
        thumbnailDefaultIcon = icon
    }

    fun setVoiceActive(active: Boolean) {
        voiceActive = active
    }

    fun getView(
        inflater: LayoutInflater,
        convertView: View?,
        parent: ViewGroup,
        inlineDistance: Boolean
    ): View? {
        val view = if (convertView?.id == R.id.contactListItemLayout) {
            convertView
        } else {
            inflater.inflate(R.layout.contact_list_item, parent, false)
        }

        view?.let { v ->
            // Set user name
            v.findViewById<TextView>(R.id.userNameTextView)?.text = label

            // Set online icon visibility
            v.findViewById<View>(R.id.onlineUserIcon)?.visibility = 
                if (onlineVisible) View.VISIBLE else View.GONE

            // Set voice active icon
            v.findViewById<View>(R.id.activeVoiceIcon)?.visibility =
                if (voiceActive) View.VISIBLE else View.GONE

            // Set distance text
            val distanceViewId = if (inlineDistance) {
                R.id.userDistanceInlineTextView
            } else {
                R.id.userDistanceTextView
            }
            
            v.findViewById<TextView>(distanceViewId)?.let { distanceView ->
                if (distanceSet) {
                    val distanceText = if (distance >= 9.5f) {
                        "${Math.round(distance)}"
                    } else {
                        String.format("%.1f", distance)
                    } + " m"
                    
                    distanceView.text = distanceText
                    distanceView.typeface = if (distance <= 20.0f) {
                        Typeface.create(distanceView.typeface, Typeface.BOLD)
                    } else {
                        Typeface.create(distanceView.typeface, Typeface.NORMAL)
                    }
                    distanceView.visibility = View.VISIBLE
                } else {
                    distanceView.text = null
                    distanceView.visibility = if (inlineDistance) View.GONE else View.INVISIBLE
                }
            }

            // Hide the other distance view
            val otherDistanceViewId = if (inlineDistance) {
                R.id.userDistanceTextView
            } else {
                R.id.userDistanceInlineTextView
            }
            v.findViewById<View>(otherDistanceViewId)?.visibility = View.GONE

            // Set unread count
            v.findViewById<TextView>(R.id.unreadCountTextView)?.let { unreadView ->
                unreadView.text = unreadCount.toString()
                unreadView.visibility = if (unreadCount != 0) View.VISIBLE else View.GONE
            }

            // Set last message
            v.findViewById<TextView>(R.id.lastMessageText)?.let { lastMessageView ->
                if (lastMessage != null) {
                    lastMessageView.text = lastMessage
                    lastMessageView.visibility = View.VISIBLE
                } else {
                    lastMessageView.visibility = View.GONE
                }
            }

            // Set active group icon
            v.findViewById<View>(R.id.activeGroupIcon)?.visibility =
                if (isActiveGroup) View.VISIBLE else View.GONE

            // Set chatter picture
            v.findViewById<ChatterPicView>(R.id.userPicView)?.let { picView ->
                picView.setDefaultIcon(thumbnailDefaultIcon, false)
                picView.setChatterID(thumbnailChatterID, thumbnailLabel)
                picView.visibility = if (thumbnailChatterID != null || thumbnailDefaultIcon != -1) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }

            // Set typing indicator
            v.findViewById<TypingIndicatorView>(R.id.typing_indicator)?.setChatterID(thumbnailChatterID)
        }

        return view
    }
}

package com.linkpoint.ui.chat.contacts
import java.util.*

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.linkpoint.R
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.ui.chat.ChatterPicView
import com.linkpoint.ui.chat.TypingIndicatorView
import androidx.annotation.Nullable

class ChatterItemViewBuilder {
    private Float distance
    private Boolean distanceSet = false
    private Boolean isActiveGroup
    private Boolean isOnline
    private String label
    private String lastMessage
    private Boolean onlineVisible = false
    private ChatterID thumbnailChatterID
    private Int thumbnailDefaultIcon
    private String thumbnailLabel
    private Int unreadCount
    private Boolean voiceActive

    ChatterItemViewBuilder() {
        reset()
    }

    @Nullable
    fun getView(LayoutInflater layoutInflater, View view, ViewGroup viewGroup, Boolean z): View {
        Int i = R.id.userDistanceInlineTextView
        Int i2 = 8
        View view2 = (view == null || view.getId() != R.id.contactListItemLayout) ? null : view
        View inflate = view2 == null ? layoutInflater.inflate(R.layout.contact_list_item, viewGroup, false) : view2
        if (inflate != null) {
            ((inflate as TextView).findViewById(R.id.userNameTextView)).setText(this.label)
            View findViewById = inflate.findViewById(R.id.onlineUserIcon)
            if (findViewById != null) {
                if (this.onlineVisible) {
                    findViewById.setVisibility(0)
                } else {
                    findViewById.setVisibility(8)
                }
            }
            View findViewById2 = inflate.findViewById(R.id.activeVoiceIcon)
            if (findViewById2 != null) {
                findViewById2.setVisibility(this.voiceActive ? 0 : 8)
            }
            TextView textView = (inflate as TextView).findViewById(z ? R.id.userDistanceInlineTextView : R.id.userDistanceTextView)
            if (textView != null) {
                if (this.distanceSet) {
                    textView.setText((this.distance >= 9.5f ? Int.toString(Math.round(this.distance)) : String.format("%.1f", Any[]{Float.valueOf(this.distance)})) + " m")
                    if (this.distance <= 20.0f) {
                        textView.setTypeface(textView.getTypeface(), 1)
                    } else {
                        textView.setTypeface(Typeface.create(textView.getTypeface(), 0))
                    }
                    textView.setVisibility(0)
                } else {
                    textView.setText((CharSequence) null)
                    textView.setVisibility(z ? 8 : 4)
                }
            }
            if (z) {
                i = R.id.userDistanceTextView
            }
            View findViewById3 = inflate.findViewById(i)
            if (findViewById3 != null) {
                findViewById3.setVisibility(8)
            }
            TextView textView2 = (inflate as TextView).findViewById(R.id.unreadCountTextView)
            if (textView2 != null) {
                textView2.setText(Int.toString(this.unreadCount))
                if (this.unreadCount != 0) {
                    textView2.setVisibility(0)
                } else {
                    textView2.setVisibility(8)
                }
            }
            TextView textView3 = (inflate as TextView).findViewById(R.id.lastMessageText)
            if (textView3 != null) {
                if (this.lastMessage != null) {
                    textView3.setText(this.lastMessage)
                    textView3.setVisibility(0)
                } else {
                    textView3.setVisibility(8)
                }
            }
            View findViewById4 = inflate.findViewById(R.id.activeGroupIcon)
            if (findViewById4 != null) {
                findViewById4.setVisibility(this.isActiveGroup ? 0 : 8)
            }
            ChatterPicView chatterPicView = (inflate as ChatterPicView).findViewById(R.id.userPicView)
            if (chatterPicView != null) {
                chatterPicView.setDefaultIcon(this.thumbnailDefaultIcon, false)
                chatterPicView.setChatterID(this.thumbnailChatterID, this.thumbnailLabel)
                if (!(this.thumbnailChatterID == null && this.thumbnailDefaultIcon == -1)) {
                    i2 = 0
                }
                chatterPicView.setVisibility(i2)
            }
            TypingIndicatorView typingIndicatorView = (inflate as TypingIndicatorView).findViewById(R.id.typing_indicator)
            if (typingIndicatorView != null) {
                typingIndicatorView.setChatterID(this.thumbnailChatterID)
            }
        }
        return inflate
    }

    fun reset(): Unit {
        this.label = null
        this.onlineVisible = false
        this.distanceSet = false
        this.unreadCount = 0
        this.lastMessage = null
        this.isActiveGroup = false
        this.thumbnailChatterID = null
        this.thumbnailLabel = null
        this.thumbnailDefaultIcon = -1
        this.voiceActive = false
    }

    fun setActiveGroup(Boolean z): Unit {
        this.isActiveGroup = z
    }

    fun setDistance(Float f): Unit {
        if (Float.isNaN(f)) {
            this.distanceSet = false
            return
        }
        this.distanceSet = true
        this.distance = f
    }

    fun setLabel(String str): Unit {
        this.label = str
    }

    fun setLastMessage(String str): Unit {
        this.lastMessage = str
    }

    fun setOnlineStatusIcon(Boolean z, Boolean z2): Unit {
        this.onlineVisible = z
        this.isOnline = z2
    }

    fun setThumbnailChatterID(ChatterID chatterID, String str): Unit {
        this.thumbnailChatterID = chatterID
        this.thumbnailLabel = str
    }

    fun setThumbnailDefaultIcon(Int i): Unit {
        this.thumbnailDefaultIcon = i
    }

    fun setUnreadCount(Int i): Unit {
        this.unreadCount = i
    }

    fun setVoiceActive(Boolean z): Unit {
        this.voiceActive = z
    }
}

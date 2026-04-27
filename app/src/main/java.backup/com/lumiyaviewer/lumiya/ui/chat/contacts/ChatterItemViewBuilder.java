package com.lumiyaviewer.lumiya.ui.chat.contacts;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.chat.TypingIndicatorView;
import javax.annotation.Nullable;

public class ChatterItemViewBuilder {
    private float distance;
    private boolean distanceSet = false;
    private boolean isActiveGroup;
    private boolean isOnline;
    private String label;
    private String lastMessage;
    private boolean onlineVisible = false;
    private ChatterID thumbnailChatterID;
    private int thumbnailDefaultIcon;
    private String thumbnailLabel;
    private int unreadCount;
    private boolean voiceActive;

    public ChatterItemViewBuilder() {
        reset();
    }

    @Nullable
    public View getView(LayoutInflater layoutInflater, View view, ViewGroup viewGroup, boolean z) {
        int i = R.id.userDistanceInlineTextView;
        int i2 = 8;
        View view2 = (view == null || view.getId() != R.id.contactListItemLayout) ? null : view;
        View inflate = view2 == null ? layoutInflater.inflate(R.layout.contact_list_item, viewGroup, false) : view2;
        if (inflate != null) {
            ((TextView) inflate.findViewById(R.id.userNameTextView)).setText(this.label);
            View findViewById = inflate.findViewById(R.id.onlineUserIcon);
            if (findViewById != null) {
                if (this.onlineVisible) {
                    findViewById.setVisibility(0);
                } else {
                    findViewById.setVisibility(8);
                }
            }
            View findViewById2 = inflate.findViewById(R.id.activeVoiceIcon);
            if (findViewById2 != null) {
                findViewById2.setVisibility(this.voiceActive ? 0 : 8);
            }
            TextView textView = (TextView) inflate.findViewById(z ? R.id.userDistanceInlineTextView : R.id.userDistanceTextView);
            if (textView != null) {
                if (this.distanceSet) {
                    textView.setText((this.distance >= 9.5f ? Integer.toString(Math.round(this.distance)) : String.format("%.1f", new Object[]{Float.valueOf(this.distance)})) + " m");
                    if (this.distance <= 20.0f) {
                        textView.setTypeface(textView.getTypeface(), 1);
                    } else {
                        textView.setTypeface(Typeface.create(textView.getTypeface(), 0));
                    }
                    textView.setVisibility(0);
                } else {
                    textView.setText((CharSequence) null);
                    textView.setVisibility(z ? 8 : 4);
                }
            }
            if (z) {
                i = R.id.userDistanceTextView;
            }
            View findViewById3 = inflate.findViewById(i);
            if (findViewById3 != null) {
                findViewById3.setVisibility(8);
            }
            TextView textView2 = (TextView) inflate.findViewById(R.id.unreadCountTextView);
            if (textView2 != null) {
                textView2.setText(Integer.toString(this.unreadCount));
                if (this.unreadCount != 0) {
                    textView2.setVisibility(0);
                } else {
                    textView2.setVisibility(8);
                }
            }
            TextView textView3 = (TextView) inflate.findViewById(R.id.lastMessageText);
            if (textView3 != null) {
                if (this.lastMessage != null) {
                    textView3.setText(this.lastMessage);
                    textView3.setVisibility(0);
                } else {
                    textView3.setVisibility(8);
                }
            }
            View findViewById4 = inflate.findViewById(R.id.activeGroupIcon);
            if (findViewById4 != null) {
                findViewById4.setVisibility(this.isActiveGroup ? 0 : 8);
            }
            ChatterPicView chatterPicView = (ChatterPicView) inflate.findViewById(R.id.userPicView);
            if (chatterPicView != null) {
                chatterPicView.setDefaultIcon(this.thumbnailDefaultIcon, false);
                chatterPicView.setChatterID(this.thumbnailChatterID, this.thumbnailLabel);
                if (!(this.thumbnailChatterID == null && this.thumbnailDefaultIcon == -1)) {
                    i2 = 0;
                }
                chatterPicView.setVisibility(i2);
            }
            TypingIndicatorView typingIndicatorView = (TypingIndicatorView) inflate.findViewById(R.id.typing_indicator);
            if (typingIndicatorView != null) {
                typingIndicatorView.setChatterID(this.thumbnailChatterID);
            }
        }
        return inflate;
    }

    public void reset() {
        this.label = null;
        this.onlineVisible = false;
        this.distanceSet = false;
        this.unreadCount = 0;
        this.lastMessage = null;
        this.isActiveGroup = false;
        this.thumbnailChatterID = null;
        this.thumbnailLabel = null;
        this.thumbnailDefaultIcon = -1;
        this.voiceActive = false;
    }

    public void setActiveGroup(boolean z) {
        this.isActiveGroup = z;
    }

    public void setDistance(float f) {
        if (Float.isNaN(f)) {
            this.distanceSet = false;
            return;
        }
        this.distanceSet = true;
        this.distance = f;
    }

    public void setLabel(String str) {
        this.label = str;
    }

    public void setLastMessage(String str) {
        this.lastMessage = str;
    }

    public void setOnlineStatusIcon(boolean z, boolean z2) {
        this.onlineVisible = z;
        this.isOnline = z2;
    }

    public void setThumbnailChatterID(ChatterID chatterID, String str) {
        this.thumbnailChatterID = chatterID;
        this.thumbnailLabel = str;
    }

    public void setThumbnailDefaultIcon(int i) {
        this.thumbnailDefaultIcon = i;
    }

    public void setUnreadCount(int i) {
        this.unreadCount = i;
    }

    public void setVoiceActive(boolean z) {
        this.voiceActive = z;
    }
}

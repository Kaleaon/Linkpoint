// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.contacts;

import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.ui.chat.TypingIndicatorView;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import android.graphics.Typeface;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;

public class ChatterItemViewBuilder
{
    private float distance;
    private boolean distanceSet;
    private boolean isActiveGroup;
    private boolean isOnline;
    private String label;
    private String lastMessage;
    private boolean onlineVisible;
    private ChatterID thumbnailChatterID;
    private int thumbnailDefaultIcon;
    private String thumbnailLabel;
    private int unreadCount;
    private boolean voiceActive;
    
    public ChatterItemViewBuilder() {
        this.onlineVisible = false;
        this.distanceSet = false;
        this.reset();
    }
    
    @Nullable
    public View getView(final LayoutInflater layoutInflater, View view, final ViewGroup viewGroup, final boolean b) {
        final int n = 2131755330;
        final int n2 = 8;
        if (view == null || view.getId() != 2131755326) {
            view = null;
        }
        View inflate;
        if (view == null) {
            inflate = layoutInflater.inflate(2130968620, viewGroup, false);
        }
        else {
            inflate = view;
        }
        if (inflate != null) {
            ((TextView)inflate.findViewById(2131755328)).setText((CharSequence)this.label);
            view = inflate.findViewById(2131755333);
            if (view != null) {
                if (this.onlineVisible) {
                    view.setVisibility(0);
                }
                else {
                    view.setVisibility(8);
                }
            }
            view = inflate.findViewById(2131755331);
            if (view != null) {
                int visibility;
                if (this.voiceActive) {
                    visibility = 0;
                }
                else {
                    visibility = 8;
                }
                view.setVisibility(visibility);
            }
            int n3;
            if (b) {
                n3 = 2131755330;
            }
            else {
                n3 = 2131755335;
            }
            final TextView textView = (TextView)inflate.findViewById(n3);
            if (textView != null) {
                if (this.distanceSet) {
                    String str;
                    if (this.distance >= 9.5f) {
                        str = Integer.toString(Math.round(this.distance));
                    }
                    else {
                        str = String.format("%.1f", this.distance);
                    }
                    textView.setText((CharSequence)(str + " m"));
                    if (this.distance <= 20.0f) {
                        textView.setTypeface(textView.getTypeface(), 1);
                    }
                    else {
                        textView.setTypeface(Typeface.create(textView.getTypeface(), 0));
                    }
                    textView.setVisibility(0);
                }
                else {
                    textView.setText((CharSequence)null);
                    int visibility2;
                    if (b) {
                        visibility2 = 8;
                    }
                    else {
                        visibility2 = 4;
                    }
                    textView.setVisibility(visibility2);
                }
            }
            int n4;
            if (!b) {
                n4 = n;
            }
            else {
                n4 = 2131755335;
            }
            view = inflate.findViewById(n4);
            if (view != null) {
                view.setVisibility(8);
            }
            final TextView textView2 = (TextView)inflate.findViewById(2131755332);
            if (textView2 != null) {
                textView2.setText((CharSequence)Integer.toString(this.unreadCount));
                if (this.unreadCount != 0) {
                    textView2.setVisibility(0);
                }
                else {
                    textView2.setVisibility(8);
                }
            }
            final TextView textView3 = (TextView)inflate.findViewById(2131755329);
            if (textView3 != null) {
                if (this.lastMessage != null) {
                    textView3.setText((CharSequence)this.lastMessage);
                    textView3.setVisibility(0);
                }
                else {
                    textView3.setVisibility(8);
                }
            }
            view = inflate.findViewById(2131755334);
            if (view != null) {
                int visibility3;
                if (this.isActiveGroup) {
                    visibility3 = 0;
                }
                else {
                    visibility3 = 8;
                }
                view.setVisibility(visibility3);
            }
            final ChatterPicView chatterPicView = (ChatterPicView)inflate.findViewById(2131755327);
            if (chatterPicView != null) {
                chatterPicView.setDefaultIcon(this.thumbnailDefaultIcon, false);
                chatterPicView.setChatterID(this.thumbnailChatterID, this.thumbnailLabel);
                int visibility4 = 0;
                Label_0383: {
                    if (this.thumbnailChatterID == null) {
                        visibility4 = n2;
                        if (this.thumbnailDefaultIcon == -1) {
                            break Label_0383;
                        }
                    }
                    visibility4 = 0;
                }
                chatterPicView.setVisibility(visibility4);
            }
            final TypingIndicatorView typingIndicatorView = (TypingIndicatorView)inflate.findViewById(2131755296);
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
    
    public void setActiveGroup(final boolean isActiveGroup) {
        this.isActiveGroup = isActiveGroup;
    }
    
    public void setDistance(final float n) {
        if (Float.isNaN(n)) {
            this.distanceSet = false;
        }
        else {
            this.distanceSet = true;
            this.distance = n;
        }
    }
    
    public void setLabel(final String label) {
        this.label = label;
    }
    
    public void setLastMessage(final String lastMessage) {
        this.lastMessage = lastMessage;
    }
    
    public void setOnlineStatusIcon(final boolean onlineVisible, final boolean isOnline) {
        this.onlineVisible = onlineVisible;
        this.isOnline = isOnline;
    }
    
    public void setThumbnailChatterID(final ChatterID thumbnailChatterID, final String thumbnailLabel) {
        this.thumbnailChatterID = thumbnailChatterID;
        this.thumbnailLabel = thumbnailLabel;
    }
    
    public void setThumbnailDefaultIcon(final int thumbnailDefaultIcon) {
        this.thumbnailDefaultIcon = thumbnailDefaultIcon;
    }
    
    public void setUnreadCount(final int unreadCount) {
        this.unreadCount = unreadCount;
    }
    
    public void setVoiceActive(final boolean voiceActive) {
        this.voiceActive = voiceActive;
    }
}

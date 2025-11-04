// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.text.format.DateUtils;
import android.content.Context;
import android.view.View$OnClickListener;
import com.lumiyaviewer.lumiya.ui.chat.HasUserPicClickHandler;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import android.view.View;
import java.lang.ref.WeakReference;
import android.support.v7.widget.RecyclerView;

public class ChatEventViewHolder extends ViewHolder
{
    protected final WeakReference<Adapter> adapter;
    final View bubbleView;
    final ChatterPicView chatSourceIcon;
    final ChatterPicView chatSourceIconRight;
    final TextView textView;
    final TextView timestampView;
    private long updateTimestamp;
    
    public ChatEventViewHolder(final View view, final Adapter referent) {
        super(view);
        this.updateTimestamp = 0L;
        this.adapter = new WeakReference<Adapter>(referent);
        this.timestampView = (TextView)view.findViewById(2131755301);
        this.textView = (TextView)view.findViewById(2131755300);
        this.bubbleView = view.findViewById(2131755299);
        this.chatSourceIcon = (ChatterPicView)view.findViewById(2131755298);
        this.chatSourceIconRight = (ChatterPicView)view.findViewById(2131755302);
        if (referent instanceof HasUserPicClickHandler) {
            final View$OnClickListener userPicClickListener = ((HasUserPicClickHandler)referent).getUserPicClickListener();
            if (userPicClickListener != null) {
                if (this.chatSourceIcon != null) {
                    this.chatSourceIcon.setOnClickListener(userPicClickListener);
                }
                if (this.chatSourceIconRight != null) {
                    this.chatSourceIconRight.setOnClickListener(userPicClickListener);
                }
            }
        }
    }
    
    void requestAdapterUpdate() {
        final Adapter adapter = (Adapter)this.adapter.get();
        if (adapter != null) {
            adapter.notifyItemChanged(((RecyclerView.ViewHolder)this).getAdapterPosition());
        }
    }
    
    void setupTimestampUpdate(final Context context, final long updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
        this.updateTimestamp(context);
    }
    
    public void updateTimestamp(final Context context) {
        if (this.timestampView != null) {
            if (this.updateTimestamp != 0L) {
                final long currentTimeMillis = System.currentTimeMillis();
                CharSequence text;
                if (currentTimeMillis < this.updateTimestamp + 60000L) {
                    text = context.getString(2131296782);
                }
                else {
                    text = DateUtils.getRelativeTimeSpanString(this.updateTimestamp, currentTimeMillis, 60000L, 262144);
                }
                this.timestampView.setText(text);
                this.timestampView.setVisibility(0);
            }
            else {
                this.timestampView.setVisibility(8);
            }
        }
    }
    
    interface Factory
    {
        ChatEventViewHolder createViewHolder(final View p0, final Adapter p1);
    }
}

// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat;

import java.util.Iterator;
import java.util.Map;
import java.util.Collections;
import java.util.WeakHashMap;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder;
import java.util.Set;
import android.os.Handler;
import android.content.Context;

public class ChatEventTimestampUpdater
{
    private static final long TIMESTAMP_UPDATE_INTERVAL = 60000L;
    private final Context context;
    private final Handler mHandler;
    private final Runnable updateRunnable;
    private boolean updateRunnablePosted;
    private final Set<ChatEventViewHolder> viewHolders;
    
    public ChatEventTimestampUpdater(final Context context) {
        this.mHandler = new Handler();
        this.updateRunnablePosted = false;
        this.viewHolders = Collections.newSetFromMap(new WeakHashMap<ChatEventViewHolder, Boolean>());
        this.updateRunnable = new Runnable() {
            @Override
            public void run() {
                ChatEventTimestampUpdater.this.updateRunnablePosted = false;
                for (final ChatEventViewHolder chatEventViewHolder : ChatEventTimestampUpdater.this.viewHolders) {
                    if (chatEventViewHolder != null) {
                        chatEventViewHolder.updateTimestamp(ChatEventTimestampUpdater.this.context);
                    }
                }
                if (!ChatEventTimestampUpdater.this.viewHolders.isEmpty()) {
                    ChatEventTimestampUpdater.this.updateRunnablePosted = true;
                    ChatEventTimestampUpdater.this.mHandler.postDelayed(ChatEventTimestampUpdater.this.updateRunnable, 60000L);
                }
            }
        };
        this.context = context;
    }
    
    public void addViewHolder(final ChatEventViewHolder chatEventViewHolder) {
        this.viewHolders.add(chatEventViewHolder);
        if (!this.updateRunnablePosted) {
            this.updateRunnablePosted = true;
            this.mHandler.postDelayed(this.updateRunnable, 60000L);
        }
    }
    
    public void removeViewHolder(final ChatEventViewHolder chatEventViewHolder) {
        this.viewHolders.remove(chatEventViewHolder);
    }
}

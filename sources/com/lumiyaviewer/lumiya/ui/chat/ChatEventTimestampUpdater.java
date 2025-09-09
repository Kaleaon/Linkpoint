package com.lumiyaviewer.lumiya.ui.chat;

import android.content.Context;
import android.os.Handler;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class ChatEventTimestampUpdater {
    private static final long TIMESTAMP_UPDATE_INTERVAL = 60000;
    /* access modifiers changed from: private */
    public final Context context;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler();
    /* access modifiers changed from: private */
    public final Runnable updateRunnable = new Runnable() {
        public void run() {
            boolean unused = ChatEventTimestampUpdater.this.updateRunnablePosted = false;
            for (ChatEventViewHolder chatEventViewHolder : ChatEventTimestampUpdater.this.viewHolders) {
                if (chatEventViewHolder != null) {
                    chatEventViewHolder.updateTimestamp(ChatEventTimestampUpdater.this.context);
                }
            }
            if (!ChatEventTimestampUpdater.this.viewHolders.isEmpty()) {
                boolean unused2 = ChatEventTimestampUpdater.this.updateRunnablePosted = true;
                ChatEventTimestampUpdater.this.mHandler.postDelayed(ChatEventTimestampUpdater.this.updateRunnable, 60000);
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean updateRunnablePosted = false;
    /* access modifiers changed from: private */
    public final Set<ChatEventViewHolder> viewHolders = Collections.newSetFromMap(new WeakHashMap());

    public ChatEventTimestampUpdater(Context context2) {
        this.context = context2;
    }

    public void addViewHolder(ChatEventViewHolder chatEventViewHolder) {
        this.viewHolders.add(chatEventViewHolder);
        if (!this.updateRunnablePosted) {
            this.updateRunnablePosted = true;
            this.mHandler.postDelayed(this.updateRunnable, 60000);
        }
    }

    public void removeViewHolder(ChatEventViewHolder chatEventViewHolder) {
        this.viewHolders.remove(chatEventViewHolder);
    }
}

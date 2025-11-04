package com.lumiyaviewer.lumiya.ui.chat;

import android.content.Context;
import android.os.Handler;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public class ChatEventTimestampUpdater {
    private static final long TIMESTAMP_UPDATE_INTERVAL = 60000;
    private final Context context;
    private final Handler mHandler = new Handler();
    private boolean updateRunnablePosted = false;
    private final Set<ChatEventViewHolder> viewHolders = Collections.newSetFromMap(new WeakHashMap());
    private final Runnable updateRunnable = new Runnable() { // from class: com.lumiyaviewer.lumiya.ui.chat.ChatEventTimestampUpdater.1
        @Override // java.lang.Runnable
        public void run() {
            ChatEventTimestampUpdater.this.updateRunnablePosted = false;
            for (ChatEventViewHolder chatEventViewHolder : ChatEventTimestampUpdater.this.viewHolders) {
                if (chatEventViewHolder != null) {
                    chatEventViewHolder.updateTimestamp(ChatEventTimestampUpdater.this.context);
                }
            }
            if (ChatEventTimestampUpdater.this.viewHolders.isEmpty()) {
                return;
            }
            ChatEventTimestampUpdater.this.updateRunnablePosted = true;
            ChatEventTimestampUpdater.this.mHandler.postDelayed(ChatEventTimestampUpdater.this.updateRunnable, 60000L);
        }
    };

    public ChatEventTimestampUpdater(Context context) {
        this.context = context;
    }

    public void addViewHolder(ChatEventViewHolder chatEventViewHolder) {
        this.viewHolders.add(chatEventViewHolder);
        if (this.updateRunnablePosted) {
            return;
        }
        this.updateRunnablePosted = true;
        this.mHandler.postDelayed(this.updateRunnable, 60000L);
    }

    public void removeViewHolder(ChatEventViewHolder chatEventViewHolder) {
        this.viewHolders.remove(chatEventViewHolder);
    }
}

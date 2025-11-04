// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat;

import com.lumiyaviewer.lumiya.Debug;
import android.view.ViewGroup;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import android.view.View;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import android.content.Context;
import android.view.View$OnClickListener;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.lang.ref.WeakReference;
import android.view.LayoutInflater;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatMessageLoader;
import com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader;
import android.support.v7.widget.RecyclerView;

class ChatRecyclerAdapter extends Adapter implements EventListener, HasUserPicClickHandler
{
    @Nullable
    private ChatMessageLoader chatMessageLoader;
    @Nonnull
    private final ChatterID chatterID;
    private final LayoutInflater inflater;
    private WeakReference<OnAdapterDataChanged> onAdapterDataChangedListener;
    private WeakReference<OnUserPicClickedListener> onUserPicClickedListener;
    private final ChatEventTimestampUpdater timestampUpdater;
    @Nonnull
    private final UserManager userManager;
    private final View$OnClickListener userPicClickListener;
    
    ChatRecyclerAdapter(final Context context, @Nonnull final UserManager userManager, @Nonnull final ChatterID chatterID) {
        this.chatMessageLoader = null;
        this.onAdapterDataChangedListener = new WeakReference<OnAdapterDataChanged>(null);
        this.onUserPicClickedListener = new WeakReference<OnUserPicClickedListener>(null);
        this.userPicClickListener = (View$OnClickListener)new _$Lambda$aLaDwVKcksSTh8O8aNFE_CFHRQc(this);
        this.inflater = LayoutInflater.from(context);
        this.timestampUpdater = new ChatEventTimestampUpdater(context);
        this.userManager = userManager;
        this.chatterID = chatterID;
        this.setHasStableIds(true);
    }
    
    @Override
    public int getItemCount() {
        int size;
        if (this.chatMessageLoader != null) {
            size = this.chatMessageLoader.size();
        }
        else {
            size = 0;
        }
        return size;
    }
    
    @Override
    public long getItemId(final int n) {
        long longValue;
        if (this.chatMessageLoader != null) {
            longValue = this.chatMessageLoader.get(n).getId();
        }
        else {
            longValue = -1L;
        }
        return longValue;
    }
    
    @Override
    public int getItemViewType(int viewType) {
        if (this.chatMessageLoader != null) {
            viewType = this.chatMessageLoader.get(viewType).getViewType();
        }
        else {
            viewType = 0;
        }
        return viewType;
    }
    
    @Nonnull
    @Override
    public Executor getListEventsExecutor() {
        return UIThreadExecutor.getSerialInstance();
    }
    
    @Override
    public View$OnClickListener getUserPicClickListener() {
        return this.userPicClickListener;
    }
    
    boolean hasMoreItemsAtBottom() {
        return this.chatMessageLoader != null && this.chatMessageLoader.hasMoreItemsAtBottom();
    }
    
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int n) {
        if (this.chatMessageLoader != null) {
            final SLChatEvent loadFromDatabaseObject = SLChatEvent.loadFromDatabaseObject(this.chatMessageLoader.get(n), this.userManager.getUserID());
            if (loadFromDatabaseObject != null && viewHolder instanceof ChatEventViewHolder) {
                loadFromDatabaseObject.bindViewHolder((ChatEventViewHolder)viewHolder, this.userManager, this.timestampUpdater);
            }
        }
    }
    
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
        return SLChatEvent.createViewHolder(this.inflater, n, viewGroup, this);
    }
    
    @Override
    public void onListItemAddedAtEnd() {
        if (this.chatMessageLoader != null) {
            final OnAdapterDataChanged onAdapterDataChanged = this.onAdapterDataChangedListener.get();
            if (onAdapterDataChanged != null) {
                onAdapterDataChanged.onAdapterDataAddedAtEnd();
            }
        }
    }
    
    @Override
    public void onListItemChanged(final int i) {
        if (this.chatMessageLoader != null) {
            Debug.Printf("ChatView: item changed: position %d", i);
            this.notifyItemChanged(i);
            final OnAdapterDataChanged onAdapterDataChanged = this.onAdapterDataChangedListener.get();
            if (onAdapterDataChanged != null) {
                onAdapterDataChanged.onAdapterDataChanged();
            }
        }
    }
    
    @Override
    public void onListItemsAdded(final int i, final int j) {
        if (this.chatMessageLoader != null) {
            Debug.Printf("ChatView: items added: new size %d, position %d, count %d", this.chatMessageLoader.size(), i, j);
            this.notifyItemRangeInserted(i, j);
            final OnAdapterDataChanged onAdapterDataChanged = this.onAdapterDataChangedListener.get();
            if (onAdapterDataChanged != null) {
                onAdapterDataChanged.onAdapterDataChanged();
            }
        }
    }
    
    @Override
    public void onListItemsRemoved(final int i, final int j) {
        if (this.chatMessageLoader != null) {
            Debug.Printf("ChatView: items removed: new size %d, position %d, count %d", this.chatMessageLoader.size(), i, j);
            this.notifyItemRangeRemoved(i, j);
            final OnAdapterDataChanged onAdapterDataChanged = this.onAdapterDataChangedListener.get();
            if (onAdapterDataChanged != null) {
                onAdapterDataChanged.onAdapterDataChanged();
            }
        }
    }
    
    @Override
    public void onListReloaded() {
        if (this.chatMessageLoader != null) {
            Debug.Printf("ChatView: list cleared", new Object[0]);
            this.notifyDataSetChanged();
            final OnAdapterDataChanged onAdapterDataChanged = this.onAdapterDataChangedListener.get();
            if (onAdapterDataChanged != null) {
                onAdapterDataChanged.onAdapterDataReloaded();
            }
        }
    }
    
    @Override
    public void onViewRecycled(final ViewHolder viewHolder) {
        if (viewHolder instanceof ChatEventViewHolder) {
            this.timestampUpdater.removeViewHolder((ChatEventViewHolder)viewHolder);
        }
    }
    
    void restartAtBottom() {
        if (this.chatMessageLoader != null) {
            this.chatMessageLoader.reload();
        }
    }
    
    void setOnUserPicClickedListener(final OnUserPicClickedListener referent) {
        this.onUserPicClickedListener = new WeakReference<OnUserPicClickedListener>(referent);
    }
    
    void setVisibleRange(final int i, final int j) {
        if (this.chatMessageLoader != null) {
            Debug.Printf("ChatView: visible range from %d to %d", i, j);
            this.chatMessageLoader.setVisibleRange(i, j);
        }
    }
    
    void startLoading(@Nonnull final OnAdapterDataChanged referent) {
        this.onAdapterDataChangedListener = new WeakReference<OnAdapterDataChanged>(referent);
        this.chatMessageLoader = this.userManager.getChatterList().getActiveChattersManager().getMessageLoader(this.chatterID, this);
        this.notifyDataSetChanged();
    }
    
    void stopLoading() {
        this.userManager.getChatterList().getActiveChattersManager().releaseMessageLoader(this.chatterID, this.chatMessageLoader);
        this.chatMessageLoader = null;
        this.onAdapterDataChangedListener.clear();
        this.notifyDataSetChanged();
    }
    
    interface OnAdapterDataChanged
    {
        void onAdapterDataAddedAtEnd();
        
        void onAdapterDataChanged();
        
        void onAdapterDataReloaded();
    }
    
    interface OnUserPicClickedListener
    {
        void onUserPicClicked(final ChatMessageSource p0);
    }
}

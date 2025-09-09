package com.lumiyaviewer.lumiya.ui.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatMessageLoader;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class ChatRecyclerAdapter extends RecyclerView.Adapter implements ChunkedListLoader.EventListener, HasUserPicClickHandler {
    @Nullable
    private ChatMessageLoader chatMessageLoader = null;
    @Nonnull
    private final ChatterID chatterID;
    private final LayoutInflater inflater;
    private WeakReference<OnAdapterDataChanged> onAdapterDataChangedListener = new WeakReference<>((Object) null);
    private WeakReference<OnUserPicClickedListener> onUserPicClickedListener = new WeakReference<>((Object) null);
    private final ChatEventTimestampUpdater timestampUpdater;
    @Nonnull
    private final UserManager userManager;
    private final View.OnClickListener userPicClickListener = new $Lambda$aLaDwVKcksSTh8O8aNFE_CFHRQc(this);

    interface OnAdapterDataChanged {
        void onAdapterDataAddedAtEnd();

        void onAdapterDataChanged();

        void onAdapterDataReloaded();
    }

    interface OnUserPicClickedListener {
        void onUserPicClicked(ChatMessageSource chatMessageSource);
    }

    ChatRecyclerAdapter(Context context, @Nonnull UserManager userManager2, @Nonnull ChatterID chatterID2) {
        this.inflater = LayoutInflater.from(context);
        this.timestampUpdater = new ChatEventTimestampUpdater(context);
        this.userManager = userManager2;
        this.chatterID = chatterID2;
        setHasStableIds(true);
    }

    public int getItemCount() {
        if (this.chatMessageLoader != null) {
            return this.chatMessageLoader.size();
        }
        return 0;
    }

    public long getItemId(int i) {
        if (this.chatMessageLoader != null) {
            return ((ChatMessage) this.chatMessageLoader.get(i)).getId().longValue();
        }
        return -1;
    }

    public int getItemViewType(int i) {
        if (this.chatMessageLoader != null) {
            return ((ChatMessage) this.chatMessageLoader.get(i)).getViewType();
        }
        return 0;
    }

    @Nonnull
    public Executor getListEventsExecutor() {
        return UIThreadExecutor.getSerialInstance();
    }

    public View.OnClickListener getUserPicClickListener() {
        return this.userPicClickListener;
    }

    /* access modifiers changed from: package-private */
    public boolean hasMoreItemsAtBottom() {
        if (this.chatMessageLoader != null) {
            return this.chatMessageLoader.hasMoreItemsAtBottom();
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_ChatRecyclerAdapter_7040  reason: not valid java name */
    public /* synthetic */ void m415lambda$com_lumiyaviewer_lumiya_ui_chat_ChatRecyclerAdapter_7040(View view) {
        ChatMessageSource attachedMessageSource;
        OnUserPicClickedListener onUserPicClickedListener2 = (OnUserPicClickedListener) this.onUserPicClickedListener.get();
        if (onUserPicClickedListener2 != null && (view instanceof ChatterPicView) && (attachedMessageSource = ((ChatterPicView) view).getAttachedMessageSource()) != null) {
            onUserPicClickedListener2.onUserPicClicked(attachedMessageSource);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        SLChatEvent loadFromDatabaseObject;
        if (this.chatMessageLoader != null && (loadFromDatabaseObject = SLChatEvent.loadFromDatabaseObject((ChatMessage) this.chatMessageLoader.get(i), this.userManager.getUserID())) != null && (viewHolder instanceof ChatEventViewHolder)) {
            loadFromDatabaseObject.bindViewHolder((ChatEventViewHolder) viewHolder, this.userManager, this.timestampUpdater);
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return SLChatEvent.createViewHolder(this.inflater, i, viewGroup, this);
    }

    public void onListItemAddedAtEnd() {
        OnAdapterDataChanged onAdapterDataChanged;
        if (this.chatMessageLoader != null && (onAdapterDataChanged = (OnAdapterDataChanged) this.onAdapterDataChangedListener.get()) != null) {
            onAdapterDataChanged.onAdapterDataAddedAtEnd();
        }
    }

    public void onListItemChanged(int i) {
        if (this.chatMessageLoader != null) {
            Debug.Printf("ChatView: item changed: position %d", Integer.valueOf(i));
            notifyItemChanged(i);
            OnAdapterDataChanged onAdapterDataChanged = (OnAdapterDataChanged) this.onAdapterDataChangedListener.get();
            if (onAdapterDataChanged != null) {
                onAdapterDataChanged.onAdapterDataChanged();
            }
        }
    }

    public void onListItemsAdded(int i, int i2) {
        if (this.chatMessageLoader != null) {
            Debug.Printf("ChatView: items added: new size %d, position %d, count %d", Integer.valueOf(this.chatMessageLoader.size()), Integer.valueOf(i), Integer.valueOf(i2));
            notifyItemRangeInserted(i, i2);
            OnAdapterDataChanged onAdapterDataChanged = (OnAdapterDataChanged) this.onAdapterDataChangedListener.get();
            if (onAdapterDataChanged != null) {
                onAdapterDataChanged.onAdapterDataChanged();
            }
        }
    }

    public void onListItemsRemoved(int i, int i2) {
        if (this.chatMessageLoader != null) {
            Debug.Printf("ChatView: items removed: new size %d, position %d, count %d", Integer.valueOf(this.chatMessageLoader.size()), Integer.valueOf(i), Integer.valueOf(i2));
            notifyItemRangeRemoved(i, i2);
            OnAdapterDataChanged onAdapterDataChanged = (OnAdapterDataChanged) this.onAdapterDataChangedListener.get();
            if (onAdapterDataChanged != null) {
                onAdapterDataChanged.onAdapterDataChanged();
            }
        }
    }

    public void onListReloaded() {
        if (this.chatMessageLoader != null) {
            Debug.Printf("ChatView: list cleared", new Object[0]);
            notifyDataSetChanged();
            OnAdapterDataChanged onAdapterDataChanged = (OnAdapterDataChanged) this.onAdapterDataChangedListener.get();
            if (onAdapterDataChanged != null) {
                onAdapterDataChanged.onAdapterDataReloaded();
            }
        }
    }

    public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ChatEventViewHolder) {
            this.timestampUpdater.removeViewHolder((ChatEventViewHolder) viewHolder);
        }
    }

    /* access modifiers changed from: package-private */
    public void restartAtBottom() {
        if (this.chatMessageLoader != null) {
            this.chatMessageLoader.reload();
        }
    }

    /* access modifiers changed from: package-private */
    public void setOnUserPicClickedListener(OnUserPicClickedListener onUserPicClickedListener2) {
        this.onUserPicClickedListener = new WeakReference<>(onUserPicClickedListener2);
    }

    /* access modifiers changed from: package-private */
    public void setVisibleRange(int i, int i2) {
        if (this.chatMessageLoader != null) {
            Debug.Printf("ChatView: visible range from %d to %d", Integer.valueOf(i), Integer.valueOf(i2));
            this.chatMessageLoader.setVisibleRange(i, i2);
        }
    }

    /* access modifiers changed from: package-private */
    public void startLoading(@Nonnull OnAdapterDataChanged onAdapterDataChanged) {
        this.onAdapterDataChangedListener = new WeakReference<>(onAdapterDataChanged);
        this.chatMessageLoader = this.userManager.getChatterList().getActiveChattersManager().getMessageLoader(this.chatterID, this);
        notifyDataSetChanged();
    }

    /* access modifiers changed from: package-private */
    public void stopLoading() {
        this.userManager.getChatterList().getActiveChattersManager().releaseMessageLoader(this.chatterID, this.chatMessageLoader);
        this.chatMessageLoader = null;
        this.onAdapterDataChangedListener.clear();
        notifyDataSetChanged();
    }
}

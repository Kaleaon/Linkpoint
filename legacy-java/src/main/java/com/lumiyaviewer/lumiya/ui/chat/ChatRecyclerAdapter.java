package com.lumiyaviewer.lumiya.ui.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lumiyaviewer.lumiya.Debug;
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

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ChatRecyclerAdapter extends RecyclerView.Adapter implements ChunkedListLoader.EventListener, HasUserPicClickHandler {

    @Nonnull
    private final ChatterID chatterID;
    private final LayoutInflater inflater;
    private final ChatEventTimestampUpdater timestampUpdater;

    @Nonnull
    private final UserManager userManager;

    @Nullable
    private ChatMessageLoader chatMessageLoader = null;
    private WeakReference<OnAdapterDataChanged> onAdapterDataChangedListener = new WeakReference<>(null);
    private WeakReference<OnUserPicClickedListener> onUserPicClickedListener = new WeakReference<>(null);
    private final View.OnClickListener userPicClickListener = new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$aLaDwVKcksSTh8O8aNFE_CFHRQc
        private final /* synthetic */ void $m$0(View view) {
            ((ChatRecyclerAdapter) this).m425lambda$com_lumiyaviewer_lumiya_ui_chat_ChatRecyclerAdapter_7040(view);
        }

        @Override // android.view.View.OnClickListener
        public final void onClick(View view) {
            $m$0(view);
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface OnAdapterDataChanged {
        void onAdapterDataAddedAtEnd();

        void onAdapterDataChanged();

        void onAdapterDataReloaded();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface OnUserPicClickedListener {
        void onUserPicClicked(ChatMessageSource chatMessageSource);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChatRecyclerAdapter(Context context, @Nonnull UserManager userManager, @Nonnull ChatterID chatterID) {
        this.inflater = LayoutInflater.from(context);
        this.timestampUpdater = new ChatEventTimestampUpdater(context);
        this.userManager = userManager;
        this.chatterID = chatterID;
        setHasStableIds(true);
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public int getItemCount() {
        if (this.chatMessageLoader != null) {
            return this.chatMessageLoader.size();
        }
        return 0;
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        if (this.chatMessageLoader != null) {
            return this.chatMessageLoader.get(i).getId().longValue();
        }
        return -1L;
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        if (this.chatMessageLoader != null) {
            return this.chatMessageLoader.get(i).getViewType();
        }
        return 0;
    }

    @Override // com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader.EventListener
    @Nonnull
    public Executor getListEventsExecutor() {
        return UIThreadExecutor.getSerialInstance();
    }

    @Override // com.lumiyaviewer.lumiya.ui.chat.HasUserPicClickHandler
    public View.OnClickListener getUserPicClickListener() {
        return this.userPicClickListener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasMoreItemsAtBottom() {
        if (this.chatMessageLoader != null) {
            return this.chatMessageLoader.hasMoreItemsAtBottom();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_ChatRecyclerAdapter_7040, reason: not valid java name */
    public /* synthetic */ void m425lambda$com_lumiyaviewer_lumiya_ui_chat_ChatRecyclerAdapter_7040(View view) {
        ChatMessageSource attachedMessageSource;
        OnUserPicClickedListener onUserPicClickedListener = this.onUserPicClickedListener.get();
        if (onUserPicClickedListener == null || !(view instanceof ChatterPicView) || (attachedMessageSource = ((ChatterPicView) view).getAttachedMessageSource()) == null) {
            return;
        }
        onUserPicClickedListener.onUserPicClicked(attachedMessageSource);
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        SLChatEvent loadFromDatabaseObject;
        if (this.chatMessageLoader == null || (loadFromDatabaseObject = SLChatEvent.loadFromDatabaseObject(this.chatMessageLoader.get(i), this.userManager.getUserID())) == null || !(viewHolder instanceof ChatEventViewHolder)) {
            return;
        }
        loadFromDatabaseObject.bindViewHolder((ChatEventViewHolder) viewHolder, this.userManager, this.timestampUpdater);
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return SLChatEvent.createViewHolder(this.inflater, i, viewGroup, this);
    }

    @Override // com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader.EventListener
    public void onListItemAddedAtEnd() {
        OnAdapterDataChanged onAdapterDataChanged;
        if (this.chatMessageLoader == null || (onAdapterDataChanged = this.onAdapterDataChangedListener.get()) == null) {
            return;
        }
        onAdapterDataChanged.onAdapterDataAddedAtEnd();
    }

    @Override // com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader.EventListener
    public void onListItemChanged(int i) {
        if (this.chatMessageLoader != null) {
            Debug.Printf("ChatView: item changed: position %d", Integer.valueOf(i));
            notifyItemChanged(i);
            OnAdapterDataChanged onAdapterDataChanged = this.onAdapterDataChangedListener.get();
            if (onAdapterDataChanged != null) {
                onAdapterDataChanged.onAdapterDataChanged();
            }
        }
    }

    @Override // com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader.EventListener
    public void onListItemsAdded(int i, int i2) {
        if (this.chatMessageLoader != null) {
            Debug.Printf("ChatView: items added: new size %d, position %d, count %d", Integer.valueOf(this.chatMessageLoader.size()), Integer.valueOf(i), Integer.valueOf(i2));
            notifyItemRangeInserted(i, i2);
            OnAdapterDataChanged onAdapterDataChanged = this.onAdapterDataChangedListener.get();
            if (onAdapterDataChanged != null) {
                onAdapterDataChanged.onAdapterDataChanged();
            }
        }
    }

    @Override // com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader.EventListener
    public void onListItemsRemoved(int i, int i2) {
        if (this.chatMessageLoader != null) {
            Debug.Printf("ChatView: items removed: new size %d, position %d, count %d", Integer.valueOf(this.chatMessageLoader.size()), Integer.valueOf(i), Integer.valueOf(i2));
            notifyItemRangeRemoved(i, i2);
            OnAdapterDataChanged onAdapterDataChanged = this.onAdapterDataChangedListener.get();
            if (onAdapterDataChanged != null) {
                onAdapterDataChanged.onAdapterDataChanged();
            }
        }
    }

    @Override // com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader.EventListener
    public void onListReloaded() {
        if (this.chatMessageLoader != null) {
            Debug.Printf("ChatView: list cleared", new Object[0]);
            notifyDataSetChanged();
            OnAdapterDataChanged onAdapterDataChanged = this.onAdapterDataChangedListener.get();
            if (onAdapterDataChanged != null) {
                onAdapterDataChanged.onAdapterDataReloaded();
            }
        }
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ChatEventViewHolder) {
            this.timestampUpdater.removeViewHolder((ChatEventViewHolder) viewHolder);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void restartAtBottom() {
        if (this.chatMessageLoader != null) {
            this.chatMessageLoader.reload();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOnUserPicClickedListener(OnUserPicClickedListener onUserPicClickedListener) {
        this.onUserPicClickedListener = new WeakReference<>(onUserPicClickedListener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setVisibleRange(int i, int i2) {
        if (this.chatMessageLoader != null) {
            Debug.Printf("ChatView: visible range from %d to %d", Integer.valueOf(i), Integer.valueOf(i2));
            this.chatMessageLoader.setVisibleRange(i, i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startLoading(@Nonnull OnAdapterDataChanged onAdapterDataChanged) {
        this.onAdapterDataChangedListener = new WeakReference<>(onAdapterDataChanged);
        this.chatMessageLoader = this.userManager.getChatterList().getActiveChattersManager().getMessageLoader(this.chatterID, this);
        notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void stopLoading() {
        this.userManager.getChatterList().getActiveChattersManager().releaseMessageLoader(this.chatterID, this.chatMessageLoader);
        this.chatMessageLoader = null;
        this.onAdapterDataChangedListener.clear();
        notifyDataSetChanged();
    }
}

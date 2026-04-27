// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.contacts;

import android.widget.TextView;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener;
import android.view.ViewGroup;
import android.view.View;
import java.io.IOException;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadMessageInfo;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.view.LayoutInflater;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterDisplayData;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType;
import com.lumiyaviewer.lumiya.react.Subscription;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.ui.common.DismissableAdapter;
import java.io.Closeable;
import android.widget.BaseAdapter;

public class ActiveChatsListAdapter extends BaseAdapter implements Closeable, DismissableAdapter
{
    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_ROW = 0;
    @Nonnull
    private ImmutableList<? extends ChatterDisplayInfo> activeChatters;
    private final Subscription<ChatterListType, ImmutableList<ChatterDisplayData>> activeChattersSubscription;
    private final Context context;
    @Nullable
    private CurrentLocationInfo currentLocationInfo;
    private final Subscription<SubscriptionSingleKey, CurrentLocationInfo> currentLocationInfoSubscription;
    private final LayoutInflater inflater;
    private final LocalChatItem localChatItem;
    private final Subscription<ChatterID, UnreadMessageInfo> localChatUnreadCountSubscription;
    private final Subscription<ChatterID, VoiceChatInfo> localVoiceChatSubscription;
    @Nonnull
    private ImmutableList<? extends ChatterDisplayInfo> onlineFriends;
    private final OnlineFriendsHeaderRow onlineFriendsHeader;
    private final Subscription<ChatterListType, ImmutableList<ChatterDisplayData>> onlineFriendsSubscription;
    private final UserManager userManager;
    private final ChatterItemViewBuilder viewBuilder;
    
    public ActiveChatsListAdapter(final Context context, final UserManager userManager) {
        this.viewBuilder = new ChatterItemViewBuilder();
        this.activeChatters = ImmutableList.of();
        this.onlineFriends = ImmutableList.of();
        this.currentLocationInfo = null;
        this.context = context;
        this.userManager = userManager;
        this.inflater = LayoutInflater.from(context);
        this.localChatItem = new LocalChatItem(ChatterID.getLocalChatterID(userManager.getUserID()), (LocalChatItem)null);
        this.onlineFriendsHeader = new OnlineFriendsHeaderRow();
        this.activeChattersSubscription = userManager.getChatterList().getChatterList().subscribe(ChatterListType.Active, UIThreadExecutor.getInstance(), new _$Lambda$6auIiCEAvthJH_C9LU_XlJZMtEQ(this));
        this.onlineFriendsSubscription = userManager.getChatterList().getChatterList().subscribe(ChatterListType.FriendsOnline, UIThreadExecutor.getInstance(), new _$Lambda$6auIiCEAvthJH_C9LU_XlJZMtEQ$1(this));
        Debug.Printf("currentLocationInfo subscribing", new Object[0]);
        this.currentLocationInfoSubscription = userManager.getCurrentLocationInfo().subscribe(SubscriptionSingleDataPool.getSingleDataKey(), UIThreadExecutor.getInstance(), new _$Lambda$6auIiCEAvthJH_C9LU_XlJZMtEQ$2(this));
        this.localChatUnreadCountSubscription = userManager.getChatterList().getActiveChattersManager().getUnreadCounts().subscribe(this.localChatItem.getChatterID(userManager), UIThreadExecutor.getInstance(), new _$Lambda$6auIiCEAvthJH_C9LU_XlJZMtEQ$3(this));
        this.localVoiceChatSubscription = userManager.getVoiceChatInfo().subscribe(this.localChatItem.getChatterID(userManager), UIThreadExecutor.getInstance(), new _$Lambda$6auIiCEAvthJH_C9LU_XlJZMtEQ$4(this));
    }
    
    public boolean areAllItemsEnabled() {
        return false;
    }
    
    public boolean canDismiss(final int n) {
        boolean b = false;
        if (n > 0) {
            b = b;
            if (n <= this.activeChatters.size()) {
                b = true;
            }
        }
        return b;
    }
    
    public void close() throws IOException {
        this.activeChattersSubscription.unsubscribe();
        this.onlineFriendsSubscription.unsubscribe();
        this.currentLocationInfoSubscription.unsubscribe();
        this.localChatUnreadCountSubscription.unsubscribe();
        this.localVoiceChatSubscription.unsubscribe();
    }
    
    public int getCount() {
        return this.activeChatters.size() + 1 + 1 + this.onlineFriends.size();
    }
    
    public Object getItem(final int n) {
        final int size = this.activeChatters.size();
        if (n == 0) {
            return this.localChatItem;
        }
        if (n >= 1 && n <= size) {
            return this.activeChatters.get(n - 1);
        }
        if (n == size + 1) {
            return this.onlineFriendsHeader;
        }
        if (n > size + 1) {
            return this.onlineFriends.get(n - size - 2);
        }
        return null;
    }
    
    public long getItemId(final int n) {
        return 0L;
    }
    
    public int getItemViewType(final int n) {
        if (n == this.activeChatters.size() + 1) {
            return 1;
        }
        return 0;
    }
    
    public View getView(final int n, View view, final ViewGroup viewGroup) {
        final Object item = this.getItem(n);
        if (item == this.onlineFriendsHeader) {
            return this.onlineFriendsHeader.getView(this.inflater, view, viewGroup);
        }
        if (item instanceof ChatterDisplayInfo) {
            this.viewBuilder.reset();
            ((ChatterDisplayInfo)item).buildView(this.context, this.viewBuilder, this.userManager);
            view = this.viewBuilder.getView(this.inflater, view, viewGroup, true);
            if (view != null) {
                SwipeDismissListViewTouchListener.restoreViewState(view);
            }
            return view;
        }
        return null;
    }
    
    public int getViewTypeCount() {
        return 2;
    }
    
    public boolean hasStableIds() {
        return false;
    }
    
    public boolean isEmpty() {
        return false;
    }
    
    public boolean isEnabled(final int n) {
        return n != this.activeChatters.size() + 1;
    }
    
    public void onDismiss(final int n) {
        final Object item = this.getItem(n);
        if (item instanceof ChatterDisplayInfo) {
            final ChatterID chatterID = ((ChatterDisplayInfo)item).getChatterID(this.userManager);
            if (chatterID != null) {
                this.userManager.getChatterList().getActiveChattersManager().markChatterInactive(chatterID, false);
            }
        }
    }
    
    private class LocalChatItem implements ChatterDisplayInfo
    {
        private final ChatterID chatterID;
        @Nullable
        private UnreadMessageInfo unreadMessageInfo;
        @Nullable
        private VoiceChatInfo voiceChatInfo;
        
        private LocalChatItem(final ChatterID chatterID) {
            this.chatterID = chatterID;
        }
        
        @Override
        public void buildView(final Context context, final ChatterItemViewBuilder chatterItemViewBuilder, final UserManager userManager) {
            final boolean b = false;
            final StringBuilder sb = new StringBuilder(context.getString(2131296657));
            if (ActiveChatsListAdapter.this.currentLocationInfo != null) {
                sb.append(": ");
                final int inChatRangeUsers = ActiveChatsListAdapter.this.currentLocationInfo.inChatRangeUsers();
                if (inChatRangeUsers != 0) {
                    sb.append(context.getString(2131297040, new Object[] { inChatRangeUsers }));
                }
                else {
                    sb.append(context.getString(2131296747));
                }
            }
            int unreadCount;
            if (this.unreadMessageInfo != null) {
                unreadCount = this.unreadMessageInfo.unreadCount();
            }
            else {
                unreadCount = 0;
            }
            chatterItemViewBuilder.setUnreadCount(unreadCount);
            chatterItemViewBuilder.setLabel(sb.toString());
            chatterItemViewBuilder.setThumbnailDefaultIcon(2130771979);
            chatterItemViewBuilder.setThumbnailChatterID(this.chatterID, null);
            SLChatEvent lastMessage;
            if (this.unreadMessageInfo != null) {
                lastMessage = this.unreadMessageInfo.lastMessage();
            }
            else {
                lastMessage = null;
            }
            String string;
            if (lastMessage != null) {
                string = lastMessage.getPlainTextMessage(context, userManager, false).toString();
            }
            else {
                string = null;
            }
            chatterItemViewBuilder.setLastMessage(string);
            boolean voiceActive = b;
            if (this.voiceChatInfo != null) {
                voiceActive = b;
                if (this.voiceChatInfo.state != VoiceChatInfo.VoiceChatState.None) {
                    voiceActive = true;
                }
            }
            chatterItemViewBuilder.setVoiceActive(voiceActive);
        }
        
        @Override
        public ChatterID getChatterID(final UserManager userManager) {
            return this.chatterID;
        }
        
        @Nullable
        @Override
        public String getDisplayName() {
            return ActiveChatsListAdapter.this.context.getString(2131296657);
        }
        
        public void setUnreadInfo(final UnreadMessageInfo unreadMessageInfo) {
            this.unreadMessageInfo = unreadMessageInfo;
        }
        
        public void setVoiceChatInfo(final VoiceChatInfo voiceChatInfo) {
            this.voiceChatInfo = voiceChatInfo;
        }
    }
    
    private static class OnlineFriendsHeaderRow
    {
        private boolean isAnyoneOnline;
        
        public OnlineFriendsHeaderRow() {
            this.isAnyoneOnline = false;
        }
        
        public View getView(final LayoutInflater layoutInflater, View inflate, final ViewGroup viewGroup) {
            final View view = null;
            int n;
            if (this.isAnyoneOnline) {
                n = 2131755460;
            }
            else {
                n = 2131755461;
            }
            int n2;
            if (this.isAnyoneOnline) {
                n2 = 2130968662;
            }
            else {
                n2 = 2130968663;
            }
            View view2 = view;
            if (inflate != null) {
                view2 = view;
                if (inflate.getId() == n) {
                    view2 = inflate;
                }
            }
            if ((inflate = view2) == null) {
                inflate = layoutInflater.inflate(n2, viewGroup, false);
            }
            final TextView textView = (TextView)inflate.findViewById(n);
            int text;
            if (this.isAnyoneOnline) {
                text = 2131296549;
            }
            else {
                text = 2131296735;
            }
            textView.setText(text);
            return inflate;
        }
        
        public void setAnyoneOnline(final boolean isAnyoneOnline) {
            this.isAnyoneOnline = isAnyoneOnline;
        }
    }
}

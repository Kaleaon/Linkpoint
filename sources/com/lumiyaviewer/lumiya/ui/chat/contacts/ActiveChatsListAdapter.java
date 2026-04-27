// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadMessageInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;
import com.lumiyaviewer.lumiya.ui.common.DismissableAdapter;
import com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import java.io.Closeable;
import java.io.IOException;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.contacts:
//            ChatterItemViewBuilder

public class ActiveChatsListAdapter extends BaseAdapter
    implements Closeable, DismissableAdapter
{
    private class LocalChatItem
        implements ChatterDisplayInfo
    {

        private final ChatterID chatterID;
        final ActiveChatsListAdapter this$0;
        private UnreadMessageInfo unreadMessageInfo;
        private VoiceChatInfo voiceChatInfo;

        public void buildView(Context context1, ChatterItemViewBuilder chatteritemviewbuilder, UserManager usermanager)
        {
            boolean flag1 = false;
            Object obj = new StringBuilder(context1.getString(0x7f090191));
            int i;
            if (ActiveChatsListAdapter._2D_get1(ActiveChatsListAdapter.this) != null)
            {
                ((StringBuilder) (obj)).append(": ");
                i = ActiveChatsListAdapter._2D_get1(ActiveChatsListAdapter.this).inChatRangeUsers();
                boolean flag;
                if (i != 0)
                {
                    ((StringBuilder) (obj)).append(context1.getString(0x7f090310, new Object[] {
                        Integer.valueOf(i)
                    }));
                } else
                {
                    ((StringBuilder) (obj)).append(context1.getString(0x7f0901eb));
                }
            }
            if (unreadMessageInfo != null)
            {
                i = unreadMessageInfo.unreadCount();
            } else
            {
                i = 0;
            }
            chatteritemviewbuilder.setUnreadCount(i);
            chatteritemviewbuilder.setLabel(((StringBuilder) (obj)).toString());
            chatteritemviewbuilder.setThumbnailDefaultIcon(0x7f01000b);
            chatteritemviewbuilder.setThumbnailChatterID(chatterID, null);
            if (unreadMessageInfo != null)
            {
                obj = unreadMessageInfo.lastMessage();
            } else
            {
                obj = null;
            }
            if (obj != null)
            {
                context1 = ((SLChatEvent) (obj)).getPlainTextMessage(context1, usermanager, false).toString();
            } else
            {
                context1 = null;
            }
            chatteritemviewbuilder.setLastMessage(context1);
            flag = flag1;
            if (voiceChatInfo != null)
            {
                flag = flag1;
                if (voiceChatInfo.state != com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.None)
                {
                    flag = true;
                }
            }
            chatteritemviewbuilder.setVoiceActive(flag);
        }

        public ChatterID getChatterID(UserManager usermanager)
        {
            return chatterID;
        }

        public String getDisplayName()
        {
            return ActiveChatsListAdapter._2D_get0(ActiveChatsListAdapter.this).getString(0x7f090191);
        }

        public void setUnreadInfo(UnreadMessageInfo unreadmessageinfo)
        {
            unreadMessageInfo = unreadmessageinfo;
        }

        public void setVoiceChatInfo(VoiceChatInfo voicechatinfo)
        {
            voiceChatInfo = voicechatinfo;
        }

        private LocalChatItem(ChatterID chatterid)
        {
            this$0 = ActiveChatsListAdapter.this;
            super();
            chatterID = chatterid;
        }

        LocalChatItem(ChatterID chatterid, LocalChatItem localchatitem)
        {
            this(chatterid);
        }
    }

    private static class OnlineFriendsHeaderRow
    {

        private boolean isAnyoneOnline;

        public View getView(LayoutInflater layoutinflater, View view, ViewGroup viewgroup)
        {
            Object obj = null;
            View view1;
            int i;
            int j;
            if (isAnyoneOnline)
            {
                i = 0x7f1001c4;
            } else
            {
                i = 0x7f1001c5;
            }
            if (isAnyoneOnline)
            {
                j = 0x7f040056;
            } else
            {
                j = 0x7f040057;
            }
            view1 = obj;
            if (view != null)
            {
                view1 = obj;
                if (view.getId() == i)
                {
                    view1 = view;
                }
            }
            view = view1;
            if (view1 == null)
            {
                view = layoutinflater.inflate(j, viewgroup, false);
            }
            layoutinflater = (TextView)view.findViewById(i);
            if (isAnyoneOnline)
            {
                i = 0x7f090125;
            } else
            {
                i = 0x7f0901df;
            }
            layoutinflater.setText(i);
            return view;
        }

        public void setAnyoneOnline(boolean flag)
        {
            isAnyoneOnline = flag;
        }

        public OnlineFriendsHeaderRow()
        {
            isAnyoneOnline = false;
        }
    }


    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_ROW = 0;
    private ImmutableList activeChatters;
    private final Subscription activeChattersSubscription;
    private final Context context;
    private CurrentLocationInfo currentLocationInfo;
    private final Subscription currentLocationInfoSubscription;
    private final LayoutInflater inflater;
    private final LocalChatItem localChatItem;
    private final Subscription localChatUnreadCountSubscription;
    private final Subscription localVoiceChatSubscription;
    private ImmutableList onlineFriends;
    private final OnlineFriendsHeaderRow onlineFriendsHeader = new OnlineFriendsHeaderRow();
    private final Subscription onlineFriendsSubscription;
    private final UserManager userManager;
    private final ChatterItemViewBuilder viewBuilder = new ChatterItemViewBuilder();

    static Context _2D_get0(ActiveChatsListAdapter activechatslistadapter)
    {
        return activechatslistadapter.context;
    }

    static CurrentLocationInfo _2D_get1(ActiveChatsListAdapter activechatslistadapter)
    {
        return activechatslistadapter.currentLocationInfo;
    }

    public ActiveChatsListAdapter(Context context1, UserManager usermanager)
    {
        activeChatters = ImmutableList.of();
        onlineFriends = ImmutableList.of();
        currentLocationInfo = null;
        context = context1;
        userManager = usermanager;
        inflater = LayoutInflater.from(context1);
        localChatItem = new LocalChatItem(ChatterID.getLocalChatterID(usermanager.getUserID()), null);
        activeChattersSubscription = usermanager.getChatterList().getChatterList().subscribe(ChatterListType.Active, UIThreadExecutor.getInstance(), new _2D_.Lambda._cls6auIiCEAvthJH_2D_C9LU_XlJZMtEQ(this));
        onlineFriendsSubscription = usermanager.getChatterList().getChatterList().subscribe(ChatterListType.FriendsOnline, UIThreadExecutor.getInstance(), new _2D_.Lambda._cls6auIiCEAvthJH_2D_C9LU_XlJZMtEQ._cls1(this));
        Debug.Printf("currentLocationInfo subscribing", new Object[0]);
        currentLocationInfoSubscription = usermanager.getCurrentLocationInfo().subscribe(SubscriptionSingleDataPool.getSingleDataKey(), UIThreadExecutor.getInstance(), new _2D_.Lambda._cls6auIiCEAvthJH_2D_C9LU_XlJZMtEQ._cls2(this));
        localChatUnreadCountSubscription = usermanager.getChatterList().getActiveChattersManager().getUnreadCounts().subscribe(localChatItem.getChatterID(usermanager), UIThreadExecutor.getInstance(), new _2D_.Lambda._cls6auIiCEAvthJH_2D_C9LU_XlJZMtEQ._cls3(this));
        localVoiceChatSubscription = usermanager.getVoiceChatInfo().subscribe(localChatItem.getChatterID(usermanager), UIThreadExecutor.getInstance(), new _2D_.Lambda._cls6auIiCEAvthJH_2D_C9LU_XlJZMtEQ._cls4(this));
    }

    public boolean areAllItemsEnabled()
    {
        return false;
    }

    public boolean canDismiss(int i)
    {
        boolean flag1 = false;
        boolean flag = flag1;
        if (i > 0)
        {
            flag = flag1;
            if (i <= activeChatters.size())
            {
                flag = true;
            }
        }
        return flag;
    }

    public void close()
        throws IOException
    {
        activeChattersSubscription.unsubscribe();
        onlineFriendsSubscription.unsubscribe();
        currentLocationInfoSubscription.unsubscribe();
        localChatUnreadCountSubscription.unsubscribe();
        localVoiceChatSubscription.unsubscribe();
    }

    public int getCount()
    {
        return activeChatters.size() + 1 + 1 + onlineFriends.size();
    }

    public Object getItem(int i)
    {
        int j = activeChatters.size();
        if (i == 0)
        {
            return localChatItem;
        }
        if (i >= 1 && i <= j)
        {
            return activeChatters.get(i - 1);
        }
        if (i == j + 1)
        {
            return onlineFriendsHeader;
        }
        if (i > j + 1)
        {
            return onlineFriends.get(i - j - 2);
        } else
        {
            return null;
        }
    }

    public long getItemId(int i)
    {
        return 0L;
    }

    public int getItemViewType(int i)
    {
        return i != activeChatters.size() + 1 ? 0 : 1;
    }

    public View getView(int i, View view, ViewGroup viewgroup)
    {
        Object obj = getItem(i);
        if (obj == onlineFriendsHeader)
        {
            return onlineFriendsHeader.getView(inflater, view, viewgroup);
        }
        if (obj instanceof ChatterDisplayInfo)
        {
            viewBuilder.reset();
            ((ChatterDisplayInfo)obj).buildView(context, viewBuilder, userManager);
            view = viewBuilder.getView(inflater, view, viewgroup, true);
            if (view != null)
            {
                SwipeDismissListViewTouchListener.restoreViewState(view);
            }
            return view;
        } else
        {
            return null;
        }
    }

    public int getViewTypeCount()
    {
        return 2;
    }

    public boolean hasStableIds()
    {
        return false;
    }

    public boolean isEmpty()
    {
        return false;
    }

    public boolean isEnabled(int i)
    {
        return i != activeChatters.size() + 1;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_contacts_ActiveChatsListAdapter_6525(ImmutableList immutablelist)
    {
        activeChatters = immutablelist;
        notifyDataSetChanged();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_contacts_ActiveChatsListAdapter_6801(ImmutableList immutablelist)
    {
        onlineFriends = immutablelist;
        onlineFriendsHeader.setAnyoneOnline(immutablelist.isEmpty() ^ true);
        notifyDataSetChanged();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_contacts_ActiveChatsListAdapter_7223(CurrentLocationInfo currentlocationinfo)
    {
        currentLocationInfo = currentlocationinfo;
        Debug.Printf("currentLocationInfo updated: %s", new Object[] {
            currentlocationinfo.toString()
        });
        notifyDataSetChanged();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_contacts_ActiveChatsListAdapter_7649(UnreadMessageInfo unreadmessageinfo)
    {
        localChatItem.setUnreadInfo(unreadmessageinfo);
        notifyDataSetChanged();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_contacts_ActiveChatsListAdapter_7934(VoiceChatInfo voicechatinfo)
    {
        localChatItem.setVoiceChatInfo(voicechatinfo);
        notifyDataSetChanged();
    }

    public void onDismiss(int i)
    {
        Object obj = getItem(i);
        if (obj instanceof ChatterDisplayInfo)
        {
            obj = ((ChatterDisplayInfo)obj).getChatterID(userManager);
            if (obj != null)
            {
                userManager.getChatterList().getActiveChattersManager().markChatterInactive(((ChatterID) (obj)), false);
            }
        }
    }
}

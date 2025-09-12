// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.content.Context;
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
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatMessageLoader;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat:
//            HasUserPicClickHandler, ChatEventTimestampUpdater, ChatterPicView

class ChatRecyclerAdapter extends android.support.v7.widget.RecyclerView.Adapter
    implements com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader.EventListener, HasUserPicClickHandler
{
    static interface OnAdapterDataChanged
    {

        public abstract void onAdapterDataAddedAtEnd();

        public abstract void onAdapterDataChanged();

        public abstract void onAdapterDataReloaded();
    }

    static interface OnUserPicClickedListener
    {

        public abstract void onUserPicClicked(ChatMessageSource chatmessagesource);
    }


    private ChatMessageLoader chatMessageLoader;
    private final ChatterID chatterID;
    private final LayoutInflater inflater;
    private WeakReference onAdapterDataChangedListener;
    private WeakReference onUserPicClickedListener;
    private final ChatEventTimestampUpdater timestampUpdater;
    private final UserManager userManager;
    private final android.view.View.OnClickListener userPicClickListener = new _2D_.Lambda.aLaDwVKcksSTh8O8aNFE_CFHRQc(this);

    ChatRecyclerAdapter(Context context, UserManager usermanager, ChatterID chatterid)
    {
        chatMessageLoader = null;
        onAdapterDataChangedListener = new WeakReference(null);
        onUserPicClickedListener = new WeakReference(null);
        inflater = LayoutInflater.from(context);
        timestampUpdater = new ChatEventTimestampUpdater(context);
        userManager = usermanager;
        chatterID = chatterid;
        setHasStableIds(true);
    }

    public int getItemCount()
    {
        if (chatMessageLoader != null)
        {
            return chatMessageLoader.size();
        } else
        {
            return 0;
        }
    }

    public long getItemId(int i)
    {
        if (chatMessageLoader != null)
        {
            return ((ChatMessage)chatMessageLoader.get(i)).getId().longValue();
        } else
        {
            return -1L;
        }
    }

    public int getItemViewType(int i)
    {
        if (chatMessageLoader != null)
        {
            return ((ChatMessage)chatMessageLoader.get(i)).getViewType();
        } else
        {
            return 0;
        }
    }

    public Executor getListEventsExecutor()
    {
        return UIThreadExecutor.getSerialInstance();
    }

    public android.view.View.OnClickListener getUserPicClickListener()
    {
        return userPicClickListener;
    }

    boolean hasMoreItemsAtBottom()
    {
        if (chatMessageLoader != null)
        {
            return chatMessageLoader.hasMoreItemsAtBottom();
        } else
        {
            return false;
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_ChatRecyclerAdapter_7040(View view)
    {
        OnUserPicClickedListener onuserpicclickedlistener = (OnUserPicClickedListener)onUserPicClickedListener.get();
        if (onuserpicclickedlistener != null && (view instanceof ChatterPicView))
        {
            view = ((ChatterPicView)view).getAttachedMessageSource();
            if (view != null)
            {
                onuserpicclickedlistener.onUserPicClicked(view);
            }
        }
    }

    public void onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewholder, int i)
    {
        if (chatMessageLoader != null)
        {
            SLChatEvent slchatevent = SLChatEvent.loadFromDatabaseObject((ChatMessage)chatMessageLoader.get(i), userManager.getUserID());
            if (slchatevent != null && (viewholder instanceof ChatEventViewHolder))
            {
                slchatevent.bindViewHolder((ChatEventViewHolder)viewholder, userManager, timestampUpdater);
            }
        }
    }

    public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewgroup, int i)
    {
        return SLChatEvent.createViewHolder(inflater, i, viewgroup, this);
    }

    public void onListItemAddedAtEnd()
    {
        if (chatMessageLoader != null)
        {
            OnAdapterDataChanged onadapterdatachanged = (OnAdapterDataChanged)onAdapterDataChangedListener.get();
            if (onadapterdatachanged != null)
            {
                onadapterdatachanged.onAdapterDataAddedAtEnd();
            }
        }
    }

    public void onListItemChanged(int i)
    {
        if (chatMessageLoader != null)
        {
            Debug.Printf("ChatView: item changed: position %d", new Object[] {
                Integer.valueOf(i)
            });
            notifyItemChanged(i);
            OnAdapterDataChanged onadapterdatachanged = (OnAdapterDataChanged)onAdapterDataChangedListener.get();
            if (onadapterdatachanged != null)
            {
                onadapterdatachanged.onAdapterDataChanged();
            }
        }
    }

    public void onListItemsAdded(int i, int j)
    {
        if (chatMessageLoader != null)
        {
            Debug.Printf("ChatView: items added: new size %d, position %d, count %d", new Object[] {
                Integer.valueOf(chatMessageLoader.size()), Integer.valueOf(i), Integer.valueOf(j)
            });
            notifyItemRangeInserted(i, j);
            OnAdapterDataChanged onadapterdatachanged = (OnAdapterDataChanged)onAdapterDataChangedListener.get();
            if (onadapterdatachanged != null)
            {
                onadapterdatachanged.onAdapterDataChanged();
            }
        }
    }

    public void onListItemsRemoved(int i, int j)
    {
        if (chatMessageLoader != null)
        {
            Debug.Printf("ChatView: items removed: new size %d, position %d, count %d", new Object[] {
                Integer.valueOf(chatMessageLoader.size()), Integer.valueOf(i), Integer.valueOf(j)
            });
            notifyItemRangeRemoved(i, j);
            OnAdapterDataChanged onadapterdatachanged = (OnAdapterDataChanged)onAdapterDataChangedListener.get();
            if (onadapterdatachanged != null)
            {
                onadapterdatachanged.onAdapterDataChanged();
            }
        }
    }

    public void onListReloaded()
    {
        if (chatMessageLoader != null)
        {
            Debug.Printf("ChatView: list cleared", new Object[0]);
            notifyDataSetChanged();
            OnAdapterDataChanged onadapterdatachanged = (OnAdapterDataChanged)onAdapterDataChangedListener.get();
            if (onadapterdatachanged != null)
            {
                onadapterdatachanged.onAdapterDataReloaded();
            }
        }
    }

    public void onViewRecycled(android.support.v7.widget.RecyclerView.ViewHolder viewholder)
    {
        if (viewholder instanceof ChatEventViewHolder)
        {
            timestampUpdater.removeViewHolder((ChatEventViewHolder)viewholder);
        }
    }

    void restartAtBottom()
    {
        if (chatMessageLoader != null)
        {
            chatMessageLoader.reload();
        }
    }

    void setOnUserPicClickedListener(OnUserPicClickedListener onuserpicclickedlistener)
    {
        onUserPicClickedListener = new WeakReference(onuserpicclickedlistener);
    }

    void setVisibleRange(int i, int j)
    {
        if (chatMessageLoader != null)
        {
            Debug.Printf("ChatView: visible range from %d to %d", new Object[] {
                Integer.valueOf(i), Integer.valueOf(j)
            });
            chatMessageLoader.setVisibleRange(i, j);
        }
    }

    void startLoading(OnAdapterDataChanged onadapterdatachanged)
    {
        onAdapterDataChangedListener = new WeakReference(onadapterdatachanged);
        chatMessageLoader = userManager.getChatterList().getActiveChattersManager().getMessageLoader(chatterID, this);
        notifyDataSetChanged();
    }

    void stopLoading()
    {
        userManager.getChatterList().getActiveChattersManager().releaseMessageLoader(chatterID, chatMessageLoader);
        chatMessageLoader = null;
        onAdapterDataChangedListener.clear();
        notifyDataSetChanged();
    }
}

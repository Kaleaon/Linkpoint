// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.content.Context;
import android.os.Handler;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

public class ChatEventTimestampUpdater
{

    private static final long TIMESTAMP_UPDATE_INTERVAL = 60000L;
    private final Context context;
    private final Handler mHandler = new Handler();
    private final Runnable updateRunnable = new Runnable() {

        final ChatEventTimestampUpdater this$0;

        public void run()
        {
            ChatEventTimestampUpdater._2D_set0(ChatEventTimestampUpdater.this, false);
            Iterator iterator = ChatEventTimestampUpdater._2D_get3(ChatEventTimestampUpdater.this).iterator();
            do
            {
                if (!iterator.hasNext())
                {
                    break;
                }
                ChatEventViewHolder chateventviewholder = (ChatEventViewHolder)iterator.next();
                if (chateventviewholder != null)
                {
                    chateventviewholder.updateTimestamp(ChatEventTimestampUpdater._2D_get0(ChatEventTimestampUpdater.this));
                }
            } while (true);
            if (!ChatEventTimestampUpdater._2D_get3(ChatEventTimestampUpdater.this).isEmpty())
            {
                ChatEventTimestampUpdater._2D_set0(ChatEventTimestampUpdater.this, true);
                ChatEventTimestampUpdater._2D_get1(ChatEventTimestampUpdater.this).postDelayed(ChatEventTimestampUpdater._2D_get2(ChatEventTimestampUpdater.this), 60000L);
            }
        }

            
            {
                this$0 = ChatEventTimestampUpdater.this;
                super();
            }
    };
    private boolean updateRunnablePosted;
    private final Set viewHolders = Collections.newSetFromMap(new WeakHashMap());

    static Context _2D_get0(ChatEventTimestampUpdater chateventtimestampupdater)
    {
        return chateventtimestampupdater.context;
    }

    static Handler _2D_get1(ChatEventTimestampUpdater chateventtimestampupdater)
    {
        return chateventtimestampupdater.mHandler;
    }

    static Runnable _2D_get2(ChatEventTimestampUpdater chateventtimestampupdater)
    {
        return chateventtimestampupdater.updateRunnable;
    }

    static Set _2D_get3(ChatEventTimestampUpdater chateventtimestampupdater)
    {
        return chateventtimestampupdater.viewHolders;
    }

    static boolean _2D_set0(ChatEventTimestampUpdater chateventtimestampupdater, boolean flag)
    {
        chateventtimestampupdater.updateRunnablePosted = flag;
        return flag;
    }

    public ChatEventTimestampUpdater(Context context1)
    {
        updateRunnablePosted = false;
        context = context1;
    }

    public void addViewHolder(ChatEventViewHolder chateventviewholder)
    {
        viewHolders.add(chateventviewholder);
        if (!updateRunnablePosted)
        {
            updateRunnablePosted = true;
            mHandler.postDelayed(updateRunnable, 60000L);
        }
    }

    public void removeViewHolder(ChatEventViewHolder chateventviewholder)
    {
        viewHolders.remove(chateventviewholder);
    }
}

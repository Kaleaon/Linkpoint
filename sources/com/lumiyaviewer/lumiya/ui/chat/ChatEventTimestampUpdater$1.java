// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.os.Handler;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder;
import java.util.Iterator;
import java.util.Set;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat:
//            ChatEventTimestampUpdater

class this._cls0
    implements Runnable
{

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

    lder()
    {
        this$0 = ChatEventTimestampUpdater.this;
        super();
    }
}

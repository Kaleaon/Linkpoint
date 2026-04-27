// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat:
//            ChatFragment, ChatRecyclerAdapter, ChatLayoutManager

class this._cls0
    implements Runnable
{

    final ChatFragment this$0;

    public void run()
    {
label0:
        {
            ChatFragment._2D_set2(ChatFragment.this, false);
            if (getView() != null)
            {
                RecyclerView recyclerview = (RecyclerView)getView().findViewById(0x7f100118);
                if (recyclerview.hasPendingAdapterUpdates())
                {
                    break label0;
                }
                if (ChatFragment._2D_get0(ChatFragment.this) != null && ChatFragment._2D_get1(ChatFragment.this) != null)
                {
                    if (ChatFragment._2D_get3(ChatFragment.this) && ChatFragment._2D_get0(ChatFragment.this).hasMoreItemsAtBottom())
                    {
                        ChatFragment._2D_set1(ChatFragment.this, false);
                        ChatFragment._2D_get0(ChatFragment.this).restartAtBottom();
                    }
                    int i = ChatFragment._2D_get0(ChatFragment.this).getItemCount();
                    if (i > 0)
                    {
                        ChatFragment._2D_get1(ChatFragment.this).setScrollMode(ChatFragment._2D_get3(ChatFragment.this));
                        recyclerview.smoothScrollToPosition(i - 1);
                        ChatFragment._2D_set1(ChatFragment.this, true);
                        ChatFragment._2D_set0(ChatFragment.this, false);
                    }
                }
            }
            return;
        }
        ChatFragment._2D_set2(ChatFragment.this, true);
        ChatFragment._2D_get2(ChatFragment.this).post(ChatFragment._2D_get5(ChatFragment.this));
    }

    er()
    {
        this$0 = ChatFragment.this;
        super();
    }
}

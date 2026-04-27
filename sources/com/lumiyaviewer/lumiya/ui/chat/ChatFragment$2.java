// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.lumiyaviewer.lumiya.Debug;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat:
//            ChatFragment, ChatLayoutManager, ChatRecyclerAdapter

class this._cls0
    implements Runnable
{

    final ChatFragment this$0;

    public void run()
    {
label0:
        {
            ChatFragment._2D_set3(ChatFragment.this, false);
            Object obj = getView();
            if (ChatFragment._2D_get0(ChatFragment.this) != null && ChatFragment._2D_get1(ChatFragment.this) != null && obj != null)
            {
                obj = (RecyclerView)((View) (obj)).findViewById(0x7f100118);
                Debug.Printf("UpdateVisibleRange: pending %b, first %d, last %d", new Object[] {
                    Boolean.valueOf(((RecyclerView) (obj)).hasPendingAdapterUpdates()), Integer.valueOf(ChatFragment._2D_get1(ChatFragment.this).findFirstVisibleItemPosition()), Integer.valueOf(ChatFragment._2D_get1(ChatFragment.this).findLastVisibleItemPosition())
                });
                if (((RecyclerView) (obj)).hasPendingAdapterUpdates())
                {
                    break label0;
                }
                ChatFragment._2D_wrap1(ChatFragment.this);
                int j = ChatFragment._2D_get1(ChatFragment.this).findFirstVisibleItemPosition();
                int i = ChatFragment._2D_get1(ChatFragment.this).findLastVisibleItemPosition();
                if (ChatFragment._2D_get4(ChatFragment.this))
                {
                    i = ChatFragment._2D_get0(ChatFragment.this).getItemCount() - 1;
                }
                ChatFragment._2D_get0(ChatFragment.this).setVisibleRange(j, i);
            }
            return;
        }
        ChatFragment._2D_set3(ChatFragment.this, true);
        ChatFragment._2D_get2(ChatFragment.this).post(ChatFragment._2D_get6(ChatFragment.this));
    }

    pter()
    {
        this$0 = ChatFragment.this;
        super();
    }
}

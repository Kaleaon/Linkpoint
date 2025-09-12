// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.support.v7.widget.RecyclerView;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat:
//            ChatFragment

class Listener extends android.support.v7.widget.ScrollListener
{

    final ChatFragment this$0;

    public void onScrollStateChanged(RecyclerView recyclerview, int i)
    {
        if (i == 0 || i == 1)
        {
            ChatFragment._2D_set1(ChatFragment.this, false);
        }
    }

    public void onScrolled(RecyclerView recyclerview, int i, int j)
    {
        ChatFragment._2D_wrap2(ChatFragment.this);
    }

    Listener()
    {
        this$0 = ChatFragment.this;
        super();
    }
}

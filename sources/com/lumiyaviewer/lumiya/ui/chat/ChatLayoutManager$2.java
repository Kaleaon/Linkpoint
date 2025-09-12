// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.content.Context;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat:
//            ChatLayoutManager

class oothScroller extends oothScroller
{

    final ChatLayoutManager this$0;

    protected float getScrollMs()
    {
        return 20F;
    }

    oothScroller(ChatLayoutManager chatlayoutmanager1, Context context)
    {
        this$0 = chatlayoutmanager1;
        super(ChatLayoutManager.this, context);
    }
}

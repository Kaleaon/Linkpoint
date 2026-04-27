// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            OnListUpdated, ActiveChattersManager, ChatterListType, ChatterList

class this._cls0
    implements OnListUpdated
{

    final ActiveChattersManager this$0;

    public void onListUpdated()
    {
        ActiveChattersManager._2D_get1(ActiveChattersManager.this).notifyListUpdated(ChatterListType.Active);
    }

    ()
    {
        this$0 = ActiveChattersManager.this;
        super();
    }
}

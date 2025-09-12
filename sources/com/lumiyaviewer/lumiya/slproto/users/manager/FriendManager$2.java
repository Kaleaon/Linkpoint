// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            OnListUpdated, FriendManager, ChatterListType, ChatterList

class this._cls0
    implements OnListUpdated
{

    final FriendManager this$0;

    public void onListUpdated()
    {
        FriendManager._2D_get0(FriendManager.this).notifyListUpdated(ChatterListType.FriendsOnline);
    }

    ()
    {
        this$0 = FriendManager.this;
        super();
    }
}

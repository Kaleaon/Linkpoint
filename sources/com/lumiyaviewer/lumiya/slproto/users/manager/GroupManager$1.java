// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            OnListUpdated, GroupManager, ChatterListType, ChatterList

class this._cls0
    implements OnListUpdated
{

    final GroupManager this$0;

    public void onListUpdated()
    {
        GroupManager._2D_get0(GroupManager.this).notifyListUpdated(ChatterListType.Groups);
    }

    ()
    {
        this$0 = GroupManager.this;
        super();
    }
}

// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.GroupRoleMemberDao;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            GroupManager

class this._cls0 extends SimpleRequestHandler
{

    final GroupManager this$0;

    public void onRequest(oupRoleMembersQuery ouprolemembersquery)
    {
        de.greenrobot.dao.query.LazyList lazylist = GroupManager._2D_get5(GroupManager.this).queryBuilder().where(com.lumiyaviewer.lumiya.dao.Dao.Properties.GroupID.eq(ouprolemembersquery.groupID()), new WhereCondition[] {
            com.lumiyaviewer.lumiya.dao.Dao.Properties.RoleID.eq(ouprolemembersquery.roleID()), com.lumiyaviewer.lumiya.dao.Dao.Properties.RequestID.eq(ouprolemembersquery.requestID())
        }).listLazyUncached();
        GroupManager._2D_get7(GroupManager.this).onResultData(ouprolemembersquery, lazylist);
    }

    public volatile void onRequest(Object obj)
    {
        onRequest((oupRoleMembersQuery)obj);
    }

    oupRoleMembersQuery()
    {
        this$0 = GroupManager.this;
        super();
    }
}

// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.dao.GroupRoleMember;
import com.lumiyaviewer.lumiya.dao.GroupRoleMemberDao;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import java.util.Iterator;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            GroupManager

class this._cls0 extends SimpleRequestHandler
{

    final GroupManager this$0;

    public void onRequest(oupMemberRolesQuery oupmemberrolesquery)
    {
        LazyList lazylist = GroupManager._2D_get5(GroupManager.this).queryBuilder().where(com.lumiyaviewer.lumiya.dao.Dao.Properties.GroupID.eq(oupmemberrolesquery.groupID()), new WhereCondition[] {
            com.lumiyaviewer.lumiya.dao.Dao.Properties.UserID.eq(oupmemberrolesquery.memberID()), com.lumiyaviewer.lumiya.dao.Dao.Properties.RequestID.eq(oupmemberrolesquery.requestID())
        }).listLazyUncached();
        com.google.common.collect.ilder ilder = ImmutableSet.builder();
        for (Iterator iterator = lazylist.iterator(); iterator.hasNext(); ilder.add(((GroupRoleMember)iterator.next()).getRoleID())) { }
        lazylist.close();
        GroupManager._2D_get3(GroupManager.this).onResultData(oupmemberrolesquery, ilder.build());
    }

    public volatile void onRequest(Object obj)
    {
        onRequest((oupMemberRolesQuery)obj);
    }

    oupMemberRolesQuery()
    {
        this$0 = GroupManager.this;
        super();
    }
}

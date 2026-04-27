// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.MoneyTransactionDao;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.QueryBuilder;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            BalanceManager

class this._cls0 extends SimpleRequestHandler
{

    final BalanceManager this$0;

    public void onRequest(SubscriptionSingleKey subscriptionsinglekey)
    {
        de.greenrobot.dao.query.LazyList lazylist = BalanceManager._2D_get2(BalanceManager.this).queryBuilder().orderAsc(new Property[] {
            com.lumiyaviewer.lumiya.dao.ao.Properties.Timestamp
        }).listLazy();
        BalanceManager._2D_get3(BalanceManager.this).onResultData(subscriptionsinglekey, lazylist);
    }

    public volatile void onRequest(Object obj)
    {
        onRequest((SubscriptionSingleKey)obj);
    }

    ()
    {
        this$0 = BalanceManager.this;
        super();
    }
}

// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.finance.SLFinancialInfo;
import java.util.concurrent.atomic.AtomicReference;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            BalanceManager, UserManager

class this._cls0 extends SimpleRequestHandler
{

    final BalanceManager this$0;

    public void onRequest(SubscriptionSingleKey subscriptionsinglekey)
    {
        subscriptionsinglekey = (SLFinancialInfo)BalanceManager._2D_get1(BalanceManager.this).get();
        if (subscriptionsinglekey != null)
        {
            if (subscriptionsinglekey.getBalanceKnown())
            {
                int i = subscriptionsinglekey.getBalance();
                BalanceManager._2D_get0(BalanceManager.this).onResultData(SubscriptionSingleKey.Value, Integer.valueOf(i));
                return;
            }
            subscriptionsinglekey = BalanceManager._2D_get5(BalanceManager.this).getActiveAgentCircuit();
            if (subscriptionsinglekey != null)
            {
                subscriptionsinglekey.execute(BalanceManager._2D_get4(BalanceManager.this));
                return;
            } else
            {
                BalanceManager._2D_get0(BalanceManager.this).onResultError(SubscriptionSingleKey.Value, new com.lumiyaviewer.lumiya.slproto.NotConnectedException());
                return;
            }
        } else
        {
            BalanceManager._2D_get0(BalanceManager.this).onResultError(SubscriptionSingleKey.Value, new com.lumiyaviewer.lumiya.slproto.NotConnectedException());
            return;
        }
    }

    public volatile void onRequest(Object obj)
    {
        onRequest((SubscriptionSingleKey)obj);
    }

    ception()
    {
        this$0 = BalanceManager.this;
        super();
    }
}

// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.modules.finance.SLFinancialInfo;
import java.util.concurrent.atomic.AtomicReference;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            BalanceManager

class this._cls0
    implements Runnable
{

    final BalanceManager this$0;

    public void run()
    {
        SLFinancialInfo slfinancialinfo = (SLFinancialInfo)BalanceManager._2D_get1(BalanceManager.this).get();
        if (slfinancialinfo != null)
        {
            slfinancialinfo.AskForMoneyBalance();
            return;
        } else
        {
            BalanceManager._2D_get0(BalanceManager.this).onResultError(SubscriptionSingleKey.Value, new com.lumiyaviewer.lumiya.slproto.NotConnectedException());
            return;
        }
    }

    ception()
    {
        this$0 = BalanceManager.this;
        super();
    }
}

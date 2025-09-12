// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.myava;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.myava:
//            TransactionLogFragment, TransactionLogAdapter

class this._cls0
    implements Runnable
{

    final TransactionLogFragment this$0;

    public void run()
    {
label0:
        {
            TransactionLogFragment._2D_set0(TransactionLogFragment.this, false);
            if (TransactionLogFragment._2D_get3(TransactionLogFragment.this) != null)
            {
                RecyclerView recyclerview = transactionLogView;
                if (recyclerview.hasPendingAdapterUpdates())
                {
                    break label0;
                }
                if (TransactionLogFragment._2D_get0(TransactionLogFragment.this) != null)
                {
                    int i = TransactionLogFragment._2D_get0(TransactionLogFragment.this).getItemCount();
                    if (i > 0)
                    {
                        recyclerview.scrollToPosition(i - 1);
                    }
                }
            }
            return;
        }
        TransactionLogFragment._2D_set0(TransactionLogFragment.this, true);
        TransactionLogFragment._2D_get1(TransactionLogFragment.this).post(TransactionLogFragment._2D_get2(TransactionLogFragment.this));
    }

    ()
    {
        this$0 = TransactionLogFragment.this;
        super();
    }
}

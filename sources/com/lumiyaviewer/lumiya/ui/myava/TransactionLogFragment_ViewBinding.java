// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.myava;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.myava:
//            TransactionLogFragment

public class TransactionLogFragment_ViewBinding
    implements Unbinder
{

    private TransactionLogFragment target;

    public TransactionLogFragment_ViewBinding(TransactionLogFragment transactionlogfragment, View view)
    {
        target = transactionlogfragment;
        transactionlogfragment.transactionLogView = (RecyclerView)Utils.findRequiredViewAsType(view, 0x7f10029c, "field 'transactionLogView'", android/support/v7/widget/RecyclerView);
        transactionlogfragment.loadingLayout = (LoadingLayout)Utils.findRequiredViewAsType(view, 0x7f1000bd, "field 'loadingLayout'", com/lumiyaviewer/lumiya/ui/common/LoadingLayout);
    }

    public void unbind()
    {
        TransactionLogFragment transactionlogfragment = target;
        if (transactionlogfragment == null)
        {
            throw new IllegalStateException("Bindings already cleared.");
        } else
        {
            target = null;
            transactionlogfragment.transactionLogView = null;
            transactionlogfragment.loadingLayout = null;
            return;
        }
    }
}

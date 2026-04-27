// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.myava;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import butterknife.internal.Utils;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.Unbinder;

public class TransactionLogFragment_ViewBinding implements Unbinder
{
    private TransactionLogFragment target;
    
    @UiThread
    public TransactionLogFragment_ViewBinding(final TransactionLogFragment target, final View view) {
        this.target = target;
        target.transactionLogView = Utils.findRequiredViewAsType(view, 2131755676, "field 'transactionLogView'", RecyclerView.class);
        target.loadingLayout = Utils.findRequiredViewAsType(view, 2131755197, "field 'loadingLayout'", LoadingLayout.class);
    }
    
    @CallSuper
    @Override
    public void unbind() {
        final TransactionLogFragment target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.transactionLogView = null;
        target.loadingLayout = null;
    }
}

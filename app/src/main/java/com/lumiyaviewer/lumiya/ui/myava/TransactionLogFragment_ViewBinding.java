package com.lumiyaviewer.lumiya.ui.myava;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;

public class TransactionLogFragment_ViewBinding implements Unbinder {
    private TransactionLogFragment target;

    @UiThread
    public TransactionLogFragment_ViewBinding(TransactionLogFragment transactionLogFragment, View view) {
        this.target = transactionLogFragment;
        transactionLogFragment.transactionLogView = (RecyclerView) Utils.findRequiredViewAsType(view, R.id.transactionLogView, "field 'transactionLogView'", RecyclerView.class);
        transactionLogFragment.loadingLayout = (LoadingLayout) Utils.findRequiredViewAsType(view, R.id.loading_layout, "field 'loadingLayout'", LoadingLayout.class);
    }

    @CallSuper
    public void unbind() {
        TransactionLogFragment transactionLogFragment = this.target;
        if (transactionLogFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        transactionLogFragment.transactionLogView = null;
        transactionLogFragment.loadingLayout = null;
    }
}

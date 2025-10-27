package com.lumiyaviewer.lumiya.ui.myava

import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import butterknife.Unbinder
import butterknife.internal.Utils
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout

class TransactionLogFragment_ViewBinding : Unbinder {
    private TransactionLogFragment target

    @UiThread
    TransactionLogFragment_ViewBinding(TransactionLogFragment transactionLogFragment, View view) {
        this.target = transactionLogFragment
        transactionLogFragment.transactionLogView = (RecyclerView) Utils.findRequiredViewAsType(view, R.id.transactionLogView, "field 'transactionLogView'", RecyclerView.class)
        transactionLogFragment.loadingLayout = (LoadingLayout) Utils.findRequiredViewAsType(view, R.id.loading_layout, "field 'loadingLayout'", LoadingLayout.class)
    }

    @CallSuper
    Unit unbind() {
        TransactionLogFragment transactionLogFragment = this.target
        if (transactionLogFragment == null) {
            throw IllegalStateException("Bindings already cleared.")
        }
        this.target = null
        transactionLogFragment.transactionLogView = null
        transactionLogFragment.loadingLayout = null
    }
}

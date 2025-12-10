package com.linkpoint.ui.myava

import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import butterknife.Unbinder
import butterknife.internal.Utils
import com.linkpoint.R
import com.linkpoint.ui.common.LoadingLayout

class TransactionLogFragment_ViewBinding : Unbinder {
    private TransactionLogFragment target

    @UiThread
    TransactionLogFragment_ViewBinding(TransactionLogFragment transactionLogFragment, View view) {
        this.target = transactionLogFragment
        transactionLogFragment.transactionLogView = (Utils as RecyclerView).findRequiredViewAsType(view, R.id.transactionLogView, "field 'transactionLogView'", RecyclerView.class)
        transactionLogFragment.loadingLayout = (Utils as LoadingLayout).findRequiredViewAsType(view, R.id.loading_layout, "field 'loadingLayout'", LoadingLayout.class)
    }

    @CallSuper
    fun unbind(): Unit {
        TransactionLogFragment transactionLogFragment = this.target
        if (transactionLogFragment == null) {
            throw IllegalStateException("Bindings already cleared.")
        }
        this.target = null
        transactionLogFragment.transactionLogView = null
        transactionLogFragment.loadingLayout = null
    }
}

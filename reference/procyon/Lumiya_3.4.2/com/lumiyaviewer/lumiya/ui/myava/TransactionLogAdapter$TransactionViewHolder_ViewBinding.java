// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.myava;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import butterknife.internal.Utils;
import android.widget.TextView;
import android.view.View;
import butterknife.Unbinder;

public class TransactionLogAdapter$TransactionViewHolder_ViewBinding implements Unbinder
{
    private TransactionLogAdapter.TransactionViewHolder target;
    
    @UiThread
    public TransactionLogAdapter$TransactionViewHolder_ViewBinding(final TransactionLogAdapter.TransactionViewHolder target, final View view) {
        this.target = target;
        target.userName = Utils.findRequiredViewAsType(view, 2131755678, "field 'userName'", TextView.class);
        target.userPicView = Utils.findRequiredViewAsType(view, 2131755327, "field 'userPicView'", ChatterPicView.class);
        target.timestampTextView = Utils.findRequiredViewAsType(view, 2131755677, "field 'timestampTextView'", TextView.class);
        target.amountTextView = Utils.findRequiredViewAsType(view, 2131755679, "field 'amountTextView'", TextView.class);
        target.finalBalanceTextView = Utils.findRequiredViewAsType(view, 2131755680, "field 'finalBalanceTextView'", TextView.class);
    }
    
    @CallSuper
    @Override
    public void unbind() {
        final TransactionLogAdapter.TransactionViewHolder target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.userName = null;
        target.userPicView = null;
        target.timestampTextView = null;
        target.amountTextView = null;
        target.finalBalanceTextView = null;
    }
}

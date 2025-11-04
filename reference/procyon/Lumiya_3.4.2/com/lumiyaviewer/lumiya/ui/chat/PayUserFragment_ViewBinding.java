// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View$OnClickListener;
import butterknife.internal.DebouncingOnClickListener;
import android.widget.EditText;
import butterknife.internal.Utils;
import android.widget.TextView;
import android.view.View;
import butterknife.Unbinder;

public class PayUserFragment_ViewBinding implements Unbinder
{
    private PayUserFragment target;
    private View view2131755622;
    private View view2131755626;
    
    @UiThread
    public PayUserFragment_ViewBinding(final PayUserFragment target, View requiredView) {
        this.target = target;
        target.paymentDetailsBalance = Utils.findRequiredViewAsType(requiredView, 2131755623, "field 'paymentDetailsBalance'", TextView.class);
        target.receivingUserName = Utils.findRequiredViewAsType(requiredView, 2131755620, "field 'receivingUserName'", TextView.class);
        target.payMessage = Utils.findRequiredViewAsType(requiredView, 2131755625, "field 'payMessage'", EditText.class);
        target.payAmount = Utils.findRequiredViewAsType(requiredView, 2131755624, "field 'payAmount'", EditText.class);
        target.receivingUserPic = Utils.findRequiredViewAsType(requiredView, 2131755621, "field 'receivingUserPic'", ChatterPicView.class);
        (this.view2131755626 = Utils.findRequiredView(requiredView, 2131755626, "method 'onUserPayButton'")).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onUserPayButton();
            }
        });
        requiredView = Utils.findRequiredView(requiredView, 2131755622, "method 'onReceivingUserViewProfileClick'");
        (this.view2131755622 = requiredView).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onReceivingUserViewProfileClick();
            }
        });
    }
    
    @CallSuper
    @Override
    public void unbind() {
        final PayUserFragment target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.paymentDetailsBalance = null;
        target.receivingUserName = null;
        target.payMessage = null;
        target.payAmount = null;
        target.receivingUserPic = null;
        this.view2131755626.setOnClickListener((View$OnClickListener)null);
        this.view2131755626 = null;
        this.view2131755622.setOnClickListener((View$OnClickListener)null);
        this.view2131755622 = null;
    }
}

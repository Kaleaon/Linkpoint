package com.lumiyaviewer.lumiya.ui.chat;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.R;

public class PayUserFragment_ViewBinding implements Unbinder {
    private PayUserFragment target;
    private View view2131755622;
    private View view2131755626;

    @UiThread
    public PayUserFragment_ViewBinding(final PayUserFragment payUserFragment, View view) {
        this.target = payUserFragment;
        payUserFragment.paymentDetailsBalance = (TextView) Utils.findRequiredViewAsType(view, R.id.payment_details_balance, "field 'paymentDetailsBalance'", TextView.class);
        payUserFragment.receivingUserName = (TextView) Utils.findRequiredViewAsType(view, R.id.receiving_user_name, "field 'receivingUserName'", TextView.class);
        payUserFragment.payMessage = (EditText) Utils.findRequiredViewAsType(view, R.id.user_pay_message, "field 'payMessage'", EditText.class);
        payUserFragment.payAmount = (EditText) Utils.findRequiredViewAsType(view, R.id.pay_amount, "field 'payAmount'", EditText.class);
        payUserFragment.receivingUserPic = (ChatterPicView) Utils.findRequiredViewAsType(view, R.id.receiving_user_pic, "field 'receivingUserPic'", ChatterPicView.class);
        View findRequiredView = Utils.findRequiredView(view, R.id.user_pay_button, "method 'onUserPayButton'");
        this.view2131755626 = findRequiredView;
        findRequiredView.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                payUserFragment.onUserPayButton();
            }
        });
        View findRequiredView2 = Utils.findRequiredView(view, R.id.receiving_user_profile_button, "method 'onReceivingUserViewProfileClick'");
        this.view2131755622 = findRequiredView2;
        findRequiredView2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                payUserFragment.onReceivingUserViewProfileClick();
            }
        });
    }

    @CallSuper
    public void unbind() {
        PayUserFragment payUserFragment = this.target;
        if (payUserFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        payUserFragment.paymentDetailsBalance = null;
        payUserFragment.receivingUserName = null;
        payUserFragment.payMessage = null;
        payUserFragment.payAmount = null;
        payUserFragment.receivingUserPic = null;
        this.view2131755626.setOnClickListener((View.OnClickListener) null);
        this.view2131755626 = null;
        this.view2131755622.setOnClickListener((View.OnClickListener) null);
        this.view2131755622 = null;
    }
}

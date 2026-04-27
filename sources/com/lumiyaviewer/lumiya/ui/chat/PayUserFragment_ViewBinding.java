// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat:
//            PayUserFragment, ChatterPicView

public class PayUserFragment_ViewBinding
    implements Unbinder
{

    private PayUserFragment target;
    private View view2131755622;
    private View view2131755626;

    public PayUserFragment_ViewBinding(final PayUserFragment target, View view)
    {
        this.target = target;
        target.paymentDetailsBalance = (TextView)Utils.findRequiredViewAsType(view, 0x7f100267, "field 'paymentDetailsBalance'", android/widget/TextView);
        target.receivingUserName = (TextView)Utils.findRequiredViewAsType(view, 0x7f100264, "field 'receivingUserName'", android/widget/TextView);
        target.payMessage = (EditText)Utils.findRequiredViewAsType(view, 0x7f100269, "field 'payMessage'", android/widget/EditText);
        target.payAmount = (EditText)Utils.findRequiredViewAsType(view, 0x7f100268, "field 'payAmount'", android/widget/EditText);
        target.receivingUserPic = (ChatterPicView)Utils.findRequiredViewAsType(view, 0x7f100265, "field 'receivingUserPic'", com/lumiyaviewer/lumiya/ui/chat/ChatterPicView);
        View view1 = Utils.findRequiredView(view, 0x7f10026a, "method 'onUserPayButton'");
        view2131755626 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final PayUserFragment_ViewBinding this$0;
            final PayUserFragment val$target;

            public void doClick(View view2)
            {
                target.onUserPayButton();
            }

            
            {
                this$0 = PayUserFragment_ViewBinding.this;
                target = payuserfragment;
                super();
            }
        });
        view = Utils.findRequiredView(view, 0x7f100266, "method 'onReceivingUserViewProfileClick'");
        view2131755622 = view;
        view.setOnClickListener(new DebouncingOnClickListener() {

            final PayUserFragment_ViewBinding this$0;
            final PayUserFragment val$target;

            public void doClick(View view2)
            {
                target.onReceivingUserViewProfileClick();
            }

            
            {
                this$0 = PayUserFragment_ViewBinding.this;
                target = payuserfragment;
                super();
            }
        });
    }

    public void unbind()
    {
        PayUserFragment payuserfragment = target;
        if (payuserfragment == null)
        {
            throw new IllegalStateException("Bindings already cleared.");
        } else
        {
            target = null;
            payuserfragment.paymentDetailsBalance = null;
            payuserfragment.receivingUserName = null;
            payuserfragment.payMessage = null;
            payuserfragment.payAmount = null;
            payuserfragment.receivingUserPic = null;
            view2131755626.setOnClickListener(null);
            view2131755626 = null;
            view2131755622.setOnClickListener(null);
            view2131755622 = null;
            return;
        }
    }
}

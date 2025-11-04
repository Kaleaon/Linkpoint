// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View$OnClickListener;
import butterknife.internal.DebouncingOnClickListener;
import android.widget.Button;
import butterknife.internal.Utils;
import android.view.View;
import butterknife.Unbinder;

public class GroupNoticeFragment_ViewBinding implements Unbinder
{
    private GroupNoticeFragment target;
    private View view2131755383;
    private View view2131755385;
    
    @UiThread
    public GroupNoticeFragment_ViewBinding(final GroupNoticeFragment target, View requiredView) {
        this.target = target;
        final View requiredView2 = Utils.findRequiredView(requiredView, 2131755383, "field 'groupNoticeAttachmentButton' and method 'onGroupNoticeAttachmentButton'");
        target.groupNoticeAttachmentButton = Utils.castView(requiredView2, 2131755383, "field 'groupNoticeAttachmentButton'", Button.class);
        (this.view2131755383 = requiredView2).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onGroupNoticeAttachmentButton();
            }
        });
        target.groupNoticeAttachmentText = Utils.findRequiredViewAsType(requiredView, 2131755382, "field 'groupNoticeAttachmentText'", TextView.class);
        target.groupNoticeSubject = Utils.findRequiredViewAsType(requiredView, 2131755381, "field 'groupNoticeSubject'", EditText.class);
        target.groupNoticeEditText = Utils.findRequiredViewAsType(requiredView, 2131755384, "field 'groupNoticeEditText'", EditText.class);
        requiredView = Utils.findRequiredView(requiredView, 2131755385, "method 'onGroupNoticeSendButton'");
        (this.view2131755385 = requiredView).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onGroupNoticeSendButton();
            }
        });
    }
    
    @CallSuper
    @Override
    public void unbind() {
        final GroupNoticeFragment target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.groupNoticeAttachmentButton = null;
        target.groupNoticeAttachmentText = null;
        target.groupNoticeSubject = null;
        target.groupNoticeEditText = null;
        this.view2131755383.setOnClickListener((View$OnClickListener)null);
        this.view2131755383 = null;
        this.view2131755385.setOnClickListener((View$OnClickListener)null);
        this.view2131755385 = null;
    }
}

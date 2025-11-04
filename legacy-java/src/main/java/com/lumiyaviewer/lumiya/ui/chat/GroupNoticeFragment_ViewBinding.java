package com.lumiyaviewer.lumiya.ui.chat;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.R;

/* loaded from: classes.dex */
public class GroupNoticeFragment_ViewBinding implements Unbinder {
    private GroupNoticeFragment target;
    private View view2131755383;
    private View view2131755385;

    @UiThread
    public GroupNoticeFragment_ViewBinding(final GroupNoticeFragment groupNoticeFragment, View view) {
        this.target = groupNoticeFragment;
        View findRequiredView = Utils.findRequiredView(view, R.id.groupNoticeAttachmentButton, "field 'groupNoticeAttachmentButton' and method 'onGroupNoticeAttachmentButton'");
        groupNoticeFragment.groupNoticeAttachmentButton = (Button) Utils.castView(findRequiredView, R.id.groupNoticeAttachmentButton, "field 'groupNoticeAttachmentButton'", Button.class);
        this.view2131755383 = findRequiredView;
        findRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.GroupNoticeFragment_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                groupNoticeFragment.onGroupNoticeAttachmentButton();
            }
        });
        groupNoticeFragment.groupNoticeAttachmentText = (TextView) Utils.findRequiredViewAsType(view, R.id.groupNoticeAttachmentText, "field 'groupNoticeAttachmentText'", TextView.class);
        groupNoticeFragment.groupNoticeSubject = (EditText) Utils.findRequiredViewAsType(view, R.id.groupNoticeSubject, "field 'groupNoticeSubject'", EditText.class);
        groupNoticeFragment.groupNoticeEditText = (EditText) Utils.findRequiredViewAsType(view, R.id.groupNoticeEditText, "field 'groupNoticeEditText'", EditText.class);
        View findRequiredView2 = Utils.findRequiredView(view, R.id.groupNoticeSendButton, "method 'onGroupNoticeSendButton'");
        this.view2131755385 = findRequiredView2;
        findRequiredView2.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.GroupNoticeFragment_ViewBinding.2
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                groupNoticeFragment.onGroupNoticeSendButton();
            }
        });
    }

    @Override // butterknife.Unbinder
    @CallSuper
    public void unbind() {
        GroupNoticeFragment groupNoticeFragment = this.target;
        if (groupNoticeFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        groupNoticeFragment.groupNoticeAttachmentButton = null;
        groupNoticeFragment.groupNoticeAttachmentText = null;
        groupNoticeFragment.groupNoticeSubject = null;
        groupNoticeFragment.groupNoticeEditText = null;
        this.view2131755383.setOnClickListener(null);
        this.view2131755383 = null;
        this.view2131755385.setOnClickListener(null);
        this.view2131755385 = null;
    }
}

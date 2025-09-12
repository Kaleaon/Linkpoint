// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat:
//            GroupNoticeFragment

public class GroupNoticeFragment_ViewBinding
    implements Unbinder
{

    private GroupNoticeFragment target;
    private View view2131755383;
    private View view2131755385;

    public GroupNoticeFragment_ViewBinding(final GroupNoticeFragment target, View view)
    {
        this.target = target;
        View view1 = Utils.findRequiredView(view, 0x7f100177, "field 'groupNoticeAttachmentButton' and method 'onGroupNoticeAttachmentButton'");
        target.groupNoticeAttachmentButton = (Button)Utils.castView(view1, 0x7f100177, "field 'groupNoticeAttachmentButton'", android/widget/Button);
        view2131755383 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final GroupNoticeFragment_ViewBinding this$0;
            final GroupNoticeFragment val$target;

            public void doClick(View view2)
            {
                target.onGroupNoticeAttachmentButton();
            }

            
            {
                this$0 = GroupNoticeFragment_ViewBinding.this;
                target = groupnoticefragment;
                super();
            }
        });
        target.groupNoticeAttachmentText = (TextView)Utils.findRequiredViewAsType(view, 0x7f100176, "field 'groupNoticeAttachmentText'", android/widget/TextView);
        target.groupNoticeSubject = (EditText)Utils.findRequiredViewAsType(view, 0x7f100175, "field 'groupNoticeSubject'", android/widget/EditText);
        target.groupNoticeEditText = (EditText)Utils.findRequiredViewAsType(view, 0x7f100178, "field 'groupNoticeEditText'", android/widget/EditText);
        view = Utils.findRequiredView(view, 0x7f100179, "method 'onGroupNoticeSendButton'");
        view2131755385 = view;
        view.setOnClickListener(new DebouncingOnClickListener() {

            final GroupNoticeFragment_ViewBinding this$0;
            final GroupNoticeFragment val$target;

            public void doClick(View view2)
            {
                target.onGroupNoticeSendButton();
            }

            
            {
                this$0 = GroupNoticeFragment_ViewBinding.this;
                target = groupnoticefragment;
                super();
            }
        });
    }

    public void unbind()
    {
        GroupNoticeFragment groupnoticefragment = target;
        if (groupnoticefragment == null)
        {
            throw new IllegalStateException("Bindings already cleared.");
        } else
        {
            target = null;
            groupnoticefragment.groupNoticeAttachmentButton = null;
            groupnoticefragment.groupNoticeAttachmentText = null;
            groupnoticefragment.groupNoticeSubject = null;
            groupnoticefragment.groupNoticeEditText = null;
            view2131755383.setOnClickListener(null);
            view2131755383 = null;
            view2131755385.setOnClickListener(null);
            view2131755385 = null;
            return;
        }
    }
}

// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.myava;

import android.view.View;
import android.widget.ListView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.myava:
//            MuteListFragment

public class MuteListFragment_ViewBinding
    implements Unbinder
{

    private MuteListFragment target;
    private View view2131755492;

    public MuteListFragment_ViewBinding(final MuteListFragment target, View view)
    {
        this.target = target;
        target.muteList = (ListView)Utils.findRequiredViewAsType(view, 0x7f1001e3, "field 'muteList'", android/widget/ListView);
        view = Utils.findRequiredView(view, 0x7f1001e4, "method 'onAddMuteListButtonClick'");
        view2131755492 = view;
        view.setOnClickListener(new DebouncingOnClickListener() {

            final MuteListFragment_ViewBinding this$0;
            final MuteListFragment val$target;

            public void doClick(View view1)
            {
                target.onAddMuteListButtonClick();
            }

            
            {
                this$0 = MuteListFragment_ViewBinding.this;
                target = mutelistfragment;
                super();
            }
        });
    }

    public void unbind()
    {
        MuteListFragment mutelistfragment = target;
        if (mutelistfragment == null)
        {
            throw new IllegalStateException("Bindings already cleared.");
        } else
        {
            target = null;
            mutelistfragment.muteList = null;
            view2131755492.setOnClickListener(null);
            view2131755492 = null;
            return;
        }
    }
}

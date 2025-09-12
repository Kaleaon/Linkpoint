// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.grids;

import android.view.View;
import android.view.Window;
import android.widget.ListView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.grids:
//            ManageGridsActivity

public class ManageGridsActivity_ViewBinding
    implements Unbinder
{

    private ManageGridsActivity target;
    private View view2131755484;

    public ManageGridsActivity_ViewBinding(ManageGridsActivity managegridsactivity)
    {
        this(managegridsactivity, managegridsactivity.getWindow().getDecorView());
    }

    public ManageGridsActivity_ViewBinding(final ManageGridsActivity target, View view)
    {
        this.target = target;
        target.gridListView = (ListView)Utils.findRequiredViewAsType(view, 0x7f1001db, "field 'gridListView'", android/widget/ListView);
        view = Utils.findRequiredView(view, 0x7f1001dc, "method 'onAddNewGridButton'");
        view2131755484 = view;
        view.setOnClickListener(new DebouncingOnClickListener() {

            final ManageGridsActivity_ViewBinding this$0;
            final ManageGridsActivity val$target;

            public void doClick(View view1)
            {
                target.onAddNewGridButton();
            }

            
            {
                this$0 = ManageGridsActivity_ViewBinding.this;
                target = managegridsactivity;
                super();
            }
        });
    }

    public void unbind()
    {
        ManageGridsActivity managegridsactivity = target;
        if (managegridsactivity == null)
        {
            throw new IllegalStateException("Bindings already cleared.");
        } else
        {
            target = null;
            managegridsactivity.gridListView = null;
            view2131755484.setOnClickListener(null);
            view2131755484 = null;
            return;
        }
    }
}

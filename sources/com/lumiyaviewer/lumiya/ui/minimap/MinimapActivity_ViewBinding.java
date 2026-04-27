// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.minimap;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import butterknife.Unbinder;
import butterknife.internal.Utils;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.minimap:
//            MinimapActivity

public class MinimapActivity_ViewBinding
    implements Unbinder
{

    private MinimapActivity target;

    public MinimapActivity_ViewBinding(MinimapActivity minimapactivity)
    {
        this(minimapactivity, minimapactivity.getWindow().getDecorView());
    }

    public MinimapActivity_ViewBinding(MinimapActivity minimapactivity, View view)
    {
        target = minimapactivity;
        minimapactivity.selectorLayout = (FrameLayout)Utils.findRequiredViewAsType(view, 0x7f100286, "field 'selectorLayout'", android/widget/FrameLayout);
        minimapactivity.splitMainLayout = (LinearLayout)Utils.findRequiredViewAsType(view, 0x7f100284, "field 'splitMainLayout'", android/widget/LinearLayout);
        minimapactivity.splitObjectPopupsLeftSpacer = Utils.findRequiredView(view, 0x7f100289, "field 'splitObjectPopupsLeftSpacer'");
        minimapactivity.detailsLayout = (ViewGroup)Utils.findRequiredViewAsType(view, 0x7f100285, "field 'detailsLayout'", android/view/ViewGroup);
    }

    public void unbind()
    {
        MinimapActivity minimapactivity = target;
        if (minimapactivity == null)
        {
            throw new IllegalStateException("Bindings already cleared.");
        } else
        {
            target = null;
            minimapactivity.selectorLayout = null;
            minimapactivity.splitMainLayout = null;
            minimapactivity.splitObjectPopupsLeftSpacer = null;
            minimapactivity.detailsLayout = null;
            return;
        }
    }
}

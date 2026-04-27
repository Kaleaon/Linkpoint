// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.settings;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.settings:
//            SettingsSelectionFragment, SettingsPage

private class this._cls0 extends ArrayAdapter
{

    final SettingsSelectionFragment this$0;

    public View getView(int i, View view, ViewGroup viewgroup)
    {
        view = super.getView(i, view, viewgroup);
        viewgroup = (SettingsPage)getItem(i);
        if ((view instanceof TextView) && viewgroup != null)
        {
            ((TextView)view).setText(viewgroup.getPageTitle());
        }
        return view;
    }

    public (Context context)
    {
        this$0 = SettingsSelectionFragment.this;
        super(context, 0x1090003, SettingsPage.values());
    }
}

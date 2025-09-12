// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.settings:
//            SettingsPage, SettingsFragment

public class SettingsSelectionFragment extends Fragment
    implements android.widget.AdapterView.OnItemClickListener
{
    private class SettingPagesAdapter extends ArrayAdapter
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

        public SettingPagesAdapter(Context context)
        {
            this$0 = SettingsSelectionFragment.this;
            super(context, 0x1090003, SettingsPage.values());
        }
    }


    public SettingsSelectionFragment()
    {
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        layoutinflater = layoutinflater.inflate(0x7f04009f, viewgroup, false);
        viewgroup = (ListView)layoutinflater.findViewById(0x7f100281);
        viewgroup.setAdapter(new SettingPagesAdapter(getContext()));
        viewgroup.setOnItemClickListener(this);
        return layoutinflater;
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        adapterview = SettingsPage.values();
        if (i >= 0 && i < adapterview.length)
        {
            adapterview = adapterview[i];
            DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/settings/SettingsFragment, SettingsFragment.makeSelection(adapterview.getPageResourceId()));
        }
    }
}

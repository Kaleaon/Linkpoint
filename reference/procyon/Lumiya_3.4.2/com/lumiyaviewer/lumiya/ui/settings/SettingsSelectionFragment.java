// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.settings;

import android.widget.TextView;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.view.View;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView$OnItemClickListener;
import android.support.v4.app.Fragment;

public class SettingsSelectionFragment extends Fragment implements AdapterView$OnItemClickListener
{
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        final View inflate = layoutInflater.inflate(2130968735, viewGroup, false);
        final ListView listView = (ListView)inflate.findViewById(2131755649);
        listView.setAdapter((ListAdapter)new SettingPagesAdapter(this.getContext()));
        listView.setOnItemClickListener((AdapterView$OnItemClickListener)this);
        return inflate;
    }
    
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
        final SettingsPage[] values = SettingsPage.values();
        if (n >= 0 && n < values.length) {
            DetailsActivity.showEmbeddedDetails(this.getActivity(), SettingsFragment.class, SettingsFragment.makeSelection(values[n].getPageResourceId()));
        }
    }
    
    private class SettingPagesAdapter extends ArrayAdapter<SettingsPage>
    {
        public SettingPagesAdapter(final Context context) {
            super(context, 17367043, (Object[])SettingsPage.values());
        }
        
        public View getView(final int n, final View view, final ViewGroup viewGroup) {
            final View view2 = super.getView(n, view, viewGroup);
            final SettingsPage settingsPage = (SettingsPage)this.getItem(n);
            if (view2 instanceof TextView && settingsPage != null) {
                ((TextView)view2).setText(settingsPage.getPageTitle());
            }
            return view2;
        }
    }
}

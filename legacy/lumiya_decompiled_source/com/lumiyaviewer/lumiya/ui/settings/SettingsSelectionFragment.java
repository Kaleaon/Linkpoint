package com.lumiyaviewer.lumiya.ui.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
/* loaded from: classes.dex */
public class SettingsSelectionFragment extends Fragment implements AdapterView.OnItemClickListener {

    /* loaded from: classes.dex */
    private class SettingPagesAdapter extends ArrayAdapter<SettingsPage> {
        public SettingPagesAdapter(Context context) {
            super(context, 17367043, SettingsPage.valuesCustom());
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            SettingsPage item = getItem(i);
            if ((view2 instanceof TextView) && item != null) {
                ((TextView) view2).setText(item.getPageTitle());
            }
            return view2;
        }
    }

    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.settings_page_selector, viewGroup, false);
        ListView listView = (ListView) inflate.findViewById(R.id.settings_page_list);
        listView.setAdapter((ListAdapter) new SettingPagesAdapter(getContext()));
        listView.setOnItemClickListener(this);
        return inflate;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        SettingsPage[] valuesCustom = SettingsPage.valuesCustom();
        if (i < 0 || i >= valuesCustom.length) {
            return;
        }
        DetailsActivity.showEmbeddedDetails(getActivity(), SettingsFragment.class, SettingsFragment.makeSelection(valuesCustom[i].getPageResourceId()));
    }
}

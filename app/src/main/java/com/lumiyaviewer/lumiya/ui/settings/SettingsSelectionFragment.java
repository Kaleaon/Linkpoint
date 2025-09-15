package com.lumiyaviewer.lumiya.ui.settings;
import java.util.*;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;

public class SettingsSelectionFragment extends Fragment implements AdapterView.OnItemClickListener {

    private class SettingPagesAdapter extends ArrayAdapter<SettingsPage> {
        public SettingPagesAdapter(Context context) {
            super(context, 17367043, SettingsPage.values());
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            SettingsPage settingsPage = (SettingsPage) getItem(i);
            if ((view2 instanceof TextView) && settingsPage != null) {
                ((TextView) view2).setText(settingsPage.getPageTitle());
            }
            return view2;
        }
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.settings_page_selector, viewGroup, false);
        ListView listView = (ListView) inflate.findViewById(R.id.settings_page_list);
        listView.setAdapter(new SettingPagesAdapter(getContext()));
        listView.setOnItemClickListener(this);
        return inflate;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        SettingsPage[] values = SettingsPage.values();
        if (i >= 0 && i < values.length) {
            DetailsActivity.showEmbeddedDetails(getActivity(), SettingsFragment.class, SettingsFragment.makeSelection(values[i].getPageResourceId()));
        }
    }
}

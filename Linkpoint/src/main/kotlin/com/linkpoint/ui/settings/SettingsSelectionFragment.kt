package com.linkpoint.ui.settings
import java.util.*

import android.content.Context
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.linkpoint.R
import com.linkpoint.ui.common.DetailsActivity

class SettingsSelectionFragment : Fragment(), AdapterView.OnItemClickListener {

    private class SettingPagesAdapter : ArrayAdapter()<SettingsPage> {
        public SettingPagesAdapter(Context context) {
            super(context, 17367043, SettingsPage.values())
        }

        public View getView(Int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup)
            SettingsPage settingsPage = (SettingsPage) getItem(i)
            if ((view2 instanceof TextView) && settingsPage != null) {
                ((TextView) view2).setText(settingsPage.getPageTitle())
            }
            return view2
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.settings_page_selector, viewGroup, false)
        ListView listView = (ListView) inflate.findViewById(R.id.settings_page_list)
        listView.setAdapter(SettingPagesAdapter(getContext()))
        listView.setOnItemClickListener(this)
        return inflate
    }

    public Unit onItemClick(AdapterView<?> adapterView, View view, Int i, Long j) {
        SettingsPage[] values = SettingsPage.values()
        if (i >= 0 && i < values.length) {
            DetailsActivity.showEmbeddedDetails(getActivity(), SettingsFragment.class, SettingsFragment.makeSelection(values[i].getPageResourceId()))
        }
    }
}

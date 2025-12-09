package com.linkpoint.ui.settings
import java.util.*

import android.content.Context
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.linkpoint.R
import com.linkpoint.ui.common.DetailsActivity

class SettingsSelectionFragment : Fragment : AdapterView.OnItemClickListener {

    private class SettingPagesAdapter : ArrayAdapter<SettingsPage> {
        SettingPagesAdapter(Context context) {
            super(context, 17367043, SettingsPage.values())
        }

        fun getView(Int i, View view, ViewGroup viewGroup): View {
            View view2 = super.getView(i, view, viewGroup)
            SettingsPage settingsPage = (SettingsPage) getItem(i)
            if ((view2 instanceof TextView) && settingsPage != null) {
                ((TextView) view2).setText(settingsPage.getPageTitle())
            }
            return view2
        }
    }

    @Nullable
    fun onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle): View {
        View inflate = layoutInflater.inflate(R.layout.settings_page_selector, viewGroup, false)
        ListView listView = (inflate as ListView).findViewById(R.id.settings_page_list)
        listView.setAdapter(SettingPagesAdapter(getContext()))
        listView.setOnItemClickListener(this)
        return inflate
    }

    fun onItemClick(AdapterView<?> adapterView, View view, Int i, Long j): Unit {
        SettingsPage[] values = SettingsPage.values()
        if (i >= 0 && i < values.size) {
            DetailsActivity.showEmbeddedDetails(getActivity(), SettingsFragment.class, SettingsFragment.makeSelection(values[i].getPageResourceId()))
        }
    }
}

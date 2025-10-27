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

         public fun getView(i: Int, view: View, viewGroup: ViewGroup): View {
            val view2: View = super.getView(i, view, viewGroup)
            val settingsPage: SettingsPage = (SettingsPage) getItem(i)
            if ((view2 instanceof TextView) && settingsPage != null) {
                ((TextView) view2).setText(settingsPage.getPageTitle())
            }
            return view2
        }
    }

     public override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup, bundle: Bundle): View {
        val inflate: View = layoutInflater.inflate(R.layout.settings_page_selector, viewGroup, false)
        val listView: ListView = (ListView) inflate.findViewById(R.id.settings_page_list)
        listView.setAdapter(SettingPagesAdapter(getContext()))
        listView.setOnItemClickListener(this)
        return inflate
    }

    fun onItemClick(adapterView: AdapterView<?>, view: View, i: Int, j: Long) {
        val values: Array<SettingsPage> = SettingsPage.values()
        if (i >= 0 && i < values.length) {
            DetailsActivity.showEmbeddedDetails(getActivity(), SettingsFragment.class, SettingsFragment.makeSelection(values[i].getPageResourceId()))
        }
    }
}

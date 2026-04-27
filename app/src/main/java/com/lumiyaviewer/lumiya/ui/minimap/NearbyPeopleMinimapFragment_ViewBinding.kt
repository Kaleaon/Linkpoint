package com.lumiyaviewer.lumiya.ui.minimap
import java.util.*

import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import butterknife.Unbinder
import butterknife.internal.Utils
import com.lumiyaviewer.lumiya.R

class NearbyPeopleMinimapFragment_ViewBinding : Unbinder {
    private NearbyPeopleMinimapFragment target

    @UiThread
    NearbyPeopleMinimapFragment_ViewBinding(NearbyPeopleMinimapFragment nearbyPeopleMinimapFragment, View view) {
        this.target = nearbyPeopleMinimapFragment
        nearbyPeopleMinimapFragment.emptyView = Utils.findRequiredView(view, 16908292, "field 'emptyView'")
        nearbyPeopleMinimapFragment.userListView = (RecyclerView) Utils.findRequiredViewAsType(view, R.id.minimap_users_list, "field 'userListView'", RecyclerView.class)
    }

    @CallSuper
    Unit unbind() {
        NearbyPeopleMinimapFragment nearbyPeopleMinimapFragment = this.target
        if (nearbyPeopleMinimapFragment == null) {
            throw IllegalStateException("Bindings already cleared.")
        }
        this.target = null
        nearbyPeopleMinimapFragment.emptyView = null
        nearbyPeopleMinimapFragment.userListView = null
    }
}

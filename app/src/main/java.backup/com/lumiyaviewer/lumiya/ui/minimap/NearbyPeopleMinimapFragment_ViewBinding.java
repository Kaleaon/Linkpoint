package com.lumiyaviewer.lumiya.ui.minimap;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.R;

public class NearbyPeopleMinimapFragment_ViewBinding implements Unbinder {
    private NearbyPeopleMinimapFragment target;

    @UiThread
    public NearbyPeopleMinimapFragment_ViewBinding(NearbyPeopleMinimapFragment nearbyPeopleMinimapFragment, View view) {
        this.target = nearbyPeopleMinimapFragment;
        nearbyPeopleMinimapFragment.emptyView = Utils.findRequiredView(view, 16908292, "field 'emptyView'");
        nearbyPeopleMinimapFragment.userListView = (RecyclerView) Utils.findRequiredViewAsType(view, R.id.minimap_users_list, "field 'userListView'", RecyclerView.class);
    }

    @CallSuper
    public void unbind() {
        NearbyPeopleMinimapFragment nearbyPeopleMinimapFragment = this.target;
        if (nearbyPeopleMinimapFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        nearbyPeopleMinimapFragment.emptyView = null;
        nearbyPeopleMinimapFragment.userListView = null;
    }
}

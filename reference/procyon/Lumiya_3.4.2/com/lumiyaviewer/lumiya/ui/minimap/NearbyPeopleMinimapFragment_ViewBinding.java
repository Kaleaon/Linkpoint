// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.minimap;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import butterknife.internal.Utils;
import android.view.View;
import butterknife.Unbinder;

public class NearbyPeopleMinimapFragment_ViewBinding implements Unbinder
{
    private NearbyPeopleMinimapFragment target;
    
    @UiThread
    public NearbyPeopleMinimapFragment_ViewBinding(final NearbyPeopleMinimapFragment target, final View view) {
        this.target = target;
        target.emptyView = Utils.findRequiredView(view, 16908292, "field 'emptyView'");
        target.userListView = Utils.findRequiredViewAsType(view, 2131755490, "field 'userListView'", RecyclerView.class);
    }
    
    @CallSuper
    @Override
    public void unbind() {
        final NearbyPeopleMinimapFragment target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.emptyView = null;
        target.userListView = null;
    }
}

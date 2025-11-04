// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.grids;

import android.support.annotation.CallSuper;
import android.view.View$OnClickListener;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import android.widget.ListView;
import android.support.annotation.UiThread;
import android.view.View;
import butterknife.Unbinder;

public class ManageGridsActivity_ViewBinding implements Unbinder
{
    private ManageGridsActivity target;
    private View view2131755484;
    
    @UiThread
    public ManageGridsActivity_ViewBinding(final ManageGridsActivity manageGridsActivity) {
        this(manageGridsActivity, manageGridsActivity.getWindow().getDecorView());
    }
    
    @UiThread
    public ManageGridsActivity_ViewBinding(final ManageGridsActivity target, View requiredView) {
        this.target = target;
        target.gridListView = Utils.findRequiredViewAsType(requiredView, 2131755483, "field 'gridListView'", ListView.class);
        requiredView = Utils.findRequiredView(requiredView, 2131755484, "method 'onAddNewGridButton'");
        (this.view2131755484 = requiredView).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onAddNewGridButton();
            }
        });
    }
    
    @CallSuper
    @Override
    public void unbind() {
        final ManageGridsActivity target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.gridListView = null;
        this.view2131755484.setOnClickListener((View$OnClickListener)null);
        this.view2131755484 = null;
    }
}

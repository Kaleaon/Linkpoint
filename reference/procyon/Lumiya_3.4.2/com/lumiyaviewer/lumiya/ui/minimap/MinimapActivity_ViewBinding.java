// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.minimap;

import android.support.annotation.CallSuper;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import butterknife.internal.Utils;
import android.widget.FrameLayout;
import android.view.View;
import android.support.annotation.UiThread;
import butterknife.Unbinder;

public class MinimapActivity_ViewBinding implements Unbinder
{
    private MinimapActivity target;
    
    @UiThread
    public MinimapActivity_ViewBinding(final MinimapActivity minimapActivity) {
        this(minimapActivity, minimapActivity.getWindow().getDecorView());
    }
    
    @UiThread
    public MinimapActivity_ViewBinding(final MinimapActivity target, final View view) {
        this.target = target;
        target.selectorLayout = Utils.findRequiredViewAsType(view, 2131755654, "field 'selectorLayout'", FrameLayout.class);
        target.splitMainLayout = Utils.findRequiredViewAsType(view, 2131755652, "field 'splitMainLayout'", LinearLayout.class);
        target.splitObjectPopupsLeftSpacer = Utils.findRequiredView(view, 2131755657, "field 'splitObjectPopupsLeftSpacer'");
        target.detailsLayout = Utils.findRequiredViewAsType(view, 2131755653, "field 'detailsLayout'", ViewGroup.class);
    }
    
    @CallSuper
    @Override
    public void unbind() {
        final MinimapActivity target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.selectorLayout = null;
        target.splitMainLayout = null;
        target.splitObjectPopupsLeftSpacer = null;
        target.detailsLayout = null;
    }
}

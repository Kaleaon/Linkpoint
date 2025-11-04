// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.myava;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View$OnClickListener;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import android.widget.ListView;
import android.view.View;
import butterknife.Unbinder;

public class MuteListFragment_ViewBinding implements Unbinder
{
    private MuteListFragment target;
    private View view2131755492;
    
    @UiThread
    public MuteListFragment_ViewBinding(final MuteListFragment target, View requiredView) {
        this.target = target;
        target.muteList = Utils.findRequiredViewAsType(requiredView, 2131755491, "field 'muteList'", ListView.class);
        requiredView = Utils.findRequiredView(requiredView, 2131755492, "method 'onAddMuteListButtonClick'");
        (this.view2131755492 = requiredView).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onAddMuteListButtonClick();
            }
        });
    }
    
    @CallSuper
    @Override
    public void unbind() {
        final MuteListFragment target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.muteList = null;
        this.view2131755492.setOnClickListener((View$OnClickListener)null);
        this.view2131755492 = null;
    }
}

// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.search;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View$OnClickListener;
import butterknife.internal.DebouncingOnClickListener;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import android.widget.TextView;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import android.view.View;
import butterknife.Unbinder;

public class ParcelInfoFragment_ViewBinding implements Unbinder
{
    private ParcelInfoFragment target;
    private View view2131755600;
    private View view2131755608;
    
    @UiThread
    public ParcelInfoFragment_ViewBinding(final ParcelInfoFragment target, View requiredView) {
        this.target = target;
        target.parcelImageView = Utils.findRequiredViewAsType(requiredView, 2131755602, "field 'parcelImageView'", ImageAssetView.class);
        target.parcelDetailsDescription = Utils.findRequiredViewAsType(requiredView, 2131755599, "field 'parcelDetailsDescription'", TextView.class);
        target.parcelOwnerName = Utils.findRequiredViewAsType(requiredView, 2131755606, "field 'parcelOwnerName'", TextView.class);
        target.parcelOwnerPic = Utils.findRequiredViewAsType(requiredView, 2131755607, "field 'parcelOwnerPic'", ChatterPicView.class);
        target.parcelSimName = Utils.findRequiredViewAsType(requiredView, 2131755604, "field 'parcelSimName'", TextView.class);
        target.parcelDetailsName = Utils.findRequiredViewAsType(requiredView, 2131755598, "field 'parcelDetailsName'", TextView.class);
        target.parcelLocation = Utils.findRequiredViewAsType(requiredView, 2131755605, "field 'parcelLocation'", TextView.class);
        (this.view2131755600 = Utils.findRequiredView(requiredView, 2131755600, "method 'onParcelTeleportButton'")).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onParcelTeleportButton();
            }
        });
        requiredView = Utils.findRequiredView(requiredView, 2131755608, "method 'onParcelOwnerProfileClick'");
        (this.view2131755608 = requiredView).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onParcelOwnerProfileClick();
            }
        });
    }
    
    @CallSuper
    @Override
    public void unbind() {
        final ParcelInfoFragment target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.parcelImageView = null;
        target.parcelDetailsDescription = null;
        target.parcelOwnerName = null;
        target.parcelOwnerPic = null;
        target.parcelSimName = null;
        target.parcelDetailsName = null;
        target.parcelLocation = null;
        this.view2131755600.setOnClickListener((View$OnClickListener)null);
        this.view2131755600 = null;
        this.view2131755608.setOnClickListener((View$OnClickListener)null);
        this.view2131755608 = null;
    }
}

// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import android.support.v7.widget.CardView;
import android.view.View$OnClickListener;
import butterknife.internal.DebouncingOnClickListener;
import android.widget.Button;
import butterknife.internal.Utils;
import android.widget.TextView;
import android.view.View;
import butterknife.Unbinder;

public class ParcelPropertiesFragment_ViewBinding implements Unbinder
{
    private ParcelPropertiesFragment target;
    private View view2131755608;
    private View view2131755611;
    private View view2131755614;
    private View view2131755615;
    private View view2131755617;
    
    @UiThread
    public ParcelPropertiesFragment_ViewBinding(final ParcelPropertiesFragment target, View requiredView) {
        this.target = target;
        target.parcelMediaURL = Utils.findRequiredViewAsType(requiredView, 2131755613, "field 'parcelMediaURL'", TextView.class);
        final View requiredView2 = Utils.findRequiredView(requiredView, 2131755615, "field 'mediaStopButton' and method 'onParcelMediaStop'");
        target.mediaStopButton = Utils.castView(requiredView2, 2131755615, "field 'mediaStopButton'", Button.class);
        (this.view2131755615 = requiredView2).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onParcelMediaStop();
            }
        });
        target.parcelMediaCardView = Utils.findRequiredViewAsType(requiredView, 2131755612, "field 'parcelMediaCardView'", CardView.class);
        target.simRestartCardView = Utils.findRequiredViewAsType(requiredView, 2131755616, "field 'simRestartCardView'", CardView.class);
        target.parcelName = Utils.findRequiredViewAsType(requiredView, 2131755609, "field 'parcelName'", TextView.class);
        target.parcelOwnerPic = Utils.findRequiredViewAsType(requiredView, 2131755607, "field 'parcelOwnerPic'", ChatterPicView.class);
        target.parcelArea = Utils.findRequiredViewAsType(requiredView, 2131755610, "field 'parcelArea'", TextView.class);
        target.parcelOwnerName = Utils.findRequiredViewAsType(requiredView, 2131755606, "field 'parcelOwnerName'", TextView.class);
        final View requiredView3 = Utils.findRequiredView(requiredView, 2131755614, "field 'mediaPlayButton' and method 'onParcelMediaPlay'");
        target.mediaPlayButton = Utils.castView(requiredView3, 2131755614, "field 'mediaPlayButton'", Button.class);
        (this.view2131755614 = requiredView3).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onParcelMediaPlay();
            }
        });
        target.parcelImageView = Utils.findRequiredViewAsType(requiredView, 2131755602, "field 'parcelImageView'", ImageAssetView.class);
        target.parcelDescription = Utils.findRequiredViewAsType(requiredView, 2131755599, "field 'parcelDescription'", TextView.class);
        (this.view2131755608 = Utils.findRequiredView(requiredView, 2131755608, "method 'onOwnerProfileButton'")).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onOwnerProfileButton();
            }
        });
        (this.view2131755617 = Utils.findRequiredView(requiredView, 2131755617, "method 'onSimRestartButton'")).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onSimRestartButton();
            }
        });
        requiredView = Utils.findRequiredView(requiredView, 2131755611, "method 'onSetHomeButton'");
        (this.view2131755611 = requiredView).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onSetHomeButton();
            }
        });
    }
    
    @CallSuper
    @Override
    public void unbind() {
        final ParcelPropertiesFragment target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.parcelMediaURL = null;
        target.mediaStopButton = null;
        target.parcelMediaCardView = null;
        target.simRestartCardView = null;
        target.parcelName = null;
        target.parcelOwnerPic = null;
        target.parcelArea = null;
        target.parcelOwnerName = null;
        target.mediaPlayButton = null;
        target.parcelImageView = null;
        target.parcelDescription = null;
        this.view2131755615.setOnClickListener((View$OnClickListener)null);
        this.view2131755615 = null;
        this.view2131755614.setOnClickListener((View$OnClickListener)null);
        this.view2131755614 = null;
        this.view2131755608.setOnClickListener((View$OnClickListener)null);
        this.view2131755608 = null;
        this.view2131755617.setOnClickListener((View$OnClickListener)null);
        this.view2131755617 = null;
        this.view2131755611.setOnClickListener((View$OnClickListener)null);
        this.view2131755611 = null;
    }
}

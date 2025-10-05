package com.lumiyaviewer.lumiya.ui.minimap;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.R;

public class MinimapActivity_ViewBinding implements Unbinder {
    private MinimapActivity target;

    @UiThread
    public MinimapActivity_ViewBinding(MinimapActivity minimapActivity) {
        this(minimapActivity, minimapActivity.getWindow().getDecorView());
    }

    @UiThread
    public MinimapActivity_ViewBinding(MinimapActivity minimapActivity, View view) {
        this.target = minimapActivity;
        minimapActivity.selectorLayout = (FrameLayout) Utils.findRequiredViewAsType(view, R.id.selector, "field 'selectorLayout'", FrameLayout.class);
        minimapActivity.splitMainLayout = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.splitMainLayout, "field 'splitMainLayout'", LinearLayout.class);
        minimapActivity.splitObjectPopupsLeftSpacer = Utils.findRequiredView(view, R.id.split_object_popups_left_spacer, "field 'splitObjectPopupsLeftSpacer'");
        minimapActivity.detailsLayout = (ViewGroup) Utils.findRequiredViewAsType(view, R.id.detailsWithOnlineStatus, "field 'detailsLayout'", ViewGroup.class);
    }

    @CallSuper
    public void unbind() {
        MinimapActivity minimapActivity = this.target;
        if (minimapActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        minimapActivity.selectorLayout = null;
        minimapActivity.splitMainLayout = null;
        minimapActivity.splitObjectPopupsLeftSpacer = null;
        minimapActivity.detailsLayout = null;
    }
}

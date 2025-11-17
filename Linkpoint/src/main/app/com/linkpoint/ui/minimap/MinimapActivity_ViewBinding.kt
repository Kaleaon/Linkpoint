package com.linkpoint.ui.minimap

import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import butterknife.Unbinder
import butterknife.internal.Utils
import com.linkpoint.R

class MinimapActivity_ViewBinding : Unbinder {
    private MinimapActivity target

    @UiThread
    MinimapActivity_ViewBinding(MinimapActivity minimapActivity) {
        this(minimapActivity, minimapActivity.getWindow().getDecorView())
    }

    @UiThread
    MinimapActivity_ViewBinding(MinimapActivity minimapActivity, View view) {
        this.target = minimapActivity
        minimapActivity.selectorLayout = (FrameLayout) Utils.findRequiredViewAsType(view, R.id.selector, "field 'selectorLayout'", FrameLayout.class)
        minimapActivity.splitMainLayout = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.splitMainLayout, "field 'splitMainLayout'", LinearLayout.class)
        minimapActivity.splitObjectPopupsLeftSpacer = Utils.findRequiredView(view, R.id.split_object_popups_left_spacer, "field 'splitObjectPopupsLeftSpacer'")
        minimapActivity.detailsLayout = (ViewGroup) Utils.findRequiredViewAsType(view, R.id.detailsWithOnlineStatus, "field 'detailsLayout'", ViewGroup.class)
    }

    @CallSuper
    Unit unbind() {
        MinimapActivity minimapActivity = this.target
        if (minimapActivity == null) {
            throw IllegalStateException("Bindings already cleared.")
        }
        this.target = null
        minimapActivity.selectorLayout = null
        minimapActivity.splitMainLayout = null
        minimapActivity.splitObjectPopupsLeftSpacer = null
        minimapActivity.detailsLayout = null
    }
}

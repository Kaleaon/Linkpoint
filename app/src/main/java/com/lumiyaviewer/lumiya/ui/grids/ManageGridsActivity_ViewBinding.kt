package com.lumiyaviewer.lumiya.ui.grids
import java.util.*

import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import android.view.View
import android.widget.ListView
import butterknife.Unbinder
import butterknife.internal.DebouncingOnClickListener
import butterknife.internal.Utils
import com.lumiyaviewer.lumiya.R

class ManageGridsActivity_ViewBinding : Unbinder {
    private ManageGridsActivity target
    private View view2131755484

    @UiThread
    ManageGridsActivity_ViewBinding(ManageGridsActivity manageGridsActivity) {
        this(manageGridsActivity, manageGridsActivity.getWindow().getDecorView())
    }

    @UiThread
    ManageGridsActivity_ViewBinding(ManageGridsActivity manageGridsActivity, View view) {
        this.target = manageGridsActivity
        manageGridsActivity.gridListView = (ListView) Utils.findRequiredViewAsType(view, R.id.gridList, "field 'gridListView'", ListView.class)
        View findRequiredView = Utils.findRequiredView(view, R.id.add_new_grid_button, "method 'onAddNewGridButton'")
        this.view2131755484 = findRequiredView
        findRequiredView.setOnClickListener(DebouncingOnClickListener() {
            Unit doClick(View view) {
                manageGridsActivity.onAddNewGridButton()
            }
    }

    @CallSuper
    Unit unbind() {
        ManageGridsActivity manageGridsActivity = this.target
        if (manageGridsActivity == null) {
            throw IllegalStateException("Bindings already cleared.")
        }
        this.target = null
        manageGridsActivity.gridListView = null
        this.view2131755484.setOnClickListener((View.OnClickListener) null)
        this.view2131755484 = null
    }
}

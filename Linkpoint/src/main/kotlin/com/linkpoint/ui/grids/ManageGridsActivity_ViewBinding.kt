package com.linkpoint.ui.grids
import java.util.*

import android.support.annotation.CallSuper
import android.support.annotation.UiThread
import android.view.View
import android.widget.ListView
import butterknife.Unbinder
import butterknife.internal.DebouncingOnClickListener
import butterknife.internal.Utils
import com.linkpoint.R

class ManageGridsActivity_ViewBinding : Unbinder {
    private ManageGridsActivity target
    private View view2131755484

    @UiThread
    public ManageGridsActivity_ViewBinding(ManageGridsActivity manageGridsActivity) {
        this(manageGridsActivity, manageGridsActivity.getWindow().getDecorView())
    }

    @UiThread
    public ManageGridsActivity_ViewBinding(final ManageGridsActivity manageGridsActivity, View view) {
        this.target = manageGridsActivity
        manageGridsActivity.gridListView = (ListView) Utils.findRequiredViewAsType(view, R.id.gridList, "field 'gridListView'", ListView.class)
        val findRequiredView: View = Utils.findRequiredView(view, R.id.add_new_grid_button, "method 'onAddNewGridButton'")
        this.view2131755484 = findRequiredView
        findRequiredView.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(view: View) {
                manageGridsActivity.onAddNewGridButton()
            }
    }

    @CallSuper
    fun unbind() {
        val manageGridsActivity: ManageGridsActivity = this.target
        if (manageGridsActivity == null) {
            throw IllegalStateException("Bindings already cleared.")
        }
        this.target = null
        manageGridsActivity.gridListView = null
        this.view2131755484.setOnClickListener((View.OnClickListener) null)
        this.view2131755484 = null
    }
}

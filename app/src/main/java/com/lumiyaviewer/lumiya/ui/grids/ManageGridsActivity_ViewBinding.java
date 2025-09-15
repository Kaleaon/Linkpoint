package com.lumiyaviewer.lumiya.ui.grids;
import java.util.*;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ListView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.R;

public class ManageGridsActivity_ViewBinding implements Unbinder {
    private ManageGridsActivity target;
    private View view2131755484;

    @UiThread
    public ManageGridsActivity_ViewBinding(ManageGridsActivity manageGridsActivity) {
        this(manageGridsActivity, manageGridsActivity.getWindow().getDecorView());
    }

    @UiThread
    public ManageGridsActivity_ViewBinding(final ManageGridsActivity manageGridsActivity, View view) {
        this.target = manageGridsActivity;
        manageGridsActivity.gridListView = (ListView) Utils.findRequiredViewAsType(view, R.id.gridList, "field 'gridListView'", ListView.class);
        View findRequiredView = Utils.findRequiredView(view, R.id.add_new_grid_button, "method 'onAddNewGridButton'");
        this.view2131755484 = findRequiredView;
        findRequiredView.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                manageGridsActivity.onAddNewGridButton();
            }
    }

    @CallSuper
    public void unbind() {
        ManageGridsActivity manageGridsActivity = this.target;
        if (manageGridsActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        manageGridsActivity.gridListView = null;
        this.view2131755484.setOnClickListener((View.OnClickListener) null);
        this.view2131755484 = null;
    }
}

package com.lumiyaviewer.lumiya.ui.myava;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ListView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.R;

/* loaded from: classes.dex */
public class MuteListFragment_ViewBinding implements Unbinder {
    private MuteListFragment target;
    private View view2131755492;

    @UiThread
    public MuteListFragment_ViewBinding(final MuteListFragment muteListFragment, View view) {
        this.target = muteListFragment;
        muteListFragment.muteList = (ListView) Utils.findRequiredViewAsType(view, R.id.muteList, "field 'muteList'", ListView.class);
        View findRequiredView = Utils.findRequiredView(view, R.id.add_mute_list_button, "method 'onAddMuteListButtonClick'");
        this.view2131755492 = findRequiredView;
        findRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.myava.MuteListFragment_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                muteListFragment.onAddMuteListButtonClick();
            }
        });
    }

    @Override // butterknife.Unbinder
    @CallSuper
    public void unbind() {
        MuteListFragment muteListFragment = this.target;
        if (muteListFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        muteListFragment.muteList = null;
        this.view2131755492.setOnClickListener(null);
        this.view2131755492 = null;
    }
}

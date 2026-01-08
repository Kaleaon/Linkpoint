package com.linkpoint.ui.myava
import java.util.*

import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import android.view.View
import android.widget.ListView
import butterknife.Unbinder
import butterknife.internal.DebouncingOnClickListener
import butterknife.internal.Utils
import com.linkpoint.R

class MuteListFragment_ViewBinding : Unbinder {
    private MuteListFragment target
    private View view2131755492

    @UiThread
    MuteListFragment_ViewBinding(MuteListFragment muteListFragment, View view) {
        this.target = muteListFragment
        muteListFragment.muteList = (Utils as ListView).findRequiredViewAsType(view, R.id.muteList, "field 'muteList'", ListView.class)
        View findRequiredView = Utils.findRequiredView(view, R.id.add_mute_list_button, "method 'onAddMuteListButtonClick'")
        this.view2131755492 = findRequiredView
        findRequiredView.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view): Unit {
                muteListFragment.onAddMuteListButtonClick()
            }
    }

    @CallSuper
    fun unbind(): Unit {
        MuteListFragment muteListFragment = this.target
        if (muteListFragment == null) {
            throw IllegalStateException("Bindings already cleared.")
        }
        this.target = null
        muteListFragment.muteList = null
        this.view2131755492.setOnClickListener((View.OnClickListener) null)
        this.view2131755492 = null
    }
}

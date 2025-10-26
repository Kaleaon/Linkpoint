package com.linkpoint.ui.myava
import java.util.*

import android.support.annotation.CallSuper
import android.support.annotation.UiThread
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
    public MuteListFragment_ViewBinding(final MuteListFragment muteListFragment, View view) {
        this.target = muteListFragment
        muteListFragment.muteList = (ListView) Utils.findRequiredViewAsType(view, R.id.muteList, "field 'muteList'", ListView.class)
        val findRequiredView: View = Utils.findRequiredView(view, R.id.add_mute_list_button, "method 'onAddMuteListButtonClick'")
        this.view2131755492 = findRequiredView
        findRequiredView.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(view: View) {
                muteListFragment.onAddMuteListButtonClick()
            }
    }

    @CallSuper
    fun unbind() {
        val muteListFragment: MuteListFragment = this.target
        if (muteListFragment == null) {
            throw IllegalStateException("Bindings already cleared.")
        }
        this.target = null
        muteListFragment.muteList = null
        this.view2131755492.setOnClickListener((View.OnClickListener) null)
        this.view2131755492 = null
    }
}

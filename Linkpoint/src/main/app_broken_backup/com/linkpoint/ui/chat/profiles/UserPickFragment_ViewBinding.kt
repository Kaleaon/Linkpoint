package com.linkpoint.ui.chat.profiles
import java.util.*

import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import android.view.View
import android.widget.Button
import android.widget.TextView
import butterknife.Unbinder
import butterknife.internal.DebouncingOnClickListener
import butterknife.internal.Utils
import com.linkpoint.R
import com.linkpoint.ui.common.ImageAssetView

class UserPickFragment_ViewBinding : Unbinder {
    private UserPickFragment target
    private View view2131755696
    private View view2131755697
    private View view2131755698
    private View view2131755700

    @UiThread
    UserPickFragment_ViewBinding(UserPickFragment userPickFragment, View view) {
        this.target = userPickFragment
        View findRequiredView = Utils.findRequiredView(view, R.id.user_pick_set_location_button, "field 'setLocationButton' and method 'onSetLocation'")
        userPickFragment.setLocationButton = (Utils as Button).castView(findRequiredView, R.id.user_pick_set_location_button, "field 'setLocationButton'", Button.class)
        this.view2131755696 = findRequiredView
        findRequiredView.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view): Unit {
                userPickFragment.onSetLocation(view)
            }
        View findRequiredView2 = Utils.findRequiredView(view, R.id.change_pic_button, "field 'changePicButton' and method 'onChangePic'")
        userPickFragment.changePicButton = (Utils as Button).castView(findRequiredView2, R.id.change_pic_button, "field 'changePicButton'", Button.class)
        this.view2131755698 = findRequiredView2
        findRequiredView2.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view): Unit {
                userPickFragment.onChangePic(view)
            }
        userPickFragment.userPickImageView = (Utils as ImageAssetView).findRequiredViewAsType(view, R.id.user_pick_image_view, "field 'userPickImageView'", ImageAssetView.class)
        userPickFragment.pickDescription = (Utils as TextView).findRequiredViewAsType(view, R.id.pick_description, "field 'pickDescription'", TextView.class)
        View findRequiredView3 = Utils.findRequiredView(view, R.id.user_pick_desc_edit_button, "field 'userPickDescEditButton' and method 'onDescEdit'")
        userPickFragment.userPickDescEditButton = (Utils as Button).castView(findRequiredView3, R.id.user_pick_desc_edit_button, "field 'userPickDescEditButton'", Button.class)
        this.view2131755700 = findRequiredView3
        findRequiredView3.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view): Unit {
                userPickFragment.onDescEdit(view)
            }
        View findRequiredView4 = Utils.findRequiredView(view, R.id.user_pick_teleport_button, "method 'onTeleportToPickClick'")
        this.view2131755697 = findRequiredView4
        findRequiredView4.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view): Unit {
                userPickFragment.onTeleportToPickClick(view)
            }
    }

    @CallSuper
    fun unbind(): Unit {
        UserPickFragment userPickFragment = this.target
        if (userPickFragment == null) {
            throw IllegalStateException("Bindings already cleared.")
        }
        this.target = null
        userPickFragment.setLocationButton = null
        userPickFragment.changePicButton = null
        userPickFragment.userPickImageView = null
        userPickFragment.pickDescription = null
        userPickFragment.userPickDescEditButton = null
        this.view2131755696.setOnClickListener((View.OnClickListener) null)
        this.view2131755696 = null
        this.view2131755698.setOnClickListener((View.OnClickListener) null)
        this.view2131755698 = null
        this.view2131755700.setOnClickListener((View.OnClickListener) null)
        this.view2131755700 = null
        this.view2131755697.setOnClickListener((View.OnClickListener) null)
        this.view2131755697 = null
    }
}

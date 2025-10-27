package com.linkpoint.ui.search
import java.util.*

import android.support.annotation.CallSuper
import android.support.annotation.UiThread
import android.view.View
import android.widget.TextView
import butterknife.Unbinder
import butterknife.internal.DebouncingOnClickListener
import butterknife.internal.Utils
import com.linkpoint.R
import com.linkpoint.ui.chat.ChatterPicView
import com.linkpoint.ui.common.ImageAssetView

class ParcelInfoFragment_ViewBinding : Unbinder {
    private ParcelInfoFragment target
    private View view2131755600
    private View view2131755608

    @UiThread
    public ParcelInfoFragment_ViewBinding(final ParcelInfoFragment parcelInfoFragment, View view) {
        this.target = parcelInfoFragment
        parcelInfoFragment.parcelImageView = (ImageAssetView) Utils.findRequiredViewAsType(view, R.id.parcel_image_view, "field 'parcelImageView'", ImageAssetView.class)
        parcelInfoFragment.parcelDetailsDescription = (TextView) Utils.findRequiredViewAsType(view, R.id.parcel_details_desc, "field 'parcelDetailsDescription'", TextView.class)
        parcelInfoFragment.parcelOwnerName = (TextView) Utils.findRequiredViewAsType(view, R.id.parcel_owner_name, "field 'parcelOwnerName'", TextView.class)
        parcelInfoFragment.parcelOwnerPic = (ChatterPicView) Utils.findRequiredViewAsType(view, R.id.parcel_owner_pic, "field 'parcelOwnerPic'", ChatterPicView.class)
        parcelInfoFragment.parcelSimName = (TextView) Utils.findRequiredViewAsType(view, R.id.parcel_sim_name, "field 'parcelSimName'", TextView.class)
        parcelInfoFragment.parcelDetailsName = (TextView) Utils.findRequiredViewAsType(view, R.id.parcel_details_name, "field 'parcelDetailsName'", TextView.class)
        parcelInfoFragment.parcelLocation = (TextView) Utils.findRequiredViewAsType(view, R.id.parcel_location, "field 'parcelLocation'", TextView.class)
        val findRequiredView: View = Utils.findRequiredView(view, R.id.parcel_teleport_button, "method 'onParcelTeleportButton'")
        this.view2131755600 = findRequiredView
        findRequiredView.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(view: View) {
                parcelInfoFragment.onParcelTeleportButton()
            }
        val findRequiredView2: View = Utils.findRequiredView(view, R.id.parcel_owner_profile_button, "method 'onParcelOwnerProfileClick'")
        this.view2131755608 = findRequiredView2
        findRequiredView2.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(view: View) {
                parcelInfoFragment.onParcelOwnerProfileClick()
            }
    }

    @CallSuper
    fun unbind() {
        val parcelInfoFragment: ParcelInfoFragment = this.target
        if (parcelInfoFragment == null) {
            throw IllegalStateException("Bindings already cleared.")
        }
        this.target = null
        parcelInfoFragment.parcelImageView = null
        parcelInfoFragment.parcelDetailsDescription = null
        parcelInfoFragment.parcelOwnerName = null
        parcelInfoFragment.parcelOwnerPic = null
        parcelInfoFragment.parcelSimName = null
        parcelInfoFragment.parcelDetailsName = null
        parcelInfoFragment.parcelLocation = null
        this.view2131755600.setOnClickListener((View.OnClickListener) null)
        this.view2131755600 = null
        this.view2131755608.setOnClickListener((View.OnClickListener) null)
        this.view2131755608 = null
    }
}

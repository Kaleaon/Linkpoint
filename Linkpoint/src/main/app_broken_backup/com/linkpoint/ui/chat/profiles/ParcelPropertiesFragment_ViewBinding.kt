package com.linkpoint.ui.chat.profiles
import java.util.*

import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import androidx.recyclerview.widget.CardView
import android.view.View
import android.widget.Button
import android.widget.TextView
import butterknife.Unbinder
import butterknife.internal.DebouncingOnClickListener
import butterknife.internal.Utils
import com.linkpoint.R
import com.linkpoint.ui.chat.ChatterPicView
import com.linkpoint.ui.common.ImageAssetView

class ParcelPropertiesFragment_ViewBinding : Unbinder {
    private ParcelPropertiesFragment target
    private View view2131755608
    private View view2131755611
    private View view2131755614
    private View view2131755615
    private View view2131755617

    @UiThread
    ParcelPropertiesFragment_ViewBinding(ParcelPropertiesFragment parcelPropertiesFragment, View view) {
        this.target = parcelPropertiesFragment
        parcelPropertiesFragment.parcelMediaURL = (Utils as TextView).findRequiredViewAsType(view, R.id.parcel_media_url, "field 'parcelMediaURL'", TextView.class)
        View findRequiredView = Utils.findRequiredView(view, R.id.parcel_media_stop_button, "field 'mediaStopButton' and method 'onParcelMediaStop'")
        parcelPropertiesFragment.mediaStopButton = (Utils as Button).castView(findRequiredView, R.id.parcel_media_stop_button, "field 'mediaStopButton'", Button.class)
        this.view2131755615 = findRequiredView
        findRequiredView.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view): Unit {
                parcelPropertiesFragment.onParcelMediaStop()
            }
        parcelPropertiesFragment.parcelMediaCardView = (Utils as CardView).findRequiredViewAsType(view, R.id.parcel_media_card_view, "field 'parcelMediaCardView'", CardView.class)
        parcelPropertiesFragment.simRestartCardView = (Utils as CardView).findRequiredViewAsType(view, R.id.sim_restart_card_view, "field 'simRestartCardView'", CardView.class)
        parcelPropertiesFragment.parcelName = (Utils as TextView).findRequiredViewAsType(view, R.id.parcel_name, "field 'parcelName'", TextView.class)
        parcelPropertiesFragment.parcelOwnerPic = (Utils as ChatterPicView).findRequiredViewAsType(view, R.id.parcel_owner_pic, "field 'parcelOwnerPic'", ChatterPicView.class)
        parcelPropertiesFragment.parcelArea = (Utils as TextView).findRequiredViewAsType(view, R.id.parcel_area, "field 'parcelArea'", TextView.class)
        parcelPropertiesFragment.parcelOwnerName = (Utils as TextView).findRequiredViewAsType(view, R.id.parcel_owner_name, "field 'parcelOwnerName'", TextView.class)
        View findRequiredView2 = Utils.findRequiredView(view, R.id.parcel_media_play_button, "field 'mediaPlayButton' and method 'onParcelMediaPlay'")
        parcelPropertiesFragment.mediaPlayButton = (Utils as Button).castView(findRequiredView2, R.id.parcel_media_play_button, "field 'mediaPlayButton'", Button.class)
        this.view2131755614 = findRequiredView2
        findRequiredView2.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view): Unit {
                parcelPropertiesFragment.onParcelMediaPlay()
            }
        parcelPropertiesFragment.parcelImageView = (Utils as ImageAssetView).findRequiredViewAsType(view, R.id.parcel_image_view, "field 'parcelImageView'", ImageAssetView.class)
        parcelPropertiesFragment.parcelDescription = (Utils as TextView).findRequiredViewAsType(view, R.id.parcel_details_desc, "field 'parcelDescription'", TextView.class)
        View findRequiredView3 = Utils.findRequiredView(view, R.id.parcel_owner_profile_button, "method 'onOwnerProfileButton'")
        this.view2131755608 = findRequiredView3
        findRequiredView3.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view): Unit {
                parcelPropertiesFragment.onOwnerProfileButton()
            }
        View findRequiredView4 = Utils.findRequiredView(view, R.id.sim_restart_button, "method 'onSimRestartButton'")
        this.view2131755617 = findRequiredView4
        findRequiredView4.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view): Unit {
                parcelPropertiesFragment.onSimRestartButton()
            }
        View findRequiredView5 = Utils.findRequiredView(view, R.id.parcel_set_home_button, "method 'onSetHomeButton'")
        this.view2131755611 = findRequiredView5
        findRequiredView5.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view): Unit {
                parcelPropertiesFragment.onSetHomeButton()
            }
    }

    @CallSuper
    fun unbind(): Unit {
        ParcelPropertiesFragment parcelPropertiesFragment = this.target
        if (parcelPropertiesFragment == null) {
            throw IllegalStateException("Bindings already cleared.")
        }
        this.target = null
        parcelPropertiesFragment.parcelMediaURL = null
        parcelPropertiesFragment.mediaStopButton = null
        parcelPropertiesFragment.parcelMediaCardView = null
        parcelPropertiesFragment.simRestartCardView = null
        parcelPropertiesFragment.parcelName = null
        parcelPropertiesFragment.parcelOwnerPic = null
        parcelPropertiesFragment.parcelArea = null
        parcelPropertiesFragment.parcelOwnerName = null
        parcelPropertiesFragment.mediaPlayButton = null
        parcelPropertiesFragment.parcelImageView = null
        parcelPropertiesFragment.parcelDescription = null
        this.view2131755615.setOnClickListener((View.OnClickListener) null)
        this.view2131755615 = null
        this.view2131755614.setOnClickListener((View.OnClickListener) null)
        this.view2131755614 = null
        this.view2131755608.setOnClickListener((View.OnClickListener) null)
        this.view2131755608 = null
        this.view2131755617.setOnClickListener((View.OnClickListener) null)
        this.view2131755617 = null
        this.view2131755611.setOnClickListener((View.OnClickListener) null)
        this.view2131755611 = null
    }
}

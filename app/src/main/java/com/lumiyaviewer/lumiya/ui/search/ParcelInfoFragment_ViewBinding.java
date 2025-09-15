package com.lumiyaviewer.lumiya.ui.search;
import java.util.*;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;

public class ParcelInfoFragment_ViewBinding implements Unbinder {
    private ParcelInfoFragment target;
    private View view2131755600;
    private View view2131755608;

    @UiThread
    public ParcelInfoFragment_ViewBinding(final ParcelInfoFragment parcelInfoFragment, View view) {
        this.target = parcelInfoFragment;
        parcelInfoFragment.parcelImageView = (ImageAssetView) Utils.findRequiredViewAsType(view, R.id.parcel_image_view, "field 'parcelImageView'", ImageAssetView.class);
        parcelInfoFragment.parcelDetailsDescription = (TextView) Utils.findRequiredViewAsType(view, R.id.parcel_details_desc, "field 'parcelDetailsDescription'", TextView.class);
        parcelInfoFragment.parcelOwnerName = (TextView) Utils.findRequiredViewAsType(view, R.id.parcel_owner_name, "field 'parcelOwnerName'", TextView.class);
        parcelInfoFragment.parcelOwnerPic = (ChatterPicView) Utils.findRequiredViewAsType(view, R.id.parcel_owner_pic, "field 'parcelOwnerPic'", ChatterPicView.class);
        parcelInfoFragment.parcelSimName = (TextView) Utils.findRequiredViewAsType(view, R.id.parcel_sim_name, "field 'parcelSimName'", TextView.class);
        parcelInfoFragment.parcelDetailsName = (TextView) Utils.findRequiredViewAsType(view, R.id.parcel_details_name, "field 'parcelDetailsName'", TextView.class);
        parcelInfoFragment.parcelLocation = (TextView) Utils.findRequiredViewAsType(view, R.id.parcel_location, "field 'parcelLocation'", TextView.class);
        View findRequiredView = Utils.findRequiredView(view, R.id.parcel_teleport_button, "method 'onParcelTeleportButton'");
        this.view2131755600 = findRequiredView;
        findRequiredView.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                parcelInfoFragment.onParcelTeleportButton();
            }
        View findRequiredView2 = Utils.findRequiredView(view, R.id.parcel_owner_profile_button, "method 'onParcelOwnerProfileClick'");
        this.view2131755608 = findRequiredView2;
        findRequiredView2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                parcelInfoFragment.onParcelOwnerProfileClick();
            }
    }

    @CallSuper
    public void unbind() {
        ParcelInfoFragment parcelInfoFragment = this.target;
        if (parcelInfoFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        parcelInfoFragment.parcelImageView = null;
        parcelInfoFragment.parcelDetailsDescription = null;
        parcelInfoFragment.parcelOwnerName = null;
        parcelInfoFragment.parcelOwnerPic = null;
        parcelInfoFragment.parcelSimName = null;
        parcelInfoFragment.parcelDetailsName = null;
        parcelInfoFragment.parcelLocation = null;
        this.view2131755600.setOnClickListener((View.OnClickListener) null);
        this.view2131755600 = null;
        this.view2131755608.setOnClickListener((View.OnClickListener) null);
        this.view2131755608 = null;
    }
}

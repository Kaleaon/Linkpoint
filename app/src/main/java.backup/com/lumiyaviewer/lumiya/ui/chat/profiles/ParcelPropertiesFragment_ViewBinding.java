package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;

public class ParcelPropertiesFragment_ViewBinding implements Unbinder {
    private ParcelPropertiesFragment target;
    private View view2131755608;
    private View view2131755611;
    private View view2131755614;
    private View view2131755615;
    private View view2131755617;

    @UiThread
    public ParcelPropertiesFragment_ViewBinding(final ParcelPropertiesFragment parcelPropertiesFragment, View view) {
        this.target = parcelPropertiesFragment;
        parcelPropertiesFragment.parcelMediaURL = (TextView) Utils.findRequiredViewAsType(view, R.id.parcel_media_url, "field 'parcelMediaURL'", TextView.class);
        View findRequiredView = Utils.findRequiredView(view, R.id.parcel_media_stop_button, "field 'mediaStopButton' and method 'onParcelMediaStop'");
        parcelPropertiesFragment.mediaStopButton = (Button) Utils.castView(findRequiredView, R.id.parcel_media_stop_button, "field 'mediaStopButton'", Button.class);
        this.view2131755615 = findRequiredView;
        findRequiredView.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                parcelPropertiesFragment.onParcelMediaStop();
            }
        });
        parcelPropertiesFragment.parcelMediaCardView = (CardView) Utils.findRequiredViewAsType(view, R.id.parcel_media_card_view, "field 'parcelMediaCardView'", CardView.class);
        parcelPropertiesFragment.simRestartCardView = (CardView) Utils.findRequiredViewAsType(view, R.id.sim_restart_card_view, "field 'simRestartCardView'", CardView.class);
        parcelPropertiesFragment.parcelName = (TextView) Utils.findRequiredViewAsType(view, R.id.parcel_name, "field 'parcelName'", TextView.class);
        parcelPropertiesFragment.parcelOwnerPic = (ChatterPicView) Utils.findRequiredViewAsType(view, R.id.parcel_owner_pic, "field 'parcelOwnerPic'", ChatterPicView.class);
        parcelPropertiesFragment.parcelArea = (TextView) Utils.findRequiredViewAsType(view, R.id.parcel_area, "field 'parcelArea'", TextView.class);
        parcelPropertiesFragment.parcelOwnerName = (TextView) Utils.findRequiredViewAsType(view, R.id.parcel_owner_name, "field 'parcelOwnerName'", TextView.class);
        View findRequiredView2 = Utils.findRequiredView(view, R.id.parcel_media_play_button, "field 'mediaPlayButton' and method 'onParcelMediaPlay'");
        parcelPropertiesFragment.mediaPlayButton = (Button) Utils.castView(findRequiredView2, R.id.parcel_media_play_button, "field 'mediaPlayButton'", Button.class);
        this.view2131755614 = findRequiredView2;
        findRequiredView2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                parcelPropertiesFragment.onParcelMediaPlay();
            }
        });
        parcelPropertiesFragment.parcelImageView = (ImageAssetView) Utils.findRequiredViewAsType(view, R.id.parcel_image_view, "field 'parcelImageView'", ImageAssetView.class);
        parcelPropertiesFragment.parcelDescription = (TextView) Utils.findRequiredViewAsType(view, R.id.parcel_details_desc, "field 'parcelDescription'", TextView.class);
        View findRequiredView3 = Utils.findRequiredView(view, R.id.parcel_owner_profile_button, "method 'onOwnerProfileButton'");
        this.view2131755608 = findRequiredView3;
        findRequiredView3.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                parcelPropertiesFragment.onOwnerProfileButton();
            }
        });
        View findRequiredView4 = Utils.findRequiredView(view, R.id.sim_restart_button, "method 'onSimRestartButton'");
        this.view2131755617 = findRequiredView4;
        findRequiredView4.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                parcelPropertiesFragment.onSimRestartButton();
            }
        });
        View findRequiredView5 = Utils.findRequiredView(view, R.id.parcel_set_home_button, "method 'onSetHomeButton'");
        this.view2131755611 = findRequiredView5;
        findRequiredView5.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                parcelPropertiesFragment.onSetHomeButton();
            }
        });
    }

    @CallSuper
    public void unbind() {
        ParcelPropertiesFragment parcelPropertiesFragment = this.target;
        if (parcelPropertiesFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        parcelPropertiesFragment.parcelMediaURL = null;
        parcelPropertiesFragment.mediaStopButton = null;
        parcelPropertiesFragment.parcelMediaCardView = null;
        parcelPropertiesFragment.simRestartCardView = null;
        parcelPropertiesFragment.parcelName = null;
        parcelPropertiesFragment.parcelOwnerPic = null;
        parcelPropertiesFragment.parcelArea = null;
        parcelPropertiesFragment.parcelOwnerName = null;
        parcelPropertiesFragment.mediaPlayButton = null;
        parcelPropertiesFragment.parcelImageView = null;
        parcelPropertiesFragment.parcelDescription = null;
        this.view2131755615.setOnClickListener((View.OnClickListener) null);
        this.view2131755615 = null;
        this.view2131755614.setOnClickListener((View.OnClickListener) null);
        this.view2131755614 = null;
        this.view2131755608.setOnClickListener((View.OnClickListener) null);
        this.view2131755608 = null;
        this.view2131755617.setOnClickListener((View.OnClickListener) null);
        this.view2131755617 = null;
        this.view2131755611.setOnClickListener((View.OnClickListener) null);
        this.view2131755611 = null;
    }
}

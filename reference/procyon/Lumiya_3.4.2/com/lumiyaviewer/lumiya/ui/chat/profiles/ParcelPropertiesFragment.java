// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import android.content.DialogInterface$OnClickListener;
import android.support.v7.app.AlertDialog;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import android.content.Intent;
import com.lumiyaviewer.lumiya.StreamingMediaService;
import com.google.common.base.Strings;
import butterknife.OnClick;
import android.support.v4.app.Fragment;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import butterknife.ButterKnife;
import android.view.View;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Toast;
import java.io.Serializable;
import android.os.Bundle;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import butterknife.Unbinder;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import android.support.v7.widget.CardView;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.ui.common.ChatterNameDisplayer;
import butterknife.BindView;
import android.widget.Button;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;

public class ParcelPropertiesFragment extends FragmentWithTitle
{
    public static final String PARCEL_DATA_KEY = "parcelData";
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit;
    private final SubscriptionData<SubscriptionSingleKey, Boolean> isPlayingMedia;
    @BindView(2131755614)
    Button mediaPlayButton;
    @BindView(2131755615)
    Button mediaStopButton;
    private final ChatterNameDisplayer ownerNameDisplayer;
    @BindView(2131755610)
    TextView parcelArea;
    private ParcelData parcelData;
    @BindView(2131755599)
    TextView parcelDescription;
    @BindView(2131755602)
    ImageAssetView parcelImageView;
    @BindView(2131755612)
    CardView parcelMediaCardView;
    @BindView(2131755613)
    TextView parcelMediaURL;
    @BindView(2131755609)
    TextView parcelName;
    @BindView(2131755606)
    TextView parcelOwnerName;
    @BindView(2131755607)
    ChatterPicView parcelOwnerPic;
    @BindView(2131755616)
    CardView simRestartCardView;
    private Unbinder unbinder;
    private UserManager userManager;
    
    public ParcelPropertiesFragment() {
        this.unbinder = null;
        this.parcelData = null;
        this.userManager = null;
        this.ownerNameDisplayer = new ChatterNameDisplayer();
        this.isPlayingMedia = new SubscriptionData<SubscriptionSingleKey, Boolean>(UIThreadExecutor.getInstance(), new _$Lambda$3KadVkUh82bQPaUr2S81wOMi_ug$3(this));
        this.agentCircuit = new SubscriptionData<UUID, SLAgentCircuit>(UIThreadExecutor.getInstance(), new _$Lambda$3KadVkUh82bQPaUr2S81wOMi_ug$4(this));
    }
    
    public static Bundle makeSelection(final UUID uuid, final ParcelData parcelData) {
        final Bundle bundle = new Bundle();
        bundle.putString("activeAgentUUID", uuid.toString());
        bundle.putSerializable("parcelData", (Serializable)parcelData);
        return bundle;
    }
    
    private void onAgentCircuit(final SLAgentCircuit slAgentCircuit) {
        this.updateSimOptions();
    }
    
    private void onIsPlayingMedia(final Boolean b) {
        this.updatePlayingStatus();
    }
    
    private void updatePlayingStatus() {
        final int n = 0;
        if (this.unbinder != null) {
            final Boolean b = this.isPlayingMedia.getData();
            final boolean b2 = b != null && b;
            final Button mediaPlayButton = this.mediaPlayButton;
            int visibility;
            if (b2) {
                visibility = 8;
            }
            else {
                visibility = 0;
            }
            mediaPlayButton.setVisibility(visibility);
            final Button mediaStopButton = this.mediaStopButton;
            int visibility2;
            if (b2) {
                visibility2 = n;
            }
            else {
                visibility2 = 8;
            }
            mediaStopButton.setVisibility(visibility2);
        }
    }
    
    private void updateSimOptions() {
        int visibility = 0;
        if (this.unbinder != null) {
            final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
            final boolean b = slAgentCircuit != null && slAgentCircuit.getIsEstateManager();
            final CardView simRestartCardView = this.simRestartCardView;
            if (!b) {
                visibility = 8;
            }
            simRestartCardView.setVisibility(visibility);
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        final View inflate = layoutInflater.inflate(2130968705, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
        this.ownerNameDisplayer.bindViews(this.parcelOwnerName, this.parcelOwnerPic);
        this.parcelImageView.setVerticalFit(true);
        this.parcelImageView.setAlignTop(true);
        return inflate;
    }
    
    @Override
    public void onDestroyView() {
        if (this.unbinder != null) {
            this.unbinder.unbind();
            this.ownerNameDisplayer.unbindViews();
            this.unbinder = null;
        }
        super.onDestroyView();
    }
    
    @OnClick({ 2131755608 })
    public void onOwnerProfileButton() {
        if (this.parcelData != null) {
            if (this.parcelData.isGroupOwned()) {
                DetailsActivity.showEmbeddedDetails(this.getActivity(), GroupProfileFragment.class, ChatterFragment.makeSelection(this.ownerNameDisplayer.getChatterID()));
            }
            else {
                DetailsActivity.showEmbeddedDetails(this.getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(this.ownerNameDisplayer.getChatterID()));
            }
        }
    }
    
    @OnClick({ 2131755614 })
    public void onParcelMediaPlay() {
        if (this.parcelData != null && (Strings.isNullOrEmpty(this.parcelData.getMediaURL()) ^ true) && this.userManager != null) {
            final Intent intent = new Intent(this.getContext(), (Class)StreamingMediaService.class);
            intent.setAction("com.lumiyaviewer.lumiya.ACTION_PLAY_MEDIA");
            ActivityUtils.setActiveAgentID(intent, this.userManager.getUserID());
            intent.putExtra("parcelData", (Serializable)this.parcelData);
            intent.putExtra("media_url", this.parcelData.getMediaURL());
            intent.putExtra("location_name", this.parcelData.getName());
            this.getContext().startService(intent);
        }
    }
    
    @OnClick({ 2131755615 })
    public void onParcelMediaStop() {
        final Intent intent = new Intent(this.getContext(), (Class)StreamingMediaService.class);
        intent.setAction("com.lumiyaviewer.lumiya.ACTION_STOP_MEDIA");
        this.getContext().startService(intent);
    }
    
    @OnClick({ 2131755611 })
    public void onSetHomeButton() {
        if (this.agentCircuit.getData() != null) {
            new AlertDialog.Builder(this.getContext()).setMessage(2131297019).setCancelable(true).setPositiveButton("Yes", (DialogInterface$OnClickListener)new _$Lambda$3KadVkUh82bQPaUr2S81wOMi_ug$2(this)).setNegativeButton("No", (DialogInterface$OnClickListener)new _$Lambda$3KadVkUh82bQPaUr2S81wOMi_ug()).create().show();
        }
    }
    
    @OnClick({ 2131755617 })
    public void onSimRestartButton() {
        final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
        if (slAgentCircuit != null) {
            new AlertDialog.Builder(this.getContext()).setMessage(2131296935).setPositiveButton("Yes", (DialogInterface$OnClickListener)new _$Lambda$3KadVkUh82bQPaUr2S81wOMi_ug$5(this, slAgentCircuit)).setNegativeButton("No", (DialogInterface$OnClickListener)new _$Lambda$3KadVkUh82bQPaUr2S81wOMi_ug$1()).setCancelable(true).create().show();
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        this.userManager = UserManager.getUserManager(UUIDPool.getUUID(this.getArguments().getString("activeAgentUUID")));
        this.parcelData = (ParcelData)this.getArguments().getSerializable("parcelData");
        this.isPlayingMedia.subscribe((Subscribable<SubscriptionSingleKey, Boolean>)StreamingMediaService.isPlayingMedia, SubscriptionSingleKey.Value);
        if (this.userManager != null) {
            this.agentCircuit.subscribe(UserManager.agentCircuits(), this.userManager.getUserID());
        }
        if (this.parcelData != null && this.userManager != null && this.unbinder != null) {
            final ChatterNameDisplayer ownerNameDisplayer = this.ownerNameDisplayer;
            ChatterID chatterID;
            if (this.parcelData.isGroupOwned()) {
                chatterID = ChatterID.getGroupChatterID(this.userManager.getUserID(), this.parcelData.getOwnerID());
            }
            else {
                chatterID = ChatterID.getUserChatterID(this.userManager.getUserID(), this.parcelData.getOwnerID());
            }
            ownerNameDisplayer.setChatterID(chatterID);
            this.parcelImageView.setAssetID(this.parcelData.getSnapshotUUID());
            this.parcelName.setText((CharSequence)this.parcelData.getName());
            this.parcelArea.setText((CharSequence)this.getString(2131296856, this.parcelData.getArea()));
            final TextView parcelDescription = this.parcelDescription;
            String text;
            if (Strings.isNullOrEmpty(this.parcelData.getDescription())) {
                text = this.getString(2131296347);
            }
            else {
                text = this.parcelData.getDescription();
            }
            parcelDescription.setText((CharSequence)text);
            final CardView parcelMediaCardView = this.parcelMediaCardView;
            int visibility;
            if (Strings.isNullOrEmpty(this.parcelData.getMediaURL())) {
                visibility = 8;
            }
            else {
                visibility = 0;
            }
            parcelMediaCardView.setVisibility(visibility);
            this.parcelMediaURL.setText((CharSequence)this.parcelData.getMediaURL());
            this.updatePlayingStatus();
            this.updateSimOptions();
        }
    }
    
    @Override
    public void onStop() {
        this.userManager = null;
        this.parcelData = null;
        this.ownerNameDisplayer.setChatterID(null);
        this.parcelImageView.setAssetID(null);
        this.isPlayingMedia.unsubscribe();
        this.agentCircuit.unsubscribe();
        super.onStop();
    }
    
    private class SetHomeLocationAsyncTask extends AsyncTask<Void, Void, Boolean>
    {
        private ProgressDialog progressDialog;
        
        protected Boolean doInBackground(final Void... array) {
            final SLAgentCircuit slAgentCircuit = ParcelPropertiesFragment.this.agentCircuit.getData();
            return slAgentCircuit != null && slAgentCircuit.getModules().userProfiles.SetHomeLocation();
        }
        
        protected void onPostExecute(final Boolean b) {
            this.progressDialog.dismiss();
            if (b == null || (b ^ true)) {
                new AlertDialog.Builder(ParcelPropertiesFragment.this.getContext()).setMessage(2131297020).setCancelable(true).create().show();
            }
            else {
                new AlertDialog.Builder(ParcelPropertiesFragment.this.getContext()).setMessage(2131297021).setCancelable(true).create().show();
            }
        }
        
        protected void onPreExecute() {
            this.progressDialog = ProgressDialog.show(ParcelPropertiesFragment.this.getContext(), (CharSequence)null, (CharSequence)ParcelPropertiesFragment.this.getString(2131297026), true);
        }
    }
}

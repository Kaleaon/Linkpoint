package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.google.common.base.Strings;
import com.google.common.logging.nano.Vr;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.StreamingMediaService;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.ChatterNameDisplayer;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;

/* loaded from: classes.dex */
public class ParcelPropertiesFragment extends FragmentWithTitle {
    public static final String PARCEL_DATA_KEY = "parcelData";

    @BindView(R.id.parcel_media_play_button)
    Button mediaPlayButton;

    @BindView(R.id.parcel_media_stop_button)
    Button mediaStopButton;

    @BindView(R.id.parcel_area)
    TextView parcelArea;

    @BindView(R.id.parcel_details_desc)
    TextView parcelDescription;

    @BindView(R.id.parcel_image_view)
    ImageAssetView parcelImageView;

    @BindView(R.id.parcel_media_card_view)
    CardView parcelMediaCardView;

    @BindView(R.id.parcel_media_url)
    TextView parcelMediaURL;

    @BindView(R.id.parcel_name)
    TextView parcelName;

    @BindView(R.id.parcel_owner_name)
    TextView parcelOwnerName;

    @BindView(R.id.parcel_owner_pic)
    ChatterPicView parcelOwnerPic;

    @BindView(R.id.sim_restart_card_view)
    CardView simRestartCardView;
    private Unbinder unbinder = null;
    private ParcelData parcelData = null;
    private UserManager userManager = null;
    private final ChatterNameDisplayer ownerNameDisplayer = new ChatterNameDisplayer();
    private final SubscriptionData<SubscriptionSingleKey, Boolean> isPlayingMedia = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$3KadVkUh82bQPaUr2S81wOMi_ug.3
        private final /* synthetic */ void $m$0(Object obj) {
            ((ParcelPropertiesFragment) this).m512x3c670cfa((Boolean) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$3KadVkUh82bQPaUr2S81wOMi_ug.4
        private final /* synthetic */ void $m$0(Object obj) {
            ((ParcelPropertiesFragment) this).m513x3c670cfb((SLAgentCircuit) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });

    /* loaded from: classes.dex */
    private class SetHomeLocationAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog progressDialog;

        private SetHomeLocationAsyncTask() {
        }

        /* synthetic */ SetHomeLocationAsyncTask(ParcelPropertiesFragment parcelPropertiesFragment, SetHomeLocationAsyncTask setHomeLocationAsyncTask) {
            this();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... voidArr) {
            SLAgentCircuit sLAgentCircuit = (SLAgentCircuit) ParcelPropertiesFragment.this.agentCircuit.getData();
            return Boolean.valueOf(sLAgentCircuit != null ? sLAgentCircuit.getModules().userProfiles.SetHomeLocation() : false);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean bool) {
            this.progressDialog.dismiss();
            if (bool == null || (!bool.booleanValue())) {
                new AlertDialog.Builder(ParcelPropertiesFragment.this.getContext()).setMessage(R.string.set_home_failed).setCancelable(true).create().show();
            } else {
                new AlertDialog.Builder(ParcelPropertiesFragment.this.getContext()).setMessage(R.string.set_home_success).setCancelable(true).create().show();
            }
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            this.progressDialog = ProgressDialog.show(ParcelPropertiesFragment.this.getContext(), null, ParcelPropertiesFragment.this.getString(R.string.setting_home_location), true);
        }
    }

    public static Bundle makeSelection(UUID uuid, ParcelData parcelData) {
        Bundle bundle = new Bundle();
        bundle.putString("activeAgentUUID", uuid.toString());
        bundle.putSerializable(PARCEL_DATA_KEY, parcelData);
        return bundle;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onAgentCircuit, reason: merged with bridge method [inline-methods] */
    public void m513x3c670cfb(SLAgentCircuit sLAgentCircuit) {
        updateSimOptions();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onIsPlayingMedia, reason: merged with bridge method [inline-methods] */
    public void m512x3c670cfa(Boolean bool) {
        updatePlayingStatus();
    }

    private void updatePlayingStatus() {
        if (this.unbinder != null) {
            Boolean data = this.isPlayingMedia.getData();
            boolean booleanValue = data != null ? data.booleanValue() : false;
            this.mediaPlayButton.setVisibility(booleanValue ? 8 : 0);
            this.mediaStopButton.setVisibility(booleanValue ? 0 : 8);
        }
    }

    private void updateSimOptions() {
        if (this.unbinder != null) {
            SLAgentCircuit data = this.agentCircuit.getData();
            this.simRestartCardView.setVisibility(data != null ? data.getIsEstateManager() : false ? 0 : 8);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_ParcelPropertiesFragment_8181, reason: not valid java name */
    public /* synthetic */ void m514x74bd5c0c(SLAgentCircuit sLAgentCircuit, DialogInterface dialogInterface, int i) {
        sLAgentCircuit.RestartRegion(Vr.VREvent.VrCore.ErrorCode.CONTROLLER_INFO_READ_ERROR);
        Toast.makeText(getContext(), R.string.region_restart_ok_message, 1).show();
        dialogInterface.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_ParcelPropertiesFragment_9097, reason: not valid java name */
    public /* synthetic */ void m515x74bdcccf(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        new SetHomeLocationAsyncTask(this, null).execute(new Void[0]);
    }

    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.parcel_properties_fragment, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
        this.ownerNameDisplayer.bindViews(this.parcelOwnerName, this.parcelOwnerPic);
        this.parcelImageView.setVerticalFit(true);
        this.parcelImageView.setAlignTop(true);
        return inflate;
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroyView() {
        if (this.unbinder != null) {
            this.unbinder.unbind();
            this.ownerNameDisplayer.unbindViews();
            this.unbinder = null;
        }
        super.onDestroyView();
    }

    @OnClick({R.id.parcel_owner_profile_button})
    public void onOwnerProfileButton() {
        if (this.parcelData != null) {
            if (this.parcelData.isGroupOwned()) {
                DetailsActivity.showEmbeddedDetails(getActivity(), GroupProfileFragment.class, GroupProfileFragment.makeSelection(this.ownerNameDisplayer.getChatterID()));
            } else {
                DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(this.ownerNameDisplayer.getChatterID()));
            }
        }
    }

    @OnClick({R.id.parcel_media_play_button})
    public void onParcelMediaPlay() {
        if (this.parcelData == null || !(!Strings.isNullOrEmpty(this.parcelData.getMediaURL())) || this.userManager == null) {
            return;
        }
        Intent intent = new Intent(getContext(), (Class<?>) StreamingMediaService.class);
        intent.setAction("com.lumiyaviewer.lumiya.ACTION_PLAY_MEDIA");
        ActivityUtils.setActiveAgentID(intent, this.userManager.getUserID());
        intent.putExtra(PARCEL_DATA_KEY, this.parcelData);
        intent.putExtra(StreamingMediaService.MEDIA_URL_KEY, this.parcelData.getMediaURL());
        intent.putExtra(StreamingMediaService.LOCATION_NAME_KEY, this.parcelData.getName());
        getContext().startService(intent);
    }

    @OnClick({R.id.parcel_media_stop_button})
    public void onParcelMediaStop() {
        Intent intent = new Intent(getContext(), (Class<?>) StreamingMediaService.class);
        intent.setAction("com.lumiyaviewer.lumiya.ACTION_STOP_MEDIA");
        getContext().startService(intent);
    }

    @OnClick({R.id.parcel_set_home_button})
    public void onSetHomeButton() {
        if (this.agentCircuit.getData() != null) {
            new AlertDialog.Builder(getContext()).setMessage(R.string.set_home_confirm_title).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$3KadVkUh82bQPaUr2S81wOMi_ug.2
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                    ((ParcelPropertiesFragment) this).m515x74bdcccf(dialogInterface, i);
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    $m$0(dialogInterface, i);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$3KadVkUh82bQPaUr2S81wOMi_ug
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    $m$0(dialogInterface, i);
                }
            }).create().show();
        }
    }

    @OnClick({R.id.sim_restart_button})
    public void onSimRestartButton() {
        final SLAgentCircuit data = this.agentCircuit.getData();
        if (data != null) {
            new AlertDialog.Builder(getContext()).setMessage(R.string.restart_region_confirm_title).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$3KadVkUh82bQPaUr2S81wOMi_ug.5
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                    ((ParcelPropertiesFragment) this).m514x74bd5c0c((SLAgentCircuit) data, dialogInterface, i);
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    $m$0(dialogInterface, i);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$3KadVkUh82bQPaUr2S81wOMi_ug.1
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    $m$0(dialogInterface, i);
                }
            }).setCancelable(true).create().show();
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        this.userManager = UserManager.getUserManager(UUIDPool.getUUID(getArguments().getString("activeAgentUUID")));
        this.parcelData = (ParcelData) getArguments().getSerializable(PARCEL_DATA_KEY);
        this.isPlayingMedia.subscribe(StreamingMediaService.isPlayingMedia, SubscriptionSingleKey.Value);
        if (this.userManager != null) {
            this.agentCircuit.subscribe(UserManager.agentCircuits(), this.userManager.getUserID());
        }
        if (this.parcelData == null || this.userManager == null || this.unbinder == null) {
            return;
        }
        this.ownerNameDisplayer.setChatterID(this.parcelData.isGroupOwned() ? ChatterID.getGroupChatterID(this.userManager.getUserID(), this.parcelData.getOwnerID()) : ChatterID.getUserChatterID(this.userManager.getUserID(), this.parcelData.getOwnerID()));
        this.parcelImageView.setAssetID(this.parcelData.getSnapshotUUID());
        this.parcelName.setText(this.parcelData.getName());
        this.parcelArea.setText(getString(R.string.parcel_area_format, Integer.valueOf(this.parcelData.getArea())));
        this.parcelDescription.setText(Strings.isNullOrEmpty(this.parcelData.getDescription()) ? getString(R.string.asset_no_description) : this.parcelData.getDescription());
        this.parcelMediaCardView.setVisibility(Strings.isNullOrEmpty(this.parcelData.getMediaURL()) ? 8 : 0);
        this.parcelMediaURL.setText(this.parcelData.getMediaURL());
        updatePlayingStatus();
        updateSimOptions();
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onStop() {
        this.userManager = null;
        this.parcelData = null;
        this.ownerNameDisplayer.setChatterID(null);
        this.parcelImageView.setAssetID(null);
        this.isPlayingMedia.unsubscribe();
        this.agentCircuit.unsubscribe();
        super.onStop();
    }
}

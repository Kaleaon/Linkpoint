package com.lumiyaviewer.lumiya.ui.search;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.ParcelInfoReply;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.chat.profiles.GroupProfileFragment;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.TeleportProgressDialog;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;
/* loaded from: classes.dex */
public class ParcelInfoFragment extends FragmentWithTitle implements ReloadableFragment, LoadableMonitor.OnLoadableDataChangedListener, ChatterNameRetriever.OnChatterNameUpdated {
    private static final String PARCEL_UUID_KEY = "parcelUUID";
    @BindView(R.id.parcel_details_desc)
    TextView parcelDetailsDescription;
    @BindView(R.id.parcel_details_name)
    TextView parcelDetailsName;
    @BindView(R.id.parcel_image_view)
    ImageAssetView parcelImageView;
    @BindView(R.id.parcel_location)
    TextView parcelLocation;
    @BindView(R.id.parcel_owner_name)
    TextView parcelOwnerName;
    @BindView(R.id.parcel_owner_pic)
    ChatterPicView parcelOwnerPic;
    @BindView(R.id.parcel_sim_name)
    TextView parcelSimName;
    private Unbinder unbinder;
    private final SubscriptionData<UUID, ParcelInfoReply> parcelInfoReply = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.parcelInfoReply).withDataChangedListener(this);
    private ChatterNameRetriever ownerNameRetriever = null;
    private ChatterNameRetriever ownerGroupNameRetriever = null;

    public static Bundle makeSelection(UUID uuid, UUID uuid2) {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        bundle.putString(PARCEL_UUID_KEY, uuid2.toString());
        return bundle;
    }

    private void showParcelInfo(UUID uuid) {
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        if (userManager == null || uuid == null) {
            return;
        }
        Debug.Printf("ParcelInfo: subscribing for UUID %s", uuid);
        this.parcelInfoReply.subscribe(userManager.parcelInfoData().getPool(), uuid);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_search_ParcelInfoFragment_9137  reason: not valid java name */
    public /* synthetic */ void m859xc965e5a8(UserManager userManager, LLVector3 lLVector3, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (activeAgentCircuit != null) {
            new TeleportProgressDialog(getContext(), userManager, R.string.teleporting_progress_message).show();
            activeAgentCircuit.TeleportToGlobalPosition(lLVector3);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
    public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        if ((chatterNameRetriever != this.ownerNameRetriever && chatterNameRetriever != this.ownerGroupNameRetriever) || this.unbinder == null || this.ownerGroupNameRetriever == null || this.ownerNameRetriever == null) {
            return;
        }
        ChatterNameRetriever chatterNameRetriever2 = this.ownerGroupNameRetriever.getResolvedName() != null ? this.ownerGroupNameRetriever : this.ownerNameRetriever;
        String resolvedName = chatterNameRetriever2.getResolvedName();
        this.parcelOwnerName.setText(resolvedName != null ? resolvedName : getString(R.string.name_loading_title));
        this.parcelOwnerPic.setVisibility(0);
        this.parcelOwnerPic.setChatterID(chatterNameRetriever2.chatterID, resolvedName);
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.parcel_info, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_parcel_selected), getString(R.string.failed_to_load_parcel_data));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        this.parcelImageView.setAlignTop(true);
        this.parcelImageView.setVerticalFit(true);
        return inflate;
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroyView() {
        if (this.unbinder != null) {
            this.unbinder.unbind();
            this.unbinder = null;
        }
        super.onDestroyView();
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
    public void onLoadableDataChanged() {
        ParcelInfoReply data = this.parcelInfoReply.getData();
        Debug.Printf("ParcelInfo: loadable data %s", data);
        UUID activeAgentID = ActivityUtils.getActiveAgentID(getArguments());
        if (this.unbinder == null || data == null || activeAgentID == null) {
            return;
        }
        if (this.ownerNameRetriever != null) {
            this.ownerNameRetriever.dispose();
            this.ownerNameRetriever = null;
        }
        if (this.ownerGroupNameRetriever != null) {
            this.ownerGroupNameRetriever.dispose();
            this.ownerGroupNameRetriever = null;
        }
        String stringFromVariableOEM = SLMessage.stringFromVariableOEM(data.Data_Field.Name);
        setTitle(stringFromVariableOEM, null);
        this.parcelDetailsName.setText(stringFromVariableOEM);
        String trim = SLMessage.stringFromVariableOEM(data.Data_Field.Desc).trim();
        TextView textView = this.parcelDetailsDescription;
        if (Strings.isNullOrEmpty(trim)) {
            trim = getString(R.string.asset_no_description);
        }
        textView.setText(trim);
        Debug.Printf("ParcelInfo: ownerID = %s", data.Data_Field.OwnerID);
        if (UUIDPool.ZeroUUID.equals(data.Data_Field.OwnerID)) {
            this.parcelOwnerName.setText(R.string.group_owned);
            this.parcelOwnerPic.setVisibility(8);
        } else {
            this.ownerNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(activeAgentID, data.Data_Field.OwnerID), this, UIThreadExecutor.getSerialInstance());
            this.ownerGroupNameRetriever = new ChatterNameRetriever(ChatterID.getGroupChatterID(activeAgentID, data.Data_Field.OwnerID), this, UIThreadExecutor.getSerialInstance());
        }
        this.parcelImageView.setAssetID(data.Data_Field.SnapshotID);
        this.parcelSimName.setText(SLMessage.stringFromVariableOEM(data.Data_Field.SimName));
        this.parcelLocation.setText(getString(R.string.parcel_location_format, Float.valueOf(data.Data_Field.GlobalX % 256.0f), Float.valueOf(data.Data_Field.GlobalY % 256.0f), Float.valueOf(data.Data_Field.GlobalZ)));
    }

    @OnClick({R.id.parcel_owner_profile_button})
    public void onParcelOwnerProfileClick() {
        UUID activeAgentID = ActivityUtils.getActiveAgentID(getArguments());
        ParcelInfoReply data = this.parcelInfoReply.getData();
        if (activeAgentID == null || data == null) {
            return;
        }
        if (this.ownerGroupNameRetriever != null && this.ownerGroupNameRetriever.getResolvedName() != null) {
            DetailsActivity.showEmbeddedDetails(getActivity(), GroupProfileFragment.class, GroupProfileFragment.makeSelection(this.ownerGroupNameRetriever.chatterID));
        } else if (this.ownerNameRetriever == null || this.ownerNameRetriever.getResolvedName() == null) {
            DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(activeAgentID, data.Data_Field.OwnerID)));
        } else {
            DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(this.ownerNameRetriever.chatterID));
        }
    }

    @OnClick({R.id.parcel_teleport_button})
    public void onParcelTeleportButton() {
        final UserManager userManager = ActivityUtils.getUserManager(getArguments());
        ParcelInfoReply data = this.parcelInfoReply.getData();
        if (data == null || userManager == null) {
            return;
        }
        final LLVector3 lLVector3 = new LLVector3(data.Data_Field.GlobalX, data.Data_Field.GlobalY, data.Data_Field.GlobalZ);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getActivity().getString(R.string.teleport_parcel_confirm_title)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.search.-$Lambda$5Jqy4HmgAu6T9fnroWh-Zqm3eJE.1
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                ((ParcelInfoFragment) this).m859xc965e5a8((UserManager) userManager, (LLVector3) lLVector3, dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.search.-$Lambda$5Jqy4HmgAu6T9fnroWh-Zqm3eJE
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder.create().show();
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        showParcelInfo(UUIDPool.getUUID(getArguments().getString(PARCEL_UUID_KEY)));
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onStop() {
        this.loadableMonitor.unsubscribeAll();
        if (this.ownerNameRetriever != null) {
            this.ownerNameRetriever.dispose();
            this.ownerNameRetriever = null;
        }
        if (this.ownerGroupNameRetriever != null) {
            this.ownerGroupNameRetriever.dispose();
            this.ownerGroupNameRetriever = null;
        }
        if (this.unbinder != null) {
            this.parcelOwnerPic.setChatterID(null, null);
            this.parcelImageView.setAssetID(null);
        }
        super.onStop();
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ReloadableFragment
    public void setFragmentArgs(Intent intent, Bundle bundle) {
        getArguments().putAll(bundle);
        if (isFragmentStarted()) {
            showParcelInfo(UUIDPool.getUUID(bundle.getString(PARCEL_UUID_KEY)));
        }
    }
}

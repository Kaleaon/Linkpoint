// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.search;

import android.content.Intent;
import android.content.DialogInterface$OnClickListener;
import android.content.Context;
import android.app.AlertDialog$Builder;
import butterknife.OnClick;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import android.support.v4.app.Fragment;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.chat.profiles.GroupProfileFragment;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import android.support.v4.widget.SwipeRefreshLayout;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import butterknife.ButterKnife;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.ui.common.TeleportProgressDialog;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import android.os.Bundle;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import butterknife.Unbinder;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.slproto.messages.ParcelInfoReply;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import butterknife.BindView;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;

public class ParcelInfoFragment extends FragmentWithTitle implements ReloadableFragment, OnLoadableDataChangedListener, OnChatterNameUpdated
{
    private static final String PARCEL_UUID_KEY = "parcelUUID";
    private final LoadableMonitor loadableMonitor;
    private ChatterNameRetriever ownerGroupNameRetriever;
    private ChatterNameRetriever ownerNameRetriever;
    @BindView(2131755599)
    TextView parcelDetailsDescription;
    @BindView(2131755598)
    TextView parcelDetailsName;
    @BindView(2131755602)
    ImageAssetView parcelImageView;
    private final SubscriptionData<UUID, ParcelInfoReply> parcelInfoReply;
    @BindView(2131755605)
    TextView parcelLocation;
    @BindView(2131755606)
    TextView parcelOwnerName;
    @BindView(2131755607)
    ChatterPicView parcelOwnerPic;
    @BindView(2131755604)
    TextView parcelSimName;
    private Unbinder unbinder;
    
    public ParcelInfoFragment() {
        this.parcelInfoReply = new SubscriptionData<UUID, ParcelInfoReply>(UIThreadExecutor.getInstance());
        this.loadableMonitor = new LoadableMonitor(new Loadable[] { this.parcelInfoReply }).withDataChangedListener((LoadableMonitor.OnLoadableDataChangedListener)this);
        this.ownerNameRetriever = null;
        this.ownerGroupNameRetriever = null;
    }
    
    public static Bundle makeSelection(final UUID uuid, final UUID uuid2) {
        final Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        bundle.putString("parcelUUID", uuid2.toString());
        return bundle;
    }
    
    private void showParcelInfo(final UUID uuid) {
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        if (userManager != null && uuid != null) {
            Debug.Printf("ParcelInfo: subscribing for UUID %s", uuid);
            this.parcelInfoReply.subscribe(userManager.parcelInfoData().getPool(), uuid);
        }
    }
    
    @Override
    public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        if ((chatterNameRetriever == this.ownerNameRetriever || chatterNameRetriever == this.ownerGroupNameRetriever) && this.unbinder != null && this.ownerGroupNameRetriever != null && this.ownerNameRetriever != null) {
            if (this.ownerGroupNameRetriever.getResolvedName() != null) {
                chatterNameRetriever = this.ownerGroupNameRetriever;
            }
            else {
                chatterNameRetriever = this.ownerNameRetriever;
            }
            final String resolvedName = chatterNameRetriever.getResolvedName();
            final TextView parcelOwnerName = this.parcelOwnerName;
            String string;
            if (resolvedName != null) {
                string = resolvedName;
            }
            else {
                string = this.getString(2131296712);
            }
            parcelOwnerName.setText((CharSequence)string);
            this.parcelOwnerPic.setVisibility(0);
            this.parcelOwnerPic.setChatterID(chatterNameRetriever.chatterID, resolvedName);
        }
    }
    
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        final View inflate = layoutInflater.inflate(2130968704, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
        this.loadableMonitor.setLoadingLayout((LoadingLayout)inflate.findViewById(2131755197), this.getString(2131296749), this.getString(2131296541));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout)inflate.findViewById(2131755195));
        this.parcelImageView.setAlignTop(true);
        this.parcelImageView.setVerticalFit(true);
        return inflate;
    }
    
    @Override
    public void onDestroyView() {
        if (this.unbinder != null) {
            this.unbinder.unbind();
            this.unbinder = null;
        }
        super.onDestroyView();
    }
    
    @Override
    public void onLoadableDataChanged() {
        final ParcelInfoReply parcelInfoReply = this.parcelInfoReply.getData();
        Debug.Printf("ParcelInfo: loadable data %s", parcelInfoReply);
        final UUID activeAgentID = ActivityUtils.getActiveAgentID(this.getArguments());
        if (this.unbinder != null && parcelInfoReply != null && activeAgentID != null) {
            if (this.ownerNameRetriever != null) {
                this.ownerNameRetriever.dispose();
                this.ownerNameRetriever = null;
            }
            if (this.ownerGroupNameRetriever != null) {
                this.ownerGroupNameRetriever.dispose();
                this.ownerGroupNameRetriever = null;
            }
            final String stringFromVariableOEM = SLMessage.stringFromVariableOEM(parcelInfoReply.Data_Field.Name);
            this.setTitle(stringFromVariableOEM, null);
            this.parcelDetailsName.setText((CharSequence)stringFromVariableOEM);
            String text = SLMessage.stringFromVariableOEM(parcelInfoReply.Data_Field.Desc).trim();
            final TextView parcelDetailsDescription = this.parcelDetailsDescription;
            if (Strings.isNullOrEmpty(text)) {
                text = this.getString(2131296347);
            }
            parcelDetailsDescription.setText((CharSequence)text);
            Debug.Printf("ParcelInfo: ownerID = %s", parcelInfoReply.Data_Field.OwnerID);
            if (UUIDPool.ZeroUUID.equals(parcelInfoReply.Data_Field.OwnerID)) {
                this.parcelOwnerName.setText(2131296591);
                this.parcelOwnerPic.setVisibility(8);
            }
            else {
                this.ownerNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(activeAgentID, parcelInfoReply.Data_Field.OwnerID), (ChatterNameRetriever.OnChatterNameUpdated)this, UIThreadExecutor.getSerialInstance());
                this.ownerGroupNameRetriever = new ChatterNameRetriever(ChatterID.getGroupChatterID(activeAgentID, parcelInfoReply.Data_Field.OwnerID), (ChatterNameRetriever.OnChatterNameUpdated)this, UIThreadExecutor.getSerialInstance());
            }
            this.parcelImageView.setAssetID(parcelInfoReply.Data_Field.SnapshotID);
            this.parcelSimName.setText((CharSequence)SLMessage.stringFromVariableOEM(parcelInfoReply.Data_Field.SimName));
            this.parcelLocation.setText((CharSequence)this.getString(2131296858, parcelInfoReply.Data_Field.GlobalX % 256.0f, parcelInfoReply.Data_Field.GlobalY % 256.0f, parcelInfoReply.Data_Field.GlobalZ));
        }
    }
    
    @OnClick({ 2131755608 })
    public void onParcelOwnerProfileClick() {
        final UUID activeAgentID = ActivityUtils.getActiveAgentID(this.getArguments());
        final ParcelInfoReply parcelInfoReply = this.parcelInfoReply.getData();
        if (activeAgentID != null && parcelInfoReply != null) {
            if (this.ownerGroupNameRetriever != null && this.ownerGroupNameRetriever.getResolvedName() != null) {
                DetailsActivity.showEmbeddedDetails(this.getActivity(), GroupProfileFragment.class, ChatterFragment.makeSelection(this.ownerGroupNameRetriever.chatterID));
            }
            else if (this.ownerNameRetriever != null && this.ownerNameRetriever.getResolvedName() != null) {
                DetailsActivity.showEmbeddedDetails(this.getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(this.ownerNameRetriever.chatterID));
            }
            else {
                DetailsActivity.showEmbeddedDetails(this.getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(activeAgentID, parcelInfoReply.Data_Field.OwnerID)));
            }
        }
    }
    
    @OnClick({ 2131755600 })
    public void onParcelTeleportButton() {
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        final ParcelInfoReply parcelInfoReply = this.parcelInfoReply.getData();
        if (parcelInfoReply != null && userManager != null) {
            final LLVector3 llVector3 = new LLVector3(parcelInfoReply.Data_Field.GlobalX, parcelInfoReply.Data_Field.GlobalY, parcelInfoReply.Data_Field.GlobalZ);
            final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder((Context)this.getActivity());
            alertDialog$Builder.setMessage((CharSequence)this.getActivity().getString(2131297098)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$5Jqy4HmgAu6T9fnroWh_Zqm3eJE$1(this, userManager, llVector3)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$5Jqy4HmgAu6T9fnroWh_Zqm3eJE());
            alertDialog$Builder.create().show();
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        this.showParcelInfo(UUIDPool.getUUID(this.getArguments().getString("parcelUUID")));
    }
    
    @Override
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
    
    @Override
    public void setFragmentArgs(final Intent intent, final Bundle bundle) {
        this.getArguments().putAll(bundle);
        if (this.isFragmentStarted()) {
            this.showParcelInfo(UUIDPool.getUUID(bundle.getString("parcelUUID")));
        }
    }
}

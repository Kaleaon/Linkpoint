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

public class ParcelInfoFragment extends FragmentWithTitle implements ReloadableFragment, LoadableMonitor.OnLoadableDataChangedListener, ChatterNameRetriever.OnChatterNameUpdated {
    private static final String PARCEL_UUID_KEY = "parcelUUID";
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.parcelInfoReply).withDataChangedListener(this);
    private ChatterNameRetriever ownerGroupNameRetriever = null;
    private ChatterNameRetriever ownerNameRetriever = null;
    @BindView(2131755599)
    TextView parcelDetailsDescription;
    @BindView(2131755598)
    TextView parcelDetailsName;
    @BindView(2131755602)
    ImageAssetView parcelImageView;
    private final SubscriptionData<UUID, ParcelInfoReply> parcelInfoReply = new SubscriptionData<>(UIThreadExecutor.getInstance());
    @BindView(2131755605)
    TextView parcelLocation;
    @BindView(2131755606)
    TextView parcelOwnerName;
    @BindView(2131755607)
    ChatterPicView parcelOwnerPic;
    @BindView(2131755604)
    TextView parcelSimName;
    private Unbinder unbinder;

    public static Bundle makeSelection(UUID uuid, UUID uuid2) {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        bundle.putString(PARCEL_UUID_KEY, uuid2.toString());
        return bundle;
    }

    private void showParcelInfo(UUID uuid) {
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        if (userManager != null && uuid != null) {
            Debug.Printf("ParcelInfo: subscribing for UUID %s", uuid);
            this.parcelInfoReply.subscribe(userManager.parcelInfoData().getPool(), uuid);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_search_ParcelInfoFragment_9137  reason: not valid java name */
    public /* synthetic */ void m848lambda$com_lumiyaviewer_lumiya_ui_search_ParcelInfoFragment_9137(UserManager userManager, LLVector3 lLVector3, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (activeAgentCircuit != null) {
            new TeleportProgressDialog(getContext(), userManager, R.string.teleporting_progress_message).show();
            activeAgentCircuit.TeleportToGlobalPosition(lLVector3);
        }
    }

    public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        if ((chatterNameRetriever == this.ownerNameRetriever || chatterNameRetriever == this.ownerGroupNameRetriever) && this.unbinder != null && this.ownerGroupNameRetriever != null && this.ownerNameRetriever != null) {
            ChatterNameRetriever chatterNameRetriever2 = this.ownerGroupNameRetriever.getResolvedName() != null ? this.ownerGroupNameRetriever : this.ownerNameRetriever;
            String resolvedName = chatterNameRetriever2.getResolvedName();
            this.parcelOwnerName.setText(resolvedName != null ? resolvedName : getString(R.string.name_loading_title));
            this.parcelOwnerPic.setVisibility(0);
            this.parcelOwnerPic.setChatterID(chatterNameRetriever2.chatterID, resolvedName);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.parcel_info, viewGroup, false);
        this.unbinder = ButterKnife.bind((Object) this, inflate);
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_parcel_selected), getString(R.string.failed_to_load_parcel_data));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        this.parcelImageView.setAlignTop(true);
        this.parcelImageView.setVerticalFit(true);
        return inflate;
    }

    public void onDestroyView() {
        if (this.unbinder != null) {
            this.unbinder.unbind();
            this.unbinder = null;
        }
        super.onDestroyView();
    }

    public void onLoadableDataChanged() {
        ParcelInfoReply data = this.parcelInfoReply.getData();
        Debug.Printf("ParcelInfo: loadable data %s", data);
        UUID activeAgentID = ActivityUtils.getActiveAgentID(getArguments());
        if (this.unbinder != null && data != null && activeAgentID != null) {
            if (this.ownerNameRetriever != null) {
                this.ownerNameRetriever.dispose();
                this.ownerNameRetriever = null;
            }
            if (this.ownerGroupNameRetriever != null) {
                this.ownerGroupNameRetriever.dispose();
                this.ownerGroupNameRetriever = null;
            }
            String stringFromVariableOEM = SLMessage.stringFromVariableOEM(data.Data_Field.Name);
            setTitle(stringFromVariableOEM, (String) null);
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
    }

    @OnClick({2131755608})
    public void onParcelOwnerProfileClick() {
        UUID activeAgentID = ActivityUtils.getActiveAgentID(getArguments());
        ParcelInfoReply data = this.parcelInfoReply.getData();
        if (activeAgentID != null && data != null) {
            if (this.ownerGroupNameRetriever != null && this.ownerGroupNameRetriever.getResolvedName() != null) {
                DetailsActivity.showEmbeddedDetails(getActivity(), GroupProfileFragment.class, GroupProfileFragment.makeSelection(this.ownerGroupNameRetriever.chatterID));
            } else if (this.ownerNameRetriever == null || this.ownerNameRetriever.getResolvedName() == null) {
                DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(activeAgentID, data.Data_Field.OwnerID)));
            } else {
                DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(this.ownerNameRetriever.chatterID));
            }
        }
    }

    @OnClick({2131755600})
    public void onParcelTeleportButton() {
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        ParcelInfoReply data = this.parcelInfoReply.getData();
        if (data != null && userManager != null) {
            LLVector3 lLVector3 = new LLVector3(data.Data_Field.GlobalX, data.Data_Field.GlobalY, data.Data_Field.GlobalZ);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getActivity().getString(R.string.teleport_parcel_confirm_title)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener(this, userManager, lLVector3) {

                /* renamed from: -$f0 */
                private final /* synthetic */ Object f581$f0;

                /* renamed from: -$f1 */
                private final /* synthetic */ Object f582$f1;

                /* renamed from: -$f2 */
                private final /* synthetic */ Object f583$f2;

                private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.search.-$Lambda$5Jqy4HmgAu6T9fnroWh-Zqm3eJE.1.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.search.-$Lambda$5Jqy4HmgAu6T9fnroWh-Zqm3eJE.1.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
                	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
                	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.addArgDot(InsnGen.java:91)
                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:697)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                
*/

                public final void brokenMethod(
        // TODO: implement method
    }
            }).setNegativeButton("No", new $Lambda$5Jqy4HmgAu6T9fnroWhZqm3eJE());
            builder.create().show();
        }
    }

    public void onStart() {
        super.onStart();
        showParcelInfo(UUIDPool.getUUID(getArguments().getString(PARCEL_UUID_KEY)));
    }

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
            this.parcelOwnerPic.setChatterID((ChatterID) null, (String) null);
            this.parcelImageView.setAssetID((UUID) null);
        }
        super.onStop();
    }

    public void setFragmentArgs(Intent intent, Bundle bundle) {
        getArguments().putAll(bundle);
        if (isFragmentStarted()) {
            showParcelInfo(UUIDPool.getUUID(bundle.getString(PARCEL_UUID_KEY)));
        }
    }
}

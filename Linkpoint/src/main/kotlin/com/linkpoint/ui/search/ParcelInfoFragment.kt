package com.linkpoint.ui.search

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.google.common.base.Strings
import com.linkpoint.Debug
import com.linkpoint.R
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.messages.ParcelInfoReply
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.ChatterNameRetriever
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.ChatterPicView
import com.linkpoint.ui.chat.profiles.GroupProfileFragment
import com.linkpoint.ui.chat.profiles.UserProfileFragment
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.DetailsActivity
import com.linkpoint.ui.common.FragmentWithTitle
import com.linkpoint.ui.common.ImageAssetView
import com.linkpoint.ui.common.LoadingLayout
import com.linkpoint.ui.common.ReloadableFragment
import com.linkpoint.ui.common.TeleportProgressDialog
import com.linkpoint.ui.common.loadmon.LoadableMonitor
import com.linkpoint.utils.UUIDPool
import java.util.UUID

class ParcelInfoFragment : FragmentWithTitle(), ReloadableFragment, LoadableMonitor.OnLoadableDataChangedListener, ChatterNameRetriever.OnChatterNameUpdated {
    private const val PARCEL_UUID_KEY: String = "parcelUUID"
    private val LoadableMonitor loadableMonitor = LoadableMonitor(this.parcelInfoReply).withDataChangedListener(this)
    private ChatterNameRetriever ownerGroupNameRetriever = null
    private ChatterNameRetriever ownerNameRetriever = null
    @BindView(2131755599)
    TextView parcelDetailsDescription
    @BindView(2131755598)
    TextView parcelDetailsName
    @BindView(2131755602)
    ImageAssetView parcelImageView
    private val SubscriptionData<UUID, ParcelInfoReply> parcelInfoReply = SubscriptionData<>(UIThreadExecutor.getInstance())
    @BindView(2131755605)
    TextView parcelLocation
    @BindView(2131755606)
    TextView parcelOwnerName
    @BindView(2131755607)
    ChatterPicView parcelOwnerPic
    @BindView(2131755604)
    TextView parcelSimName
    private Unbinder unbinder

    @JvmStatic
     fun makeSelection(uuid: UUID, uuid2: UUID): Bundle {
        val bundle: Bundle = Bundle()
        ActivityUtils.setActiveAgentID(bundle, uuid)
        bundle.putString(PARCEL_UUID_KEY, uuid2.toString())
        return bundle
    }

     private fun showParcelInfo(uuid: UUID) {
        val userManager: UserManager = ActivityUtils.getUserManager(getArguments())
        if (userManager != null && uuid != null) {
            Debug.Printf("ParcelInfo: subscribing for UUID %s", uuid)
            this.parcelInfoReply.subscribe(userManager.parcelInfoData().getPool(), uuid)
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_search_ParcelInfoFragment_9137  reason: not valid java name */
    public /* synthetic */ Unit m848lambda$com_lumiyaviewer_lumiya_ui_search_ParcelInfoFragment_9137(UserManager userManager, LLVector3 lLVector3, DialogInterface dialogInterface, Int i) {
        dialogInterface.dismiss()
        val activeAgentCircuit: SLAgentCircuit = userManager.getActiveAgentCircuit()
        if (activeAgentCircuit != null) {
            TeleportProgressDialog(getContext(), userManager, R.string.teleporting_progress_message).show()
            activeAgentCircuit.TeleportToGlobalPosition(lLVector3)
        }
    }

    fun onChatterNameUpdated(chatterNameRetriever: ChatterNameRetriever) {
        if ((chatterNameRetriever == this.ownerNameRetriever || chatterNameRetriever == this.ownerGroupNameRetriever) && this.unbinder != null && this.ownerGroupNameRetriever != null && this.ownerNameRetriever != null) {
            val chatterNameRetriever2: ChatterNameRetriever = this.ownerGroupNameRetriever.getResolvedName() != null ? this.ownerGroupNameRetriever : this.ownerNameRetriever
            val resolvedName: String = chatterNameRetriever2.getResolvedName()
            this.parcelOwnerName.setText(resolvedName != null ? resolvedName : getString(R.string.name_loading_title))
            this.parcelOwnerPic.setVisibility(0)
            this.parcelOwnerPic.setChatterID(chatterNameRetriever2.chatterID, resolvedName)
        }
    }

     public fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup, bundle: Bundle): View {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        val inflate: View = layoutInflater.inflate(R.layout.parcel_info, viewGroup, false)
        this.unbinder = ButterKnife.bind((Object) this, inflate)
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_parcel_selected), getString(R.string.failed_to_load_parcel_data))
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout))
        this.parcelImageView.setAlignTop(true)
        this.parcelImageView.setVerticalFit(true)
        return inflate
    }

    fun onDestroyView() {
        if (this.unbinder != null) {
            this.unbinder.unbind()
            this.unbinder = null
        }
        super.onDestroyView()
    }

    fun onLoadableDataChanged() {
        val data: ParcelInfoReply = this.parcelInfoReply.getData()
        Debug.Printf("ParcelInfo: loadable data %s", data)
        val activeAgentID: UUID = ActivityUtils.getActiveAgentID(getArguments())
        if (this.unbinder != null && data != null && activeAgentID != null) {
            if (this.ownerNameRetriever != null) {
                this.ownerNameRetriever.dispose()
                this.ownerNameRetriever = null
            }
            if (this.ownerGroupNameRetriever != null) {
                this.ownerGroupNameRetriever.dispose()
                this.ownerGroupNameRetriever = null
            }
            val stringFromVariableOEM: String = SLMessage.stringFromVariableOEM(data.Data_Field.Name)
            setTitle(stringFromVariableOEM, (String) null)
            this.parcelDetailsName.setText(stringFromVariableOEM)
            val trim: String = SLMessage.stringFromVariableOEM(data.Data_Field.Desc).trim()
            val textView: TextView = this.parcelDetailsDescription
            if (Strings.isNullOrEmpty(trim)) {
                trim = getString(R.string.asset_no_description)
            }
            textView.setText(trim)
            Debug.Printf("ParcelInfo: ownerID = %s", data.Data_Field.OwnerID)
            if (UUIDPool.ZeroUUID.equals(data.Data_Field.OwnerID)) {
                this.parcelOwnerName.setText(R.string.group_owned)
                this.parcelOwnerPic.setVisibility(8)
            } else {
                this.ownerNameRetriever = ChatterNameRetriever(ChatterID.getUserChatterID(activeAgentID, data.Data_Field.OwnerID), this, UIThreadExecutor.getSerialInstance())
                this.ownerGroupNameRetriever = ChatterNameRetriever(ChatterID.getGroupChatterID(activeAgentID, data.Data_Field.OwnerID), this, UIThreadExecutor.getSerialInstance())
            }
            this.parcelImageView.setAssetID(data.Data_Field.SnapshotID)
            this.parcelSimName.setText(SLMessage.stringFromVariableOEM(data.Data_Field.SimName))
            this.parcelLocation.setText(getString(R.string.parcel_location_format, Float.valueOf(data.Data_Field.GlobalX % 256.0f), Float.valueOf(data.Data_Field.GlobalY % 256.0f), Float.valueOf(data.Data_Field.GlobalZ)))
        }
    }

    @OnClick({2131755608})
    fun onParcelOwnerProfileClick() {
        val activeAgentID: UUID = ActivityUtils.getActiveAgentID(getArguments())
        val data: ParcelInfoReply = this.parcelInfoReply.getData()
        if (activeAgentID != null && data != null) {
            if (this.ownerGroupNameRetriever != null && this.ownerGroupNameRetriever.getResolvedName() != null) {
                DetailsActivity.showEmbeddedDetails(getActivity(), GroupProfileFragment.class, GroupProfileFragment.makeSelection(this.ownerGroupNameRetriever.chatterID))
            } else if (this.ownerNameRetriever == null || this.ownerNameRetriever.getResolvedName() == null) {
                DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(activeAgentID, data.Data_Field.OwnerID)))
            } else {
                DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(this.ownerNameRetriever.chatterID))
            }
        }
    }

    @OnClick({2131755600})
    fun onParcelTeleportButton() {
        val userManager: UserManager = ActivityUtils.getUserManager(getArguments())
        val data: ParcelInfoReply = this.parcelInfoReply.getData()
        if (data != null && userManager != null) {
            val lLVector3: LLVector3 = LLVector3(data.Data_Field.GlobalX, data.Data_Field.GlobalY, data.Data_Field.GlobalZ)
            AlertDialog.Builder builder = AlertDialog.Builder(getActivity())
            builder.setMessage(getActivity().getString(R.string.teleport_parcel_confirm_title)).setCancelable(true).setPositiveButton("Yes", DialogInterface.OnClickListener(this, userManager, lLVector3) {

                /* renamed from: -$f0 */
                private val /* synthetic */ Object f581$f0

                /* renamed from: -$f1 */
                private val /* synthetic */ Object f582$f1

                /* renamed from: -$f2 */
                private val /* synthetic */ Object f583$f2

                private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.search.-$Lambda$5Jqy4HmgAu6T9fnroWh-Zqm3eJE.1.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.search.-$Lambda$5Jqy4HmgAu6T9fnroWh-Zqm3eJE.1.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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

            }).setNegativeButton("No", $Lambda$5Jqy4HmgAu6T9fnroWhZqm3eJE())
            builder.create().show()
        }
    }

    fun onStart() {
        super.onStart()
        showParcelInfo(UUIDPool.getUUID(getArguments().getString(PARCEL_UUID_KEY)))
    }

    fun onStop() {
        this.loadableMonitor.unsubscribeAll()
        if (this.ownerNameRetriever != null) {
            this.ownerNameRetriever.dispose()
            this.ownerNameRetriever = null
        }
        if (this.ownerGroupNameRetriever != null) {
            this.ownerGroupNameRetriever.dispose()
            this.ownerGroupNameRetriever = null
        }
        if (this.unbinder != null) {
            this.parcelOwnerPic.setChatterID((ChatterID) null, (String) null)
            this.parcelImageView.setAssetID((UUID) null)
        }
        super.onStop()
    }

    fun setFragmentArgs(intent: Intent, bundle: Bundle) {
        getArguments().putAll(bundle)
        if (isFragmentStarted()) {
            showParcelInfo(UUIDPool.getUUID(bundle.getString(PARCEL_UUID_KEY)))
        }
    }
}

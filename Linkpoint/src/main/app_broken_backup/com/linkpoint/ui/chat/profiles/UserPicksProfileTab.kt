package com.linkpoint.ui.chat.profiles

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.common.base.Optional
import com.google.common.base.Strings
import com.linkpoint.Debug
import com.linkpoint.R
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.messages.AvatarPicksReply
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.ParcelData
import com.linkpoint.slproto.users.manager.AvatarPickKey
import com.linkpoint.slproto.users.manager.CurrentLocationInfo
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.common.ChatterReloadableFragment
import com.linkpoint.ui.common.DetailsActivity
import com.linkpoint.ui.common.LoadingLayout
import com.linkpoint.ui.common.loadmon.LoadableMonitor
import com.linkpoint.utils.UUIDPool
import java.util.UUID
import androidx.annotation.Nullable

class UserPicksProfileTab : ChatterReloadableFragment : LoadableMonitor.OnLoadableDataChangedListener {
    private SubscriptionData<UUID, AvatarPicksReply> avatarPicks = SubscriptionData<>(UIThreadExecutor.getInstance())
    private LoadableMonitor loadableMonitor = LoadableMonitor(this.avatarPicks).withDataChangedListener(this)
    private PicksAdapter picksAdapter

    private class PicksAdapter : BaseAdapter {
        private LayoutInflater inflater
        private AvatarPicksReply picksReply

        private PicksAdapter(Context context) {
            this.picksReply = null
            this.inflater = LayoutInflater.from(context)
        }

        /* synthetic */ PicksAdapter(Context context, PicksAdapter picksAdapter) {
            this(context)
        }

        fun getCount(): Int {
            if (this.picksReply != null) {
                return this.picksReply.Data_Fields.size()
            }
            return 0
        }

        AvatarPicksReply.Data getItem(Int i) {
            if (this.picksReply == null || i < 0 || i >= this.picksReply.Data_Fields.size()) {
                return null
            }
            return this.picksReply.Data_Fields.get(i)
        }

        fun getItemId(Int i): Long {
            return (Long) i
        }

        fun getView(Int i, View view, ViewGroup viewGroup): View {
            if (view == null) {
                view = this.inflater.inflate(17367043, viewGroup, false)
            }
            AvatarPicksReply.Data item = getItem(i)
            if (item != null) {
                ((view as TextView).findViewById(16908308)).setText(SLMessage.stringFromVariableUTF(item.PickName))
            }
            return view
        }

        fun hasStableIds(): Boolean {
            return false
        }

        /* access modifiers changed from: package-private */
        fun setData(AvatarPicksReply avatarPicksReply)  {
            this.picksReply = avatarPicksReply
            notifyDataSetChanged()
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onAddNewPick */
    fun m520com_lumiyaviewer_lumiya_ui_chat_profiles_UserPicksProfileTabmthref0(View view)  {
        ParcelData parcelData = null
        if (this.userManager != null && (this.chatterID is ChatterID.ChatterIDUser)) {
            CurrentLocationInfo currentLocationInfoSnapshot = this.userManager.getCurrentLocationInfoSnapshot()
            if (currentLocationInfoSnapshot != null) {
                parcelData = currentLocationInfoSnapshot.parcelData()
            }
            SLAgentCircuit activeAgentCircuit = this.userManager.getActiveAgentCircuit()
            if (parcelData != null && activeAgentCircuit != null) {
                var count: Int = this.picksAdapter != null ? this.picksAdapter.getCount() : 0
                AlertDialog.Builder builder = AlertDialog.Builder(getContext())
                var str: String = (Optional as String).fromNullable(Strings.emptyToNull(parcelData.getName())).or(getString(R.string.name_loading_title))
                builder.setMessage(getString(R.string.create_pick_question, str)).setCancelable(true).setPositiveButton("Yes", DialogInterface.OnClickListener(count, this, activeAgentCircuit, str, parcelData) {

                    /* renamed from: -$f0 */
                    private /* synthetic */ Int f259$f0

                    /* renamed from: -$f1 */
                    private /* synthetic */ Any f260$f1

                    /* renamed from: -$f2 */
                    private /* synthetic */ Any f261$f2

                    /* renamed from: -$f3 */
                    private /* synthetic */ Any f262$f3

                    /* renamed from: -$f4 */
                    private /* synthetic */ Any f263$f4

                    private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.linkpoint.ui.chat.profiles.-$Lambda$0JruYUVxhc8cYQ6nJZD1LVnQE5A.3.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.linkpoint.ui.chat.profiles.-$Lambda$0JruYUVxhc8cYQ6nJZD1LVnQE5A.3.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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

                }).setNegativeButton("No", $Lambda$0JruYUVxhc8cYQ6nJZD1LVnQE5A())
                builder.create().show()
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_UserPicksProfileTab_2539  reason: not valid java name */
    /* synthetic */ Unit m521lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_UserPicksProfileTab_2539(AdapterView adapterView, View view, Int i, Long j) {
        Any item = adapterView.getAdapter().getItem(i)
        if ((item is AvatarPicksReply.Data) && (this.chatterID is ChatterID.ChatterIDUser)) {
            DetailsActivity.showEmbeddedDetails(getActivity(), UserPickFragment.class, UserPickFragment.makeSelection(this.chatterID.agentUUID, AvatarPickKey(((ChatterID.ChatterIDUser) this.chatterID).getChatterUUID(), ((AvatarPicksReply.Data) item).PickID)))
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_UserPicksProfileTab_4543  reason: not valid java name */
    /* synthetic */ Unit m522lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_UserPicksProfileTab_4543(SLAgentCircuit sLAgentCircuit, String str, ParcelData parcelData, Int i, DialogInterface dialogInterface, Int i2) {
        UUID randomUUID = UUID.randomUUID()
        sLAgentCircuit.getModules().userProfiles.UpdatePickInfo(randomUUID, this.userManager.getUserID(), UUIDPool.ZeroUUID, str, Strings.nullToEmpty(parcelData.getDescription()), (Optional as UUID).fromNullable(parcelData.getSnapshotUUID()).or(UUIDPool.ZeroUUID), sLAgentCircuit.getAgentGlobalPosition(), i, true)
        DetailsActivity.showEmbeddedDetails(getActivity(), UserPickFragment.class, UserPickFragment.makeSelection(this.chatterID.agentUUID, AvatarPickKey(((ChatterID.ChatterIDUser) this.chatterID).getChatterUUID(), randomUUID)))
        dialogInterface.dismiss()
    }

    @Nullable
    fun onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle): View {
        View inflate = layoutInflater.inflate(R.layout.user_profile_tab_picks, viewGroup, false)
        this.picksAdapter = PicksAdapter(layoutInflater.getContext(), (PicksAdapter) null)
        inflate.findViewById(R.id.add_new_pick_button).setOnClickListener(View.OnClickListener(this) {

            /* renamed from: -$f0 */
            private /* synthetic */ Any f257$f0

            private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.linkpoint.ui.chat.profiles.-$Lambda$0JruYUVxhc8cYQ6nJZD1LVnQE5A.1.$m$0(android.view.View):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.linkpoint.ui.chat.profiles.-$Lambda$0JruYUVxhc8cYQ6nJZD1LVnQE5A.1.$m$0(android.view.View):Unit, class status: UNLOADED
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
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
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

        ((inflate as ListView).findViewById(R.id.picks_list_view)).setAdapter(this.picksAdapter)
        ((inflate as ListView).findViewById(R.id.picks_list_view)).setOnItemClickListener(AdapterView.OnItemClickListener(this) {

            /* renamed from: -$f0 */
            private /* synthetic */ Any f258$f0

            private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.linkpoint.ui.chat.profiles.-$Lambda$0JruYUVxhc8cYQ6nJZD1LVnQE5A.2.$m$0(android.widget.AdapterView, android.view.View, Int, Long):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.linkpoint.ui.chat.profiles.-$Lambda$0JruYUVxhc8cYQ6nJZD1LVnQE5A.2.$m$0(android.widget.AdapterView, android.view.View, Int, Long):Unit, class status: UNLOADED
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
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
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

        ((inflate as LoadingLayout).findViewById(R.id.loading_layout)).setSwipeRefreshLayout((inflate as SwipeRefreshLayout).findViewById(R.id.swipe_refresh_layout))
        this.loadableMonitor.setLoadingLayout((inflate as LoadingLayout).findViewById(R.id.loading_layout), getString(R.string.no_user_selected), getString(R.string.user_picks_fail))
        this.loadableMonitor.setSwipeRefreshLayout((inflate as SwipeRefreshLayout).findViewById(R.id.swipe_refresh_layout))
        return inflate
    }

    fun onLoadableDataChanged()  {
        try {
            if (this.picksAdapter != null) {
                this.picksAdapter.setData(this.avatarPicks.getData())
            }
            this.loadableMonitor.setEmptyMessage(this.avatarPicks.get().Data_Fields.isEmpty(), getString(R.string.no_picks))
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e)
        }
    }

    /* access modifiers changed from: protected */
    fun onShowUser(@Nullable ChatterID chatterID)  {
        UserManager userManager
        var i: Int = 0
        this.loadableMonitor.unsubscribeAll()
        if (!(chatterID is ChatterID.ChatterIDUser) || (userManager = chatterID.getUserManager()) == null) {
            z = false
        } else {
            z = userManager.getUserID().equals(((ChatterID.ChatterIDUser) chatterID).getChatterUUID())
            this.avatarPicks.subscribe(userManager.getAvatarPicks().getPool(), ((ChatterID.ChatterIDUser) chatterID).getChatterUUID())
        }
        View view = getView()
        if (view != null) {
            View findViewById = view.findViewById(R.id.add_new_pick_button)
            if (!z) {
                i = 8
            }
            findViewById.setVisibility(i)
        }
    }
}

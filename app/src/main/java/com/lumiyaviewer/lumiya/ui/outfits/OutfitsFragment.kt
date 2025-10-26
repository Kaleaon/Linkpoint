package com.lumiyaviewer.lumiya.ui.outfits

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.google.common.base.Objects
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Table
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.eventbus.EventBus
import com.lumiyaviewer.lumiya.eventbus.EventHandler
import com.lumiyaviewer.lumiya.orm.InventoryDB
import com.lumiyaviewer.lumiya.orm.InventoryEntryList
import com.lumiyaviewer.lumiya.orm.InventoryQuery
import com.lumiyaviewer.lumiya.react.Subscription
import com.lumiyaviewer.lumiya.react.SubscriptionData
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey
import com.lumiyaviewer.lumiya.react.UIThreadExecutor
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor
import com.lumiyaviewer.lumiya.ui.inventory.InventoryFolderAdapter
import com.lumiyaviewer.lumiya.ui.inventory.InventoryFragmentHelper
import com.lumiyaviewer.lumiya.ui.inventory.InventorySortOrderChangedEvent
import com.lumiyaviewer.lumiya.utils.UUIDPool
import java.util.Iterator
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class OutfitsFragment : FragmentWithTitle : ReloadableFragment, View.OnClickListener, InventoryFolderAdapter.OnItemCheckboxClickListener {
    private val FOLDER_ID_KEY: String = "folderID"
    private InventoryFolderAdapter adapter = null
    private SubscriptionData<UUID, SLAgentCircuit> agentCircuit = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private /* synthetic */ Any f505$f0

        private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.2.$m$0(java.lang.Any):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.2.$m$0(java.lang.Any):Unit, class status: UNLOADED
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
        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
        	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
        	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
        
*/

    private SubscriptionData<InventoryQuery, InventoryEntryList> entryList = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private /* synthetic */ Any f504$f0

        private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.1.$m$0(java.lang.Any):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.1.$m$0(java.lang.Any):Unit, class status: UNLOADED
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
        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
        	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
        	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
        
*/

    private SubscriptionData<UUID, Boolean> folderLoading = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private /* synthetic */ Any f506$f0

        private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.3.$m$0(java.lang.Any):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.3.$m$0(java.lang.Any):Unit, class status: UNLOADED
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
        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
        	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
        	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
        
*/

    private AdapterView.OnItemClickListener itemClickListener = $Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs(this)
    private ViewGroup listHeader
    private Any listHeaderData = Any()
    private LoadableMonitor loadableMonitor = LoadableMonitor(this.entryList)
    private val myOutfitsFolderUUID: UUID = null
    private SubscriptionData<InventoryQuery, InventoryEntryList> rootFolderEntryList = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private /* synthetic */ Any f509$f0

        private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.6.$m$0(java.lang.Any):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.6.$m$0(java.lang.Any):Unit, class status: UNLOADED
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
        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
        	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
        	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
        
*/

    private SubscriptionData<SubscriptionSingleKey, ImmutableMap<UUID, String>> wornAttachments = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private /* synthetic */ Any f507$f0

        private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.4.$m$0(java.lang.Any):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.4.$m$0(java.lang.Any):Unit, class status: UNLOADED
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
        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
        	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
        	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
        
*/

    private SubscriptionData<SubscriptionSingleKey, UUID> wornOutfitFolder = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private /* synthetic */ Any f510$f0

        private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.7.$m$0(java.lang.Any):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.7.$m$0(java.lang.Any):Unit, class status: UNLOADED
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
        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
        	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
        	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
        
*/

    private SubscriptionData<SubscriptionSingleKey, Table<SLWearableType, UUID, SLWearable>> wornWearables = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private /* synthetic */ Any f508$f0

        private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.5.$m$0(java.lang.Any):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.5.$m$0(java.lang.Any):Unit, class status: UNLOADED
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
        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
        	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
        	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
        
*/


    private Unit changeOutfit(Boolean z) {
        InventoryEntryList<SLInventoryEntry> data = this.entryList.getData()
        SLAgentCircuit data2 = this.agentCircuit.getData()
        if (data != null && data2 != null) {
            ImmutableList.Builder builder = ImmutableList.builder()
            for (SLInventoryEntry sLInventoryEntry : data) {
                if (!sLInventoryEntry.isFolderOrFolderLink()) {
                    builder.add((Any) sLInventoryEntry)
                }
            }
            data2.getModules().avatarAppearance.ChangeOutfit(builder.build(), z, data.getFolder())
        }
    }

    @Nullable
    private UUID getFolderUUID() {
        String str = null
        Bundle arguments = getArguments()
        if (arguments != null) {
            str = arguments.getString(FOLDER_ID_KEY)
        }
        return UUIDPool.getUUID(str)
    }

    private InventoryQuery getInventoryQuery(@Nullable UUID uuid) {
        Boolean z = false
        if (InventoryFragmentHelper.getSortOrder(getContext()) == 0) {
            z = true
        }
        return InventoryQuery.create(uuid, (String) null, true, true, z, (SLAssetType) null)
    }

    @Nullable
    private UserManager getUserManager() {
        return ActivityUtils.getUserManager(getArguments())
    }

    Bundle makeSelection(@Nonnull UUID uuid, @Nullable UUID uuid2) {
        Bundle bundle = Bundle()
        ActivityUtils.setActiveAgentID(bundle, uuid)
        if (uuid2 != null) {
            bundle.putString(FOLDER_ID_KEY, uuid2.toString())
        }
        return bundle
    }

    private Unit navigateToFolder(UUID uuid) {
        getArguments().putString(FOLDER_ID_KEY, uuid.toString())
        showInventoryList(uuid)
    }

    /* access modifiers changed from: private */
    /* renamed from: onAgentCircuit */
    Unit m696com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref1(SLAgentCircuit sLAgentCircuit) {
        SLAvatarAppearance sLAvatarAppearance = null
        if (this.adapter != null) {
            InventoryFolderAdapter inventoryFolderAdapter = this.adapter
            if (sLAgentCircuit != null) {
                sLAvatarAppearance = sLAgentCircuit.getModules().avatarAppearance
            }
            inventoryFolderAdapter.setAvatarAppearance(sLAvatarAppearance)
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onInventoryEntryList */
    Unit m695com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref0(InventoryEntryList inventoryEntryList) {
        Debug.Printf("InventoryFragment (%s): onInventoryEntryList: %d entries", this, Int.valueOf(inventoryEntryList.size()))
        setTitle(inventoryEntryList.getTitle(), (String) null)
        if (this.adapter != null) {
            this.adapter.setData(inventoryEntryList)
        }
        updateLoadingStatus()
    }

    /* access modifiers changed from: private */
    /* renamed from: onLoadingStatusChanged */
    Unit m697com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref2(Boolean bool) {
        updateLoadingStatus()
    }

    /* access modifiers changed from: private */
    /* renamed from: onRootFolderEntryList */
    Unit m700com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref5(InventoryEntryList inventoryEntryList) {
        if (inventoryEntryList != null) {
            Iterator it = inventoryEntryList.iterator()
            while (it.hasNext()) {
                SLInventoryEntry sLInventoryEntry = (SLInventoryEntry) it.next()
                if (sLInventoryEntry.isFolder && sLInventoryEntry.typeDefault == 48) {
                    this.myOutfitsFolderUUID = sLInventoryEntry.uuid
                    this.rootFolderEntryList.unsubscribe()
                    if (getFolderUUID() == null) {
                        showInventoryList(getFolderUUID())
                        return
                    }
                    return
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onWornAttachmentsChanged */
    Unit m698com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref3(ImmutableMap<UUID, String> immutableMap) {
        if (this.adapter != null) {
            this.adapter.setWornAttachments(immutableMap)
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onWornOutfitFolder */
    Unit m701com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref6(UUID uuid) {
        if (this.adapter != null) {
            this.adapter.setWornOutfitFolder(uuid)
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onWornWearablesChanged */
    Unit m699com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref4(Table<SLWearableType, UUID, SLWearable> table) {
        if (this.adapter != null) {
            this.adapter.setWornWearables(table)
        }
    }

    private Unit showInventoryList(@Nullable UUID uuid) {
        UUID rootFolder
        Debug.Printf("OutfitsNewFragment (%s): showInventoryList '%s'", this, uuid)
        View view = getView()
        this.entryList.unsubscribe()
        this.agentCircuit.unsubscribe()
        this.folderLoading.unsubscribe()
        this.rootFolderEntryList.unsubscribe()
        UserManager userManager = getUserManager()
        if (userManager != null) {
            InventoryDB database = userManager.getInventoryManager().getDatabase()
            this.wornAttachments.subscribe(userManager.getWornAttachmentsPool(), SubscriptionSingleKey.Value)
            this.wornWearables.subscribe(userManager.getWornWearablesPool(), SubscriptionSingleKey.Value)
            this.wornOutfitFolder.subscribe(userManager.wornOutfitLink(), SubscriptionSingleKey.Value)
            this.agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID())
            if (uuid == null) {
                uuid = this.myOutfitsFolderUUID
            }
            Debug.Printf("After checking myoutfits: %s", uuid)
            if (uuid == null && (rootFolder = userManager.getInventoryManager().getRootFolder()) != null) {
                SLInventoryEntry findSpecialFolder = database.findSpecialFolder(rootFolder, 48)
                if (findSpecialFolder != null) {
                    this.myOutfitsFolderUUID = findSpecialFolder.uuid
                    uuid = findSpecialFolder.uuid
                    Debug.Printf("Found special folder: %s", uuid)
                } else {
                    Debug.Printf("Special folder not found", Any[0])
                }
            }
            if (uuid != null) {
                this.folderLoading.subscribe(userManager.getInventoryManager().getFolderLoading(), uuid)
                this.entryList.subscribe(userManager.getInventoryManager().getInventoryEntries(), getInventoryQuery(uuid))
                if (!(view == null || this.listHeader == null)) {
                    if (Objects.equal(uuid, this.myOutfitsFolderUUID)) {
                        ((TextView) this.listHeader.findViewById(R.id.itemNameTextView)).setText(R.string.current_outfit)
                        ((ImageView) this.listHeader.findViewById(R.id.itemTypeIconView)).setImageResource(R.drawable.inv_folder)
                        view.findViewById(R.id.wear_buttons_layout).setVisibility(8)
                    } else {
                        ((TextView) this.listHeader.findViewById(R.id.itemNameTextView)).setText(R.string.inventory_go_up)
                        ((ImageView) this.listHeader.findViewById(R.id.itemTypeIconView)).setImageResource(R.drawable.inv_up)
                        view.findViewById(R.id.wear_buttons_layout).setVisibility(0)
                    }
                    this.listHeader.findViewById(R.id.itemSubTypeIconView).setVisibility(8)
                    this.listHeader.setVisibility(0)
                }
            } else {
                this.rootFolderEntryList.subscribe(userManager.getInventoryManager().getInventoryEntries(), InventoryQuery.create((UUID) null, (String) null, true, false, false, (SLAssetType) null))
                if (this.listHeader != null) {
                    this.listHeader.setVisibility(8)
                }
                if (view != null) {
                    view.findViewById(R.id.wear_buttons_layout).setVisibility(8)
                }
            }
            if (this.adapter != null) {
                this.adapter.setDatabase(database)
            }
        } else {
            this.wornAttachments.unsubscribe()
            this.wornWearables.unsubscribe()
            this.rootFolderEntryList.unsubscribe()
            this.adapter.setDatabase((InventoryDB) null)
            this.wornOutfitFolder.unsubscribe()
        }
        updateLoadingStatus()
    }

    private Unit updateLoadingStatus() {
        Context context = getContext()
        if (context != null) {
            Boolean z2 = true
            if (this.folderLoading.isSubscribed()) {
                Boolean data = this.folderLoading.getData()
                z = data != null ? data.booleanValue() : false
            } else {
                z = false
            }
            if (this.adapter != null) {
                z2 = this.adapter.isEmpty()
            }
            this.loadableMonitor.setExtraLoading(z2 ? z : false)
            LoadableMonitor loadableMonitor2 = this.loadableMonitor
            if (z2) {
                z = false
            }
            loadableMonitor2.setButteryProgressBar(z)
            this.loadableMonitor.setEmptyMessage(z2, context.getString(R.string.no_inventory_subentries))
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragment_13544  reason: not valid java name */
    /* synthetic */ Unit m702lambda$com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragment_13544(AdapterView adapterView, View view, Int i, Long j) {
        SLInventoryEntry folder
        UserManager userManager = getUserManager()
        if (userManager != null) {
            Any item = adapterView.getAdapter().getItem(i)
            if (item == this.listHeaderData) {
                UUID folderUUID = getFolderUUID()
                if (folderUUID == null || Objects.equal(folderUUID, this.myOutfitsFolderUUID)) {
                    DetailsActivity.showEmbeddedDetails(getActivity(), CurrentOutfitFragment.class, CurrentOutfitFragment.makeSelection(userManager.getUserID()))
                    return
                }
                InventoryEntryList data = this.entryList.getData()
                if (data != null && (folder = data.getFolder()) != null) {
                    navigateToFolder(folder.parentUUID)
                }
            } else if (item instanceof SLInventoryEntry) {
                SLInventoryEntry sLInventoryEntry = (SLInventoryEntry) item
                Debug.Printf("Inventory: Item click: item isFolder %b invType %d typeDefault %d assetType %d", Boolean.valueOf(sLInventoryEntry.isFolder), Int.valueOf(sLInventoryEntry.invType), Int.valueOf(sLInventoryEntry.typeDefault), Int.valueOf(sLInventoryEntry.assetType))
                UUID uuid = (!sLInventoryEntry.isFolder || sLInventoryEntry.uuid == null) ? (!sLInventoryEntry.isLink() || sLInventoryEntry.invType != 8) ? null : sLInventoryEntry.assetUUID : sLInventoryEntry.uuid
                if (uuid != null) {
                    navigateToFolder(uuid)
                }
            }
        }
    }

    Unit onClick(View view) {
        switch (view.getId()) {
            case R.id.outfit_folder_wear_button:
                changeOutfit(true)
                return
            case R.id.outfit_folder_add_button:
                changeOutfit(false)
                return
            default:
                return
        }
    }

    @Nullable
    View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        Debug.Printf("InventoryFragment: onCreateView", Any[0])
        View inflate = layoutInflater.inflate(R.layout.outfit_folder, viewGroup, false)
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_folder_selected), getString(R.string.inventory_folder_fail))
        this.listHeader = (ViewGroup) layoutInflater.inflate(R.layout.inventory_item, (ListView) inflate.findViewById(R.id.item_list), false)
        this.adapter = InventoryFolderAdapter(layoutInflater, true)
        this.adapter.setOnItemCheckboxClickListener(this)
        ((ListView) inflate.findViewById(R.id.item_list)).addHeaderView(this.listHeader, this.listHeaderData, true)
        ((ListView) inflate.findViewById(R.id.item_list)).setAdapter(this.adapter)
        ((ListView) inflate.findViewById(R.id.item_list)).setOnItemClickListener(this.itemClickListener)
        inflate.findViewById(R.id.outfit_folder_wear_button).setOnClickListener(this)
        inflate.findViewById(R.id.outfit_folder_add_button).setOnClickListener(this)
        return inflate
    }

    @EventHandler
    Unit onInventorySortOrderChanged(InventorySortOrderChangedEvent inventorySortOrderChangedEvent) {
        if (isFragmentStarted()) {
            showInventoryList(getFolderUUID())
        }
    }

    Unit onItemCheckboxClicked(SLInventoryEntry sLInventoryEntry) {
        SLInventoryEntry resolveLink
        UserManager userManager = getUserManager()
        SLAgentCircuit data = this.agentCircuit.getData()
        if (data != null && userManager != null) {
            SLAvatarAppearance sLAvatarAppearance = data.getModules().avatarAppearance
            InventoryDB database = userManager.getInventoryManager().getDatabase()
            if (!(database == null || (resolveLink = database.resolveLink(sLInventoryEntry)) == null)) {
                sLInventoryEntry = resolveLink
            }
            if (sLAvatarAppearance.isItemWorn(sLInventoryEntry)) {
                if (sLInventoryEntry.isWearable()) {
                    sLAvatarAppearance.TakeItemOff(sLInventoryEntry)
                } else {
                    sLAvatarAppearance.DetachInventoryItem(sLInventoryEntry)
                }
            } else if (sLInventoryEntry.isWearable()) {
                sLAvatarAppearance.WearItem(sLInventoryEntry, false)
            } else {
                sLAvatarAppearance.AttachInventoryItem(sLInventoryEntry, 0, false)
            }
        }
    }

    Unit onStart() {
        super.onStart()
        EventBus.getInstance().subscribe((Any) this)
        showInventoryList(getFolderUUID())
    }

    Unit onStop() {
        showInventoryList((UUID) null)
        EventBus.getInstance().unsubscribe(this)
        super.onStop()
    }

    Unit setFragmentArgs(Intent intent, Bundle bundle) {
        Debug.Printf("InventoryFragment: setFragmentArgs '%s'", bundle)
        if (bundle != null) {
            getArguments().putAll(bundle)
        }
        if (isFragmentStarted()) {
            showInventoryList(getFolderUUID())
        }
    }
}

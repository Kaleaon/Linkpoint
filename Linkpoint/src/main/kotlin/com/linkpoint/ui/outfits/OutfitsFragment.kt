package com.linkpoint.ui.outfits

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
import com.linkpoint.Debug
import com.linkpoint.R
import com.linkpoint.eventbus.EventBus
import com.linkpoint.eventbus.EventHandler
import com.linkpoint.orm.InventoryDB
import com.linkpoint.orm.InventoryEntryList
import com.linkpoint.orm.InventoryQuery
import com.linkpoint.react.Subscription
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.assets.SLWearable
import com.linkpoint.slproto.assets.SLWearableType
import com.linkpoint.slproto.inventory.SLAssetType
import com.linkpoint.slproto.inventory.SLInventoryEntry
import com.linkpoint.slproto.modules.SLAvatarAppearance
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.DetailsActivity
import com.linkpoint.ui.common.FragmentWithTitle
import com.linkpoint.ui.common.LoadingLayout
import com.linkpoint.ui.common.ReloadableFragment
import com.linkpoint.ui.common.loadmon.LoadableMonitor
import com.linkpoint.ui.inventory.InventoryFolderAdapter
import com.linkpoint.ui.inventory.InventoryFragmentHelper
import com.linkpoint.ui.inventory.InventorySortOrderChangedEvent
import com.linkpoint.utils.UUIDPool
import java.util.Iterator
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class OutfitsFragment : FragmentWithTitle(), ReloadableFragment, View.OnClickListener, InventoryFolderAdapter.OnItemCheckboxClickListener {
    private const val FOLDER_ID_KEY: String = "folderID"
    private InventoryFolderAdapter adapter = null
    private val SubscriptionData<UUID, SLAgentCircuit> agentCircuit = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f505$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.2.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.2.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    private val SubscriptionData<InventoryQuery, InventoryEntryList> entryList = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f504$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.1.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.1.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    private val SubscriptionData<UUID, Boolean> folderLoading = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f506$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.3.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.3.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    private val AdapterView.OnItemClickListener itemClickListener = $Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs(this)
    private ViewGroup listHeader
    private val Object listHeaderData = Object()
    private val LoadableMonitor loadableMonitor = LoadableMonitor(this.entryList)
    private UUID myOutfitsFolderUUID = null
    private val SubscriptionData<InventoryQuery, InventoryEntryList> rootFolderEntryList = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f509$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.6.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.6.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    private val SubscriptionData<SubscriptionSingleKey, ImmutableMap<UUID, String>> wornAttachments = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f507$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.4.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.4.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    private val SubscriptionData<SubscriptionSingleKey, UUID> wornOutfitFolder = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f510$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.7.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.7.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    private val SubscriptionData<SubscriptionSingleKey, Table<SLWearableType, UUID, SLWearable>> wornWearables = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f508$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.5.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.5.$m$0(java.lang.Object):Unit, class status: UNLOADED
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
                    builder.add((Object) sLInventoryEntry)
                }
            }
            data2.getModules().avatarAppearance.ChangeOutfit(builder.build(), z, data.getFolder())
        }
    }

    private UUID getFolderUUID() {
        String str = null
        Bundle arguments = getArguments()
        if (arguments != null) {
            str = arguments.getString(FOLDER_ID_KEY)
        }
        return UUIDPool.getUUID(str)
    }

    private InventoryQuery getInventoryQuery(UUID uuid) {
        Boolean z = false
        if (InventoryFragmentHelper.getSortOrder(getContext()) == 0) {
            z = true
        }
        return InventoryQuery.create(uuid, (String) null, true, true, z, (SLAssetType) null)
    }

    private UserManager getUserManager() {
        return ActivityUtils.getUserManager(getArguments())
    }

    @JvmStatic
    Bundle makeSelection(UUID uuid, UUID uuid2) {
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
    public Unit m696com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref1(SLAgentCircuit sLAgentCircuit) {
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
    public Unit m695com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref0(InventoryEntryList inventoryEntryList) {
        Debug.Printf("InventoryFragment (%s): onInventoryEntryList: %d entries", this, Integer.valueOf(inventoryEntryList.size()))
        setTitle(inventoryEntryList.getTitle(), (String) null)
        if (this.adapter != null) {
            this.adapter.setData(inventoryEntryList)
        }
        updateLoadingStatus()
    }

    /* access modifiers changed from: private */
    /* renamed from: onLoadingStatusChanged */
    public Unit m697com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref2(Boolean bool) {
        updateLoadingStatus()
    }

    /* access modifiers changed from: private */
    /* renamed from: onRootFolderEntryList */
    public Unit m700com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref5(InventoryEntryList inventoryEntryList) {
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
    public Unit m698com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref3(ImmutableMap<UUID, String> immutableMap) {
        if (this.adapter != null) {
            this.adapter.setWornAttachments(immutableMap)
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onWornOutfitFolder */
    public Unit m701com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref6(UUID uuid) {
        if (this.adapter != null) {
            this.adapter.setWornOutfitFolder(uuid)
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onWornWearablesChanged */
    public Unit m699com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref4(Table<SLWearableType, UUID, SLWearable> table) {
        if (this.adapter != null) {
            this.adapter.setWornWearables(table)
        }
    }

    private Unit showInventoryList(UUID uuid) {
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
                    Debug.Printf("Special folder not found", Object[0])
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
    public /* synthetic */ Unit m702lambda$com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragment_13544(AdapterView adapterView, View view, Int i, Long j) {
        SLInventoryEntry folder
        UserManager userManager = getUserManager()
        if (userManager != null) {
            Object item = adapterView.getAdapter().getItem(i)
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
                Debug.Printf("Inventory: Item click: item isFolder %b invType %d typeDefault %d assetType %d", Boolean.valueOf(sLInventoryEntry.isFolder), Integer.valueOf(sLInventoryEntry.invType), Integer.valueOf(sLInventoryEntry.typeDefault), Integer.valueOf(sLInventoryEntry.assetType))
                UUID uuid = (!sLInventoryEntry.isFolder || sLInventoryEntry.uuid == null) ? (!sLInventoryEntry.isLink() || sLInventoryEntry.invType != 8) ? null : sLInventoryEntry.assetUUID : sLInventoryEntry.uuid
                if (uuid != null) {
                    navigateToFolder(uuid)
                }
            }
        }
    }

    public Unit onClick(View view) {
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

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Debug.Printf("InventoryFragment: onCreateView", Object[0])
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
    public Unit onInventorySortOrderChanged(InventorySortOrderChangedEvent inventorySortOrderChangedEvent) {
        if (isFragmentStarted()) {
            showInventoryList(getFolderUUID())
        }
    }

    public Unit onItemCheckboxClicked(SLInventoryEntry sLInventoryEntry) {
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

    public Unit onStart() {
        super.onStart()
        EventBus.getInstance().subscribe((Object) this)
        showInventoryList(getFolderUUID())
    }

    public Unit onStop() {
        showInventoryList((UUID) null)
        EventBus.getInstance().unsubscribe(this)
        super.onStop()
    }

    public Unit setFragmentArgs(Intent intent, Bundle bundle) {
        Debug.Printf("InventoryFragment: setFragmentArgs '%s'", bundle)
        if (bundle != null) {
            getArguments().putAll(bundle)
        }
        if (isFragmentStarted()) {
            showInventoryList(getFolderUUID())
        }
    }
}

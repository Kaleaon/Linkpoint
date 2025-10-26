package com.linkpoint.ui.inventory

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.common.base.Objects
import com.google.common.base.Strings
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Table
import com.linkpoint.Debug
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
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
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.assets.SLWearable
import com.linkpoint.slproto.assets.SLWearableType
import com.linkpoint.slproto.chat.SLChatInventoryItemOfferedEvent
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.inventory.SLAssetType
import com.linkpoint.slproto.inventory.SLInventoryEntry
import com.linkpoint.slproto.inventory.SLInventoryType
import com.linkpoint.slproto.messages.AvatarPropertiesReply
import com.linkpoint.slproto.messages.PickInfoReply
import com.linkpoint.slproto.modules.SLUserProfiles
import com.linkpoint.slproto.users.manager.InventoryManager
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.DetailsActivity
import com.linkpoint.ui.common.FragmentWithTitle
import com.linkpoint.ui.common.LoadingLayout
import com.linkpoint.ui.common.MasterDetailsActivity
import com.linkpoint.ui.common.ReloadableFragment
import com.linkpoint.ui.common.loadmon.LoadableMonitor
import com.linkpoint.ui.inventory.InventoryActivity
import com.linkpoint.ui.inventory.InventorySaveInfo
import com.linkpoint.utils.UUIDPool
import java.io.IOException
import java.util.Map
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class InventoryFragment : FragmentWithTitle(), ReloadableFragment {

    /* renamed from: -com-lumiyaviewer-lumiya-ui-inventory-InventoryActivity$SelectActionSwitchesValues  reason: not valid java name */
    private const val /* synthetic */ IntArray f446comlumiyaviewerlumiyauiinventoryInventoryActivity$SelectActionSwitchesValues = null
    private const val FOLDER_ID_KEY: String = "folderID"
    private const val IS_MASTER_FRAGMENT: String = "isMasterFragment"
    private const val IS_SEARCHING_KEY: String = "isSearching"
    private const val SEARCH_STRING_KEY: String = "searchString"
    const val SELECTED_INVENTORY_ENTRY: String = "selectedInventoryEntry"
    private const val IntArray folderActionIds = {R.id.inventory_go_up_item, R.id.inventory_create_item, R.id.inventory_folder_create_item, R.id.inventory_folder_create_landmark, R.id.inventory_folder_create_notecard, R.id.inventory_folder_upload_picture, R.id.inventory_folder_delete_item, R.id.inventory_folder_rename_item, R.id.inventory_folder_share_item, R.id.inventory_folder_cut_item, R.id.inventory_folder_copy_item, R.id.inventory_folder_paste_item, R.id.inventory_folder_paste_as_link_item}
    private InventoryFolderAdapter adapter = null
    private val SubscriptionData<UUID, SLAgentCircuit> agentCircuit = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f427$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.8.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.8.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    private val SubscriptionData<SubscriptionSingleKey, InventoryManager.InventoryClipboardEntry> clipboardEntry = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f426$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.7.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.7.$m$0(java.lang.Object):Unit, class status: UNLOADED
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
        private val /* synthetic */ Object f425$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.6.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.6.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    private ImmutableMap<Integer, MenuItem> folderActionMenuItems = ImmutableMap.of()
    private val SubscriptionData<UUID, Boolean> folderLoading = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f428$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.9.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.9.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

    private val InventoryFragmentHelper inventoryFragmentHelper = InventoryFragmentHelper(this)
    private val AdapterView.OnItemClickListener itemClickListener = AdapterView.OnItemClickListener(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f424$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.5.$m$0(android.widget.AdapterView, android.view.View, Int, Long):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.5.$m$0(android.widget.AdapterView, android.view.View, Int, Long):Unit, class status: UNLOADED
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

    }
    private val LoadableMonitor loadableMonitor = LoadableMonitor(this.entryList)
    private val View.OnClickListener saveAsClickListener = View.OnClickListener(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f423$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.4.$m$0(android.view.View):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.4.$m$0(android.view.View):Unit, class status: UNLOADED
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

    }
    private InventorySaveInfo saveInfo = null
    private val SubscriptionData<SubscriptionSingleKey, Boolean> searchRunning = SubscriptionData<>(UIThreadExecutor.getInstance(), Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private val /* synthetic */ Object f405$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.10.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.10.$m$0(java.lang.Object):Unit, class status: UNLOADED
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
        private val /* synthetic */ Object f406$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.11.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.11.$m$0(java.lang.Object):Unit, class status: UNLOADED
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
        private val /* synthetic */ Object f407$f0

        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.12.$m$0(java.lang.Object):Unit, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.12.$m$0(java.lang.Object):Unit, class status: UNLOADED
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


    /* renamed from: -getcom-lumiyaviewer-lumiya-ui-inventory-InventoryActivity$SelectActionSwitchesValues  reason: not valid java name */
    @JvmStatic
private /* synthetic */ IntArray m590getcomlumiyaviewerlumiyauiinventoryInventoryActivity$SelectActionSwitchesValues() {
        if (f446comlumiyaviewerlumiyauiinventoryInventoryActivity$SelectActionSwitchesValues != null) {
            return f446comlumiyaviewerlumiyauiinventoryInventoryActivity$SelectActionSwitchesValues
        }
        IntArray iArr = Int[InventoryActivity.SelectAction.values().length]
        try {
            iArr[InventoryActivity.SelectAction.applyFirstLife.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[InventoryActivity.SelectAction.applyPickImage.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[InventoryActivity.SelectAction.applyUserProfile.ordinal()] = 3
        } catch (NoSuchFieldError e3) {
        }
        f446comlumiyaviewerlumiyauiinventoryInventoryActivity$SelectActionSwitchesValues = iArr
        return iArr
    }

    private Unit applyPickImage(SLInventoryEntry sLInventoryEntry, PickInfoReply pickInfoReply) {
        SLAgentCircuit data = this.agentCircuit.getData()
        if (data != null && pickInfoReply != null) {
            data.getModules().userProfiles.UpdatePickInfo(pickInfoReply.Data_Field.PickID, pickInfoReply.Data_Field.CreatorID, pickInfoReply.Data_Field.ParcelID, SLMessage.stringFromVariableOEM(pickInfoReply.Data_Field.Name), SLMessage.stringFromVariableUTF(pickInfoReply.Data_Field.Desc), sLInventoryEntry.assetUUID, pickInfoReply.Data_Field.PosGlobal, pickInfoReply.Data_Field.SortOrder, pickInfoReply.Data_Field.Enabled)
            FragmentActivity activity = getActivity()
            if (activity != null) {
                activity.finish()
            }
        }
    }

    private Unit applyProfilePic(SLInventoryEntry sLInventoryEntry, Boolean z, AvatarPropertiesReply avatarPropertiesReply) {
        Boolean z2 = true
        SLAgentCircuit data = this.agentCircuit.getData()
        if (!(data == null || avatarPropertiesReply == null)) {
            UUID uuid = avatarPropertiesReply.PropertiesData_Field.ImageID
            UUID uuid2 = avatarPropertiesReply.PropertiesData_Field.FLImageID
            if (z) {
                uuid2 = sLInventoryEntry.assetUUID
            } else {
                uuid = sLInventoryEntry.assetUUID
            }
            SLUserProfiles sLUserProfiles = data.getModules().userProfiles
            String stringFromVariableUTF = SLMessage.stringFromVariableUTF(avatarPropertiesReply.PropertiesData_Field.AboutText)
            String stringFromVariableOEM = SLMessage.stringFromVariableOEM(avatarPropertiesReply.PropertiesData_Field.FLAboutText)
            Boolean z3 = (avatarPropertiesReply.PropertiesData_Field.Flags & 1) != 0
            if ((avatarPropertiesReply.PropertiesData_Field.Flags & 2) == 0) {
                z2 = false
            }
            sLUserProfiles.UpdateAvatarProperties(uuid, uuid2, stringFromVariableUTF, stringFromVariableOEM, z3, z2, SLMessage.stringFromVariableOEM(avatarPropertiesReply.PropertiesData_Field.ProfileURL))
        }
        FragmentActivity activity = getActivity()
        if (activity != null) {
            activity.finish()
        }
    }

    private Boolean folderActionsVisible() {
        if (isMasterFragment()) {
            return !isSplitScreen()
        }
        return true
    }

    private UUID forTransferToUUID() {
        Intent intent
        String stringExtra
        FragmentActivity activity = getActivity()
        if (activity == null || (intent = activity.getIntent()) == null || (stringExtra = intent.getStringExtra("transferToID")) == null) {
            return null
        }
        return UUIDPool.getUUID(stringExtra)
    }

    private SLAssetType getFilterAssetType() {
        Intent intent
        SLAssetType byType
        FragmentActivity activity = getActivity()
        if (activity == null || (intent = activity.getIntent()) == null || (byType = SLAssetType.getByType(intent.getIntExtra("selectActionAssetType", SLAssetType.AT_UNKNOWN.getTypeCode()))) == SLAssetType.AT_UNKNOWN) {
            return null
        }
        return byType
    }

    private InventoryQuery getInventoryQuery() {
        Boolean z = true
        Bundle arguments = getArguments()
        UUID uuid = UUIDPool.getUUID(arguments.getString(FOLDER_ID_KEY))
        String string = arguments.getBoolean(IS_SEARCHING_KEY) ? arguments.getString(SEARCH_STRING_KEY) : null
        if (string != null) {
            string = string.trim()
        }
        String emptyToNull = Strings.emptyToNull(string)
        Int sortOrder = InventoryFragmentHelper.getSortOrder(getContext())
        UUID uuid2 = emptyToNull != null ? null : uuid
        Boolean z2 = !isMasterFragment() ? !isSplitScreen() : true
        Boolean z3 = isMasterFragment() ? !isSplitScreen() : true
        if (sortOrder != 0) {
            z = false
        }
        return InventoryQuery.create(uuid2, emptyToNull, z2, z3, z, getFilterAssetType())
    }

    private InventoryActivity.SelectAction getSelectAction() {
        Intent intent
        String stringExtra
        FragmentActivity activity = getActivity()
        if (activity == null || (intent = activity.getIntent()) == null || (stringExtra = intent.getStringExtra("selectAction")) == null) {
            return null
        }
        try {
            return InventoryActivity.SelectAction.valueOf(stringExtra)
        } catch (IllegalArgumentException e) {
            return null
        }
    }

    private UserManager getUserManager() {
        FragmentActivity activity = getActivity()
        if (activity != null) {
            return ActivityUtils.getUserManager(activity.getIntent())
        }
        return null
    }

    private Boolean isForSelectItem() {
        Intent intent
        FragmentActivity activity = getActivity()
        if (activity == null || (intent = activity.getIntent()) == null) {
            return false
        }
        return intent.getBooleanExtra("forSelectItem", false)
    }

    private Boolean isMasterFragment() {
        Bundle arguments = getArguments()
        if (arguments == null || !arguments.containsKey(IS_MASTER_FRAGMENT)) {
            return false
        }
        return arguments.getBoolean(IS_MASTER_FRAGMENT)
    }

    private Boolean isSplitScreen() {
        FragmentActivity activity = getActivity()
        if (activity instanceof MasterDetailsActivity) {
            return ((MasterDetailsActivity) activity).isSplitScreen()
        }
        return false
    }

    @JvmStatic
    Bundle makeDetailsArguments(Bundle bundle) {
        Bundle bundle2 = Bundle()
        bundle2.putString(FOLDER_ID_KEY, bundle.getString(FOLDER_ID_KEY))
        bundle2.putString(SEARCH_STRING_KEY, bundle.getString(SEARCH_STRING_KEY))
        bundle2.putBoolean(IS_SEARCHING_KEY, bundle.getBoolean(IS_SEARCHING_KEY))
        return bundle2
    }

    @JvmStatic
    Bundle makeSelection(UUID uuid, String str) {
        Bundle bundle = Bundle()
        if (uuid != null) {
            bundle.putString(FOLDER_ID_KEY, uuid.toString())
        }
        bundle.putBoolean(IS_SEARCHING_KEY, str != null)
        bundle.putString(SEARCH_STRING_KEY, str)
        return bundle
    }

    private Unit navigateToFolder(UUID uuid) {
        Bundle arguments = getArguments()
        arguments.putString(FOLDER_ID_KEY, uuid.toString())
        arguments.putBoolean(IS_SEARCHING_KEY, false)
        showInventoryList(getInventoryQuery())
        FragmentActivity activity = getActivity()
        if (activity instanceof InventoryActivity) {
            ((InventoryActivity) activity).clearSearchMode()
        }
    }

    @JvmStatic
    Fragment newInstance(Bundle bundle, Boolean z) {
        InventoryFragment inventoryFragment = InventoryFragment()
        Bundle bundle2 = Bundle()
        if (bundle != null) {
            bundle2.putAll(bundle)
        }
        bundle2.putBoolean(IS_MASTER_FRAGMENT, z)
        inventoryFragment.setArguments(bundle2)
        return inventoryFragment
    }

    /* access modifiers changed from: private */
    /* renamed from: onAgentCircuit */
    fun m597com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentmthref2(SLAgentCircuit sLAgentCircuit) {
        updateFolderActionItems()
    }

    /* access modifiers changed from: private */
    /* renamed from: onClipboardEntry */
    fun m596com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentmthref1(InventoryManager.InventoryClipboardEntry inventoryClipboardEntry) {
        updateFolderActionItems()
    }

    /* access modifiers changed from: private */
    /* renamed from: onInventoryEntryList */
    fun m595com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentmthref0(InventoryEntryList inventoryEntryList) {
        Boolean z = true
        Debug.Printf("InventoryFragment (%s): onInventoryEntryList: %d entries", this, Integer.valueOf(inventoryEntryList.size()))
        if (isForSelectItem()) {
            InventoryActivity.SelectAction selectAction = getSelectAction()
            if (selectAction != null) {
                setTitle(inventoryEntryList.getTitle(), getString(selectAction.subtitleResourceId))
            } else {
                if (forTransferToUUID() == null) {
                    z = false
                }
                setTitle(inventoryEntryList.getTitle(), z ? getString(R.string.select_item_to_share_title) : getString(R.string.select_item_for_attachment_title))
            }
        } else {
            setTitle(inventoryEntryList.getTitle(), (String) null)
        }
        if (this.adapter != null) {
            this.adapter.setData(inventoryEntryList)
        }
        updateLoadingStatus()
        updateFolderActionItems()
    }

    /* access modifiers changed from: private */
    /* renamed from: onLoadingStatusChanged */
    fun m599com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentmthref4(Boolean bool) {
        updateLoadingStatus()
    }

    /* access modifiers changed from: private */
    /* renamed from: onWornAttachmentsChanged */
    fun m600com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentmthref5(ImmutableMap<UUID, String> immutableMap) {
        if (this.adapter != null) {
            this.adapter.setWornAttachments(immutableMap)
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onWornWearablesChanged */
    fun m601com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentmthref6(Table<SLWearableType, UUID, SLWearable> table) {
        if (this.adapter != null) {
            this.adapter.setWornWearables(table)
        }
    }

    private Unit performSelectAction(SLInventoryEntry sLInventoryEntry, InventoryActivity.SelectAction selectAction) {
        Intent intent
        Boolean z = true
        Bundle bundle = null
        FragmentActivity activity = getActivity()
        if (!(activity == null || (intent = activity.getIntent()) == null)) {
            bundle = intent.getBundleExtra("selectActionParams")
            Debug.Printf("InventoryAction: actionParams %s, has params %b", bundle, Boolean.valueOf(intent.hasExtra("selectActionParams")))
        }
        if (bundle == null) {
            bundle = Bundle()
        }
        switch (m590getcomlumiyaviewerlumiyauiinventoryInventoryActivity$SelectActionSwitchesValues()[selectAction.ordinal()]) {
            case 1:
            case 3:
                if (selectAction != InventoryActivity.SelectAction.applyFirstLife) {
                    z = false
                }
                applyProfilePic(sLInventoryEntry, z, (AvatarPropertiesReply) bundle.getParcelable("oldProfileData"))
                return
            case 2:
                applyPickImage(sLInventoryEntry, (PickInfoReply) bundle.getParcelable("oldPickData"))
                return
            default:
                return
        }
    }

    private Unit selectPictureForUpload() {
        Intent intent = Intent()
        intent.setType("image/*")
        intent.setAction("android.intent.action.GET_CONTENT")
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10)
    }

    private Unit shareItem(SLInventoryEntry sLInventoryEntry, UUID uuid) {
        Intent intent
        String str = null
        if ((sLInventoryEntry.baseMask & sLInventoryEntry.ownerMask & 8192) == 0) {
            AlertDialog.Builder(getContext()).setMessage(getString(R.string.item_is_no_transfer)).setCancelable(true).setNegativeButton("Dismiss", DialogInterface.OnClickListener() {
                private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.2.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.2.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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

            }).create().show()
            return
        }
        FragmentActivity activity = getActivity()
        if (!(activity == null || (intent = activity.getIntent()) == null)) {
            str = intent.getStringExtra("transferToName")
        }
        Array<Any> objArr = Object[2]
        objArr[0] = sLInventoryEntry.name
        if (str == null) {
            str = getString(R.string.name_loading_title)
        }
        objArr[1] = str
        String string = getString(R.string.share_inv_item_title, objArr)
        if ((sLInventoryEntry.baseMask & sLInventoryEntry.ownerMask & 32768) == 0) {
            string = string + "\n" + getString(R.string.no_copy_item_transfer_confirm)
        }
        AlertDialog.Builder builder = AlertDialog.Builder(getContext())
        builder.setMessage(string).setCancelable(true).setPositiveButton("Yes", DialogInterface.OnClickListener(this, uuid, sLInventoryEntry, activity) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f414$f0

            /* renamed from: -$f1 */
            private val /* synthetic */ Object f415$f1

            /* renamed from: -$f2 */
            private val /* synthetic */ Object f416$f2

            /* renamed from: -$f3 */
            private val /* synthetic */ Object f417$f3

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.16.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.16.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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

        }).setNegativeButton("No", DialogInterface.OnClickListener() {
            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.3.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.3.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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

        builder.create().show()
    }

    private Unit showInventoryList(InventoryQuery inventoryQuery) {
        Debug.Printf("InventoryFragment (%s): showInventoryList '%s'", this, inventoryQuery)
        this.entryList.unsubscribe()
        this.agentCircuit.unsubscribe()
        this.clipboardEntry.unsubscribe()
        this.folderLoading.unsubscribe()
        UserManager userManager = getUserManager()
        if (inventoryQuery == null || userManager == null) {
            this.searchRunning.unsubscribe()
            this.wornAttachments.unsubscribe()
            this.wornWearables.unsubscribe()
            this.adapter.setDatabase((InventoryDB) null)
        } else {
            this.wornAttachments.subscribe(userManager.getWornAttachmentsPool(), SubscriptionSingleKey.Value)
            this.wornWearables.subscribe(userManager.getWornWearablesPool(), SubscriptionSingleKey.Value)
            this.agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID())
            UUID folderId = inventoryQuery.folderId()
            if (folderId != null) {
                this.folderLoading.subscribe(userManager.getInventoryManager().getFolderLoading(), folderId)
            }
            this.searchRunning.subscribe(userManager.getInventoryManager().getSearchRunning(), SubscriptionSingleKey.Value)
            this.clipboardEntry.subscribe(userManager.getInventoryManager().getClipboard(), SubscriptionSingleKey.Value)
            this.entryList.subscribe(userManager.getInventoryManager().getInventoryEntries(), inventoryQuery)
            if (isMasterFragment() && isSplitScreen()) {
                FragmentActivity activity = getActivity()
                if (activity instanceof MasterDetailsActivity) {
                    ((MasterDetailsActivity) activity).setCurrentDetailsArguments(InventoryFragment.class, makeDetailsArguments(getArguments()))
                }
            }
            if (this.adapter != null) {
                this.adapter.setDatabase(userManager.getInventoryManager().getDatabase())
            }
        }
        updateLoadingStatus()
    }

    private Unit updateFolderActionItems() {
        Boolean z = true
        SLInventoryEntry sLInventoryEntry = null
        if (!this.folderActionMenuItems.isEmpty()) {
            InventoryEntryList data = this.entryList.getData()
            if (data != null) {
                sLInventoryEntry = data.getFolder()
            }
            if (!folderActionsVisible() || sLInventoryEntry == null) {
                for (MenuItem visible : this.folderActionMenuItems.values()) {
                    visible.setVisible(false)
                }
                return
            }
            InventoryManager.InventoryClipboardEntry data2 = this.clipboardEntry.getData()
            Boolean hasData = this.agentCircuit.hasData()
            Boolean equal = sLInventoryEntry.parentUUID != null ? Objects.equal(sLInventoryEntry.parentUUID, UUIDPool.ZeroUUID) : true
            Boolean z2 = sLInventoryEntry.typeDefault >= 0
            Boolean isCopyable = data2 != null ? !data2.isCut ? data2.inventoryEntry.isCopyable() : true : false
            if (data2 == null) {
                z = false
            }
            for (Map.Entry entry : this.folderActionMenuItems.entrySet()) {
                switch (((Integer) entry.getKey()).intValue()) {
                    case R.id.inventory_go_up_item:
                        ((MenuItem) entry.getValue()).setVisible(!equal)
                        break
                    case R.id.inventory_create_item:
                    case R.id.inventory_folder_create_item:
                    case R.id.inventory_folder_create_landmark:
                    case R.id.inventory_folder_create_notecard:
                    case R.id.inventory_folder_upload_picture:
                        ((MenuItem) entry.getValue()).setVisible(hasData)
                        break
                    case R.id.inventory_folder_paste_item:
                        ((MenuItem) entry.getValue()).setVisible(isCopyable ? hasData : false)
                        break
                    case R.id.inventory_folder_paste_as_link_item:
                        ((MenuItem) entry.getValue()).setVisible(z ? hasData : false)
                        break
                    default:
                        ((MenuItem) entry.getValue()).setVisible((equal || !(z2 ^ true)) ? false : hasData)
                        break
                }
            }
        }
    }

    private Unit updateLoadingStatus() {
        Context context = getContext()
        if (context != null) {
            Boolean z3 = getArguments().getBoolean(IS_SEARCHING_KEY)
            if (this.folderLoading.isSubscribed()) {
                Boolean data = this.folderLoading.getData()
                z = data != null ? data.booleanValue() : false
            } else {
                z = false
            }
            if (z3) {
                Boolean data2 = this.searchRunning.getData()
                z2 = (data2 != null ? data2.booleanValue() : false) | z
            } else {
                z2 = z
            }
            Boolean isEmpty = this.adapter != null ? this.adapter.isEmpty() : true
            this.loadableMonitor.setExtraLoading(isEmpty ? z2 : false)
            LoadableMonitor loadableMonitor2 = this.loadableMonitor
            if (isEmpty) {
                z2 = false
            }
            loadableMonitor2.setButteryProgressBar(z2)
            this.loadableMonitor.setEmptyMessage(isEmpty, z3 ? isSplitScreen() ? isMasterFragment() ? context.getString(R.string.no_inventory_folders_found) : context.getString(R.string.no_inventory_items_found) : context.getString(R.string.no_inventory_entries_found) : isSplitScreen() ? isMasterFragment() ? context.getString(R.string.no_inventory_subfolders) : context.getString(R.string.no_inventory_subitems) : context.getString(R.string.no_inventory_subentries))
        }
    }

    private Unit uploadImage(String str, Bitmap bitmap, UUID uuid, UUID uuid2) {
        String trim = Strings.nullToEmpty(str).trim()
        Int indexOf = trim.indexOf(46)
        if (indexOf != -1) {
            trim = trim.substring(0, indexOf).trim()
        }
        StringBuilder sb = StringBuilder()
        Boolean z = false
        for (Int i = 0; i < trim.length(); i++) {
            Char charAt = trim.charAt(i)
            if ("0123456789ABCDEFGHIJKLMNOPQRSTUWXYZabcdefghijklmnopqrstuwxyz ".indexOf(charAt) != -1) {
                sb.append(charAt)
                z = false
            } else {
                if (!z) {
                    sb.append('_')
                }
                z = true
            }
        }
        String trim2 = sb.toString().trim()
        if (trim2.equals("") || trim2.equals("_")) {
            trim2 = "Picture"
        }
        Debug.Printf("Upload: uploading name '%s' bitmap %d x %d", trim2, Integer.valueOf(bitmap.getWidth()), Integer.valueOf(bitmap.getHeight()))
        UploadImageAsyncTask(getContext(), uuid).execute(UploadImageParams[]{UploadImageParams(trim2, bitmap, uuid, uuid2)})
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_23670  reason: not valid java name */
    public /* synthetic */ Unit m602lambda$com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_23670(SLInventoryEntry sLInventoryEntry) {
        if (sLInventoryEntry.parentUUID != null && (!Objects.equal(sLInventoryEntry.parentUUID, UUIDPool.ZeroUUID))) {
            navigateToFolder(sLInventoryEntry.parentUUID)
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_29313  reason: not valid java name */
    public /* synthetic */ Unit m603lambda$com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_29313(String str, Bitmap bitmap, UserManager userManager, UUID uuid, DialogInterface dialogInterface, Int i) {
        dialogInterface.dismiss()
        uploadImage(str, bitmap, userManager.getUserID(), uuid)
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_32146  reason: not valid java name */
    public /* synthetic */ Unit m604lambda$com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_32146(AdapterView adapterView, View view, Int i, Long j) {
        SLInventoryEntry item
        UUID uuid = null
        if (this.adapter != null && (item = this.adapter.getItem(i)) != null) {
            Debug.Printf("Inventory: Item click: item isFolder %b invType %d typeDefault %d assetType %d", Boolean.valueOf(item.isFolder), Integer.valueOf(item.invType), Integer.valueOf(item.typeDefault), Integer.valueOf(item.assetType))
            if (item.isFolder && item.uuid != null) {
                uuid = item.uuid
            } else if (item.isLink() && item.invType == 8) {
                uuid = item.assetUUID
            }
            if (uuid != null) {
                navigateToFolder(uuid)
            } else if (isForSelectItem()) {
                InventoryActivity.SelectAction selectAction = getSelectAction()
                if (selectAction != null) {
                    performSelectAction(item, selectAction)
                    return
                }
                UUID forTransferToUUID = forTransferToUUID()
                if (forTransferToUUID != null) {
                    shareItem(item, forTransferToUUID)
                    return
                }
                FragmentActivity activity = getActivity()
                if (activity != null) {
                    Intent intent = Intent()
                    intent.putExtra(SELECTED_INVENTORY_ENTRY, item)
                    activity.setResult(-1, intent)
                    activity.finish()
                }
            } else {
                FragmentActivity activity2 = getActivity()
                if (activity2 instanceof InventoryActivity) {
                    ((InventoryActivity) activity2).clearSearchMode()
                }
                UserManager userManager = getUserManager()
                if (userManager != null) {
                    DetailsActivity.showEmbeddedDetails(getActivity(), AssetInfoFragment.class, AssetInfoFragment.makeSelection(userManager.getUserID(), item.uuid))
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_39597  reason: not valid java name */
    public /* synthetic */ Unit m605lambda$com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_39597(UUID uuid, SLInventoryEntry sLInventoryEntry, FragmentActivity fragmentActivity, DialogInterface dialogInterface, Int i) {
        dialogInterface.dismiss()
        SLAgentCircuit data = this.agentCircuit.getData()
        if (data != null) {
            data.OfferInventoryItem(uuid, sLInventoryEntry)
            if (fragmentActivity != null) {
                fragmentActivity.finish()
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_40227  reason: not valid java name */
    public /* synthetic */ Unit m606lambda$com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_40227(View view) {
        if (this.saveInfo == null) {
            return
        }
        if (this.saveInfo.saveType == InventorySaveInfo.InventorySaveType.NotecardItem) {
            SLAgentCircuit data = this.agentCircuit.getData()
            UUID uuid = UUIDPool.getUUID(getArguments().getString(FOLDER_ID_KEY))
            if (data != null && uuid != null) {
                data.getModules().inventory.CopyInventoryFromNotecard(this.saveInfo.notecardUUID, this.saveInfo.saveItemUUID, uuid, Runnable(this, ProgressDialog.show(getContext(), (CharSequence) null, getString(R.string.notecard_saving_contents), true, true)) {

                    /* renamed from: -$f0 */
                    private val /* synthetic */ Object f408$f0

                    /* renamed from: -$f1 */
                    private val /* synthetic */ Object f409$f1

                    private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.13.$m$0():Unit, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.13.$m$0():Unit, class status: UNLOADED
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

            }
        } else if (this.saveInfo.saveType == InventorySaveInfo.InventorySaveType.InventoryOffer) {
            SLAgentCircuit data2 = this.agentCircuit.getData()
            UUID uuid2 = UUIDPool.getUUID(getArguments().getString(FOLDER_ID_KEY))
            UserManager userManager = getUserManager()
            if (data2 != null && uuid2 != null && userManager != null) {
                ChatMessage chatMessage = userManager.getChatterList().getActiveChattersManager().getChatMessage(this.saveInfo.inventoryOfferMessageId)
                if (chatMessage != null) {
                    SLChatEvent loadFromDatabaseObject = SLChatEvent.loadFromDatabaseObject(chatMessage, userManager.getUserID())
                    if (loadFromDatabaseObject instanceof SLChatInventoryItemOfferedEvent) {
                        ((SLChatInventoryItemOfferedEvent) loadFromDatabaseObject).onOfferAccepted(getContext(), userManager, uuid2)
                    }
                }
                FragmentActivity activity = getActivity()
                if (activity != null) {
                    activity.finish()
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_40889  reason: not valid java name */
    public /* synthetic */ Unit m607lambda$com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_40889(ProgressDialog progressDialog) {
        UIThreadExecutor.getInstance().execute(Runnable(this, progressDialog) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f410$f0

            /* renamed from: -$f1 */
            private val /* synthetic */ Object f411$f1

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.14.$m$0():Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.14.$m$0():Unit, class status: UNLOADED
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

    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_40936  reason: not valid java name */
    public /* synthetic */ Unit m608lambda$com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_40936(ProgressDialog progressDialog) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss()
            FragmentActivity activity = getActivity()
            if (activity != null) {
                activity.finish()
            }
        }
    }

    fun onActivityCreated(Bundle bundle) {
        Intent intent
        super.onActivityCreated(bundle)
        FragmentActivity activity = getActivity()
        if (!(activity == null || (intent = activity.getIntent()) == null)) {
            this.saveInfo = (InventorySaveInfo) intent.getParcelableExtra("forSaveInfo")
        }
        View view = getView()
        if (view == null) {
            return
        }
        if (this.saveInfo == null || !folderActionsVisible()) {
            view.findViewById(R.id.save_as_layout).setVisibility(8)
            return
        }
        view.findViewById(R.id.save_as_layout).setVisibility(0)
        ((TextView) view.findViewById(R.id.save_as_message)).setText(getString(R.string.inventory_save_format, this.saveInfo.saveItemName))
    }

    fun onActivityResult(Int i, Int i2, Intent intent) {
        Uri data
        SLAgentCircuit sLAgentCircuit = null
        super.onActivityResult(i, i2, intent)
        if (i == 10 && i2 == -1 && intent != null && (data = intent.getData()) != null) {
            try {
                String lastPathSegment = data.getLastPathSegment()
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data)
                UserManager userManager = getUserManager()
                if (userManager != null) {
                    sLAgentCircuit = userManager.getActiveAgentCircuit()
                }
                UUID uuid = UUIDPool.getUUID(getArguments().getString(FOLDER_ID_KEY))
                if (sLAgentCircuit != null) {
                    Int uploadCost = sLAgentCircuit.getModules().financialInfo.getUploadCost()
                    if (uploadCost != 0) {
                        AlertDialog.Builder builder = AlertDialog.Builder(getContext())
                        builder.setMessage(getString(R.string.upload_confirm_question, Integer.valueOf(uploadCost))).setCancelable(true).setPositiveButton("Yes", DialogInterface.OnClickListener(this, lastPathSegment, bitmap, userManager, uuid) {

                            /* renamed from: -$f0 */
                            private val /* synthetic */ Object f418$f0

                            /* renamed from: -$f1 */
                            private val /* synthetic */ Object f419$f1

                            /* renamed from: -$f2 */
                            private val /* synthetic */ Object f420$f2

                            /* renamed from: -$f3 */
                            private val /* synthetic */ Object f421$f3

                            /* renamed from: -$f4 */
                            private val /* synthetic */ Object f422$f4

                            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.17.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
                            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.17.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:311)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:68)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
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

                        }).setNegativeButton("No", $Lambda$MXulZZBv5zNDEqgJzTmU0EFG10())
                        builder.create().show()
                        return
                    }
                    uploadImage(lastPathSegment, bitmap, userManager.getUserID(), uuid)
                }
            } catch (IOException e) {
                Debug.Warning(e)
                AlertDialog.Builder(getContext()).setMessage(getString(R.string.failed_to_open_picture)).setCancelable(true).setNegativeButton("Dismiss", DialogInterface.OnClickListener() {
                    private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.1.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.1.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
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
                    	at jadx.core.codegen.RegionGen.makeCatchBlock(RegionGen.java:363)
                    	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:322)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:68)
                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
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

                }).create().show()
            }
        }
    }

    fun onCreate(Bundle bundle) {
        super.onCreate(bundle)
        setHasOptionsMenu(true)
    }

    fun onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        if (folderActionsVisible()) {
            menuInflater.inflate(R.menu.inventory_fragment_menu, menu)
            ImmutableMap.Builder builder = ImmutableMap.Builder()
            for (Int i : folderActionIds) {
                builder.put(Integer.valueOf(i), menu.findItem(i))
            }
            this.folderActionMenuItems = builder.build()
            updateFolderActionItems()
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Debug.Printf("InventoryFragment: onCreateView", Object[0])
        View inflate = layoutInflater.inflate(R.layout.inventory_folder, viewGroup, false)
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_folder_selected), getString(R.string.inventory_folder_fail))
        this.adapter = InventoryFolderAdapter(layoutInflater, false)
        ((ListView) inflate.findViewById(R.id.item_list)).setAdapter(this.adapter)
        ((ListView) inflate.findViewById(R.id.item_list)).setOnItemClickListener(this.itemClickListener)
        inflate.findViewById(R.id.save_as_button).setOnClickListener(this.saveAsClickListener)
        return inflate
    }

    @EventHandler
    fun onInventorySortOrderChanged(InventorySortOrderChangedEvent inventorySortOrderChangedEvent) {
        if (isFragmentStarted()) {
            showInventoryList(getInventoryQuery())
        }
    }

    public Boolean onOptionsItemSelected(MenuItem menuItem) {
        SLInventoryEntry folder
        UserManager userManager = getUserManager()
        InventoryEntryList data = this.entryList.getData()
        if (!(data == null || userManager == null || (folder = data.getFolder()) == null)) {
            SLAgentCircuit data2 = this.agentCircuit.getData()
            switch (menuItem.getItemId()) {
                case R.id.inventory_go_up_item:
                    if (folder.parentUUID != null && (!Objects.equal(folder.parentUUID, UUIDPool.ZeroUUID))) {
                        Debug.Printf("InventoryFragment: navigate up to %s (current is %s)", folder.parentUUID, folder.uuid)
                        navigateToFolder(folder.parentUUID)
                    }
                    return true
                case R.id.inventory_folder_create_item:
                    this.inventoryFragmentHelper.CreateNewFolder(folder)
                    return true
                case R.id.inventory_folder_create_landmark:
                    this.inventoryFragmentHelper.CreateNewLandmark(folder)
                    return true
                case R.id.inventory_folder_create_notecard:
                    getActivity().startActivity(NotecardEditActivity.createIntent(getContext(), userManager.getUserID(), folder.uuid, (SLInventoryEntry) null, false, (UUID) null, 0))
                    return true
                case R.id.inventory_folder_upload_picture:
                    selectPictureForUpload()
                    return true
                case R.id.inventory_folder_delete_item:
                    this.inventoryFragmentHelper.DeleteInventoryEntry(folder, Runnable(this, folder) {

                        /* renamed from: -$f0 */
                        private val /* synthetic */ Object f412$f0

                        /* renamed from: -$f1 */
                        private val /* synthetic */ Object f413$f1

                        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.15.$m$0():Unit, dex: classes.dex
                        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.15.$m$0():Unit, class status: UNLOADED
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
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeSwitch(RegionGen.java:298)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:64)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
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

                    return true
                case R.id.inventory_folder_rename_item:
                    this.inventoryFragmentHelper.RenameInventoryEntry(folder)
                    return true
                case R.id.inventory_folder_share_item:
                    this.inventoryFragmentHelper.ShareInventoryEntry(folder)
                    return true
                case R.id.inventory_folder_cut_item:
                    userManager.getInventoryManager().copyToClipboard(InventoryManager.InventoryClipboardEntry(true, folder))
                    Toast.makeText(getContext(), R.string.copied_to_clipboard, 1).show()
                    return true
                case R.id.inventory_folder_copy_item:
                    userManager.getInventoryManager().copyToClipboard(InventoryManager.InventoryClipboardEntry(false, folder))
                    Toast.makeText(getContext(), R.string.copied_to_clipboard, 1).show()
                    return true
                case R.id.inventory_folder_paste_item:
                    InventoryManager.InventoryClipboardEntry data3 = this.clipboardEntry.getData()
                    if (!(data3 == null || data2 == null)) {
                        if (data3.isCut) {
                            data2.getModules().inventory.MoveInventoryItem(data3.inventoryEntry, folder)
                            userManager.getInventoryManager().copyToClipboard((InventoryManager.InventoryClipboardEntry) null)
                        } else {
                            data2.getModules().inventory.CopyInventoryItem(data3.inventoryEntry, folder)
                        }
                    }
                    return true
                case R.id.inventory_folder_paste_as_link_item:
                    InventoryManager.InventoryClipboardEntry data4 = this.clipboardEntry.getData()
                    if (!(data4 == null || data2 == null)) {
                        data2.getModules().inventory.LinkInventoryItem(folder, data4.inventoryEntry.uuid, data4.inventoryEntry.isFolder ? SLInventoryType.IT_CATEGORY.getTypeCode() : data4.inventoryEntry.invType, data4.inventoryEntry.isFolder ? SLAssetType.AT_LINK_FOLDER.getTypeCode() : SLAssetType.AT_LINK.getTypeCode(), data4.inventoryEntry.name, data4.inventoryEntry.description)
                    }
                    return true
            }
        }
        return false
    }

    fun onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu)
        updateFolderActionItems()
    }

    fun onStart() {
        super.onStart()
        EventBus.getInstance().subscribe((Object) this)
        showInventoryList(getInventoryQuery())
    }

    fun onStop() {
        showInventoryList((InventoryQuery) null)
        EventBus.getInstance().unsubscribe(this)
        super.onStop()
    }

    fun setFragmentArgs(Intent intent, Bundle bundle) {
        Debug.Printf("InventoryFragment: setFragmentArgs '%s'", bundle)
        if (bundle != null) {
            getArguments().putAll(bundle)
        }
        if (isFragmentStarted()) {
            showInventoryList(getInventoryQuery())
        }
    }

    /* access modifiers changed from: package-private */
    fun setSearchString(String str) {
        Bundle arguments = getArguments()
        arguments.putBoolean(IS_SEARCHING_KEY, str != null)
        arguments.putString(SEARCH_STRING_KEY, str)
        if (isFragmentStarted()) {
            showInventoryList(getInventoryQuery())
        }
    }
}

package com.lumiyaviewer.lumiya.ui.inventory;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import com.lumiyaviewer.lumiya.orm.InventoryEntryList;
import com.lumiyaviewer.lumiya.orm.InventoryQuery;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryType;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.lumiyaviewer.lumiya.slproto.messages.PickInfoReply;
import com.lumiyaviewer.lumiya.slproto.modules.SLUserProfiles;
import com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import com.lumiyaviewer.lumiya.ui.inventory.InventorySaveInfo;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InventoryFragment extends FragmentWithTitle implements ReloadableFragment {

    /* renamed from: -com-lumiyaviewer-lumiya-ui-inventory-InventoryActivity$SelectActionSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f446comlumiyaviewerlumiyauiinventoryInventoryActivity$SelectActionSwitchesValues = null;
    private static final String FOLDER_ID_KEY = "folderID";
    private static final String IS_MASTER_FRAGMENT = "isMasterFragment";
    private static final String IS_SEARCHING_KEY = "isSearching";
    private static final String SEARCH_STRING_KEY = "searchString";
    public static final String SELECTED_INVENTORY_ENTRY = "selectedInventoryEntry";
    private static final int[] folderActionIds = {R.id.inventory_go_up_item, R.id.inventory_create_item, R.id.inventory_folder_create_item, R.id.inventory_folder_create_landmark, R.id.inventory_folder_create_notecard, R.id.inventory_folder_upload_picture, R.id.inventory_folder_delete_item, R.id.inventory_folder_rename_item, R.id.inventory_folder_share_item, R.id.inventory_folder_cut_item, R.id.inventory_folder_copy_item, R.id.inventory_folder_paste_item, R.id.inventory_folder_paste_as_link_item};
    private InventoryFolderAdapter adapter = null;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f427$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.8.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.8.$m$0(java.lang.Object):void, class status: UNLOADED
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

    private final SubscriptionData<SubscriptionSingleKey, InventoryManager.InventoryClipboardEntry> clipboardEntry = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f426$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.7.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.7.$m$0(java.lang.Object):void, class status: UNLOADED
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

    private final SubscriptionData<InventoryQuery, InventoryEntryList> entryList = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f425$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.6.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.6.$m$0(java.lang.Object):void, class status: UNLOADED
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

    @Nonnull
    private ImmutableMap<Integer, MenuItem> folderActionMenuItems = ImmutableMap.of();
    private final SubscriptionData<UUID, Boolean> folderLoading = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f428$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.9.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.9.$m$0(java.lang.Object):void, class status: UNLOADED
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

    private final InventoryFragmentHelper inventoryFragmentHelper = new InventoryFragmentHelper(this);
    private final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f424$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.5.$m$0(android.widget.AdapterView, android.view.View, int, long):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.5.$m$0(android.widget.AdapterView, android.view.View, int, long):void, class status: UNLOADED
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

    };
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.entryList);
    private final View.OnClickListener saveAsClickListener = new View.OnClickListener(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f423$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.4.$m$0(android.view.View):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.4.$m$0(android.view.View):void, class status: UNLOADED
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

    };
    private InventorySaveInfo saveInfo = null;
    private final SubscriptionData<SubscriptionSingleKey, Boolean> searchRunning = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f405$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.10.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.10.$m$0(java.lang.Object):void, class status: UNLOADED
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

    private final SubscriptionData<SubscriptionSingleKey, ImmutableMap<UUID, String>> wornAttachments = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f406$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.11.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.11.$m$0(java.lang.Object):void, class status: UNLOADED
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

    private final SubscriptionData<SubscriptionSingleKey, Table<SLWearableType, UUID, SLWearable>> wornWearables = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f407$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.12.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.12.$m$0(java.lang.Object):void, class status: UNLOADED
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
    private static /* synthetic */ int[] m590getcomlumiyaviewerlumiyauiinventoryInventoryActivity$SelectActionSwitchesValues() {
        if (f446comlumiyaviewerlumiyauiinventoryInventoryActivity$SelectActionSwitchesValues != null) {
            return f446comlumiyaviewerlumiyauiinventoryInventoryActivity$SelectActionSwitchesValues;
        }
        int[] iArr = new int[InventoryActivity.SelectAction.values().length];
        try {
            iArr[InventoryActivity.SelectAction.applyFirstLife.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[InventoryActivity.SelectAction.applyPickImage.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[InventoryActivity.SelectAction.applyUserProfile.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        f446comlumiyaviewerlumiyauiinventoryInventoryActivity$SelectActionSwitchesValues = iArr;
        return iArr;
    }

    private void applyPickImage(SLInventoryEntry sLInventoryEntry, PickInfoReply pickInfoReply) {
        SLAgentCircuit data = this.agentCircuit.getData();
        if (data != null && pickInfoReply != null) {
            data.getModules().userProfiles.UpdatePickInfo(pickInfoReply.Data_Field.PickID, pickInfoReply.Data_Field.CreatorID, pickInfoReply.Data_Field.ParcelID, SLMessage.stringFromVariableOEM(pickInfoReply.Data_Field.Name), SLMessage.stringFromVariableUTF(pickInfoReply.Data_Field.Desc), sLInventoryEntry.assetUUID, pickInfoReply.Data_Field.PosGlobal, pickInfoReply.Data_Field.SortOrder, pickInfoReply.Data_Field.Enabled);
            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.finish();
            }
        }
    }

    private void applyProfilePic(SLInventoryEntry sLInventoryEntry, boolean z, AvatarPropertiesReply avatarPropertiesReply) {
        boolean z2 = true;
        SLAgentCircuit data = this.agentCircuit.getData();
        if (!(data == null || avatarPropertiesReply == null)) {
            UUID uuid = avatarPropertiesReply.PropertiesData_Field.ImageID;
            UUID uuid2 = avatarPropertiesReply.PropertiesData_Field.FLImageID;
            if (z) {
                uuid2 = sLInventoryEntry.assetUUID;
            } else {
                uuid = sLInventoryEntry.assetUUID;
            }
            SLUserProfiles sLUserProfiles = data.getModules().userProfiles;
            String stringFromVariableUTF = SLMessage.stringFromVariableUTF(avatarPropertiesReply.PropertiesData_Field.AboutText);
            String stringFromVariableOEM = SLMessage.stringFromVariableOEM(avatarPropertiesReply.PropertiesData_Field.FLAboutText);
            boolean z3 = (avatarPropertiesReply.PropertiesData_Field.Flags & 1) != 0;
            if ((avatarPropertiesReply.PropertiesData_Field.Flags & 2) == 0) {
                z2 = false;
            }
            sLUserProfiles.UpdateAvatarProperties(uuid, uuid2, stringFromVariableUTF, stringFromVariableOEM, z3, z2, SLMessage.stringFromVariableOEM(avatarPropertiesReply.PropertiesData_Field.ProfileURL));
        }
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    private boolean folderActionsVisible() {
        if (isMasterFragment()) {
            return !isSplitScreen();
        }
        return true;
    }

    @Nullable
    private UUID forTransferToUUID() {
        Intent intent;
        String stringExtra;
        FragmentActivity activity = getActivity();
        if (activity == null || (intent = activity.getIntent()) == null || (stringExtra = intent.getStringExtra("transferToID")) == null) {
            return null;
        }
        return UUIDPool.getUUID(stringExtra);
    }

    @Nullable
    private SLAssetType getFilterAssetType() {
        Intent intent;
        SLAssetType byType;
        FragmentActivity activity = getActivity();
        if (activity == null || (intent = activity.getIntent()) == null || (byType = SLAssetType.getByType(intent.getIntExtra("selectActionAssetType", SLAssetType.AT_UNKNOWN.getTypeCode()))) == SLAssetType.AT_UNKNOWN) {
            return null;
        }
        return byType;
    }

    private InventoryQuery getInventoryQuery() {
        boolean z = true;
        Bundle arguments = getArguments();
        UUID uuid = UUIDPool.getUUID(arguments.getString(FOLDER_ID_KEY));
        String string = arguments.getBoolean(IS_SEARCHING_KEY) ? arguments.getString(SEARCH_STRING_KEY) : null;
        if (string != null) {
            string = string.trim();
        }
        String emptyToNull = Strings.emptyToNull(string);
        int sortOrder = InventoryFragmentHelper.getSortOrder(getContext());
        UUID uuid2 = emptyToNull != null ? null : uuid;
        boolean z2 = !isMasterFragment() ? !isSplitScreen() : true;
        boolean z3 = isMasterFragment() ? !isSplitScreen() : true;
        if (sortOrder != 0) {
            z = false;
        }
        return InventoryQuery.create(uuid2, emptyToNull, z2, z3, z, getFilterAssetType());
    }

    @Nullable
    private InventoryActivity.SelectAction getSelectAction() {
        Intent intent;
        String stringExtra;
        FragmentActivity activity = getActivity();
        if (activity == null || (intent = activity.getIntent()) == null || (stringExtra = intent.getStringExtra("selectAction")) == null) {
            return null;
        }
        try {
            return InventoryActivity.SelectAction.valueOf(stringExtra);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Nullable
    private UserManager getUserManager() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            return ActivityUtils.getUserManager(activity.getIntent());
        }
        return null;
    }

    private boolean isForSelectItem() {
        Intent intent;
        FragmentActivity activity = getActivity();
        if (activity == null || (intent = activity.getIntent()) == null) {
            return false;
        }
        return intent.getBooleanExtra("forSelectItem", false);
    }

    private boolean isMasterFragment() {
        Bundle arguments = getArguments();
        if (arguments == null || !arguments.containsKey(IS_MASTER_FRAGMENT)) {
            return false;
        }
        return arguments.getBoolean(IS_MASTER_FRAGMENT);
    }

    private boolean isSplitScreen() {
        FragmentActivity activity = getActivity();
        if (activity instanceof MasterDetailsActivity) {
            return ((MasterDetailsActivity) activity).isSplitScreen();
        }
        return false;
    }

    public static Bundle makeDetailsArguments(Bundle bundle) {
        Bundle bundle2 = new Bundle();
        bundle2.putString(FOLDER_ID_KEY, bundle.getString(FOLDER_ID_KEY));
        bundle2.putString(SEARCH_STRING_KEY, bundle.getString(SEARCH_STRING_KEY));
        bundle2.putBoolean(IS_SEARCHING_KEY, bundle.getBoolean(IS_SEARCHING_KEY));
        return bundle2;
    }

    public static Bundle makeSelection(@Nullable UUID uuid, @Nullable String str) {
        Bundle bundle = new Bundle();
        if (uuid != null) {
            bundle.putString(FOLDER_ID_KEY, uuid.toString());
        }
        bundle.putBoolean(IS_SEARCHING_KEY, str != null);
        bundle.putString(SEARCH_STRING_KEY, str);
        return bundle;
    }

    private void navigateToFolder(UUID uuid) {
        Bundle arguments = getArguments();
        arguments.putString(FOLDER_ID_KEY, uuid.toString());
        arguments.putBoolean(IS_SEARCHING_KEY, false);
        showInventoryList(getInventoryQuery());
        FragmentActivity activity = getActivity();
        if (activity instanceof InventoryActivity) {
            ((InventoryActivity) activity).clearSearchMode();
        }
    }

    public static Fragment newInstance(Bundle bundle, boolean z) {
        InventoryFragment inventoryFragment = new InventoryFragment();
        Bundle bundle2 = new Bundle();
        if (bundle != null) {
            bundle2.putAll(bundle);
        }
        bundle2.putBoolean(IS_MASTER_FRAGMENT, z);
        inventoryFragment.setArguments(bundle2);
        return inventoryFragment;
    }

    /* access modifiers changed from: private */
    /* renamed from: onAgentCircuit */
    public void m597com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentmthref2(SLAgentCircuit sLAgentCircuit) {
        updateFolderActionItems();
    }

    /* access modifiers changed from: private */
    /* renamed from: onClipboardEntry */
    public void m596com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentmthref1(InventoryManager.InventoryClipboardEntry inventoryClipboardEntry) {
        updateFolderActionItems();
    }

    /* access modifiers changed from: private */
    /* renamed from: onInventoryEntryList */
    public void m595com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentmthref0(InventoryEntryList inventoryEntryList) {
        boolean z = true;
        Debug.Printf("InventoryFragment (%s): onInventoryEntryList: %d entries", this, Integer.valueOf(inventoryEntryList.size()));
        if (isForSelectItem()) {
            InventoryActivity.SelectAction selectAction = getSelectAction();
            if (selectAction != null) {
                setTitle(inventoryEntryList.getTitle(), getString(selectAction.subtitleResourceId));
            } else {
                if (forTransferToUUID() == null) {
                    z = false;
                }
                setTitle(inventoryEntryList.getTitle(), z ? getString(R.string.select_item_to_share_title) : getString(R.string.select_item_for_attachment_title));
            }
        } else {
            setTitle(inventoryEntryList.getTitle(), (String) null);
        }
        if (this.adapter != null) {
            this.adapter.setData(inventoryEntryList);
        }
        updateLoadingStatus();
        updateFolderActionItems();
    }

    /* access modifiers changed from: private */
    /* renamed from: onLoadingStatusChanged */
    public void m599com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentmthref4(Boolean bool) {
        updateLoadingStatus();
    }

    /* access modifiers changed from: private */
    /* renamed from: onWornAttachmentsChanged */
    public void m600com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentmthref5(ImmutableMap<UUID, String> immutableMap) {
        if (this.adapter != null) {
            this.adapter.setWornAttachments(immutableMap);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onWornWearablesChanged */
    public void m601com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentmthref6(Table<SLWearableType, UUID, SLWearable> table) {
        if (this.adapter != null) {
            this.adapter.setWornWearables(table);
        }
    }

    private void performSelectAction(SLInventoryEntry sLInventoryEntry, InventoryActivity.SelectAction selectAction) {
        Intent intent;
        boolean z = true;
        Bundle bundle = null;
        FragmentActivity activity = getActivity();
        if (!(activity == null || (intent = activity.getIntent()) == null)) {
            bundle = intent.getBundleExtra("selectActionParams");
            Debug.Printf("InventoryAction: actionParams %s, has params %b", bundle, Boolean.valueOf(intent.hasExtra("selectActionParams")));
        }
        if (bundle == null) {
            bundle = new Bundle();
        }
        switch (m590getcomlumiyaviewerlumiyauiinventoryInventoryActivity$SelectActionSwitchesValues()[selectAction.ordinal()]) {
            case 1:
            case 3:
                if (selectAction != InventoryActivity.SelectAction.applyFirstLife) {
                    z = false;
                }
                applyProfilePic(sLInventoryEntry, z, (AvatarPropertiesReply) bundle.getParcelable("oldProfileData"));
                return;
            case 2:
                applyPickImage(sLInventoryEntry, (PickInfoReply) bundle.getParcelable("oldPickData"));
                return;
            default:
                return;
        }
    }

    private void selectPictureForUpload() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
    }

    private void shareItem(SLInventoryEntry sLInventoryEntry, UUID uuid) {
        Intent intent;
        String str = null;
        if ((sLInventoryEntry.baseMask & sLInventoryEntry.ownerMask & 8192) == 0) {
            new AlertDialog.Builder(getContext()).setMessage(getString(R.string.item_is_no_transfer)).setCancelable(true).setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.2.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.2.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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

            }).create().show();
            return;
        }
        FragmentActivity activity = getActivity();
        if (!(activity == null || (intent = activity.getIntent()) == null)) {
            str = intent.getStringExtra("transferToName");
        }
        Object[] objArr = new Object[2];
        objArr[0] = sLInventoryEntry.name;
        if (str == null) {
            str = getString(R.string.name_loading_title);
        }
        objArr[1] = str;
        String string = getString(R.string.share_inv_item_title, objArr);
        if ((sLInventoryEntry.baseMask & sLInventoryEntry.ownerMask & 32768) == 0) {
            string = string + "\n" + getString(R.string.no_copy_item_transfer_confirm);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(string).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener(this, uuid, sLInventoryEntry, activity) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f414$f0;

            /* renamed from: -$f1 */
            private final /* synthetic */ Object f415$f1;

            /* renamed from: -$f2 */
            private final /* synthetic */ Object f416$f2;

            /* renamed from: -$f3 */
            private final /* synthetic */ Object f417$f3;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.16.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.16.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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

        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.3.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.3.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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

        builder.create().show();
    }

    private void showInventoryList(@Nullable InventoryQuery inventoryQuery) {
        Debug.Printf("InventoryFragment (%s): showInventoryList '%s'", this, inventoryQuery);
        this.entryList.unsubscribe();
        this.agentCircuit.unsubscribe();
        this.clipboardEntry.unsubscribe();
        this.folderLoading.unsubscribe();
        UserManager userManager = getUserManager();
        if (inventoryQuery == null || userManager == null) {
            this.searchRunning.unsubscribe();
            this.wornAttachments.unsubscribe();
            this.wornWearables.unsubscribe();
            this.adapter.setDatabase((InventoryDB) null);
        } else {
            this.wornAttachments.subscribe(userManager.getWornAttachmentsPool(), SubscriptionSingleKey.Value);
            this.wornWearables.subscribe(userManager.getWornWearablesPool(), SubscriptionSingleKey.Value);
            this.agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID());
            UUID folderId = inventoryQuery.folderId();
            if (folderId != null) {
                this.folderLoading.subscribe(userManager.getInventoryManager().getFolderLoading(), folderId);
            }
            this.searchRunning.subscribe(userManager.getInventoryManager().getSearchRunning(), SubscriptionSingleKey.Value);
            this.clipboardEntry.subscribe(userManager.getInventoryManager().getClipboard(), SubscriptionSingleKey.Value);
            this.entryList.subscribe(userManager.getInventoryManager().getInventoryEntries(), inventoryQuery);
            if (isMasterFragment() && isSplitScreen()) {
                FragmentActivity activity = getActivity();
                if (activity instanceof MasterDetailsActivity) {
                    ((MasterDetailsActivity) activity).setCurrentDetailsArguments(InventoryFragment.class, makeDetailsArguments(getArguments()));
                }
            }
            if (this.adapter != null) {
                this.adapter.setDatabase(userManager.getInventoryManager().getDatabase());
            }
        }
        updateLoadingStatus();
    }

    private void updateFolderActionItems() {
        boolean z = true;
        SLInventoryEntry sLInventoryEntry = null;
        if (!this.folderActionMenuItems.isEmpty()) {
            InventoryEntryList data = this.entryList.getData();
            if (data != null) {
                sLInventoryEntry = data.getFolder();
            }
            if (!folderActionsVisible() || sLInventoryEntry == null) {
                for (MenuItem visible : this.folderActionMenuItems.values()) {
                    visible.setVisible(false);
                }
                return;
            }
            InventoryManager.InventoryClipboardEntry data2 = this.clipboardEntry.getData();
            boolean hasData = this.agentCircuit.hasData();
            boolean equal = sLInventoryEntry.parentUUID != null ? Objects.equal(sLInventoryEntry.parentUUID, UUIDPool.ZeroUUID) : true;
            boolean z2 = sLInventoryEntry.typeDefault >= 0;
            boolean isCopyable = data2 != null ? !data2.isCut ? data2.inventoryEntry.isCopyable() : true : false;
            if (data2 == null) {
                z = false;
            }
            for (Map.Entry entry : this.folderActionMenuItems.entrySet()) {
                switch (((Integer) entry.getKey()).intValue()) {
                    case R.id.inventory_go_up_item:
                        ((MenuItem) entry.getValue()).setVisible(!equal);
                        break;
                    case R.id.inventory_create_item:
                    case R.id.inventory_folder_create_item:
                    case R.id.inventory_folder_create_landmark:
                    case R.id.inventory_folder_create_notecard:
                    case R.id.inventory_folder_upload_picture:
                        ((MenuItem) entry.getValue()).setVisible(hasData);
                        break;
                    case R.id.inventory_folder_paste_item:
                        ((MenuItem) entry.getValue()).setVisible(isCopyable ? hasData : false);
                        break;
                    case R.id.inventory_folder_paste_as_link_item:
                        ((MenuItem) entry.getValue()).setVisible(z ? hasData : false);
                        break;
                    default:
                        ((MenuItem) entry.getValue()).setVisible((equal || !(z2 ^ true)) ? false : hasData);
                        break;
                }
            }
        }
    }

    private void updateLoadingStatus() {
        Context context = getContext();
        if (context != null) {
            boolean z3 = getArguments().getBoolean(IS_SEARCHING_KEY);
            if (this.folderLoading.isSubscribed()) {
                Boolean data = this.folderLoading.getData();
                z = data != null ? data.booleanValue() : false;
            } else {
                z = false;
            }
            if (z3) {
                Boolean data2 = this.searchRunning.getData();
                z2 = (data2 != null ? data2.booleanValue() : false) | z;
            } else {
                z2 = z;
            }
            boolean isEmpty = this.adapter != null ? this.adapter.isEmpty() : true;
            this.loadableMonitor.setExtraLoading(isEmpty ? z2 : false);
            LoadableMonitor loadableMonitor2 = this.loadableMonitor;
            if (isEmpty) {
                z2 = false;
            }
            loadableMonitor2.setButteryProgressBar(z2);
            this.loadableMonitor.setEmptyMessage(isEmpty, z3 ? isSplitScreen() ? isMasterFragment() ? context.getString(R.string.no_inventory_folders_found) : context.getString(R.string.no_inventory_items_found) : context.getString(R.string.no_inventory_entries_found) : isSplitScreen() ? isMasterFragment() ? context.getString(R.string.no_inventory_subfolders) : context.getString(R.string.no_inventory_subitems) : context.getString(R.string.no_inventory_subentries));
        }
    }

    private void uploadImage(String str, Bitmap bitmap, UUID uuid, UUID uuid2) {
        String trim = Strings.nullToEmpty(str).trim();
        int indexOf = trim.indexOf(46);
        if (indexOf != -1) {
            trim = trim.substring(0, indexOf).trim();
        }
        StringBuilder sb = new StringBuilder();
        boolean z = false;
        for (int i = 0; i < trim.length(); i++) {
            char charAt = trim.charAt(i);
            if ("0123456789ABCDEFGHIJKLMNOPQRSTUWXYZabcdefghijklmnopqrstuwxyz ".indexOf(charAt) != -1) {
                sb.append(charAt);
                z = false;
            } else {
                if (!z) {
                    sb.append('_');
                }
                z = true;
            }
        }
        String trim2 = sb.toString().trim();
        if (trim2.equals("") || trim2.equals("_")) {
            trim2 = "Picture";
        }
        Debug.Printf("Upload: uploading name '%s' bitmap %d x %d", trim2, Integer.valueOf(bitmap.getWidth()), Integer.valueOf(bitmap.getHeight()));
        new UploadImageAsyncTask(getContext(), uuid).execute(new UploadImageParams[]{new UploadImageParams(trim2, bitmap, uuid, uuid2)});
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_23670  reason: not valid java name */
    public /* synthetic */ void m602lambda$com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_23670(SLInventoryEntry sLInventoryEntry) {
        if (sLInventoryEntry.parentUUID != null && (!Objects.equal(sLInventoryEntry.parentUUID, UUIDPool.ZeroUUID))) {
            navigateToFolder(sLInventoryEntry.parentUUID);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_29313  reason: not valid java name */
    public /* synthetic */ void m603lambda$com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_29313(String str, Bitmap bitmap, UserManager userManager, UUID uuid, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        uploadImage(str, bitmap, userManager.getUserID(), uuid);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_32146  reason: not valid java name */
    public /* synthetic */ void m604lambda$com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_32146(AdapterView adapterView, View view, int i, long j) {
        SLInventoryEntry item;
        UUID uuid = null;
        if (this.adapter != null && (item = this.adapter.getItem(i)) != null) {
            Debug.Printf("Inventory: Item click: item isFolder %b invType %d typeDefault %d assetType %d", Boolean.valueOf(item.isFolder), Integer.valueOf(item.invType), Integer.valueOf(item.typeDefault), Integer.valueOf(item.assetType));
            if (item.isFolder && item.uuid != null) {
                uuid = item.uuid;
            } else if (item.isLink() && item.invType == 8) {
                uuid = item.assetUUID;
            }
            if (uuid != null) {
                navigateToFolder(uuid);
            } else if (isForSelectItem()) {
                InventoryActivity.SelectAction selectAction = getSelectAction();
                if (selectAction != null) {
                    performSelectAction(item, selectAction);
                    return;
                }
                UUID forTransferToUUID = forTransferToUUID();
                if (forTransferToUUID != null) {
                    shareItem(item, forTransferToUUID);
                    return;
                }
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    Intent intent = new Intent();
                    intent.putExtra(SELECTED_INVENTORY_ENTRY, item);
                    activity.setResult(-1, intent);
                    activity.finish();
                }
            } else {
                FragmentActivity activity2 = getActivity();
                if (activity2 instanceof InventoryActivity) {
                    ((InventoryActivity) activity2).clearSearchMode();
                }
                UserManager userManager = getUserManager();
                if (userManager != null) {
                    DetailsActivity.showEmbeddedDetails(getActivity(), AssetInfoFragment.class, AssetInfoFragment.makeSelection(userManager.getUserID(), item.uuid));
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_39597  reason: not valid java name */
    public /* synthetic */ void m605lambda$com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_39597(UUID uuid, SLInventoryEntry sLInventoryEntry, FragmentActivity fragmentActivity, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        SLAgentCircuit data = this.agentCircuit.getData();
        if (data != null) {
            data.OfferInventoryItem(uuid, sLInventoryEntry);
            if (fragmentActivity != null) {
                fragmentActivity.finish();
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_40227  reason: not valid java name */
    public /* synthetic */ void m606lambda$com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_40227(View view) {
        if (this.saveInfo == null) {
            return;
        }
        if (this.saveInfo.saveType == InventorySaveInfo.InventorySaveType.NotecardItem) {
            SLAgentCircuit data = this.agentCircuit.getData();
            UUID uuid = UUIDPool.getUUID(getArguments().getString(FOLDER_ID_KEY));
            if (data != null && uuid != null) {
                data.getModules().inventory.CopyInventoryFromNotecard(this.saveInfo.notecardUUID, this.saveInfo.saveItemUUID, uuid, new Runnable(this, ProgressDialog.show(getContext(), (CharSequence) null, getString(R.string.notecard_saving_contents), true, true)) {

                    /* renamed from: -$f0 */
                    private final /* synthetic */ Object f408$f0;

                    /* renamed from: -$f1 */
                    private final /* synthetic */ Object f409$f1;

                    private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.13.$m$0():void, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.13.$m$0():void, class status: UNLOADED
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
            SLAgentCircuit data2 = this.agentCircuit.getData();
            UUID uuid2 = UUIDPool.getUUID(getArguments().getString(FOLDER_ID_KEY));
            UserManager userManager = getUserManager();
            if (data2 != null && uuid2 != null && userManager != null) {
                ChatMessage chatMessage = userManager.getChatterList().getActiveChattersManager().getChatMessage(this.saveInfo.inventoryOfferMessageId);
                if (chatMessage != null) {
                    SLChatEvent loadFromDatabaseObject = SLChatEvent.loadFromDatabaseObject(chatMessage, userManager.getUserID());
                    if (loadFromDatabaseObject instanceof SLChatInventoryItemOfferedEvent) {
                        ((SLChatInventoryItemOfferedEvent) loadFromDatabaseObject).onOfferAccepted(getContext(), userManager, uuid2);
                    }
                }
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    activity.finish();
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_40889  reason: not valid java name */
    public /* synthetic */ void m607lambda$com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_40889(ProgressDialog progressDialog) {
        UIThreadExecutor.getInstance().execute(new Runnable(this, progressDialog) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f410$f0;

            /* renamed from: -$f1 */
            private final /* synthetic */ Object f411$f1;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.14.$m$0():void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.14.$m$0():void, class status: UNLOADED
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
    public /* synthetic */ void m608lambda$com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_40936(ProgressDialog progressDialog) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.finish();
            }
        }
    }

    public void onActivityCreated(@Nullable Bundle bundle) {
        Intent intent;
        super.onActivityCreated(bundle);
        FragmentActivity activity = getActivity();
        if (!(activity == null || (intent = activity.getIntent()) == null)) {
            this.saveInfo = (InventorySaveInfo) intent.getParcelableExtra("forSaveInfo");
        }
        View view = getView();
        if (view == null) {
            return;
        }
        if (this.saveInfo == null || !folderActionsVisible()) {
            view.findViewById(R.id.save_as_layout).setVisibility(8);
            return;
        }
        view.findViewById(R.id.save_as_layout).setVisibility(0);
        ((TextView) view.findViewById(R.id.save_as_message)).setText(getString(R.string.inventory_save_format, this.saveInfo.saveItemName));
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        Uri data;
        SLAgentCircuit sLAgentCircuit = null;
        super.onActivityResult(i, i2, intent);
        if (i == 10 && i2 == -1 && intent != null && (data = intent.getData()) != null) {
            try {
                String lastPathSegment = data.getLastPathSegment();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data);
                UserManager userManager = getUserManager();
                if (userManager != null) {
                    sLAgentCircuit = userManager.getActiveAgentCircuit();
                }
                UUID uuid = UUIDPool.getUUID(getArguments().getString(FOLDER_ID_KEY));
                if (sLAgentCircuit != null) {
                    int uploadCost = sLAgentCircuit.getModules().financialInfo.getUploadCost();
                    if (uploadCost != 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(getString(R.string.upload_confirm_question, Integer.valueOf(uploadCost))).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener(this, lastPathSegment, bitmap, userManager, uuid) {

                            /* renamed from: -$f0 */
                            private final /* synthetic */ Object f418$f0;

                            /* renamed from: -$f1 */
                            private final /* synthetic */ Object f419$f1;

                            /* renamed from: -$f2 */
                            private final /* synthetic */ Object f420$f2;

                            /* renamed from: -$f3 */
                            private final /* synthetic */ Object f421$f3;

                            /* renamed from: -$f4 */
                            private final /* synthetic */ Object f422$f4;

                            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.17.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
                            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.17.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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

                        }).setNegativeButton("No", new $Lambda$MXulZZBv5zNDEqgJzTmU0EFG10());
                        builder.create().show();
                        return;
                    }
                    uploadImage(lastPathSegment, bitmap, userManager.getUserID(), uuid);
                }
            } catch (IOException e) {
                Debug.Warning(e);
                new AlertDialog.Builder(getContext()).setMessage(getString(R.string.failed_to_open_picture)).setCancelable(true).setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.1.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.1.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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

                }).create().show();
            }
        }
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        if (folderActionsVisible()) {
            menuInflater.inflate(R.menu.inventory_fragment_menu, menu);
            ImmutableMap.Builder builder = new ImmutableMap.Builder();
            for (int i : folderActionIds) {
                builder.put(Integer.valueOf(i), menu.findItem(i));
            }
            this.folderActionMenuItems = builder.build();
            updateFolderActionItems();
        }
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        Debug.Printf("InventoryFragment: onCreateView", new Object[0]);
        View inflate = layoutInflater.inflate(R.layout.inventory_folder, viewGroup, false);
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_folder_selected), getString(R.string.inventory_folder_fail));
        this.adapter = new InventoryFolderAdapter(layoutInflater, false);
        ((ListView) inflate.findViewById(R.id.item_list)).setAdapter(this.adapter);
        ((ListView) inflate.findViewById(R.id.item_list)).setOnItemClickListener(this.itemClickListener);
        inflate.findViewById(R.id.save_as_button).setOnClickListener(this.saveAsClickListener);
        return inflate;
    }

    @EventHandler
    public void onInventorySortOrderChanged(InventorySortOrderChangedEvent inventorySortOrderChangedEvent) {
        if (isFragmentStarted()) {
            showInventoryList(getInventoryQuery());
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        SLInventoryEntry folder;
        UserManager userManager = getUserManager();
        InventoryEntryList data = this.entryList.getData();
        if (!(data == null || userManager == null || (folder = data.getFolder()) == null)) {
            SLAgentCircuit data2 = this.agentCircuit.getData();
            switch (menuItem.getItemId()) {
                case R.id.inventory_go_up_item:
                    if (folder.parentUUID != null && (!Objects.equal(folder.parentUUID, UUIDPool.ZeroUUID))) {
                        Debug.Printf("InventoryFragment: navigate up to %s (current is %s)", folder.parentUUID, folder.uuid);
                        navigateToFolder(folder.parentUUID);
                    }
                    return true;
                case R.id.inventory_folder_create_item:
                    this.inventoryFragmentHelper.CreateNewFolder(folder);
                    return true;
                case R.id.inventory_folder_create_landmark:
                    this.inventoryFragmentHelper.CreateNewLandmark(folder);
                    return true;
                case R.id.inventory_folder_create_notecard:
                    getActivity().startActivity(NotecardEditActivity.createIntent(getContext(), userManager.getUserID(), folder.uuid, (SLInventoryEntry) null, false, (UUID) null, 0));
                    return true;
                case R.id.inventory_folder_upload_picture:
                    selectPictureForUpload();
                    return true;
                case R.id.inventory_folder_delete_item:
                    this.inventoryFragmentHelper.DeleteInventoryEntry(folder, new Runnable(this, folder) {

                        /* renamed from: -$f0 */
                        private final /* synthetic */ Object f412$f0;

                        /* renamed from: -$f1 */
                        private final /* synthetic */ Object f413$f1;

                        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.15.$m$0():void, dex: classes.dex
                        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$MXulZZBv5zNDEqgJzTmU0EFG-10.15.$m$0():void, class status: UNLOADED
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

                    return true;
                case R.id.inventory_folder_rename_item:
                    this.inventoryFragmentHelper.RenameInventoryEntry(folder);
                    return true;
                case R.id.inventory_folder_share_item:
                    this.inventoryFragmentHelper.ShareInventoryEntry(folder);
                    return true;
                case R.id.inventory_folder_cut_item:
                    userManager.getInventoryManager().copyToClipboard(new InventoryManager.InventoryClipboardEntry(true, folder));
                    Toast.makeText(getContext(), R.string.copied_to_clipboard, 1).show();
                    return true;
                case R.id.inventory_folder_copy_item:
                    userManager.getInventoryManager().copyToClipboard(new InventoryManager.InventoryClipboardEntry(false, folder));
                    Toast.makeText(getContext(), R.string.copied_to_clipboard, 1).show();
                    return true;
                case R.id.inventory_folder_paste_item:
                    InventoryManager.InventoryClipboardEntry data3 = this.clipboardEntry.getData();
                    if (!(data3 == null || data2 == null)) {
                        if (data3.isCut) {
                            data2.getModules().inventory.MoveInventoryItem(data3.inventoryEntry, folder);
                            userManager.getInventoryManager().copyToClipboard((InventoryManager.InventoryClipboardEntry) null);
                        } else {
                            data2.getModules().inventory.CopyInventoryItem(data3.inventoryEntry, folder);
                        }
                    }
                    return true;
                case R.id.inventory_folder_paste_as_link_item:
                    InventoryManager.InventoryClipboardEntry data4 = this.clipboardEntry.getData();
                    if (!(data4 == null || data2 == null)) {
                        data2.getModules().inventory.LinkInventoryItem(folder, data4.inventoryEntry.uuid, data4.inventoryEntry.isFolder ? SLInventoryType.IT_CATEGORY.getTypeCode() : data4.inventoryEntry.invType, data4.inventoryEntry.isFolder ? SLAssetType.AT_LINK_FOLDER.getTypeCode() : SLAssetType.AT_LINK.getTypeCode(), data4.inventoryEntry.name, data4.inventoryEntry.description);
                    }
                    return true;
            }
        }
        return false;
    }

    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        updateFolderActionItems();
    }

    public void onStart() {
        super.onStart();
        EventBus.getInstance().subscribe((Object) this);
        showInventoryList(getInventoryQuery());
    }

    public void onStop() {
        showInventoryList((InventoryQuery) null);
        EventBus.getInstance().unsubscribe(this);
        super.onStop();
    }

    public void setFragmentArgs(Intent intent, Bundle bundle) {
        Debug.Printf("InventoryFragment: setFragmentArgs '%s'", bundle);
        if (bundle != null) {
            getArguments().putAll(bundle);
        }
        if (isFragmentStarted()) {
            showInventoryList(getInventoryQuery());
        }
    }

    /* access modifiers changed from: package-private */
    public void setSearchString(String str) {
        Bundle arguments = getArguments();
        arguments.putBoolean(IS_SEARCHING_KEY, str != null);
        arguments.putString(SEARCH_STRING_KEY, str);
        if (isFragmentStarted()) {
            showInventoryList(getInventoryQuery());
        }
    }
}

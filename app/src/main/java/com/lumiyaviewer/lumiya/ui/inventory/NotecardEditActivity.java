package com.lumiyaviewer.lumiya.ui.inventory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.TextKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.assets.SLNotecard;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryType;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetData;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.TeleportProgressDialog;
import com.lumiyaviewer.lumiya.ui.common.ThemedActivity;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.ui.inventory.InventorySaveInfo;
import com.lumiyaviewer.lumiya.utils.SimpleStringParser;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NotecardEditActivity extends ThemedActivity implements SLNotecard.OnAttachmentClickListener, View.OnClickListener {
    private static final String INVENTORY_ENTRY_KEY = "inventoryEntry";
    private static final String IS_SCRIPT_KEY = "isScript";
    private static final int ITEM_FOR_ATTACHMENT_REQUEST = 1;
    private static final String PARENT_FOLDER_KEY = "parentFolderUUID";
    private static final String TASK_LOCAL_ID_KEY = "taskLocalID";
    private static final String TASK_UUID_KEY = "taskUUID";
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f437$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk.1.$m$0(java.lang.Object):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk.1.$m$0(java.lang.Object):void, class status: UNLOADED
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

    private boolean editMode = false;
    private boolean isEditingScript = false;
    private boolean isSaving = false;
    private String lastErrorMessage = null;
    private MenuItem menuItemNewAttachment;
    private SLInventoryEntry noteEntry = null;
    private SLNotecard notecard = null;
    private final SubscriptionData<AssetKey, AssetData> notecardAssetSubscription = new SubscriptionData<>(UIThreadExecutor.getInstance(), new $Lambda$srzsajEQjSwYc3yok0XsNFeAjNk(this));
    private String notecardDescription;
    private String notecardTitle;
    private UUID parentFolderUUID = null;
    private int taskLocalID = 0;
    private UUID taskUUID = null;
    private UserManager userManager;

    private void copyAttachmentToInventory(SLInventoryEntry sLInventoryEntry) {
        if (this.userManager != null && this.noteEntry != null && sLInventoryEntry != null) {
            startActivity(InventoryActivity.makeSaveItemIntent(this, this.userManager.getUserID(), new InventorySaveInfo(InventorySaveInfo.InventorySaveType.NotecardItem, sLInventoryEntry.uuid, sLInventoryEntry.name, this.noteEntry.uuid, SLAssetType.getByType(this.noteEntry.assetType), 0)));
        }
    }

    public static Intent createIntent(Context context, @Nonnull UUID uuid, @Nullable UUID uuid2, @Nullable SLInventoryEntry sLInventoryEntry, boolean z, @Nullable UUID uuid3, int i) {
        Intent intent = new Intent(context, NotecardEditActivity.class);
        intent.putExtra("activeAgentUUID", uuid.toString());
        if (uuid2 != null) {
            intent.putExtra(PARENT_FOLDER_KEY, uuid2.toString());
        }
        if (sLInventoryEntry != null) {
            intent.putExtra(INVENTORY_ENTRY_KEY, sLInventoryEntry);
        }
        if (uuid3 != null) {
            intent.putExtra(TASK_UUID_KEY, uuid3.toString());
        }
        intent.putExtra(TASK_LOCAL_ID_KEY, i);
        intent.putExtra(IS_SCRIPT_KEY, z);
        return intent;
    }

    private void createNewAttachment() {
        if (this.editMode && this.userManager != null) {
            startActivityForResult(InventoryActivity.makeSelectIntent(this, this.userManager.getUserID()), 1);
        }
    }

    private void discardChanges() {
        if (this.editMode && this.notecard != null) {
            this.editMode = false;
            ((EditText) findViewById(R.id.notecardEditTitle)).clearComposingText();
            ((EditText) findViewById(R.id.notecardEditTitle)).setText("");
            ((EditText) findViewById(R.id.notecardEditDescription)).clearComposingText();
            ((EditText) findViewById(R.id.notecardEditDescription)).setText("");
            ((EditText) findViewById(R.id.notecardEditContents)).clearComposingText();
            ((EditText) findViewById(R.id.notecardEditContents)).setText("");
            updateButtonsForMode();
            ((EditText) findViewById(R.id.notecardEditContents)).setText(this.notecard.toSpannableString(false, this));
            ((EditText) findViewById(R.id.notecardEditTitle)).setText(this.notecardTitle);
            ((EditText) findViewById(R.id.notecardEditDescription)).setText(this.notecardDescription);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onAgentCircuit */
    public void m622com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivitymthref1(SLAgentCircuit sLAgentCircuit) {
        if (sLAgentCircuit != null && !sLAgentCircuit.getModules().rlvController.canViewNotecard()) {
            finish();
        }
        updateButtonsForMode();
    }

    /* access modifiers changed from: private */
    /* renamed from: onNotecardLoaded */
    public void m621com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivitymthref0(AssetData assetData) {
        if (assetData.getStatus() == 1) {
            try {
                this.notecard = new SLNotecard(assetData.getData(), this.isEditingScript);
                ((EditText) findViewById(R.id.notecardEditContents)).setText(this.notecard.toSpannableString(false, this));
                ((EditText) findViewById(R.id.notecardEditContents)).setMovementMethod(LinkMovementMethod.getInstance());
                updateButtonsForMode();
            } catch (SimpleStringParser.StringParsingException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveChanges() {
        boolean z = true;
        byte[] bArr = null;
        if (this.editMode && this.notecard != null) {
            String editable = ((EditText) findViewById(R.id.notecardEditTitle)).getText().toString();
            String editable2 = ((EditText) findViewById(R.id.notecardEditDescription)).getText().toString();
            if (this.noteEntry != null) {
                z = !(Objects.equal(editable, this.noteEntry.name) ? Objects.equal(editable2, this.noteEntry.description) : false);
            }
            SLNotecard sLNotecard = new SLNotecard((Spanned) ((EditText) findViewById(R.id.notecardEditContents)).getText(), this.isEditingScript);
            byte[] lindenText = sLNotecard.toLindenText();
            if (!Arrays.equals(lindenText, this.notecard.toLindenText()) || this.noteEntry == null || this.noteEntry.getId() == 0) {
                this.notecard = sLNotecard;
                bArr = lindenText;
            }
            if (bArr != null || this.noteEntry == null || z) {
                this.notecardTitle = editable;
                this.notecardDescription = editable2;
                try {
                    SLInventory sLInventory = this.agentCircuit.get().getModules().inventory;
                    this.isSaving = true;
                    updateButtonsForMode();
                    sLInventory.UpdateNotecard(this.noteEntry, this.parentFolderUUID, this.isEditingScript, editable, editable2, bArr, this.taskUUID, this.taskLocalID, new SLInventory.OnNotecardUpdatedListener(this) {

                        /* renamed from: -$f0 */
                        private final /* synthetic */ Object f438$f0;

                        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk.2.$m$0(com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry, java.lang.String):void, dex: classes.dex
                        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk.2.$m$0(com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry, java.lang.String):void, class status: UNLOADED
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
                        	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:311)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:68)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
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

                } catch (SubscriptionData.DataNotReadyException e) {
                    Debug.Warning(e);
                }
            }
            discardChanges();
        }
    }

    private void startEditing() {
        if (!this.editMode && this.notecard != null) {
            this.editMode = true;
            ((EditText) findViewById(R.id.notecardEditTitle)).clearComposingText();
            ((EditText) findViewById(R.id.notecardEditTitle)).setText("");
            ((EditText) findViewById(R.id.notecardEditDescription)).clearComposingText();
            ((EditText) findViewById(R.id.notecardEditDescription)).setText("");
            ((EditText) findViewById(R.id.notecardEditContents)).clearComposingText();
            ((EditText) findViewById(R.id.notecardEditContents)).setText("");
            updateButtonsForMode();
            ((EditText) findViewById(R.id.notecardEditTitle)).setText(this.notecardTitle);
            ((EditText) findViewById(R.id.notecardEditDescription)).setText(this.notecardDescription);
            ((EditText) findViewById(R.id.notecardEditContents)).setText(this.notecard.toSpannableString(true, this));
        }
    }

    private void updateButtonsForMode() {
        boolean z = true;
        int i2 = 8;
        TextKeyListener textKeyListener = null;
        boolean z2 = this.notecardAssetSubscription.getLoadableStatus() == Loadable.Status.Loading;
        if (this.noteEntry != null && (this.noteEntry.ownerMask & 16384) == 0) {
            z = false;
        }
        if (this.menuItemNewAttachment != null) {
            this.menuItemNewAttachment.setVisible(this.editMode);
        }
        if (!z || !(!z2) || !(!this.isSaving)) {
            findViewById(R.id.notecardSaveButton).setVisibility(8);
            findViewById(R.id.notecardDiscardButton).setVisibility(8);
            findViewById(R.id.notecardEditButton).setVisibility(4);
            i = z2 ? R.string.notecard_loading_contents : this.isSaving ? R.string.notecard_saving_contents : this.noteEntry != null ? R.string.notecard_read_only : -1;
        } else {
            findViewById(R.id.notecardSaveButton).setVisibility(this.editMode ? 0 : 8);
            findViewById(R.id.notecardDiscardButton).setVisibility(this.editMode ? 0 : 8);
            findViewById(R.id.notecardEditButton).setVisibility(!this.editMode ? 0 : 8);
            i = -1;
        }
        if (i != -1) {
            ((TextView) findViewById(R.id.notecardProgressText)).setText(i);
        } else {
            ((TextView) findViewById(R.id.notecardProgressText)).setText((CharSequence) null);
        }
        if (this.lastErrorMessage != null) {
            findViewById(R.id.notecard_error_layout).setVisibility(0);
            ((TextView) findViewById(R.id.notecardErrorMessage)).setText(this.lastErrorMessage);
        } else {
            findViewById(R.id.notecard_error_layout).setVisibility(8);
        }
        View findViewById = findViewById(R.id.notecardProgressIndicator);
        if (z2 || this.isSaving) {
            i2 = 0;
        }
        findViewById.setVisibility(i2);
        ((EditText) findViewById(R.id.notecardEditContents)).setKeyListener(this.editMode ? TextKeyListener.getInstance() : null);
        ((EditText) findViewById(R.id.notecardEditTitle)).setKeyListener(this.editMode ? TextKeyListener.getInstance() : null);
        EditText editText = (EditText) findViewById(R.id.notecardEditDescription);
        if (this.editMode) {
            textKeyListener = TextKeyListener.getInstance();
        }
        editText.setKeyListener(textKeyListener);
        ((EditText) findViewById(R.id.notecardEditContents)).setMovementMethod(this.editMode ? ArrowKeyMovementMethod.getInstance() : LinkMovementMethod.getInstance());
        ((EditText) findViewById(R.id.notecardEditTitle)).setMovementMethod(this.editMode ? ArrowKeyMovementMethod.getInstance() : LinkMovementMethod.getInstance());
        ((EditText) findViewById(R.id.notecardEditDescription)).setMovementMethod(this.editMode ? ArrowKeyMovementMethod.getInstance() : LinkMovementMethod.getInstance());
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity_14261  reason: not valid java name */
    public /* synthetic */ void m623lambda$com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity_14261(SLInventoryEntry sLInventoryEntry, DialogInterface dialogInterface, int i) {
        switch (i) {
            case 0:
                if (this.userManager != null) {
                    TeleportProgressDialog.TeleportToLandmark(this, this.userManager, sLInventoryEntry.assetUUID, false);
                    return;
                }
                return;
            case 1:
                copyAttachmentToInventory(sLInventoryEntry);
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity_15042  reason: not valid java name */
    public /* synthetic */ void m624lambda$com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity_15042(SLInventoryEntry sLInventoryEntry, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            copyAttachmentToInventory(sLInventoryEntry);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity_19315  reason: not valid java name */
    public /* synthetic */ void m625lambda$com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity_19315(SLInventoryEntry sLInventoryEntry, String str) {
        UIThreadExecutor.getInstance().execute(new Runnable(this, sLInventoryEntry, str) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f443$f0;

            /* renamed from: -$f1 */
            private final /* synthetic */ Object f444$f1;

            /* renamed from: -$f2 */
            private final /* synthetic */ Object f445$f2;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk.5.$m$0():void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk.5.$m$0():void, class status: UNLOADED
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
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity_19384  reason: not valid java name */
    public /* synthetic */ void m626lambda$com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity_19384(SLInventoryEntry sLInventoryEntry, String str) {
        this.noteEntry = sLInventoryEntry;
        this.lastErrorMessage = str;
        this.isSaving = false;
        updateButtonsForMode();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        SLInventoryEntry sLInventoryEntry;
        switch (i) {
            case 1:
                if (i2 == -1 && this.editMode && (sLInventoryEntry = (SLInventoryEntry) intent.getParcelableExtra(InventoryFragment.SELECTED_INVENTORY_ENTRY)) != null) {
                    Spanned createSingleEditableAttachment = SLNotecard.createSingleEditableAttachment(sLInventoryEntry);
                    EditText editText = (EditText) findViewById(R.id.notecardEditContents);
                    int selectionStart = editText.getSelectionStart();
                    int selectionEnd = editText.getSelectionEnd();
                    editText.getText().replace(Math.min(selectionStart, selectionEnd), Math.max(selectionStart, selectionEnd), createSingleEditableAttachment, 0, createSingleEditableAttachment.length());
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void onAttachmentClick(SLInventoryEntry sLInventoryEntry) {
        if (sLInventoryEntry.invType == SLInventoryType.IT_LANDMARK.getTypeCode()) {
            CharSequence[] charSequenceArr = {getString(R.string.attachment_action_teleport), getString(R.string.attachment_action_copy)};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.attachment_action_title));
            builder.setItems(charSequenceArr, new DialogInterface.OnClickListener(this, sLInventoryEntry) {

                /* renamed from: -$f0 */
                private final /* synthetic */ Object f439$f0;

                /* renamed from: -$f1 */
                private final /* synthetic */ Object f440$f1;

                private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk.3.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk.3.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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
            return;
        }
        CharSequence[] charSequenceArr2 = {getString(R.string.attachment_action_copy)};
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle(getString(R.string.attachment_action_title));
        builder2.setItems(charSequenceArr2, new DialogInterface.OnClickListener(this, sLInventoryEntry) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f441$f0;

            /* renamed from: -$f1 */
            private final /* synthetic */ Object f442$f1;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk.4.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk.4.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
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

        builder2.create().show();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.notecardErrorDiscard:
                this.lastErrorMessage = null;
                updateButtonsForMode();
                return;
            case R.id.notecardSaveButton:
                saveChanges();
                return;
            case R.id.notecardDiscardButton:
                discardChanges();
                return;
            case R.id.notecardEditButton:
                startEditing();
                return;
            default:
                return;
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent != null) {
            this.userManager = ActivityUtils.getUserManager(intent);
            this.isEditingScript = intent.getBooleanExtra(IS_SCRIPT_KEY, false);
            this.taskUUID = UUIDPool.getUUID(intent.getStringExtra(TASK_UUID_KEY));
            this.taskLocalID = intent.getIntExtra(TASK_LOCAL_ID_KEY, 0);
            this.noteEntry = (SLInventoryEntry) intent.getParcelableExtra(INVENTORY_ENTRY_KEY);
            this.parentFolderUUID = UUIDPool.getUUID(intent.getStringExtra(PARENT_FOLDER_KEY));
        }
        if (this.userManager == null) {
            finish();
            return;
        }
        setTitle(getString(this.isEditingScript ? R.string.script_edit_title : R.string.notecard_edit_window_title));
        setContentView((int) R.layout.notecard_edit);
        findViewById(R.id.notecardSaveButton).setOnClickListener(this);
        findViewById(R.id.notecardDiscardButton).setOnClickListener(this);
        findViewById(R.id.notecardEditButton).setOnClickListener(this);
        findViewById(R.id.notecardErrorDiscard).setOnClickListener(this);
        this.editMode = false;
        this.notecardTitle = this.noteEntry != null ? this.noteEntry.name : getString(R.string.new_notecard_title);
        this.notecardDescription = this.noteEntry != null ? this.noteEntry.description : getString(R.string.new_notecard_description_format, new Object[]{DateFormat.getDateTimeInstance(3, 3).format(new Date())});
        ((EditText) findViewById(R.id.notecardEditTitle)).setText(this.notecardTitle);
        ((EditText) findViewById(R.id.notecardEditDescription)).setText(this.notecardDescription);
        this.agentCircuit.subscribe(UserManager.agentCircuits(), this.userManager.getUserID());
        if (this.noteEntry == null) {
            this.notecard = new SLNotecard(this.isEditingScript);
            updateButtonsForMode();
            startEditing();
            return;
        }
        this.notecardAssetSubscription.subscribe(this.userManager.getAssetResponseCacher().getPool(), AssetKey.createInventoryKey(this.noteEntry, this.taskUUID));
        updateButtonsForMode();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notecard_menu, menu);
        this.menuItemNewAttachment = menu.findItem(R.id.item_new_attachment);
        if (this.menuItemNewAttachment == null) {
            return true;
        }
        this.menuItemNewAttachment.setVisible(this.editMode);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_new_attachment:
                createNewAttachment();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}

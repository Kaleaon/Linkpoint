// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.view.Menu;
import java.util.Date;
import java.text.DateFormat;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import android.os.Bundle;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryType;
import android.text.Spanned;
import com.lumiyaviewer.lumiya.ui.common.TeleportProgressDialog;
import android.content.DialogInterface;
import android.text.method.MovementMethod;
import android.view.View;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.KeyListener;
import android.text.method.TextKeyListener;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.utils.SimpleStringParser;
import android.text.method.LinkMovementMethod;
import android.widget.EditText;
import android.os.Parcelable;
import android.content.Intent;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetData;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import android.view.MenuItem;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import android.view.View$OnClickListener;
import com.lumiyaviewer.lumiya.slproto.assets.SLNotecard;
import com.lumiyaviewer.lumiya.ui.common.ThemedActivity;

public class NotecardEditActivity extends ThemedActivity implements OnAttachmentClickListener, View$OnClickListener
{
    private static final String INVENTORY_ENTRY_KEY = "inventoryEntry";
    private static final String IS_SCRIPT_KEY = "isScript";
    private static final int ITEM_FOR_ATTACHMENT_REQUEST = 1;
    private static final String PARENT_FOLDER_KEY = "parentFolderUUID";
    private static final String TASK_LOCAL_ID_KEY = "taskLocalID";
    private static final String TASK_UUID_KEY = "taskUUID";
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit;
    private boolean editMode;
    private boolean isEditingScript;
    private boolean isSaving;
    private String lastErrorMessage;
    private MenuItem menuItemNewAttachment;
    private SLInventoryEntry noteEntry;
    private SLNotecard notecard;
    private final SubscriptionData<AssetKey, AssetData> notecardAssetSubscription;
    private String notecardDescription;
    private String notecardTitle;
    private UUID parentFolderUUID;
    private int taskLocalID;
    private UUID taskUUID;
    private UserManager userManager;
    
    public NotecardEditActivity() {
        this.parentFolderUUID = null;
        this.noteEntry = null;
        this.isEditingScript = false;
        this.taskUUID = null;
        this.taskLocalID = 0;
        this.notecardAssetSubscription = new SubscriptionData<AssetKey, AssetData>(UIThreadExecutor.getInstance(), new _$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk(this));
        this.agentCircuit = new SubscriptionData<UUID, SLAgentCircuit>(UIThreadExecutor.getInstance(), new _$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk$1(this));
        this.notecard = null;
        this.editMode = false;
        this.lastErrorMessage = null;
        this.isSaving = false;
    }
    
    private void copyAttachmentToInventory(final SLInventoryEntry slInventoryEntry) {
        if (this.userManager != null && this.noteEntry != null && slInventoryEntry != null) {
            this.startActivity(InventoryActivity.makeSaveItemIntent((Context)this, this.userManager.getUserID(), new InventorySaveInfo(InventorySaveInfo.InventorySaveType.NotecardItem, slInventoryEntry.uuid, slInventoryEntry.name, this.noteEntry.uuid, SLAssetType.getByType(this.noteEntry.assetType), 0L)));
        }
    }
    
    public static Intent createIntent(final Context context, @Nonnull final UUID uuid, @Nullable final UUID uuid2, @Nullable final SLInventoryEntry slInventoryEntry, final boolean b, @Nullable final UUID uuid3, final int n) {
        final Intent intent = new Intent(context, (Class)NotecardEditActivity.class);
        intent.putExtra("activeAgentUUID", uuid.toString());
        if (uuid2 != null) {
            intent.putExtra("parentFolderUUID", uuid2.toString());
        }
        if (slInventoryEntry != null) {
            intent.putExtra("inventoryEntry", (Parcelable)slInventoryEntry);
        }
        if (uuid3 != null) {
            intent.putExtra("taskUUID", uuid3.toString());
        }
        intent.putExtra("taskLocalID", n);
        intent.putExtra("isScript", b);
        return intent;
    }
    
    private void createNewAttachment() {
        if (this.editMode && this.userManager != null) {
            this.startActivityForResult(InventoryActivity.makeSelectIntent((Context)this, this.userManager.getUserID()), 1);
        }
    }
    
    private void discardChanges() {
        if (this.editMode && this.notecard != null) {
            this.editMode = false;
            this.findViewById(2131755502).clearComposingText();
            this.findViewById(2131755502).setText((CharSequence)"");
            this.findViewById(2131755503).clearComposingText();
            this.findViewById(2131755503).setText((CharSequence)"");
            this.findViewById(2131755504).clearComposingText();
            this.findViewById(2131755504).setText((CharSequence)"");
            this.updateButtonsForMode();
            this.findViewById(2131755504).setText((CharSequence)this.notecard.toSpannableString(false, (SLNotecard.OnAttachmentClickListener)this));
            this.findViewById(2131755502).setText((CharSequence)this.notecardTitle);
            this.findViewById(2131755503).setText((CharSequence)this.notecardDescription);
        }
    }
    
    private void onAgentCircuit(final SLAgentCircuit slAgentCircuit) {
        if (slAgentCircuit != null && !slAgentCircuit.getModules().rlvController.canViewNotecard()) {
            this.finish();
        }
        this.updateButtonsForMode();
    }
    
    private void onNotecardLoaded(final AssetData assetData) {
        if (assetData.getStatus() != 1) {
            return;
        }
        try {
            this.notecard = new SLNotecard(assetData.getData(), this.isEditingScript);
            this.findViewById(2131755504).setText((CharSequence)this.notecard.toSpannableString(false, (SLNotecard.OnAttachmentClickListener)this));
            this.findViewById(2131755504).setMovementMethod(LinkMovementMethod.getInstance());
            this.updateButtonsForMode();
        }
        catch (final SimpleStringParser.StringParsingException ex) {
            ex.printStackTrace();
        }
    }
    
    private void saveChanges() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore_1       
        //     2: aconst_null    
        //     3: astore_2       
        //     4: aload_0        
        //     5: getfield        com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.editMode:Z
        //     8: ifeq            271
        //    11: aload_0        
        //    12: getfield        com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.notecard:Lcom/lumiyaviewer/lumiya/slproto/assets/SLNotecard;
        //    15: ifnull          271
        //    18: aload_0        
        //    19: ldc             2131755502
        //    21: invokevirtual   com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.findViewById:(I)Landroid/view/View;
        //    24: checkcast       Landroid/widget/EditText;
        //    27: invokevirtual   android/widget/EditText.getText:()Landroid/text/Editable;
        //    30: invokeinterface android/text/Editable.toString:()Ljava/lang/String;
        //    35: astore_3       
        //    36: aload_0        
        //    37: ldc             2131755503
        //    39: invokevirtual   com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.findViewById:(I)Landroid/view/View;
        //    42: checkcast       Landroid/widget/EditText;
        //    45: invokevirtual   android/widget/EditText.getText:()Landroid/text/Editable;
        //    48: invokeinterface android/text/Editable.toString:()Ljava/lang/String;
        //    53: astore          4
        //    55: aload_0        
        //    56: getfield        com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.noteEntry:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
        //    59: ifnull          95
        //    62: aload_3        
        //    63: aload_0        
        //    64: getfield        com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.noteEntry:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
        //    67: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.name:Ljava/lang/String;
        //    70: invokestatic    com/google/common/base/Objects.equal:(Ljava/lang/Object;Ljava/lang/Object;)Z
        //    73: ifeq            272
        //    76: aload           4
        //    78: aload_0        
        //    79: getfield        com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.noteEntry:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
        //    82: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.description:Ljava/lang/String;
        //    85: invokestatic    com/google/common/base/Objects.equal:(Ljava/lang/Object;Ljava/lang/Object;)Z
        //    88: istore          5
        //    90: iload           5
        //    92: iconst_1       
        //    93: ixor           
        //    94: istore_1       
        //    95: new             Lcom/lumiyaviewer/lumiya/slproto/assets/SLNotecard;
        //    98: dup            
        //    99: aload_0        
        //   100: ldc             2131755504
        //   102: invokevirtual   com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.findViewById:(I)Landroid/view/View;
        //   105: checkcast       Landroid/widget/EditText;
        //   108: invokevirtual   android/widget/EditText.getText:()Landroid/text/Editable;
        //   111: aload_0        
        //   112: getfield        com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.isEditingScript:Z
        //   115: invokespecial   com/lumiyaviewer/lumiya/slproto/assets/SLNotecard.<init>:(Landroid/text/Spanned;Z)V
        //   118: astore          6
        //   120: aload           6
        //   122: invokevirtual   com/lumiyaviewer/lumiya/slproto/assets/SLNotecard.toLindenText:()[B
        //   125: astore          7
        //   127: aload           7
        //   129: aload_0        
        //   130: getfield        com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.notecard:Lcom/lumiyaviewer/lumiya/slproto/assets/SLNotecard;
        //   133: invokevirtual   com/lumiyaviewer/lumiya/slproto/assets/SLNotecard.toLindenText:()[B
        //   136: invokestatic    java/util/Arrays.equals:([B[B)Z
        //   139: ifeq            149
        //   142: aload_0        
        //   143: getfield        com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.noteEntry:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
        //   146: ifnonnull       278
        //   149: aload_0        
        //   150: aload           6
        //   152: putfield        com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.notecard:Lcom/lumiyaviewer/lumiya/slproto/assets/SLNotecard;
        //   155: aload           7
        //   157: astore_2       
        //   158: aload_2        
        //   159: ifnonnull       169
        //   162: aload_0        
        //   163: getfield        com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.noteEntry:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
        //   166: ifnonnull       293
        //   169: aload_0        
        //   170: aload_3        
        //   171: putfield        com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.notecardTitle:Ljava/lang/String;
        //   174: aload_0        
        //   175: aload           4
        //   177: putfield        com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.notecardDescription:Ljava/lang/String;
        //   180: aload_0        
        //   181: getfield        com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.agentCircuit:Lcom/lumiyaviewer/lumiya/react/SubscriptionData;
        //   184: invokevirtual   com/lumiyaviewer/lumiya/react/SubscriptionData.get:()Ljava/lang/Object;
        //   187: checkcast       Lcom/lumiyaviewer/lumiya/slproto/SLAgentCircuit;
        //   190: invokevirtual   com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.getModules:()Lcom/lumiyaviewer/lumiya/slproto/modules/SLModules;
        //   193: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLModules.inventory:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventory;
        //   196: astore          7
        //   198: aload_0        
        //   199: iconst_1       
        //   200: putfield        com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.isSaving:Z
        //   203: aload_0        
        //   204: invokespecial   com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.updateButtonsForMode:()V
        //   207: aload_0        
        //   208: getfield        com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.noteEntry:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
        //   211: astore          8
        //   213: aload_0        
        //   214: getfield        com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.parentFolderUUID:Ljava/util/UUID;
        //   217: astore          9
        //   219: aload_0        
        //   220: getfield        com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.isEditingScript:Z
        //   223: istore          5
        //   225: aload_0        
        //   226: getfield        com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.taskUUID:Ljava/util/UUID;
        //   229: astore          6
        //   231: aload_0        
        //   232: getfield        com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.taskLocalID:I
        //   235: istore_1       
        //   236: new             Lcom/lumiyaviewer/lumiya/ui/inventory/_$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk$2;
        //   239: astore          10
        //   241: aload           10
        //   243: aload_0        
        //   244: invokespecial   com/lumiyaviewer/lumiya/ui/inventory/_$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk$2.<init>:(Ljava/lang/Object;)V
        //   247: aload           7
        //   249: aload           8
        //   251: aload           9
        //   253: iload           5
        //   255: aload_3        
        //   256: aload           4
        //   258: aload_2        
        //   259: aload           6
        //   261: iload_1        
        //   262: aload           10
        //   264: invokevirtual   com/lumiyaviewer/lumiya/slproto/inventory/SLInventory.UpdateNotecard:(Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;Ljava/util/UUID;ZLjava/lang/String;Ljava/lang/String;[BLjava/util/UUID;ILcom/lumiyaviewer/lumiya/slproto/inventory/SLInventory$OnNotecardUpdatedListener;)V
        //   267: aload_0        
        //   268: invokespecial   com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.discardChanges:()V
        //   271: return         
        //   272: iconst_0       
        //   273: istore          5
        //   275: goto            90
        //   278: aload_0        
        //   279: getfield        com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity.noteEntry:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
        //   282: invokevirtual   com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.getId:()J
        //   285: lconst_0       
        //   286: lcmp           
        //   287: ifne            158
        //   290: goto            149
        //   293: iload_1        
        //   294: ifeq            267
        //   297: goto            169
        //   300: astore_2       
        //   301: aload_2        
        //   302: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
        //   305: goto            267
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                                                  
        //  -----  -----  -----  -----  ----------------------------------------------------------------------
        //  180    267    300    308    Lcom/lumiyaviewer/lumiya/react/SubscriptionData$DataNotReadyException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: cmpeq:boolean(__lcmp:int(invokevirtual:long(DBObject::getId, getfield:SLInventoryEntry(NotecardEditActivity::noteEntry, this:NotecardEditActivity)), ldc:long(0L)), ldc:int(0))
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void startEditing() {
        if (!this.editMode && this.notecard != null) {
            this.editMode = true;
            this.findViewById(2131755502).clearComposingText();
            this.findViewById(2131755502).setText((CharSequence)"");
            this.findViewById(2131755503).clearComposingText();
            this.findViewById(2131755503).setText((CharSequence)"");
            this.findViewById(2131755504).clearComposingText();
            this.findViewById(2131755504).setText((CharSequence)"");
            this.updateButtonsForMode();
            this.findViewById(2131755502).setText((CharSequence)this.notecardTitle);
            this.findViewById(2131755503).setText((CharSequence)this.notecardDescription);
            this.findViewById(2131755504).setText((CharSequence)this.notecard.toSpannableString(true, (SLNotecard.OnAttachmentClickListener)this));
        }
    }
    
    private void updateButtonsForMode() {
        final int n = 1;
        final int n2 = 8;
        final KeyListener keyListener = null;
        boolean b;
        if (this.notecardAssetSubscription.getLoadableStatus() == Loadable.Status.Loading) {
            b = true;
        }
        else {
            b = false;
        }
        int n3 = n;
        if (this.noteEntry != null) {
            if ((this.noteEntry.ownerMask & 0x4000) != 0x0) {
                n3 = n;
            }
            else {
                n3 = 0;
            }
        }
        if (this.menuItemNewAttachment != null) {
            this.menuItemNewAttachment.setVisible(this.editMode);
        }
        int text;
        if (n3 != 0 && (b ^ true) && (this.isSaving ^ true)) {
            final View viewById = this.findViewById(2131755510);
            int visibility;
            if (this.editMode) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            viewById.setVisibility(visibility);
            final View viewById2 = this.findViewById(2131755511);
            int visibility2;
            if (this.editMode) {
                visibility2 = 0;
            }
            else {
                visibility2 = 8;
            }
            viewById2.setVisibility(visibility2);
            final View viewById3 = this.findViewById(2131755512);
            int visibility3;
            if (!this.editMode) {
                visibility3 = 0;
            }
            else {
                visibility3 = 8;
            }
            viewById3.setVisibility(visibility3);
            text = -1;
        }
        else {
            this.findViewById(2131755510).setVisibility(8);
            this.findViewById(2131755511).setVisibility(8);
            this.findViewById(2131755512).setVisibility(4);
            if (b) {
                text = 2131296761;
            }
            else if (this.isSaving) {
                text = 2131296764;
            }
            else if (this.noteEntry != null) {
                text = 2131296762;
            }
            else {
                text = -1;
            }
        }
        if (text != -1) {
            this.findViewById(2131755509).setText(text);
        }
        else {
            this.findViewById(2131755509).setText((CharSequence)null);
        }
        if (this.lastErrorMessage != null) {
            this.findViewById(2131755505).setVisibility(0);
            this.findViewById(2131755506).setText((CharSequence)this.lastErrorMessage);
        }
        else {
            this.findViewById(2131755505).setVisibility(8);
        }
        final View viewById4 = this.findViewById(2131755508);
        int visibility4 = 0;
        Label_0256: {
            if (!b) {
                visibility4 = n2;
                if (!this.isSaving) {
                    break Label_0256;
                }
            }
            visibility4 = 0;
        }
        viewById4.setVisibility(visibility4);
        final EditText editText = this.findViewById(2131755504);
        Object instance;
        if (this.editMode) {
            instance = TextKeyListener.getInstance();
        }
        else {
            instance = null;
        }
        editText.setKeyListener((KeyListener)instance);
        final EditText editText2 = this.findViewById(2131755502);
        Object instance2;
        if (this.editMode) {
            instance2 = TextKeyListener.getInstance();
        }
        else {
            instance2 = null;
        }
        editText2.setKeyListener((KeyListener)instance2);
        final EditText editText3 = this.findViewById(2131755503);
        Object instance3 = keyListener;
        if (this.editMode) {
            instance3 = TextKeyListener.getInstance();
        }
        editText3.setKeyListener((KeyListener)instance3);
        final EditText editText4 = this.findViewById(2131755504);
        MovementMethod movementMethod;
        if (this.editMode) {
            movementMethod = ArrowKeyMovementMethod.getInstance();
        }
        else {
            movementMethod = LinkMovementMethod.getInstance();
        }
        editText4.setMovementMethod(movementMethod);
        final EditText editText5 = this.findViewById(2131755502);
        MovementMethod movementMethod2;
        if (this.editMode) {
            movementMethod2 = ArrowKeyMovementMethod.getInstance();
        }
        else {
            movementMethod2 = LinkMovementMethod.getInstance();
        }
        editText5.setMovementMethod(movementMethod2);
        final EditText editText6 = this.findViewById(2131755503);
        MovementMethod movementMethod3;
        if (this.editMode) {
            movementMethod3 = ArrowKeyMovementMethod.getInstance();
        }
        else {
            movementMethod3 = LinkMovementMethod.getInstance();
        }
        editText6.setMovementMethod(movementMethod3);
    }
    
    protected void onActivityResult(int selectionStart, int selectionEnd, final Intent intent) {
        switch (selectionStart) {
            case 1: {
                if (selectionEnd != -1 || !this.editMode) {
                    break;
                }
                final SLInventoryEntry slInventoryEntry = (SLInventoryEntry)intent.getParcelableExtra("selectedInventoryEntry");
                if (slInventoryEntry != null) {
                    final Spanned singleEditableAttachment = SLNotecard.createSingleEditableAttachment(slInventoryEntry);
                    final EditText editText = this.findViewById(2131755504);
                    selectionStart = editText.getSelectionStart();
                    selectionEnd = editText.getSelectionEnd();
                    editText.getText().replace(Math.min(selectionStart, selectionEnd), Math.max(selectionStart, selectionEnd), (CharSequence)singleEditableAttachment, 0, singleEditableAttachment.length());
                    break;
                }
                break;
            }
        }
    }
    
    @Override
    public void onAttachmentClick(final SLInventoryEntry slInventoryEntry) {
        if (slInventoryEntry.invType == SLInventoryType.IT_LANDMARK.getTypeCode()) {
            final String string = this.getString(2131296379);
            final String string2 = this.getString(2131296378);
            final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder((Context)this);
            alertDialog$Builder.setTitle((CharSequence)this.getString(2131296380));
            alertDialog$Builder.setItems(new CharSequence[] { string, string2 }, (DialogInterface$OnClickListener)new _$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk$3(this, slInventoryEntry));
            alertDialog$Builder.create().show();
        }
        else {
            final String string3 = this.getString(2131296378);
            final AlertDialog$Builder alertDialog$Builder2 = new AlertDialog$Builder((Context)this);
            alertDialog$Builder2.setTitle((CharSequence)this.getString(2131296380));
            alertDialog$Builder2.setItems(new CharSequence[] { string3 }, (DialogInterface$OnClickListener)new _$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk$4(this, slInventoryEntry));
            alertDialog$Builder2.create().show();
        }
    }
    
    public void onClick(final View view) {
        switch (view.getId()) {
            case 2131755512: {
                this.startEditing();
                break;
            }
            case 2131755511: {
                this.discardChanges();
                break;
            }
            case 2131755510: {
                this.saveChanges();
                break;
            }
            case 2131755507: {
                this.lastErrorMessage = null;
                this.updateButtonsForMode();
                break;
            }
        }
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        final Intent intent = this.getIntent();
        if (intent != null) {
            this.userManager = ActivityUtils.getUserManager(intent);
            this.isEditingScript = intent.getBooleanExtra("isScript", false);
            this.taskUUID = UUIDPool.getUUID(intent.getStringExtra("taskUUID"));
            this.taskLocalID = intent.getIntExtra("taskLocalID", 0);
            this.noteEntry = (SLInventoryEntry)intent.getParcelableExtra("inventoryEntry");
            this.parentFolderUUID = UUIDPool.getUUID(intent.getStringExtra("parentFolderUUID"));
        }
        if (this.userManager == null) {
            this.finish();
            return;
        }
        int n;
        if (this.isEditingScript) {
            n = 2131296997;
        }
        else {
            n = 2131296760;
        }
        this.setTitle((CharSequence)this.getString(n));
        this.setContentView(2130968677);
        this.findViewById(2131755510).setOnClickListener((View$OnClickListener)this);
        this.findViewById(2131755511).setOnClickListener((View$OnClickListener)this);
        this.findViewById(2131755512).setOnClickListener((View$OnClickListener)this);
        this.findViewById(2131755507).setOnClickListener((View$OnClickListener)this);
        this.editMode = false;
        String notecardTitle;
        if (this.noteEntry != null) {
            notecardTitle = this.noteEntry.name;
        }
        else {
            notecardTitle = this.getString(2131296731);
        }
        this.notecardTitle = notecardTitle;
        String notecardDescription;
        if (this.noteEntry != null) {
            notecardDescription = this.noteEntry.description;
        }
        else {
            notecardDescription = this.getString(2131296730, new Object[] { DateFormat.getDateTimeInstance(3, 3).format(new Date()) });
        }
        this.notecardDescription = notecardDescription;
        this.findViewById(2131755502).setText((CharSequence)this.notecardTitle);
        this.findViewById(2131755503).setText((CharSequence)this.notecardDescription);
        this.agentCircuit.subscribe(UserManager.agentCircuits(), this.userManager.getUserID());
        if (this.noteEntry == null) {
            this.notecard = new SLNotecard(this.isEditingScript);
            this.updateButtonsForMode();
            this.startEditing();
        }
        else {
            this.notecardAssetSubscription.subscribe(this.userManager.getAssetResponseCacher().getPool(), AssetKey.createInventoryKey(this.noteEntry, this.taskUUID));
            this.updateButtonsForMode();
        }
    }
    
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.getMenuInflater().inflate(2131886100, menu);
        this.menuItemNewAttachment = menu.findItem(2131755826);
        if (this.menuItemNewAttachment != null) {
            this.menuItemNewAttachment.setVisible(this.editMode);
        }
        return true;
    }
    
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            default: {
                return super.onOptionsItemSelected(menuItem);
            }
            case 2131755826: {
                this.createNewAttachment();
                return true;
            }
        }
    }
}

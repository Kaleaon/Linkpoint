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
/* loaded from: classes.dex */
public class NotecardEditActivity extends ThemedActivity implements SLNotecard.OnAttachmentClickListener, View.OnClickListener {
    private static final String INVENTORY_ENTRY_KEY = "inventoryEntry";
    private static final String IS_SCRIPT_KEY = "isScript";
    private static final int ITEM_FOR_ATTACHMENT_REQUEST = 1;
    private static final String PARENT_FOLDER_KEY = "parentFolderUUID";
    private static final String TASK_LOCAL_ID_KEY = "taskLocalID";
    private static final String TASK_UUID_KEY = "taskUUID";
    private MenuItem menuItemNewAttachment;
    private String notecardDescription;
    private String notecardTitle;
    private UserManager userManager;
    private UUID parentFolderUUID = null;
    private SLInventoryEntry noteEntry = null;
    private boolean isEditingScript = false;
    private UUID taskUUID = null;
    private int taskLocalID = 0;
    private final SubscriptionData<AssetKey, AssetData> notecardAssetSubscription = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk
        private final /* synthetic */ void $m$0(Object obj) {
            ((NotecardEditActivity) this).m632x5527ee36((AssetData) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk.1
        private final /* synthetic */ void $m$0(Object obj) {
            ((NotecardEditActivity) this).m633x5527ee37((SLAgentCircuit) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private SLNotecard notecard = null;
    private boolean editMode = false;
    private String lastErrorMessage = null;
    private boolean isSaving = false;

    private void copyAttachmentToInventory(SLInventoryEntry sLInventoryEntry) {
        if (this.userManager == null || this.noteEntry == null || sLInventoryEntry == null) {
            return;
        }
        startActivity(InventoryActivity.makeSaveItemIntent(this, this.userManager.getUserID(), new InventorySaveInfo(InventorySaveInfo.InventorySaveType.NotecardItem, sLInventoryEntry.uuid, sLInventoryEntry.name, this.noteEntry.uuid, SLAssetType.getByType(this.noteEntry.assetType), 0L)));
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
        if (!this.editMode || this.userManager == null) {
            return;
        }
        startActivityForResult(InventoryActivity.makeSelectIntent(this, this.userManager.getUserID()), 1);
    }

    private void discardChanges() {
        if (!this.editMode || this.notecard == null) {
            return;
        }
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

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onAgentCircuit */
    public void m633x5527ee37(SLAgentCircuit sLAgentCircuit) {
        if (sLAgentCircuit != null && !sLAgentCircuit.getModules().rlvController.canViewNotecard()) {
            finish();
        }
        updateButtonsForMode();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onNotecardLoaded */
    public void m632x5527ee36(AssetData assetData) {
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
        if (!this.editMode || this.notecard == null) {
            return;
        }
        String editable = ((EditText) findViewById(R.id.notecardEditTitle)).getText().toString();
        String editable2 = ((EditText) findViewById(R.id.notecardEditDescription)).getText().toString();
        if (this.noteEntry != null) {
            z = !(Objects.equal(editable, this.noteEntry.name) ? Objects.equal(editable2, this.noteEntry.description) : false);
        }
        SLNotecard sLNotecard = new SLNotecard(((EditText) findViewById(R.id.notecardEditContents)).getText(), this.isEditingScript);
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
                sLInventory.UpdateNotecard(this.noteEntry, this.parentFolderUUID, this.isEditingScript, editable, editable2, bArr, this.taskUUID, this.taskLocalID, new SLInventory.OnNotecardUpdatedListener() { // from class: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk.2
                    private final /* synthetic */ void $m$0(SLInventoryEntry sLInventoryEntry, String str) {
                        ((NotecardEditActivity) this).m636xf7aea699(sLInventoryEntry, str);
                    }

                    @Override // com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.OnNotecardUpdatedListener
                    public final void onNotecardUpdated(SLInventoryEntry sLInventoryEntry, String str) {
                        $m$0(sLInventoryEntry, str);
                    }
                });
            } catch (SubscriptionData.DataNotReadyException e) {
                Debug.Warning(e);
            }
        }
        discardChanges();
    }

    private void startEditing() {
        if (this.editMode || this.notecard == null) {
            return;
        }
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

    private void updateButtonsForMode() {
        int i;
        boolean z = true;
        int i2 = 8;
        boolean z2 = this.notecardAssetSubscription.getLoadableStatus() == Loadable.Status.Loading;
        if (this.noteEntry != null && (this.noteEntry.ownerMask & 16384) == 0) {
            z = false;
        }
        if (this.menuItemNewAttachment != null) {
            this.menuItemNewAttachment.setVisible(this.editMode);
        }
        if (z && (!z2) && (!this.isSaving)) {
            findViewById(R.id.notecardSaveButton).setVisibility(this.editMode ? 0 : 8);
            findViewById(R.id.notecardDiscardButton).setVisibility(this.editMode ? 0 : 8);
            findViewById(R.id.notecardEditButton).setVisibility(!this.editMode ? 0 : 8);
            i = -1;
        } else {
            findViewById(R.id.notecardSaveButton).setVisibility(8);
            findViewById(R.id.notecardDiscardButton).setVisibility(8);
            findViewById(R.id.notecardEditButton).setVisibility(4);
            i = z2 ? R.string.notecard_loading_contents : this.isSaving ? R.string.notecard_saving_contents : this.noteEntry != null ? R.string.notecard_read_only : -1;
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
        ((EditText) findViewById(R.id.notecardEditDescription)).setKeyListener(this.editMode ? TextKeyListener.getInstance() : null);
        ((EditText) findViewById(R.id.notecardEditContents)).setMovementMethod(this.editMode ? ArrowKeyMovementMethod.getInstance() : LinkMovementMethod.getInstance());
        ((EditText) findViewById(R.id.notecardEditTitle)).setMovementMethod(this.editMode ? ArrowKeyMovementMethod.getInstance() : LinkMovementMethod.getInstance());
        ((EditText) findViewById(R.id.notecardEditDescription)).setMovementMethod(this.editMode ? ArrowKeyMovementMethod.getInstance() : LinkMovementMethod.getInstance());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity_14261  reason: not valid java name */
    public /* synthetic */ void m634xf7ac5d94(SLInventoryEntry sLInventoryEntry, DialogInterface dialogInterface, int i) {
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

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity_15042  reason: not valid java name */
    public /* synthetic */ void m635xf7acca34(SLInventoryEntry sLInventoryEntry, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            copyAttachmentToInventory(sLInventoryEntry);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity_19315  reason: not valid java name */
    public /* synthetic */ void m636xf7aea699(final SLInventoryEntry sLInventoryEntry, final String str) {
        UIThreadExecutor.getInstance().execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk.5
            private final /* synthetic */ void $m$0() {
                ((NotecardEditActivity) this).m637xf7aea771((SLInventoryEntry) sLInventoryEntry, (String) str);
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity_19384  reason: not valid java name */
    public /* synthetic */ void m637xf7aea771(SLInventoryEntry sLInventoryEntry, String str) {
        this.noteEntry = sLInventoryEntry;
        this.lastErrorMessage = str;
        this.isSaving = false;
        updateButtonsForMode();
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
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

    @Override // com.lumiyaviewer.lumiya.slproto.assets.SLNotecard.OnAttachmentClickListener
    public void onAttachmentClick(final SLInventoryEntry sLInventoryEntry) {
        if (sLInventoryEntry.invType == SLInventoryType.IT_LANDMARK.getTypeCode()) {
            CharSequence[] charSequenceArr = {getString(R.string.attachment_action_teleport), getString(R.string.attachment_action_copy)};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.attachment_action_title));
            builder.setItems(charSequenceArr, new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk.3
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                    ((NotecardEditActivity) this).m634xf7ac5d94((SLInventoryEntry) sLInventoryEntry, dialogInterface, i);
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    $m$0(dialogInterface, i);
                }
            });
            builder.create().show();
            return;
        }
        CharSequence[] charSequenceArr2 = {getString(R.string.attachment_action_copy)};
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle(getString(R.string.attachment_action_title));
        builder2.setItems(charSequenceArr2, new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk.4
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                ((NotecardEditActivity) this).m635xf7acca34((SLInventoryEntry) sLInventoryEntry, dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder2.create().show();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.notecardErrorDiscard /* 2131755507 */:
                this.lastErrorMessage = null;
                updateButtonsForMode();
                return;
            case R.id.notecardProgressIndicator /* 2131755508 */:
            case R.id.notecardProgressText /* 2131755509 */:
            default:
                return;
            case R.id.notecardSaveButton /* 2131755510 */:
                saveChanges();
                return;
            case R.id.notecardDiscardButton /* 2131755511 */:
                discardChanges();
                return;
            case R.id.notecardEditButton /* 2131755512 */:
                startEditing();
                return;
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ThemedActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
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
        setContentView(R.layout.notecard_edit);
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
        if (this.noteEntry != null) {
            this.notecardAssetSubscription.subscribe(this.userManager.getAssetResponseCacher().getPool(), AssetKey.createInventoryKey(this.noteEntry, this.taskUUID));
            updateButtonsForMode();
            return;
        }
        this.notecard = new SLNotecard(this.isEditingScript);
        updateButtonsForMode();
        startEditing();
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notecard_menu, menu);
        this.menuItemNewAttachment = menu.findItem(R.id.item_new_attachment);
        if (this.menuItemNewAttachment != null) {
            this.menuItemNewAttachment.setVisible(this.editMode);
            return true;
        }
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_new_attachment /* 2131755826 */:
                createNewAttachment();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}

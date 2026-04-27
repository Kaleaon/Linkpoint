// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.TextKeyListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.assets.SLNotecard;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryType;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetData;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetResponseCacher;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.TeleportProgressDialog;
import com.lumiyaviewer.lumiya.ui.common.ThemedActivity;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.inventory:
//            InventorySaveInfo, InventoryActivity

public class NotecardEditActivity extends ThemedActivity
    implements com.lumiyaviewer.lumiya.slproto.assets.SLNotecard.OnAttachmentClickListener, android.view.View.OnClickListener
{

    private static final String INVENTORY_ENTRY_KEY = "inventoryEntry";
    private static final String IS_SCRIPT_KEY = "isScript";
    private static final int ITEM_FOR_ATTACHMENT_REQUEST = 1;
    private static final String PARENT_FOLDER_KEY = "parentFolderUUID";
    private static final String TASK_LOCAL_ID_KEY = "taskLocalID";
    private static final String TASK_UUID_KEY = "taskUUID";
    private final SubscriptionData agentCircuit = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.srzsajEQjSwYc3yok0XsNFeAjNk._cls1(this));
    private boolean editMode;
    private boolean isEditingScript;
    private boolean isSaving;
    private String lastErrorMessage;
    private MenuItem menuItemNewAttachment;
    private SLInventoryEntry noteEntry;
    private SLNotecard notecard;
    private final SubscriptionData notecardAssetSubscription = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.srzsajEQjSwYc3yok0XsNFeAjNk(this));
    private String notecardDescription;
    private String notecardTitle;
    private UUID parentFolderUUID;
    private int taskLocalID;
    private UUID taskUUID;
    private UserManager userManager;

    public NotecardEditActivity()
    {
        parentFolderUUID = null;
        noteEntry = null;
        isEditingScript = false;
        taskUUID = null;
        taskLocalID = 0;
        notecard = null;
        editMode = false;
        lastErrorMessage = null;
        isSaving = false;
    }

    private void copyAttachmentToInventory(SLInventoryEntry slinventoryentry)
    {
        if (userManager != null && noteEntry != null && slinventoryentry != null)
        {
            SLAssetType slassettype = SLAssetType.getByType(noteEntry.assetType);
            startActivity(InventoryActivity.makeSaveItemIntent(this, userManager.getUserID(), new InventorySaveInfo(InventorySaveInfo.InventorySaveType.NotecardItem, slinventoryentry.uuid, slinventoryentry.name, noteEntry.uuid, slassettype, 0L)));
        }
    }

    public static Intent createIntent(Context context, UUID uuid, UUID uuid1, SLInventoryEntry slinventoryentry, boolean flag, UUID uuid2, int i)
    {
        context = new Intent(context, com/lumiyaviewer/lumiya/ui/inventory/NotecardEditActivity);
        context.putExtra("activeAgentUUID", uuid.toString());
        if (uuid1 != null)
        {
            context.putExtra("parentFolderUUID", uuid1.toString());
        }
        if (slinventoryentry != null)
        {
            context.putExtra("inventoryEntry", slinventoryentry);
        }
        if (uuid2 != null)
        {
            context.putExtra("taskUUID", uuid2.toString());
        }
        context.putExtra("taskLocalID", i);
        context.putExtra("isScript", flag);
        return context;
    }

    private void createNewAttachment()
    {
        if (editMode && userManager != null)
        {
            startActivityForResult(InventoryActivity.makeSelectIntent(this, userManager.getUserID()), 1);
        }
    }

    private void discardChanges()
    {
        if (editMode && notecard != null)
        {
            editMode = false;
            ((EditText)findViewById(0x7f1001ee)).clearComposingText();
            ((EditText)findViewById(0x7f1001ee)).setText("");
            ((EditText)findViewById(0x7f1001ef)).clearComposingText();
            ((EditText)findViewById(0x7f1001ef)).setText("");
            ((EditText)findViewById(0x7f1001f0)).clearComposingText();
            ((EditText)findViewById(0x7f1001f0)).setText("");
            updateButtonsForMode();
            ((EditText)findViewById(0x7f1001f0)).setText(notecard.toSpannableString(false, this));
            ((EditText)findViewById(0x7f1001ee)).setText(notecardTitle);
            ((EditText)findViewById(0x7f1001ef)).setText(notecardDescription);
        }
    }

    private void onAgentCircuit(SLAgentCircuit slagentcircuit)
    {
        if (slagentcircuit != null && !slagentcircuit.getModules().rlvController.canViewNotecard())
        {
            finish();
        }
        updateButtonsForMode();
    }

    private void onNotecardLoaded(AssetData assetdata)
    {
        if (assetdata.getStatus() != 1)
        {
            break MISSING_BLOCK_LABEL_67;
        }
        notecard = new SLNotecard(assetdata.getData(), isEditingScript);
        ((EditText)findViewById(0x7f1001f0)).setText(notecard.toSpannableString(false, this));
        ((EditText)findViewById(0x7f1001f0)).setMovementMethod(LinkMovementMethod.getInstance());
        updateButtonsForMode();
        return;
        assetdata;
        assetdata.printStackTrace();
        return;
    }

    private void saveChanges()
    {
        discardChanges();
        return;
        boolean flag;
        {
            flag = true;
            byte abyte0[] = null;
            if (!editMode || notecard == null)
            {
                break MISSING_BLOCK_LABEL_245;
            }
            String s = ((EditText)findViewById(0x7f1001ee)).getText().toString();
            String s1 = ((EditText)findViewById(0x7f1001ef)).getText().toString();
            if (noteEntry != null)
            {
                byte abyte1[];
                SLInventory slinventory;
                SLNotecard slnotecard;
                boolean flag1;
                if (Objects.equal(s, noteEntry.name))
                {
                    flag1 = Objects.equal(s1, noteEntry.description);
                } else
                {
                    flag1 = false;
                }
                flag = flag1 ^ true;
            }
            slnotecard = new SLNotecard(((EditText)findViewById(0x7f1001f0)).getText(), isEditingScript);
            abyte1 = slnotecard.toLindenText();
        }
        if (!Arrays.equals(abyte1, notecard.toLindenText()) || noteEntry == null || noteEntry.getId() == 0L)
        {
            notecard = slnotecard;
            abyte0 = abyte1;
        }
        if (abyte0 != null || noteEntry == null || flag)
        {
            notecardTitle = s;
            notecardDescription = s1;
            try
            {
                slinventory = ((SLAgentCircuit)agentCircuit.get()).getModules().inventory;
                isSaving = true;
                updateButtonsForMode();
                slinventory.UpdateNotecard(noteEntry, parentFolderUUID, isEditingScript, s, s1, abyte0, taskUUID, taskLocalID, new _2D_.Lambda.srzsajEQjSwYc3yok0XsNFeAjNk._cls2(this));
            }
            catch (com.lumiyaviewer.lumiya.react.SubscriptionData.DataNotReadyException datanotreadyexception)
            {
                Debug.Warning(datanotreadyexception);
            }
        }
        break MISSING_BLOCK_LABEL_241;
    }

    private void startEditing()
    {
        if (!editMode && notecard != null)
        {
            editMode = true;
            ((EditText)findViewById(0x7f1001ee)).clearComposingText();
            ((EditText)findViewById(0x7f1001ee)).setText("");
            ((EditText)findViewById(0x7f1001ef)).clearComposingText();
            ((EditText)findViewById(0x7f1001ef)).setText("");
            ((EditText)findViewById(0x7f1001f0)).clearComposingText();
            ((EditText)findViewById(0x7f1001f0)).setText("");
            updateButtonsForMode();
            ((EditText)findViewById(0x7f1001ee)).setText(notecardTitle);
            ((EditText)findViewById(0x7f1001ef)).setText(notecardDescription);
            ((EditText)findViewById(0x7f1001f0)).setText(notecard.toSpannableString(true, this));
        }
    }

    private void updateButtonsForMode()
    {
label0:
        {
            boolean flag1 = true;
            byte byte0 = 8;
            EditText edittext = null;
            Object obj;
            int i;
            boolean flag;
            if (notecardAssetSubscription.getLoadableStatus() == com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable.Status.Loading)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            i = ((flag1) ? 1 : 0);
            if (noteEntry != null)
            {
                if ((noteEntry.ownerMask & 0x4000) != 0)
                {
                    i = ((flag1) ? 1 : 0);
                } else
                {
                    i = 0;
                }
            }
            if (menuItemNewAttachment != null)
            {
                menuItemNewAttachment.setVisible(editMode);
            }
            if (i != 0 && flag ^ true && isSaving ^ true)
            {
                obj = findViewById(0x7f1001f6);
                EditText edittext1;
                if (editMode)
                {
                    i = 0;
                } else
                {
                    i = 8;
                }
                ((View) (obj)).setVisibility(i);
                obj = findViewById(0x7f1001f7);
                if (editMode)
                {
                    i = 0;
                } else
                {
                    i = 8;
                }
                ((View) (obj)).setVisibility(i);
                obj = findViewById(0x7f1001f8);
                if (!editMode)
                {
                    i = 0;
                } else
                {
                    i = 8;
                }
                ((View) (obj)).setVisibility(i);
                i = -1;
            } else
            {
                findViewById(0x7f1001f6).setVisibility(8);
                findViewById(0x7f1001f7).setVisibility(8);
                findViewById(0x7f1001f8).setVisibility(4);
                if (flag)
                {
                    i = 0x7f0901f9;
                } else
                if (isSaving)
                {
                    i = 0x7f0901fc;
                } else
                if (noteEntry != null)
                {
                    i = 0x7f0901fa;
                } else
                {
                    i = -1;
                }
            }
            if (i != -1)
            {
                ((TextView)findViewById(0x7f1001f5)).setText(i);
            } else
            {
                ((TextView)findViewById(0x7f1001f5)).setText(null);
            }
            if (lastErrorMessage != null)
            {
                findViewById(0x7f1001f1).setVisibility(0);
                ((TextView)findViewById(0x7f1001f2)).setText(lastErrorMessage);
            } else
            {
                findViewById(0x7f1001f1).setVisibility(8);
            }
            obj = findViewById(0x7f1001f4);
            if (!flag)
            {
                i = byte0;
                if (!isSaving)
                {
                    break label0;
                }
            }
            i = 0;
        }
        ((View) (obj)).setVisibility(i);
        edittext1 = (EditText)findViewById(0x7f1001f0);
        if (editMode)
        {
            obj = TextKeyListener.getInstance();
        } else
        {
            obj = null;
        }
        edittext1.setKeyListener(((android.text.method.KeyListener) (obj)));
        edittext1 = (EditText)findViewById(0x7f1001ee);
        if (editMode)
        {
            obj = TextKeyListener.getInstance();
        } else
        {
            obj = null;
        }
        edittext1.setKeyListener(((android.text.method.KeyListener) (obj)));
        edittext1 = (EditText)findViewById(0x7f1001ef);
        obj = edittext;
        if (editMode)
        {
            obj = TextKeyListener.getInstance();
        }
        edittext1.setKeyListener(((android.text.method.KeyListener) (obj)));
        edittext = (EditText)findViewById(0x7f1001f0);
        if (editMode)
        {
            obj = ArrowKeyMovementMethod.getInstance();
        } else
        {
            obj = LinkMovementMethod.getInstance();
        }
        edittext.setMovementMethod(((android.text.method.MovementMethod) (obj)));
        edittext = (EditText)findViewById(0x7f1001ee);
        if (editMode)
        {
            obj = ArrowKeyMovementMethod.getInstance();
        } else
        {
            obj = LinkMovementMethod.getInstance();
        }
        edittext.setMovementMethod(((android.text.method.MovementMethod) (obj)));
        edittext = (EditText)findViewById(0x7f1001ef);
        if (editMode)
        {
            obj = ArrowKeyMovementMethod.getInstance();
        } else
        {
            obj = LinkMovementMethod.getInstance();
        }
        edittext.setMovementMethod(((android.text.method.MovementMethod) (obj)));
    }

    void _2D_com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity_2D_mthref_2D_0(AssetData assetdata)
    {
        onNotecardLoaded(assetdata);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity_2D_mthref_2D_1(SLAgentCircuit slagentcircuit)
    {
        onAgentCircuit(slagentcircuit);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity_14261(SLInventoryEntry slinventoryentry, DialogInterface dialoginterface, int i)
    {
        i;
        JVM INSTR tableswitch 0 1: default 24
    //                   0 25
    //                   1 46;
           goto _L1 _L2 _L3
_L1:
        return;
_L2:
        if (userManager != null)
        {
            TeleportProgressDialog.TeleportToLandmark(this, userManager, slinventoryentry.assetUUID, false);
            return;
        }
          goto _L1
_L3:
        copyAttachmentToInventory(slinventoryentry);
        return;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity_15042(SLInventoryEntry slinventoryentry, DialogInterface dialoginterface, int i)
    {
        if (i == 0)
        {
            copyAttachmentToInventory(slinventoryentry);
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity_19315(SLInventoryEntry slinventoryentry, String s)
    {
        UIThreadExecutor.getInstance().execute(new _2D_.Lambda.srzsajEQjSwYc3yok0XsNFeAjNk._cls5(this, slinventoryentry, s));
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity_19384(SLInventoryEntry slinventoryentry, String s)
    {
        noteEntry = slinventoryentry;
        lastErrorMessage = s;
        isSaving = false;
        updateButtonsForMode();
    }

    protected void onActivityResult(int i, int j, Intent intent)
    {
        i;
        JVM INSTR tableswitch 1 1: default 20
    //                   1 21;
           goto _L1 _L2
_L1:
        return;
_L2:
        if (j == -1 && editMode && (intent = (SLInventoryEntry)intent.getParcelableExtra("selectedInventoryEntry")) != null)
        {
            intent = SLNotecard.createSingleEditableAttachment(intent);
            EditText edittext = (EditText)findViewById(0x7f1001f0);
            i = edittext.getSelectionStart();
            j = edittext.getSelectionEnd();
            edittext.getText().replace(Math.min(i, j), Math.max(i, j), intent, 0, intent.length());
            return;
        }
        if (true) goto _L1; else goto _L3
_L3:
    }

    public void onAttachmentClick(SLInventoryEntry slinventoryentry)
    {
        if (slinventoryentry.invType == SLInventoryType.IT_LANDMARK.getTypeCode())
        {
            String s = getString(0x7f09007b);
            String s2 = getString(0x7f09007a);
            android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(this);
            builder1.setTitle(getString(0x7f09007c));
            slinventoryentry = new _2D_.Lambda.srzsajEQjSwYc3yok0XsNFeAjNk._cls3(this, slinventoryentry);
            builder1.setItems(new CharSequence[] {
                s, s2
            }, slinventoryentry);
            builder1.create().show();
            return;
        } else
        {
            String s1 = getString(0x7f09007a);
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle(getString(0x7f09007c));
            slinventoryentry = new _2D_.Lambda.srzsajEQjSwYc3yok0XsNFeAjNk._cls4(this, slinventoryentry);
            builder.setItems(new CharSequence[] {
                s1
            }, slinventoryentry);
            builder.create().show();
            return;
        }
    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
        case 2131755508: 
        case 2131755509: 
        default:
            return;

        case 2131755512: 
            startEditing();
            return;

        case 2131755511: 
            discardChanges();
            return;

        case 2131755510: 
            saveChanges();
            return;

        case 2131755507: 
            lastErrorMessage = null;
            updateButtonsForMode();
            return;
        }
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        bundle = getIntent();
        if (bundle != null)
        {
            userManager = ActivityUtils.getUserManager(bundle);
            isEditingScript = bundle.getBooleanExtra("isScript", false);
            taskUUID = UUIDPool.getUUID(bundle.getStringExtra("taskUUID"));
            taskLocalID = bundle.getIntExtra("taskLocalID", 0);
            noteEntry = (SLInventoryEntry)bundle.getParcelableExtra("inventoryEntry");
            parentFolderUUID = UUIDPool.getUUID(bundle.getStringExtra("parentFolderUUID"));
        }
        if (userManager == null)
        {
            finish();
            return;
        }
        int i;
        if (isEditingScript)
        {
            i = 0x7f0902e5;
        } else
        {
            i = 0x7f0901f8;
        }
        setTitle(getString(i));
        setContentView(0x7f040065);
        findViewById(0x7f1001f6).setOnClickListener(this);
        findViewById(0x7f1001f7).setOnClickListener(this);
        findViewById(0x7f1001f8).setOnClickListener(this);
        findViewById(0x7f1001f3).setOnClickListener(this);
        editMode = false;
        if (noteEntry != null)
        {
            bundle = noteEntry.name;
        } else
        {
            bundle = getString(0x7f0901db);
        }
        notecardTitle = bundle;
        if (noteEntry != null)
        {
            bundle = noteEntry.description;
        } else
        {
            bundle = getString(0x7f0901da, new Object[] {
                DateFormat.getDateTimeInstance(3, 3).format(new Date())
            });
        }
        notecardDescription = bundle;
        ((EditText)findViewById(0x7f1001ee)).setText(notecardTitle);
        ((EditText)findViewById(0x7f1001ef)).setText(notecardDescription);
        agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID());
        if (noteEntry == null)
        {
            notecard = new SLNotecard(isEditingScript);
            updateButtonsForMode();
            startEditing();
            return;
        } else
        {
            notecardAssetSubscription.subscribe(userManager.getAssetResponseCacher().getPool(), AssetKey.createInventoryKey(noteEntry, taskUUID));
            updateButtonsForMode();
            return;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(0x7f120014, menu);
        menuItemNewAttachment = menu.findItem(0x7f100332);
        if (menuItemNewAttachment != null)
        {
            menuItemNewAttachment.setVisible(editMode);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        switch (menuitem.getItemId())
        {
        default:
            return super.onOptionsItemSelected(menuitem);

        case 2131755826: 
            createNewAttachment();
            break;
        }
        return true;
    }
}

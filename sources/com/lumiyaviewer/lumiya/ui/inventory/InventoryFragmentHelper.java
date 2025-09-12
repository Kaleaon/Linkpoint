// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.types.AgentPosition;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.avapicker.AvatarPickerForShare;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.TeleportProgressDialog;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.inventory:
//            TextureViewFragment, NotecardEditActivity

public class InventoryFragmentHelper
{

    static final int SORT_ORDER_ALPHA = 1;
    private static final String SORT_ORDER_KEY = "inventorySortOrder";
    public static final int SORT_ORDER_NEWEST_FIRST = 0;
    private final Fragment fragment;

    public InventoryFragmentHelper(Fragment fragment1)
    {
        fragment = fragment1;
    }

    private void ViewTexture(UUID uuid)
    {
        UserManager usermanager = getUserManager();
        FragmentActivity fragmentactivity = fragment.getActivity();
        if (fragmentactivity != null && usermanager != null)
        {
            DetailsActivity.showEmbeddedDetails(fragmentactivity, com/lumiyaviewer/lumiya/ui/inventory/TextureViewFragment, TextureViewFragment.makeArguments(usermanager.getUserID(), uuid));
        }
    }

    private SLAgentCircuit getActiveAgentCircuit()
    {
        SLAgentCircuit slagentcircuit = null;
        UserManager usermanager = getUserManager();
        if (usermanager != null)
        {
            slagentcircuit = usermanager.getActiveAgentCircuit();
        }
        return slagentcircuit;
    }

    private Context getContext()
    {
        return fragment.getContext();
    }

    public static int getSortOrder(Context context)
    {
        if (context != null)
        {
            return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getInt("inventorySortOrder", 0);
        } else
        {
            return 0;
        }
    }

    private UserManager getUserManager()
    {
        Object obj = UserManager.getUserManager(ActivityUtils.getActiveAgentID(fragment.getArguments()));
        if (obj != null)
        {
            return ((UserManager) (obj));
        }
        obj = fragment.getActivity();
        if (obj != null)
        {
            return UserManager.getUserManager(ActivityUtils.getActiveAgentID(((FragmentActivity) (obj)).getIntent()));
        } else
        {
            return null;
        }
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentHelper_10110(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentHelper_12001(Dialog dialog, SLInventory slinventory, SLInventoryEntry slinventoryentry, String s, View view)
    {
        view = ((EditText)dialog.findViewById(0x7f1001b1)).getText().toString().trim();
        if (!view.isEmpty())
        {
            slinventory.DoCreateNewLandmark(slinventoryentry, view, s);
        }
        dialog.dismiss();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentHelper_12390(Dialog dialog, View view)
    {
        dialog.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentHelper_4506(Dialog dialog, View view)
    {
        dialog.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentHelper_6279(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentHelper_7522(Dialog dialog, View view)
    {
        dialog.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentHelper_8206(boolean flag, SLInventory slinventory, SLInventoryEntry slinventoryentry, Runnable runnable, DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        if (flag)
        {
            slinventory.DeleteInventoryItem(slinventoryentry);
        } else
        {
            slinventory.TrashInventoryItem(slinventoryentry);
        }
        if (runnable != null)
        {
            runnable.run();
        }
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentHelper_8645(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentHelper_9777(Runnable runnable, SLAgentCircuit slagentcircuit, ChatterID chatterid, SLInventoryEntry slinventoryentry, DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        if (runnable != null)
        {
            runnable.run();
        }
        slagentcircuit.OfferInventoryItem(chatterid.getOptionalChatterUUID(), slinventoryentry);
    }

    static void setSortOrder(Context context, int i)
    {
        context = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
        context.putInt("inventorySortOrder", i);
        context.apply();
    }

    private void showRezDialog(SLInventoryEntry slinventoryentry)
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setMessage(getContext().getString(0x7f0902a9)).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda._cls6k4r7SqDbbDd94MFx7S4Wwav0Lg._cls6(this, slinventoryentry)).setNegativeButton("No", new _2D_.Lambda._cls6k4r7SqDbbDd94MFx7S4Wwav0Lg._cls2());
        builder.create().show();
    }

    private void showTeleportDialog(SLInventoryEntry slinventoryentry)
    {
        if (getUserManager() != null && slinventoryentry != null)
        {
            TeleportProgressDialog.TeleportToLandmark(getContext(), getUserManager(), slinventoryentry.assetUUID, true);
        }
    }

    public void ConfirmShareInventoryEntry(SLInventoryEntry slinventoryentry, ChatterID chatterid, String s, Runnable runnable)
    {
        SLAgentCircuit slagentcircuit = getActiveAgentCircuit();
        if (slinventoryentry != null && chatterid != null && slagentcircuit != null)
        {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
            builder.setMessage(String.format(getContext().getString(0x7f090304), new Object[] {
                slinventoryentry.name, s
            })).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda._cls6k4r7SqDbbDd94MFx7S4Wwav0Lg._cls9(runnable, slagentcircuit, chatterid, slinventoryentry)).setNegativeButton("No", new _2D_.Lambda._cls6k4r7SqDbbDd94MFx7S4Wwav0Lg());
            builder.create().show();
        }
    }

    void CreateNewFolder(SLInventoryEntry slinventoryentry)
    {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(0x7f040050);
        dialog.setTitle(0x7f0901d7);
        ((Button)dialog.findViewById(0x7f1000b6)).setText(0x7f0900d2);
        dialog.findViewById(0x7f1000b6).setOnClickListener(new _2D_.Lambda._cls6k4r7SqDbbDd94MFx7S4Wwav0Lg._cls7(this, dialog, slinventoryentry));
        dialog.findViewById(0x7f1000b7).setOnClickListener(new _2D_.Lambda._cls6k4r7SqDbbDd94MFx7S4Wwav0Lg._cls3(dialog));
        dialog.show();
    }

    void CreateNewLandmark(SLInventoryEntry slinventoryentry)
    {
        String s1 = null;
        Object obj1 = getUserManager();
        SLAgentCircuit slagentcircuit = getActiveAgentCircuit();
        SLInventory slinventory = getInventory();
        if (slagentcircuit != null && obj1 != null && slinventory != null)
        {
            LLVector3 llvector3 = new LLVector3();
            String s = slagentcircuit.getRegionName();
            Object obj = s;
            if (s == null)
            {
                obj = "Simulator";
            }
            slagentcircuit.getModules().avatarControl.getAgentPosition().getPosition(llvector3);
            obj1 = ((UserManager) (obj1)).getCurrentLocationInfoSnapshot();
            s = s1;
            if (obj1 != null)
            {
                obj1 = ((CurrentLocationInfo) (obj1)).parcelData();
                s = s1;
                if (obj1 != null)
                {
                    s = ((ParcelData) (obj1)).getName();
                }
            }
            s1 = s;
            if (Strings.isNullOrEmpty(s))
            {
                s1 = "Landmark";
            }
            s = String.format("%s, %s (%d, %d, %d)", new Object[] {
                s1, obj, Integer.valueOf((int)llvector3.x), Integer.valueOf((int)llvector3.y), Integer.valueOf((int)llvector3.z)
            });
            obj = new Dialog(getContext());
            ((Dialog) (obj)).setContentView(0x7f040050);
            ((Dialog) (obj)).setTitle(0x7f0901d9);
            ((Button)((Dialog) (obj)).findViewById(0x7f1000b6)).setText(0x7f0900d2);
            ((EditText)((Dialog) (obj)).findViewById(0x7f1001b1)).setText(s1);
            ((Dialog) (obj)).findViewById(0x7f1000b6).setOnClickListener(new _2D_.Lambda._cls6k4r7SqDbbDd94MFx7S4Wwav0Lg._cls10(obj, slinventory, slinventoryentry, s));
            ((Dialog) (obj)).findViewById(0x7f1000b7).setOnClickListener(new _2D_.Lambda._cls6k4r7SqDbbDd94MFx7S4Wwav0Lg._cls4(obj));
            ((Dialog) (obj)).show();
        }
    }

    void DeleteInventoryEntry(SLInventoryEntry slinventoryentry, Runnable runnable)
    {
        SLInventory slinventory = getInventory();
        if (slinventory != null && slinventoryentry != null)
        {
            android.app.AlertDialog.Builder builder;
            Context context;
            int i;
            boolean flag;
            if (slinventory.canMoveToTrash(slinventoryentry))
            {
                flag = slinventoryentry.isLink();
            } else
            {
                flag = true;
            }
            builder = new android.app.AlertDialog.Builder(getContext());
            context = getContext();
            if (flag)
            {
                i = 0x7f0900df;
            } else
            {
                i = 0x7f09035f;
            }
            builder.setMessage(context.getString(i)).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda._cls6k4r7SqDbbDd94MFx7S4Wwav0Lg._cls11(flag, slinventory, slinventoryentry, runnable)).setNegativeButton("No", new _2D_.Lambda._cls6k4r7SqDbbDd94MFx7S4Wwav0Lg._cls1());
            builder.create().show();
        }
    }

    void PerformInventoryAction(SLInventoryEntry slinventoryentry, int i)
    {
        Debug.Log((new StringBuilder()).append("PerformInventoryAction: entry = ").append(slinventoryentry).toString());
        i;
        JVM INSTR tableswitch 2131296333 2131296336: default 56
    //                   2131296333 69
    //                   2131296334 63
    //                   2131296335 57
    //                   2131296336 139;
           goto _L1 _L2 _L3 _L4 _L5
_L1:
        return;
_L4:
        showTeleportDialog(slinventoryentry);
        return;
_L3:
        Object obj;
        showRezDialog(slinventoryentry);
        return;
_L2:
        if ((obj = getUserManager()) != null)
        {
            Context context = getContext();
            obj = ((UserManager) (obj)).getUserID();
            UUID uuid = slinventoryentry.parentUUID;
            boolean flag;
            if (slinventoryentry.invType == 10)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            slinventoryentry = NotecardEditActivity.createIntent(context, ((UUID) (obj)), uuid, slinventoryentry, flag, null, 0);
            getContext().startActivity(slinventoryentry);
            return;
        }
        continue; /* Loop/switch isn't completed */
_L5:
        Debug.Log((new StringBuilder()).append("Inventory: view action for asset type = ").append(slinventoryentry.assetType).toString());
        if (slinventoryentry.assetType == SLAssetType.AT_TEXTURE.getTypeCode())
        {
            ViewTexture(slinventoryentry.assetUUID);
            return;
        }
        if (true) goto _L1; else goto _L6
_L6:
    }

    void RenameInventoryEntry(SLInventoryEntry slinventoryentry)
    {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(0x7f040050);
        dialog.setTitle(0x7f0902a4);
        ((EditText)dialog.findViewById(0x7f1001b1)).setText(slinventoryentry.name);
        dialog.findViewById(0x7f1000b6).setOnClickListener(new _2D_.Lambda._cls6k4r7SqDbbDd94MFx7S4Wwav0Lg._cls8(this, dialog, slinventoryentry));
        dialog.findViewById(0x7f1000b7).setOnClickListener(new _2D_.Lambda._cls6k4r7SqDbbDd94MFx7S4Wwav0Lg._cls5(dialog));
        dialog.show();
    }

    void ShareInventoryEntry(SLInventoryEntry slinventoryentry)
    {
        UserManager usermanager = getUserManager();
        FragmentActivity fragmentactivity = fragment.getActivity();
        if (fragmentactivity != null && usermanager != null)
        {
            DetailsActivity.showEmbeddedDetails(fragmentactivity, com/lumiyaviewer/lumiya/ui/avapicker/AvatarPickerForShare, AvatarPickerForShare.makeArguments(usermanager.getUserID(), slinventoryentry));
        }
    }

    SLInventory getInventory()
    {
        SLAgentCircuit slagentcircuit = getActiveAgentCircuit();
        if (slagentcircuit != null)
        {
            return slagentcircuit.getModules().inventory;
        } else
        {
            return null;
        }
    }

    boolean isActionAllowed(SLInventoryEntry slinventoryentry, int i)
    {
        SLAgentCircuit slagentcircuit = getActiveAgentCircuit();
        if (slagentcircuit == null)
        {
            return false;
        }
        if (i == 0x7f09004f)
        {
            if (!slagentcircuit.getModules().rlvController.canTeleportToLandmark())
            {
                return false;
            }
        } else
        if (i == 0x7f09004d && slinventoryentry.invType == 7 && !slagentcircuit.getModules().rlvController.canViewNotecard())
        {
            return false;
        }
        return true;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentHelper_3806(Dialog dialog, SLInventoryEntry slinventoryentry, View view)
    {
        view = ((EditText)dialog.findViewById(0x7f1001b1)).getText().toString().trim();
        if (!view.isEmpty())
        {
            Object obj = getUserManager();
            if (obj != null)
            {
                obj = ((UserManager) (obj)).getActiveAgentCircuit();
                if (obj != null)
                {
                    ((SLAgentCircuit) (obj)).getModules().inventory.DoCreateNewFolder(slinventoryentry, view, false, null);
                }
            }
        }
        dialog.dismiss();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentHelper_5882(SLInventoryEntry slinventoryentry, DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        if (slinventoryentry != null)
        {
            dialoginterface = getActiveAgentCircuit();
            if (dialoginterface != null)
            {
                dialoginterface.RezObject(slinventoryentry);
            }
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentHelper_7038(Dialog dialog, SLInventoryEntry slinventoryentry, View view)
    {
        view = ((EditText)dialog.findViewById(0x7f1001b1)).getText().toString().trim();
        if (!view.isEmpty())
        {
            SLInventory slinventory = getInventory();
            if (slinventory != null)
            {
                slinventory.RenameInventoryItem(slinventoryentry, view);
            }
        }
        dialog.dismiss();
    }
}

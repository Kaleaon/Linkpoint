package com.lumiyaviewer.lumiya.ui.inventory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class InventoryFragmentHelper {
    static final int SORT_ORDER_ALPHA = 1;
    private static final String SORT_ORDER_KEY = "inventorySortOrder";
    public static final int SORT_ORDER_NEWEST_FIRST = 0;

    @Nonnull
    private final Fragment fragment;

    public InventoryFragmentHelper(@Nonnull Fragment fragment) {
        this.fragment = fragment;
    }

    private void ViewTexture(UUID uuid) {
        UserManager userManager = getUserManager();
        FragmentActivity activity = this.fragment.getActivity();
        if (activity == null || userManager == null) {
            return;
        }
        DetailsActivity.showEmbeddedDetails(activity, TextureViewFragment.class, TextureViewFragment.makeArguments(userManager.getUserID(), uuid));
    }

    @Nullable
    private SLAgentCircuit getActiveAgentCircuit() {
        UserManager userManager = getUserManager();
        if (userManager != null) {
            return userManager.getActiveAgentCircuit();
        }
        return null;
    }

    private Context getContext() {
        return this.fragment.getContext();
    }

    public static int getSortOrder(Context context) {
        if (context != null) {
            return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getInt(SORT_ORDER_KEY, 0);
        }
        return 0;
    }

    @Nullable
    private UserManager getUserManager() {
        UserManager userManager = UserManager.getUserManager(ActivityUtils.getActiveAgentID(this.fragment.getArguments()));
        if (userManager != null) {
            return userManager;
        }
        FragmentActivity activity = this.fragment.getActivity();
        if (activity != null) {
            return UserManager.getUserManager(ActivityUtils.getActiveAgentID(activity.getIntent()));
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentHelper_12001, reason: not valid java name */
    public static /* synthetic */ void m620xa4b588bd(Dialog dialog, SLInventory sLInventory, SLInventoryEntry sLInventoryEntry, String str, View view) {
        String trim = ((EditText) dialog.findViewById(R.id.itemNameText)).getText().toString().trim();
        if (!trim.isEmpty()) {
            sLInventory.DoCreateNewLandmark(sLInventoryEntry, trim, str);
        }
        dialog.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentHelper_8206, reason: not valid java name */
    public static /* synthetic */ void m625x89746493(boolean z, SLInventory sLInventory, SLInventoryEntry sLInventoryEntry, Runnable runnable, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        if (z) {
            sLInventory.DeleteInventoryItem(sLInventoryEntry);
        } else {
            sLInventory.TrashInventoryItem(sLInventoryEntry);
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentHelper_9777, reason: not valid java name */
    public static /* synthetic */ void m627x8974ec91(Runnable runnable, SLAgentCircuit sLAgentCircuit, ChatterID chatterID, SLInventoryEntry sLInventoryEntry, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        if (runnable != null) {
            runnable.run();
        }
        sLAgentCircuit.OfferInventoryItem(chatterID.getOptionalChatterUUID(), sLInventoryEntry);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setSortOrder(Context context, int i) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
        edit.putInt(SORT_ORDER_KEY, i);
        edit.apply();
    }

    private void showRezDialog(final SLInventoryEntry sLInventoryEntry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getContext().getString(R.string.rez_confirm_title)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg.6
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                ((InventoryFragmentHelper) this).m629x89731ef0((SLInventoryEntry) sLInventoryEntry, dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg.2
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder.create().show();
    }

    private void showTeleportDialog(SLInventoryEntry sLInventoryEntry) {
        if (getUserManager() == null || sLInventoryEntry == null) {
            return;
        }
        TeleportProgressDialog.TeleportToLandmark(getContext(), getUserManager(), sLInventoryEntry.assetUUID, true);
    }

    public void ConfirmShareInventoryEntry(final SLInventoryEntry sLInventoryEntry, final ChatterID chatterID, String str, @Nullable final Runnable runnable) {
        final SLAgentCircuit activeAgentCircuit = getActiveAgentCircuit();
        if (sLInventoryEntry == null || chatterID == null || activeAgentCircuit == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(String.format(getContext().getString(R.string.share_inv_item_title), sLInventoryEntry.name, str)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg.9
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                InventoryFragmentHelper.m627x8974ec91((Runnable) runnable, (SLAgentCircuit) activeAgentCircuit, (ChatterID) chatterID, (SLInventoryEntry) sLInventoryEntry, dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder.create().show();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void CreateNewFolder(final SLInventoryEntry sLInventoryEntry) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.inv_rename_dialog);
        dialog.setTitle(R.string.new_folder_dialog_title);
        ((Button) dialog.findViewById(R.id.okButton)).setText(R.string.create_folder_button);
        dialog.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg.7
            private final /* synthetic */ void $m$0(View view) {
                ((InventoryFragmentHelper) this).m628x8972353e((Dialog) dialog, (SLInventoryEntry) sLInventoryEntry, view);
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg.3
            private final /* synthetic */ void $m$0(View view) {
                ((Dialog) dialog).cancel();
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
        dialog.show();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void CreateNewLandmark(final SLInventoryEntry sLInventoryEntry) {
        ParcelData parcelData;
        String str = null;
        UserManager userManager = getUserManager();
        SLAgentCircuit activeAgentCircuit = getActiveAgentCircuit();
        final SLInventory inventory = getInventory();
        if (activeAgentCircuit == null || userManager == null || inventory == null) {
            return;
        }
        LLVector3 lLVector3 = new LLVector3();
        String regionName = activeAgentCircuit.getRegionName();
        if (regionName == null) {
            regionName = "Simulator";
        }
        activeAgentCircuit.getModules().avatarControl.getAgentPosition().getPosition(lLVector3);
        CurrentLocationInfo currentLocationInfoSnapshot = userManager.getCurrentLocationInfoSnapshot();
        if (currentLocationInfoSnapshot != null && (parcelData = currentLocationInfoSnapshot.parcelData()) != null) {
            str = parcelData.getName();
        }
        if (Strings.isNullOrEmpty(str)) {
            str = "Landmark";
        }
        final String format = String.format("%s, %s (%d, %d, %d)", str, regionName, Integer.valueOf((int) lLVector3.x), Integer.valueOf((int) lLVector3.y), Integer.valueOf((int) lLVector3.z));
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.inv_rename_dialog);
        dialog.setTitle(R.string.new_landmark_dialog_title);
        ((Button) dialog.findViewById(R.id.okButton)).setText(R.string.create_folder_button);
        ((EditText) dialog.findViewById(R.id.itemNameText)).setText(str);
        dialog.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg.10
            private final /* synthetic */ void $m$0(View view) {
                InventoryFragmentHelper.m620xa4b588bd((Dialog) dialog, (SLInventory) inventory, (SLInventoryEntry) sLInventoryEntry, (String) format, view);
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg.4
            private final /* synthetic */ void $m$0(View view) {
                ((Dialog) dialog).cancel();
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
        dialog.show();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void DeleteInventoryEntry(final SLInventoryEntry sLInventoryEntry, @Nullable final Runnable runnable) {
        final SLInventory inventory = getInventory();
        if (inventory == null || sLInventoryEntry == null) {
            return;
        }
        final boolean isLink = inventory.canMoveToTrash(sLInventoryEntry) ? sLInventoryEntry.isLink() : true;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getContext().getString(isLink ? R.string.delete_inv_item_title : R.string.trash_inv_item_title)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg.11
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                InventoryFragmentHelper.m625x89746493(isLink, (SLInventory) inventory, (SLInventoryEntry) sLInventoryEntry, (Runnable) runnable, dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg.1
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder.create().show();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void PerformInventoryAction(SLInventoryEntry sLInventoryEntry, int i) {
        Debug.Log("PerformInventoryAction: entry = " + sLInventoryEntry);
        switch (i) {
            case R.string.asset_action_edit /* 2131296333 */:
                UserManager userManager = getUserManager();
                if (userManager != null) {
                    getContext().startActivity(NotecardEditActivity.createIntent(getContext(), userManager.getUserID(), sLInventoryEntry.parentUUID, sLInventoryEntry, sLInventoryEntry.invType == 10, null, 0));
                    return;
                }
                return;
            case R.string.asset_action_rez /* 2131296334 */:
                showRezDialog(sLInventoryEntry);
                return;
            case R.string.asset_action_teleport /* 2131296335 */:
                showTeleportDialog(sLInventoryEntry);
                return;
            case R.string.asset_action_view /* 2131296336 */:
                Debug.Log("Inventory: view action for asset type = " + sLInventoryEntry.assetType);
                if (sLInventoryEntry.assetType == SLAssetType.AT_TEXTURE.getTypeCode()) {
                    ViewTexture(sLInventoryEntry.assetUUID);
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void RenameInventoryEntry(final SLInventoryEntry sLInventoryEntry) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.inv_rename_dialog);
        dialog.setTitle(R.string.rename_inv_item_title);
        ((EditText) dialog.findViewById(R.id.itemNameText)).setText(sLInventoryEntry.name);
        dialog.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg.8
            private final /* synthetic */ void $m$0(View view) {
                ((InventoryFragmentHelper) this).m630x8973e911((Dialog) dialog, (SLInventoryEntry) sLInventoryEntry, view);
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg.5
            private final /* synthetic */ void $m$0(View view) {
                ((Dialog) dialog).cancel();
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
        dialog.show();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void ShareInventoryEntry(SLInventoryEntry sLInventoryEntry) {
        UserManager userManager = getUserManager();
        FragmentActivity activity = this.fragment.getActivity();
        if (activity == null || userManager == null) {
            return;
        }
        DetailsActivity.showEmbeddedDetails(activity, AvatarPickerForShare.class, AvatarPickerForShare.makeArguments(userManager.getUserID(), sLInventoryEntry));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public SLInventory getInventory() {
        SLAgentCircuit activeAgentCircuit = getActiveAgentCircuit();
        if (activeAgentCircuit != null) {
            return activeAgentCircuit.getModules().inventory;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isActionAllowed(SLInventoryEntry sLInventoryEntry, int i) {
        SLAgentCircuit activeAgentCircuit = getActiveAgentCircuit();
        if (activeAgentCircuit == null) {
            return false;
        }
        return i == R.string.asset_action_teleport ? activeAgentCircuit.getModules().rlvController.canTeleportToLandmark() : (i == R.string.asset_action_edit && sLInventoryEntry.invType == 7 && !activeAgentCircuit.getModules().rlvController.canViewNotecard()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentHelper_3806, reason: not valid java name */
    public /* synthetic */ void m628x8972353e(Dialog dialog, SLInventoryEntry sLInventoryEntry, View view) {
        UserManager userManager;
        SLAgentCircuit activeAgentCircuit;
        String trim = ((EditText) dialog.findViewById(R.id.itemNameText)).getText().toString().trim();
        if (!trim.isEmpty() && (userManager = getUserManager()) != null && (activeAgentCircuit = userManager.getActiveAgentCircuit()) != null) {
            activeAgentCircuit.getModules().inventory.DoCreateNewFolder(sLInventoryEntry, trim, false, null);
        }
        dialog.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentHelper_5882, reason: not valid java name */
    public /* synthetic */ void m629x89731ef0(SLInventoryEntry sLInventoryEntry, DialogInterface dialogInterface, int i) {
        SLAgentCircuit activeAgentCircuit;
        dialogInterface.dismiss();
        if (sLInventoryEntry == null || (activeAgentCircuit = getActiveAgentCircuit()) == null) {
            return;
        }
        activeAgentCircuit.RezObject(sLInventoryEntry);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentHelper_7038, reason: not valid java name */
    public /* synthetic */ void m630x8973e911(Dialog dialog, SLInventoryEntry sLInventoryEntry, View view) {
        SLInventory inventory;
        String trim = ((EditText) dialog.findViewById(R.id.itemNameText)).getText().toString().trim();
        if (!trim.isEmpty() && (inventory = getInventory()) != null) {
            inventory.RenameInventoryItem(sLInventoryEntry, trim);
        }
        dialog.dismiss();
    }
}

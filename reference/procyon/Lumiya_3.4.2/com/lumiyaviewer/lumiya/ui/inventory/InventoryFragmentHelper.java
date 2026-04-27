// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.inventory;

import com.google.common.base.Function;
import com.lumiyaviewer.lumiya.ui.avapicker.AvatarPickerForShare;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import android.view.View$OnClickListener;
import android.widget.Button;
import com.lumiyaviewer.lumiya.ui.common.TeleportProgressDialog;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import android.content.SharedPreferences$Editor;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.widget.EditText;
import android.view.View;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import android.app.Dialog;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import android.preference.PreferenceManager;
import android.content.Context;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import android.support.v4.app.FragmentActivity;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import java.util.UUID;
import javax.annotation.Nonnull;
import android.support.v4.app.Fragment;

public class InventoryFragmentHelper
{
    static final int SORT_ORDER_ALPHA = 1;
    private static final String SORT_ORDER_KEY = "inventorySortOrder";
    public static final int SORT_ORDER_NEWEST_FIRST = 0;
    @Nonnull
    private final Fragment fragment;
    
    public InventoryFragmentHelper(@Nonnull final Fragment fragment) {
        this.fragment = fragment;
    }
    
    private void ViewTexture(final UUID uuid) {
        final UserManager userManager = this.getUserManager();
        final FragmentActivity activity = this.fragment.getActivity();
        if (activity != null && userManager != null) {
            DetailsActivity.showEmbeddedDetails(activity, TextureViewFragment.class, TextureViewFragment.makeArguments(userManager.getUserID(), uuid));
        }
    }
    
    @Nullable
    private SLAgentCircuit getActiveAgentCircuit() {
        SLAgentCircuit activeAgentCircuit = null;
        final UserManager userManager = this.getUserManager();
        if (userManager != null) {
            activeAgentCircuit = userManager.getActiveAgentCircuit();
        }
        return activeAgentCircuit;
    }
    
    private Context getContext() {
        return this.fragment.getContext();
    }
    
    public static int getSortOrder(final Context context) {
        if (context != null) {
            return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getInt("inventorySortOrder", 0);
        }
        return 0;
    }
    
    @Nullable
    private UserManager getUserManager() {
        final UserManager userManager = UserManager.getUserManager(ActivityUtils.getActiveAgentID(this.fragment.getArguments()));
        if (userManager != null) {
            return userManager;
        }
        final FragmentActivity activity = this.fragment.getActivity();
        if (activity != null) {
            return UserManager.getUserManager(ActivityUtils.getActiveAgentID(activity.getIntent()));
        }
        return null;
    }
    
    static void setSortOrder(final Context context, final int n) {
        final SharedPreferences$Editor edit = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
        edit.putInt("inventorySortOrder", n);
        edit.apply();
    }
    
    private void showRezDialog(final SLInventoryEntry slInventoryEntry) {
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.getContext());
        alertDialog$Builder.setMessage((CharSequence)this.getContext().getString(2131296937)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg$6(this, slInventoryEntry)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg$2());
        alertDialog$Builder.create().show();
    }
    
    private void showTeleportDialog(final SLInventoryEntry slInventoryEntry) {
        if (this.getUserManager() != null && slInventoryEntry != null) {
            TeleportProgressDialog.TeleportToLandmark(this.getContext(), this.getUserManager(), slInventoryEntry.assetUUID, true);
        }
    }
    
    public void ConfirmShareInventoryEntry(final SLInventoryEntry slInventoryEntry, final ChatterID chatterID, final String s, @Nullable final Runnable runnable) {
        final SLAgentCircuit activeAgentCircuit = this.getActiveAgentCircuit();
        if (slInventoryEntry != null && chatterID != null && activeAgentCircuit != null) {
            final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.getContext());
            alertDialog$Builder.setMessage((CharSequence)String.format(this.getContext().getString(2131297028), slInventoryEntry.name, s)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg$9(runnable, activeAgentCircuit, chatterID, slInventoryEntry)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg());
            alertDialog$Builder.create().show();
        }
    }
    
    void CreateNewFolder(final SLInventoryEntry slInventoryEntry) {
        final Dialog dialog = new Dialog(this.getContext());
        dialog.setContentView(2130968656);
        dialog.setTitle(2131296727);
        ((Button)dialog.findViewById(2131755190)).setText(2131296466);
        dialog.findViewById(2131755190).setOnClickListener((View$OnClickListener)new _$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg$7(this, dialog, slInventoryEntry));
        dialog.findViewById(2131755191).setOnClickListener((View$OnClickListener)new _$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg$3(dialog));
        dialog.show();
    }
    
    void CreateNewLandmark(final SLInventoryEntry slInventoryEntry) {
        final String s = null;
        final UserManager userManager = this.getUserManager();
        final SLAgentCircuit activeAgentCircuit = this.getActiveAgentCircuit();
        final SLInventory inventory = this.getInventory();
        if (activeAgentCircuit != null && userManager != null && inventory != null) {
            final LLVector3 llVector3 = new LLVector3();
            String regionName;
            if ((regionName = activeAgentCircuit.getRegionName()) == null) {
                regionName = "Simulator";
            }
            activeAgentCircuit.getModules().avatarControl.getAgentPosition().getPosition(llVector3);
            final CurrentLocationInfo currentLocationInfoSnapshot = userManager.getCurrentLocationInfoSnapshot();
            String name = s;
            if (currentLocationInfoSnapshot != null) {
                final ParcelData parcelData = currentLocationInfoSnapshot.parcelData();
                name = s;
                if (parcelData != null) {
                    name = parcelData.getName();
                }
            }
            String text = name;
            if (Strings.isNullOrEmpty(name)) {
                text = "Landmark";
            }
            final String format = String.format("%s, %s (%d, %d, %d)", text, regionName, (int)llVector3.x, (int)llVector3.y, (int)llVector3.z);
            final Dialog dialog = new Dialog(this.getContext());
            dialog.setContentView(2130968656);
            dialog.setTitle(2131296729);
            ((Button)dialog.findViewById(2131755190)).setText(2131296466);
            ((EditText)dialog.findViewById(2131755441)).setText((CharSequence)text);
            dialog.findViewById(2131755190).setOnClickListener((View$OnClickListener)new _$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg$10(dialog, inventory, slInventoryEntry, format));
            dialog.findViewById(2131755191).setOnClickListener((View$OnClickListener)new _$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg$4(dialog));
            dialog.show();
        }
    }
    
    void DeleteInventoryEntry(final SLInventoryEntry slInventoryEntry, @Nullable final Runnable runnable) {
        final SLInventory inventory = this.getInventory();
        if (inventory != null && slInventoryEntry != null) {
            final boolean b = !inventory.canMoveToTrash(slInventoryEntry) || slInventoryEntry.isLink();
            final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.getContext());
            final Context context = this.getContext();
            int n;
            if (b) {
                n = 2131296479;
            }
            else {
                n = 2131297119;
            }
            alertDialog$Builder.setMessage((CharSequence)context.getString(n)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg$11(b, inventory, slInventoryEntry, runnable)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg$1());
            alertDialog$Builder.create().show();
        }
    }
    
    void PerformInventoryAction(final SLInventoryEntry obj, final int n) {
        Debug.Log("PerformInventoryAction: entry = " + obj);
        switch (n) {
            case 2131296335: {
                this.showTeleportDialog(obj);
                break;
            }
            case 2131296334: {
                this.showRezDialog(obj);
                break;
            }
            case 2131296333: {
                final UserManager userManager = this.getUserManager();
                if (userManager != null) {
                    this.getContext().startActivity(NotecardEditActivity.createIntent(this.getContext(), userManager.getUserID(), obj.parentUUID, obj, obj.invType == 10, null, 0));
                    break;
                }
                break;
            }
            case 2131296336: {
                Debug.Log("Inventory: view action for asset type = " + obj.assetType);
                if (obj.assetType == SLAssetType.AT_TEXTURE.getTypeCode()) {
                    this.ViewTexture(obj.assetUUID);
                    break;
                }
                break;
            }
        }
    }
    
    void RenameInventoryEntry(final SLInventoryEntry slInventoryEntry) {
        final Dialog dialog = new Dialog(this.getContext());
        dialog.setContentView(2130968656);
        dialog.setTitle(2131296932);
        ((EditText)dialog.findViewById(2131755441)).setText((CharSequence)slInventoryEntry.name);
        dialog.findViewById(2131755190).setOnClickListener((View$OnClickListener)new _$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg$8(this, dialog, slInventoryEntry));
        dialog.findViewById(2131755191).setOnClickListener((View$OnClickListener)new _$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg$5(dialog));
        dialog.show();
    }
    
    void ShareInventoryEntry(final SLInventoryEntry slInventoryEntry) {
        final UserManager userManager = this.getUserManager();
        final FragmentActivity activity = this.fragment.getActivity();
        if (activity != null && userManager != null) {
            DetailsActivity.showEmbeddedDetails(activity, AvatarPickerForShare.class, AvatarPickerForShare.makeArguments(userManager.getUserID(), slInventoryEntry));
        }
    }
    
    @Nullable
    SLInventory getInventory() {
        final SLAgentCircuit activeAgentCircuit = this.getActiveAgentCircuit();
        if (activeAgentCircuit != null) {
            return activeAgentCircuit.getModules().inventory;
        }
        return null;
    }
    
    boolean isActionAllowed(final SLInventoryEntry slInventoryEntry, final int n) {
        final SLAgentCircuit activeAgentCircuit = this.getActiveAgentCircuit();
        if (activeAgentCircuit == null) {
            return false;
        }
        if (n == 2131296335) {
            if (!activeAgentCircuit.getModules().rlvController.canTeleportToLandmark()) {
                return false;
            }
        }
        else if (n == 2131296333 && slInventoryEntry.invType == 7 && !activeAgentCircuit.getModules().rlvController.canViewNotecard()) {
            return false;
        }
        return true;
    }
}

// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.content.Intent;
import android.widget.Toast;
import com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager;
import android.support.v4.widget.SwipeRefreshLayout;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.Menu;
import android.support.v4.app.FragmentActivity;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import android.widget.Button;
import android.graphics.Bitmap;
import android.widget.ImageView;
import com.google.common.base.Strings;
import android.widget.TextView;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import javax.annotation.Nullable;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import android.app.Dialog;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.google.common.collect.Table;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import android.view.MenuItem;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import android.view.View$OnClickListener;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;

public class AssetInfoFragment extends FragmentWithTitle implements ReloadableFragment, View$OnClickListener, OnLoadableDataChangedListener
{
    private static final String ITEM_UUID_KEY = "itemUUID";
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit;
    private ChatterNameRetriever creatorNameRetriever;
    private final SubscriptionData<UUID, SLInventoryEntry> entrySubscription;
    private final InventoryFragmentHelper inventoryFragmentHelper;
    private ChatterNameRetriever lastOwnerNameRetriever;
    private final LoadableMonitor loadableMonitor;
    private MenuItem menuItemCopy;
    private MenuItem menuItemCut;
    private MenuItem menuItemDelete;
    private MenuItem menuItemRename;
    private MenuItem menuItemShare;
    private final ChatterNameRetriever.OnChatterNameUpdated onNameUpdated;
    private ChatterNameRetriever ownerNameRetriever;
    private final SubscriptionData<SubscriptionSingleKey, ImmutableSet<UUID>> runningAnimations;
    private final SubscriptionData<SubscriptionSingleKey, ImmutableMap<UUID, String>> wornAttachments;
    private final SubscriptionData<SubscriptionSingleKey, Table<SLWearableType, UUID, SLWearable>> wornWearables;
    
    public AssetInfoFragment() {
        this.inventoryFragmentHelper = new InventoryFragmentHelper(this);
        this.agentCircuit = new SubscriptionData<UUID, SLAgentCircuit>(UIThreadExecutor.getInstance());
        this.entrySubscription = new SubscriptionData<UUID, SLInventoryEntry>(UIThreadExecutor.getInstance());
        this.wornAttachments = new SubscriptionData<SubscriptionSingleKey, ImmutableMap<UUID, String>>(UIThreadExecutor.getInstance());
        this.wornWearables = new SubscriptionData<SubscriptionSingleKey, Table<SLWearableType, UUID, SLWearable>>(UIThreadExecutor.getInstance());
        this.runningAnimations = new SubscriptionData<SubscriptionSingleKey, ImmutableSet<UUID>>(UIThreadExecutor.getInstance());
        this.loadableMonitor = new LoadableMonitor(new Loadable[] { this.entrySubscription }).withOptionalLoadables(this.wornAttachments, this.wornWearables, this.agentCircuit, this.runningAnimations).withDataChangedListener((LoadableMonitor.OnLoadableDataChangedListener)this);
        this.ownerNameRetriever = null;
        this.creatorNameRetriever = null;
        this.lastOwnerNameRetriever = null;
        this.onNameUpdated = new _$Lambda$OIe5MtmKyVPF26gruCQoZkxXroQ$1(this);
    }
    
    private void applyEditedPermissions(final Dialog dialog) {
        final SLInventoryEntry slInventoryEntry = this.entrySubscription.getData();
        if (slInventoryEntry != null) {
            final SLInventory inventory = this.inventoryFragmentHelper.getInventory();
            if (inventory != null) {
                slInventoryEntry.nextOwnerMask = (slInventoryEntry.ownerMask & this.getCheckboxes(dialog, 2131755241, 2131755242, 2131755243));
                slInventoryEntry.groupMask = (slInventoryEntry.ownerMask & this.getCheckboxes(dialog, 2131755244, 2131755245, 2131755246));
                slInventoryEntry.everyoneMask = (slInventoryEntry.ownerMask & this.getCheckboxes(dialog, 2131755247, 2131755248, 2131755249));
                inventory.UpdateStoreInventoryItem(slInventoryEntry);
                this.showEntryInfo(slInventoryEntry);
            }
        }
    }
    
    private void attachObject(final SLInventoryEntry slInventoryEntry) {
        try {
            this.agentCircuit.get().getModules().avatarAppearance.AttachInventoryItem(slInventoryEntry, 0, false);
        }
        catch (final SubscriptionData.DataNotReadyException ex) {
            Debug.Warning(ex);
        }
    }
    
    private void detachObject(final SLInventoryEntry slInventoryEntry) {
        try {
            this.agentCircuit.get().getModules().avatarAppearance.DetachInventoryItem(slInventoryEntry);
        }
        catch (final SubscriptionData.DataNotReadyException ex) {
            Debug.Warning(ex);
        }
    }
    
    private int getCheckboxes(final Dialog dialog, int n, int n2, final int n3) {
        int n4 = 0;
        if (((CheckBox)dialog.findViewById(n)).isChecked()) {
            n4 = 32768;
        }
        n = n4;
        if (((CheckBox)dialog.findViewById(n2)).isChecked()) {
            n = (n4 | 0x4000);
        }
        n2 = n;
        if (((CheckBox)dialog.findViewById(n3)).isChecked()) {
            n2 = (n | 0x2000);
        }
        return n2;
    }
    
    public static Bundle makeSelection(final UUID uuid, final UUID uuid2) {
        final Bundle bundle = new Bundle();
        if (uuid != null) {
            bundle.putString("activeAgentUUID", uuid.toString());
        }
        if (uuid2 != null) {
            bundle.putString("itemUUID", uuid2.toString());
        }
        return bundle;
    }
    
    private void playAnimation(final SLInventoryEntry slInventoryEntry, final boolean b) {
        final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
        if (slAgentCircuit != null) {
            slAgentCircuit.getModules().avatarControl.playAnimation(slInventoryEntry.assetUUID, b);
        }
    }
    
    private void setCheckboxes(final Dialog dialog, final int n, final int n2, final int n3, final int n4, final int n5, final boolean b) {
        final boolean b2 = false;
        ((CheckBox)dialog.findViewById(n3)).setChecked((n & 0x8000) != 0x0);
        ((CheckBox)dialog.findViewById(n4)).setChecked((n & 0x4000) != 0x0);
        ((CheckBox)dialog.findViewById(n5)).setChecked((n & 0x2000) != 0x0);
        dialog.findViewById(n3).setEnabled(b && (n2 & 0x8000) != 0x0);
        dialog.findViewById(n4).setEnabled(b && (n2 & 0x4000) != 0x0);
        final View viewById = dialog.findViewById(n5);
        boolean enabled = b2;
        if (b) {
            enabled = b2;
            if ((n2 & 0x2000) != 0x0) {
                enabled = true;
            }
        }
        viewById.setEnabled(enabled);
    }
    
    private void showEditPermissionsDialog() {
        final SLInventoryEntry slInventoryEntry = this.entrySubscription.getData();
        if (slInventoryEntry != null) {
            final Dialog dialog = new Dialog((Context)this.getActivity());
            dialog.setContentView(2130968606);
            dialog.setTitle(2131296522);
            this.setCheckboxes(dialog, slInventoryEntry.ownerMask, slInventoryEntry.ownerMask, 2131755238, 2131755239, 2131755240, false);
            this.setCheckboxes(dialog, slInventoryEntry.nextOwnerMask, slInventoryEntry.ownerMask, 2131755241, 2131755242, 2131755243, true);
            this.setCheckboxes(dialog, slInventoryEntry.groupMask, slInventoryEntry.ownerMask, 2131755244, 2131755245, 2131755246, true);
            this.setCheckboxes(dialog, slInventoryEntry.everyoneMask, slInventoryEntry.ownerMask, 2131755247, 2131755248, 2131755249, true);
            ((CheckBox)dialog.findViewById(2131755248)).setChecked(false);
            dialog.findViewById(2131755248).setEnabled(false);
            dialog.findViewById(2131755190).setOnClickListener((View$OnClickListener)new _$Lambda$OIe5MtmKyVPF26gruCQoZkxXroQ$3(this, dialog));
            dialog.findViewById(2131755191).setOnClickListener((View$OnClickListener)new _$Lambda$OIe5MtmKyVPF26gruCQoZkxXroQ(dialog));
            dialog.show();
        }
    }
    
    private void showEntry(@Nullable final UUID uuid) {
        this.loadableMonitor.unsubscribeAll();
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        if (uuid != null && userManager != null) {
            this.agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID());
            this.entrySubscription.subscribe(userManager.getInventoryManager().getFolderEntryPool(), uuid);
            this.wornAttachments.subscribe((Subscribable<SubscriptionSingleKey, ImmutableMap<UUID, String>>)userManager.getWornAttachmentsPool(), SubscriptionSingleKey.Value);
            this.wornWearables.subscribe((Subscribable<SubscriptionSingleKey, Table<SLWearableType, UUID, SLWearable>>)userManager.getWornWearablesPool(), SubscriptionSingleKey.Value);
            this.runningAnimations.subscribe((Subscribable<SubscriptionSingleKey, ImmutableSet<UUID>>)userManager.getObjectsManager().runningAnimations(), SubscriptionSingleKey.Value);
        }
        else {
            if (this.creatorNameRetriever != null) {
                this.creatorNameRetriever.dispose();
            }
            if (this.ownerNameRetriever != null) {
                this.ownerNameRetriever.dispose();
            }
            if (this.lastOwnerNameRetriever != null) {
                this.lastOwnerNameRetriever.dispose();
            }
        }
    }
    
    private void showEntryInfo(@Nonnull final SLInventoryEntry slInventoryEntry) {
        final int n = 0;
        final int n2 = 8;
        final View view = this.getView();
        if (view != null) {
            ((TextView)view.findViewById(2131755200)).setText((CharSequence)slInventoryEntry.name);
            final TextView textView = (TextView)view.findViewById(2131755211);
            String text;
            if (!Strings.isNullOrEmpty(slInventoryEntry.description)) {
                text = slInventoryEntry.description;
            }
            else {
                text = this.getResources().getString(2131296347);
            }
            textView.setText((CharSequence)text);
            ((TextView)view.findViewById(2131755199)).setText(slInventoryEntry.getTypeDescriptionResId());
            final int drawableResource = slInventoryEntry.getDrawableResource();
            if (drawableResource >= 0) {
                ((ImageView)view.findViewById(2131755201)).setImageResource(drawableResource);
            }
            else {
                ((ImageView)view.findViewById(2131755201)).setImageBitmap((Bitmap)null);
            }
            final int actionDescriptionResId = slInventoryEntry.getActionDescriptionResId();
            if (actionDescriptionResId >= 0) {
                ((Button)view.findViewById(2131755202)).setText(actionDescriptionResId);
                view.findViewById(2131755202).setVisibility(0);
                view.findViewById(2131755202).setEnabled(this.inventoryFragmentHelper.isActionAllowed(slInventoryEntry, actionDescriptionResId));
            }
            else {
                view.findViewById(2131755202).setVisibility(8);
            }
            final View viewById = view.findViewById(2131755225);
            int visibility;
            if ((slInventoryEntry.ownerMask & 0x4000) != 0x0) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            viewById.setVisibility(visibility);
            this.showPermissions(slInventoryEntry.ownerMask, 2131755213, 2131755214, 2131755215);
            this.showPermissions(slInventoryEntry.groupMask, 2131755219, 2131755220, 2131755221);
            this.showPermissions(slInventoryEntry.everyoneMask, 2131755222, 2131755223, 2131755224);
            this.showPermissions(slInventoryEntry.nextOwnerMask, 2131755216, 2131755217, 2131755218);
            final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
            boolean b;
            if (slAgentCircuit != null) {
                b = true;
            }
            else {
                b = false;
            }
            if (b && (slInventoryEntry.assetType == SLAssetType.AT_OBJECT.getTypeCode() || (slInventoryEntry.assetType == SLAssetType.AT_LINK.getTypeCode() && slInventoryEntry.invType == SLInventoryType.IT_OBJECT.getTypeCode()))) {
                boolean b2;
                if (slInventoryEntry.whatIsItemWornOn(this.wornAttachments.getData(), this.wornWearables.getData(), false) != null) {
                    b2 = true;
                }
                else {
                    b2 = false;
                }
                final boolean b3 = b2 && slAgentCircuit.getModules().avatarAppearance.canDetachItem(slInventoryEntry);
                final View viewById2 = view.findViewById(2131755203);
                int visibility2;
                if (b2) {
                    visibility2 = 8;
                }
                else {
                    visibility2 = 0;
                }
                viewById2.setVisibility(visibility2);
                final View viewById3 = view.findViewById(2131755204);
                int visibility3;
                if (b3) {
                    visibility3 = 0;
                }
                else {
                    visibility3 = 8;
                }
                viewById3.setVisibility(visibility3);
            }
            else {
                view.findViewById(2131755203).setVisibility(8);
                view.findViewById(2131755204).setVisibility(8);
            }
            if (b && slInventoryEntry.isAnimation()) {
                final ImmutableSet set = this.runningAnimations.getData();
                final View viewById4 = view.findViewById(2131755207);
                int visibility4;
                if (set != null && (set.contains(slInventoryEntry.assetUUID) ^ true)) {
                    visibility4 = 0;
                }
                else {
                    visibility4 = 8;
                }
                viewById4.setVisibility(visibility4);
                final View viewById5 = view.findViewById(2131755208);
                int visibility5;
                if (set != null && set.contains(slInventoryEntry.assetUUID)) {
                    visibility5 = 0;
                }
                else {
                    visibility5 = 8;
                }
                viewById5.setVisibility(visibility5);
            }
            else {
                view.findViewById(2131755207).setVisibility(8);
                view.findViewById(2131755208).setVisibility(8);
            }
            if (b && slInventoryEntry.isWearable()) {
                final Object whatIsItemWornOn = slInventoryEntry.whatIsItemWornOn(this.wornAttachments.getData(), this.wornWearables.getData(), false);
                if (whatIsItemWornOn instanceof SLWearableType) {
                    view.findViewById(2131755205).setVisibility(8);
                    if (((SLWearableType)whatIsItemWornOn).isBodyPart()) {
                        view.findViewById(2131755206).setVisibility(8);
                    }
                    else {
                        final boolean canTakeItemOff = slAgentCircuit.getModules().avatarAppearance.canTakeItemOff((SLWearableType)whatIsItemWornOn);
                        final View viewById6 = view.findViewById(2131755206);
                        int visibility6;
                        if (canTakeItemOff) {
                            visibility6 = 0;
                        }
                        else {
                            visibility6 = 8;
                        }
                        viewById6.setVisibility(visibility6);
                    }
                    final View viewById7 = view.findViewById(2131755209);
                    int visibility7;
                    if (view.findViewById(2131755206).getVisibility() == 0) {
                        visibility7 = n2;
                    }
                    else {
                        visibility7 = 0;
                    }
                    viewById7.setVisibility(visibility7);
                }
                else {
                    view.findViewById(2131755206).setVisibility(8);
                    final boolean canWearItem = slAgentCircuit.getModules().avatarAppearance.canWearItem(slInventoryEntry);
                    final View viewById8 = view.findViewById(2131755205);
                    int visibility8;
                    if (canWearItem) {
                        visibility8 = n;
                    }
                    else {
                        visibility8 = 8;
                    }
                    viewById8.setVisibility(visibility8);
                    view.findViewById(2131755209).setVisibility(8);
                }
            }
            else {
                view.findViewById(2131755205).setVisibility(8);
                view.findViewById(2131755206).setVisibility(8);
                view.findViewById(2131755209).setVisibility(8);
            }
        }
        this.updateMenuItems();
    }
    
    private void showPermissions(final int n, final int n2, final int n3, final int n4) {
        final View view = this.getView();
        if (view != null) {
            final TextView textView = (TextView)view.findViewById(n2);
            final TextView textView2 = (TextView)view.findViewById(n3);
            final TextView textView3 = (TextView)view.findViewById(n4);
            if ((0x8000 & n) != 0x0) {
                textView.setPaintFlags(textView.getPaintFlags() & 0xFFFFFFEF);
            }
            else {
                textView.setPaintFlags(textView.getPaintFlags() | 0x10);
            }
            if ((n & 0x4000) != 0x0) {
                textView2.setPaintFlags(textView2.getPaintFlags() & 0xFFFFFFEF);
            }
            else {
                textView2.setPaintFlags(textView2.getPaintFlags() | 0x10);
            }
            if ((n & 0x2000) != 0x0) {
                textView3.setPaintFlags(textView3.getPaintFlags() & 0xFFFFFFEF);
            }
            else {
                textView3.setPaintFlags(textView3.getPaintFlags() | 0x10);
            }
        }
    }
    
    private void showProfile(final UUID uuid) {
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        if (uuid != null && (Objects.equal(uuid, UUIDPool.ZeroUUID) ^ true) && userManager != null) {
            DetailsActivity.showEmbeddedDetails(this.getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(userManager.getUserID(), uuid)));
        }
    }
    
    private void showUserInfo(final UUID uuid, final ChatterNameRetriever chatterNameRetriever, final int n, final int n2, final int n3) {
        final View view = this.getView();
        if (view != null) {
            if (uuid == null || Objects.equal(uuid, UUIDPool.ZeroUUID) || chatterNameRetriever == null) {
                view.findViewById(n).setVisibility(8);
            }
            else {
                view.findViewById(n).setVisibility(0);
                final String resolvedName = chatterNameRetriever.getResolvedName();
                final TextView textView = (TextView)view.findViewById(n2);
                String string;
                if (resolvedName != null) {
                    string = resolvedName;
                }
                else {
                    string = this.getString(2131296712);
                }
                textView.setText((CharSequence)string);
                ((ChatterPicView)view.findViewById(n3)).setChatterID(chatterNameRetriever.chatterID, resolvedName);
            }
        }
    }
    
    private void takeOffObject(final SLInventoryEntry slInventoryEntry) {
        try {
            this.agentCircuit.get().getModules().avatarAppearance.TakeItemOff(slInventoryEntry);
        }
        catch (final SubscriptionData.DataNotReadyException ex) {
            Debug.Warning(ex);
        }
    }
    
    private void updateMenuItems() {
        if (this.menuItemCopy == null || this.menuItemCut == null || this.menuItemShare == null || this.menuItemRename == null || this.menuItemDelete == null) {
            return;
        }
        try {
            this.agentCircuit.assertHasData();
            final SLInventoryEntry slInventoryEntry = this.entrySubscription.get();
            this.menuItemDelete.setVisible(true);
            this.menuItemRename.setVisible((slInventoryEntry.baseMask & slInventoryEntry.ownerMask & 0x4000) != 0x0);
            this.menuItemShare.setVisible((slInventoryEntry.ownerMask & slInventoryEntry.baseMask & 0x2000) != 0x0);
            this.menuItemCut.setVisible(true);
            this.menuItemCopy.setVisible(true);
        }
        catch (final SubscriptionData.DataNotReadyException ex) {
            this.menuItemDelete.setVisible(false);
            this.menuItemRename.setVisible(false);
            this.menuItemShare.setVisible(false);
            this.menuItemCut.setVisible(false);
            this.menuItemCopy.setVisible(false);
        }
    }
    
    private void wearObject(final SLInventoryEntry slInventoryEntry) {
        try {
            this.agentCircuit.get().getModules().avatarAppearance.WearItem(slInventoryEntry, false);
        }
        catch (final SubscriptionData.DataNotReadyException ex) {
            Debug.Warning(ex);
        }
    }
    
    public void onClick(final View view) {
        final SLInventoryEntry slInventoryEntry = this.entrySubscription.getData();
        if (slInventoryEntry != null) {
            switch (view.getId()) {
                case 2131755229: {
                    this.showProfile(slInventoryEntry.ownerUUID);
                    break;
                }
                case 2131755233: {
                    this.showProfile(slInventoryEntry.creatorUUID);
                    break;
                }
                case 2131755237: {
                    this.showProfile(slInventoryEntry.lastOwnerUUID);
                    break;
                }
                case 2131755202: {
                    final int actionDescriptionResId = slInventoryEntry.getActionDescriptionResId();
                    if (actionDescriptionResId >= 0 && this.inventoryFragmentHelper.isActionAllowed(slInventoryEntry, actionDescriptionResId)) {
                        this.inventoryFragmentHelper.PerformInventoryAction(slInventoryEntry, actionDescriptionResId);
                        break;
                    }
                    break;
                }
                case 2131755203: {
                    this.attachObject(slInventoryEntry);
                    break;
                }
                case 2131755207: {
                    this.playAnimation(slInventoryEntry, true);
                    break;
                }
                case 2131755208: {
                    this.playAnimation(slInventoryEntry, false);
                    break;
                }
                case 2131755204: {
                    this.detachObject(slInventoryEntry);
                    break;
                }
                case 2131755205: {
                    this.wearObject(slInventoryEntry);
                    break;
                }
                case 2131755206: {
                    this.takeOffObject(slInventoryEntry);
                    break;
                }
                case 2131755225: {
                    this.showEditPermissionsDialog();
                    break;
                }
            }
        }
    }
    
    @Override
    public void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
        this.setHasOptionsMenu(true);
    }
    
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
        menuInflater.inflate(2131886092, menu);
        this.menuItemDelete = menu.findItem(2131755813);
        this.menuItemRename = menu.findItem(2131755814);
        this.menuItemShare = menu.findItem(2131755815);
        this.menuItemCut = menu.findItem(2131755816);
        this.menuItemCopy = menu.findItem(2131755817);
        this.updateMenuItems();
    }
    
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        final View inflate = layoutInflater.inflate(2130968605, viewGroup, false);
        ((LoadingLayout)inflate.findViewById(2131755197)).setSwipeRefreshLayout((SwipeRefreshLayout)inflate.findViewById(2131755195));
        this.loadableMonitor.setLoadingLayout((LoadingLayout)inflate.findViewById(2131755197), this.getString(2131296744), this.getString(2131296612));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout)inflate.findViewById(2131755195));
        inflate.findViewById(2131755233).setOnClickListener((View$OnClickListener)this);
        inflate.findViewById(2131755229).setOnClickListener((View$OnClickListener)this);
        inflate.findViewById(2131755237).setOnClickListener((View$OnClickListener)this);
        inflate.findViewById(2131755202).setOnClickListener((View$OnClickListener)this);
        inflate.findViewById(2131755225).setOnClickListener((View$OnClickListener)this);
        inflate.findViewById(2131755225).setVisibility(8);
        inflate.findViewById(2131755203).setOnClickListener((View$OnClickListener)this);
        inflate.findViewById(2131755204).setOnClickListener((View$OnClickListener)this);
        inflate.findViewById(2131755205).setOnClickListener((View$OnClickListener)this);
        inflate.findViewById(2131755206).setOnClickListener((View$OnClickListener)this);
        inflate.findViewById(2131755207).setOnClickListener((View$OnClickListener)this);
        inflate.findViewById(2131755208).setOnClickListener((View$OnClickListener)this);
        return inflate;
    }
    
    public void onLoadableDataChanged() {
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        try {
            this.showEntryInfo(this.entrySubscription.get());
            if (userManager != null) {
                this.creatorNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(userManager.getUserID(), this.entrySubscription.get().creatorUUID), this.onNameUpdated, UIThreadExecutor.getInstance());
                this.ownerNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(userManager.getUserID(), this.entrySubscription.get().ownerUUID), this.onNameUpdated, UIThreadExecutor.getInstance());
                this.lastOwnerNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(userManager.getUserID(), this.entrySubscription.get().lastOwnerUUID), this.onNameUpdated, UIThreadExecutor.getInstance());
            }
        }
        catch (final SubscriptionData.DataNotReadyException ex) {
            Debug.Warning(ex);
        }
    }
    
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        Label_0068: {
            if (userManager == null) {
                break Label_0068;
            }
            try {
                final SLInventoryEntry slInventoryEntry = this.entrySubscription.get();
                switch (menuItem.getItemId()) {
                    default: {
                        return super.onOptionsItemSelected(menuItem);
                    }
                    case 2131755813: {
                        this.inventoryFragmentHelper.DeleteInventoryEntry(slInventoryEntry, new _$Lambda$OIe5MtmKyVPF26gruCQoZkxXroQ$2(this));
                        return true;
                    }
                    case 2131755814: {
                        this.inventoryFragmentHelper.RenameInventoryEntry(slInventoryEntry);
                        return true;
                    }
                    case 2131755815: {
                        this.inventoryFragmentHelper.ShareInventoryEntry(slInventoryEntry);
                        return true;
                    }
                    case 2131755816: {
                        userManager.getInventoryManager().copyToClipboard(new InventoryManager.InventoryClipboardEntry(true, slInventoryEntry));
                        Toast.makeText(this.getContext(), 2131296461, 1).show();
                        return true;
                    }
                    case 2131755817: {
                        userManager.getInventoryManager().copyToClipboard(new InventoryManager.InventoryClipboardEntry(false, slInventoryEntry));
                        Toast.makeText(this.getContext(), 2131296461, 1).show();
                        return true;
                    }
                }
            }
            catch (final SubscriptionData.DataNotReadyException ex) {
                Debug.Warning(ex);
                return super.onOptionsItemSelected(menuItem);
            }
        }
    }
    
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.updateMenuItems();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        this.showEntry(UUIDPool.getUUID(this.getArguments().getString("itemUUID")));
    }
    
    public void onStop() {
        this.showEntry(null);
        super.onStop();
    }
    
    @Override
    public void setFragmentArgs(final Intent intent, final Bundle bundle) {
        final UUID activeAgentID = ActivityUtils.getActiveAgentID(intent);
        if (activeAgentID != null) {
            this.getArguments().putString("activeAgentUUID", activeAgentID.toString());
        }
        if (bundle != null) {
            this.getArguments().putAll(bundle);
        }
        if (this.isFragmentStarted()) {
            this.showEntry(UUIDPool.getUUID(this.getArguments().getString("itemUUID")));
        }
    }
}

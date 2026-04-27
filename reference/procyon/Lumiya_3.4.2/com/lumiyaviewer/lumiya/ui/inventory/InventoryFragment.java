// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.inventory;

import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryType;
import android.widget.Toast;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.Menu;
import android.net.Uri;
import java.io.IOException;
import android.provider.MediaStore$Images$Media;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import android.app.ProgressDialog;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.graphics.Bitmap;
import android.content.Context;
import java.util.Iterator;
import java.util.Map;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import com.lumiyaviewer.lumiya.react.Subscribable;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import com.lumiyaviewer.lumiya.Debug;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.os.Bundle;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import javax.annotation.Nullable;
import android.content.Intent;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.lumiyaviewer.lumiya.slproto.modules.SLUserProfiles;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import android.support.v4.app.FragmentActivity;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.PickInfoReply;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.google.common.collect.Table;
import android.view.View$OnClickListener;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import android.widget.AdapterView$OnItemClickListener;
import javax.annotation.Nonnull;
import android.view.MenuItem;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.orm.InventoryEntryList;
import com.lumiyaviewer.lumiya.orm.InventoryQuery;
import com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;

public class InventoryFragment extends FragmentWithTitle implements ReloadableFragment
{
    private static final String FOLDER_ID_KEY = "folderID";
    private static final String IS_MASTER_FRAGMENT = "isMasterFragment";
    private static final String IS_SEARCHING_KEY = "isSearching";
    private static final String SEARCH_STRING_KEY = "searchString";
    public static final String SELECTED_INVENTORY_ENTRY = "selectedInventoryEntry";
    private static final int[] folderActionIds;
    private InventoryFolderAdapter adapter;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit;
    private final SubscriptionData<SubscriptionSingleKey, InventoryManager.InventoryClipboardEntry> clipboardEntry;
    private final SubscriptionData<InventoryQuery, InventoryEntryList> entryList;
    @Nonnull
    private ImmutableMap<Integer, MenuItem> folderActionMenuItems;
    private final SubscriptionData<UUID, Boolean> folderLoading;
    private final InventoryFragmentHelper inventoryFragmentHelper;
    private final AdapterView$OnItemClickListener itemClickListener;
    private final LoadableMonitor loadableMonitor;
    private final View$OnClickListener saveAsClickListener;
    private InventorySaveInfo saveInfo;
    private final SubscriptionData<SubscriptionSingleKey, Boolean> searchRunning;
    private final SubscriptionData<SubscriptionSingleKey, ImmutableMap<UUID, String>> wornAttachments;
    private final SubscriptionData<SubscriptionSingleKey, Table<SLWearableType, UUID, SLWearable>> wornWearables;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-ui-inventory-InventoryActivity$SelectActionSwitchesValues() {
        if (InventoryFragment.-com-lumiyaviewer-lumiya-ui-inventory-InventoryActivity$SelectActionSwitchesValues != null) {
            return InventoryFragment.-com-lumiyaviewer-lumiya-ui-inventory-InventoryActivity$SelectActionSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-ui-inventory-InventoryActivity$SelectActionSwitchesValues = new int[InventoryActivity.SelectAction.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-ui-inventory-InventoryActivity$SelectActionSwitchesValues[InventoryActivity.SelectAction.applyFirstLife.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-ui-inventory-InventoryActivity$SelectActionSwitchesValues[InventoryActivity.SelectAction.applyPickImage.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-ui-inventory-InventoryActivity$SelectActionSwitchesValues[InventoryActivity.SelectAction.applyUserProfile.ordinal()] = 3;
                        return -com-lumiyaviewer-lumiya-ui-inventory-InventoryActivity$SelectActionSwitchesValues = -com-lumiyaviewer-lumiya-ui-inventory-InventoryActivity$SelectActionSwitchesValues;
                    }
                    catch (final NoSuchFieldError noSuchFieldError) {}
                }
                catch (final NoSuchFieldError noSuchFieldError2) {}
            }
            catch (final NoSuchFieldError noSuchFieldError3) {
                continue;
            }
            break;
        }
    }
    
    static {
        folderActionIds = new int[] { 2131755800, 2131755801, 2131755802, 2131755803, 2131755804, 2131755805, 2131755806, 2131755807, 2131755808, 2131755809, 2131755810, 2131755811, 2131755812 };
    }
    
    public InventoryFragment() {
        this.inventoryFragmentHelper = new InventoryFragmentHelper(this);
        this.adapter = null;
        this.saveInfo = null;
        this.folderActionMenuItems = ImmutableMap.of();
        this.entryList = new SubscriptionData<InventoryQuery, InventoryEntryList>(UIThreadExecutor.getInstance(), new _$Lambda$MXulZZBv5zNDEqgJzTmU0EFG_10$6(this));
        this.clipboardEntry = new SubscriptionData<SubscriptionSingleKey, InventoryManager.InventoryClipboardEntry>(UIThreadExecutor.getInstance(), new _$Lambda$MXulZZBv5zNDEqgJzTmU0EFG_10$7(this));
        this.agentCircuit = new SubscriptionData<UUID, SLAgentCircuit>(UIThreadExecutor.getInstance(), new _$Lambda$MXulZZBv5zNDEqgJzTmU0EFG_10$8(this));
        this.folderLoading = new SubscriptionData<UUID, Boolean>(UIThreadExecutor.getInstance(), new _$Lambda$MXulZZBv5zNDEqgJzTmU0EFG_10$9(this));
        this.searchRunning = new SubscriptionData<SubscriptionSingleKey, Boolean>(UIThreadExecutor.getInstance(), new _$Lambda$MXulZZBv5zNDEqgJzTmU0EFG_10$10(this));
        this.wornAttachments = new SubscriptionData<SubscriptionSingleKey, ImmutableMap<UUID, String>>(UIThreadExecutor.getInstance(), new _$Lambda$MXulZZBv5zNDEqgJzTmU0EFG_10$11(this));
        this.wornWearables = new SubscriptionData<SubscriptionSingleKey, Table<SLWearableType, UUID, SLWearable>>(UIThreadExecutor.getInstance(), new _$Lambda$MXulZZBv5zNDEqgJzTmU0EFG_10$12(this));
        this.loadableMonitor = new LoadableMonitor(new Loadable[] { this.entryList });
        this.itemClickListener = (AdapterView$OnItemClickListener)new _$Lambda$MXulZZBv5zNDEqgJzTmU0EFG_10$5(this);
        this.saveAsClickListener = (View$OnClickListener)new _$Lambda$MXulZZBv5zNDEqgJzTmU0EFG_10$4(this);
    }
    
    private void applyPickImage(final SLInventoryEntry slInventoryEntry, final PickInfoReply pickInfoReply) {
        final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
        if (slAgentCircuit != null && pickInfoReply != null) {
            slAgentCircuit.getModules().userProfiles.UpdatePickInfo(pickInfoReply.Data_Field.PickID, pickInfoReply.Data_Field.CreatorID, pickInfoReply.Data_Field.ParcelID, SLMessage.stringFromVariableOEM(pickInfoReply.Data_Field.Name), SLMessage.stringFromVariableUTF(pickInfoReply.Data_Field.Desc), slInventoryEntry.assetUUID, pickInfoReply.Data_Field.PosGlobal, pickInfoReply.Data_Field.SortOrder, pickInfoReply.Data_Field.Enabled);
            final FragmentActivity activity = this.getActivity();
            if (activity != null) {
                activity.finish();
            }
        }
    }
    
    private void applyProfilePic(final SLInventoryEntry slInventoryEntry, final boolean b, final AvatarPropertiesReply avatarPropertiesReply) {
        boolean b2 = true;
        final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
        if (slAgentCircuit != null && avatarPropertiesReply != null) {
            UUID uuid = avatarPropertiesReply.PropertiesData_Field.ImageID;
            final UUID flImageID = avatarPropertiesReply.PropertiesData_Field.FLImageID;
            UUID assetUUID;
            if (b) {
                assetUUID = slInventoryEntry.assetUUID;
            }
            else {
                uuid = slInventoryEntry.assetUUID;
                assetUUID = flImageID;
            }
            final SLUserProfiles userProfiles = slAgentCircuit.getModules().userProfiles;
            final String stringFromVariableUTF = SLMessage.stringFromVariableUTF(avatarPropertiesReply.PropertiesData_Field.AboutText);
            final String stringFromVariableOEM = SLMessage.stringFromVariableOEM(avatarPropertiesReply.PropertiesData_Field.FLAboutText);
            final boolean b3 = (avatarPropertiesReply.PropertiesData_Field.Flags & 0x1) != 0x0;
            if ((avatarPropertiesReply.PropertiesData_Field.Flags & 0x2) == 0x0) {
                b2 = false;
            }
            userProfiles.UpdateAvatarProperties(uuid, assetUUID, stringFromVariableUTF, stringFromVariableOEM, b3, b2, SLMessage.stringFromVariableOEM(avatarPropertiesReply.PropertiesData_Field.ProfileURL));
        }
        final FragmentActivity activity = this.getActivity();
        if (activity != null) {
            activity.finish();
        }
    }
    
    private boolean folderActionsVisible() {
        return !this.isMasterFragment() || (this.isSplitScreen() ^ true);
    }
    
    @Nullable
    private UUID forTransferToUUID() {
        final FragmentActivity activity = this.getActivity();
        if (activity != null) {
            final Intent intent = activity.getIntent();
            if (intent != null) {
                final String stringExtra = intent.getStringExtra("transferToID");
                if (stringExtra != null) {
                    return UUIDPool.getUUID(stringExtra);
                }
            }
        }
        return null;
    }
    
    @Nullable
    private SLAssetType getFilterAssetType() {
        final FragmentActivity activity = this.getActivity();
        if (activity != null) {
            final Intent intent = activity.getIntent();
            if (intent != null) {
                final SLAssetType byType = SLAssetType.getByType(intent.getIntExtra("selectActionAssetType", SLAssetType.AT_UNKNOWN.getTypeCode()));
                if (byType != SLAssetType.AT_UNKNOWN) {
                    return byType;
                }
            }
        }
        return null;
    }
    
    private InventoryQuery getInventoryQuery() {
        boolean b = true;
        final Bundle arguments = this.getArguments();
        final UUID uuid = UUIDPool.getUUID(arguments.getString("folderID"));
        String string;
        if (arguments.getBoolean("isSearching")) {
            string = arguments.getString("searchString");
        }
        else {
            string = null;
        }
        String trim = string;
        if (string != null) {
            trim = string.trim();
        }
        final String emptyToNull = Strings.emptyToNull(trim);
        final int sortOrder = InventoryFragmentHelper.getSortOrder(this.getContext());
        UUID uuid2;
        if (emptyToNull != null) {
            uuid2 = null;
        }
        else {
            uuid2 = uuid;
        }
        final boolean b2 = this.isMasterFragment() || (this.isSplitScreen() ^ true);
        final boolean b3 = !this.isMasterFragment() || (this.isSplitScreen() ^ true);
        if (sortOrder != 0) {
            b = false;
        }
        return InventoryQuery.create(uuid2, emptyToNull, b2, b3, b, this.getFilterAssetType());
    }
    
    @Nullable
    private InventoryActivity.SelectAction getSelectAction() {
        final FragmentActivity activity = this.getActivity();
        if (activity != null) {
            final Intent intent = activity.getIntent();
            if (intent != null) {
                final String stringExtra = intent.getStringExtra("selectAction");
                if (stringExtra != null) {
                    try {
                        return InventoryActivity.SelectAction.valueOf(stringExtra);
                    }
                    catch (final IllegalArgumentException ex) {
                        return null;
                    }
                }
            }
        }
        return null;
    }
    
    @Nullable
    private UserManager getUserManager() {
        final FragmentActivity activity = this.getActivity();
        if (activity != null) {
            return ActivityUtils.getUserManager(activity.getIntent());
        }
        return null;
    }
    
    private boolean isForSelectItem() {
        final FragmentActivity activity = this.getActivity();
        if (activity != null) {
            final Intent intent = activity.getIntent();
            if (intent != null) {
                return intent.getBooleanExtra("forSelectItem", false);
            }
        }
        return false;
    }
    
    private boolean isMasterFragment() {
        final Bundle arguments = this.getArguments();
        return arguments != null && arguments.containsKey("isMasterFragment") && arguments.getBoolean("isMasterFragment");
    }
    
    private boolean isSplitScreen() {
        final FragmentActivity activity = this.getActivity();
        return activity instanceof MasterDetailsActivity && ((MasterDetailsActivity)activity).isSplitScreen();
    }
    
    public static Bundle makeDetailsArguments(final Bundle bundle) {
        final Bundle bundle2 = new Bundle();
        bundle2.putString("folderID", bundle.getString("folderID"));
        bundle2.putString("searchString", bundle.getString("searchString"));
        bundle2.putBoolean("isSearching", bundle.getBoolean("isSearching"));
        return bundle2;
    }
    
    public static Bundle makeSelection(@Nullable final UUID uuid, @Nullable final String s) {
        final Bundle bundle = new Bundle();
        if (uuid != null) {
            bundle.putString("folderID", uuid.toString());
        }
        bundle.putBoolean("isSearching", s != null);
        bundle.putString("searchString", s);
        return bundle;
    }
    
    private void navigateToFolder(final UUID uuid) {
        final Bundle arguments = this.getArguments();
        arguments.putString("folderID", uuid.toString());
        arguments.putBoolean("isSearching", false);
        this.showInventoryList(this.getInventoryQuery());
        final FragmentActivity activity = this.getActivity();
        if (activity instanceof InventoryActivity) {
            ((InventoryActivity)activity).clearSearchMode();
        }
    }
    
    public static Fragment newInstance(final Bundle bundle, final boolean b) {
        final InventoryFragment inventoryFragment = new InventoryFragment();
        final Bundle arguments = new Bundle();
        if (bundle != null) {
            arguments.putAll(bundle);
        }
        arguments.putBoolean("isMasterFragment", b);
        inventoryFragment.setArguments(arguments);
        return inventoryFragment;
    }
    
    private void onAgentCircuit(final SLAgentCircuit slAgentCircuit) {
        this.updateFolderActionItems();
    }
    
    private void onClipboardEntry(final InventoryManager.InventoryClipboardEntry inventoryClipboardEntry) {
        this.updateFolderActionItems();
    }
    
    private void onInventoryEntryList(final InventoryEntryList data) {
        int n = 1;
        Debug.Printf("InventoryFragment (%s): onInventoryEntryList: %d entries", this, data.size());
        if (this.isForSelectItem()) {
            final InventoryActivity.SelectAction selectAction = this.getSelectAction();
            if (selectAction != null) {
                this.setTitle(data.getTitle(), this.getString(selectAction.subtitleResourceId));
            }
            else {
                if (this.forTransferToUUID() == null) {
                    n = 0;
                }
                final String title = data.getTitle();
                String s;
                if (n != 0) {
                    s = this.getString(2131297014);
                }
                else {
                    s = this.getString(2131297012);
                }
                this.setTitle(title, s);
            }
        }
        else {
            this.setTitle(data.getTitle(), null);
        }
        if (this.adapter != null) {
            this.adapter.setData(data);
        }
        this.updateLoadingStatus();
        this.updateFolderActionItems();
    }
    
    private void onLoadingStatusChanged(final Boolean b) {
        this.updateLoadingStatus();
    }
    
    private void onWornAttachmentsChanged(final ImmutableMap<UUID, String> wornAttachments) {
        if (this.adapter != null) {
            this.adapter.setWornAttachments(wornAttachments);
        }
    }
    
    private void onWornWearablesChanged(final Table<SLWearableType, UUID, SLWearable> wornWearables) {
        if (this.adapter != null) {
            this.adapter.setWornWearables(wornWearables);
        }
    }
    
    private void performSelectAction(final SLInventoryEntry slInventoryEntry, final InventoryActivity.SelectAction selectAction) {
        boolean b = true;
        final Bundle bundle = null;
        final FragmentActivity activity = this.getActivity();
        Bundle bundleExtra = bundle;
        if (activity != null) {
            final Intent intent = activity.getIntent();
            bundleExtra = bundle;
            if (intent != null) {
                bundleExtra = intent.getBundleExtra("selectActionParams");
                Debug.Printf("InventoryAction: actionParams %s, has params %b", bundleExtra, intent.hasExtra("selectActionParams"));
            }
        }
        Bundle bundle2;
        if ((bundle2 = bundleExtra) == null) {
            bundle2 = new Bundle();
        }
        switch (-getcom-lumiyaviewer-lumiya-ui-inventory-InventoryActivity$SelectActionSwitchesValues()[selectAction.ordinal()]) {
            case 1:
            case 3: {
                if (selectAction != InventoryActivity.SelectAction.applyFirstLife) {
                    b = false;
                }
                this.applyProfilePic(slInventoryEntry, b, (AvatarPropertiesReply)bundle2.getParcelable("oldProfileData"));
                break;
            }
            case 2: {
                this.applyPickImage(slInventoryEntry, (PickInfoReply)bundle2.getParcelable("oldPickData"));
                break;
            }
        }
    }
    
    private void selectPictureForUpload() {
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        this.startActivityForResult(Intent.createChooser(intent, (CharSequence)"Select Picture"), 10);
    }
    
    private void shareItem(final SLInventoryEntry slInventoryEntry, final UUID uuid) {
        final String s = null;
        if ((slInventoryEntry.baseMask & slInventoryEntry.ownerMask & 0x2000) == 0x0) {
            new AlertDialog$Builder(this.getContext()).setMessage((CharSequence)this.getString(2131296640)).setCancelable(true).setNegativeButton((CharSequence)"Dismiss", (DialogInterface$OnClickListener)new _$Lambda$MXulZZBv5zNDEqgJzTmU0EFG_10$2()).create().show();
        }
        else {
            final FragmentActivity activity = this.getActivity();
            String s2 = s;
            if (activity != null) {
                final Intent intent = activity.getIntent();
                s2 = s;
                if (intent != null) {
                    s2 = intent.getStringExtra("transferToName");
                }
            }
            final String name = slInventoryEntry.name;
            if (s2 == null) {
                s2 = this.getString(2131296712);
            }
            String s3 = this.getString(2131297028, name, s2);
            if ((slInventoryEntry.baseMask & slInventoryEntry.ownerMask & 0x8000) == 0x0) {
                s3 = s3 + "\n" + this.getString(2131296733);
            }
            final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.getContext());
            alertDialog$Builder.setMessage((CharSequence)s3).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$MXulZZBv5zNDEqgJzTmU0EFG_10$16(this, uuid, slInventoryEntry, activity)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$MXulZZBv5zNDEqgJzTmU0EFG_10$3());
            alertDialog$Builder.create().show();
        }
    }
    
    private void showInventoryList(@Nullable final InventoryQuery inventoryQuery) {
        Debug.Printf("InventoryFragment (%s): showInventoryList '%s'", this, inventoryQuery);
        this.entryList.unsubscribe();
        this.agentCircuit.unsubscribe();
        this.clipboardEntry.unsubscribe();
        this.folderLoading.unsubscribe();
        final UserManager userManager = this.getUserManager();
        if (inventoryQuery != null && userManager != null) {
            this.wornAttachments.subscribe((Subscribable<SubscriptionSingleKey, ImmutableMap<UUID, String>>)userManager.getWornAttachmentsPool(), SubscriptionSingleKey.Value);
            this.wornWearables.subscribe((Subscribable<SubscriptionSingleKey, Table<SLWearableType, UUID, SLWearable>>)userManager.getWornWearablesPool(), SubscriptionSingleKey.Value);
            this.agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID());
            final UUID folderId = inventoryQuery.folderId();
            if (folderId != null) {
                this.folderLoading.subscribe(userManager.getInventoryManager().getFolderLoading(), folderId);
            }
            this.searchRunning.subscribe(userManager.getInventoryManager().getSearchRunning(), SubscriptionSingleKey.Value);
            this.clipboardEntry.subscribe(userManager.getInventoryManager().getClipboard(), SubscriptionSingleKey.Value);
            this.entryList.subscribe(userManager.getInventoryManager().getInventoryEntries(), inventoryQuery);
            if (this.isMasterFragment() && this.isSplitScreen()) {
                final FragmentActivity activity = this.getActivity();
                if (activity instanceof MasterDetailsActivity) {
                    ((MasterDetailsActivity)activity).setCurrentDetailsArguments(InventoryFragment.class, makeDetailsArguments(this.getArguments()));
                }
            }
            if (this.adapter != null) {
                this.adapter.setDatabase(userManager.getInventoryManager().getDatabase());
            }
        }
        else {
            this.searchRunning.unsubscribe();
            this.wornAttachments.unsubscribe();
            this.wornWearables.unsubscribe();
            this.adapter.setDatabase(null);
        }
        this.updateLoadingStatus();
    }
    
    private void updateFolderActionItems() {
        boolean b = true;
        SLInventoryEntry folder = null;
        if (!this.folderActionMenuItems.isEmpty()) {
            final InventoryEntryList list = this.entryList.getData();
            if (list != null) {
                folder = list.getFolder();
            }
            if (this.folderActionsVisible() && folder != null) {
                final InventoryManager.InventoryClipboardEntry inventoryClipboardEntry = this.clipboardEntry.getData();
                final boolean hasData = this.agentCircuit.hasData();
                final boolean b2 = folder.parentUUID == null || Objects.equal(folder.parentUUID, UUIDPool.ZeroUUID);
                boolean b3;
                if (folder.typeDefault >= 0) {
                    b3 = true;
                }
                else {
                    b3 = false;
                }
                final boolean b4 = inventoryClipboardEntry != null && (inventoryClipboardEntry.isCut || inventoryClipboardEntry.inventoryEntry.isCopyable());
                if (inventoryClipboardEntry == null) {
                    b = false;
                }
                for (final Map.Entry<Integer, V> entry : this.folderActionMenuItems.entrySet()) {
                    switch (entry.getKey()) {
                        default: {
                            ((MenuItem)entry.getValue()).setVisible(!b2 && (b3 ^ true) && hasData);
                            continue;
                        }
                        case 2131755800: {
                            ((MenuItem)entry.getValue()).setVisible(b2 ^ true);
                            continue;
                        }
                        case 2131755811: {
                            ((MenuItem)entry.getValue()).setVisible(b4 && hasData);
                            continue;
                        }
                        case 2131755812: {
                            ((MenuItem)entry.getValue()).setVisible(b && hasData);
                            continue;
                        }
                        case 2131755801:
                        case 2131755802:
                        case 2131755803:
                        case 2131755804:
                        case 2131755805: {
                            ((MenuItem)entry.getValue()).setVisible(hasData);
                            continue;
                        }
                    }
                }
            }
            else {
                final Iterator<Object> iterator2 = this.folderActionMenuItems.values().iterator();
                while (iterator2.hasNext()) {
                    iterator2.next().setVisible(false);
                }
            }
        }
    }
    
    private void updateLoadingStatus() {
        final Context context = this.getContext();
        if (context != null) {
            final boolean boolean1 = this.getArguments().getBoolean("isSearching");
            int butteryProgressBar;
            if (this.folderLoading.isSubscribed()) {
                final Boolean b = this.folderLoading.getData();
                butteryProgressBar = ((b != null && b) ? 1 : 0);
            }
            else {
                butteryProgressBar = 0;
            }
            if (boolean1) {
                final Boolean b2 = this.searchRunning.getData();
                butteryProgressBar |= ((b2 != null && b2) ? 1 : 0);
            }
            final boolean b3 = this.adapter == null || this.adapter.isEmpty();
            final LoadableMonitor loadableMonitor = this.loadableMonitor;
            boolean extraLoading;
            if (b3) {
                extraLoading = (butteryProgressBar != 0);
            }
            else {
                extraLoading = false;
            }
            loadableMonitor.setExtraLoading(extraLoading);
            final LoadableMonitor loadableMonitor2 = this.loadableMonitor;
            if (b3) {
                butteryProgressBar = (false ? 1 : 0);
            }
            loadableMonitor2.setButteryProgressBar((boolean)(butteryProgressBar != 0));
            String s;
            if (boolean1) {
                if (this.isSplitScreen()) {
                    if (this.isMasterFragment()) {
                        s = context.getString(2131296739);
                    }
                    else {
                        s = context.getString(2131296740);
                    }
                }
                else {
                    s = context.getString(2131296738);
                }
            }
            else if (this.isSplitScreen()) {
                if (this.isMasterFragment()) {
                    s = context.getString(2131296742);
                }
                else {
                    s = context.getString(2131296743);
                }
            }
            else {
                s = context.getString(2131296741);
            }
            this.loadableMonitor.setEmptyMessage(b3, s);
        }
    }
    
    private void uploadImage(String trim, final Bitmap bitmap, final UUID uuid, final UUID uuid2) {
        final String trim2 = Strings.nullToEmpty(trim).trim();
        final int index = trim2.indexOf(46);
        trim = trim2;
        if (index != -1) {
            trim = trim2.substring(0, index).trim();
        }
        final StringBuilder sb = new StringBuilder();
        int i = 0;
        int n = 0;
        while (i < trim.length()) {
            final char char1 = trim.charAt(i);
            if ("0123456789ABCDEFGHIJKLMNOPQRSTUWXYZabcdefghijklmnopqrstuwxyz ".indexOf(char1) != -1) {
                sb.append(char1);
                n = 0;
            }
            else {
                if (n == 0) {
                    sb.append('_');
                }
                n = 1;
            }
            ++i;
        }
        final String trim3 = sb.toString().trim();
        Label_0158: {
            if (!trim3.equals("")) {
                trim = trim3;
                if (!trim3.equals("_")) {
                    break Label_0158;
                }
            }
            trim = "Picture";
        }
        Debug.Printf("Upload: uploading name '%s' bitmap %d x %d", trim, bitmap.getWidth(), bitmap.getHeight());
        new UploadImageAsyncTask(this.getContext(), uuid).execute((Object[])new UploadImageParams[] { new UploadImageParams(trim, bitmap, uuid, uuid2) });
    }
    
    @Override
    public void onActivityCreated(@Nullable final Bundle bundle) {
        super.onActivityCreated(bundle);
        final FragmentActivity activity = this.getActivity();
        if (activity != null) {
            final Intent intent = activity.getIntent();
            if (intent != null) {
                this.saveInfo = (InventorySaveInfo)intent.getParcelableExtra("forSaveInfo");
            }
        }
        final View view = this.getView();
        if (view != null) {
            if (this.saveInfo != null && this.folderActionsVisible()) {
                view.findViewById(2131755449).setVisibility(0);
                ((TextView)view.findViewById(2131755450)).setText((CharSequence)this.getString(2131296631, this.saveInfo.saveItemName));
            }
            else {
                view.findViewById(2131755449).setVisibility(8);
            }
        }
    }
    
    @Override
    public void onActivityResult(int uploadCost, final int n, final Intent intent) {
        final SLAgentCircuit slAgentCircuit = null;
        super.onActivityResult(uploadCost, n, intent);
        if (uploadCost != 10 || n != -1 || intent == null) {
            return;
        }
        final Uri data = intent.getData();
        if (data == null) {
            return;
        }
        while (true) {
            try {
                final String lastPathSegment = data.getLastPathSegment();
                final Bitmap bitmap = MediaStore$Images$Media.getBitmap(this.getContext().getContentResolver(), data);
                final UserManager userManager = this.getUserManager();
                SLAgentCircuit activeAgentCircuit = slAgentCircuit;
                if (userManager != null) {
                    activeAgentCircuit = userManager.getActiveAgentCircuit();
                }
                final UUID uuid = UUIDPool.getUUID(this.getArguments().getString("folderID"));
                if (activeAgentCircuit != null) {
                    uploadCost = activeAgentCircuit.getModules().financialInfo.getUploadCost();
                    if (uploadCost != 0) {
                        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.getContext());
                        alertDialog$Builder.setMessage((CharSequence)this.getString(2131297126, uploadCost)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$MXulZZBv5zNDEqgJzTmU0EFG_10$17(this, lastPathSegment, bitmap, userManager, uuid)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$MXulZZBv5zNDEqgJzTmU0EFG_10());
                        alertDialog$Builder.create().show();
                    }
                    else {
                        this.uploadImage(lastPathSegment, bitmap, userManager.getUserID(), uuid);
                    }
                }
            }
            catch (final IOException ex) {
                Debug.Warning(ex);
                new AlertDialog$Builder(this.getContext()).setMessage((CharSequence)this.getString(2131296543)).setCancelable(true).setNegativeButton((CharSequence)"Dismiss", (DialogInterface$OnClickListener)new _$Lambda$MXulZZBv5zNDEqgJzTmU0EFG_10$1()).create().show();
            }
        }
    }
    
    @Override
    public void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
        this.setHasOptionsMenu(true);
    }
    
    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        if (this.folderActionsVisible()) {
            menuInflater.inflate(2131886091, menu);
            final ImmutableMap.Builder<Integer, MenuItem> builder = new ImmutableMap.Builder<Integer, MenuItem>();
            final int[] folderActionIds = InventoryFragment.folderActionIds;
            for (int i = 0; i < folderActionIds.length; ++i) {
                final int j = folderActionIds[i];
                builder.put(j, menu.findItem(j));
            }
            this.folderActionMenuItems = builder.build();
            this.updateFolderActionItems();
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        Debug.Printf("InventoryFragment: onCreateView", new Object[0]);
        final View inflate = layoutInflater.inflate(2130968658, viewGroup, false);
        this.loadableMonitor.setLoadingLayout((LoadingLayout)inflate.findViewById(2131755197), this.getString(2131296734), this.getString(2131296621));
        this.adapter = new InventoryFolderAdapter(layoutInflater, false);
        ((ListView)inflate.findViewById(2131755448)).setAdapter((ListAdapter)this.adapter);
        ((ListView)inflate.findViewById(2131755448)).setOnItemClickListener(this.itemClickListener);
        inflate.findViewById(2131755451).setOnClickListener(this.saveAsClickListener);
        return inflate;
    }
    
    @EventHandler
    public void onInventorySortOrderChanged(final InventorySortOrderChangedEvent inventorySortOrderChangedEvent) {
        if (this.isFragmentStarted()) {
            this.showInventoryList(this.getInventoryQuery());
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        final UserManager userManager = this.getUserManager();
        final InventoryEntryList list = this.entryList.getData();
        if (list != null && userManager != null) {
            final SLInventoryEntry folder = list.getFolder();
            if (folder != null) {
                final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
                switch (menuItem.getItemId()) {
                    case 2131755800: {
                        if (folder.parentUUID != null && (Objects.equal(folder.parentUUID, UUIDPool.ZeroUUID) ^ true)) {
                            Debug.Printf("InventoryFragment: navigate up to %s (current is %s)", folder.parentUUID, folder.uuid);
                            this.navigateToFolder(folder.parentUUID);
                        }
                        return true;
                    }
                    case 2131755802: {
                        this.inventoryFragmentHelper.CreateNewFolder(folder);
                        return true;
                    }
                    case 2131755806: {
                        this.inventoryFragmentHelper.DeleteInventoryEntry(folder, new _$Lambda$MXulZZBv5zNDEqgJzTmU0EFG_10$15(this, folder));
                        return true;
                    }
                    case 2131755807: {
                        this.inventoryFragmentHelper.RenameInventoryEntry(folder);
                        return true;
                    }
                    case 2131755808: {
                        this.inventoryFragmentHelper.ShareInventoryEntry(folder);
                        return true;
                    }
                    case 2131755809: {
                        userManager.getInventoryManager().copyToClipboard(new InventoryManager.InventoryClipboardEntry(true, folder));
                        Toast.makeText(this.getContext(), 2131296461, 1).show();
                        return true;
                    }
                    case 2131755810: {
                        userManager.getInventoryManager().copyToClipboard(new InventoryManager.InventoryClipboardEntry(false, folder));
                        Toast.makeText(this.getContext(), 2131296461, 1).show();
                        return true;
                    }
                    case 2131755811: {
                        final InventoryManager.InventoryClipboardEntry inventoryClipboardEntry = this.clipboardEntry.getData();
                        if (inventoryClipboardEntry != null && slAgentCircuit != null) {
                            if (inventoryClipboardEntry.isCut) {
                                slAgentCircuit.getModules().inventory.MoveInventoryItem(inventoryClipboardEntry.inventoryEntry, folder);
                                userManager.getInventoryManager().copyToClipboard(null);
                            }
                            else {
                                slAgentCircuit.getModules().inventory.CopyInventoryItem(inventoryClipboardEntry.inventoryEntry, folder);
                            }
                        }
                        return true;
                    }
                    case 2131755812: {
                        final InventoryManager.InventoryClipboardEntry inventoryClipboardEntry2 = this.clipboardEntry.getData();
                        if (inventoryClipboardEntry2 != null && slAgentCircuit != null) {
                            final SLInventory inventory = slAgentCircuit.getModules().inventory;
                            final UUID uuid = inventoryClipboardEntry2.inventoryEntry.uuid;
                            int n;
                            if (inventoryClipboardEntry2.inventoryEntry.isFolder) {
                                n = SLInventoryType.IT_CATEGORY.getTypeCode();
                            }
                            else {
                                n = inventoryClipboardEntry2.inventoryEntry.invType;
                            }
                            int n2;
                            if (inventoryClipboardEntry2.inventoryEntry.isFolder) {
                                n2 = SLAssetType.AT_LINK_FOLDER.getTypeCode();
                            }
                            else {
                                n2 = SLAssetType.AT_LINK.getTypeCode();
                            }
                            inventory.LinkInventoryItem(folder, uuid, n, n2, inventoryClipboardEntry2.inventoryEntry.name, inventoryClipboardEntry2.inventoryEntry.description);
                        }
                        return true;
                    }
                    case 2131755803: {
                        this.inventoryFragmentHelper.CreateNewLandmark(folder);
                        return true;
                    }
                    case 2131755804: {
                        this.getActivity().startActivity(NotecardEditActivity.createIntent(this.getContext(), userManager.getUserID(), folder.uuid, null, false, null, 0));
                        return true;
                    }
                    case 2131755805: {
                        this.selectPictureForUpload();
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.updateFolderActionItems();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getInstance().subscribe(this);
        this.showInventoryList(this.getInventoryQuery());
    }
    
    @Override
    public void onStop() {
        this.showInventoryList(null);
        EventBus.getInstance().unsubscribe(this);
        super.onStop();
    }
    
    @Override
    public void setFragmentArgs(final Intent intent, final Bundle bundle) {
        Debug.Printf("InventoryFragment: setFragmentArgs '%s'", bundle);
        if (bundle != null) {
            this.getArguments().putAll(bundle);
        }
        if (this.isFragmentStarted()) {
            this.showInventoryList(this.getInventoryQuery());
        }
    }
    
    void setSearchString(final String s) {
        final Bundle arguments = this.getArguments();
        arguments.putBoolean("isSearching", s != null);
        arguments.putString("searchString", s);
        if (this.isFragmentStarted()) {
            this.showInventoryList(this.getInventoryQuery());
        }
    }
}

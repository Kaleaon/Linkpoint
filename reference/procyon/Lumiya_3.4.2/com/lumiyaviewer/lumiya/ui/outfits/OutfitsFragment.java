// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.outfits;

import android.content.Intent;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import com.lumiyaviewer.lumiya.ui.inventory.InventorySortOrderChangedEvent;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import android.widget.AdapterView;
import android.content.Context;
import android.view.View;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryFragmentHelper;
import javax.annotation.Nullable;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.Iterator;
import java.util.List;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.google.common.collect.Table;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import android.view.ViewGroup;
import android.widget.AdapterView$OnItemClickListener;
import com.lumiyaviewer.lumiya.orm.InventoryEntryList;
import com.lumiyaviewer.lumiya.orm.InventoryQuery;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryFolderAdapter;
import android.view.View$OnClickListener;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;

public class OutfitsFragment extends FragmentWithTitle implements ReloadableFragment, View$OnClickListener, OnItemCheckboxClickListener
{
    private static final String FOLDER_ID_KEY = "folderID";
    private InventoryFolderAdapter adapter;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit;
    private final SubscriptionData<InventoryQuery, InventoryEntryList> entryList;
    private final SubscriptionData<UUID, Boolean> folderLoading;
    private final AdapterView$OnItemClickListener itemClickListener;
    private ViewGroup listHeader;
    private final Object listHeaderData;
    private final LoadableMonitor loadableMonitor;
    private UUID myOutfitsFolderUUID;
    private final SubscriptionData<InventoryQuery, InventoryEntryList> rootFolderEntryList;
    private final SubscriptionData<SubscriptionSingleKey, ImmutableMap<UUID, String>> wornAttachments;
    private final SubscriptionData<SubscriptionSingleKey, UUID> wornOutfitFolder;
    private final SubscriptionData<SubscriptionSingleKey, Table<SLWearableType, UUID, SLWearable>> wornWearables;
    
    public OutfitsFragment() {
        this.adapter = null;
        this.myOutfitsFolderUUID = null;
        this.entryList = new SubscriptionData<InventoryQuery, InventoryEntryList>(UIThreadExecutor.getInstance(), new _$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs$1(this));
        this.agentCircuit = new SubscriptionData<UUID, SLAgentCircuit>(UIThreadExecutor.getInstance(), new _$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs$2(this));
        this.folderLoading = new SubscriptionData<UUID, Boolean>(UIThreadExecutor.getInstance(), new _$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs$3(this));
        this.wornAttachments = new SubscriptionData<SubscriptionSingleKey, ImmutableMap<UUID, String>>(UIThreadExecutor.getInstance(), new _$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs$4(this));
        this.wornWearables = new SubscriptionData<SubscriptionSingleKey, Table<SLWearableType, UUID, SLWearable>>(UIThreadExecutor.getInstance(), new _$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs$5(this));
        this.rootFolderEntryList = new SubscriptionData<InventoryQuery, InventoryEntryList>(UIThreadExecutor.getInstance(), new _$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs$6(this));
        this.wornOutfitFolder = new SubscriptionData<SubscriptionSingleKey, UUID>(UIThreadExecutor.getInstance(), new _$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs$7(this));
        this.loadableMonitor = new LoadableMonitor(new Loadable[] { this.entryList });
        this.listHeaderData = new Object();
        this.itemClickListener = (AdapterView$OnItemClickListener)new _$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs(this);
    }
    
    private void changeOutfit(final boolean b) {
        final InventoryEntryList list = this.entryList.getData();
        final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
        if (list != null && slAgentCircuit != null) {
            final ImmutableList.Builder<SLInventoryEntry> builder = ImmutableList.builder();
            for (final SLInventoryEntry slInventoryEntry : list) {
                if (!slInventoryEntry.isFolderOrFolderLink()) {
                    builder.add(slInventoryEntry);
                }
            }
            slAgentCircuit.getModules().avatarAppearance.ChangeOutfit(builder.build(), b, list.getFolder());
        }
    }
    
    @Nullable
    private UUID getFolderUUID() {
        String string = null;
        final Bundle arguments = this.getArguments();
        if (arguments != null) {
            string = arguments.getString("folderID");
        }
        return UUIDPool.getUUID(string);
    }
    
    private InventoryQuery getInventoryQuery(@Nullable final UUID uuid) {
        boolean b = false;
        if (InventoryFragmentHelper.getSortOrder(this.getContext()) == 0) {
            b = true;
        }
        return InventoryQuery.create(uuid, null, true, true, b, null);
    }
    
    @Nullable
    private UserManager getUserManager() {
        return ActivityUtils.getUserManager(this.getArguments());
    }
    
    public static Bundle makeSelection(@Nonnull final UUID uuid, @Nullable final UUID uuid2) {
        final Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        if (uuid2 != null) {
            bundle.putString("folderID", uuid2.toString());
        }
        return bundle;
    }
    
    private void navigateToFolder(final UUID uuid) {
        this.getArguments().putString("folderID", uuid.toString());
        this.showInventoryList(uuid);
    }
    
    private void onAgentCircuit(final SLAgentCircuit slAgentCircuit) {
        SLAvatarAppearance avatarAppearance = null;
        if (this.adapter != null) {
            final InventoryFolderAdapter adapter = this.adapter;
            if (slAgentCircuit != null) {
                avatarAppearance = slAgentCircuit.getModules().avatarAppearance;
            }
            adapter.setAvatarAppearance(avatarAppearance);
        }
    }
    
    private void onInventoryEntryList(final InventoryEntryList data) {
        Debug.Printf("InventoryFragment (%s): onInventoryEntryList: %d entries", this, data.size());
        this.setTitle(data.getTitle(), null);
        if (this.adapter != null) {
            this.adapter.setData(data);
        }
        this.updateLoadingStatus();
    }
    
    private void onLoadingStatusChanged(final Boolean b) {
        this.updateLoadingStatus();
    }
    
    private void onRootFolderEntryList(final InventoryEntryList list) {
        if (list != null) {
            for (final SLInventoryEntry slInventoryEntry : list) {
                if (slInventoryEntry.isFolder && slInventoryEntry.typeDefault == 48) {
                    this.myOutfitsFolderUUID = slInventoryEntry.uuid;
                    this.rootFolderEntryList.unsubscribe();
                    if (this.getFolderUUID() == null) {
                        this.showInventoryList(this.getFolderUUID());
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    private void onWornAttachmentsChanged(final ImmutableMap<UUID, String> wornAttachments) {
        if (this.adapter != null) {
            this.adapter.setWornAttachments(wornAttachments);
        }
    }
    
    private void onWornOutfitFolder(final UUID wornOutfitFolder) {
        if (this.adapter != null) {
            this.adapter.setWornOutfitFolder(wornOutfitFolder);
        }
    }
    
    private void onWornWearablesChanged(final Table<SLWearableType, UUID, SLWearable> wornWearables) {
        if (this.adapter != null) {
            this.adapter.setWornWearables(wornWearables);
        }
    }
    
    private void showInventoryList(@Nullable UUID uuid) {
        Debug.Printf("OutfitsNewFragment (%s): showInventoryList '%s'", this, uuid);
        final View view = this.getView();
        this.entryList.unsubscribe();
        this.agentCircuit.unsubscribe();
        this.folderLoading.unsubscribe();
        this.rootFolderEntryList.unsubscribe();
        final UserManager userManager = this.getUserManager();
        if (userManager != null) {
            final InventoryDB database = userManager.getInventoryManager().getDatabase();
            this.wornAttachments.subscribe((Subscribable<SubscriptionSingleKey, ImmutableMap<UUID, String>>)userManager.getWornAttachmentsPool(), SubscriptionSingleKey.Value);
            this.wornWearables.subscribe((Subscribable<SubscriptionSingleKey, Table<SLWearableType, UUID, SLWearable>>)userManager.getWornWearablesPool(), SubscriptionSingleKey.Value);
            this.wornOutfitFolder.subscribe((Subscribable<SubscriptionSingleKey, UUID>)userManager.wornOutfitLink(), SubscriptionSingleKey.Value);
            this.agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID());
            UUID myOutfitsFolderUUID;
            if ((myOutfitsFolderUUID = uuid) == null) {
                myOutfitsFolderUUID = this.myOutfitsFolderUUID;
            }
            Debug.Printf("After checking myoutfits: %s", myOutfitsFolderUUID);
            if ((uuid = myOutfitsFolderUUID) == null) {
                final UUID rootFolder = userManager.getInventoryManager().getRootFolder();
                uuid = myOutfitsFolderUUID;
                if (rootFolder != null) {
                    final SLInventoryEntry specialFolder = database.findSpecialFolder(rootFolder, 48);
                    if (specialFolder != null) {
                        this.myOutfitsFolderUUID = specialFolder.uuid;
                        uuid = specialFolder.uuid;
                        Debug.Printf("Found special folder: %s", uuid);
                    }
                    else {
                        Debug.Printf("Special folder not found", new Object[0]);
                        uuid = myOutfitsFolderUUID;
                    }
                }
            }
            if (uuid != null) {
                this.folderLoading.subscribe(userManager.getInventoryManager().getFolderLoading(), uuid);
                this.entryList.subscribe(userManager.getInventoryManager().getInventoryEntries(), this.getInventoryQuery(uuid));
                if (view != null && this.listHeader != null) {
                    if (Objects.equal(uuid, this.myOutfitsFolderUUID)) {
                        ((TextView)this.listHeader.findViewById(2131755455)).setText(2131296468);
                        ((ImageView)this.listHeader.findViewById(2131755453)).setImageResource(2130837696);
                        view.findViewById(2131755590).setVisibility(8);
                    }
                    else {
                        ((TextView)this.listHeader.findViewById(2131755455)).setText(2131296623);
                        ((ImageView)this.listHeader.findViewById(2131755453)).setImageResource(2130837708);
                        view.findViewById(2131755590).setVisibility(0);
                    }
                    this.listHeader.findViewById(2131755454).setVisibility(8);
                    this.listHeader.setVisibility(0);
                }
            }
            else {
                this.rootFolderEntryList.subscribe(userManager.getInventoryManager().getInventoryEntries(), InventoryQuery.create(null, null, true, false, false, null));
                if (this.listHeader != null) {
                    this.listHeader.setVisibility(8);
                }
                if (view != null) {
                    view.findViewById(2131755590).setVisibility(8);
                }
            }
            if (this.adapter != null) {
                this.adapter.setDatabase(database);
            }
        }
        else {
            this.wornAttachments.unsubscribe();
            this.wornWearables.unsubscribe();
            this.rootFolderEntryList.unsubscribe();
            this.adapter.setDatabase(null);
            this.wornOutfitFolder.unsubscribe();
        }
        this.updateLoadingStatus();
    }
    
    private void updateLoadingStatus() {
        final Context context = this.getContext();
        if (context != null) {
            boolean empty = true;
            boolean butteryProgressBar;
            if (this.folderLoading.isSubscribed()) {
                final Boolean b = this.folderLoading.getData();
                butteryProgressBar = (b != null && b);
            }
            else {
                butteryProgressBar = false;
            }
            if (this.adapter != null) {
                empty = this.adapter.isEmpty();
            }
            this.loadableMonitor.setExtraLoading(empty && butteryProgressBar);
            final LoadableMonitor loadableMonitor = this.loadableMonitor;
            if (empty) {
                butteryProgressBar = false;
            }
            loadableMonitor.setButteryProgressBar(butteryProgressBar);
            this.loadableMonitor.setEmptyMessage(empty, context.getString(2131296741));
        }
    }
    
    public void onClick(final View view) {
        switch (view.getId()) {
            case 2131755591: {
                this.changeOutfit(true);
                break;
            }
            case 2131755592: {
                this.changeOutfit(false);
                break;
            }
        }
    }
    
    @Nullable
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        Debug.Printf("InventoryFragment: onCreateView", new Object[0]);
        final View inflate = layoutInflater.inflate(2130968701, viewGroup, false);
        this.loadableMonitor.setLoadingLayout((LoadingLayout)inflate.findViewById(2131755197), this.getString(2131296734), this.getString(2131296621));
        this.listHeader = (ViewGroup)layoutInflater.inflate(2130968659, (ViewGroup)inflate.findViewById(2131755448), false);
        (this.adapter = new InventoryFolderAdapter(layoutInflater, true)).setOnItemCheckboxClickListener((InventoryFolderAdapter.OnItemCheckboxClickListener)this);
        ((ListView)inflate.findViewById(2131755448)).addHeaderView((View)this.listHeader, this.listHeaderData, true);
        ((ListView)inflate.findViewById(2131755448)).setAdapter((ListAdapter)this.adapter);
        ((ListView)inflate.findViewById(2131755448)).setOnItemClickListener(this.itemClickListener);
        inflate.findViewById(2131755591).setOnClickListener((View$OnClickListener)this);
        inflate.findViewById(2131755592).setOnClickListener((View$OnClickListener)this);
        return inflate;
    }
    
    @EventHandler
    public void onInventorySortOrderChanged(final InventorySortOrderChangedEvent inventorySortOrderChangedEvent) {
        if (this.isFragmentStarted()) {
            this.showInventoryList(this.getFolderUUID());
        }
    }
    
    public void onItemCheckboxClicked(final SLInventoryEntry slInventoryEntry) {
        final UserManager userManager = this.getUserManager();
        final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
        if (slAgentCircuit != null && userManager != null) {
            final SLAvatarAppearance avatarAppearance = slAgentCircuit.getModules().avatarAppearance;
            final InventoryDB database = userManager.getInventoryManager().getDatabase();
            SLInventoryEntry slInventoryEntry2 = slInventoryEntry;
            if (database != null) {
                final SLInventoryEntry resolveLink = database.resolveLink(slInventoryEntry);
                slInventoryEntry2 = slInventoryEntry;
                if (resolveLink != null) {
                    slInventoryEntry2 = resolveLink;
                }
            }
            if (avatarAppearance.isItemWorn(slInventoryEntry2)) {
                if (slInventoryEntry2.isWearable()) {
                    avatarAppearance.TakeItemOff(slInventoryEntry2);
                }
                else {
                    avatarAppearance.DetachInventoryItem(slInventoryEntry2);
                }
            }
            else if (slInventoryEntry2.isWearable()) {
                avatarAppearance.WearItem(slInventoryEntry2, false);
            }
            else {
                avatarAppearance.AttachInventoryItem(slInventoryEntry2, 0, false);
            }
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getInstance().subscribe(this);
        this.showInventoryList(this.getFolderUUID());
    }
    
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
            this.showInventoryList(this.getFolderUUID());
        }
    }
}

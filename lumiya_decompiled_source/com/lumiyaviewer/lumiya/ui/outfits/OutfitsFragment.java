package com.lumiyaviewer.lumiya.ui.outfits;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import com.lumiyaviewer.lumiya.orm.InventoryEntryList;
import com.lumiyaviewer.lumiya.orm.InventoryQuery;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryFolderAdapter;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryFragmentHelper;
import com.lumiyaviewer.lumiya.ui.inventory.InventorySortOrderChangedEvent;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class OutfitsFragment extends FragmentWithTitle implements ReloadableFragment, View.OnClickListener, InventoryFolderAdapter.OnItemCheckboxClickListener {
    private static final String FOLDER_ID_KEY = "folderID";
    private ViewGroup listHeader;
    private InventoryFolderAdapter adapter = null;
    private UUID myOutfitsFolderUUID = null;
    private final SubscriptionData<InventoryQuery, InventoryEntryList> entryList = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.1
        private final /* synthetic */ void $m$0(Object obj) {
            ((OutfitsFragment) this).m706com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref0((InventoryEntryList) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.2
        private final /* synthetic */ void $m$0(Object obj) {
            ((OutfitsFragment) this).m707com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref1((SLAgentCircuit) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private final SubscriptionData<UUID, Boolean> folderLoading = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.3
        private final /* synthetic */ void $m$0(Object obj) {
            ((OutfitsFragment) this).m708com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref2((Boolean) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private final SubscriptionData<SubscriptionSingleKey, ImmutableMap<UUID, String>> wornAttachments = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.4
        private final /* synthetic */ void $m$0(Object obj) {
            ((OutfitsFragment) this).m709com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref3((ImmutableMap) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private final SubscriptionData<SubscriptionSingleKey, Table<SLWearableType, UUID, SLWearable>> wornWearables = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.5
        private final /* synthetic */ void $m$0(Object obj) {
            ((OutfitsFragment) this).m710com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref4((Table) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private final SubscriptionData<InventoryQuery, InventoryEntryList> rootFolderEntryList = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.6
        private final /* synthetic */ void $m$0(Object obj) {
            ((OutfitsFragment) this).m711com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref5((InventoryEntryList) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private final SubscriptionData<SubscriptionSingleKey, UUID> wornOutfitFolder = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs.7
        private final /* synthetic */ void $m$0(Object obj) {
            ((OutfitsFragment) this).m712com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref6((UUID) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.entryList);
    private final Object listHeaderData = new Object();
    private final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() { // from class: com.lumiyaviewer.lumiya.ui.outfits.-$Lambda$oBJjjSxYBPvwKW_FzKQvdarEfUs
        private final /* synthetic */ void $m$0(AdapterView adapterView, View view, int i, long j) {
            ((OutfitsFragment) this).m713lambda$com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragment_13544(adapterView, view, i, j);
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
            $m$0(adapterView, view, i, j);
        }
    };

    private void changeOutfit(boolean z) {
        InventoryEntryList data = this.entryList.getData();
        SLAgentCircuit data2 = this.agentCircuit.getData();
        if (data == null || data2 == null) {
            return;
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        for (SLInventoryEntry sLInventoryEntry : data) {
            if (!sLInventoryEntry.isFolderOrFolderLink()) {
                builder.add((ImmutableList.Builder) sLInventoryEntry);
            }
        }
        data2.getModules().avatarAppearance.ChangeOutfit(builder.build(), z, data.getFolder());
    }

    @Nullable
    private UUID getFolderUUID() {
        Bundle arguments = getArguments();
        return UUIDPool.getUUID(arguments != null ? arguments.getString(FOLDER_ID_KEY) : null);
    }

    private InventoryQuery getInventoryQuery(@Nullable UUID uuid) {
        return InventoryQuery.create(uuid, (String) null, true, true, InventoryFragmentHelper.getSortOrder(getContext()) == 0, (SLAssetType) null);
    }

    @Nullable
    private UserManager getUserManager() {
        return ActivityUtils.getUserManager(getArguments());
    }

    public static Bundle makeSelection(@Nonnull UUID uuid, @Nullable UUID uuid2) {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        if (uuid2 != null) {
            bundle.putString(FOLDER_ID_KEY, uuid2.toString());
        }
        return bundle;
    }

    private void navigateToFolder(UUID uuid) {
        getArguments().putString(FOLDER_ID_KEY, uuid.toString());
        showInventoryList(uuid);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onAgentCircuit */
    public void m707com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref1(SLAgentCircuit sLAgentCircuit) {
        if (this.adapter != null) {
            this.adapter.setAvatarAppearance(sLAgentCircuit != null ? sLAgentCircuit.getModules().avatarAppearance : null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onInventoryEntryList */
    public void m706com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref0(InventoryEntryList inventoryEntryList) {
        Debug.Printf("InventoryFragment (%s): onInventoryEntryList: %d entries", this, Integer.valueOf(inventoryEntryList.size()));
        setTitle(inventoryEntryList.getTitle(), null);
        if (this.adapter != null) {
            this.adapter.setData(inventoryEntryList);
        }
        updateLoadingStatus();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onLoadingStatusChanged */
    public void m708com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref2(Boolean bool) {
        updateLoadingStatus();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onRootFolderEntryList */
    public void m711com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref5(InventoryEntryList inventoryEntryList) {
        if (inventoryEntryList != null) {
            for (SLInventoryEntry sLInventoryEntry : inventoryEntryList) {
                if (sLInventoryEntry.isFolder && sLInventoryEntry.typeDefault == 48) {
                    this.myOutfitsFolderUUID = sLInventoryEntry.uuid;
                    this.rootFolderEntryList.unsubscribe();
                    if (getFolderUUID() == null) {
                        showInventoryList(getFolderUUID());
                        return;
                    }
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onWornAttachmentsChanged */
    public void m709com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref3(ImmutableMap<UUID, String> immutableMap) {
        if (this.adapter != null) {
            this.adapter.setWornAttachments(immutableMap);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onWornOutfitFolder */
    public void m712com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref6(UUID uuid) {
        if (this.adapter != null) {
            this.adapter.setWornOutfitFolder(uuid);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onWornWearablesChanged */
    public void m710com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragmentmthref4(Table<SLWearableType, UUID, SLWearable> table) {
        if (this.adapter != null) {
            this.adapter.setWornWearables(table);
        }
    }

    private void showInventoryList(@Nullable UUID uuid) {
        UUID rootFolder;
        Debug.Printf("OutfitsNewFragment (%s): showInventoryList '%s'", this, uuid);
        View view = getView();
        this.entryList.unsubscribe();
        this.agentCircuit.unsubscribe();
        this.folderLoading.unsubscribe();
        this.rootFolderEntryList.unsubscribe();
        UserManager userManager = getUserManager();
        if (userManager != null) {
            InventoryDB database = userManager.getInventoryManager().getDatabase();
            this.wornAttachments.subscribe(userManager.getWornAttachmentsPool(), SubscriptionSingleKey.Value);
            this.wornWearables.subscribe(userManager.getWornWearablesPool(), SubscriptionSingleKey.Value);
            this.wornOutfitFolder.subscribe(userManager.wornOutfitLink(), SubscriptionSingleKey.Value);
            this.agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID());
            if (uuid == null) {
                uuid = this.myOutfitsFolderUUID;
            }
            Debug.Printf("After checking myoutfits: %s", uuid);
            if (uuid == null && (rootFolder = userManager.getInventoryManager().getRootFolder()) != null) {
                SLInventoryEntry findSpecialFolder = database.findSpecialFolder(rootFolder, 48);
                if (findSpecialFolder != null) {
                    this.myOutfitsFolderUUID = findSpecialFolder.uuid;
                    uuid = findSpecialFolder.uuid;
                    Debug.Printf("Found special folder: %s", uuid);
                } else {
                    Debug.Printf("Special folder not found", new Object[0]);
                }
            }
            if (uuid != null) {
                this.folderLoading.subscribe(userManager.getInventoryManager().getFolderLoading(), uuid);
                this.entryList.subscribe(userManager.getInventoryManager().getInventoryEntries(), getInventoryQuery(uuid));
                if (view != null && this.listHeader != null) {
                    if (Objects.equal(uuid, this.myOutfitsFolderUUID)) {
                        ((TextView) this.listHeader.findViewById(R.id.itemNameTextView)).setText(R.string.current_outfit);
                        ((ImageView) this.listHeader.findViewById(R.id.itemTypeIconView)).setImageResource(R.drawable.inv_folder);
                        view.findViewById(R.id.wear_buttons_layout).setVisibility(8);
                    } else {
                        ((TextView) this.listHeader.findViewById(R.id.itemNameTextView)).setText(R.string.inventory_go_up);
                        ((ImageView) this.listHeader.findViewById(R.id.itemTypeIconView)).setImageResource(R.drawable.inv_up);
                        view.findViewById(R.id.wear_buttons_layout).setVisibility(0);
                    }
                    this.listHeader.findViewById(R.id.itemSubTypeIconView).setVisibility(8);
                    this.listHeader.setVisibility(0);
                }
            } else {
                this.rootFolderEntryList.subscribe(userManager.getInventoryManager().getInventoryEntries(), InventoryQuery.create((UUID) null, (String) null, true, false, false, (SLAssetType) null));
                if (this.listHeader != null) {
                    this.listHeader.setVisibility(8);
                }
                if (view != null) {
                    view.findViewById(R.id.wear_buttons_layout).setVisibility(8);
                }
            }
            if (this.adapter != null) {
                this.adapter.setDatabase(database);
            }
        } else {
            this.wornAttachments.unsubscribe();
            this.wornWearables.unsubscribe();
            this.rootFolderEntryList.unsubscribe();
            this.adapter.setDatabase(null);
            this.wornOutfitFolder.unsubscribe();
        }
        updateLoadingStatus();
    }

    private void updateLoadingStatus() {
        boolean z;
        Context context = getContext();
        if (context != null) {
            if (this.folderLoading.isSubscribed()) {
                Boolean data = this.folderLoading.getData();
                z = data != null ? data.booleanValue() : false;
            } else {
                z = false;
            }
            boolean isEmpty = this.adapter != null ? this.adapter.isEmpty() : true;
            this.loadableMonitor.setExtraLoading(isEmpty ? z : false);
            LoadableMonitor loadableMonitor = this.loadableMonitor;
            if (isEmpty) {
                z = false;
            }
            loadableMonitor.setButteryProgressBar(z);
            this.loadableMonitor.setEmptyMessage(isEmpty, context.getString(R.string.no_inventory_subentries));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragment_13544  reason: not valid java name */
    public /* synthetic */ void m713lambda$com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragment_13544(AdapterView adapterView, View view, int i, long j) {
        SLInventoryEntry folder;
        UserManager userManager = getUserManager();
        if (userManager != null) {
            Object item = adapterView.getAdapter().getItem(i);
            if (item != this.listHeaderData) {
                if (item instanceof SLInventoryEntry) {
                    SLInventoryEntry sLInventoryEntry = (SLInventoryEntry) item;
                    Debug.Printf("Inventory: Item click: item isFolder %b invType %d typeDefault %d assetType %d", Boolean.valueOf(sLInventoryEntry.isFolder), Integer.valueOf(sLInventoryEntry.invType), Integer.valueOf(sLInventoryEntry.typeDefault), Integer.valueOf(sLInventoryEntry.assetType));
                    UUID uuid = (!sLInventoryEntry.isFolder || sLInventoryEntry.uuid == null) ? (sLInventoryEntry.isLink() && sLInventoryEntry.invType == 8) ? sLInventoryEntry.assetUUID : null : sLInventoryEntry.uuid;
                    if (uuid != null) {
                        navigateToFolder(uuid);
                        return;
                    }
                    return;
                }
                return;
            }
            UUID folderUUID = getFolderUUID();
            if (folderUUID == null || Objects.equal(folderUUID, this.myOutfitsFolderUUID)) {
                DetailsActivity.showEmbeddedDetails(getActivity(), CurrentOutfitFragment.class, CurrentOutfitFragment.makeSelection(userManager.getUserID()));
                return;
            }
            InventoryEntryList data = this.entryList.getData();
            if (data == null || (folder = data.getFolder()) == null) {
                return;
            }
            navigateToFolder(folder.parentUUID);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.outfit_folder_wear_button /* 2131755591 */:
                changeOutfit(true);
                return;
            case R.id.outfit_folder_add_button /* 2131755592 */:
                changeOutfit(false);
                return;
            default:
                return;
        }
    }

    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        Debug.Printf("InventoryFragment: onCreateView", new Object[0]);
        View inflate = layoutInflater.inflate(R.layout.outfit_folder, viewGroup, false);
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_folder_selected), getString(R.string.inventory_folder_fail));
        this.listHeader = (ViewGroup) layoutInflater.inflate(R.layout.inventory_item, (ViewGroup) ((ListView) inflate.findViewById(R.id.item_list)), false);
        this.adapter = new InventoryFolderAdapter(layoutInflater, true);
        this.adapter.setOnItemCheckboxClickListener(this);
        ((ListView) inflate.findViewById(R.id.item_list)).addHeaderView(this.listHeader, this.listHeaderData, true);
        ((ListView) inflate.findViewById(R.id.item_list)).setAdapter((ListAdapter) this.adapter);
        ((ListView) inflate.findViewById(R.id.item_list)).setOnItemClickListener(this.itemClickListener);
        inflate.findViewById(R.id.outfit_folder_wear_button).setOnClickListener(this);
        inflate.findViewById(R.id.outfit_folder_add_button).setOnClickListener(this);
        return inflate;
    }

    @EventHandler
    public void onInventorySortOrderChanged(InventorySortOrderChangedEvent inventorySortOrderChangedEvent) {
        if (isFragmentStarted()) {
            showInventoryList(getFolderUUID());
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.inventory.InventoryFolderAdapter.OnItemCheckboxClickListener
    public void onItemCheckboxClicked(SLInventoryEntry sLInventoryEntry) {
        SLInventoryEntry resolveLink;
        UserManager userManager = getUserManager();
        SLAgentCircuit data = this.agentCircuit.getData();
        if (data == null || userManager == null) {
            return;
        }
        SLAvatarAppearance sLAvatarAppearance = data.getModules().avatarAppearance;
        InventoryDB database = userManager.getInventoryManager().getDatabase();
        if (database != null && (resolveLink = database.resolveLink(sLInventoryEntry)) != null) {
            sLInventoryEntry = resolveLink;
        }
        if (sLAvatarAppearance.isItemWorn(sLInventoryEntry)) {
            if (sLInventoryEntry.isWearable()) {
                sLAvatarAppearance.TakeItemOff(sLInventoryEntry);
            } else {
                sLAvatarAppearance.DetachInventoryItem(sLInventoryEntry);
            }
        } else if (sLInventoryEntry.isWearable()) {
            sLAvatarAppearance.WearItem(sLInventoryEntry, false);
        } else {
            sLAvatarAppearance.AttachInventoryItem(sLInventoryEntry, 0, false);
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        EventBus.getInstance().subscribe(this);
        showInventoryList(getFolderUUID());
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onStop() {
        showInventoryList(null);
        EventBus.getInstance().unsubscribe(this);
        super.onStop();
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ReloadableFragment
    public void setFragmentArgs(Intent intent, Bundle bundle) {
        Debug.Printf("InventoryFragment: setFragmentArgs '%s'", bundle);
        if (bundle != null) {
            getArguments().putAll(bundle);
        }
        if (isFragmentStarted()) {
            showInventoryList(getFolderUUID());
        }
    }
}

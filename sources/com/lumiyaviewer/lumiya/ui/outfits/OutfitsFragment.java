// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.outfits;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import com.lumiyaviewer.lumiya.orm.InventoryEntryList;
import com.lumiyaviewer.lumiya.orm.InventoryQuery;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryFolderAdapter;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryFragmentHelper;
import com.lumiyaviewer.lumiya.ui.inventory.InventorySortOrderChangedEvent;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.outfits:
//            CurrentOutfitFragment

public class OutfitsFragment extends FragmentWithTitle
    implements ReloadableFragment, android.view.View.OnClickListener, com.lumiyaviewer.lumiya.ui.inventory.InventoryFolderAdapter.OnItemCheckboxClickListener
{

    private static final String FOLDER_ID_KEY = "folderID";
    private InventoryFolderAdapter adapter;
    private final SubscriptionData agentCircuit = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.oBJjjSxYBPvwKW_FzKQvdarEfUs._cls2(this));
    private final SubscriptionData entryList = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.oBJjjSxYBPvwKW_FzKQvdarEfUs._cls1(this));
    private final SubscriptionData folderLoading = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.oBJjjSxYBPvwKW_FzKQvdarEfUs._cls3(this));
    private final android.widget.AdapterView.OnItemClickListener itemClickListener = new _2D_.Lambda.oBJjjSxYBPvwKW_FzKQvdarEfUs(this);
    private ViewGroup listHeader;
    private final Object listHeaderData = new Object();
    private final LoadableMonitor loadableMonitor;
    private UUID myOutfitsFolderUUID;
    private final SubscriptionData rootFolderEntryList = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.oBJjjSxYBPvwKW_FzKQvdarEfUs._cls6(this));
    private final SubscriptionData wornAttachments = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.oBJjjSxYBPvwKW_FzKQvdarEfUs._cls4(this));
    private final SubscriptionData wornOutfitFolder = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.oBJjjSxYBPvwKW_FzKQvdarEfUs._cls7(this));
    private final SubscriptionData wornWearables = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.oBJjjSxYBPvwKW_FzKQvdarEfUs._cls5(this));

    public OutfitsFragment()
    {
        adapter = null;
        myOutfitsFolderUUID = null;
        loadableMonitor = new LoadableMonitor(new Loadable[] {
            entryList
        });
    }

    private void changeOutfit(boolean flag)
    {
        InventoryEntryList inventoryentrylist = (InventoryEntryList)entryList.getData();
        SLAgentCircuit slagentcircuit = (SLAgentCircuit)agentCircuit.getData();
        if (inventoryentrylist != null && slagentcircuit != null)
        {
            com.google.common.collect.ImmutableList.Builder builder = ImmutableList.builder();
            Iterator iterator = inventoryentrylist.iterator();
            do
            {
                if (!iterator.hasNext())
                {
                    break;
                }
                SLInventoryEntry slinventoryentry = (SLInventoryEntry)iterator.next();
                if (!slinventoryentry.isFolderOrFolderLink())
                {
                    builder.add(slinventoryentry);
                }
            } while (true);
            slagentcircuit.getModules().avatarAppearance.ChangeOutfit(builder.build(), flag, inventoryentrylist.getFolder());
        }
    }

    private UUID getFolderUUID()
    {
        String s = null;
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            s = bundle.getString("folderID");
        }
        return UUIDPool.getUUID(s);
    }

    private InventoryQuery getInventoryQuery(UUID uuid)
    {
        boolean flag = false;
        if (InventoryFragmentHelper.getSortOrder(getContext()) == 0)
        {
            flag = true;
        }
        return InventoryQuery.create(uuid, null, true, true, flag, null);
    }

    private UserManager getUserManager()
    {
        return ActivityUtils.getUserManager(getArguments());
    }

    public static Bundle makeSelection(UUID uuid, UUID uuid1)
    {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        if (uuid1 != null)
        {
            bundle.putString("folderID", uuid1.toString());
        }
        return bundle;
    }

    private void navigateToFolder(UUID uuid)
    {
        getArguments().putString("folderID", uuid.toString());
        showInventoryList(uuid);
    }

    private void onAgentCircuit(SLAgentCircuit slagentcircuit)
    {
        SLAvatarAppearance slavatarappearance = null;
        if (adapter != null)
        {
            InventoryFolderAdapter inventoryfolderadapter = adapter;
            if (slagentcircuit != null)
            {
                slavatarappearance = slagentcircuit.getModules().avatarAppearance;
            }
            inventoryfolderadapter.setAvatarAppearance(slavatarappearance);
        }
    }

    private void onInventoryEntryList(InventoryEntryList inventoryentrylist)
    {
        Debug.Printf("InventoryFragment (%s): onInventoryEntryList: %d entries", new Object[] {
            this, Integer.valueOf(inventoryentrylist.size())
        });
        setTitle(inventoryentrylist.getTitle(), null);
        if (adapter != null)
        {
            adapter.setData(inventoryentrylist);
        }
        updateLoadingStatus();
    }

    private void onLoadingStatusChanged(Boolean boolean1)
    {
        updateLoadingStatus();
    }

    private void onRootFolderEntryList(InventoryEntryList inventoryentrylist)
    {
label0:
        {
            if (inventoryentrylist == null)
            {
                break label0;
            }
            inventoryentrylist = inventoryentrylist.iterator();
            SLInventoryEntry slinventoryentry;
            do
            {
                if (!inventoryentrylist.hasNext())
                {
                    break label0;
                }
                slinventoryentry = (SLInventoryEntry)inventoryentrylist.next();
            } while (!slinventoryentry.isFolder || slinventoryentry.typeDefault != 48);
            myOutfitsFolderUUID = slinventoryentry.uuid;
            rootFolderEntryList.unsubscribe();
            if (getFolderUUID() == null)
            {
                showInventoryList(getFolderUUID());
            }
        }
    }

    private void onWornAttachmentsChanged(ImmutableMap immutablemap)
    {
        if (adapter != null)
        {
            adapter.setWornAttachments(immutablemap);
        }
    }

    private void onWornOutfitFolder(UUID uuid)
    {
        if (adapter != null)
        {
            adapter.setWornOutfitFolder(uuid);
        }
    }

    private void onWornWearablesChanged(Table table)
    {
        if (adapter != null)
        {
            adapter.setWornWearables(table);
        }
    }

    private void showInventoryList(UUID uuid)
    {
        Debug.Printf("OutfitsNewFragment (%s): showInventoryList '%s'", new Object[] {
            this, uuid
        });
        View view = getView();
        entryList.unsubscribe();
        agentCircuit.unsubscribe();
        folderLoading.unsubscribe();
        rootFolderEntryList.unsubscribe();
        UserManager usermanager = getUserManager();
        if (usermanager != null)
        {
            InventoryDB inventorydb = usermanager.getInventoryManager().getDatabase();
            wornAttachments.subscribe(usermanager.getWornAttachmentsPool(), SubscriptionSingleKey.Value);
            wornWearables.subscribe(usermanager.getWornWearablesPool(), SubscriptionSingleKey.Value);
            wornOutfitFolder.subscribe(usermanager.wornOutfitLink(), SubscriptionSingleKey.Value);
            agentCircuit.subscribe(UserManager.agentCircuits(), usermanager.getUserID());
            UUID uuid1 = uuid;
            if (uuid == null)
            {
                uuid1 = myOutfitsFolderUUID;
            }
            Debug.Printf("After checking myoutfits: %s", new Object[] {
                uuid1
            });
            uuid = uuid1;
            if (uuid1 == null)
            {
                UUID uuid2 = usermanager.getInventoryManager().getRootFolder();
                uuid = uuid1;
                if (uuid2 != null)
                {
                    uuid = inventorydb.findSpecialFolder(uuid2, 48);
                    if (uuid != null)
                    {
                        myOutfitsFolderUUID = ((SLInventoryEntry) (uuid)).uuid;
                        uuid = ((SLInventoryEntry) (uuid)).uuid;
                        Debug.Printf("Found special folder: %s", new Object[] {
                            uuid
                        });
                    } else
                    {
                        Debug.Printf("Special folder not found", new Object[0]);
                        uuid = uuid1;
                    }
                }
            }
            if (uuid != null)
            {
                folderLoading.subscribe(usermanager.getInventoryManager().getFolderLoading(), uuid);
                entryList.subscribe(usermanager.getInventoryManager().getInventoryEntries(), getInventoryQuery(uuid));
                if (view != null && listHeader != null)
                {
                    if (Objects.equal(uuid, myOutfitsFolderUUID))
                    {
                        ((TextView)listHeader.findViewById(0x7f1001bf)).setText(0x7f0900d4);
                        ((ImageView)listHeader.findViewById(0x7f1001bd)).setImageResource(0x7f0200c0);
                        view.findViewById(0x7f100246).setVisibility(8);
                    } else
                    {
                        ((TextView)listHeader.findViewById(0x7f1001bf)).setText(0x7f09016f);
                        ((ImageView)listHeader.findViewById(0x7f1001bd)).setImageResource(0x7f0200cc);
                        view.findViewById(0x7f100246).setVisibility(0);
                    }
                    listHeader.findViewById(0x7f1001be).setVisibility(8);
                    listHeader.setVisibility(0);
                }
            } else
            {
                rootFolderEntryList.subscribe(usermanager.getInventoryManager().getInventoryEntries(), InventoryQuery.create(null, null, true, false, false, null));
                if (listHeader != null)
                {
                    listHeader.setVisibility(8);
                }
                if (view != null)
                {
                    view.findViewById(0x7f100246).setVisibility(8);
                }
            }
            if (adapter != null)
            {
                adapter.setDatabase(inventorydb);
            }
        } else
        {
            wornAttachments.unsubscribe();
            wornWearables.unsubscribe();
            rootFolderEntryList.unsubscribe();
            adapter.setDatabase(null);
            wornOutfitFolder.unsubscribe();
        }
        updateLoadingStatus();
    }

    private void updateLoadingStatus()
    {
        Context context = getContext();
        if (context != null)
        {
            boolean flag1 = true;
            boolean flag;
            boolean flag2;
            if (folderLoading.isSubscribed())
            {
                Object obj = (Boolean)folderLoading.getData();
                if (obj != null)
                {
                    flag = ((Boolean) (obj)).booleanValue();
                } else
                {
                    flag = false;
                }
            } else
            {
                flag = false;
            }
            if (adapter != null)
            {
                flag1 = adapter.isEmpty();
            }
            obj = loadableMonitor;
            if (flag1)
            {
                flag2 = flag;
            } else
            {
                flag2 = false;
            }
            ((LoadableMonitor) (obj)).setExtraLoading(flag2);
            obj = loadableMonitor;
            if (flag1)
            {
                flag = false;
            }
            ((LoadableMonitor) (obj)).setButteryProgressBar(flag);
            loadableMonitor.setEmptyMessage(flag1, context.getString(0x7f0901e5));
        }
    }

    void _2D_com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragment_2D_mthref_2D_0(InventoryEntryList inventoryentrylist)
    {
        onInventoryEntryList(inventoryentrylist);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragment_2D_mthref_2D_1(SLAgentCircuit slagentcircuit)
    {
        onAgentCircuit(slagentcircuit);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragment_2D_mthref_2D_2(Boolean boolean1)
    {
        onLoadingStatusChanged(boolean1);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragment_2D_mthref_2D_3(ImmutableMap immutablemap)
    {
        onWornAttachmentsChanged(immutablemap);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragment_2D_mthref_2D_4(Table table)
    {
        onWornWearablesChanged(table);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragment_2D_mthref_2D_5(InventoryEntryList inventoryentrylist)
    {
        onRootFolderEntryList(inventoryentrylist);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragment_2D_mthref_2D_6(UUID uuid)
    {
        onWornOutfitFolder(uuid);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_outfits_OutfitsFragment_13544(AdapterView adapterview, View view, int i, long l)
    {
        view = getUserManager();
        if (view == null) goto _L2; else goto _L1
_L1:
        adapterview = ((AdapterView) (adapterview.getAdapter().getItem(i)));
        if (adapterview != listHeaderData) goto _L4; else goto _L3
_L3:
        adapterview = getFolderUUID();
        if (adapterview != null && !Objects.equal(adapterview, myOutfitsFolderUUID)) goto _L6; else goto _L5
_L5:
        DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/outfits/CurrentOutfitFragment, CurrentOutfitFragment.makeSelection(view.getUserID()));
_L2:
        return;
_L6:
        adapterview = (InventoryEntryList)entryList.getData();
        if (adapterview != null)
        {
            adapterview = adapterview.getFolder();
            if (adapterview != null)
            {
                navigateToFolder(((SLInventoryEntry) (adapterview)).parentUUID);
                return;
            }
        }
        continue; /* Loop/switch isn't completed */
_L4:
        if (adapterview instanceof SLInventoryEntry)
        {
            adapterview = (SLInventoryEntry)adapterview;
            Debug.Printf("Inventory: Item click: item isFolder %b invType %d typeDefault %d assetType %d", new Object[] {
                Boolean.valueOf(((SLInventoryEntry) (adapterview)).isFolder), Integer.valueOf(((SLInventoryEntry) (adapterview)).invType), Integer.valueOf(((SLInventoryEntry) (adapterview)).typeDefault), Integer.valueOf(((SLInventoryEntry) (adapterview)).assetType)
            });
            if (((SLInventoryEntry) (adapterview)).isFolder && ((SLInventoryEntry) (adapterview)).uuid != null)
            {
                adapterview = ((SLInventoryEntry) (adapterview)).uuid;
            } else
            if (adapterview.isLink() && ((SLInventoryEntry) (adapterview)).invType == 8)
            {
                adapterview = ((SLInventoryEntry) (adapterview)).assetUUID;
            } else
            {
                adapterview = null;
            }
            if (adapterview != null)
            {
                navigateToFolder(adapterview);
                return;
            }
        }
        if (true) goto _L2; else goto _L7
_L7:
    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
        default:
            return;

        case 2131755591: 
            changeOutfit(true);
            return;

        case 2131755592: 
            changeOutfit(false);
            break;
        }
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        Debug.Printf("InventoryFragment: onCreateView", new Object[0]);
        viewgroup = layoutinflater.inflate(0x7f04007d, viewgroup, false);
        loadableMonitor.setLoadingLayout((LoadingLayout)viewgroup.findViewById(0x7f1000bd), getString(0x7f0901de), getString(0x7f09016d));
        listHeader = (ViewGroup)layoutinflater.inflate(0x7f040053, (ListView)viewgroup.findViewById(0x7f1001b8), false);
        adapter = new InventoryFolderAdapter(layoutinflater, true);
        adapter.setOnItemCheckboxClickListener(this);
        ((ListView)viewgroup.findViewById(0x7f1001b8)).addHeaderView(listHeader, listHeaderData, true);
        ((ListView)viewgroup.findViewById(0x7f1001b8)).setAdapter(adapter);
        ((ListView)viewgroup.findViewById(0x7f1001b8)).setOnItemClickListener(itemClickListener);
        viewgroup.findViewById(0x7f100247).setOnClickListener(this);
        viewgroup.findViewById(0x7f100248).setOnClickListener(this);
        return viewgroup;
    }

    public void onInventorySortOrderChanged(InventorySortOrderChangedEvent inventorysortorderchangedevent)
    {
        if (isFragmentStarted())
        {
            showInventoryList(getFolderUUID());
        }
    }

    public void onItemCheckboxClicked(SLInventoryEntry slinventoryentry)
    {
        Object obj;
        SLAvatarAppearance slavatarappearance;
label0:
        {
label1:
            {
                obj = getUserManager();
                Object obj1 = (SLAgentCircuit)agentCircuit.getData();
                if (obj1 != null && obj != null)
                {
                    slavatarappearance = ((SLAgentCircuit) (obj1)).getModules().avatarAppearance;
                    obj1 = ((UserManager) (obj)).getInventoryManager().getDatabase();
                    obj = slinventoryentry;
                    if (obj1 != null)
                    {
                        obj1 = ((InventoryDB) (obj1)).resolveLink(slinventoryentry);
                        obj = slinventoryentry;
                        if (obj1 != null)
                        {
                            obj = obj1;
                        }
                    }
                    if (!slavatarappearance.isItemWorn(((SLInventoryEntry) (obj))))
                    {
                        break label0;
                    }
                    if (!((SLInventoryEntry) (obj)).isWearable())
                    {
                        break label1;
                    }
                    slavatarappearance.TakeItemOff(((SLInventoryEntry) (obj)));
                }
                return;
            }
            slavatarappearance.DetachInventoryItem(((SLInventoryEntry) (obj)));
            return;
        }
        if (((SLInventoryEntry) (obj)).isWearable())
        {
            slavatarappearance.WearItem(((SLInventoryEntry) (obj)), false);
            return;
        } else
        {
            slavatarappearance.AttachInventoryItem(((SLInventoryEntry) (obj)), 0, false);
            return;
        }
    }

    public void onStart()
    {
        super.onStart();
        EventBus.getInstance().subscribe(this);
        showInventoryList(getFolderUUID());
    }

    public void onStop()
    {
        showInventoryList(null);
        EventBus.getInstance().unsubscribe(this);
        super.onStop();
    }

    public void setFragmentArgs(Intent intent, Bundle bundle)
    {
        Debug.Printf("InventoryFragment: setFragmentArgs '%s'", new Object[] {
            bundle
        });
        if (bundle != null)
        {
            getArguments().putAll(bundle);
        }
        if (isFragmentStarted())
        {
            showInventoryList(getFolderUUID());
        }
    }
}

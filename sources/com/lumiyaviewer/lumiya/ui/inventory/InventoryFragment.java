// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.orm.InventoryEntryList;
import com.lumiyaviewer.lumiya.orm.InventoryQuery;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryType;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.lumiyaviewer.lumiya.slproto.messages.PickInfoReply;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.SLUserProfiles;
import com.lumiyaviewer.lumiya.slproto.modules.finance.SLFinancialInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.inventory:
//            InventoryFragmentHelper, InventoryActivity, InventoryFolderAdapter, UploadImageParams, 
//            UploadImageAsyncTask, AssetInfoFragment, InventorySaveInfo, NotecardEditActivity, 
//            InventorySortOrderChangedEvent

public class InventoryFragment extends FragmentWithTitle
    implements ReloadableFragment
{

    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_inventory_2D_InventoryActivity$SelectActionSwitchesValues[];
    private static final String FOLDER_ID_KEY = "folderID";
    private static final String IS_MASTER_FRAGMENT = "isMasterFragment";
    private static final String IS_SEARCHING_KEY = "isSearching";
    private static final String SEARCH_STRING_KEY = "searchString";
    public static final String SELECTED_INVENTORY_ENTRY = "selectedInventoryEntry";
    private static final int folderActionIds[] = {
        0x7f100318, 0x7f100319, 0x7f10031a, 0x7f10031b, 0x7f10031c, 0x7f10031d, 0x7f10031e, 0x7f10031f, 0x7f100320, 0x7f100321, 
        0x7f100322, 0x7f100323, 0x7f100324
    };
    private InventoryFolderAdapter adapter;
    private final SubscriptionData agentCircuit = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.MXulZZBv5zNDEqgJzTmU0EFG_2D_10._cls8(this));
    private final SubscriptionData clipboardEntry = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.MXulZZBv5zNDEqgJzTmU0EFG_2D_10._cls7(this));
    private final SubscriptionData entryList = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.MXulZZBv5zNDEqgJzTmU0EFG_2D_10._cls6(this));
    private ImmutableMap folderActionMenuItems;
    private final SubscriptionData folderLoading = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.MXulZZBv5zNDEqgJzTmU0EFG_2D_10._cls9(this));
    private final InventoryFragmentHelper inventoryFragmentHelper = new InventoryFragmentHelper(this);
    private final android.widget.AdapterView.OnItemClickListener itemClickListener = new _2D_.Lambda.MXulZZBv5zNDEqgJzTmU0EFG_2D_10._cls5(this);
    private final LoadableMonitor loadableMonitor;
    private final android.view.View.OnClickListener saveAsClickListener = new _2D_.Lambda.MXulZZBv5zNDEqgJzTmU0EFG_2D_10._cls4(this);
    private InventorySaveInfo saveInfo;
    private final SubscriptionData searchRunning = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.MXulZZBv5zNDEqgJzTmU0EFG_2D_10._cls10(this));
    private final SubscriptionData wornAttachments = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.MXulZZBv5zNDEqgJzTmU0EFG_2D_10._cls11(this));
    private final SubscriptionData wornWearables = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.MXulZZBv5zNDEqgJzTmU0EFG_2D_10._cls12(this));

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_inventory_2D_InventoryActivity$SelectActionSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_inventory_2D_InventoryActivity$SelectActionSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_inventory_2D_InventoryActivity$SelectActionSwitchesValues;
        }
        int ai[] = new int[InventoryActivity.SelectAction.values().length];
        try
        {
            ai[InventoryActivity.SelectAction.applyFirstLife.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[InventoryActivity.SelectAction.applyPickImage.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[InventoryActivity.SelectAction.applyUserProfile.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_inventory_2D_InventoryActivity$SelectActionSwitchesValues = ai;
        return ai;
    }

    public InventoryFragment()
    {
        adapter = null;
        saveInfo = null;
        folderActionMenuItems = ImmutableMap.of();
        loadableMonitor = new LoadableMonitor(new Loadable[] {
            entryList
        });
    }

    private void applyPickImage(SLInventoryEntry slinventoryentry, PickInfoReply pickinforeply)
    {
        SLAgentCircuit slagentcircuit = (SLAgentCircuit)agentCircuit.getData();
        if (slagentcircuit != null && pickinforeply != null)
        {
            slagentcircuit.getModules().userProfiles.UpdatePickInfo(pickinforeply.Data_Field.PickID, pickinforeply.Data_Field.CreatorID, pickinforeply.Data_Field.ParcelID, SLMessage.stringFromVariableOEM(pickinforeply.Data_Field.Name), SLMessage.stringFromVariableUTF(pickinforeply.Data_Field.Desc), slinventoryentry.assetUUID, pickinforeply.Data_Field.PosGlobal, pickinforeply.Data_Field.SortOrder, pickinforeply.Data_Field.Enabled);
            slinventoryentry = getActivity();
            if (slinventoryentry != null)
            {
                slinventoryentry.finish();
            }
        }
    }

    private void applyProfilePic(SLInventoryEntry slinventoryentry, boolean flag, AvatarPropertiesReply avatarpropertiesreply)
    {
        boolean flag1 = true;
        Object obj1 = (SLAgentCircuit)agentCircuit.getData();
        if (obj1 != null && avatarpropertiesreply != null)
        {
            Object obj = avatarpropertiesreply.PropertiesData_Field.ImageID;
            UUID uuid = avatarpropertiesreply.PropertiesData_Field.FLImageID;
            String s;
            if (flag)
            {
                uuid = slinventoryentry.assetUUID;
                slinventoryentry = ((SLInventoryEntry) (obj));
            } else
            {
                slinventoryentry = slinventoryentry.assetUUID;
            }
            obj = ((SLAgentCircuit) (obj1)).getModules().userProfiles;
            obj1 = SLMessage.stringFromVariableUTF(avatarpropertiesreply.PropertiesData_Field.AboutText);
            s = SLMessage.stringFromVariableOEM(avatarpropertiesreply.PropertiesData_Field.FLAboutText);
            if ((avatarpropertiesreply.PropertiesData_Field.Flags & 1) != 0)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            if ((avatarpropertiesreply.PropertiesData_Field.Flags & 2) == 0)
            {
                flag1 = false;
            }
            ((SLUserProfiles) (obj)).UpdateAvatarProperties(slinventoryentry, uuid, ((String) (obj1)), s, flag, flag1, SLMessage.stringFromVariableOEM(avatarpropertiesreply.PropertiesData_Field.ProfileURL));
        }
        slinventoryentry = getActivity();
        if (slinventoryentry != null)
        {
            slinventoryentry.finish();
        }
    }

    private boolean folderActionsVisible()
    {
        if (isMasterFragment())
        {
            return isSplitScreen() ^ true;
        } else
        {
            return true;
        }
    }

    private UUID forTransferToUUID()
    {
        Object obj = getActivity();
        if (obj != null)
        {
            obj = ((FragmentActivity) (obj)).getIntent();
            if (obj != null)
            {
                obj = ((Intent) (obj)).getStringExtra("transferToID");
                if (obj != null)
                {
                    return UUIDPool.getUUID(((String) (obj)));
                }
            }
        }
        return null;
    }

    private SLAssetType getFilterAssetType()
    {
        Object obj = getActivity();
        if (obj != null)
        {
            obj = ((FragmentActivity) (obj)).getIntent();
            if (obj != null)
            {
                obj = SLAssetType.getByType(((Intent) (obj)).getIntExtra("selectActionAssetType", SLAssetType.AT_UNKNOWN.getTypeCode()));
                if (obj != SLAssetType.AT_UNKNOWN)
                {
                    return ((SLAssetType) (obj));
                }
            }
        }
        return null;
    }

    private InventoryQuery getInventoryQuery()
    {
        boolean flag2 = true;
        Object obj = getArguments();
        UUID uuid = UUIDPool.getUUID(((Bundle) (obj)).getString("folderID"));
        String s;
        int i;
        boolean flag;
        boolean flag1;
        if (((Bundle) (obj)).getBoolean("isSearching"))
        {
            obj = ((Bundle) (obj)).getString("searchString");
        } else
        {
            obj = null;
        }
        s = ((String) (obj));
        if (obj != null)
        {
            s = ((String) (obj)).trim();
        }
        s = Strings.emptyToNull(s);
        i = InventoryFragmentHelper.getSortOrder(getContext());
        if (s != null)
        {
            obj = null;
        } else
        {
            obj = uuid;
        }
        if (!isMasterFragment())
        {
            flag = isSplitScreen() ^ true;
        } else
        {
            flag = true;
        }
        if (isMasterFragment())
        {
            flag1 = isSplitScreen() ^ true;
        } else
        {
            flag1 = true;
        }
        if (i != 0)
        {
            flag2 = false;
        }
        return InventoryQuery.create(((UUID) (obj)), s, flag, flag1, flag2, getFilterAssetType());
    }

    private InventoryActivity.SelectAction getSelectAction()
    {
        Object obj = getActivity();
        if (obj != null)
        {
            obj = ((FragmentActivity) (obj)).getIntent();
            if (obj != null)
            {
                obj = ((Intent) (obj)).getStringExtra("selectAction");
                if (obj != null)
                {
                    try
                    {
                        obj = InventoryActivity.SelectAction.valueOf(((String) (obj)));
                    }
                    catch (IllegalArgumentException illegalargumentexception)
                    {
                        return null;
                    }
                    return ((InventoryActivity.SelectAction) (obj));
                }
            }
        }
        return null;
    }

    private UserManager getUserManager()
    {
        FragmentActivity fragmentactivity = getActivity();
        if (fragmentactivity != null)
        {
            return ActivityUtils.getUserManager(fragmentactivity.getIntent());
        } else
        {
            return null;
        }
    }

    private boolean isForSelectItem()
    {
        Object obj = getActivity();
        if (obj != null)
        {
            obj = ((FragmentActivity) (obj)).getIntent();
            if (obj != null)
            {
                return ((Intent) (obj)).getBooleanExtra("forSelectItem", false);
            }
        }
        return false;
    }

    private boolean isMasterFragment()
    {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("isMasterFragment"))
        {
            return bundle.getBoolean("isMasterFragment");
        } else
        {
            return false;
        }
    }

    private boolean isSplitScreen()
    {
        FragmentActivity fragmentactivity = getActivity();
        if (fragmentactivity instanceof MasterDetailsActivity)
        {
            return ((MasterDetailsActivity)fragmentactivity).isSplitScreen();
        } else
        {
            return false;
        }
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_29595(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_30202(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_38573(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_40073(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    public static Bundle makeDetailsArguments(Bundle bundle)
    {
        Bundle bundle1 = new Bundle();
        bundle1.putString("folderID", bundle.getString("folderID"));
        bundle1.putString("searchString", bundle.getString("searchString"));
        bundle1.putBoolean("isSearching", bundle.getBoolean("isSearching"));
        return bundle1;
    }

    public static Bundle makeSelection(UUID uuid, String s)
    {
        Bundle bundle = new Bundle();
        if (uuid != null)
        {
            bundle.putString("folderID", uuid.toString());
        }
        boolean flag;
        if (s != null)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        bundle.putBoolean("isSearching", flag);
        bundle.putString("searchString", s);
        return bundle;
    }

    private void navigateToFolder(UUID uuid)
    {
        Bundle bundle = getArguments();
        bundle.putString("folderID", uuid.toString());
        bundle.putBoolean("isSearching", false);
        showInventoryList(getInventoryQuery());
        uuid = getActivity();
        if (uuid instanceof InventoryActivity)
        {
            ((InventoryActivity)uuid).clearSearchMode();
        }
    }

    public static Fragment newInstance(Bundle bundle, boolean flag)
    {
        InventoryFragment inventoryfragment = new InventoryFragment();
        Bundle bundle1 = new Bundle();
        if (bundle != null)
        {
            bundle1.putAll(bundle);
        }
        bundle1.putBoolean("isMasterFragment", flag);
        inventoryfragment.setArguments(bundle1);
        return inventoryfragment;
    }

    private void onAgentCircuit(SLAgentCircuit slagentcircuit)
    {
        updateFolderActionItems();
    }

    private void onClipboardEntry(com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager.InventoryClipboardEntry inventoryclipboardentry)
    {
        updateFolderActionItems();
    }

    private void onInventoryEntryList(InventoryEntryList inventoryentrylist)
    {
        boolean flag = true;
        Debug.Printf("InventoryFragment (%s): onInventoryEntryList: %d entries", new Object[] {
            this, Integer.valueOf(inventoryentrylist.size())
        });
        if (isForSelectItem())
        {
            InventoryActivity.SelectAction selectaction = getSelectAction();
            if (selectaction != null)
            {
                setTitle(inventoryentrylist.getTitle(), getString(selectaction.subtitleResourceId));
            } else
            {
                String s;
                String s1;
                if (forTransferToUUID() == null)
                {
                    flag = false;
                }
                s1 = inventoryentrylist.getTitle();
                if (flag)
                {
                    s = getString(0x7f0902f6);
                } else
                {
                    s = getString(0x7f0902f4);
                }
                setTitle(s1, s);
            }
        } else
        {
            setTitle(inventoryentrylist.getTitle(), null);
        }
        if (adapter != null)
        {
            adapter.setData(inventoryentrylist);
        }
        updateLoadingStatus();
        updateFolderActionItems();
    }

    private void onLoadingStatusChanged(Boolean boolean1)
    {
        updateLoadingStatus();
    }

    private void onWornAttachmentsChanged(ImmutableMap immutablemap)
    {
        if (adapter != null)
        {
            adapter.setWornAttachments(immutablemap);
        }
    }

    private void onWornWearablesChanged(Table table)
    {
        if (adapter != null)
        {
            adapter.setWornWearables(table);
        }
    }

    private void performSelectAction(SLInventoryEntry slinventoryentry, InventoryActivity.SelectAction selectaction)
    {
        boolean flag = true;
        Bundle bundle1 = null;
        Object obj = getActivity();
        Bundle bundle = bundle1;
        if (obj != null)
        {
            obj = ((FragmentActivity) (obj)).getIntent();
            bundle = bundle1;
            if (obj != null)
            {
                bundle = ((Intent) (obj)).getBundleExtra("selectActionParams");
                Debug.Printf("InventoryAction: actionParams %s, has params %b", new Object[] {
                    bundle, Boolean.valueOf(((Intent) (obj)).hasExtra("selectActionParams"))
                });
            }
        }
        bundle1 = bundle;
        if (bundle == null)
        {
            bundle1 = new Bundle();
        }
        switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_inventory_2D_InventoryActivity$SelectActionSwitchesValues()[selectaction.ordinal()])
        {
        default:
            return;

        case 1: // '\001'
        case 3: // '\003'
            if (selectaction != InventoryActivity.SelectAction.applyFirstLife)
            {
                flag = false;
            }
            applyProfilePic(slinventoryentry, flag, (AvatarPropertiesReply)bundle1.getParcelable("oldProfileData"));
            return;

        case 2: // '\002'
            applyPickImage(slinventoryentry, (PickInfoReply)bundle1.getParcelable("oldPickData"));
            return;
        }
    }

    private void selectPictureForUpload()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
    }

    private void shareItem(SLInventoryEntry slinventoryentry, UUID uuid)
    {
        Object obj = null;
        if ((slinventoryentry.baseMask & slinventoryentry.ownerMask & 0x2000) == 0)
        {
            (new android.app.AlertDialog.Builder(getContext())).setMessage(getString(0x7f090180)).setCancelable(true).setNegativeButton("Dismiss", new _2D_.Lambda.MXulZZBv5zNDEqgJzTmU0EFG_2D_10._cls2()).create().show();
            return;
        }
        FragmentActivity fragmentactivity = getActivity();
        String s = ((String) (obj));
        if (fragmentactivity != null)
        {
            Intent intent = fragmentactivity.getIntent();
            s = ((String) (obj));
            if (intent != null)
            {
                s = intent.getStringExtra("transferToName");
            }
        }
        obj = slinventoryentry.name;
        if (s == null)
        {
            s = getString(0x7f0901c8);
        }
        obj = getString(0x7f090304, new Object[] {
            obj, s
        });
        s = ((String) (obj));
        if ((slinventoryentry.baseMask & slinventoryentry.ownerMask & 0x8000) == 0)
        {
            s = (new StringBuilder()).append(((String) (obj))).append("\n").append(getString(0x7f0901dd)).toString();
        }
        obj = new android.app.AlertDialog.Builder(getContext());
        ((android.app.AlertDialog.Builder) (obj)).setMessage(s).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda.MXulZZBv5zNDEqgJzTmU0EFG_2D_10._cls16(this, uuid, slinventoryentry, fragmentactivity)).setNegativeButton("No", new _2D_.Lambda.MXulZZBv5zNDEqgJzTmU0EFG_2D_10._cls3());
        ((android.app.AlertDialog.Builder) (obj)).create().show();
    }

    private void showInventoryList(InventoryQuery inventoryquery)
    {
        Debug.Printf("InventoryFragment (%s): showInventoryList '%s'", new Object[] {
            this, inventoryquery
        });
        entryList.unsubscribe();
        agentCircuit.unsubscribe();
        clipboardEntry.unsubscribe();
        folderLoading.unsubscribe();
        UserManager usermanager = getUserManager();
        if (inventoryquery != null && usermanager != null)
        {
            wornAttachments.subscribe(usermanager.getWornAttachmentsPool(), SubscriptionSingleKey.Value);
            wornWearables.subscribe(usermanager.getWornWearablesPool(), SubscriptionSingleKey.Value);
            agentCircuit.subscribe(UserManager.agentCircuits(), usermanager.getUserID());
            UUID uuid = inventoryquery.folderId();
            if (uuid != null)
            {
                folderLoading.subscribe(usermanager.getInventoryManager().getFolderLoading(), uuid);
            }
            searchRunning.subscribe(usermanager.getInventoryManager().getSearchRunning(), SubscriptionSingleKey.Value);
            clipboardEntry.subscribe(usermanager.getInventoryManager().getClipboard(), SubscriptionSingleKey.Value);
            entryList.subscribe(usermanager.getInventoryManager().getInventoryEntries(), inventoryquery);
            if (isMasterFragment() && isSplitScreen())
            {
                inventoryquery = getActivity();
                if (inventoryquery instanceof MasterDetailsActivity)
                {
                    ((MasterDetailsActivity)inventoryquery).setCurrentDetailsArguments(com/lumiyaviewer/lumiya/ui/inventory/InventoryFragment, makeDetailsArguments(getArguments()));
                }
            }
            if (adapter != null)
            {
                adapter.setDatabase(usermanager.getInventoryManager().getDatabase());
            }
        } else
        {
            searchRunning.unsubscribe();
            wornAttachments.unsubscribe();
            wornWearables.unsubscribe();
            adapter.setDatabase(null);
        }
        updateLoadingStatus();
    }

    private void updateFolderActionItems()
    {
        Object obj;
        boolean flag1;
        flag1 = true;
        obj = null;
        if (folderActionMenuItems.isEmpty()) goto _L2; else goto _L1
_L1:
        InventoryEntryList inventoryentrylist = (InventoryEntryList)entryList.getData();
        if (inventoryentrylist != null)
        {
            obj = inventoryentrylist.getFolder();
        }
        if (!folderActionsVisible() || obj == null) goto _L4; else goto _L3
_L3:
        Object obj1;
        boolean flag2;
        boolean flag3;
        boolean flag4;
        obj1 = (com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager.InventoryClipboardEntry)clipboardEntry.getData();
        flag2 = agentCircuit.hasData();
        boolean flag;
        if (((SLInventoryEntry) (obj)).parentUUID != null)
        {
            flag4 = Objects.equal(((SLInventoryEntry) (obj)).parentUUID, UUIDPool.ZeroUUID);
        } else
        {
            flag4 = true;
        }
        if (((SLInventoryEntry) (obj)).typeDefault >= 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (obj1 != null)
        {
            if (!((com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager.InventoryClipboardEntry) (obj1)).isCut)
            {
                flag3 = ((com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager.InventoryClipboardEntry) (obj1)).inventoryEntry.isCopyable();
            } else
            {
                flag3 = true;
            }
        } else
        {
            flag3 = false;
        }
        if (obj1 == null)
        {
            flag1 = false;
        }
        obj = folderActionMenuItems.entrySet().iterator();
_L5:
        if (!((Iterator) (obj)).hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        obj1 = (java.util.Map.Entry)((Iterator) (obj)).next();
        switch (((Integer)((java.util.Map.Entry) (obj1)).getKey()).intValue())
        {
        case 2131755806: 
        case 2131755807: 
        case 2131755808: 
        case 2131755809: 
        case 2131755810: 
        default:
            obj1 = (MenuItem)((java.util.Map.Entry) (obj1)).getValue();
            boolean flag5;
            if (!flag4 && flag ^ true)
            {
                flag5 = flag2;
            } else
            {
                flag5 = false;
            }
            ((MenuItem) (obj1)).setVisible(flag5);
            break;

        case 2131755800: 
            ((MenuItem)((java.util.Map.Entry) (obj1)).getValue()).setVisible(flag4 ^ true);
            break;

        case 2131755811: 
            obj1 = (MenuItem)((java.util.Map.Entry) (obj1)).getValue();
            if (flag3)
            {
                flag5 = flag2;
            } else
            {
                flag5 = false;
            }
            ((MenuItem) (obj1)).setVisible(flag5);
            break;

        case 2131755812: 
            obj1 = (MenuItem)((java.util.Map.Entry) (obj1)).getValue();
            if (flag1)
            {
                flag5 = flag2;
            } else
            {
                flag5 = false;
            }
            ((MenuItem) (obj1)).setVisible(flag5);
            break;

        case 2131755801: 
        case 2131755802: 
        case 2131755803: 
        case 2131755804: 
        case 2131755805: 
            ((MenuItem)((java.util.Map.Entry) (obj1)).getValue()).setVisible(flag2);
            break;
        }
        continue; /* Loop/switch isn't completed */
_L4:
        for (Iterator iterator = folderActionMenuItems.values().iterator(); iterator.hasNext(); ((MenuItem)iterator.next()).setVisible(false)) { }
        break; /* Loop/switch isn't completed */
        if (true) goto _L5; else goto _L2
_L2:
    }

    private void updateLoadingStatus()
    {
        Object obj = getContext();
        if (obj != null)
        {
            boolean flag3 = getArguments().getBoolean("isSearching");
            boolean flag;
            boolean flag1;
            boolean flag2;
            if (folderLoading.isSubscribed())
            {
                Object obj1 = (Boolean)folderLoading.getData();
                if (obj1 != null)
                {
                    flag = ((Boolean) (obj1)).booleanValue();
                } else
                {
                    flag = false;
                }
            } else
            {
                flag = false;
            }
            if (flag3)
            {
                obj1 = (Boolean)searchRunning.getData();
                if (obj1 != null)
                {
                    flag1 = ((Boolean) (obj1)).booleanValue();
                } else
                {
                    flag1 = false;
                }
                flag = flag1 | flag;
            }
            if (adapter != null)
            {
                flag1 = adapter.isEmpty();
            } else
            {
                flag1 = true;
            }
            obj1 = loadableMonitor;
            if (flag1)
            {
                flag2 = flag;
            } else
            {
                flag2 = false;
            }
            ((LoadableMonitor) (obj1)).setExtraLoading(flag2);
            obj1 = loadableMonitor;
            if (flag1)
            {
                flag = false;
            }
            ((LoadableMonitor) (obj1)).setButteryProgressBar(flag);
            if (flag3)
            {
                if (isSplitScreen())
                {
                    if (isMasterFragment())
                    {
                        obj = ((Context) (obj)).getString(0x7f0901e3);
                    } else
                    {
                        obj = ((Context) (obj)).getString(0x7f0901e4);
                    }
                } else
                {
                    obj = ((Context) (obj)).getString(0x7f0901e2);
                }
            } else
            if (isSplitScreen())
            {
                if (isMasterFragment())
                {
                    obj = ((Context) (obj)).getString(0x7f0901e6);
                } else
                {
                    obj = ((Context) (obj)).getString(0x7f0901e7);
                }
            } else
            {
                obj = ((Context) (obj)).getString(0x7f0901e5);
            }
            loadableMonitor.setEmptyMessage(flag1, ((String) (obj)));
        }
    }

    private void uploadImage(String s, Bitmap bitmap, UUID uuid, UUID uuid1)
    {
label0:
        {
            Object obj = Strings.nullToEmpty(s).trim();
            int i = ((String) (obj)).indexOf('.');
            s = ((String) (obj));
            if (i != -1)
            {
                s = ((String) (obj)).substring(0, i).trim();
            }
            obj = new StringBuilder();
            int j = 0;
            boolean flag = false;
            while (j < s.length()) 
            {
                char c = s.charAt(j);
                if ("0123456789ABCDEFGHIJKLMNOPQRSTUWXYZabcdefghijklmnopqrstuwxyz ".indexOf(c) != -1)
                {
                    ((StringBuilder) (obj)).append(c);
                    flag = false;
                } else
                {
                    if (!flag)
                    {
                        ((StringBuilder) (obj)).append('_');
                    }
                    flag = true;
                }
                j++;
            }
            obj = ((StringBuilder) (obj)).toString().trim();
            if (!((String) (obj)).equals(""))
            {
                s = ((String) (obj));
                if (!((String) (obj)).equals("_"))
                {
                    break label0;
                }
            }
            s = "Picture";
        }
        Debug.Printf("Upload: uploading name '%s' bitmap %d x %d", new Object[] {
            s, Integer.valueOf(bitmap.getWidth()), Integer.valueOf(bitmap.getHeight())
        });
        s = new UploadImageParams(s, bitmap, uuid, uuid1);
        (new UploadImageAsyncTask(getContext(), uuid)).execute(new UploadImageParams[] {
            s
        });
    }

    void _2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_2D_mthref_2D_0(InventoryEntryList inventoryentrylist)
    {
        onInventoryEntryList(inventoryentrylist);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_2D_mthref_2D_1(com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager.InventoryClipboardEntry inventoryclipboardentry)
    {
        onClipboardEntry(inventoryclipboardentry);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_2D_mthref_2D_2(SLAgentCircuit slagentcircuit)
    {
        onAgentCircuit(slagentcircuit);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_2D_mthref_2D_3(Boolean boolean1)
    {
        onLoadingStatusChanged(boolean1);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_2D_mthref_2D_4(Boolean boolean1)
    {
        onLoadingStatusChanged(boolean1);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_2D_mthref_2D_5(ImmutableMap immutablemap)
    {
        onWornAttachmentsChanged(immutablemap);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_2D_mthref_2D_6(Table table)
    {
        onWornWearablesChanged(table);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_23670(SLInventoryEntry slinventoryentry)
    {
        if (slinventoryentry.parentUUID != null && Objects.equal(slinventoryentry.parentUUID, UUIDPool.ZeroUUID) ^ true)
        {
            navigateToFolder(slinventoryentry.parentUUID);
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_29313(String s, Bitmap bitmap, UserManager usermanager, UUID uuid, DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        uploadImage(s, bitmap, usermanager.getUserID(), uuid);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_32146(AdapterView adapterview, View view, int i, long l)
    {
        view = null;
        if (adapter == null) goto _L2; else goto _L1
_L1:
        SLInventoryEntry slinventoryentry = adapter.getItem(i);
        if (slinventoryentry == null) goto _L2; else goto _L3
_L3:
        Debug.Printf("Inventory: Item click: item isFolder %b invType %d typeDefault %d assetType %d", new Object[] {
            Boolean.valueOf(slinventoryentry.isFolder), Integer.valueOf(slinventoryentry.invType), Integer.valueOf(slinventoryentry.typeDefault), Integer.valueOf(slinventoryentry.assetType)
        });
        if (!slinventoryentry.isFolder || slinventoryentry.uuid == null) goto _L5; else goto _L4
_L4:
        adapterview = slinventoryentry.uuid;
_L11:
        if (adapterview == null) goto _L7; else goto _L6
_L6:
        navigateToFolder(adapterview);
_L2:
        return;
_L5:
        adapterview = view;
        if (slinventoryentry.isLink())
        {
            adapterview = view;
            if (slinventoryentry.invType == 8)
            {
                adapterview = slinventoryentry.assetUUID;
            }
        }
        continue; /* Loop/switch isn't completed */
_L7:
        if (!isForSelectItem())
        {
            break; /* Loop/switch isn't completed */
        }
        adapterview = getSelectAction();
        if (adapterview != null)
        {
            performSelectAction(slinventoryentry, adapterview);
            return;
        }
        adapterview = forTransferToUUID();
        if (adapterview != null)
        {
            shareItem(slinventoryentry, adapterview);
            return;
        }
        adapterview = getActivity();
        if (adapterview != null)
        {
            view = new Intent();
            view.putExtra("selectedInventoryEntry", slinventoryentry);
            adapterview.setResult(-1, view);
            adapterview.finish();
            return;
        }
        if (true) goto _L2; else goto _L8
_L8:
        adapterview = getActivity();
        if (adapterview instanceof InventoryActivity)
        {
            ((InventoryActivity)adapterview).clearSearchMode();
        }
        adapterview = getUserManager();
        if (adapterview == null) goto _L2; else goto _L9
_L9:
        DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/inventory/AssetInfoFragment, AssetInfoFragment.makeSelection(adapterview.getUserID(), slinventoryentry.uuid));
        return;
        if (true) goto _L11; else goto _L10
_L10:
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_39597(UUID uuid, SLInventoryEntry slinventoryentry, FragmentActivity fragmentactivity, DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        dialoginterface = (SLAgentCircuit)agentCircuit.getData();
        if (dialoginterface != null)
        {
            dialoginterface.OfferInventoryItem(uuid, slinventoryentry);
            if (fragmentactivity != null)
            {
                fragmentactivity.finish();
            }
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_40227(View view)
    {
        if (saveInfo != null)
        {
            if (saveInfo.saveType == InventorySaveInfo.InventorySaveType.NotecardItem)
            {
                view = (SLAgentCircuit)agentCircuit.getData();
                UUID uuid = UUIDPool.getUUID(getArguments().getString("folderID"));
                if (view != null && uuid != null)
                {
                    ProgressDialog progressdialog = ProgressDialog.show(getContext(), null, getString(0x7f0901fc), true, true);
                    view.getModules().inventory.CopyInventoryFromNotecard(saveInfo.notecardUUID, saveInfo.saveItemUUID, uuid, new _2D_.Lambda.MXulZZBv5zNDEqgJzTmU0EFG_2D_10._cls13(this, progressdialog));
                }
            } else
            if (saveInfo.saveType == InventorySaveInfo.InventorySaveType.InventoryOffer)
            {
                SLAgentCircuit slagentcircuit = (SLAgentCircuit)agentCircuit.getData();
                view = UUIDPool.getUUID(getArguments().getString("folderID"));
                UserManager usermanager = getUserManager();
                if (slagentcircuit != null && view != null && usermanager != null)
                {
                    Object obj = usermanager.getChatterList().getActiveChattersManager().getChatMessage(saveInfo.inventoryOfferMessageId);
                    if (obj != null)
                    {
                        obj = SLChatEvent.loadFromDatabaseObject(((com.lumiyaviewer.lumiya.dao.ChatMessage) (obj)), usermanager.getUserID());
                        if (obj instanceof SLChatInventoryItemOfferedEvent)
                        {
                            ((SLChatInventoryItemOfferedEvent)obj).onOfferAccepted(getContext(), usermanager, view);
                        }
                    }
                    view = getActivity();
                    if (view != null)
                    {
                        view.finish();
                        return;
                    }
                }
            }
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_40889(ProgressDialog progressdialog)
    {
        UIThreadExecutor.getInstance().execute(new _2D_.Lambda.MXulZZBv5zNDEqgJzTmU0EFG_2D_10._cls14(this, progressdialog));
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_40936(ProgressDialog progressdialog)
    {
        if (progressdialog.isShowing())
        {
            progressdialog.dismiss();
            progressdialog = getActivity();
            if (progressdialog != null)
            {
                progressdialog.finish();
            }
        }
    }

    public void onActivityCreated(Bundle bundle)
    {
label0:
        {
            super.onActivityCreated(bundle);
            bundle = getActivity();
            if (bundle != null)
            {
                bundle = bundle.getIntent();
                if (bundle != null)
                {
                    saveInfo = (InventorySaveInfo)bundle.getParcelableExtra("forSaveInfo");
                }
            }
            bundle = getView();
            if (bundle != null)
            {
                if (saveInfo == null || !folderActionsVisible())
                {
                    break label0;
                }
                bundle.findViewById(0x7f1001b9).setVisibility(0);
                ((TextView)bundle.findViewById(0x7f1001ba)).setText(getString(0x7f090177, new Object[] {
                    saveInfo.saveItemName
                }));
            }
            return;
        }
        bundle.findViewById(0x7f1001b9).setVisibility(8);
    }

    public void onActivityResult(int i, int j, Intent intent)
    {
        UUID uuid;
        uuid = null;
        super.onActivityResult(i, j, intent);
        if (i != 10 || j != -1 || intent == null)
        {
            break MISSING_BLOCK_LABEL_253;
        }
        intent = intent.getData();
        if (intent == null)
        {
            break MISSING_BLOCK_LABEL_253;
        }
        String s;
        Bitmap bitmap;
        UserManager usermanager;
        s = intent.getLastPathSegment();
        bitmap = android.provider.MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), intent);
        usermanager = getUserManager();
        intent = uuid;
        if (usermanager == null)
        {
            break MISSING_BLOCK_LABEL_73;
        }
        intent = usermanager.getActiveAgentCircuit();
        uuid = UUIDPool.getUUID(getArguments().getString("folderID"));
        if (intent == null)
        {
            break MISSING_BLOCK_LABEL_253;
        }
        i = intent.getModules().financialInfo.getUploadCost();
        if (i != 0)
        {
            try
            {
                intent = new android.app.AlertDialog.Builder(getContext());
                intent.setMessage(getString(0x7f090366, new Object[] {
                    Integer.valueOf(i)
                })).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda.MXulZZBv5zNDEqgJzTmU0EFG_2D_10._cls17(this, s, bitmap, usermanager, uuid)).setNegativeButton("No", new _2D_.Lambda.MXulZZBv5zNDEqgJzTmU0EFG_2D_10());
                intent.create().show();
                return;
            }
            // Misplaced declaration of an exception variable
            catch (Intent intent)
            {
                Debug.Warning(intent);
            }
            break MISSING_BLOCK_LABEL_209;
        }
        uploadImage(s, bitmap, usermanager.getUserID(), uuid);
        return;
        (new android.app.AlertDialog.Builder(getContext())).setMessage(getString(0x7f09011f)).setCancelable(true).setNegativeButton("Dismiss", new _2D_.Lambda.MXulZZBv5zNDEqgJzTmU0EFG_2D_10._cls1()).create().show();
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater)
    {
        super.onCreateOptionsMenu(menu, menuinflater);
        if (folderActionsVisible())
        {
            menuinflater.inflate(0x7f12000b, menu);
            menuinflater = new com.google.common.collect.ImmutableMap.Builder();
            int ai[] = folderActionIds;
            int i = 0;
            for (int j = ai.length; i < j; i++)
            {
                int k = ai[i];
                menuinflater.put(Integer.valueOf(k), menu.findItem(k));
            }

            folderActionMenuItems = menuinflater.build();
            updateFolderActionItems();
        }
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        Debug.Printf("InventoryFragment: onCreateView", new Object[0]);
        viewgroup = layoutinflater.inflate(0x7f040052, viewgroup, false);
        loadableMonitor.setLoadingLayout((LoadingLayout)viewgroup.findViewById(0x7f1000bd), getString(0x7f0901de), getString(0x7f09016d));
        adapter = new InventoryFolderAdapter(layoutinflater, false);
        ((ListView)viewgroup.findViewById(0x7f1001b8)).setAdapter(adapter);
        ((ListView)viewgroup.findViewById(0x7f1001b8)).setOnItemClickListener(itemClickListener);
        viewgroup.findViewById(0x7f1001bb).setOnClickListener(saveAsClickListener);
        return viewgroup;
    }

    public void onInventorySortOrderChanged(InventorySortOrderChangedEvent inventorysortorderchangedevent)
    {
        if (isFragmentStarted())
        {
            showInventoryList(getInventoryQuery());
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        Object obj;
        Object obj1;
        obj1 = getUserManager();
        obj = (InventoryEntryList)entryList.getData();
        if (obj == null || obj1 == null) goto _L2; else goto _L1
_L1:
        obj = ((InventoryEntryList) (obj)).getFolder();
        if (obj == null) goto _L2; else goto _L3
_L3:
        Object obj2 = (SLAgentCircuit)agentCircuit.getData();
        menuitem.getItemId();
        JVM INSTR tableswitch 2131755800 2131755812: default 116
    //                   2131755800 118
    //                   2131755801 116
    //                   2131755802 174
    //                   2131755803 482
    //                   2131755804 492
    //                   2131755805 520
    //                   2131755806 184
    //                   2131755807 203
    //                   2131755808 213
    //                   2131755809 223
    //                   2131755810 255
    //                   2131755811 287
    //                   2131755812 358;
           goto _L2 _L4 _L2 _L5 _L6 _L7 _L8 _L9 _L10 _L11 _L12 _L13 _L14 _L15
_L2:
        return false;
_L4:
        if (((SLInventoryEntry) (obj)).parentUUID != null && Objects.equal(((SLInventoryEntry) (obj)).parentUUID, UUIDPool.ZeroUUID) ^ true)
        {
            Debug.Printf("InventoryFragment: navigate up to %s (current is %s)", new Object[] {
                ((SLInventoryEntry) (obj)).parentUUID, ((SLInventoryEntry) (obj)).uuid
            });
            navigateToFolder(((SLInventoryEntry) (obj)).parentUUID);
        }
        return true;
_L5:
        inventoryFragmentHelper.CreateNewFolder(((SLInventoryEntry) (obj)));
        return true;
_L9:
        inventoryFragmentHelper.DeleteInventoryEntry(((SLInventoryEntry) (obj)), new _2D_.Lambda.MXulZZBv5zNDEqgJzTmU0EFG_2D_10._cls15(this, obj));
        return true;
_L10:
        inventoryFragmentHelper.RenameInventoryEntry(((SLInventoryEntry) (obj)));
        return true;
_L11:
        inventoryFragmentHelper.ShareInventoryEntry(((SLInventoryEntry) (obj)));
        return true;
_L12:
        ((UserManager) (obj1)).getInventoryManager().copyToClipboard(new com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager.InventoryClipboardEntry(true, ((SLInventoryEntry) (obj))));
        Toast.makeText(getContext(), 0x7f0900cd, 1).show();
        return true;
_L13:
        ((UserManager) (obj1)).getInventoryManager().copyToClipboard(new com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager.InventoryClipboardEntry(false, ((SLInventoryEntry) (obj))));
        Toast.makeText(getContext(), 0x7f0900cd, 1).show();
        return true;
_L14:
        menuitem = (com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager.InventoryClipboardEntry)clipboardEntry.getData();
        if (menuitem == null || obj2 == null) goto _L17; else goto _L16
_L16:
        if (!((com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager.InventoryClipboardEntry) (menuitem)).isCut) goto _L19; else goto _L18
_L18:
        ((SLAgentCircuit) (obj2)).getModules().inventory.MoveInventoryItem(((com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager.InventoryClipboardEntry) (menuitem)).inventoryEntry, ((SLInventoryEntry) (obj)));
        ((UserManager) (obj1)).getInventoryManager().copyToClipboard(null);
_L17:
        return true;
_L19:
        ((SLAgentCircuit) (obj2)).getModules().inventory.CopyInventoryItem(((com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager.InventoryClipboardEntry) (menuitem)).inventoryEntry, ((SLInventoryEntry) (obj)));
        return true;
_L15:
        menuitem = (com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager.InventoryClipboardEntry)clipboardEntry.getData();
        if (menuitem != null && obj2 != null)
        {
            obj1 = ((SLAgentCircuit) (obj2)).getModules().inventory;
            obj2 = ((com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager.InventoryClipboardEntry) (menuitem)).inventoryEntry.uuid;
            int i;
            int j;
            if (((com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager.InventoryClipboardEntry) (menuitem)).inventoryEntry.isFolder)
            {
                i = SLInventoryType.IT_CATEGORY.getTypeCode();
            } else
            {
                i = ((com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager.InventoryClipboardEntry) (menuitem)).inventoryEntry.invType;
            }
            if (((com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager.InventoryClipboardEntry) (menuitem)).inventoryEntry.isFolder)
            {
                j = SLAssetType.AT_LINK_FOLDER.getTypeCode();
            } else
            {
                j = SLAssetType.AT_LINK.getTypeCode();
            }
            ((SLInventory) (obj1)).LinkInventoryItem(((SLInventoryEntry) (obj)), ((UUID) (obj2)), i, j, ((com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager.InventoryClipboardEntry) (menuitem)).inventoryEntry.name, ((com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager.InventoryClipboardEntry) (menuitem)).inventoryEntry.description);
        }
        return true;
_L6:
        inventoryFragmentHelper.CreateNewLandmark(((SLInventoryEntry) (obj)));
        return true;
_L7:
        getActivity().startActivity(NotecardEditActivity.createIntent(getContext(), ((UserManager) (obj1)).getUserID(), ((SLInventoryEntry) (obj)).uuid, null, false, null, 0));
        return true;
_L8:
        selectPictureForUpload();
        return true;
    }

    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        updateFolderActionItems();
    }

    public void onStart()
    {
        super.onStart();
        EventBus.getInstance().subscribe(this);
        showInventoryList(getInventoryQuery());
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
            showInventoryList(getInventoryQuery());
        }
    }

    void setSearchString(String s)
    {
        Bundle bundle = getArguments();
        boolean flag;
        if (s != null)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        bundle.putBoolean("isSearching", flag);
        bundle.putString("searchString", s);
        if (isFragmentStarted())
        {
            showInventoryList(getInventoryQuery());
        }
    }

}

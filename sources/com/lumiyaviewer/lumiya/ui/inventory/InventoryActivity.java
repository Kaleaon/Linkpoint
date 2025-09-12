// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.inventory:
//            InventoryFragmentHelper, InventoryFragment, InventorySortOrderChangedEvent, InventorySaveInfo

public class InventoryActivity extends MasterDetailsActivity
{
    public static final class SelectAction extends Enum
    {

        private static final SelectAction $VALUES[];
        public static final SelectAction applyFirstLife;
        public static final SelectAction applyPickImage;
        public static final SelectAction applyUserProfile;
        public final int subtitleResourceId;

        public static SelectAction valueOf(String s)
        {
            return (SelectAction)Enum.valueOf(com/lumiyaviewer/lumiya/ui/inventory/InventoryActivity$SelectAction, s);
        }

        public static SelectAction[] values()
        {
            return $VALUES;
        }

        static 
        {
            applyUserProfile = new SelectAction("applyUserProfile", 0, 0x7f0902f8);
            applyFirstLife = new SelectAction("applyFirstLife", 1, 0x7f0902f8);
            applyPickImage = new SelectAction("applyPickImage", 2, 0x7f0902f8);
            $VALUES = (new SelectAction[] {
                applyUserProfile, applyFirstLife, applyPickImage
            });
        }

        private SelectAction(String s, int i, int j)
        {
            super(s, i);
            subtitleResourceId = j;
        }
    }


    private static final String INITIAL_FOLDER_ID_TAG = "folderID";
    private static final String NAME_FILTER_TAG = "nameFilter";
    static final String SAVE_INFO_INTENT_TAG = "forSaveInfo";
    private static final String SEARCH_ACTIVE_TAG = "searchActive";
    static final String SELECT_ACTION_ASSET_TYPE = "selectActionAssetType";
    static final String SELECT_ACTION_INTENT_TAG = "selectAction";
    static final String SELECT_ACTION_PARAMS_TAG = "selectActionParams";
    static final String SELECT_ITEM_INTENT_TAG = "forSelectItem";
    static final String TRANSFER_TO_INTENT_TAG = "transferToID";
    static final String TRANSFER_TO_NAME_TAG = "transferToName";
    private final FragmentActivityFactory InventoryDetailsFragmentFactory = new FragmentActivityFactory() {

        final InventoryActivity this$0;

        public Intent createIntent(Context context, Bundle bundle)
        {
            context = new Intent(context, com/lumiyaviewer/lumiya/ui/inventory/InventoryActivity);
            context.putExtra("selection", bundle);
            return context;
        }

        public Class getFragmentClass()
        {
            return com/lumiyaviewer/lumiya/ui/inventory/InventoryFragment;
        }

            
            {
                this$0 = InventoryActivity.this;
                super();
            }
    };
    private boolean activityStarted;
    private String fragmentSearchString;
    private String nameFilter;
    private boolean searchActive;
    private MenuItem searchMenuItem;
    private final SubscriptionData searchProcess = new SubscriptionData(UIThreadExecutor.getInstance());

    static String _2D_set0(InventoryActivity inventoryactivity, String s)
    {
        inventoryactivity.nameFilter = s;
        return s;
    }

    static boolean _2D_set1(InventoryActivity inventoryactivity, boolean flag)
    {
        inventoryactivity.searchActive = flag;
        return flag;
    }

    static void _2D_wrap0(InventoryActivity inventoryactivity)
    {
        inventoryactivity.updateSearchAction();
    }

    public InventoryActivity()
    {
        searchMenuItem = null;
        searchActive = false;
        activityStarted = false;
        nameFilter = null;
        fragmentSearchString = null;
    }

    public static Intent makeFolderIntent(Context context, UUID uuid, UUID uuid1)
    {
        context = new Intent(context, com/lumiyaviewer/lumiya/ui/inventory/InventoryActivity);
        context.putExtra("activeAgentUUID", uuid.toString());
        context.putExtra("folderID", uuid1.toString());
        return context;
    }

    public static Intent makeSaveItemIntent(Context context, UUID uuid, InventorySaveInfo inventorysaveinfo)
    {
        context = new Intent(context, com/lumiyaviewer/lumiya/ui/inventory/InventoryActivity);
        context.putExtra("activeAgentUUID", uuid.toString());
        context.putExtra("forSaveInfo", inventorysaveinfo);
        return context;
    }

    public static Intent makeSelectActionIntent(Context context, UUID uuid, SelectAction selectaction, Bundle bundle, SLAssetType slassettype)
    {
        context = new Intent(context, com/lumiyaviewer/lumiya/ui/inventory/InventoryActivity);
        context.putExtra("activeAgentUUID", uuid.toString());
        context.putExtra("forSelectItem", true);
        context.putExtra("selectAction", selectaction.toString());
        context.putExtra("selectActionParams", bundle);
        if (slassettype != null)
        {
            context.putExtra("selectActionAssetType", slassettype.getTypeCode());
        }
        return context;
    }

    public static Intent makeSelectIntent(Context context, UUID uuid)
    {
        context = new Intent(context, com/lumiyaviewer/lumiya/ui/inventory/InventoryActivity);
        context.putExtra("activeAgentUUID", uuid.toString());
        context.putExtra("forSelectItem", true);
        return context;
    }

    public static Intent makeTransferIntent(Context context, UUID uuid, UUID uuid1, String s)
    {
        context = new Intent(context, com/lumiyaviewer/lumiya/ui/inventory/InventoryActivity);
        context.putExtra("activeAgentUUID", uuid.toString());
        context.putExtra("forSelectItem", true);
        context.putExtra("transferToID", uuid1.toString());
        if (s != null)
        {
            context.putExtra("transferToName", s);
        }
        return context;
    }

    private void selectSortOrder()
    {
        int i = InventoryFragmentHelper.getSortOrder(this);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(0x7f090311);
        _2D_.Lambda.Tc22ivDU79Y83KauKGybv49CW7A tc22ivdu79y83kaukgybv49cw7a = new _2D_.Lambda.Tc22ivDU79Y83KauKGybv49CW7A(i, this);
        builder.setSingleChoiceItems(new CharSequence[] {
            "Newest first", "Alphabetical"
        }, i, tc22ivdu79y83kaukgybv49cw7a);
        builder.create().show();
    }

    private void updateSearchAction()
    {
        Object obj;
        if (activityStarted && searchActive)
        {
            obj = ActivityUtils.getUserManager(getIntent());
            if (obj != null)
            {
                searchProcess.subscribe(((UserManager) (obj)).getInventoryManager().getSearchProcess(), SubscriptionSingleKey.Value);
            }
            obj = Strings.nullToEmpty(nameFilter).trim();
        } else
        {
            searchProcess.unsubscribe();
            obj = "";
        }
        if (!Objects.equal(fragmentSearchString, obj))
        {
            fragmentSearchString = ((String) (obj));
            Fragment fragment = getSupportFragmentManager().findFragmentById(0x7f100286);
            if (fragment instanceof InventoryFragment)
            {
                ((InventoryFragment)fragment).setSearchString(Strings.emptyToNull(((String) (obj))));
            }
        }
    }

    void clearSearchMode()
    {
        searchActive = false;
        if (searchMenuItem != null)
        {
            MenuItemCompat.collapseActionView(searchMenuItem);
        }
    }

    protected FragmentActivityFactory getDetailsFragmentFactory()
    {
        return InventoryDetailsFragmentFactory;
    }

    protected Bundle getNewDetailsFragmentArguments(Bundle bundle, Bundle bundle1)
    {
        if (bundle != null)
        {
            return InventoryFragment.makeDetailsArguments(bundle);
        } else
        {
            return super.getNewDetailsFragmentArguments(null, bundle1);
        }
    }

    protected boolean isAlwaysImplicitFragment(Class class1)
    {
        return class1.equals(com/lumiyaviewer/lumiya/ui/inventory/InventoryFragment);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_InventoryActivity_10944(int i, DialogInterface dialoginterface, int j)
    {
        if (i != j)
        {
            InventoryFragmentHelper.setSortOrder(this, j);
            EventBus.getInstance().publish(new InventorySortOrderChangedEvent(j));
        }
        dialoginterface.dismiss();
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if (bundle != null)
        {
            searchActive = bundle.getBoolean("searchActive");
            nameFilter = bundle.getString("nameFilter");
        }
    }

    protected Fragment onCreateMasterFragment(Intent intent, Bundle bundle)
    {
        if (bundle == null) goto _L2; else goto _L1
_L1:
        intent = bundle;
        if (!bundle.isEmpty()) goto _L3; else goto _L2
_L2:
        if (!getIntent().hasExtra("folderID")) goto _L5; else goto _L4
_L4:
        intent = InventoryFragment.makeSelection(UUIDPool.getUUID(getIntent().getStringExtra("folderID")), null);
_L3:
        return InventoryFragment.newInstance(intent, true);
_L5:
        intent = bundle;
        if (getIntent().hasExtra("forSaveInfo"))
        {
            Object obj = (InventorySaveInfo)getIntent().getParcelableExtra("forSaveInfo");
            intent = bundle;
            if (obj != null)
            {
                intent = bundle;
                if (((InventorySaveInfo) (obj)).assetType != null)
                {
                    intent = bundle;
                    if (((InventorySaveInfo) (obj)).assetType != SLAssetType.AT_UNKNOWN)
                    {
                        int i = ((InventorySaveInfo) (obj)).assetType.getSpecialFolderType();
                        obj = ActivityUtils.getUserManager(getIntent());
                        intent = bundle;
                        if (obj != null)
                        {
                            obj = ((UserManager) (obj)).getInventoryManager().getDatabase().findSpecialFolder(((UserManager) (obj)).getInventoryManager().getRootFolder(), i);
                            intent = bundle;
                            if (obj != null)
                            {
                                intent = InventoryFragment.makeSelection(((SLInventoryEntry) (obj)).uuid, null);
                            }
                        }
                    }
                }
            }
        }
        if (true) goto _L3; else goto _L6
_L6:
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(0x7f12000d, menu);
        searchMenuItem = menu.findItem(0x7f10032a);
        menu = (SearchView)MenuItemCompat.getActionView(searchMenuItem);
        if (searchActive)
        {
            MenuItemCompat.expandActionView(searchMenuItem);
            menu.setQuery(nameFilter, false);
        }
        menu.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {

            final InventoryActivity this$0;

            public boolean onQueryTextChange(String s)
            {
                InventoryActivity._2D_set0(InventoryActivity.this, s);
                InventoryActivity._2D_wrap0(InventoryActivity.this);
                return true;
            }

            public boolean onQueryTextSubmit(String s)
            {
                return true;
            }

            
            {
                this$0 = InventoryActivity.this;
                super();
            }
        });
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new android.support.v4.view.MenuItemCompat.OnActionExpandListener() {

            final InventoryActivity this$0;

            public boolean onMenuItemActionCollapse(MenuItem menuitem)
            {
                InventoryActivity._2D_set1(InventoryActivity.this, false);
                InventoryActivity._2D_wrap0(InventoryActivity.this);
                return true;
            }

            public boolean onMenuItemActionExpand(MenuItem menuitem)
            {
                InventoryActivity._2D_set1(InventoryActivity.this, true);
                InventoryActivity._2D_wrap0(InventoryActivity.this);
                return true;
            }

            
            {
                this$0 = InventoryActivity.this;
                super();
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        switch (menuitem.getItemId())
        {
        default:
            return super.onOptionsItemSelected(menuitem);

        case 2131755796: 
            selectSortOrder();
            break;
        }
        return true;
    }

    protected void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if (bundle != null)
        {
            bundle.putBoolean("searchActive", searchActive);
            bundle.putString("nameFilter", nameFilter);
        }
    }

    protected void onStart()
    {
        super.onStart();
        activityStarted = true;
        updateSearchAction();
    }

    protected void onStop()
    {
        activityStarted = false;
        updateSearchAction();
        super.onStop();
    }
}

// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.support.v7.widget.SearchView;
import android.view.Menu;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import android.content.DialogInterface;
import android.support.v4.view.MenuItemCompat;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import android.os.Parcelable;
import java.util.UUID;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import android.view.MenuItem;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;

public class InventoryActivity extends MasterDetailsActivity
{
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
    private final FragmentActivityFactory InventoryDetailsFragmentFactory;
    private boolean activityStarted;
    private String fragmentSearchString;
    private String nameFilter;
    private boolean searchActive;
    private MenuItem searchMenuItem;
    private final SubscriptionData<SubscriptionSingleKey, Boolean> searchProcess;
    
    public InventoryActivity() {
        this.searchMenuItem = null;
        this.searchActive = false;
        this.activityStarted = false;
        this.nameFilter = null;
        this.fragmentSearchString = null;
        this.searchProcess = new SubscriptionData<SubscriptionSingleKey, Boolean>(UIThreadExecutor.getInstance());
        this.InventoryDetailsFragmentFactory = new FragmentActivityFactory() {
            @Override
            public Intent createIntent(final Context context, final Bundle bundle) {
                final Intent intent = new Intent(context, (Class)InventoryActivity.class);
                intent.putExtra("selection", bundle);
                return intent;
            }
            
            @Override
            public Class<? extends Fragment> getFragmentClass() {
                return InventoryFragment.class;
            }
        };
    }
    
    public static Intent makeFolderIntent(final Context context, final UUID uuid, final UUID uuid2) {
        final Intent intent = new Intent(context, (Class)InventoryActivity.class);
        intent.putExtra("activeAgentUUID", uuid.toString());
        intent.putExtra("folderID", uuid2.toString());
        return intent;
    }
    
    public static Intent makeSaveItemIntent(final Context context, final UUID uuid, final InventorySaveInfo inventorySaveInfo) {
        final Intent intent = new Intent(context, (Class)InventoryActivity.class);
        intent.putExtra("activeAgentUUID", uuid.toString());
        intent.putExtra("forSaveInfo", (Parcelable)inventorySaveInfo);
        return intent;
    }
    
    public static Intent makeSelectActionIntent(final Context context, final UUID uuid, final SelectAction selectAction, final Bundle bundle, @Nullable final SLAssetType slAssetType) {
        final Intent intent = new Intent(context, (Class)InventoryActivity.class);
        intent.putExtra("activeAgentUUID", uuid.toString());
        intent.putExtra("forSelectItem", true);
        intent.putExtra("selectAction", selectAction.toString());
        intent.putExtra("selectActionParams", bundle);
        if (slAssetType != null) {
            intent.putExtra("selectActionAssetType", slAssetType.getTypeCode());
        }
        return intent;
    }
    
    public static Intent makeSelectIntent(final Context context, final UUID uuid) {
        final Intent intent = new Intent(context, (Class)InventoryActivity.class);
        intent.putExtra("activeAgentUUID", uuid.toString());
        intent.putExtra("forSelectItem", true);
        return intent;
    }
    
    public static Intent makeTransferIntent(final Context context, final UUID uuid, final UUID uuid2, final String s) {
        final Intent intent = new Intent(context, (Class)InventoryActivity.class);
        intent.putExtra("activeAgentUUID", uuid.toString());
        intent.putExtra("forSelectItem", true);
        intent.putExtra("transferToID", uuid2.toString());
        if (s != null) {
            intent.putExtra("transferToName", s);
        }
        return intent;
    }
    
    private void selectSortOrder() {
        final int sortOrder = InventoryFragmentHelper.getSortOrder((Context)this);
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder((Context)this);
        alertDialog$Builder.setTitle(2131297041);
        alertDialog$Builder.setSingleChoiceItems(new CharSequence[] { "Newest first", "Alphabetical" }, sortOrder, (DialogInterface$OnClickListener)new _$Lambda$Tc22ivDU79Y83KauKGybv49CW7A(sortOrder, this));
        alertDialog$Builder.create().show();
    }
    
    private void updateSearchAction() {
        String trim;
        if (this.activityStarted && this.searchActive) {
            final UserManager userManager = ActivityUtils.getUserManager(this.getIntent());
            if (userManager != null) {
                this.searchProcess.subscribe(userManager.getInventoryManager().getSearchProcess(), SubscriptionSingleKey.Value);
            }
            trim = Strings.nullToEmpty(this.nameFilter).trim();
        }
        else {
            this.searchProcess.unsubscribe();
            trim = "";
        }
        if (!Objects.equal(this.fragmentSearchString, trim)) {
            this.fragmentSearchString = trim;
            final Fragment fragmentById = this.getSupportFragmentManager().findFragmentById(2131755654);
            if (fragmentById instanceof InventoryFragment) {
                ((InventoryFragment)fragmentById).setSearchString(Strings.emptyToNull(trim));
            }
        }
    }
    
    void clearSearchMode() {
        this.searchActive = false;
        if (this.searchMenuItem != null) {
            MenuItemCompat.collapseActionView(this.searchMenuItem);
        }
    }
    
    @Override
    protected FragmentActivityFactory getDetailsFragmentFactory() {
        return this.InventoryDetailsFragmentFactory;
    }
    
    @Override
    protected Bundle getNewDetailsFragmentArguments(@Nullable final Bundle bundle, @Nullable final Bundle bundle2) {
        if (bundle != null) {
            return InventoryFragment.makeDetailsArguments(bundle);
        }
        return super.getNewDetailsFragmentArguments(null, bundle2);
    }
    
    @Override
    protected boolean isAlwaysImplicitFragment(final Class<? extends Fragment> clazz) {
        return clazz.equals(InventoryFragment.class);
    }
    
    @Override
    protected void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.searchActive = bundle.getBoolean("searchActive");
            this.nameFilter = bundle.getString("nameFilter");
        }
    }
    
    @Override
    protected Fragment onCreateMasterFragment(final Intent intent, @Nullable final Bundle bundle) {
        if (bundle != null) {
            final Bundle bundle2 = bundle;
            if (!bundle.isEmpty()) {
                return InventoryFragment.newInstance(bundle2, true);
            }
        }
        Bundle bundle2;
        if (this.getIntent().hasExtra("folderID")) {
            bundle2 = InventoryFragment.makeSelection(UUIDPool.getUUID(this.getIntent().getStringExtra("folderID")), null);
        }
        else {
            bundle2 = bundle;
            if (this.getIntent().hasExtra("forSaveInfo")) {
                final InventorySaveInfo inventorySaveInfo = (InventorySaveInfo)this.getIntent().getParcelableExtra("forSaveInfo");
                bundle2 = bundle;
                if (inventorySaveInfo != null) {
                    bundle2 = bundle;
                    if (inventorySaveInfo.assetType != null) {
                        bundle2 = bundle;
                        if (inventorySaveInfo.assetType != SLAssetType.AT_UNKNOWN) {
                            final int specialFolderType = inventorySaveInfo.assetType.getSpecialFolderType();
                            final UserManager userManager = ActivityUtils.getUserManager(this.getIntent());
                            bundle2 = bundle;
                            if (userManager != null) {
                                final SLInventoryEntry specialFolder = userManager.getInventoryManager().getDatabase().findSpecialFolder(userManager.getInventoryManager().getRootFolder(), specialFolderType);
                                bundle2 = bundle;
                                if (specialFolder != null) {
                                    bundle2 = InventoryFragment.makeSelection(specialFolder.uuid, null);
                                }
                            }
                        }
                    }
                }
            }
        }
        return InventoryFragment.newInstance(bundle2, true);
    }
    
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.getMenuInflater().inflate(2131886093, menu);
        this.searchMenuItem = menu.findItem(2131755818);
        final SearchView searchView = (SearchView)MenuItemCompat.getActionView(this.searchMenuItem);
        if (this.searchActive) {
            MenuItemCompat.expandActionView(this.searchMenuItem);
            searchView.setQuery(this.nameFilter, false);
        }
        searchView.setOnQueryTextListener((SearchView.OnQueryTextListener)new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(final String s) {
                InventoryActivity.this.nameFilter = s;
                InventoryActivity.this.updateSearchAction();
                return true;
            }
            
            @Override
            public boolean onQueryTextSubmit(final String s) {
                return true;
            }
        });
        MenuItemCompat.setOnActionExpandListener(this.searchMenuItem, (MenuItemCompat.OnActionExpandListener)new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(final MenuItem menuItem) {
                InventoryActivity.this.searchActive = false;
                InventoryActivity.this.updateSearchAction();
                return true;
            }
            
            @Override
            public boolean onMenuItemActionExpand(final MenuItem menuItem) {
                InventoryActivity.this.searchActive = true;
                InventoryActivity.this.updateSearchAction();
                return true;
            }
        });
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            default: {
                return super.onOptionsItemSelected(menuItem);
            }
            case 2131755796: {
                this.selectSortOrder();
                return true;
            }
        }
    }
    
    @Override
    protected void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (bundle != null) {
            bundle.putBoolean("searchActive", this.searchActive);
            bundle.putString("nameFilter", this.nameFilter);
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        this.activityStarted = true;
        this.updateSearchAction();
    }
    
    @Override
    protected void onStop() {
        this.activityStarted = false;
        this.updateSearchAction();
        super.onStop();
    }
    
    public enum SelectAction
    {
        applyFirstLife("applyFirstLife", 1, 2131297016), 
        applyPickImage("applyPickImage", 2, 2131297016), 
        applyUserProfile("applyUserProfile", 0, 2131297016);
        
        public final int subtitleResourceId;
        
        private SelectAction(final String name, final int ordinal, final int subtitleResourceId) {
            this.subtitleResourceId = subtitleResourceId;
        }
    }
}

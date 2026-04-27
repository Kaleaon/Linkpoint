package com.lumiyaviewer.lumiya.ui.inventory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class InventoryActivity extends MasterDetailsActivity {
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
    private MenuItem searchMenuItem = null;
    private boolean searchActive = false;
    private boolean activityStarted = false;
    private String nameFilter = null;
    private String fragmentSearchString = null;
    private final SubscriptionData<SubscriptionSingleKey, Boolean> searchProcess = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final FragmentActivityFactory InventoryDetailsFragmentFactory = new FragmentActivityFactory() { // from class: com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity.1
        @Override // com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory
        public Intent createIntent(Context context, Bundle bundle) {
            Intent intent = new Intent(context, (Class<?>) InventoryActivity.class);
            intent.putExtra(MasterDetailsActivity.INTENT_SELECTION_KEY, bundle);
            return intent;
        }

        @Override // com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory
        public Class<? extends Fragment> getFragmentClass() {
            return InventoryFragment.class;
        }
    };

    /* loaded from: classes.dex */
    public enum SelectAction {
        applyUserProfile(R.string.select_picture_subtitle),
        applyFirstLife(R.string.select_picture_subtitle),
        applyPickImage(R.string.select_picture_subtitle);

        public final int subtitleResourceId;

        SelectAction(int i) {
            this.subtitleResourceId = i;
        }

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static SelectAction[] valuesCustom() {
            return values();
        }
    }

    public static Intent makeFolderIntent(Context context, UUID uuid, UUID uuid2) {
        Intent intent = new Intent(context, (Class<?>) InventoryActivity.class);
        intent.putExtra("activeAgentUUID", uuid.toString());
        intent.putExtra(INITIAL_FOLDER_ID_TAG, uuid2.toString());
        return intent;
    }

    public static Intent makeSaveItemIntent(Context context, UUID uuid, InventorySaveInfo inventorySaveInfo) {
        Intent intent = new Intent(context, (Class<?>) InventoryActivity.class);
        intent.putExtra("activeAgentUUID", uuid.toString());
        intent.putExtra(SAVE_INFO_INTENT_TAG, inventorySaveInfo);
        return intent;
    }

    public static Intent makeSelectActionIntent(Context context, UUID uuid, SelectAction selectAction, Bundle bundle, @Nullable SLAssetType sLAssetType) {
        Intent intent = new Intent(context, (Class<?>) InventoryActivity.class);
        intent.putExtra("activeAgentUUID", uuid.toString());
        intent.putExtra(SELECT_ITEM_INTENT_TAG, true);
        intent.putExtra(SELECT_ACTION_INTENT_TAG, selectAction.toString());
        intent.putExtra(SELECT_ACTION_PARAMS_TAG, bundle);
        if (sLAssetType != null) {
            intent.putExtra(SELECT_ACTION_ASSET_TYPE, sLAssetType.getTypeCode());
        }
        return intent;
    }

    public static Intent makeSelectIntent(Context context, UUID uuid) {
        Intent intent = new Intent(context, (Class<?>) InventoryActivity.class);
        intent.putExtra("activeAgentUUID", uuid.toString());
        intent.putExtra(SELECT_ITEM_INTENT_TAG, true);
        return intent;
    }

    public static Intent makeTransferIntent(Context context, UUID uuid, UUID uuid2, String str) {
        Intent intent = new Intent(context, (Class<?>) InventoryActivity.class);
        intent.putExtra("activeAgentUUID", uuid.toString());
        intent.putExtra(SELECT_ITEM_INTENT_TAG, true);
        intent.putExtra(TRANSFER_TO_INTENT_TAG, uuid2.toString());
        if (str != null) {
            intent.putExtra(TRANSFER_TO_NAME_TAG, str);
        }
        return intent;
    }

    private void selectSortOrder() {
        final int sortOrder = InventoryFragmentHelper.getSortOrder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.sort_order_caption);
        builder.setSingleChoiceItems(new CharSequence[]{"Newest first", "Alphabetical"}, sortOrder, new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$Tc22ivDU79Y83KauKGybv49CW7A
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                ((InventoryActivity) this).m599xeedaf4f8(sortOrder, dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder.create().show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSearchAction() {
        String str;
        if (this.activityStarted && this.searchActive) {
            UserManager userManager = ActivityUtils.getUserManager(getIntent());
            if (userManager != null) {
                this.searchProcess.subscribe(userManager.getInventoryManager().getSearchProcess(), SubscriptionSingleKey.Value);
            }
            str = Strings.nullToEmpty(this.nameFilter).trim();
        } else {
            this.searchProcess.unsubscribe();
            str = "";
        }
        if (Objects.equal(this.fragmentSearchString, str)) {
            return;
        }
        this.fragmentSearchString = str;
        Fragment findFragmentById = getSupportFragmentManager().findFragmentById(R.id.selector);
        if (findFragmentById instanceof InventoryFragment) {
            ((InventoryFragment) findFragmentById).setSearchString(Strings.emptyToNull(str));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearSearchMode() {
        this.searchActive = false;
        if (this.searchMenuItem != null) {
            MenuItemCompat.collapseActionView(this.searchMenuItem);
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity
    protected FragmentActivityFactory getDetailsFragmentFactory() {
        return this.InventoryDetailsFragmentFactory;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity
    protected Bundle getNewDetailsFragmentArguments(@Nullable Bundle bundle, @Nullable Bundle bundle2) {
        return bundle != null ? InventoryFragment.makeDetailsArguments(bundle) : super.getNewDetailsFragmentArguments(null, bundle2);
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity
    protected boolean isAlwaysImplicitFragment(Class<? extends Fragment> cls) {
        return cls.equals(InventoryFragment.class);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryActivity_10944, reason: not valid java name */
    public /* synthetic */ void m599xeedaf4f8(int i, DialogInterface dialogInterface, int i2) {
        if (i != i2) {
            InventoryFragmentHelper.setSortOrder(this, i2);
            EventBus.getInstance().publish(new InventorySortOrderChangedEvent(i2));
        }
        dialogInterface.dismiss();
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity, com.lumiyaviewer.lumiya.ui.common.DetailsActivity, com.lumiyaviewer.lumiya.ui.common.ConnectedActivity, com.lumiyaviewer.lumiya.ui.common.ThemedActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.searchActive = bundle.getBoolean(SEARCH_ACTIVE_TAG);
            this.nameFilter = bundle.getString(NAME_FILTER_TAG);
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity
    protected Fragment onCreateMasterFragment(Intent intent, @Nullable Bundle bundle) {
        InventorySaveInfo inventorySaveInfo;
        SLInventoryEntry findSpecialFolder;
        if (bundle == null || bundle.isEmpty()) {
            if (getIntent().hasExtra(INITIAL_FOLDER_ID_TAG)) {
                bundle = InventoryFragment.makeSelection(UUIDPool.getUUID(getIntent().getStringExtra(INITIAL_FOLDER_ID_TAG)), null);
            } else if (getIntent().hasExtra(SAVE_INFO_INTENT_TAG) && (inventorySaveInfo = (InventorySaveInfo) getIntent().getParcelableExtra(SAVE_INFO_INTENT_TAG)) != null && inventorySaveInfo.assetType != null && inventorySaveInfo.assetType != SLAssetType.AT_UNKNOWN) {
                int specialFolderType = inventorySaveInfo.assetType.getSpecialFolderType();
                UserManager userManager = ActivityUtils.getUserManager(getIntent());
                if (userManager != null && (findSpecialFolder = userManager.getInventoryManager().getDatabase().findSpecialFolder(userManager.getInventoryManager().getRootFolder(), specialFolderType)) != null) {
                    bundle = InventoryFragment.makeSelection(findSpecialFolder.uuid, null);
                }
            }
        }
        return InventoryFragment.newInstance(bundle, true);
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ConnectedActivity, android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory_menu, menu);
        this.searchMenuItem = menu.findItem(R.id.inventory_search_item);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(this.searchMenuItem);
        if (this.searchActive) {
            MenuItemCompat.expandActionView(this.searchMenuItem);
            searchView.setQuery(this.nameFilter, false);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { // from class: com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity.2
            @Override // android.support.v7.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextChange(String str) {
                InventoryActivity.this.nameFilter = str;
                InventoryActivity.this.updateSearchAction();
                return true;
            }

            @Override // android.support.v7.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextSubmit(String str) {
                return true;
            }
        });
        MenuItemCompat.setOnActionExpandListener(this.searchMenuItem, new MenuItemCompat.OnActionExpandListener() { // from class: com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity.3
            @Override // android.support.v4.view.MenuItemCompat.OnActionExpandListener
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                InventoryActivity.this.searchActive = false;
                InventoryActivity.this.updateSearchAction();
                return true;
            }

            @Override // android.support.v4.view.MenuItemCompat.OnActionExpandListener
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                InventoryActivity.this.searchActive = true;
                InventoryActivity.this.updateSearchAction();
                return true;
            }
        });
        return true;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ConnectedActivity, android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_sort_order /* 2131755796 */:
                selectSortOrder();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity, com.lumiyaviewer.lumiya.ui.common.DetailsActivity, com.lumiyaviewer.lumiya.ui.common.ConnectedActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (bundle != null) {
            bundle.putBoolean(SEARCH_ACTIVE_TAG, this.searchActive);
            bundle.putString(NAME_FILTER_TAG, this.nameFilter);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.ui.common.ThemedActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        this.activityStarted = true;
        updateSearchAction();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.ui.common.ThemedActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStop() {
        this.activityStarted = false;
        updateSearchAction();
        super.onStop();
    }
}

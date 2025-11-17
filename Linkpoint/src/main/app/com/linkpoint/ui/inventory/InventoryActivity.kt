package com.linkpoint.ui.inventory

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.google.common.base.Objects
import com.google.common.base.Strings
import com.linkpoint.R
import com.linkpoint.eventbus.EventBus
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.inventory.SLAssetType
import com.linkpoint.slproto.inventory.SLInventoryEntry
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.FragmentActivityFactory
import com.linkpoint.ui.common.MasterDetailsActivity
import com.linkpoint.utils.UUIDPool
import java.util.UUID
import androidx.annotation.Nullable

class InventoryActivity : MasterDetailsActivity {
    private val INITIAL_FOLDER_ID_TAG: String = "folderID"
    private val NAME_FILTER_TAG: String = "nameFilter"
    val SAVE_INFO_INTENT_TAG: String = "forSaveInfo"
    private val SEARCH_ACTIVE_TAG: String = "searchActive"
    val SELECT_ACTION_ASSET_TYPE: String = "selectActionAssetType"
    val SELECT_ACTION_INTENT_TAG: String = "selectAction"
    val SELECT_ACTION_PARAMS_TAG: String = "selectActionParams"
    val SELECT_ITEM_INTENT_TAG: String = "forSelectItem"
    val TRANSFER_TO_INTENT_TAG: String = "transferToID"
    val TRANSFER_TO_NAME_TAG: String = "transferToName"
    private FragmentActivityFactory InventoryDetailsFragmentFactory = FragmentActivityFactory() {
        Intent createIntent(Context context, Bundle bundle) {
            Intent intent = Intent(context, InventoryActivity.class)
            intent.putExtra(MasterDetailsActivity.INTENT_SELECTION_KEY, bundle)
            return intent
        }

        Class<? : Fragment> getFragmentClass() {
            return InventoryFragment.class
        }
    }
    private Boolean activityStarted = false
    private val fragmentSearchString: String = null
    /* access modifiers changed from: private */
    String nameFilter = null
    /* access modifiers changed from: private */
    Boolean searchActive = false
    private MenuItem searchMenuItem = null
    private SubscriptionData<SubscriptionSingleKey, Boolean> searchProcess = SubscriptionData<>(UIThreadExecutor.getInstance())

    enum SelectAction {
        applyUserProfile(R.string.select_picture_subtitle),
        applyFirstLife(R.string.select_picture_subtitle),
        applyPickImage(R.string.select_picture_subtitle)
        
        Int subtitleResourceId

        private SelectAction(Int i) {
            this.subtitleResourceId = i
        }
    }

    Intent makeFolderIntent(Context context, UUID uuid, UUID uuid2) {
        Intent intent = Intent(context, InventoryActivity.class)
        intent.putExtra("activeAgentUUID", uuid.toString())
        intent.putExtra(INITIAL_FOLDER_ID_TAG, uuid2.toString())
        return intent
    }

    Intent makeSaveItemIntent(Context context, UUID uuid, InventorySaveInfo inventorySaveInfo) {
        Intent intent = Intent(context, InventoryActivity.class)
        intent.putExtra("activeAgentUUID", uuid.toString())
        intent.putExtra(SAVE_INFO_INTENT_TAG, inventorySaveInfo)
        return intent
    }

    Intent makeSelectActionIntent(Context context, UUID uuid, SelectAction selectAction, Bundle bundle, @Nullable SLAssetType sLAssetType) {
        Intent intent = Intent(context, InventoryActivity.class)
        intent.putExtra("activeAgentUUID", uuid.toString())
        intent.putExtra(SELECT_ITEM_INTENT_TAG, true)
        intent.putExtra(SELECT_ACTION_INTENT_TAG, selectAction.toString())
        intent.putExtra(SELECT_ACTION_PARAMS_TAG, bundle)
        if (sLAssetType != null) {
            intent.putExtra(SELECT_ACTION_ASSET_TYPE, sLAssetType.getTypeCode())
        }
        return intent
    }

    Intent makeSelectIntent(Context context, UUID uuid) {
        Intent intent = Intent(context, InventoryActivity.class)
        intent.putExtra("activeAgentUUID", uuid.toString())
        intent.putExtra(SELECT_ITEM_INTENT_TAG, true)
        return intent
    }

    Intent makeTransferIntent(Context context, UUID uuid, UUID uuid2, String str) {
        Intent intent = Intent(context, InventoryActivity.class)
        intent.putExtra("activeAgentUUID", uuid.toString())
        intent.putExtra(SELECT_ITEM_INTENT_TAG, true)
        intent.putExtra(TRANSFER_TO_INTENT_TAG, uuid2.toString())
        if (str != null) {
            intent.putExtra(TRANSFER_TO_NAME_TAG, str)
        }
        return intent
    }

    private Unit selectSortOrder() {
        Int sortOrder = InventoryFragmentHelper.getSortOrder(this)
        AlertDialog.Builder builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.sort_order_caption)
        builder.setSingleChoiceItems(CharSequence[]{"Newest first", "Alphabetical"}, sortOrder, $Lambda$Tc22ivDU79Y83KauKGybv49CW7A(sortOrder, this))
        builder.create().show()
    }

    /* access modifiers changed from: private */
    Unit updateSearchAction() {
        String str
        if (!this.activityStarted || !this.searchActive) {
            this.searchProcess.unsubscribe()
            str = ""
        } else {
            UserManager userManager = ActivityUtils.getUserManager(getIntent())
            if (userManager != null) {
                this.searchProcess.subscribe(userManager.getInventoryManager().getSearchProcess(), SubscriptionSingleKey.Value)
            }
            str = Strings.nullToEmpty(this.nameFilter).trim()
        }
        if (!Objects.equal(this.fragmentSearchString, str)) {
            this.fragmentSearchString = str
            Fragment findFragmentById = getSupportFragmentManager().findFragmentById(R.id.selector)
            if (findFragmentById instanceof InventoryFragment) {
                ((InventoryFragment) findFragmentById).setSearchString(Strings.emptyToNull(str))
            }
        }
    }

    /* access modifiers changed from: package-private */
    Unit clearSearchMode() {
        this.searchActive = false
        if (this.searchMenuItem != null) {
            MenuItemCompat.collapseActionView(this.searchMenuItem)
        }
    }

    /* access modifiers changed from: protected */
    FragmentActivityFactory getDetailsFragmentFactory() {
        return this.InventoryDetailsFragmentFactory
    }

    /* access modifiers changed from: protected */
    Bundle getNewDetailsFragmentArguments(@Nullable Bundle bundle, @Nullable Bundle bundle2) {
        return bundle != null ? InventoryFragment.makeDetailsArguments(bundle) : super.getNewDetailsFragmentArguments((Bundle) null, bundle2)
    }

    /* access modifiers changed from: protected */
    Boolean isAlwaysImplicitFragment(Class<? : Fragment> cls) {
        return cls.equals(InventoryFragment.class)
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryActivity_10944  reason: not valid java name */
    /* synthetic */ Unit m589lambda$com_lumiyaviewer_lumiya_ui_inventory_InventoryActivity_10944(Int i, DialogInterface dialogInterface, Int i2) {
        if (i != i2) {
            InventoryFragmentHelper.setSortOrder(this, i2)
            EventBus.getInstance().publish(InventorySortOrderChangedEvent(i2))
        }
        dialogInterface.dismiss()
    }

    /* access modifiers changed from: protected */
    Unit onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle)
        if (bundle != null) {
            this.searchActive = bundle.getBoolean(SEARCH_ACTIVE_TAG)
            this.nameFilter = bundle.getString(NAME_FILTER_TAG)
        }
    }

    /* access modifiers changed from: protected */
    Fragment onCreateMasterFragment(Intent intent, @Nullable Bundle bundle) {
        InventorySaveInfo inventorySaveInfo
        SLInventoryEntry findSpecialFolder
        if (bundle == null || bundle.isEmpty()) {
            if (getIntent().hasExtra(INITIAL_FOLDER_ID_TAG)) {
                bundle = InventoryFragment.makeSelection(UUIDPool.getUUID(getIntent().getStringExtra(INITIAL_FOLDER_ID_TAG)), (String) null)
            } else if (!(!getIntent().hasExtra(SAVE_INFO_INTENT_TAG) || (inventorySaveInfo = (InventorySaveInfo) getIntent().getParcelableExtra(SAVE_INFO_INTENT_TAG)) == null || inventorySaveInfo.assetType == null || inventorySaveInfo.assetType == SLAssetType.AT_UNKNOWN)) {
                Int specialFolderType = inventorySaveInfo.assetType.getSpecialFolderType()
                UserManager userManager = ActivityUtils.getUserManager(getIntent())
                if (!(userManager == null || (findSpecialFolder = userManager.getInventoryManager().getDatabase().findSpecialFolder(userManager.getInventoryManager().getRootFolder(), specialFolderType)) == null)) {
                    bundle = InventoryFragment.makeSelection(findSpecialFolder.uuid, (String) null)
                }
            }
        }
        return InventoryFragment.newInstance(bundle, true)
    }

    Boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory_menu, menu)
        this.searchMenuItem = menu.findItem(R.id.inventory_search_item)
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(this.searchMenuItem)
        if (this.searchActive) {
            MenuItemCompat.expandActionView(this.searchMenuItem)
            searchView.setQuery(this.nameFilter, false)
        }
        searchView.setOnQueryTextListener(SearchView.OnQueryTextListener() {
            Boolean onQueryTextChange(String str) {
                String unused = InventoryActivity.this.nameFilter = str
                InventoryActivity.this.updateSearchAction()
                return true
            }

            Boolean onQueryTextSubmit(String str) {
                return true
            }
        MenuItemCompat.setOnActionExpandListener(this.searchMenuItem, MenuItemCompat.OnActionExpandListener() {
            Boolean onMenuItemActionCollapse(MenuItem menuItem) {
                Boolean unused = InventoryActivity.this.searchActive = false
                InventoryActivity.this.updateSearchAction()
                return true
            }

            Boolean onMenuItemActionExpand(MenuItem menuItem) {
                Boolean unused = InventoryActivity.this.searchActive = true
                InventoryActivity.this.updateSearchAction()
                return true
            }
        return true
    }

    Boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_sort_order:
                selectSortOrder()
                return true
            default:
                return super.onOptionsItemSelected(menuItem)
        }
    }

    /* access modifiers changed from: protected */
    Unit onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle)
        if (bundle != null) {
            bundle.putBoolean(SEARCH_ACTIVE_TAG, this.searchActive)
            bundle.putString(NAME_FILTER_TAG, this.nameFilter)
        }
    }

    /* access modifiers changed from: protected */
    Unit onStart() {
        super.onStart()
        this.activityStarted = true
        updateSearchAction()
    }

    /* access modifiers changed from: protected */
    Unit onStop() {
        this.activityStarted = false
        updateSearchAction()
        super.onStop()
    }
}

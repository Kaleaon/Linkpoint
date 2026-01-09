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
        fun createIntent(Context context, Bundle bundle): Intent {
            Intent intent = Intent(context, InventoryActivity.class)
            intent.putExtra(MasterDetailsActivity.INTENT_SELECTION_KEY, bundle)
            return intent
        }

        fun getFragmentClass(): Class<? : Fragment> {
            return InventoryFragment.class
        }
    }
    private Boolean activityStarted = false
    private val fragmentSearchString: String = null
    /* access modifiers changed from: private */
    var nameFilter: String = null
    /* access modifiers changed from: private */
    var searchActive: Boolean = false
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

    fun makeFolderIntent(Context context, UUID uuid, UUID uuid2): Intent {
        Intent intent = Intent(context, InventoryActivity.class)
        intent.putExtra("activeAgentUUID", uuid.toString())
        intent.putExtra(INITIAL_FOLDER_ID_TAG, uuid2.toString())
        return intent
    }

    fun makeSaveItemIntent(Context context, UUID uuid, InventorySaveInfo inventorySaveInfo): Intent {
        Intent intent = Intent(context, InventoryActivity.class)
        intent.putExtra("activeAgentUUID", uuid.toString())
        intent.putExtra(SAVE_INFO_INTENT_TAG, inventorySaveInfo)
        return intent
    }

    fun makeSelectActionIntent(Context context, UUID uuid, SelectAction selectAction, Bundle bundle, @Nullable SLAssetType sLAssetType): Intent {
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

    fun makeSelectIntent(Context context, UUID uuid): Intent {
        Intent intent = Intent(context, InventoryActivity.class)
        intent.putExtra("activeAgentUUID", uuid.toString())
        intent.putExtra(SELECT_ITEM_INTENT_TAG, true)
        return intent
    }

    fun makeTransferIntent(Context context, UUID uuid, UUID uuid2, String str): Intent {
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
        var sortOrder: Int = InventoryFragmentHelper.getSortOrder(this)
        AlertDialog.Builder builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.sort_order_caption)
        builder.setSingleChoiceItems(CharSequence[]{"Newest first", "Alphabetical"}, sortOrder, $Lambda$Tc22ivDU79Y83KauKGybv49CW7A(sortOrder, this))
        builder.create().show()
    }

    /* access modifiers changed from: private */
    fun updateSearchAction()  {
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
            if (findFragmentById is InventoryFragment) {
                ((InventoryFragment) findFragmentById).setSearchString(Strings.emptyToNull(str))
            }
        }
    }

    /* access modifiers changed from: package-private */
    fun clearSearchMode()  {
        this.searchActive = false
        if (this.searchMenuItem != null) {
            MenuItemCompat.collapseActionView(this.searchMenuItem)
        }
    }

    /* access modifiers changed from: protected */
    fun getDetailsFragmentFactory(): FragmentActivityFactory {
        return this.InventoryDetailsFragmentFactory
    }

    /* access modifiers changed from: protected */
    fun getNewDetailsFragmentArguments(@Nullable Bundle bundle, @Nullable Bundle bundle2): Bundle {
        return bundle != null ? InventoryFragment.makeDetailsArguments(bundle) : super.getNewDetailsFragmentArguments((Bundle) null, bundle2)
    }

    /* access modifiers changed from: protected */
    fun isAlwaysImplicitFragment(Class<? : Fragment> cls): Boolean {
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
    fun onCreate(@Nullable Bundle bundle)  {
        super.onCreate(bundle)
        if (bundle != null) {
            this.searchActive = bundle.getBoolean(SEARCH_ACTIVE_TAG)
            this.nameFilter = bundle.getString(NAME_FILTER_TAG)
        }
    }

    /* access modifiers changed from: protected */
    fun onCreateMasterFragment(Intent intent, @Nullable Bundle bundle): Fragment {
        InventorySaveInfo inventorySaveInfo
        SLInventoryEntry findSpecialFolder
        if (bundle == null || bundle.isEmpty()) {
            if (getIntent().hasExtra(INITIAL_FOLDER_ID_TAG)) {
                bundle = InventoryFragment.makeSelection(UUIDPool.getUUID(getIntent().getStringExtra(INITIAL_FOLDER_ID_TAG)), (String) null)
            } else if (!(!getIntent().hasExtra(SAVE_INFO_INTENT_TAG) || (inventorySaveInfo = (InventorySaveInfo) getIntent().getParcelableExtra(SAVE_INFO_INTENT_TAG)) == null || inventorySaveInfo.assetType == null || inventorySaveInfo.assetType == SLAssetType.AT_UNKNOWN)) {
                var specialFolderType: Int = inventorySaveInfo.assetType.getSpecialFolderType()
                UserManager userManager = ActivityUtils.getUserManager(getIntent())
                if (!(userManager == null || (findSpecialFolder = userManager.getInventoryManager().getDatabase().findSpecialFolder(userManager.getInventoryManager().getRootFolder(), specialFolderType)) == null)) {
                    bundle = InventoryFragment.makeSelection(findSpecialFolder.uuid, (String) null)
                }
            }
        }
        return InventoryFragment.newInstance(bundle, true)
    }

    fun onCreateOptionsMenu(Menu menu): Boolean {
        getMenuInflater().inflate(R.menu.inventory_menu, menu)
        this.searchMenuItem = menu.findItem(R.id.inventory_search_item)
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(this.searchMenuItem)
        if (this.searchActive) {
            MenuItemCompat.expandActionView(this.searchMenuItem)
            searchView.setQuery(this.nameFilter, false)
        }
        searchView.setOnQueryTextListener(SearchView.OnQueryTextListener() {
            fun onQueryTextChange(String str): Boolean {
                var unused: String = InventoryActivity.this.nameFilter = str
                InventoryActivity.this.updateSearchAction()
                return true
            }

            fun onQueryTextSubmit(String str): Boolean {
                return true
            }
        MenuItemCompat.setOnActionExpandListener(this.searchMenuItem, MenuItemCompat.OnActionExpandListener() {
            fun onMenuItemActionCollapse(MenuItem menuItem): Boolean {
                var unused: Boolean = InventoryActivity.this.searchActive = false
                InventoryActivity.this.updateSearchAction()
                return true
            }

            fun onMenuItemActionExpand(MenuItem menuItem): Boolean {
                var unused: Boolean = InventoryActivity.this.searchActive = true
                InventoryActivity.this.updateSearchAction()
                return true
            }
        return true
    }

    fun onOptionsItemSelected(MenuItem menuItem): Boolean {
        switch (menuItem.getItemId()) {
            case R.id.item_sort_order:
                selectSortOrder()
                return true
            default:
                return super.onOptionsItemSelected(menuItem)
        }
    }

    /* access modifiers changed from: protected */
    fun onSaveInstanceState(Bundle bundle)  {
        super.onSaveInstanceState(bundle)
        if (bundle != null) {
            bundle.putBoolean(SEARCH_ACTIVE_TAG, this.searchActive)
            bundle.putString(NAME_FILTER_TAG, this.nameFilter)
        }
    }

    /* access modifiers changed from: protected */
    fun onStart()  {
        super.onStart()
        this.activityStarted = true
        updateSearchAction()
    }

    /* access modifiers changed from: protected */
    fun onStop()  {
        this.activityStarted = false
        updateSearchAction()
        super.onStop()
    }
}

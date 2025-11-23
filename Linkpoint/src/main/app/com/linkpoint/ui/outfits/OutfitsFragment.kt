package com.linkpoint.ui.outfits

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.google.common.base.Objects
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Table
import com.linkpoint.Debug
import com.linkpoint.R
import com.linkpoint.eventbus.EventBus
import com.linkpoint.eventbus.EventHandler
import com.linkpoint.orm.InventoryDB
import com.linkpoint.orm.InventoryEntryList
import com.linkpoint.orm.InventoryQuery
import com.linkpoint.react.Subscription
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.assets.SLWearable
import com.linkpoint.slproto.assets.SLWearableType
import com.linkpoint.slproto.inventory.SLAssetType
import com.linkpoint.slproto.inventory.SLInventoryEntry
import com.linkpoint.slproto.modules.SLAvatarAppearance
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.DetailsActivity
import com.linkpoint.ui.common.FragmentWithTitle
import com.linkpoint.ui.common.LoadingLayout
import com.linkpoint.ui.common.ReloadableFragment
import com.linkpoint.ui.common.loadmon.LoadableMonitor
import com.linkpoint.ui.inventory.InventoryFolderAdapter
import com.linkpoint.ui.inventory.InventoryFragmentHelper
import com.linkpoint.ui.inventory.InventorySortOrderChangedEvent
import com.linkpoint.utils.UUIDPool
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class OutfitsFragment : FragmentWithTitle(), ReloadableFragment, View.OnClickListener, InventoryFolderAdapter.OnItemCheckboxClickListener {
    private val FOLDER_ID_KEY = "folderID"
    private var adapter: InventoryFolderAdapter? = null
    
    private val agentCircuit = SubscriptionData<UUID, SLAgentCircuit>(UIThreadExecutor.getInstance(), Subscription.OnData {
        onAgentCircuit(it)
    })
    
    private val entryList = SubscriptionData<InventoryQuery, InventoryEntryList>(UIThreadExecutor.getInstance(), Subscription.OnData {
        onInventoryEntryList(it)
    })
    
    private val folderLoading = SubscriptionData<UUID, Boolean>(UIThreadExecutor.getInstance(), Subscription.OnData {
        onLoadingStatusChanged(it)
    })
    
    private val itemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        val userManager = getUserManager()
        if (userManager != null) {
            val item = parent.adapter.getItem(position)
            if (item === listHeaderData) {
                val folderUUID = getFolderUUID()
                if (folderUUID == null || Objects.equal(folderUUID, myOutfitsFolderUUID)) {
                    DetailsActivity.showEmbeddedDetails(activity, CurrentOutfitFragment::class.java, CurrentOutfitFragment.makeSelection(userManager.getUserID()))
                } else {
                    val data = entryList.data
                    val folder = data?.folder
                    if (folder != null) {
                        navigateToFolder(folder.parentUUID)
                    }
                }
            } else if (item is SLInventoryEntry) {
                Debug.Printf("Inventory: Item click: item isFolder %b invType %d typeDefault %d assetType %d", item.isFolder, item.invType, item.typeDefault, item.assetType)
                val uuid = if (!item.isFolder || item.uuid == null) {
                    if (!item.isLink || item.invType != 8) null else item.assetUUID
                } else {
                    item.uuid
                }
                if (uuid != null) {
                    navigateToFolder(uuid)
                }
            }
        }
    }
    
    private var listHeader: ViewGroup? = null
    private val listHeaderData = Any()
    private val loadableMonitor = LoadableMonitor(entryList)
    private var myOutfitsFolderUUID: UUID? = null
    
    private val rootFolderEntryList = SubscriptionData<InventoryQuery, InventoryEntryList>(UIThreadExecutor.getInstance(), Subscription.OnData {
        onRootFolderEntryList(it)
    })
    
    private val wornAttachments = SubscriptionData<SubscriptionSingleKey, ImmutableMap<UUID, String>>(UIThreadExecutor.getInstance(), Subscription.OnData {
        onWornAttachmentsChanged(it)
    })
    
    private val wornOutfitFolder = SubscriptionData<SubscriptionSingleKey, UUID>(UIThreadExecutor.getInstance(), Subscription.OnData {
        onWornOutfitFolder(it)
    })
    
    private val wornWearables = SubscriptionData<SubscriptionSingleKey, Table<SLWearableType, UUID, SLWearable>>(UIThreadExecutor.getInstance(), Subscription.OnData {
        onWornWearablesChanged(it)
    })

    private fun changeOutfit(replace: Boolean) {
        val data = entryList.data
        val circuit = agentCircuit.data
        if (data != null && circuit != null) {
            val builder = ImmutableList.builder<SLInventoryEntry>()
            for (entry in data) {
                if (!entry.isFolderOrFolderLink) {
                    builder.add(entry)
                }
            }
            circuit.getModules().avatarAppearance.ChangeOutfit(builder.build(), replace, data.folder)
        }
    }

    private fun getFolderUUID(): UUID? {
        val args = arguments
        val str = args?.getString(FOLDER_ID_KEY)
        return UUIDPool.getUUID(str)
    }

    private fun getInventoryQuery(uuid: UUID?): InventoryQuery {
        val sortOrder = InventoryFragmentHelper.getSortOrder(context)
        return InventoryQuery.create(uuid, null, true, true, sortOrder == 0, null)
    }

    private fun getUserManager(): UserManager? {
        return ActivityUtils.getUserManager(arguments)
    }

    fun makeSelection(@NonNull uuid: UUID, @Nullable uuid2: UUID?): Bundle {
        val bundle = Bundle()
        ActivityUtils.setActiveAgentID(bundle, uuid)
        if (uuid2 != null) {
            bundle.putString(FOLDER_ID_KEY, uuid2.toString())
        }
        return bundle
    }

    private fun navigateToFolder(uuid: UUID) {
        arguments?.putString(FOLDER_ID_KEY, uuid.toString())
        showInventoryList(uuid)
    }

    private fun onAgentCircuit(circuit: SLAgentCircuit?) {
        var appearance: SLAvatarAppearance? = null
        if (adapter != null) {
            if (circuit != null) {
                appearance = circuit.getModules().avatarAppearance
            }
            adapter!!.setAvatarAppearance(appearance)
        }
    }

    private fun onInventoryEntryList(list: InventoryEntryList?) {
        if (list != null) {
            Debug.Printf("InventoryFragment (%s): onInventoryEntryList: %d entries", this, list.size)
            setTitle(list.title, null)
            adapter?.setData(list)
        }
        updateLoadingStatus()
    }

    private fun onLoadingStatusChanged(loading: Boolean?) {
        updateLoadingStatus()
    }

    private fun onRootFolderEntryList(list: InventoryEntryList?) {
        if (list != null) {
            for (entry in list) {
                if (entry.isFolder && entry.typeDefault == 48) { // 48 is Outfits folder type?
                    myOutfitsFolderUUID = entry.uuid
                    rootFolderEntryList.unsubscribe()
                    if (getFolderUUID() == null) {
                        showInventoryList(getFolderUUID())
                        return
                    }
                    return
                }
            }
        }
    }

    private fun onWornAttachmentsChanged(map: ImmutableMap<UUID, String>?) {
        adapter?.setWornAttachments(map)
    }

    private fun onWornOutfitFolder(uuid: UUID?) {
        adapter?.setWornOutfitFolder(uuid)
    }

    private fun onWornWearablesChanged(table: Table<SLWearableType, UUID, SLWearable>?) {
        adapter?.setWornWearables(table)
    }

    private fun showInventoryList(uuid: UUID?) {
        var currentUUID = uuid
        Debug.Printf("OutfitsNewFragment (%s): showInventoryList '%s'", this, currentUUID)
        
        val view = view
        entryList.unsubscribe()
        agentCircuit.unsubscribe()
        folderLoading.unsubscribe()
        rootFolderEntryList.unsubscribe()
        
        val userManager = getUserManager()
        if (userManager != null) {
            val database = userManager.getInventoryManager().getDatabase()
            wornAttachments.subscribe(userManager.getWornAttachmentsPool(), SubscriptionSingleKey.Value)
            wornWearables.subscribe(userManager.getWornWearablesPool(), SubscriptionSingleKey.Value)
            wornOutfitFolder.subscribe(userManager.wornOutfitLink(), SubscriptionSingleKey.Value)
            agentCircuit.subscribe(userManager.agentCircuits(), userManager.getUserID())
            
            if (currentUUID == null) {
                currentUUID = myOutfitsFolderUUID
            }
            
            Debug.Printf("After checking myoutfits: %s", currentUUID)
            
            if (currentUUID == null) {
                val rootFolder = userManager.getInventoryManager().getRootFolder()
                if (rootFolder != null) {
                     // database might be null check?
                     // Decompiled code assumed database valid if manager valid?
                     // InventoryDB database = ...
                     val specialFolder = database?.findSpecialFolder(rootFolder, 48)
                     if (specialFolder != null) {
                         myOutfitsFolderUUID = specialFolder.uuid
                         currentUUID = specialFolder.uuid
                         Debug.Printf("Found special folder: %s", currentUUID)
                     } else {
                         Debug.Printf("Special folder not found")
                     }
                }
            }
            
            if (currentUUID != null) {
                folderLoading.subscribe(userManager.getInventoryManager().getFolderLoading(), currentUUID)
                entryList.subscribe(userManager.getInventoryManager().getInventoryEntries(), getInventoryQuery(currentUUID))
                
                if (view != null && listHeader != null) {
                    val nameView = listHeader!!.findViewById<TextView>(R.id.itemNameTextView)
                    val iconView = listHeader!!.findViewById<ImageView>(R.id.itemTypeIconView)
                    val buttonsLayout = view.findViewById<View>(R.id.wear_buttons_layout)
                    
                    if (Objects.equal(currentUUID, myOutfitsFolderUUID)) {
                        nameView.setText(R.string.current_outfit)
                        iconView.setImageResource(R.drawable.inv_folder)
                        buttonsLayout.visibility = View.GONE
                    } else {
                        nameView.setText(R.string.inventory_go_up)
                        iconView.setImageResource(R.drawable.inv_up)
                        buttonsLayout.visibility = View.VISIBLE
                    }
                    listHeader!!.findViewById<View>(R.id.itemSubTypeIconView).visibility = View.GONE
                    listHeader!!.visibility = View.VISIBLE
                }
            } else {
                // Subscribe to root to find outfits folder
                rootFolderEntryList.subscribe(userManager.getInventoryManager().getInventoryEntries(), InventoryQuery.create(null, null, true, false, false, null))
                listHeader?.visibility = View.GONE
                view?.findViewById<View>(R.id.wear_buttons_layout)?.visibility = View.GONE
            }
            
            adapter?.setDatabase(database)
            
        } else {
            wornAttachments.unsubscribe()
            wornWearables.unsubscribe()
            rootFolderEntryList.unsubscribe()
            adapter?.setDatabase(null)
            wornOutfitFolder.unsubscribe()
        }
        updateLoadingStatus()
    }

    private fun updateLoadingStatus() {
        val context = context
        if (context != null) {
            var isLoading = false
            if (folderLoading.isSubscribed) {
                val data = folderLoading.data
                isLoading = data == true
            }
            
            val isEmpty = adapter?.isEmpty ?: true
            loadableMonitor.setExtraLoading(if (isEmpty) isLoading else false)
            loadableMonitor.setButteryProgressBar(if (isEmpty) false else isLoading)
            loadableMonitor.setEmptyMessage(isEmpty, context.getString(R.string.no_inventory_subentries))
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.outfit_folder_wear_button -> changeOutfit(true)
            R.id.outfit_folder_add_button -> changeOutfit(false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Debug.Printf("InventoryFragment: onCreateView")
        val view = inflater.inflate(R.layout.outfit_folder, container, false)
        
        loadableMonitor.setLoadingLayout(view.findViewById(R.id.loading_layout), getString(R.string.no_folder_selected), getString(R.string.inventory_folder_fail))
        
        val listView = view.findViewById<ListView>(R.id.item_list)
        listHeader = inflater.inflate(R.layout.inventory_item, listView, false) as ViewGroup
        
        adapter = InventoryFolderAdapter(inflater, true)
        adapter!!.setOnItemCheckboxClickListener(this)
        
        listView.addHeaderView(listHeader, listHeaderData, true)
        listView.adapter = adapter
        listView.onItemClickListener = itemClickListener
        
        view.findViewById<View>(R.id.outfit_folder_wear_button).setOnClickListener(this)
        view.findViewById<View>(R.id.outfit_folder_add_button).setOnClickListener(this)
        
        return view
    }

    @EventHandler
    fun onInventorySortOrderChanged(event: InventorySortOrderChangedEvent) {
        if (isFragmentStarted) {
            showInventoryList(getFolderUUID())
        }
    }

    override fun onItemCheckboxClicked(entry: SLInventoryEntry) {
        var item = entry
        val userManager = getUserManager()
        val circuit = agentCircuit.data
        
        if (circuit != null && userManager != null) {
            val appearance = circuit.getModules().avatarAppearance
            val database = userManager.getInventoryManager().getDatabase()
            
            if (database != null) {
                val resolved = database.resolveLink(item)
                if (resolved != null) {
                    item = resolved
                }
            }
            
            if (appearance.isItemWorn(item)) {
                if (item.isWearable) {
                    appearance.TakeItemOff(item)
                } else {
                    appearance.DetachInventoryItem(item)
                }
            } else if (item.isWearable) {
                appearance.WearItem(item, false)
            } else {
                appearance.AttachInventoryItem(item, 0, false)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getInstance().subscribe(this)
        showInventoryList(getFolderUUID())
    }

    override fun onStop() {
        showInventoryList(null)
        EventBus.getInstance().unsubscribe(this)
        super.onStop()
    }

    override fun setFragmentArgs(intent: Intent?, bundle: Bundle?) {
        Debug.Printf("InventoryFragment: setFragmentArgs '%s'", bundle)
        if (bundle != null) {
            arguments?.putAll(bundle)
        }
        if (isFragmentStarted) {
            showInventoryList(getFolderUUID())
        }
    }
}

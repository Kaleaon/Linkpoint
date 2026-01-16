package com.linkpoint.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.inventory.InventoryFolder
import com.linkpoint.inventory.InventoryItem
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Fragment for browsing inventory
 */
class InventoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InventoryAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyView: TextView
    private lateinit var breadcrumbText: TextView
    private lateinit var backButton: MaterialButton
    private lateinit var homeButton: FloatingActionButton

    private val inventoryManager by lazy { 
        LinkpointApp.getInstance().inventoryManager 
    }

    private val currentPath = mutableListOf<UUID>()
    private val currentItems = mutableListOf<InventoryAdapter.InventoryListItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setupAdapter()
        loadRootFolder()
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.inventory_recyclerview)
        progressBar = view.findViewById(R.id.progress_bar)
        emptyView = view.findViewById(R.id.empty_view)
        breadcrumbText = view.findViewById(R.id.breadcrumb_text)
        backButton = view.findViewById(R.id.back_button)
        homeButton = view.findViewById(R.id.home_button)

        backButton.setOnClickListener { navigateUp() }
        homeButton.setOnClickListener { navigateToRoot() }

        updateBreadcrumb()
    }

    private fun setupAdapter() {
        adapter = InventoryAdapter(
            onItemClick = { item -> onItemClicked(item) },
            onFolderClick = { folder -> onFolderClicked(folder) },
            onItemLongClick = { item -> onItemLongClicked(item) }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun loadRootFolder() {
        val rootFolderId = inventoryManager.getSystemFolder(
            com.linkpoint.inventory.InventoryManager.FOLDER_TYPE_ROOT
        ) ?: return

        currentPath.clear()
        loadFolder(rootFolderId)
    }

    private fun loadFolder(folderId: UUID) {
        currentPath.add(folderId)
        updateBreadcrumb()

        progressBar.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
        recyclerView.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val success = inventoryManager.fetchFolderContents(folderId)
                
                if (success) {
                    val folders = inventoryManager.getFolders(folderId)
                    val items = inventoryManager.getItems(folderId)

                    currentItems.clear()
                    folders.forEach { folder ->
                        currentItems.add(InventoryAdapter.InventoryListItem.FolderItem(folder))
                    }
                    items.forEach { item ->
                        currentItems.add(InventoryAdapter.InventoryListItem.ItemItem(item))
                    }

                    adapter.submitList(currentItems.toList())

                    if (currentItems.isEmpty()) {
                        emptyView.visibility = View.VISIBLE
                        emptyView.text = getString(R.string.inventory_empty)
                    } else {
                        recyclerView.visibility = View.VISIBLE
                    }
                } else {
                    emptyView.visibility = View.VISIBLE
                    emptyView.text = getString(R.string.inventory_load_failed)
                }
            } catch (e: Exception) {
                emptyView.visibility = View.VISIBLE
                emptyView.text = getString(R.string.inventory_error, e.message)
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun onFolderClicked(folder: InventoryFolder) {
        loadFolder(folder.folderId)
    }

    private fun onItemClicked(item: InventoryItem) {
        // Show item details dialog
        ItemDetailDialog.newInstance(item).show(
            childFragmentManager,
            "item_detail"
        )
    }

    private fun onItemLongClicked(item: InventoryItem) {
        // Show context menu with actions
        ItemContextMenu.show(requireView(), item) { action ->
            when (action) {
                ItemContextMenu.Action.WEAR -> wearItem(item)
                ItemContextMenu.Action.COPY -> copyItem(item)
                ItemContextMenu.Action.DELETE -> deleteItem(item)
                ItemContextMenu.Action.PROPERTIES -> showProperties(item)
            }
        }
    }

    private fun wearItem(item: InventoryItem) {
        // Implementation for wearing item
    }

    private fun copyItem(item: InventoryItem) {
        // Implementation for copying item
    }

    private fun deleteItem(item: InventoryItem) {
        // Implementation for deleting item
    }

    private fun showProperties(item: InventoryItem) {
        ItemPropertiesDialog.newInstance(item).show(
            childFragmentManager,
            "item_properties"
        )
    }

    private fun navigateUp() {
        if (currentPath.size > 1) {
            currentPath.removeAt(currentPath.size - 1)
            val parentFolderId = currentPath.last()
            currentPath.removeAt(currentPath.size - 1)
            loadFolder(parentFolderId)
        }
    }

    private fun navigateToRoot() {
        loadRootFolder()
    }

    private fun updateBreadcrumb() {
        if (currentPath.isEmpty()) {
            breadcrumbText.text = getString(R.string.inventory_root)
        } else {
            val currentFolderId = currentPath.last()
            val folder = inventoryManager.getFolder(currentFolderId)
            breadcrumbText.text = folder?.name ?: getString(R.string.unknown_folder)
        }

        backButton.isEnabled = currentPath.size > 1
    }

    companion object {
        fun newInstance() = InventoryFragment()
    }
}
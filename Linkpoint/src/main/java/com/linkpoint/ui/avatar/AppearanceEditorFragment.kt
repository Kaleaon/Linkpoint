package com.linkpoint.ui.avatar

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
import com.google.android.material.tabs.TabLayout
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.avatar.Wearable
import com.linkpoint.avatar.WearableType
import kotlinx.coroutines.launch

/**
 * Fragment for editing avatar appearance
 */
class AppearanceEditorFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WearableAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyView: TextView
    private lateinit var tabLayout: TabLayout

    private val avatarManager by lazy { 
        LinkpointApp.getInstance().avatarManager 
    }

    private var currentWearableType = WearableType.SHAPE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_appearance_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setupAdapter()
        setupTabs()
        loadWearables(currentWearableType)
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.wearable_recyclerview)
        progressBar = view.findViewById(R.id.progress_bar)
        emptyView = view.findViewById(R.id.empty_view)
        tabLayout = view.findViewById(R.id.wearable_tabs)
    }

    private fun setupAdapter() {
        adapter = WearableAdapter { wearable ->
            onWearableSelected(wearable)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupTabs() {
        // Use actual wearable types from Second Life
        val wearableTypes = listOf(
            WearableType.SHAPE,
            WearableType.SKIN,
            WearableType.HAIR,
            WearableType.EYES,
            WearableType.SHIRT,
            WearableType.PANTS,
            WearableType.SHOES,
            WearableType.JACKET
        )

        wearableTypes.forEach { type ->
            tabLayout.addTab(tabLayout.newTab().setText(type.name.lowercase().replaceFirstChar { it.uppercase() }))
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let { position ->
                    currentWearableType = wearableTypes[position]
                    loadWearables(currentWearableType)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadWearables(type: WearableType) {
        progressBar.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
        recyclerView.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val wearables = avatarManager.getWearables(type)
                
                if (wearables.isNotEmpty()) {
                    adapter.submitList(wearables)
                    recyclerView.visibility = View.VISIBLE
                } else {
                    emptyView.visibility = View.VISIBLE
                    emptyView.text = getString(R.string.no_wearables_available)
                }
            } catch (e: Exception) {
                emptyView.visibility = View.VISIBLE
                emptyView.text = getString(R.string.error_loading_wearables, e.message)
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun onWearableSelected(wearable: Wearable) {
        // Apply wearable to avatar
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                avatarManager.wear(wearable)
                // Show success message
                // Could also update preview
            } catch (e: Exception) {
                // Show error message
            }
        }
    }

    private fun String.capitalize(): String {
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    companion object {
        fun newInstance() = AppearanceEditorFragment()
    }
}
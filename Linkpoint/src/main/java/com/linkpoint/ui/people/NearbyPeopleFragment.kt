package com.linkpoint.ui.people

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
import com.linkpoint.world.NearbyUser
import com.linkpoint.world.WorldMap
import kotlinx.coroutines.launch

/**
 * Fragment for displaying nearby people
 */
class NearbyPeopleFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NearbyPeopleAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyView: TextView
    private lateinit var tabLayout: TabLayout

    private val worldMap by lazy { 
        LinkpointApp.getInstance().worldMap 
    }

    private var currentTab = TabType.ALL

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_nearby_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setupAdapter()
        setupTabs()
        loadNearbyPeople()
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.nearby_recyclerview)
        progressBar = view.findViewById(R.id.progress_bar)
        emptyView = view.findViewById(R.id.empty_view)
        tabLayout = view.findViewById(R.id.nearby_tabs)
    }

    private fun setupAdapter() {
        adapter = NearbyPeopleAdapter(
            onUserClick = { user -> showUserActions(user) },
            onUserLongClick = { user -> showUserActions(user) }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.all))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.friends))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.strangers))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let { position ->
                    currentTab = when (position) {
                        0 -> TabType.ALL
                        1 -> TabType.FRIENDS
                        2 -> TabType.STRANGERS
                        else -> TabType.ALL
                    }
                    loadNearbyPeople()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadNearbyPeople() {
        progressBar.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
        recyclerView.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val nearbyUsers = worldMap.getNearbyUsers()
                
                val filteredUsers = when (currentTab) {
                    TabType.ALL -> nearbyUsers
                    TabType.FRIENDS -> nearbyUsers.filter { it.isFriend }
                    TabType.STRANGERS -> nearbyUsers.filter { !it.isFriend }
                }

                if (filteredUsers.isNotEmpty()) {
                    adapter.submitList(filteredUsers)
                    recyclerView.visibility = View.VISIBLE
                } else {
                    emptyView.visibility = View.VISIBLE
                    emptyView.text = when (currentTab) {
                        TabType.ALL -> getString(R.string.no_nearby_people)
                        TabType.FRIENDS -> getString(R.string.no_nearby_friends)
                        TabType.STRANGERS -> getString(R.string.no_nearby_strangers)
                    }
                }
            } catch (e: Exception) {
                emptyView.visibility = View.VISIBLE
                emptyView.text = getString(R.string.error_loading_nearby_people, e.message)
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showUserActions(user: NearbyUser) {
        // Show user action dialog
        UserActionsDialog.newInstance(user.agentId, user.name, user.isFriend).show(
            childFragmentManager,
            "user_actions"
        )
    }

    enum class TabType {
        ALL,
        FRIENDS,
        STRANGERS
    }

    companion object {
        fun newInstance() = NearbyPeopleFragment()
    }
}
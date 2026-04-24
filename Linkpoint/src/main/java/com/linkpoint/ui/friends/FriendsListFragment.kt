package com.linkpoint.ui.friends

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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.world.Friend
import com.linkpoint.world.FriendEvent
import com.linkpoint.world.FriendshipOffer
import com.linkpoint.world.FriendsManager
import kotlinx.coroutines.launch

/**
 * Fragment for managing friends list
 */
class FriendsListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FriendsAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyView: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var addFriendButton: FloatingActionButton

    private val friendsManager by lazy { 
        LinkpointApp.getInstance().friendsManager 
    }

    private var currentTab = TabType.ALL

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friends_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setupAdapter()
        setupTabs()
        loadFriends()
        observeFriendEvents()
    }

    override fun onResume() {
        super.onResume()
        // The buddy-list parser populates FriendsManager asynchronously after login,
        // so refresh whenever the user comes back to this screen rather than relying
        // solely on the initial onViewCreated load.
        loadFriends()
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.friends_recyclerview)
        progressBar = view.findViewById(R.id.progress_bar)
        emptyView = view.findViewById(R.id.empty_view)
        tabLayout = view.findViewById(R.id.friends_tabs)
        addFriendButton = view.findViewById(R.id.add_friend_button)

        addFriendButton.setOnClickListener {
            showAddFriendDialog()
        }
    }

    private fun setupAdapter() {
        adapter = FriendsAdapter(
            onFriendClick = { friend -> showFriendActions(friend) },
            onFriendLongClick = { friend -> showFriendActions(friend) }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.all_friends))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.online))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.offline))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let { position ->
                    currentTab = when (position) {
                        0 -> TabType.ALL
                        1 -> TabType.ONLINE
                        2 -> TabType.OFFLINE
                        else -> TabType.ALL
                    }
                    loadFriends()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadFriends() {
        progressBar.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
        recyclerView.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val friends = friendsManager.getAllFriends()
                
                val filteredFriends = when (currentTab) {
                    TabType.ALL -> friends
                    TabType.ONLINE -> friends.filter { it.isOnline }
                    TabType.OFFLINE -> friends.filter { !it.isOnline }
                }

                if (filteredFriends.isNotEmpty()) {
                    adapter.submitList(filteredFriends)
                    recyclerView.visibility = View.VISIBLE
                } else {
                    emptyView.visibility = View.VISIBLE
                    emptyView.text = when (currentTab) {
                        TabType.ALL -> getString(R.string.no_friends)
                        TabType.ONLINE -> getString(R.string.no_online_friends)
                        TabType.OFFLINE -> getString(R.string.no_offline_friends)
                    }
                }
            } catch (e: Exception) {
                emptyView.visibility = View.VISIBLE
                emptyView.text = getString(R.string.error_loading_friends, e.message)
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun observeFriendEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            friendsManager.friendsFlow.collect { event ->
                when (event) {
                    is FriendEvent.OfferReceived -> {
                        // Show friendship offer dialog
                        showFriendshipOfferDialog(event.offer)
                    }
                    is FriendEvent.Added,
                    is FriendEvent.Removed,
                    is FriendEvent.OnlineStatusChanged,
                    is FriendEvent.RightsChanged,
                    is FriendEvent.NameUpdated -> {
                        // Refresh friends list
                        loadFriends()
                    }
                }
            }
        }
    }

    private fun showFriendActions(friend: Friend) {
        FriendActionsDialog.newInstance(friend).show(
            childFragmentManager,
            "friend_actions"
        )
    }

    private fun showAddFriendDialog() {
        AddFriendDialog.newInstance().show(
            childFragmentManager,
            "add_friend"
        )
    }

    private fun showFriendshipOfferDialog(offer: FriendshipOffer) {
        FriendshipOfferDialog.newInstance(offer).show(
            childFragmentManager,
            "friendship_offer"
        )
    }

    enum class TabType {
        ALL,
        ONLINE,
        OFFLINE
    }

    companion object {
        fun newInstance() = FriendsListFragment()
    }
}

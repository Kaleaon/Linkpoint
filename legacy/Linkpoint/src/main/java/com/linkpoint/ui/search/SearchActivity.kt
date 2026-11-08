package com.linkpoint.ui.search

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.model.search.*
import com.linkpoint.world.*
import kotlinx.coroutines.launch

/**
 * Search for people, places, groups, and events
 */
class SearchActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var tabLayout: TabLayout
    private lateinit var resultsRecycler: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyText: TextView

    private lateinit var searchManager: SearchManager

    private var currentTab = 0
    private var lastQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchManager = (application as LinkpointApp).searchManager

        setupViews()
        setupTabs()
    }

    private fun setupViews() {
        searchInput = findViewById(R.id.searchInput)
        searchButton = findViewById(R.id.searchButton)
        tabLayout = findViewById(R.id.tabLayout)
        resultsRecycler = findViewById(R.id.resultsRecycler)
        progressBar = findViewById(R.id.progressBar)
        emptyText = findViewById(R.id.emptyText)

        resultsRecycler.layoutManager = LinearLayoutManager(this)

        searchButton.setOnClickListener { performSearch() }

        searchInput.setOnEditorActionListener { _, _, _ ->
            performSearch()
            true
        }
    }

    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("People"))
        tabLayout.addTab(tabLayout.newTab().setText("Places"))
        tabLayout.addTab(tabLayout.newTab().setText("Groups"))
        tabLayout.addTab(tabLayout.newTab().setText("Events"))
        tabLayout.addTab(tabLayout.newTab().setText("Destinations"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentTab = tab.position
                if (lastQuery.isNotEmpty()) {
                    performSearch()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun performSearch() {
        val query = searchInput.text.toString()
        if (query.isEmpty() && currentTab != 4) return // Destinations doesn't need query

        lastQuery = query

        progressBar.visibility = View.VISIBLE
        resultsRecycler.visibility = View.GONE
        emptyText.visibility = View.GONE

        lifecycleScope.launch {
            when (currentTab) {
                0 -> searchPeople(query)
                1 -> searchPlaces(query)
                2 -> searchGroups(query)
                3 -> searchEvents(query)
                4 -> loadDestinations()
            }
        }
    }

    private suspend fun searchPeople(query: String) {
        val results = searchManager.searchPeople(query)
        showResults(results.results)
    }

    private suspend fun searchPlaces(query: String) {
        val results = searchManager.searchPlaces(query)
        showResults(results.results)
    }

    private suspend fun searchGroups(query: String) {
        val results = searchManager.searchGroups(query)
        showResults(results.results)
    }

    private suspend fun searchEvents(query: String) {
        val results = searchManager.searchEvents(query)
        showResults(results.results)
    }

    private suspend fun loadDestinations() {
        val results = searchManager.getDestinations()
        showResults(results)
    }

    private fun showResults(results: List<SearchResult>) {
        progressBar.visibility = View.GONE

        if (results.isEmpty()) {
            emptyText.visibility = View.VISIBLE
            emptyText.text = "No results found"
            resultsRecycler.visibility = View.GONE
        } else {
            emptyText.visibility = View.GONE
            resultsRecycler.visibility = View.VISIBLE
            resultsRecycler.adapter = SearchResultsAdapter(results) { result ->
                onResultClicked(result)
            }
        }
    }

    private fun onResultClicked(result: SearchResult) {
        when (result) {
            is PersonResult -> showProfile(result.agentId)
            is PlaceResult -> teleportToPlace(result)
            is GroupResult -> showGroupProfile(result.groupId)
            is EventResult -> showEventDetails(result)
            is DestinationResult -> teleportToDestination(result)
            else -> {}
        }
    }

    private fun showProfile(agentId: java.util.UUID) {
        // Navigate to profile activity
    }

    private fun teleportToPlace(place: PlaceResult) {
        lifecycleScope.launch {
            val protocol = (application as LinkpointApp).protocol
            protocol.teleport(place.region, 128f, 128f, 0f)
            Toast.makeText(this@SearchActivity, "Teleporting to ${place.name}...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showGroupProfile(groupId: java.util.UUID) {
        // Navigate to group profile
    }

    private fun showEventDetails(event: EventResult) {
        // Show event details dialog
        android.app.AlertDialog.Builder(this)
            .setTitle(event.name)
            .setMessage("${event.description}\n\nRegion: ${event.region}")
            .setPositiveButton("Teleport") { _, _ ->
                lifecycleScope.launch {
                    val protocol = (application as LinkpointApp).protocol
                    protocol.teleport(event.region, 128f, 128f, 0f)
                }
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun teleportToDestination(dest: DestinationResult) {
        lifecycleScope.launch {
            val protocol = (application as LinkpointApp).protocol
            protocol.teleport(dest.region, 128f, 128f, 0f)
            Toast.makeText(this@SearchActivity, "Teleporting to ${dest.name}...", Toast.LENGTH_SHORT).show()
        }
    }
}

class SearchResultsAdapter(
    private val results: List<SearchResult>,
    private val onClick: (SearchResult) -> Unit
) : RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.icon)
        val title: TextView = view.findViewById(R.id.title)
        val subtitle: TextView = view.findViewById(R.id.subtitle)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]

        when (result) {
            is PersonResult -> {
                holder.title.text = result.displayName.ifEmpty { result.userName }
                holder.subtitle.text = if (result.isOnline) "Online" else "Offline"
            }
            is PlaceResult -> {
                holder.title.text = result.name
                holder.subtitle.text = "${result.region} - Traffic: ${result.traffic.toInt()}"
            }
            is GroupResult -> {
                holder.title.text = result.name
                holder.subtitle.text = "${result.memberCount} members"
            }
            is EventResult -> {
                holder.title.text = result.name
                holder.subtitle.text = result.region
            }
            is DestinationResult -> {
                holder.title.text = result.name
                holder.subtitle.text = result.category
            }
            else -> {
                holder.title.text = result.name
                holder.subtitle.text = result.description
            }
        }

        holder.itemView.setOnClickListener { onClick(result) }
    }

    override fun getItemCount() = results.size
}

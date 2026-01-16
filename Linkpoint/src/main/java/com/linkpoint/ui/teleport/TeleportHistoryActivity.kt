package com.linkpoint.ui.teleport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.core.TeleportHistoryEntry
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Activity for viewing and managing teleport history.
 */
class TeleportHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TeleportHistoryAdapter
    private lateinit var emptyView: TextView
    private lateinit var setHomeButton: FloatingActionButton
    private lateinit var homeInfoText: TextView

    private val app by lazy { LinkpointApp.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teleport_history)
        
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.teleport_history)
        }
        
        setupViews()
        setupAdapter()
        observeHistory()
        updateHomeInfo()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.history_recyclerview)
        emptyView = findViewById(R.id.empty_view)
        setHomeButton = findViewById(R.id.set_home_button)
        homeInfoText = findViewById(R.id.home_info)

        setHomeButton.setOnClickListener {
            app.sessionManager.setHomeHere()
            updateHomeInfo()
            Toast.makeText(this, R.string.home_set, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAdapter() {
        adapter = TeleportHistoryAdapter(
            onItemClick = { entry -> teleportTo(entry) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun observeHistory() {
        lifecycleScope.launch {
            app.sessionManager.teleportHistory.collectLatest { history ->
                if (history.isEmpty()) {
                    emptyView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    adapter.submitList(history)
                }
            }
        }
    }

    private fun updateHomeInfo() {
        val home = app.sessionManager.getHomeLocation()
        homeInfoText.text = if (home != null) {
            "Home: ${home.regionName} (${home.x}, ${home.y}, ${home.z})"
        } else {
            "No home location set"
        }
    }

    private fun teleportTo(entry: TeleportHistoryEntry) {
        Toast.makeText(this, "Teleporting to ${entry.regionName}...", Toast.LENGTH_SHORT).show()
        // Would trigger teleport via capabilities
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

/**
 * Adapter for teleport history list.
 */
class TeleportHistoryAdapter(
    private val onItemClick: (TeleportHistoryEntry) -> Unit
) : ListAdapter<TeleportHistoryEntry, TeleportHistoryAdapter.ViewHolder>(DiffCallback()) {

    private val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_teleport_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val regionName: TextView = itemView.findViewById(R.id.region_name)
        private val coordinates: TextView = itemView.findViewById(R.id.coordinates)
        private val timestamp: TextView = itemView.findViewById(R.id.timestamp)

        fun bind(entry: TeleportHistoryEntry) {
            regionName.text = entry.regionName
            coordinates.text = "(${entry.x}, ${entry.y}, ${entry.z})"
            timestamp.text = dateFormat.format(Date(entry.timestamp))

            itemView.setOnClickListener { onItemClick(entry) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TeleportHistoryEntry>() {
        override fun areItemsTheSame(oldItem: TeleportHistoryEntry, newItem: TeleportHistoryEntry): Boolean {
            return oldItem.regionName == newItem.regionName && oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: TeleportHistoryEntry, newItem: TeleportHistoryEntry): Boolean {
            return oldItem == newItem
        }
    }
}

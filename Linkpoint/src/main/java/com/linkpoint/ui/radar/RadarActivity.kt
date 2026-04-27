package com.linkpoint.ui.radar

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Activity that displays a radar showing nearby avatars and objects.
 *
 * Legacy entry point retained during Compose migration.
 * Removal target: 2026.09.
 */
@Deprecated(
    message = "Legacy Activity entry point. Use RadarCompose-based Compose navigation.",
    replaceWith = ReplaceWith("RadarCompose")
)
class RadarActivity : AppCompatActivity() {

    private lateinit var radarView: RadarView
    private lateinit var rangeSeekBar: SeekBar
    private lateinit var rangeText: TextView
    private lateinit var blipList: RecyclerView
    private lateinit var adapter: BlipListAdapter

    private val app by lazy { LinkpointApp.getInstance() }
    
    private var currentRange = 96f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_radar)
        
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.radar)
        }
        
        setupViews()
        startRadarUpdates()
    }

    private fun setupViews() {
        radarView = findViewById(R.id.radar_view)
        rangeSeekBar = findViewById(R.id.range_seekbar)
        rangeText = findViewById(R.id.range_text)
        blipList = findViewById(R.id.blip_list)
        
        // Setup range control
        rangeSeekBar.max = 256
        rangeSeekBar.progress = currentRange.toInt()
        rangeText.text = "${currentRange.toInt()}m"
        
        rangeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val range = progress.coerceAtLeast(16).toFloat()
                currentRange = range
                radarView.setRadarRange(range)
                rangeText.text = "${range.toInt()}m"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // Setup blip list
        adapter = BlipListAdapter { blip ->
            // On blip click - show profile or teleport to
        }
        blipList.layoutManager = LinearLayoutManager(this)
        blipList.adapter = adapter
    }

    private fun startRadarUpdates() {
        lifecycleScope.launch {
            while (isActive) {
                updateRadar()
                delay(1000) // Update every second
            }
        }
    }

    private fun updateRadar() {
        if (!app.isAvatarManagerInitialized()) return
        
        val myAvatar = app.avatarManager.getMyAvatar()
        val myPosition = myAvatar?.position ?: LLVector3(128f, 128f, 25f)
        
        // Get heading from movement controller
        val heading = if (app.isAvatarManagerInitialized()) {
            Math.toRadians(app.avatarManager.movementController.getHeadingDegrees().toDouble()).toFloat()
        } else {
            0f
        }
        radarView.heading = heading
        
        val blips = mutableListOf<RadarBlip>()
        
        // Add avatars
        for (avatar in app.avatarManager.getAllAvatars()) {
            if (avatar.agentId == myAvatar?.agentId) continue
            
            val isFriend = if (app.agentId != null) {
                try {
                    app.friendsManager.isFriend(avatar.agentId)
                } catch (e: Exception) {
                    false
                }
            } else {
                false
            }
            
            val blip = RadarUtils.createBlip(
                id = avatar.agentId.toString(),
                name = avatar.displayName ?: avatar.userName ?: "Avatar",
                isFriend = isFriend,
                isObject = false,
                selfPosition = myPosition,
                targetPosition = avatar.position
            )
            
            if (blip.distance <= currentRange) {
                blips.add(blip)
            }
        }
        
        // Sort by distance
        blips.sortBy { it.distance }
        
        // Update views
        radarView.updateBlips(blips)
        adapter.submitList(blips.toList())
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
 * Adapter for the blip list.
 */
class BlipListAdapter(
    private val onBlipClick: (RadarBlip) -> Unit
) : androidx.recyclerview.widget.ListAdapter<RadarBlip, BlipListAdapter.ViewHolder>(
    object : androidx.recyclerview.widget.DiffUtil.ItemCallback<RadarBlip>() {
        override fun areItemsTheSame(oldItem: RadarBlip, newItem: RadarBlip) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: RadarBlip, newItem: RadarBlip) = oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_radar_blip, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.blip_name)
        private val distanceText: TextView = itemView.findViewById(R.id.blip_distance)
        private val altitudeText: TextView = itemView.findViewById(R.id.blip_altitude)
        private val typeIndicator: View = itemView.findViewById(R.id.type_indicator)

        fun bind(blip: RadarBlip) {
            nameText.text = blip.name
            distanceText.text = "${blip.distance.toInt()}m"
            
            val altSign = if (blip.altitude >= 0) "+" else ""
            altitudeText.text = "($altSign${blip.altitude.toInt()}m)"
            
            // Set indicator color
            val color = when (blip.type) {
                BlipType.FRIEND -> android.graphics.Color.GREEN
                BlipType.STRANGER -> android.graphics.Color.CYAN
                BlipType.OBJECT -> android.graphics.Color.RED
                BlipType.SELF -> android.graphics.Color.YELLOW
            }
            typeIndicator.setBackgroundColor(color)

            itemView.setOnClickListener { onBlipClick(blip) }
        }
    }
}

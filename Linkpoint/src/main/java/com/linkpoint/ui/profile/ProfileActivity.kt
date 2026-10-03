package com.linkpoint.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.users.DisplayName
import com.linkpoint.users.DisplayNameOutputMode
import com.linkpoint.world.AvatarProfile
import com.linkpoint.world.GroupMembership
import com.linkpoint.world.ProfilePick
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Avatar profile viewer and editor
 */
class ProfileActivity : AppCompatActivity() {
    
    companion object {
        const val EXTRA_AGENT_ID = "agent_id"
        const val EXTRA_IS_OWN = "is_own"
    }
    
    private lateinit var profileImage: ImageView
    private lateinit var displayName: TextView
    private lateinit var userName: TextView
    private lateinit var bornDate: TextView
    private lateinit var partnerName: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var contentFrame: FrameLayout
    
    private lateinit var aboutPanel: View
    private lateinit var firstLifePanel: View
    private lateinit var interestsPanel: View
    private lateinit var picksPanel: View
    private lateinit var groupsPanel: View
    
    private var agentId: UUID? = null
    private var isOwnProfile = false
    private var profile: AvatarProfile? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        agentId = intent.getStringExtra(EXTRA_AGENT_ID)?.let { UUID.fromString(it) }
        isOwnProfile = intent.getBooleanExtra(EXTRA_IS_OWN, false)
        
        setupViews()
        setupTabs()
        loadProfile()
    }
    
    private fun setupViews() {
        profileImage = findViewById(R.id.profileImage)
        displayName = findViewById(R.id.displayName)
        userName = findViewById(R.id.userName)
        bornDate = findViewById(R.id.bornDate)
        partnerName = findViewById(R.id.partnerName)
        tabLayout = findViewById(R.id.tabLayout)
        contentFrame = findViewById(R.id.contentFrame)
        
        // Inflate panels
        aboutPanel = layoutInflater.inflate(R.layout.panel_profile_about, contentFrame, false)
        firstLifePanel = layoutInflater.inflate(R.layout.panel_profile_firstlife, contentFrame, false)
        interestsPanel = layoutInflater.inflate(R.layout.panel_profile_interests, contentFrame, false)
        picksPanel = layoutInflater.inflate(R.layout.panel_profile_picks, contentFrame, false)
        groupsPanel = layoutInflater.inflate(R.layout.panel_profile_groups, contentFrame, false)
        
        // Action buttons
        findViewById<Button>(R.id.btnIM)?.setOnClickListener { startIM() }
        findViewById<Button>(R.id.btnFriend)?.setOnClickListener { offerFriendship() }
        findViewById<Button>(R.id.btnPay)?.setOnClickListener { showPayDialog() }
        findViewById<Button>(R.id.btnTeleport)?.setOnClickListener { offerTeleport() }
        
        // Hide action buttons for own profile
        if (isOwnProfile) {
            findViewById<View>(R.id.actionButtons)?.visibility = View.GONE
        }
    }
    
    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("About"))
        tabLayout.addTab(tabLayout.newTab().setText("1st Life"))
        tabLayout.addTab(tabLayout.newTab().setText("Interests"))
        tabLayout.addTab(tabLayout.newTab().setText("Picks"))
        tabLayout.addTab(tabLayout.newTab().setText("Groups"))
        
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                showPanel(tab.position)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        
        showPanel(0)
    }
    
    private fun showPanel(position: Int) {
        contentFrame.removeAllViews()
        
        val panel = when (position) {
            0 -> aboutPanel
            1 -> firstLifePanel
            2 -> interestsPanel
            3 -> picksPanel
            4 -> groupsPanel
            else -> aboutPanel
        }
        
        contentFrame.addView(panel)
        updatePanel(position)
    }
    
    private fun loadProfile() {
        val id = agentId ?: return
        
        lifecycleScope.launch {
            val profileManager = (application as LinkpointApp).profileManager
            profile = profileManager.getAvatarProfile(id)
            
            profile?.let { updateUI(it) }
        }
    }
    
    private fun updateUI(profile: AvatarProfile) {
        val app = application as LinkpointApp
        val policy = app.displayNameFormattingPolicy.policy
        val formattedPrimaryName = DisplayName(
            agentId = profile.agentId,
            username = profile.userName,
            displayName = profile.displayName,
            isDefault = profile.displayName.isBlank(),
            nextUpdate = 0L
        ).format(policy)
        displayName.text = formattedPrimaryName.ifEmpty { "Unknown" }
        userName.text = profile.userName
        bornDate.text = "Born: ${profile.bornOn}"
        
        if (profile.partner != null) {
            partnerName.text = "Partner: (loading...)"
            lifecycleScope.launch {
                val rawName = app.profileManager.getDisplayName(profile.partner)
                val formattedName = rawName?.let {
                    DisplayName(
                        agentId = profile.partner,
                        username = it,
                        displayName = null,
                        isDefault = true,
                        nextUpdate = 0L
                    ).format(policy.copy(outputMode = DisplayNameOutputMode.LEGACY_FALLBACK))
                }
                partnerName.text = "Partner: ${formattedName ?: "(unknown)"}"
            }
        } else {
            partnerName.visibility = View.GONE
        }
        
        // Load profile image
        profile.profileImage?.let { imageId ->
            loadProfileImage(imageId)
        }
        
        updatePanel(tabLayout.selectedTabPosition)
    }
    
    private fun updatePanel(position: Int) {
        val p = profile ?: return
        
        when (position) {
            0 -> {
                aboutPanel.findViewById<TextView>(R.id.aboutText)?.text = 
                    p.aboutText.ifEmpty { "(No about text)" }
            }
            1 -> {
                firstLifePanel.findViewById<TextView>(R.id.firstLifeText)?.text = 
                    p.firstLifeText.ifEmpty { "(No first life info)" }
            }
            2 -> {
                interestsPanel.findViewById<TextView>(R.id.wantToText)?.text = 
                    p.interests.wantToText.ifEmpty { "(None)" }
                interestsPanel.findViewById<TextView>(R.id.skillsText)?.text = 
                    p.interests.skillsText.ifEmpty { "(None)" }
                interestsPanel.findViewById<TextView>(R.id.languagesText)?.text = 
                    p.interests.languagesText.ifEmpty { "(None)" }
            }
            3 -> {
                val picksList = picksPanel.findViewById<ListView>(R.id.picksList)
                picksList?.adapter = PicksAdapter(p.picks)
            }
            4 -> {
                val groupsList = groupsPanel.findViewById<ListView>(R.id.groupsList)
                groupsList?.adapter = GroupsAdapter(p.groups)
            }
        }
    }
    
    private fun loadProfileImage(imageId: UUID) {
        lifecycleScope.launch {
            // Would load texture
        }
    }
    
    private fun startIM() {
        val id = agentId ?: return
        val name = profile?.displayName ?: "Unknown"
        
        // Start IM session
        val imManager = (application as LinkpointApp).imManager
        val sessionId = imManager.startP2PSession(id, name)
        
        // Navigate to IM activity
        // startActivity(...)
    }
    
    private fun offerFriendship() {
        val id = agentId ?: return
        
        lifecycleScope.launch {
            val profileManager = (application as LinkpointApp).profileManager
            val success = profileManager.offerFriendship(id)
            
            if (success) {
                Toast.makeText(this@ProfileActivity, "Friendship offered", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showPayDialog() {
        // Show payment dialog
    }
    
    private fun offerTeleport() {
        val id = agentId ?: return
        
        // Offer teleport to avatar
    }
    
    inner class PicksAdapter(private val picks: List<ProfilePick>) : BaseAdapter() {
        override fun getCount() = picks.size
        override fun getItem(position: Int) = picks[position]
        override fun getItemId(position: Int) = position.toLong()
        
        override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
            val view = convertView ?: layoutInflater.inflate(
                android.R.layout.simple_list_item_2, parent, false
            )
            
            val pick = picks[position]
            view.findViewById<TextView>(android.R.id.text1).text = pick.name
            view.findViewById<TextView>(android.R.id.text2).text = pick.simName
            
            return view
        }
    }
    
    inner class GroupsAdapter(private val groups: List<GroupMembership>) : BaseAdapter() {
        override fun getCount() = groups.size
        override fun getItem(position: Int) = groups[position]
        override fun getItemId(position: Int) = position.toLong()
        
        override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
            val view = convertView ?: layoutInflater.inflate(
                android.R.layout.simple_list_item_1, parent, false
            )
            
            val group = groups[position]
            view.findViewById<TextView>(android.R.id.text1).text = group.name
            
            return view
        }
    }
}

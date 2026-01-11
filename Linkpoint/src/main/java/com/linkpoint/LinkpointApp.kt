package com.linkpoint

import android.app.Application
import android.util.Log
import com.linkpoint.assets.*
import com.linkpoint.avatar.AvatarManager
import com.linkpoint.chat.ChatManager
import com.linkpoint.chat.IMManager
import com.linkpoint.core.GridManager
import com.linkpoint.core.SessionManager
import com.linkpoint.inventory.GestureManager
import com.linkpoint.inventory.InventoryManager
import com.linkpoint.inventory.OutfitManager
import com.linkpoint.network.SecondLifeProtocol
import com.linkpoint.objects.BuildTools
import com.linkpoint.objects.ObjectManager
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.messages.UDPConnection
import com.linkpoint.render.RenderManager
import com.linkpoint.voice.VoiceManager
import com.linkpoint.world.ParcelManager
import com.linkpoint.world.ProfileManager
import com.linkpoint.world.SearchManager
import com.linkpoint.world.WorldMap
import com.linkpoint.xr.XRManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.util.UUID

/**
 * Main Application class for Linkpoint - Second Life viewer for Android and XR
 * 
 * Based on Lumiya's architecture, modernized for:
 * - Kotlin
 * - Filament rendering
 * - Android XR / VR support
 * - WebRTC voice
 */
class LinkpointApp : Application() {
    
    companion object {
        private const val TAG = "LinkpointApp"
        
        @Volatile
        private var instance: LinkpointApp? = null
        
        fun getInstance(): LinkpointApp {
            return instance ?: throw IllegalStateException("Application not initialized")
        }
    }
    
    // Application-wide coroutine scope for background operations
    val applicationScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Core managers
    lateinit var gridManager: GridManager
        private set
    lateinit var sessionManager: SessionManager
        private set
    lateinit var renderManager: RenderManager
        private set
    lateinit var xrManager: XRManager
        private set
    lateinit var protocol: SecondLifeProtocol
        private set
    
    // Protocol layer
    lateinit var capabilityManager: CapabilityManager
        private set
    lateinit var udpConnection: UDPConnection
        private set
    
    // Asset system
    lateinit var assetCache: AssetCache
        private set
    lateinit var textureManager: TextureManager
        private set
    lateinit var meshManager: MeshManager
        private set
    lateinit var animationManager: AnimationManager
        private set
    lateinit var soundManager: SoundManager
        private set
    
    // Avatar system
    lateinit var avatarManager: AvatarManager
        private set
    
    // Chat & IM
    lateinit var chatManager: ChatManager
        private set
    lateinit var imManager: IMManager
        private set
    
    // Inventory
    lateinit var inventoryManager: InventoryManager
        private set
    lateinit var outfitManager: OutfitManager
        private set
    lateinit var gestureManager: GestureManager
        private set
    
    // World
    lateinit var worldMap: WorldMap
        private set
    lateinit var searchManager: SearchManager
        private set
    lateinit var profileManager: ProfileManager
        private set
    lateinit var parcelManager: ParcelManager
        private set
    
    // Objects
    lateinit var objectManager: ObjectManager
        private set
    lateinit var buildTools: BuildTools
        private set
    
    // Voice
    lateinit var voiceManager: VoiceManager
        private set
    
    // Agent ID (set after login)
    var agentId: UUID? = null
        private set
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.i(TAG, "Linkpoint application starting...")
        
        initializeManagers()
        
        Log.i(TAG, "Linkpoint initialized successfully")
    }
    
    private fun initializeManagers() {
        Log.d(TAG, "Initializing managers...")
        
        // Grid management (login, multiple grids)
        gridManager = GridManager(this)
        
        // Session management (active connection state)
        sessionManager = SessionManager(this)
        
        // Protocol components
        capabilityManager = CapabilityManager()
        udpConnection = UDPConnection()
        
        // Protocol handler
        protocol = SecondLifeProtocol(this)
        
        // Rendering (Filament-based)
        renderManager = RenderManager(this)
        
        // XR/VR support
        xrManager = XRManager(this)
        
        // Asset system
        assetCache = AssetCache(this)
        textureManager = TextureManager(this, assetCache, capabilityManager)
        meshManager = MeshManager(assetCache, capabilityManager)
        animationManager = AnimationManager(this, assetCache)
        soundManager = SoundManager(this, assetCache)
        
        // World features
        worldMap = WorldMap(capabilityManager)
        searchManager = SearchManager(capabilityManager)
        profileManager = ProfileManager(capabilityManager)
        parcelManager = ParcelManager(udpConnection)
        
        // Voice
        voiceManager = VoiceManager(this, capabilityManager)
        
        Log.d(TAG, "Core managers initialized")
    }
    
    /**
     * Initialize managers that require agent ID (call after login)
     */
    fun initializeAgentManagers(agentId: UUID) {
        this.agentId = agentId
        
        Log.d(TAG, "Initializing agent-specific managers for $agentId")
        
        // Avatar manager
        avatarManager = AvatarManager(
            this, meshManager, textureManager, animationManager, capabilityManager
        )
        avatarManager.setMyAgentId(agentId)
        
        // Chat manager
        chatManager = ChatManager(udpConnection, agentId)
        
        // IM manager
        imManager = IMManager(udpConnection, capabilityManager, agentId)
        
        // Inventory
        inventoryManager = InventoryManager(capabilityManager, agentId)
        
        // Outfit manager (needs baker from avatar manager)
        val myAvatar = avatarManager.getMyAvatar()
        if (myAvatar != null) {
            outfitManager = OutfitManager(inventoryManager, myAvatar.baker)
        }
        
        // Gesture manager
        gestureManager = GestureManager(assetCache, animationManager, soundManager) { message ->
            chatManager.sendChat(message)
        }
        
        // Object manager
        objectManager = ObjectManager(udpConnection)
        buildTools = BuildTools(objectManager)
        
        Log.d(TAG, "Agent managers initialized")
    }
    
    override fun onTerminate() {
        super.onTerminate()
        Log.i(TAG, "Linkpoint application terminating")
        
        // Cleanup
        voiceManager.shutdown()
        if (::avatarManager.isInitialized) avatarManager.shutdown()
        if (::objectManager.isInitialized) objectManager.shutdown()
        if (::chatManager.isInitialized) chatManager.shutdown()
        if (::imManager.isInitialized) imManager.shutdown()
        if (::inventoryManager.isInitialized) inventoryManager.shutdown()
        if (::gestureManager.isInitialized) gestureManager.shutdown()
        
        worldMap.shutdown()
        searchManager.shutdown()
        profileManager.shutdown()
        parcelManager.shutdown()
        
        soundManager.shutdown()
        animationManager.shutdown()
        meshManager.shutdown()
        textureManager.shutdown()
        
        capabilityManager.shutdown()
        udpConnection.disconnect()
        
        xrManager.shutdown()
        renderManager.shutdown()
        
        // Shutdown protocol and networking
        protocol.shutdown()
        
        sessionManager.disconnect()
        
        // Cancel application scope
        applicationScope.cancel()
    }
    
    /**
     * Check if XR mode is available on this device
     */
    fun isXRAvailable(): Boolean = xrManager.isAvailable()
    
    /**
     * Check if currently connected to a grid
     */
    fun isConnected(): Boolean = sessionManager.isConnected()
    
    /**
     * Get the current region name
     */
    fun getCurrentRegion(): String? = sessionManager.currentRegion.value?.name
}

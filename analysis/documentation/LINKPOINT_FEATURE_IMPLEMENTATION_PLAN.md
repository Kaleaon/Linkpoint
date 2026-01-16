# Linkpoint Feature Implementation Plan

## Executive Summary

Based on Lumiya decompilation, Second Life's WebRTC migration, and Firestorm's implementation, this document outlines the comprehensive implementation plan for Linkpoint's remaining core features.

---

## Part 1: Modern Voice Chat (WebRTC)

### Background
**Current State:** Second Life is migrating from Vivox (SIP-based) to WebRTC for voice chat.

**Benefits of WebRTC:**
- Higher quality audio
- Stereo audio support
- Noise reduction
- Automatic gain control
- Echo cancellation
- Improved security
- Modern, open-source protocol
- Better mobile support

### Implementation Strategy

#### Option 1: Use WebRTC Library (Recommended)
**Library:** `org.webrtc:google-webrtc:1.0.+` (official WebRTC Android SDK)

**Architecture:**
```
VoiceManager
  ├── WebRTCSessionManager
  │   ├── PeerConnectionManager
  │   ├── AudioTrackManager
  │   ├── MediaStreamManager
  │   └── SignalingManager
  ├── VoiceCodecAdapter
  └── VoiceUIController
```

**Key Components:**

1. **VoiceManager.kt**
```kotlin
class VoiceManager {
    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private lateinit var audioManager: AudioManager
    private var peerConnection: PeerConnection? = null
    
    // Initialize WebRTC
    fun initialize(context: Context) {
        // 1. Create audio constraints
        val audioConstraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("googEchoCancellation", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("googNoiseSuppression", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("googHighpassFilter", "true"))
        }
        
        // 2. Create peer connection factory
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(true)
                .createInitializationOptions()
        )
        
        peerConnectionFactory = PeerConnectionFactory.builder()
            .setAudioConstraints(audioConstraints)
            .createPeerConnectionFactory()
    }
    
    // Connect to voice session
    fun connect(regionID: String, position: Vector3) {
        // Implementation details...
    }
}
```

2. **Integration with Second Life Protocol**
```kotlin
// In SLAgentCircuit.kt equivalent
class VoiceModule {
    fun handleProvisionVoiceAccount(response: ProvisionVoiceAccountResponse) {
        // Get WebRTC credentials from response
        val channelURI = response.channelURI
        val sdpOffer = response.sdpOffer
        
        // Connect using WebRTC
        voiceManager.connect(channelURI, sdpOffer)
    }
}
```

---

## Part 2: Inventory System

### Current Status
**Implemented:** Basic folder structure, item representation, fetch from server
**Missing:** Full tree navigation UI, bulk operations, drag-and-drop

### Architecture (Based on Lumiya)

```
InventorySystem
├── InventoryManager (Main controller)
├── InventoryFolder (Folder representation)
├── InventoryItem (Item representation)
├── InventoryFetcher (Fetch from server)
├── InventoryDownloadManager (Download data)
└── InventoryUI (User interface)
```

### Implementation Plan

#### 1. Enhanced Inventory Manager
```kotlin
class InventoryManager {
    private val rootFolder: InventoryFolder
    private val folderMap: MutableMap<UUID, InventoryFolder>
    private val itemMap: MutableMap<UUID, InventoryItem>
    private val downloadManager: InventoryDownloadManager
    
    // Fetch entire inventory tree
    suspend fun fetchInventory(): InventoryFolder {
        // 1. Fetch root folder
        val root = capabilityManager.fetchInventoryDescendents(rootFolderID)
        
        // 2. Fetch all subfolders recursively
        fetchFolderTree(root)
        
        // 3. Store in database/cache
        cacheInventory(root)
        
        return root
    }
    
    // Get folder contents
    fun getFolderContents(folderID: UUID): List<InventoryItem> {
        return folderMap[folderID]?.items ?: emptyList()
    }
    
    // Search inventory
    fun searchItems(query: String): List<InventoryItem> {
        return itemMap.values.filter { 
            it.name.contains(query, ignoreCase = true) 
        }
    }
}
```

#### 2. Inventory UI Components

**InventoryFragment.kt**
```kotlin
class InventoryFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InventoryAdapter
    private val manager by lazy { LinkpointApp.getInstance().inventoryManager }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.inventory_recyclerview)
        adapter = InventoryAdapter { item -> onItemClick(item) }
        recyclerView.adapter = adapter
    }
    
    private fun loadFolder(folderID: UUID) {
        lifecycleScope.launch {
            val items = manager.getFolderContents(folderID)
            adapter.submitList(items)
        }
    }
}
```

**InventoryAdapter.kt**
```kotlin
class InventoryAdapter(
    private val onItemClick: (InventoryItem) -> Unit
) : ListAdapter<InventoryItem, InventoryViewHolder>(DiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory, parent, false)
        return InventoryViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }
}
```

---

## Part 3: Object Management System

### Current Status
**Implemented:** Basic object tracking, update handlers
**Missing:** Object selection, editing, properties UI

### Architecture

```
ObjectSystem
├── ObjectManager (Main controller)
├── SLObject (Base object representation)
├── ObjectPrim (Primitive object)
├── ObjectMesh (Mesh object)
├── ObjectTexture (Texture management)
└── ObjectUI (User interface)
```

### Implementation Plan

#### 1. Enhanced Object Manager
```kotlin
class ObjectManager {
    private val objects: MutableMap<UUID, SLObject>
    private val selectedObjects: MutableSet<UUID>
    private val objectPropertiesCache: MutableMap<UUID, ObjectProperties>
    
    // Track object updates
    fun handleObjectUpdate(update: ObjectUpdate) {
        val obj = objects[update.objectID]
        obj?.apply {
            position = update.position
            rotation = update.rotation
            scale = update.scale
        }
    }
    
    // Select object
    fun selectObject(objectID: UUID) {
        selectedObjects.add(objectID)
        // Request object details if needed
        sendObjectPropertiesRequest(objectID)
    }
    
    // Deselect object
    fun deselectObject(objectID: UUID) {
        selectedObjects.remove(objectID)
    }
    
    // Get selected objects
    fun getSelectedObjects(): List<SLObject> {
        return selectedObjects.mapNotNull { objects[it] }
    }
}
```

#### 2. Object Selection System
```kotlin
class ObjectSelectionManager {
    private val selectedObjects: MutableSet<UUID> = mutableSetOf()
    private val objects: MutableMap<UUID, SLObject> = mutableMapOf()
    
    // Handle raycast hit
    fun handleRaycastHit(screenX: Float, screenY: Float): UUID? {
        // Convert screen coordinates to world ray
        val ray = renderer.screenToWorldRay(screenX, screenY)
        
        // Check intersection with objects
        for ((id, obj) in objects) {
            if (obj.intersects(ray)) {
                return id
            }
        }
        return null
    }
    
    // Select object
    fun selectObject(objectID: UUID) {
        selectedObjects.add(objectID)
        // Send selection packet to simulator
        sendObjectSelectPacket(objectID)
    }
}
```

---

## Part 4: Avatar System

### Current Status
**Implemented:** Basic avatar tracking, appearance handlers, animation handlers
**Missing:** Appearance editor, outfit management, customization UI

### Architecture

```
AvatarSystem
├── AvatarManager (Main controller)
├── AvatarAppearance (Appearance data)
├── AvatarBaker (Texture baking)
├── AvatarAnimation (Animation playback)
├── AvatarMovement (Movement logic)
└── AvatarUI (User interface)
```

### Implementation Plan

#### 1. Enhanced Avatar Manager
```kotlin
class AvatarManager {
    private val avatars: MutableMap<UUID, SLAvatar>
    private val localAvatar: SLAvatar?
    
    // Handle avatar appearance update
    fun handleAvatarAppearance(appearance: AvatarAppearance) {
        val avatar = avatars[appearance.agentID]
        avatar?.apply {
            this.appearance = appearance
            // Request textures for appearance
            requestAppearanceTextures(appearance)
        }
    }
    
    // Bake avatar textures (Bakes on Mesh)
    suspend fun bakeAvatarTextures(agentID: UUID): Map<BakeChannel, UUID> {
        val avatar = avatars[agentID] ?: return emptyMap()
        
        val bakedTextures = mutableMapOf<BakeChannel, UUID>()
        
        // Bake each channel
        BakeChannel.values().forEach { channel ->
            val textureID = baker.bakeChannel(avatar, channel)
            bakedTextures[channel] = textureID
        }
        
        return bakedTextures
    }
}
```

#### 2. Appearance Editor UI
```kotlin
class AppearanceEditorFragment : Fragment() {
    private lateinit var currentWearable: Wearable
    private val avatarManager by lazy { LinkpointApp.getInstance().avatarManager }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Load current appearance
        loadWearable(WearableType.HEAD)
    }
    
    private fun loadWearable(type: WearableType) {
        lifecycleScope.launch {
            val wearable = avatarManager.getWearable(type)
            currentWearable = wearable
            
            // Update UI with wearable options
            updateWearableUI(wearable)
        }
    }
    
    private fun updateWearableUI(wearable: Wearable) {
        // Show texture options, colors, etc.
    }
}
```

---

## Part 5: Chat System

### Current Status
**Implemented:** Local chat, IM handling, message handlers, chat history
**Missing:** Advanced UI features, typing indicators, chat history persistence

### Architecture

```
ChatSystem
├── ChatManager (Main controller)
├── ChatMessage (Message representation)
├── InstantMessage (IM handler)
├── IMSession (IM session)
├── ChatHistory (Message history)
└── ChatUI (User interface)
```

### Implementation Plan

#### 1. Enhanced Chat Manager
```kotlin
class ChatManager {
    private val chatHistory: MutableList<ChatMessage>
    private val imSessions: MutableMap<UUID, IMSession>
    
    // Send chat message
    fun sendChat(message: String, channel: Int = 0) {
        val chatMsg = ChatMessage(
            message = message,
            channel = channel,
            type = ChatType.NORMAL
        )
        
        // Send to simulator
        sendChatPacket(chatMsg)
        
        // Add to local history
        addToHistory(chatMsg)
    }
    
    // Send IM
    fun sendIM(recipientID: UUID, message: String) {
        val im = InstantMessage(
            toAgentID = recipientID,
            message = message,
            offline = false
        )
        
        // Send to simulator
        sendInstantMessagePacket(im)
        
        // Add to history
        addToIMHistory(recipientID, im)
    }
    
    // Get chat history
    fun getChatHistory(maxMessages: Int = 100): List<ChatMessage> {
        return chatHistory.takeLast(maxMessages)
    }
}
```

#### 2. Chat UI Components
```kotlin
class ChatFragment : Fragment() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private val chatManager by lazy { LinkpointApp.getInstance().chatManager }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        chatRecyclerView = view.findViewById(R.id.chat_recyclerview)
        messageEditText = view.findViewById(R.id.message_edittext)
        sendButton = view.findViewById(R.id.send_button)
        
        val adapter = ChatAdapter()
        chatRecyclerView.adapter = adapter
        
        sendButton.setOnClickListener {
            val message = messageEditText.text.toString()
            if (message.isNotEmpty()) {
                chatManager.sendChat(message)
                messageEditText.text.clear()
            }
        }
    }
    
    private fun updateChatHistory() {
        lifecycleScope.launch {
            val messages = chatManager.getChatHistory()
            adapter.submitList(messages)
        }
    }
}
```

---

## Part 6: Implementation Priority

### Phase 1: Core Functionality (Week 1-2)
1. ✅ Complete inventory UI with tree navigation
2. ✅ Complete object selection system
3. ✅ Complete chat UI enhancements
4. ✅ Add chat history persistence

### Phase 2: Advanced Features (Week 3-4)
1. 🔄 Avatar appearance editor
2. 🔄 Outfit management system
3. 🔄 Object properties UI
4. 🔄 Advanced inventory operations (drag-and-drop)

### Phase 3: Voice Chat (Week 5-6)
1. 🚧 WebRTC integration
2. 🚧 Voice session management
3. 🚧 Voice UI controls
4. 🚧 Spatial audio positioning

### Phase 4: Polish & Optimization (Week 7-8)
1. 🚧 Performance optimization
2. 🚧 Memory management
3. 🚧 UI responsiveness
4. 🚧 Error handling

---

## Part 7: Technical Specifications

### Dependencies

```gradle
dependencies {
    // WebRTC for voice chat
    implementation 'org.webrtc:google-webrtc:1.0.32006'
    
    // For better UI components
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'androidx.cardview:cardview:1.0.0'
    implementation 'com.google.android.material:material:1.11.0'
    
    // For image loading
    implementation 'com.github.bumptech.glide:glide:4.16.0'
    
    // For coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
}
```

### File Structure

```
Linkpoint/src/main/java/com/linkpoint/
├── inventory/
│   ├── InventoryManager.kt
│   ├── InventoryFolder.kt
│   ├── InventoryItem.kt
│   ├── InventoryFetcher.kt
│   ├── InventoryDownloadManager.kt
│   └── ui/
│       ├── InventoryFragment.kt
│       ├── InventoryAdapter.kt
│       └── InventoryItemViewHolder.kt
├── objects/
│   ├── ObjectManager.kt
│   ├── SLObject.kt
│   ├── ObjectPrim.kt
│   ├── ObjectMesh.kt
│   ├── ObjectTexture.kt
│   ├── ObjectSelectionManager.kt
│   └── ui/
│       ├── ObjectPropertiesDialog.kt
│       └── ObjectEditFragment.kt
├── avatar/
│   ├── AvatarManager.kt
│   ├── AvatarAppearance.kt
│   ├── AvatarBaker.kt
│   ├── AvatarAnimation.kt
│   ├── AvatarMovement.kt
│   └── ui/
│       ├── AppearanceEditorFragment.kt
│       ├── OutfitManagerFragment.kt
│       └── AvatarPreviewView.kt
├── chat/
│   ├── ChatManager.kt
│   ├── ChatMessage.kt
│   ├── InstantMessage.kt
│   ├── IMSession.kt
│   ├── ChatHistory.kt
│   └── ui/
│       ├── ChatFragment.kt
│       ├── ChatAdapter.kt
│       └── IMFragment.kt
└── voice/
    ├── VoiceManager.kt
    ├── WebRTCSessionManager.kt
    ├── VoiceSession.kt
    ├── VoiceCodecAdapter.kt
    └── ui/
        ├── VoiceControlView.kt
        └── VoiceSettingsFragment.kt
```

---

## Part 8: Testing Strategy

### Unit Tests
- InventoryManager tests
- ObjectManager tests
- AvatarManager tests
- ChatManager tests
- VoiceManager tests

### Integration Tests
- Inventory fetch and display
- Object selection and editing
- Avatar appearance changes
- Chat message sending/receiving
- Voice connection and audio

### UI Tests
- Inventory navigation
- Object interaction
- Avatar customization
- Chat interface
- Voice controls

---

## Part 9: Migration Notes

### From Lumiya to Linkpoint

**Voice:**
- Lumiya uses Vivox (SIP)
- Linkpoint will use WebRTC (modern)
- Both protocols supported during transition

**Architecture:**
- Lumiya uses Java with threads
- Linkpoint uses Kotlin with coroutines
- Modern, more maintainable codebase

**Rendering:**
- Lumiya uses OpenGL ES
- Linkpoint uses Filament (PBR)
- Better visual quality

---

## Conclusion

This implementation plan provides a comprehensive roadmap for completing Linkpoint's core features using modern technologies and best practices. The migration to WebRTC for voice chat aligns with Second Life's direction and provides better audio quality and mobile support.

**Timeline:** 8 weeks for complete implementation
**Resources:** 1-2 developers
**Priority:** Complete inventory, objects, chat, avatar before voice chat

---

*Document created: 2026-01-15*
*Based on: Lumiya 3.4.2 decompilation, Second Life WebRTC documentation, Firestorm implementation*
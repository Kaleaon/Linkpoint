# LibreMetaverse Integration Guide for Linkpoint

## Overview

LibreMetaverse (formerly OpenMetaverse) is a comprehensive .NET library for developing applications that interact with Second Life and OpenSimulator virtual worlds. This guide details how Linkpoint implements Java equivalents of LibreMetaverse's patterns, architectures, and best practices to ensure robust virtual world interaction.

## LibreMetaverse Architecture Translation

### Core Components Mapping

| LibreMetaverse (C#) | Linkpoint (Java) | Purpose |
|---------------------|------------------|---------|
| `GridClient` | `LinkpointGridClient` | Main client interface |
| `NetworkManager` | `ModernNetworkManager` | Network communication |
| `AgentManager` | `LinkpointAgentManager` | Avatar/agent operations |
| `ObjectManager` | `ModernObjectManager` | Object management |
| `AssetManager` | `UniversalAssetManager` | Asset handling |
| `InventoryManager` | `EnhancedInventoryManager` | Inventory operations |
| `FriendsManager` | `SocialConnectionManager` | Social features |
| `GroupManager` | `GroupInteractionManager` | Group functionality |
| `ParcelManager` | `LandManagementSystem` | Land/parcel operations |

### Java Implementation of LibreMetaverse Patterns

#### Main Grid Client (GridClient equivalent)
```java
public class LinkpointGridClient {
    
    // Core managers (equivalent to LibreMetaverse structure)
    public final ModernNetworkManager Network;
    public final LinkpointAgentManager Self;
    public final ModernObjectManager Objects;
    public final UniversalAssetManager Assets;
    public final EnhancedInventoryManager Inventory;
    public final SocialConnectionManager Friends;
    public final GroupInteractionManager Groups;
    public final LandManagementSystem Parcels;
    public final TerrainManager Terrain;
    public final SoundManager Sound;
    public final AppearanceManager Appearance;
    
    // Event system (LibreMetaverse-style events)
    private final EventBus eventBus = new EventBus();
    
    // Settings (similar to LibreMetaverse Settings class)
    public final ClientSettings Settings;
    
    public LinkpointGridClient() {
        this.Settings = new ClientSettings();
        
        // Initialize managers with cross-references (LibreMetaverse pattern)
        this.Network = new ModernNetworkManager(this);
        this.Self = new LinkpointAgentManager(this);
        this.Objects = new ModernObjectManager(this);
        this.Assets = new UniversalAssetManager(this);
        this.Inventory = new EnhancedInventoryManager(this);
        this.Friends = new SocialConnectionManager(this);
        this.Groups = new GroupInteractionManager(this);
        this.Parcels = new LandManagementSystem(this);
        this.Terrain = new TerrainManager(this);
        this.Sound = new SoundManager(this);
        this.Appearance = new AppearanceManager(this);
    }
    
    // Login method (equivalent to LibreMetaverse Login)
    public CompletableFuture<LoginStatus> login(String loginURI, String firstName, String lastName, 
                                               String password, String userAgent, String author) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                LoginParams loginParams = new LoginParams();
                loginParams.URI = loginURI;
                loginParams.FirstName = firstName;
                loginParams.LastName = lastName;
                loginParams.Password = password;
                loginParams.UserAgent = userAgent;
                loginParams.Author = author;
                loginParams.Platform = "Android";
                loginParams.Options = Arrays.asList("inventory-root", "inventory-skeleton", 
                                                   "inventory-lib-root", "inventory-lib-owner");
                
                LoginResponse response = Network.login(loginParams);
                
                if (response.Success) {
                    // Initialize session data (LibreMetaverse pattern)
                    Self.agentID = response.AgentID;
                    Self.sessionID = response.SessionID;
                    Self.secureSessionID = response.SecureSessionID;
                    Self.firstName = firstName;
                    Self.lastName = lastName;
                    
                    // Initialize inventory (LibreMetaverse pattern)
                    Inventory.initializeFromLoginResponse(response);
                    
                    // Initialize other managers
                    initializeManagers(response);
                    
                    return LoginStatus.SUCCESS;
                } else {
                    return LoginStatus.fromMessage(response.Message);
                }
                
            } catch (Exception e) {
                Log.e("GridClient", "Login failed", e);
                return LoginStatus.CONNECT_FAILED;
            }
        });
    }
    
    // Event registration (LibreMetaverse-style)
    public <T> void registerEventHandler(Class<T> eventType, Consumer<T> handler) {
        eventBus.register(new Object() {
            @Subscribe
            public void handle(T event) {
                handler.accept(event);
            }
        });
    }
    
    // Logout method
    public void logout() {
        try {
            // Send logout message
            Network.sendLogout();
            
            // Clean up managers
            shutdownManagers();
            
        } catch (Exception e) {
            Log.e("GridClient", "Logout error", e);
        }
    }
    
    private void initializeManagers(LoginResponse response) {
        // Initialize each manager with login response data
        Objects.initialize(response);
        Assets.initialize(response);
        Parcels.initialize(response);
        // etc.
    }
    
    private void shutdownManagers() {
        // Shutdown all managers gracefully
        Network.shutdown();
        Assets.shutdown();
        Objects.shutdown();
        // etc.
    }
}
```

#### Network Manager (LibreMetaverse NetworkManager equivalent)
```java
public class ModernNetworkManager {
    private final LinkpointGridClient client;
    private final UDPClient udpClient;
    private final HTTPCapsClient capsClient;
    private final MessageBuilder messageBuilder;
    private final PacketDecoder packetDecoder;
    
    // Connection state (LibreMetaverse pattern)
    private volatile boolean connected = false;
    private volatile UUID currentRegion = UUID.ZERO;
    private volatile InetSocketAddress currentSimulator;
    
    // Circuit management (LibreMetaverse pattern)
    private final Map<UUID, SimulatorCircuit> circuits = new ConcurrentHashMap<>();
    
    // Event callbacks (LibreMetaverse-style)
    public interface SimConnectedCallback {
        void onSimConnected(SimConnectedEventArgs e);
    }
    
    public interface SimDisconnectedCallback {
        void onSimDisconnected(SimDisconnectedEventArgs e);
    }
    
    public interface PacketReceivedCallback {
        void onPacketReceived(PacketReceivedEventArgs e);
    }
    
    // Event handler lists
    private final List<SimConnectedCallback> simConnectedHandlers = new CopyOnWriteArrayList<>();
    private final List<SimDisconnectedCallback> simDisconnectedHandlers = new CopyOnWriteArrayList<>();
    private final List<PacketReceivedCallback> packetReceivedHandlers = new CopyOnWriteArrayList<>();
    
    public ModernNetworkManager(LinkpointGridClient client) {
        this.client = client;
        this.udpClient = new UDPClient();
        this.capsClient = new HTTPCapsClient();
        this.messageBuilder = new MessageBuilder();
        this.packetDecoder = new PacketDecoder();
        
        setupNetworkHandlers();
    }
    
    // Login implementation (LibreMetaverse pattern)
    public LoginResponse login(LoginParams loginParams) throws LoginException {
        try {
            // Build login request (LibreMetaverse XML-RPC style)
            LoginRequest request = messageBuilder.buildLoginRequest(loginParams);
            
            // Send login request
            HTTPResponse httpResponse = capsClient.sendRequest(loginParams.URI, request);
            
            // Parse login response
            LoginResponse response = parseLoginResponse(httpResponse);
            
            if (response.Success) {
                // Establish UDP connection to simulator
                connectToSimulator(response.SimIP, response.SimPort, response.CircuitCode);
                
                // Set up CAPS
                initializeCaps(response.SeedCapability);
                
                // Mark as connected
                connected = true;
                currentRegion = response.RegionID;
                
                // Fire connected event
                fireSimConnected(new SimConnectedEventArgs(response.RegionID));
            }
            
            return response;
            
        } catch (Exception e) {
            throw new LoginException("Login failed", e);
        }
    }
    
    // Packet sending (LibreMetaverse pattern)
    public void sendPacket(Packet packet, UUID simulator) {
        SimulatorCircuit circuit = circuits.get(simulator);
        if (circuit != null) {
            byte[] packetData = encodePacket(packet);
            circuit.sendPacket(packetData);
        } else {
            Log.w("Network", "No circuit for simulator: " + simulator);
        }
    }
    
    // Message sending (high-level LibreMetaverse pattern)
    public void sendMessage(SLMessage message) {
        Packet packet = messageBuilder.buildPacket(message);
        sendPacket(packet, currentRegion);
    }
    
    // Capabilities request (LibreMetaverse pattern)
    public CompletableFuture<CapsResponse> requestCaps(String capabilityName, Object requestData) {
        return capsClient.requestCapability(capabilityName, requestData);
    }
    
    // Event handler registration (LibreMetaverse pattern)
    public void addSimConnectedHandler(SimConnectedCallback handler) {
        simConnectedHandlers.add(handler);
    }
    
    public void removeSimConnectedHandler(SimConnectedCallback handler) {
        simConnectedHandlers.remove(handler);
    }
    
    // Packet processing (LibreMetaverse pattern)
    private void processIncomingPacket(byte[] packetData, InetSocketAddress sender) {
        try {
            Packet packet = packetDecoder.decode(packetData);
            
            // Handle reliable packets
            if (packet.Header.Reliable) {
                sendAcknowledgement(packet.Header.Sequence, sender);
            }
            
            // Fire packet received event
            firePacketReceived(new PacketReceivedEventArgs(packet, sender));
            
            // Process messages in packet
            for (SLMessage message : packet.Messages) {
                routeMessage(message);
            }
            
        } catch (Exception e) {
            Log.e("Network", "Packet processing failed", e);
        }
    }
    
    private void routeMessage(SLMessage message) {
        // Route messages to appropriate managers (LibreMetaverse pattern)
        switch (message.Type) {
            case OBJECT_UPDATE:
                client.Objects.processObjectUpdate((ObjectUpdateMessage) message);
                break;
            case CHAT_FROM_SIMULATOR:
                client.Self.processChatMessage((ChatMessage) message);
                break;
            case INVENTORY_DESCENDENT:
                client.Inventory.processInventoryUpdate((InventoryMessage) message);
                break;
            // Add more message routing...
        }
    }
    
    // Event firing methods (LibreMetaverse pattern)
    private void fireSimConnected(SimConnectedEventArgs args) {
        for (SimConnectedCallback handler : simConnectedHandlers) {
            try {
                handler.onSimConnected(args);
            } catch (Exception e) {
                Log.e("Network", "Event handler error", e);
            }
        }
    }
    
    private void firePacketReceived(PacketReceivedEventArgs args) {
        for (PacketReceivedCallback handler : packetReceivedHandlers) {
            try {
                handler.onPacketReceived(args);
            } catch (Exception e) {
                Log.e("Network", "Event handler error", e);
            }
        }
    }
}
```

### Agent Manager (LibreMetaverse AgentManager equivalent)
```java
public class LinkpointAgentManager {
    private final LinkpointGridClient client;
    
    // Agent properties (LibreMetaverse pattern)
    public UUID agentID = UUID.ZERO;
    public UUID sessionID = UUID.ZERO;
    public UUID secureSessionID = UUID.ZERO;
    public String firstName = "";
    public String lastName = "";
    public Vector3f position = Vector3f.ZERO;
    public Quaternionf rotation = Quaternionf.IDENTITY;
    public Vector3f velocity = Vector3f.ZERO;
    public Vector3f angularVelocity = Vector3f.ZERO;
    
    // Movement state
    public boolean isFlying = false;
    public boolean isSitting = false;
    public boolean isTyping = false;
    public boolean isAway = false;
    public boolean isBusy = false;
    
    // Animation state
    public UUID currentAnimation = UUID.ZERO;
    public final Set<UUID> activeAnimations = ConcurrentHashMap.newKeySet();
    
    // Chat/communication
    public int chatChannel = 0;
    public ChatType lastChatType = ChatType.NORMAL;
    
    // Events (LibreMetaverse pattern)
    public interface ChatCallback {
        void onChat(ChatEventArgs e);
    }
    
    public interface InstantMessageCallback {
        void onInstantMessage(InstantMessageEventArgs e);
    }
    
    public interface TeleportCallback {
        void onTeleport(TeleportEventArgs e);
    }
    
    private final List<ChatCallback> chatHandlers = new CopyOnWriteArrayList<>();
    private final List<InstantMessageCallback> imHandlers = new CopyOnWriteArrayList<>();
    private final List<TeleportCallback> teleportHandlers = new CopyOnWriteArrayList<>();
    
    public LinkpointAgentManager(LinkpointGridClient client) {
        this.client = client;
    }
    
    // Movement methods (LibreMetaverse pattern)
    public void requestMovement(Vector3f direction, boolean fly) {
        AgentUpdateMessage update = new AgentUpdateMessage();
        update.agentID = agentID;
        update.sessionID = sessionID;
        update.bodyRotation = rotation;
        update.headRotation = rotation;
        update.direction = direction;
        update.flags = calculateMovementFlags(direction, fly);
        
        client.Network.sendMessage(update);
    }
    
    public void fly(boolean enable) {
        if (isFlying != enable) {
            isFlying = enable;
            
            // Send agent update with fly flag
            AgentUpdateMessage update = createAgentUpdate();
            if (enable) {
                update.flags |= AgentFlags.AGENT_FLY;
            }
            
            client.Network.sendMessage(update);
        }
    }
    
    public void sit(UUID targetObject, Vector3f offset) {
        AgentRequestSitMessage sitRequest = new AgentRequestSitMessage();
        sitRequest.agentID = agentID;
        sitRequest.sessionID = sessionID;
        sitRequest.targetID = targetObject;
        sitRequest.offset = offset;
        
        client.Network.sendMessage(sitRequest);
    }
    
    public void stand() {
        AgentSetAlwaysRunMessage standMessage = new AgentSetAlwaysRunMessage();
        standMessage.agentID = agentID;
        standMessage.sessionID = sessionID;
        standMessage.alwaysRun = false;
        
        client.Network.sendMessage(standMessage);
    }
    
    // Chat methods (LibreMetaverse pattern)
    public void chat(String message) {
        chat(message, chatChannel, ChatType.NORMAL);
    }
    
    public void chat(String message, int channel, ChatType type) {
        ChatFromViewerMessage chatMessage = new ChatFromViewerMessage();
        chatMessage.agentID = agentID;
        chatMessage.sessionID = sessionID;
        chatMessage.message = message;
        chatMessage.channel = channel;
        chatMessage.type = type;
        
        client.Network.sendMessage(chatMessage);
    }
    
    public void whisper(String message) {
        chat(message, 0, ChatType.WHISPER);
    }
    
    public void shout(String message) {
        chat(message, 0, ChatType.SHOUT);
    }
    
    // Instant message methods (LibreMetaverse pattern)
    public void sendInstantMessage(UUID targetAgent, String message) {
        sendInstantMessage(targetAgent, message, InstantMessageDialog.MESSAGE_FROM_AGENT);
    }
    
    public void sendInstantMessage(UUID targetAgent, String message, InstantMessageDialog dialog) {
        ImprovedInstantMessageMessage im = new ImprovedInstantMessageMessage();
        im.agentID = agentID;
        im.sessionID = sessionID;
        im.fromAgentName = firstName + " " + lastName;
        im.toAgentID = targetAgent;
        im.message = message;
        im.dialog = dialog;
        im.offline = InstantMessageOnline.ONLINE;
        im.imSessionID = UUID.randomUUID();
        im.timestamp = Instant.now();
        
        client.Network.sendMessage(im);
    }
    
    // Teleport methods (LibreMetaverse pattern)
    public CompletableFuture<TeleportStatus> teleport(String regionName, Vector3f position) {
        return CompletableFuture.supplyAsync(() -> {
            TeleportLocationRequestMessage teleportRequest = new TeleportLocationRequestMessage();
            teleportRequest.agentID = agentID;
            teleportRequest.sessionID = sessionID;
            teleportRequest.regionName = regionName;
            teleportRequest.position = position;
            teleportRequest.lookAt = Vector3f.UNIT_X; // Default look direction
            
            client.Network.sendMessage(teleportRequest);
            
            // Wait for teleport response (simplified)
            try {
                Thread.sleep(5000); // Wait up to 5 seconds
                return TeleportStatus.FINISHED; // Would be set by response handler
            } catch (InterruptedException e) {
                return TeleportStatus.FAILED;
            }
        });
    }
    
    public CompletableFuture<TeleportStatus> teleport(UUID regionID, Vector3f position) {
        return teleportToRegion(regionID, position, Vector3f.UNIT_X);
    }
    
    private CompletableFuture<TeleportStatus> teleportToRegion(UUID regionID, Vector3f position, Vector3f lookAt) {
        // Implementation would send TeleportRequest and wait for response
        return CompletableFuture.completedFuture(TeleportStatus.FINISHED);
    }
    
    // Animation methods (LibreMetaverse pattern)
    public void playAnimation(UUID animationID) {
        if (!activeAnimations.contains(animationID)) {
            AgentAnimationMessage animMessage = new AgentAnimationMessage();
            animMessage.agentID = agentID;
            animMessage.sessionID = sessionID;
            animMessage.addAnimation(animationID, true);
            
            client.Network.sendMessage(animMessage);
            activeAnimations.add(animationID);
        }
    }
    
    public void stopAnimation(UUID animationID) {
        if (activeAnimations.contains(animationID)) {
            AgentAnimationMessage animMessage = new AgentAnimationMessage();
            animMessage.agentID = agentID;
            animMessage.sessionID = sessionID;
            animMessage.addAnimation(animationID, false);
            
            client.Network.sendMessage(animMessage);
            activeAnimations.remove(animationID);
        }
    }
    
    // Message processing (called by NetworkManager)
    public void processChatMessage(ChatMessage message) {
        // Fire chat event
        ChatEventArgs args = new ChatEventArgs(
            message.sourceID,
            message.sourceName,
            message.message,
            message.chatType,
            message.channel
        );
        
        fireChatEvent(args);
    }
    
    public void processInstantMessage(InstantMessageMessage message) {
        // Fire IM event
        InstantMessageEventArgs args = new InstantMessageEventArgs(
            message.fromAgentID,
            message.fromAgentName,
            message.toAgentID,
            message.message,
            message.dialog
        );
        
        fireInstantMessageEvent(args);
    }
    
    // Event handler management (LibreMetaverse pattern)
    public void addChatHandler(ChatCallback handler) {
        chatHandlers.add(handler);
    }
    
    public void removeChatHandler(ChatCallback handler) {
        chatHandlers.remove(handler);
    }
    
    public void addInstantMessageHandler(InstantMessageCallback handler) {
        imHandlers.add(handler);
    }
    
    public void removeInstantMessageHandler(InstantMessageCallback handler) {
        imHandlers.remove(handler);
    }
    
    // Event firing
    private void fireChatEvent(ChatEventArgs args) {
        for (ChatCallback handler : chatHandlers) {
            try {
                handler.onChat(args);
            } catch (Exception e) {
                Log.e("AgentManager", "Chat event handler error", e);
            }
        }
    }
    
    private void fireInstantMessageEvent(InstantMessageEventArgs args) {
        for (InstantMessageCallback handler : imHandlers) {
            try {
                handler.onInstantMessage(args);
            } catch (Exception e) {
                Log.e("AgentManager", "IM event handler error", e);
            }
        }
    }
    
    // Utility methods
    private AgentUpdateMessage createAgentUpdate() {
        AgentUpdateMessage update = new AgentUpdateMessage();
        update.agentID = agentID;
        update.sessionID = sessionID;
        update.bodyRotation = rotation;
        update.headRotation = rotation;
        update.cameraCenter = position;
        update.cameraAtAxis = Vector3f.UNIT_X;
        update.cameraLeftAxis = Vector3f.UNIT_Y;
        update.cameraUpAxis = Vector3f.UNIT_Z;
        update.flags = calculateCurrentFlags();
        
        return update;
    }
    
    private int calculateCurrentFlags() {
        int flags = 0;
        
        if (isFlying) flags |= AgentFlags.AGENT_FLY;
        if (isSitting) flags |= AgentFlags.AGENT_SITTING;
        if (isTyping) flags |= AgentFlags.AGENT_TYPING;
        if (isAway) flags |= AgentFlags.AGENT_AWAY;
        if (isBusy) flags |= AgentFlags.AGENT_BUSY;
        
        return flags;
    }
    
    private int calculateMovementFlags(Vector3f direction, boolean fly) {
        int flags = calculateCurrentFlags();
        
        // Add movement flags based on direction
        if (direction.z > 0) flags |= AgentFlags.AGENT_CONTROL_UP_POS;
        if (direction.z < 0) flags |= AgentFlags.AGENT_CONTROL_UP_NEG;
        if (direction.x > 0) flags |= AgentFlags.AGENT_CONTROL_AT_POS;
        if (direction.x < 0) flags |= AgentFlags.AGENT_CONTROL_AT_NEG;
        if (direction.y > 0) flags |= AgentFlags.AGENT_CONTROL_LEFT_POS;
        if (direction.y < 0) flags |= AgentFlags.AGENT_CONTROL_LEFT_NEG;
        
        if (fly) flags |= AgentFlags.AGENT_FLY;
        
        return flags;
    }
}
```

### Object Manager (LibreMetaverse ObjectManager equivalent)
```java
public class ModernObjectManager {
    private final LinkpointGridClient client;
    private final Map<Long, SLObject> objects = new ConcurrentHashMap<>();
    private final Map<UUID, SLAvatar> avatars = new ConcurrentHashMap<>();
    
    // Events (LibreMetaverse pattern)
    public interface ObjectUpdateCallback {
        void onObjectUpdate(ObjectUpdateEventArgs e);
    }
    
    public interface NewPrimCallback {
        void onNewPrim(NewPrimEventArgs e);
    }
    
    public interface ObjectKilledCallback {
        void onObjectKilled(ObjectKilledEventArgs e);
    }
    
    public interface AvatarUpdateCallback {
        void onAvatarUpdate(AvatarUpdateEventArgs e);
    }
    
    private final List<ObjectUpdateCallback> objectUpdateHandlers = new CopyOnWriteArrayList<>();
    private final List<NewPrimCallback> newPrimHandlers = new CopyOnWriteArrayList<>();
    private final List<ObjectKilledCallback> objectKilledHandlers = new CopyOnWriteArrayList<>();
    private final List<AvatarUpdateCallback> avatarUpdateHandlers = new CopyOnWriteArrayList<>();
    
    public ModernObjectManager(LinkpointGridClient client) {
        this.client = client;
    }
    
    // Object access methods (LibreMetaverse pattern)
    public SLObject getObject(long localID) {
        return objects.get(localID);
    }
    
    public SLObject getObject(UUID fullID) {
        return objects.values().stream()
            .filter(obj -> obj.getID().equals(fullID))
            .findFirst()
            .orElse(null);
    }
    
    public Collection<SLObject> getAllObjects() {
        return Collections.unmodifiableCollection(objects.values());
    }
    
    public SLAvatar getAvatar(UUID agentID) {
        return avatars.get(agentID);
    }
    
    public Collection<SLAvatar> getAllAvatars() {
        return Collections.unmodifiableCollection(avatars.values());
    }
    
    // Object interaction methods (LibreMetaverse pattern)
    public void selectObject(SLObject object) {
        selectObjects(Collections.singletonList(object));
    }
    
    public void selectObjects(Collection<SLObject> objectsToSelect) {
        ObjectSelectMessage selectMessage = new ObjectSelectMessage();
        selectMessage.agentID = client.Self.agentID;
        selectMessage.sessionID = client.Self.sessionID;
        
        for (SLObject obj : objectsToSelect) {
            ObjectSelectData selectData = new ObjectSelectData();
            selectData.objectLocalID = obj.getLocalID();
            selectMessage.addObjectData(selectData);
        }
        
        client.Network.sendMessage(selectMessage);
    }
    
    public void deselectObject(SLObject object) {
        deselectObjects(Collections.singletonList(object));
    }
    
    public void deselectObjects(Collection<SLObject> objectsToDeselect) {
        ObjectDeselectMessage deselectMessage = new ObjectDeselectMessage();
        deselectMessage.agentID = client.Self.agentID;
        deselectMessage.sessionID = client.Self.sessionID;
        
        for (SLObject obj : objectsToDeselect) {
            ObjectDeselectData deselectData = new ObjectDeselectData();
            deselectData.objectLocalID = obj.getLocalID();
            deselectMessage.addObjectData(deselectData);
        }
        
        client.Network.sendMessage(deselectMessage);
    }
    
    // Object manipulation methods (LibreMetaverse pattern)
    public void setObjectPosition(SLObject object, Vector3f newPosition) {
        MultipleObjectUpdateMessage updateMessage = new MultipleObjectUpdateMessage();
        updateMessage.agentID = client.Self.agentID;
        updateMessage.sessionID = client.Self.sessionID;
        
        ObjectUpdateData updateData = new ObjectUpdateData();
        updateData.objectLocalID = object.getLocalID();
        updateData.type = ObjectUpdateType.POSITION;
        updateData.data = serializeVector3(newPosition);
        
        updateMessage.addObjectData(updateData);
        client.Network.sendMessage(updateMessage);
    }
    
    public void setObjectRotation(SLObject object, Quaternionf newRotation) {
        MultipleObjectUpdateMessage updateMessage = new MultipleObjectUpdateMessage();
        updateMessage.agentID = client.Self.agentID;
        updateMessage.sessionID = client.Self.sessionID;
        
        ObjectUpdateData updateData = new ObjectUpdateData();
        updateData.objectLocalID = object.getLocalID();
        updateData.type = ObjectUpdateType.ROTATION;
        updateData.data = serializeQuaternion(newRotation);
        
        updateMessage.addObjectData(updateData);
        client.Network.sendMessage(updateMessage);
    }
    
    public void setObjectScale(SLObject object, Vector3f newScale) {
        MultipleObjectUpdateMessage updateMessage = new MultipleObjectUpdateMessage();
        updateMessage.agentID = client.Self.agentID;
        updateMessage.sessionID = client.Self.sessionID;
        
        ObjectUpdateData updateData = new ObjectUpdateData();
        updateData.objectLocalID = object.getLocalID();
        updateData.type = ObjectUpdateType.SCALE;
        updateData.data = serializeVector3(newScale);
        
        updateMessage.addObjectData(updateData);
        client.Network.sendMessage(updateMessage);
    }
    
    // Touch/interaction methods (LibreMetaverse pattern)
    public void touchObject(SLObject object) {
        touchObject(object, Vector3f.ZERO, Vector3f.ZERO, 0);
    }
    
    public void touchObject(SLObject object, Vector3f touchPos, Vector3f surfaceNormal, int touchFace) {
        ObjectGrabMessage grabMessage = new ObjectGrabMessage();
        grabMessage.agentID = client.Self.agentID;
        grabMessage.sessionID = client.Self.sessionID;
        grabMessage.objectLocalID = object.getLocalID();
        grabMessage.grabOffset = touchPos;
        
        client.Network.sendMessage(grabMessage);
        
        // Immediately send grab update (LibreMetaverse pattern)
        ObjectGrabUpdateMessage updateMessage = new ObjectGrabUpdateMessage();
        updateMessage.agentID = client.Self.agentID;
        updateMessage.sessionID = client.Self.sessionID;
        updateMessage.objectLocalID = object.getLocalID();
        updateMessage.grabOffsetInitial = touchPos;
        updateMessage.grabPosition = touchPos;
        updateMessage.timeSinceLast = 0;
        
        client.Network.sendMessage(updateMessage);
        
        // Send de-grab message
        ObjectDeGrabMessage degrabMessage = new ObjectDeGrabMessage();
        degrabMessage.agentID = client.Self.agentID;
        degrabMessage.sessionID = client.Self.sessionID;
        degrabMessage.objectLocalID = object.getLocalID();
        degrabMessage.grabOffset = touchPos;
        
        client.Network.sendMessage(degrabMessage);
    }
    
    // Message processing (called by NetworkManager)
    public void processObjectUpdate(ObjectUpdateMessage message) {
        for (ObjectUpdateData updateData : message.getObjectData()) {
            long localID = updateData.objectLocalID;
            
            SLObject object = objects.computeIfAbsent(localID, id -> new SLObject(id));
            
            // Apply updates to object
            applyObjectUpdate(object, updateData);
            
            // Determine if this is a new object
            boolean isNewObject = !objects.containsKey(localID);
            
            if (isNewObject) {
                // Fire new prim event
                fireNewPrimEvent(new NewPrimEventArgs(object));
            } else {
                // Fire object update event
                fireObjectUpdateEvent(new ObjectUpdateEventArgs(object, updateData.updateType));
            }
        }
    }
    
    public void processKillObject(KillObjectMessage message) {
        for (long localID : message.getKilledObjects()) {
            SLObject removedObject = objects.remove(localID);
            if (removedObject != null) {
                // Fire object killed event
                fireObjectKilledEvent(new ObjectKilledEventArgs(removedObject));
            }
        }
    }
    
    private void applyObjectUpdate(SLObject object, ObjectUpdateData updateData) {
        // Apply various update types (LibreMetaverse pattern)
        switch (updateData.updateType) {
            case FULL_UPDATE:
                applyFullUpdate(object, updateData);
                break;
            case COMPRESSED_UPDATE:
                applyCompressedUpdate(object, updateData);
                break;
            case TERSE_UPDATE:
                applyTerseUpdate(object, updateData);
                break;
            case CACHED_UPDATE:
                applyCachedUpdate(object, updateData);
                break;
        }
    }
    
    private void applyFullUpdate(SLObject object, ObjectUpdateData updateData) {
        // Full object property update
        object.setPosition(updateData.position);
        object.setRotation(updateData.rotation);
        object.setScale(updateData.scale);
        object.setVelocity(updateData.velocity);
        object.setAngularVelocity(updateData.angularVelocity);
        object.setName(updateData.name);
        object.setDescription(updateData.description);
        object.setOwnerID(updateData.ownerID);
        object.setGroupID(updateData.groupID);
        // ... apply all properties
    }
    
    private void applyTerseUpdate(SLObject object, ObjectUpdateData updateData) {
        // Terse update - only position, rotation, velocity
        object.setPosition(updateData.position);
        object.setRotation(updateData.rotation);
        object.setVelocity(updateData.velocity);
        object.setAngularVelocity(updateData.angularVelocity);
    }
    
    // Event handler management and firing (LibreMetaverse pattern)
    public void addObjectUpdateHandler(ObjectUpdateCallback handler) {
        objectUpdateHandlers.add(handler);
    }
    
    public void addNewPrimHandler(NewPrimCallback handler) {
        newPrimHandlers.add(handler);
    }
    
    public void addObjectKilledHandler(ObjectKilledCallback handler) {
        objectKilledHandlers.add(handler);
    }
    
    private void fireObjectUpdateEvent(ObjectUpdateEventArgs args) {
        for (ObjectUpdateCallback handler : objectUpdateHandlers) {
            try {
                handler.onObjectUpdate(args);
            } catch (Exception e) {
                Log.e("ObjectManager", "Object update handler error", e);
            }
        }
    }
    
    private void fireNewPrimEvent(NewPrimEventArgs args) {
        for (NewPrimCallback handler : newPrimHandlers) {
            try {
                handler.onNewPrim(args);
            } catch (Exception e) {
                Log.e("ObjectManager", "New prim handler error", e);
            }
        }
    }
    
    private void fireObjectKilledEvent(ObjectKilledEventArgs args) {
        for (ObjectKilledCallback handler : objectKilledHandlers) {
            try {
                handler.onObjectKilled(args);
            } catch (Exception e) {
                Log.e("ObjectManager", "Object killed handler error", e);
            }
        }
    }
    
    // Utility methods
    private byte[] serializeVector3(Vector3f vector) {
        ByteBuffer buffer = ByteBuffer.allocate(12);
        buffer.putFloat(vector.x);
        buffer.putFloat(vector.y);
        buffer.putFloat(vector.z);
        return buffer.array();
    }
    
    private byte[] serializeQuaternion(Quaternionf quaternion) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putFloat(quaternion.x);
        buffer.putFloat(quaternion.y);
        buffer.putFloat(quaternion.z);
        buffer.putFloat(quaternion.w);
        return buffer.array();
    }
}
```

## Integration Testing with LibreMetaverse Patterns

### Unit Test Structure
```java
public class LibreMetaversePatternTests {
    private LinkpointGridClient client;
    private TestSimulator mockSimulator;
    
    @BeforeEach
    public void setUp() {
        client = new LinkpointGridClient();
        mockSimulator = new TestSimulator();
    }
    
    @Test
    public void testLoginSequence() {
        // Test login similar to LibreMetaverse pattern
        LoginParams loginParams = new LoginParams();
        loginParams.FirstName = "Test";
        loginParams.LastName = "User";
        loginParams.Password = "password";
        loginParams.URI = mockSimulator.getLoginUri();
        
        CompletableFuture<LoginStatus> loginFuture = client.login(
            loginParams.URI, 
            loginParams.FirstName, 
            loginParams.LastName,
            loginParams.Password,
            "Linkpoint Test",
            "Test Suite"
        );
        
        LoginStatus status = loginFuture.join();
        assertEquals(LoginStatus.SUCCESS, status);
        
        // Verify client state
        assertNotEquals(UUID.ZERO, client.Self.agentID);
        assertNotEquals(UUID.ZERO, client.Self.sessionID);
        assertEquals("Test", client.Self.firstName);
        assertEquals("User", client.Self.lastName);
    }
    
    @Test
    public void testEventHandling() {
        // Test LibreMetaverse-style event handling
        List<ChatEventArgs> receivedChats = new ArrayList<>();
        
        client.Self.addChatHandler(chatEvent -> {
            receivedChats.add(chatEvent);
        });
        
        // Simulate incoming chat message
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.sourceID = UUID.randomUUID();
        chatMessage.sourceName = "Other User";
        chatMessage.message = "Hello World";
        chatMessage.chatType = ChatType.NORMAL;
        chatMessage.channel = 0;
        
        client.Self.processChatMessage(chatMessage);
        
        // Verify event was fired
        assertEquals(1, receivedChats.size());
        ChatEventArgs chatEvent = receivedChats.get(0);
        assertEquals("Hello World", chatEvent.getMessage());
        assertEquals("Other User", chatEvent.getSourceName());
    }
    
    @Test
    public void testObjectManagement() {
        // Test LibreMetaverse-style object management
        List<NewPrimEventArgs> newPrims = new ArrayList<>();
        
        client.Objects.addNewPrimHandler(newPrimEvent -> {
            newPrims.add(newPrimEvent);
        });
        
        // Simulate object update message
        ObjectUpdateMessage updateMessage = new ObjectUpdateMessage();
        ObjectUpdateData updateData = new ObjectUpdateData();
        updateData.objectLocalID = 12345;
        updateData.objectID = UUID.randomUUID();
        updateData.position = new Vector3f(100, 100, 25);
        updateData.rotation = Quaternionf.IDENTITY;
        updateData.scale = Vector3f.ONE;
        updateData.updateType = ObjectUpdateType.FULL_UPDATE;
        updateMessage.addObjectData(updateData);
        
        client.Objects.processObjectUpdate(updateMessage);
        
        // Verify object was created and event fired
        assertEquals(1, newPrims.size());
        SLObject object = client.Objects.getObject(12345);
        assertNotNull(object);
        assertEquals(new Vector3f(100, 100, 25), object.getPosition());
    }
    
    @Test
    public void testAssetManagement() {
        // Test LibreMetaverse-style asset handling
        UUID textureId = UUID.randomUUID();
        
        // Request asset
        CompletableFuture<Asset> assetFuture = client.Assets.requestAsset(textureId, AssetType.TEXTURE);
        
        // Verify asset request was made
        Asset asset = assetFuture.join();
        assertNotNull(asset);
        assertEquals(textureId, asset.getAssetID());
        assertEquals(AssetType.TEXTURE, asset.getAssetType());
    }
}
```

This comprehensive LibreMetaverse integration guide ensures that Linkpoint follows the proven patterns and architectures established by the LibreMetaverse library, providing robust and familiar APIs for virtual world interaction while leveraging modern Java features and mobile optimizations.
# OpenSimulator Compatibility Guide for Linkpoint

## Overview

OpenSimulator (OpenSim) is an open source multi-platform, multi-user 3D application server. It can be used to create a virtual environment (or world) which can be accessed through a variety of clients, on multiple protocols. This guide details how Linkpoint provides comprehensive compatibility with OpenSimulator grids.

## OpenSimulator Architecture

### Core Components
```
OpenSimulator Architecture
├── Grid Services
│   ├── User Account Service
│   ├── Grid Service (Region Registration)
│   ├── Asset Service (Texture, Mesh, Sound storage)
│   ├── Inventory Service 
│   ├── Friends Service
│   ├── Group Service
│   └── Presence Service
├── Region Simulator
│   ├── Scene Management
│   ├── Physics Engine (ODE/BulletSim/ubODE)
│   ├── Script Engine (XEngine/YEngine)
│   ├── Avatar Management
│   └── Object Management
└── External Interfaces
    ├── LLSD Web Services
    ├── UDP Circuit Interface
    └── Console Interface
```

## Linkpoint OpenSim Integration Features

### Full Protocol Compatibility

#### Supported OpenSim Message Types
```java
public enum OpenSimMessageType {
    // Enhanced OpenSim messages
    REGION_HANDSHAKE_ENHANCED,      // Variable region sizes
    PHYSICS_SHAPE_ENHANCED,         // Advanced physics materials
    AVATAR_ANIMATION_ENHANCED,      // Improved animation system
    GROUP_CHAT_ENHANCED,            // Extended group features
    REGION_CROSSING_ENHANCED,       // Better region boundaries
    
    // OpenSim-specific messages
    NPC_COMMAND,                    // Non-player character control
    REGION_RESTART_NOTIFICATION,    // Grid maintenance alerts
    GRID_INFO_REQUEST,              // Grid capability queries
    PHYSICS_MATERIAL_UPDATE,        // Physics material properties
    VARIABLE_REGION_SIZE,           // Non-standard region dimensions
    
    // Backwards compatibility
    LEGACY_SL_MESSAGE,              // Standard SL protocol messages
    COMPATIBILITY_BRIDGE            // Translation layer
}
```

#### OpenSim Feature Detection
```java
public class OpenSimFeatureDetector {
    
    public OpenSimCapabilities detectCapabilities(LoginResponse loginResponse) {
        OpenSimCapabilities.Builder capabilities = OpenSimCapabilities.builder();
        
        // Check for OpenSimulator identifier
        if (loginResponse.containsKey("OpenSimVersion")) {
            String version = loginResponse.getString("OpenSimVersion");
            capabilities.openSimVersion(version);
            
            // Parse version for feature support
            Version osVersion = Version.parse(version);
            capabilities.supportsVariableRegions(osVersion.isAtLeast("0.8.0"));
            capabilities.supportsPhysicsMaterials(osVersion.isAtLeast("0.8.2"));
            capabilities.supportsNPCs(osVersion.isAtLeast("0.7.6"));
            capabilities.supportsEnhancedTerrain(osVersion.isAtLeast("0.9.0"));
        }
        
        // Check grid-specific capabilities
        if (loginResponse.containsKey("GridInfo")) {
            GridInfo gridInfo = loginResponse.getGridInfo();
            
            capabilities.maxRegionSize(gridInfo.getInteger("MaxRegionSize", 256));
            capabilities.supportsHypergrid(gridInfo.getBoolean("HypergridEnabled", false));
            capabilities.supportsCurrency(gridInfo.containsKey("CurrencySymbol"));
            capabilities.supportsSearch(gridInfo.getBoolean("SearchEnabled", false));
        }
        
        return capabilities.build();
    }
    
    public void configureForOpenSim(OpenSimCapabilities capabilities) {
        // Configure Linkpoint for detected OpenSim features
        if (capabilities.supportsVariableRegions()) {
            regionManager.enableVariableRegionSupport();
            terrainRenderer.setMaxRegionSize(capabilities.maxRegionSize());
        }
        
        if (capabilities.supportsPhysicsMaterials()) {
            physicsEngine.enablePhysicsMaterials();
        }
        
        if (capabilities.supportsNPCs()) {
            npcManager.enableNPCSupport();
        }
        
        if (capabilities.supportsHypergrid()) {
            hypergridManager.enableHypergridTravel();
        }
    }
}
```

### Variable Region Size Support

#### Dynamic Region Sizing
```java
public class VariableRegionManager {
    private static final Map<String, RegionSize> SUPPORTED_SIZES = Map.of(
        "Homestead", new RegionSize(256, 256),
        "Full", new RegionSize(256, 256),
        "Large", new RegionSize(512, 512),
        "XL", new RegionSize(1024, 1024),
        "Mega", new RegionSize(2048, 2048),
        "Ultra", new RegionSize(4096, 4096)  // Theoretical maximum
    );
    
    public void handleRegionInfo(RegionHandshakeMessage message) {
        int regionSizeX = message.getRegionSizeX();
        int regionSizeY = message.getRegionSizeY();
        
        // Validate region size
        if (regionSizeX <= 0 || regionSizeY <= 0) {
            Log.w("Region", "Invalid region size, defaulting to 256x256");
            regionSizeX = regionSizeY = 256;
        }
        
        // Update rendering systems
        updateRenderingForRegionSize(regionSizeX, regionSizeY);
        
        // Update navigation systems
        updateNavigationForRegionSize(regionSizeX, regionSizeY);
        
        // Update mini-map
        updateMiniMapForRegionSize(regionSizeX, regionSizeY);
        
        Log.i("Region", String.format("Region configured for %dx%d", regionSizeX, regionSizeY));
    }
    
    private void updateRenderingForRegionSize(int sizeX, int sizeY) {
        // Adjust terrain patch count
        int terrainPatchesX = sizeX / 16; // 16m per patch
        int terrainPatchesY = sizeY / 16;
        terrainRenderer.setPatchGrid(terrainPatchesX, terrainPatchesY);
        
        // Adjust frustum culling
        camera.setFarClipPlane(Math.max(sizeX, sizeY) * 2.0f);
        
        // Adjust LOD calculations
        lodManager.setMaxDistance(Math.max(sizeX, sizeY) * 1.5f);
    }
    
    private void updateNavigationForRegionSize(int sizeX, int sizeY) {
        // Set navigation bounds
        navigationMesh.setBounds(0, 0, sizeX, sizeY);
        
        // Update path finding grid
        pathFinder.setGridSize(sizeX / 4, sizeY / 4); // 4m grid resolution
        
        // Update collision detection boundaries
        collisionDetector.setRegionBounds(sizeX, sizeY);
    }
    
    private void updateMiniMapForRegionSize(int sizeX, int sizeY) {
        // Adjust mini-map scale
        float scale = Math.max(sizeX, sizeY) / 256.0f;
        miniMap.setScale(scale);
        
        // Update mini-map texture size
        int textureSize = Math.min(Math.max(sizeX, sizeY) / 4, 1024);
        miniMap.setTextureSize(textureSize, textureSize);
    }
}
```

### Enhanced Physics Support

#### Physics Materials
```java
public class OpenSimPhysicsMaterialManager {
    
    public class PhysicsMaterial {
        private final float friction;
        private final float bounce;
        private final float gravityModifier;
        private final float density;
        
        public PhysicsMaterial(float friction, float bounce, float gravityModifier, float density) {
            this.friction = Math.max(0.0f, Math.min(1.0f, friction));
            this.bounce = Math.max(0.0f, Math.min(1.0f, bounce));
            this.gravityModifier = Math.max(-1.0f, Math.min(28.0f, gravityModifier));
            this.density = Math.max(1.0f, Math.min(22587.0f, density));
        }
        
        // Getters...
    }
    
    public void handlePhysicsMaterialUpdate(PhysicsMaterialMessage message) {
        long localId = message.getLocalId();
        SLObject object = objectManager.getObject(localId);
        
        if (object != null) {
            PhysicsMaterial material = new PhysicsMaterial(
                message.getFriction(),
                message.getBounce(), 
                message.getGravityModifier(),
                message.getDensity()
            );
            
            // Apply to physics engine
            if (physicsEngine instanceof BulletSimPhysicsEngine) {
                ((BulletSimPhysicsEngine) physicsEngine).setObjectMaterial(object, material);
            } else if (physicsEngine instanceof ODEPhysicsEngine) {
                ((ODEPhysicsEngine) physicsEngine).setObjectMaterial(object, material);
            }
            
            // Store for persistence
            object.setPhysicsMaterial(material);
        }
    }
    
    // Pre-defined OpenSim physics materials
    public static final PhysicsMaterial STONE = new PhysicsMaterial(0.8f, 0.4f, 1.0f, 2000.0f);
    public static final PhysicsMaterial METAL = new PhysicsMaterial(0.3f, 0.4f, 1.0f, 7000.0f);
    public static final PhysicsMaterial GLASS = new PhysicsMaterial(0.2f, 0.7f, 1.0f, 2500.0f);
    public static final PhysicsMaterial WOOD = new PhysicsMaterial(0.6f, 0.5f, 1.0f, 700.0f);
    public static final PhysicsMaterial FLESH = new PhysicsMaterial(0.9f, 0.3f, 1.0f, 1000.0f);
    public static final PhysicsMaterial PLASTIC = new PhysicsMaterial(0.4f, 0.7f, 1.0f, 1000.0f);
    public static final PhysicsMaterial RUBBER = new PhysicsMaterial(0.9f, 0.9f, 1.0f, 1000.0f);
}
```

### NPC (Non-Player Character) Support

#### NPC Management
```java
public class OpenSimNPCManager {
    private final Map<UUID, NPCInfo> activeNPCs = new ConcurrentHashMap<>();
    private final NPCAppearanceManager appearanceManager;
    private final NPCMovementManager movementManager;
    
    public class NPCInfo {
        private final UUID npcId;
        private final String firstName;
        private final String lastName;
        private final UUID ownerId;
        private final Vector3f position;
        private final Quaternionf rotation;
        private final UUID appearanceUUID;
        private boolean isSensing;
        
        // Constructor and getters...
    }
    
    public void handleNPCCreate(NPCCreateMessage message) {
        UUID npcId = message.getNPCId();
        UUID ownerId = message.getOwnerId();
        
        NPCInfo npc = new NPCInfo(
            npcId,
            message.getFirstName(),
            message.getLastName(), 
            ownerId,
            message.getPosition(),
            message.getRotation(),
            message.getAppearanceUUID(),
            message.isSensing()
        );
        
        activeNPCs.put(npcId, npc);
        
        // Create visual representation
        SLAvatar npcAvatar = createNPCAvatar(npc);
        avatarManager.addNPC(npcAvatar);
        
        // Set up appearance
        if (npc.getAppearanceUUID() != null) {
            appearanceManager.applyAppearanceToNPC(npcId, npc.getAppearanceUUID());
        }
        
        Log.i("NPC", String.format("Created NPC: %s %s (ID: %s)", 
            npc.getFirstName(), npc.getLastName(), npcId));
    }
    
    public void handleNPCMove(NPCMoveMessage message) {
        UUID npcId = message.getNPCId();
        NPCInfo npc = activeNPCs.get(npcId);
        
        if (npc != null) {
            Vector3f targetPosition = message.getTargetPosition();
            float speed = message.getSpeed();
            boolean fly = message.shouldFly();
            
            // Update NPC movement
            movementManager.moveNPCTo(npcId, targetPosition, speed, fly);
            
            // Animate movement
            SLAvatar npcAvatar = avatarManager.getNPC(npcId);
            if (npcAvatar != null) {
                if (fly) {
                    npcAvatar.playAnimation("fly");
                } else {
                    npcAvatar.playAnimation("walk");
                }
            }
        }
    }
    
    public void handleNPCRemove(NPCRemoveMessage message) {
        UUID npcId = message.getNPCId();
        NPCInfo npc = activeNPCs.remove(npcId);
        
        if (npc != null) {
            // Remove visual representation
            avatarManager.removeNPC(npcId);
            
            // Clean up movement
            movementManager.stopNPCMovement(npcId);
            
            Log.i("NPC", String.format("Removed NPC: %s", npcId));
        }
    }
    
    private SLAvatar createNPCAvatar(NPCInfo npc) {
        SLAvatar avatar = new SLAvatar(npc.getNPCId());
        avatar.setName(npc.getFirstName() + " " + npc.getLastName());
        avatar.setPosition(npc.getPosition());
        avatar.setRotation(npc.getRotation());
        avatar.setIsNPC(true);
        avatar.setOwnerId(npc.getOwnerId());
        
        return avatar;
    }
}
```

### Hypergrid Support

#### Inter-Grid Teleportation
```java
public class HypergridManager {
    private final Map<String, GridInfo> knownGrids = new ConcurrentHashMap<>();
    private final HypergridTeleportManager teleportManager;
    
    public class HypergridAddress {
        private final String gridUri;
        private final String regionName;
        private final Vector3f position;
        
        public static HypergridAddress parse(String hgAddress) {
            // Parse format: grid.example.com:8002:RegionName or 
            //              grid.example.com:8002:RegionName&vector=<128,128,25>
            String[] parts = hgAddress.split("&");
            String[] locationParts = parts[0].split(":");
            
            if (locationParts.length < 3) {
                throw new IllegalArgumentException("Invalid hypergrid address: " + hgAddress);
            }
            
            String gridUri = locationParts[0] + ":" + locationParts[1];
            String regionName = locationParts[2];
            
            Vector3f position = new Vector3f(128, 128, 25); // Default position
            
            // Parse vector if provided
            if (parts.length > 1 && parts[1].startsWith("vector=")) {
                position = parseVector(parts[1].substring(7));
            }
            
            return new HypergridAddress(gridUri, regionName, position);
        }
    }
    
    public CompletableFuture<TeleportResult> teleportToHypergridDestination(String hgAddress) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HypergridAddress destination = HypergridAddress.parse(hgAddress);
                
                // Query destination grid info
                GridInfo destGridInfo = queryGridInfo(destination.getGridUri());
                if (destGridInfo == null) {
                    return TeleportResult.failure("Cannot reach destination grid");
                }
                
                // Check if hypergrid is enabled
                if (!destGridInfo.getBoolean("HypergridEnabled", false)) {
                    return TeleportResult.failure("Destination grid does not support hypergrid");
                }
                
                // Perform hypergrid teleport
                HypergridTeleportRequest request = new HypergridTeleportRequest(
                    getCurrentAgent().getAgentId(),
                    getCurrentAgent().getSessionId(),
                    destination.getGridUri(),
                    destination.getRegionName(),
                    destination.getPosition()
                );
                
                HypergridTeleportResponse response = teleportManager.performHypergridTeleport(request);
                
                if (response.isSuccess()) {
                    // Update current grid context
                    setCurrentGrid(destGridInfo);
                    
                    // Update agent location
                    getCurrentAgent().setPosition(destination.getPosition());
                    getCurrentAgent().setRegionName(destination.getRegionName());
                    
                    return TeleportResult.success(destination.getPosition());
                } else {
                    return TeleportResult.failure(response.getErrorMessage());
                }
                
            } catch (Exception e) {
                Log.e("Hypergrid", "Teleport failed", e);
                return TeleportResult.failure("Teleport failed: " + e.getMessage());
            }
        });
    }
    
    public List<GridInfo> discoverNearbyGrids() {
        // Query current grid for hypergrid directory
        String directoryUrl = getCurrentGrid().getString("HypergridDirectory");
        if (directoryUrl != null) {
            try {
                return queryHypergridDirectory(directoryUrl);
            } catch (Exception e) {
                Log.w("Hypergrid", "Failed to query directory", e);
            }
        }
        
        return Collections.emptyList();
    }
    
    private List<GridInfo> queryHypergridDirectory(String directoryUrl) {
        // Implementation would query the hypergrid directory service
        // and return list of available grids
        return new ArrayList<>();
    }
}
```

## OpenSim-Specific UI Features

### Grid Selection Interface
```java
public class OpenSimGridSelector {
    
    private static final List<GridPreset> POPULAR_OPENSIM_GRIDS = Arrays.asList(
        new GridPreset("OSGrid", "login.osgrid.org", 8002, true, "The original OpenSim grid"),
        new GridPreset("Kitely", "grid.kitely.com", 8002, true, "Commercial OpenSim hosting"),
        new GridPreset("InWorldz", "grid.inworldz.com", 8002, true, "Popular community grid"),
        new GridPreset("3rd Rock Grid", "grid.3rdrockgrid.com", 8002, true, "Educational focused grid"),
        new GridPreset("FrancoGrid", "www.francogrid.org", 8002, true, "French OpenSim community"),
        new GridPreset("Great Canadian Grid", "gcc.greatcanadiangrid.ca", 8002, false, "Canadian OpenSim grid"),
        new GridPreset("Local OpenSim", "localhost", 9000, false, "Local development grid")
    );
    
    public class GridPreset {
        private final String name;
        private final String hostname;
        private final int port;
        private final boolean hypergridEnabled;
        private final String description;
        
        public String getLoginUri() {
            return String.format("http://%s:%d", hostname, port);
        }
        
        public String getGridInfoUri() {
            return getLoginUri() + "/get_grid_info";
        }
    }
    
    public void showGridSelectionDialog() {
        // Create grid selection UI
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select OpenSim Grid");
        
        String[] gridNames = POPULAR_OPENSIM_GRIDS.stream()
            .map(GridPreset::getName)
            .toArray(String[]::new);
        
        builder.setItems(gridNames, (dialog, which) -> {
            GridPreset selected = POPULAR_OPENSIM_GRIDS.get(which);
            connectToSelectedGrid(selected);
        });
        
        builder.setNeutralButton("Custom Grid", (dialog, which) -> {
            showCustomGridDialog();
        });
        
        builder.show();
    }
    
    private void showCustomGridDialog() {
        // Show dialog for custom grid entry
        View customGridView = LayoutInflater.from(context).inflate(R.layout.custom_grid_dialog, null);
        
        EditText hostnameEdit = customGridView.findViewById(R.id.hostname_edit);
        EditText portEdit = customGridView.findViewById(R.id.port_edit);
        CheckBox hypergridCheck = customGridView.findViewById(R.id.hypergrid_check);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Custom OpenSim Grid");
        builder.setView(customGridView);
        
        builder.setPositiveButton("Connect", (dialog, which) -> {
            String hostname = hostnameEdit.getText().toString();
            int port = Integer.parseInt(portEdit.getText().toString());
            boolean hypergrid = hypergridCheck.isChecked();
            
            GridPreset custom = new GridPreset("Custom", hostname, port, hypergrid, "Custom grid");
            connectToSelectedGrid(custom);
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
```

## Performance Optimizations for OpenSim

### OpenSim-Specific Caching
```java
public class OpenSimAssetCache extends StandardAssetCache {
    
    // OpenSim grids often have slower asset servers
    private static final long OPENSIM_TIMEOUT_MS = 30000; // 30 seconds
    private static final int MAX_CONCURRENT_DOWNLOADS = 3; // Reduced for OpenSim
    
    @Override
    protected CompletableFuture<Asset> downloadAsset(UUID assetId, String assetType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Use longer timeout for OpenSim
                return assetDownloader.download(assetId, assetType, OPENSIM_TIMEOUT_MS);
            } catch (TimeoutException e) {
                Log.w("AssetCache", "OpenSim asset download timeout: " + assetId);
                throw new AssetDownloadException("Download timeout", e);
            }
        }).orTimeout(OPENSIM_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }
    
    @Override
    protected void configureCachingStrategy() {
        // More aggressive caching for OpenSim
        setMemoryCacheSize(128 * 1024 * 1024); // 128MB
        setDiskCacheSize(1024 * 1024 * 1024);  // 1GB
        setMaxConcurrentDownloads(MAX_CONCURRENT_DOWNLOADS);
        
        // Longer cache retention
        setMemoryCacheTimeout(30 * 60 * 1000); // 30 minutes
        setDiskCacheTimeout(7 * 24 * 60 * 60 * 1000); // 7 days
    }
}
```

## Troubleshooting OpenSim Issues

### Common Problems and Solutions

#### Grid Connection Issues
```java
public class OpenSimTroubleshooting {
    
    public void diagnoseGridConnection(String gridUri) {
        try {
            // Test basic connectivity
            URL testUrl = new URL(gridUri + "/get_grid_info");
            HttpURLConnection conn = (HttpURLConnection) testUrl.openConnection();
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("GET");
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                Log.i("Diagnosis", "Grid responding to HTTP requests");
            } else {
                Log.w("Diagnosis", "Grid returned HTTP " + responseCode);
            }
            
            // Test grid info parsing
            try {
                GridInfo gridInfo = parseGridInfo(conn.getInputStream());
                Log.i("Diagnosis", "Grid info parsed successfully");
                
                // Check for common OpenSim issues
                if (!gridInfo.containsKey("OpenSimVersion")) {
                    Log.w("Diagnosis", "Grid may not be running OpenSimulator");
                }
                
                if (!gridInfo.getBoolean("LoginEnabled", true)) {
                    Log.w("Diagnosis", "Grid has login disabled");
                }
                
            } catch (Exception e) {
                Log.e("Diagnosis", "Failed to parse grid info", e);
            }
            
        } catch (Exception e) {
            Log.e("Diagnosis", "Grid connection test failed", e);
        }
    }
    
    public void diagnoseRegionCrossing() {
        // Check for common region crossing issues in OpenSim
        if (getCurrentRegion().getSize() != 256) {
            Log.i("Diagnosis", "Variable region size detected, may affect crossing");
        }
        
        // Check physics engine compatibility
        String physicsEngine = getCurrentRegion().getPhysicsEngine();
        if ("BulletSim".equals(physicsEngine)) {
            Log.i("Diagnosis", "BulletSim physics engine detected");
        } else if ("ODE".equals(physicsEngine)) {
            Log.i("Diagnosis", "ODE physics engine detected");
        }
        
        // Check for region neighbors
        List<RegionInfo> neighbors = getNeighboringRegions();
        if (neighbors.isEmpty()) {
            Log.w("Diagnosis", "No neighboring regions found, teleport required for movement");
        }
    }
    
    public void diagnoseAssetIssues() {
        // Check asset server connectivity
        String assetServerUrl = getCurrentGrid().getString("AssetServerURI");
        if (assetServerUrl != null) {
            try {
                testAssetServerConnectivity(assetServerUrl);
                Log.i("Diagnosis", "Asset server responding");
            } catch (Exception e) {
                Log.e("Diagnosis", "Asset server connection failed", e);
            }
        }
        
        // Check for common OpenSim asset problems
        if (getCurrentGrid().getName().toLowerCase().contains("osgrid")) {
            Log.i("Diagnosis", "OSGrid detected - using distributed asset servers");
        }
    }
}
```

## Integration Testing

### OpenSim-Specific Test Suite
```java
public class OpenSimIntegrationTests {
    
    @Test
    public void testVariableRegionSupport() {
        // Test different region sizes
        int[] regionSizes = {256, 512, 1024, 2048};
        
        for (int size : regionSizes) {
            RegionHandshakeMessage message = createRegionHandshakeMessage(size, size);
            regionManager.handleRegionHandshake(message);
            
            // Verify region size was applied
            assertEquals(size, regionManager.getCurrentRegionSize().width);
            assertEquals(size, regionManager.getCurrentRegionSize().height);
            
            // Verify terrain rendering adapted
            assertEquals(size / 16, terrainRenderer.getPatchCountX());
            assertEquals(size / 16, terrainRenderer.getPatchCountY());
        }
    }
    
    @Test
    public void testPhysicsMaterialSupport() {
        // Test physics material application
        PhysicsMaterial testMaterial = new PhysicsMaterial(0.5f, 0.8f, 1.2f, 1500.0f);
        
        SLObject testObject = createTestObject();
        physicsManager.setObjectMaterial(testObject, testMaterial);
        
        // Verify material was applied
        PhysicsMaterial applied = testObject.getPhysicsMaterial();
        assertNotNull(applied);
        assertEquals(0.5f, applied.getFriction(), 0.001f);
        assertEquals(0.8f, applied.getBounce(), 0.001f);
        assertEquals(1.2f, applied.getGravityModifier(), 0.001f);
        assertEquals(1500.0f, applied.getDensity(), 0.001f);
    }
    
    @Test
    public void testNPCManagement() {
        // Test NPC creation
        UUID npcId = UUID.randomUUID();
        UUID ownerId = getCurrentAgent().getAgentId();
        
        NPCCreateMessage createMessage = new NPCCreateMessage(
            npcId, "Test", "NPC", ownerId, 
            new Vector3f(128, 128, 25), Quaternionf.IDENTITY,
            null, false
        );
        
        npcManager.handleNPCCreate(createMessage);
        
        // Verify NPC was created
        assertTrue(npcManager.hasNPC(npcId));
        NPCInfo npc = npcManager.getNPC(npcId);
        assertEquals("Test", npc.getFirstName());
        assertEquals("NPC", npc.getLastName());
        assertEquals(ownerId, npc.getOwnerId());
        
        // Test NPC movement
        Vector3f targetPosition = new Vector3f(100, 100, 25);
        NPCMoveMessage moveMessage = new NPCMoveMessage(npcId, targetPosition, 5.0f, false);
        
        npcManager.handleNPCMove(moveMessage);
        
        // Verify movement started
        assertTrue(movementManager.isNPCMoving(npcId));
        
        // Test NPC removal
        NPCRemoveMessage removeMessage = new NPCRemoveMessage(npcId);
        npcManager.handleNPCRemove(removeMessage);
        
        // Verify NPC was removed
        assertFalse(npcManager.hasNPC(npcId));
    }
    
    @Test
    public void testHypergridTeleport() {
        // Mock hypergrid-enabled grid
        GridInfo mockGrid = createMockHypergridGrid();
        setCurrentGrid(mockGrid);
        
        // Test hypergrid address parsing
        String hgAddress = "test.grid.com:8002:TestRegion&vector=<64,64,30>";
        HypergridAddress parsed = HypergridAddress.parse(hgAddress);
        
        assertEquals("test.grid.com:8002", parsed.getGridUri());
        assertEquals("TestRegion", parsed.getRegionName());
        assertEquals(new Vector3f(64, 64, 30), parsed.getPosition());
        
        // Test teleport (would require mock server)
        // TeleportResult result = hypergridManager.teleportToHypergridDestination(hgAddress).get();
        // assertTrue(result.isSuccess());
    }
}
```

---

This comprehensive OpenSimulator compatibility guide ensures that Linkpoint can fully interact with the rich ecosystem of OpenSim grids, taking advantage of their enhanced features while maintaining compatibility with the standard Second Life protocol.
# Second Life Modernization Integration Guide

## Overview

This guide provides specific implementation details for integrating modern virtual world technologies with Second Life protocols, drawing from the referenced projects:

- **@webaverse-studios/webaverse**: Modern WebXR and asset pipeline technologies
- **OMI Group (github.com/omigroup)**: Open Metaverse Interoperability standards  
- **@cinderblocks/libremetaverse**: Contemporary C# Second Life protocol implementation
- **Second Life OpenMetaverse Community**: Protocol modernization insights

## Table of Contents

1. [Protocol Modernization](#protocol-modernization)
2. [Asset Format Integration](#asset-format-integration)
3. [Graphics Pipeline Updates](#graphics-pipeline-updates)
4. [Interoperability Implementation](#interoperability-implementation)
5. [Performance Optimization](#performance-optimization)

## Protocol Modernization

### Current Second Life Protocol Issues

The Lumiya Viewer currently implements Second Life protocols with several limitations:

```java
// Current problematic implementation in SLAgentCircuit.java
public class SLAgentCircuit {
    private DatagramSocket udpSocket;  // UDP only - no modern transport
    
    public void sendMessage(SLMessage message) {
        // Legacy binary serialization - inefficient
        byte[] data = message.serializeBinary();
        
        // No compression, no HTTP/2, no WebSocket support
        udpSocket.send(new DatagramPacket(data, data.length));
    }
}
```

### Modern Protocol Implementation

Based on insights from @cinderblocks/libremetaverse, here's the modernized approach:

#### 1. Hybrid Transport Layer

```java
// New file: app/src/main/java/com/lumiyaviewer/lumiya/protocol/HybridSLTransport.java
package com.lumiyaviewer.lumiya.protocol;

public class HybridSLTransport implements SLTransport {
    private final HTTP2CapsClient capsClient;        // Modern CAPS using HTTP/2
    private final WebSocketEventClient eventClient;  // Real-time events
    private final UDPLegacyCircuit udpCircuit;       // Legacy compatibility
    private final MessageRouter router;
    
    // Message routing based on type and capabilities
    @Override
    public CompletableFuture<SLResponse> sendMessage(SLMessage message) {
        TransportRoute route = router.selectOptimalRoute(message);
        
        switch (route.getTransport()) {
            case HTTP2_CAPS:
                // Use HTTP/2 for large data transfers, asset uploads
                return capsClient.sendAsync(message.toHTTP2Request())
                    .thenApply(this::parseHTTP2Response);
                    
            case WEBSOCKET_REALTIME:
                // Use WebSocket for chat, object updates, real-time events
                return eventClient.sendAsync(message.toWebSocketFrame())
                    .thenApply(this::parseWebSocketResponse);
                    
            case UDP_LEGACY:
                // Fall back to UDP for legacy message compatibility
                return udpCircuit.sendAsync(message.toUDPPacket())
                    .thenApply(this::parseUDPResponse);
                    
            default:
                throw new UnsupportedOperationException("Unknown transport: " + route);
        }
    }
    
    // Enhanced authentication using modern OAuth2 flow
    public void authenticate(LoginCredentials credentials) {
        // Modern authentication flow inspired by LibreMetaverse
        OAuth2AuthFlow authFlow = OAuth2AuthFlow.builder()
            .withClientId(SL_CLIENT_ID)
            .withScopes("avatar.read", "inventory.write", "chat.send")
            .withRedirectUri("lumiya://auth/callback")
            .build();
            
        AuthToken token = authFlow.authenticate(credentials);
        
        // Configure all transport layers with token
        capsClient.setAuthToken(token);
        eventClient.setAuthToken(token);
        udpCircuit.setLegacySession(token.toLegacySession());
    }
}
```

#### 2. Enhanced Message Serialization

```java
// New file: app/src/main/java/com/lumiyaviewer/lumiya/protocol/ModernSLSerialization.java
package com.lumiyaviewer.lumiya.protocol;

public class ModernSLSerialization {
    private final ProtobufSerializer protobufSerializer;
    private final MessagePackSerializer msgpackSerializer;
    private final LLSDSerializer llsdSerializer;          // Legacy LLSD support
    private final BinarySerializer binarySerializer;      // Legacy binary
    
    public byte[] serialize(SLMessage message, SerializationFormat format) {
        switch (format) {
            case PROTOBUF:
                // Modern efficient binary format for new message types
                return protobufSerializer.serialize(message.toProtobuf());
                
            case MESSAGEPACK:
                // Compact binary format optimized for mobile
                return msgpackSerializer.serialize(message.toMessagePack());
                
            case LLSD_XML:
                // Legacy LLSD XML for CAPS compatibility
                return llsdSerializer.serializeXML(message.toLLSD());
                
            case LLSD_BINARY:
                // Legacy LLSD binary for efficiency
                return llsdSerializer.serializeBinary(message.toLLSD());
                
            case SL_BINARY:
                // Legacy SL binary format for UDP messages
                return binarySerializer.serialize(message);
                
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }
    
    // Automatic format detection and deserialization
    public SLMessage deserialize(byte[] data) {
        SerializationFormat format = detectFormat(data);
        return deserialize(data, format);
    }
    
    private SerializationFormat detectFormat(byte[] data) {
        // Magic number detection for different formats
        if (isProtobuf(data)) return SerializationFormat.PROTOBUF;
        if (isMessagePack(data)) return SerializationFormat.MESSAGEPACK;
        if (isLLSDXML(data)) return SerializationFormat.LLSD_XML;
        if (isLLSDBinary(data)) return SerializationFormat.LLSD_BINARY;
        return SerializationFormat.SL_BINARY; // Default fallback
    }
}
```

#### 3. Modern CAPS Implementation

```java
// New file: app/src/main/java/com/lumiyaviewer/lumiya/protocol/ModernCapsClient.java
package com.lumiyaviewer.lumiya.protocol;

public class ModernCapsClient {
    private final HTTP2Client httpClient;
    private final CompletionService<CapsResponse> responseService;
    private final Map<String, String> capabilities = new ConcurrentHashMap<>();
    
    public void initializeCapabilities(URI seedCapability, AuthToken token) {
        // Fetch initial capability seed
        CapsRequest seedRequest = CapsRequest.builder()
            .uri(seedCapability)
            .token(token)
            .format(SerializationFormat.LLSD_XML)
            .build();
            
        CapsResponse seedResponse = httpClient.send(seedRequest);
        Map<String, String> caps = parseSeedCapabilities(seedResponse);
        capabilities.putAll(caps);
        
        // Request additional capabilities needed for modern features
        requestModernCapabilities(token);
    }
    
    private void requestModernCapabilities(AuthToken token) {
        // Request capabilities for modern features
        List<String> modernCaps = Arrays.asList(
            "EventQueueGet",           // Event queue for real-time updates
            "UploadBakedTexture",      // Avatar appearance
            "UploaderRequestAsset",    // Asset uploads
            "ViewerMetrics",           // Performance telemetry
            "InventoryAPIv3",          // Modern inventory API
            "ObjectAnimation",         // Enhanced animation support
            "MeshUploadFlag",          // Mesh upload support
            "GetDisplayNames",         // Display name resolution
            "AgentPreferences"         // User preference sync
        );
        
        for (String capName : modernCaps) {
            if (capabilities.containsKey(capName)) {
                Log.d("ModernCaps", "Capability available: " + capName);
            } else {
                Log.w("ModernCaps", "Capability not available: " + capName);
            }
        }
    }
    
    // Enhanced asset upload with progress tracking
    public CompletableFuture<AssetUploadResult> uploadAssetAsync(Asset asset) {
        String uploadCap = capabilities.get("UploaderRequestAsset");
        if (uploadCap == null) {
            throw new UnsupportedOperationException("Asset upload not supported");
        }
        
        return CompletableFuture.supplyAsync(() -> {
            // Modern multipart upload with progress callbacks
            MultipartUpload upload = MultipartUpload.builder()
                .asset(asset)
                .capability(uploadCap)
                .onProgress(this::reportUploadProgress)
                .build();
                
            return httpClient.upload(upload);
        });
    }
    
    // WebSocket event queue integration
    public void subscribeToEventQueue() {
        String eventQueueCap = capabilities.get("EventQueueGet");
        if (eventQueueCap != null) {
            WebSocketEventClient eventClient = new WebSocketEventClient(eventQueueCap);
            eventClient.subscribe("ChatFromSimulator", this::handleChatMessage);
            eventClient.subscribe("ObjectUpdate", this::handleObjectUpdate);
            eventClient.subscribe("AvatarAnimation", this::handleAvatarAnimation);
        }
    }
}
```

## Asset Format Integration

### Modern Asset Pipeline Using Webaverse Insights

The Webaverse project demonstrates advanced asset handling that can be adapted for Second Life:

#### 1. Universal Asset Format Support

```java
// New file: app/src/main/java/com/lumiyaviewer/lumiya/assets/UniversalAssetLoader.java
package com.lumiyaviewer.lumiya.assets;

public class UniversalAssetLoader {
    private final GLTFLoader gltfLoader;              // Webaverse standard
    private final VRMLoader vrmLoader;                // Avatar format
    private final VOXLoader voxLoader;                // Voxel models
    private final BasisUniversalLoader basisLoader;   // Universal textures
    private final SLLegacyLoader legacyLoader;        // SL compatibility
    
    public CompletableFuture<Asset> loadAssetAsync(AssetReference ref) {
        return CompletableFuture.supplyAsync(() -> {
            String format = ref.getFormat().toLowerCase();
            
            switch (format) {
                case "gltf":
                case "glb":
                    // Modern 3D format with PBR materials
                    return loadGLTFAsset(ref);
                    
                case "vrm":
                    // Webaverse-compatible avatar format
                    return loadVRMAvatar(ref);
                    
                case "vox":
                    // Voxel format popular in metaverse
                    return loadVoxelAsset(ref);
                    
                case "basis":
                case "ktx2":
                    // Universal texture compression
                    return loadBasisTexture(ref);
                    
                case "j2k":
                case "jp2":
                    // Legacy Second Life texture format
                    return loadLegacyTexture(ref);
                    
                case "llm":
                    // Legacy Second Life mesh format
                    return loadLegacyMesh(ref);
                    
                default:
                    throw new UnsupportedAssetFormatException("Unknown format: " + format);
            }
        });
    }
    
    private Asset loadGLTFAsset(AssetReference ref) {
        GLTFDocument gltf = gltfLoader.load(ref.getURL());
        
        // Process OMI extensions for Second Life compatibility
        OMIExtensionProcessor omiProcessor = new OMIExtensionProcessor();
        omiProcessor.processExtensions(gltf);
        
        // Convert to Second Life coordinate system
        CoordinateSystemConverter converter = new CoordinateSystemConverter();
        converter.convertFromGLTFToSL(gltf);
        
        // Convert materials to Second Life format
        MaterialConverter materialConverter = new MaterialConverter();
        List<SLMaterial> slMaterials = materialConverter.convertPBRToSL(gltf.getMaterials());
        
        return new SLMeshAsset(gltf, slMaterials);
    }
    
    private Asset loadVRMAvatar(AssetReference ref) {
        VRMDocument vrm = vrmLoader.load(ref.getURL());
        
        // Convert VRM avatar to Second Life avatar format
        VRMToSLConverter converter = new VRMToSLConverter();
        SLAvatar slAvatar = converter.convert(vrm);
        
        // Map VRM bones to Second Life skeleton
        SkeletonMapper mapper = new SkeletonMapper();
        mapper.mapVRMToSLSkeleton(slAvatar);
        
        return slAvatar;
    }
}
```

#### 2. Enhanced Texture Management

```java
// New file: app/src/main/java/com/lumiyaviewer/lumiya/assets/ModernTextureManager.java
package com.lumiyaviewer.lumiya.assets;

public class ModernTextureManager {
    private final BasisUniversalTranscoder basisTranscoder;
    private final ASTC_Encoder astcEncoder;
    private final JPEG2000Decoder j2kDecoder;         // Legacy support
    private final TextureCache cache;
    private final GPUMemoryManager gpuMemory;
    
    public CompletableFuture<GLTexture> loadTextureAsync(UUID textureId) {
        // Check cache first
        GLTexture cached = cache.get(textureId);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            // Try to fetch modern format first
            TextureData modernData = fetchModernTexture(textureId);
            if (modernData != null) {
                return loadModernTexture(modernData);
            }
            
            // Fall back to legacy JPEG2000
            TextureData legacyData = fetchLegacyTexture(textureId);
            return loadLegacyTexture(legacyData);
        });
    }
    
    private GLTexture loadModernTexture(TextureData data) {
        if (data.isBasisUniversal()) {
            // Transcode Basis Universal to optimal GPU format
            TextureFormat optimalFormat = selectOptimalFormat();
            byte[] transcodedData = basisTranscoder.transcode(
                data.getData(), optimalFormat);
            return gpuMemory.createCompressedTexture(transcodedData, optimalFormat);
        }
        
        if (data.isASTC()) {
            // Direct ASTC loading for supported GPUs
            return gpuMemory.createCompressedTexture(data.getData(), TextureFormat.ASTC_4x4);
        }
        
        throw new UnsupportedOperationException("Unknown modern texture format");
    }
    
    private GLTexture loadLegacyTexture(TextureData data) {
        // Decode JPEG2000 in background thread
        BufferedImage image = j2kDecoder.decode(data.getData());
        
        // Transcode to modern compressed format for GPU efficiency
        if (GPUCapabilities.supportsASTC()) {
            byte[] astcData = astcEncoder.encode(image);
            return gpuMemory.createCompressedTexture(astcData, TextureFormat.ASTC_4x4);
        }
        
        // Fall back to uncompressed upload
        return gpuMemory.createUncompressedTexture(image);
    }
    
    private TextureFormat selectOptimalFormat() {
        GPUCapabilities caps = GPUCapabilities.detect();
        
        if (caps.supportsASTC()) {
            return TextureFormat.ASTC_4x4;      // Best quality/size ratio
        } else if (caps.supportsETC2()) {
            return TextureFormat.ETC2_RGB;      // Good mobile compatibility
        } else {
            return TextureFormat.DXT1;          // PC fallback
        }
    }
}
```

## Graphics Pipeline Updates

### PBR Integration for Second Life

Modern graphics engines use Physically Based Rendering (PBR). Here's how to integrate it with Second Life's material system:

#### 1. PBR Material Converter

```java
// New file: app/src/main/java/com/lumiyaviewer/lumiya/graphics/PBRMaterialConverter.java
package com.lumiyaviewer.lumiya.graphics;

public class PBRMaterialConverter {
    
    // Convert legacy Second Life materials to PBR
    public PBRMaterial convertSLToPBR(SLMaterial slMaterial) {
        PBRMaterial.Builder pbrBuilder = PBRMaterial.builder();
        
        // Base color from diffuse texture
        if (slMaterial.getDiffuseTexture() != null) {
            pbrBuilder.baseColorTexture(slMaterial.getDiffuseTexture());
        }
        pbrBuilder.baseColorFactor(slMaterial.getDiffuseColor());
        
        // Convert SL specular to PBR metallic/roughness
        float metallic = calculateMetallicFromSpecular(slMaterial);
        float roughness = calculateRoughnessFromShininess(slMaterial.getShininess());
        
        pbrBuilder.metallicFactor(metallic);
        pbrBuilder.roughnessFactor(roughness);
        
        // Normal mapping
        if (slMaterial.getNormalTexture() != null) {
            pbrBuilder.normalTexture(slMaterial.getNormalTexture());
        }
        
        // Emissive
        if (slMaterial.hasGlow()) {
            pbrBuilder.emissiveFactor(slMaterial.getGlowColor());
            pbrBuilder.emissiveStrength(slMaterial.getGlowIntensity());
        }
        
        // Alpha handling
        if (slMaterial.hasAlpha()) {
            pbrBuilder.alphaMode(PBRMaterial.AlphaMode.BLEND);
            pbrBuilder.alphaCutoff(slMaterial.getAlphaMaskCutoff());
        }
        
        return pbrBuilder.build();
    }
    
    private float calculateMetallicFromSpecular(SLMaterial slMaterial) {
        // Heuristic conversion from specular to metallic workflow
        Vector3f specularColor = slMaterial.getSpecularColor();
        float specularIntensity = (specularColor.x + specularColor.y + specularColor.z) / 3.0f;
        
        // High specular intensity suggests metallic material
        return Math.min(specularIntensity * 2.0f, 1.0f);
    }
    
    private float calculateRoughnessFromShininess(float shininess) {
        // Convert OpenGL shininess to PBR roughness
        // Shininess range: 0-128, Roughness range: 0-1
        return Math.max(0.0f, 1.0f - (float)Math.sqrt(shininess / 128.0f));
    }
}
```

#### 2. Modern Shader System

```java
// New file: app/src/main/java/com/lumiyaviewer/lumiya/graphics/ModernShaderManager.java
package com.lumiyaviewer.lumiya.graphics;

public class ModernShaderManager {
    private final Map<String, ShaderProgram> shaderCache = new ConcurrentHashMap<>();
    private final ShaderCompiler compiler;
    
    public ShaderProgram getShader(ShaderType type, MaterialProperties properties) {
        String shaderKey = generateShaderKey(type, properties);
        
        return shaderCache.computeIfAbsent(shaderKey, key -> {
            return compileShader(type, properties);
        });
    }
    
    private ShaderProgram compileShader(ShaderType type, MaterialProperties properties) {
        switch (type) {
            case PBR_STANDARD:
                return compilePBRShader(properties);
            case AVATAR_RIGGED:
                return compileAvatarShader(properties);
            case TERRAIN:
                return compileTerrainShader(properties);
            case SKY:
                return compileSkyShader(properties);
            case WATER:
                return compileWaterShader(properties);
            default:
                throw new IllegalArgumentException("Unknown shader type: " + type);
        }
    }
    
    private ShaderProgram compilePBRShader(MaterialProperties properties) {
        StringBuilder vertexShader = new StringBuilder();
        StringBuilder fragmentShader = new StringBuilder();
        
        // Vertex shader
        vertexShader.append("#version 300 es\n");
        vertexShader.append("precision highp float;\n\n");
        
        // Standard vertex attributes
        vertexShader.append("layout(location = 0) in vec3 a_position;\n");
        vertexShader.append("layout(location = 1) in vec3 a_normal;\n");
        vertexShader.append("layout(location = 2) in vec2 a_texCoord;\n");
        
        if (properties.hasTangents()) {
            vertexShader.append("layout(location = 3) in vec4 a_tangent;\n");
        }
        
        if (properties.isSkinned()) {
            vertexShader.append("layout(location = 4) in ivec4 a_joints;\n");
            vertexShader.append("layout(location = 5) in vec4 a_weights;\n");
        }
        
        // Uniform buffer objects for efficiency
        vertexShader.append("layout(std140) uniform CameraUBO {\n");
        vertexShader.append("    mat4 u_viewMatrix;\n");
        vertexShader.append("    mat4 u_projectionMatrix;\n");
        vertexShader.append("    vec3 u_cameraPosition;\n");
        vertexShader.append("};\n\n");
        
        if (properties.isSkinned()) {
            vertexShader.append("layout(std140) uniform SkinningUBO {\n");
            vertexShader.append("    mat4 u_jointMatrices[64];\n");
            vertexShader.append("};\n\n");
        }
        
        // Fragment shader - PBR implementation
        fragmentShader.append("#version 300 es\n");
        fragmentShader.append("precision highp float;\n\n");
        
        // PBR lighting calculation
        fragmentShader.append(loadShaderSource("pbr_lighting.glsl"));
        
        // Image-based lighting for realistic reflections
        if (properties.hasIBL()) {
            fragmentShader.append(loadShaderSource("ibl_lighting.glsl"));
        }
        
        return compiler.compile(vertexShader.toString(), fragmentShader.toString());
    }
}
```

## Interoperability Implementation

### OMI Standards Integration

The OMI Group defines standards for metaverse interoperability. Here's how to implement them:

#### 1. glTF Extension Handler

```java
// New file: app/src/main/java/com/lumiyaviewer/lumiya/interop/OMIExtensionHandler.java
package com.lumiyaviewer.lumiya.interop;

public class OMIExtensionHandler {
    
    // OMI_collider extension for physics
    public CollisionShape processOMICollider(GLTFNode node) {
        JSONObject colliderExt = node.getExtension("OMI_collider");
        if (colliderExt == null) return null;
        
        String type = colliderExt.getString("type");
        JSONObject shape = colliderExt.getJSONObject("shape");
        
        switch (type) {
            case "box":
                Vector3f size = parseVector3(shape.getJSONArray("size"));
                return new BoxCollisionShape(size);
                
            case "sphere":
                float radius = shape.getFloat("radius");
                return new SphereCollisionShape(radius);
                
            case "capsule":
                float height = shape.getFloat("height");
                float capRadius = shape.getFloat("radius");
                return new CapsuleCollisionShape(height, capRadius);
                
            case "mesh":
                // Use the node's mesh geometry for collision
                return new MeshCollisionShape(node.getMesh());
                
            default:
                Log.w("OMI", "Unknown collider type: " + type);
                return null;
        }
    }
    
    // OMI_spawn_point extension for teleportation
    public SpawnPoint processOMISpawnPoint(GLTFScene scene) {
        JSONObject spawnExt = scene.getExtension("OMI_spawn_point");
        if (spawnExt == null) return null;
        
        Vector3f position = parseVector3(spawnExt.getJSONArray("position"));
        Quaternionf rotation = parseQuaternion(spawnExt.getJSONArray("rotation"));
        
        SpawnPoint spawnPoint = new SpawnPoint(position, rotation);
        
        // Optional spawn point metadata
        if (spawnExt.has("title")) {
            spawnPoint.setTitle(spawnExt.getString("title"));
        }
        
        if (spawnExt.has("description")) {
            spawnPoint.setDescription(spawnExt.getString("description"));
        }
        
        return spawnPoint;
    }
    
    // OMI_vrm extension for avatar interoperability
    public VRMAvatar processOMIVRM(GLTFDocument gltf) {
        JSONObject vrmExt = gltf.getExtension("OMI_vrm");
        if (vrmExt == null) return null;
        
        VRMAvatar avatar = new VRMAvatar();
        
        // VRM metadata
        JSONObject meta = vrmExt.getJSONObject("meta");
        avatar.setName(meta.getString("name"));
        avatar.setVersion(meta.getString("version"));
        avatar.setAuthor(meta.getString("author"));
        
        // VRM humanoid bone mapping
        JSONObject humanoid = vrmExt.getJSONObject("humanoid");
        JSONArray humanBones = humanoid.getJSONArray("humanBones");
        
        for (int i = 0; i < humanBones.length(); i++) {
            JSONObject bone = humanBones.getJSONObject(i);
            String boneName = bone.getString("bone");
            int nodeIndex = bone.getInt("node");
            
            avatar.mapBone(boneName, nodeIndex);
        }
        
        // Convert VRM to Second Life avatar format
        return convertVRMToSLAvatar(avatar, gltf);
    }
    
    private VRMAvatar convertVRMToSLAvatar(VRMAvatar vrm, GLTFDocument gltf) {
        // Map VRM bone names to Second Life bone names
        Map<String, String> boneMapping = createVRMToSLBoneMapping();
        
        SLSkeleton slSkeleton = new SLSkeleton();
        for (Map.Entry<String, Integer> vrmBone : vrm.getBoneMapping().entrySet()) {
            String vrmBoneName = vrmBone.getKey();
            String slBoneName = boneMapping.get(vrmBoneName);
            
            if (slBoneName != null) {
                GLTFNode node = gltf.getNode(vrmBone.getValue());
                SLBone slBone = convertGLTFNodeToSLBone(node, slBoneName);
                slSkeleton.addBone(slBone);
            }
        }
        
        vrm.setSLSkeleton(slSkeleton);
        return vrm;
    }
    
    private Map<String, String> createVRMToSLBoneMapping() {
        Map<String, String> mapping = new HashMap<>();
        
        // Head and neck
        mapping.put("head", "mHead");
        mapping.put("neck", "mNeck");
        
        // Torso
        mapping.put("spine", "mSpine1");
        mapping.put("chest", "mSpine2");
        mapping.put("upperChest", "mSpine3");
        mapping.put("hips", "mPelvis");
        
        // Arms
        mapping.put("leftShoulder", "mCollarLeft");
        mapping.put("leftUpperArm", "mShoulderLeft");
        mapping.put("leftLowerArm", "mElbowLeft");
        mapping.put("leftHand", "mWristLeft");
        
        mapping.put("rightShoulder", "mCollarRight");
        mapping.put("rightUpperArm", "mShoulderRight");
        mapping.put("rightLowerArm", "mElbowRight");
        mapping.put("rightHand", "mWristRight");
        
        // Legs
        mapping.put("leftUpperLeg", "mHipLeft");
        mapping.put("leftLowerLeg", "mKneeLeft");
        mapping.put("leftFoot", "mAnkleLeft");
        
        mapping.put("rightUpperLeg", "mHipRight");
        mapping.put("rightLowerLeg", "mKneeRight");
        mapping.put("rightFoot", "mAnkleRight");
        
        return mapping;
    }
}
```

#### 2. Cross-Platform Asset Bridge

```java
// New file: app/src/main/java/com/lumiyaviewer/lumiya/interop/CrossPlatformAssetBridge.java
package com.lumiyaviewer.lumiya.interop;

public class CrossPlatformAssetBridge {
    private final AssetConverter converter;
    private final AssetValidator validator;
    
    // Convert Second Life assets to universal formats
    public UniversalAsset exportSLAssetToUniversal(SLAsset slAsset) {
        switch (slAsset.getType()) {
            case MESH:
                return convertSLMeshToGLTF((SLMesh) slAsset);
            case TEXTURE:
                return convertSLTextureToBasis((SLTexture) slAsset);
            case ANIMATION:
                return convertSLAnimationToGLTF((SLAnimation) slAsset);
            case AVATAR:
                return convertSLAvatarToVRM((SLAvatar) slAsset);
            default:
                throw new UnsupportedOperationException("Cannot convert asset type: " + slAsset.getType());
        }
    }
    
    private GLTFAsset convertSLMeshToGLTF(SLMesh slMesh) {
        GLTFDocument gltf = new GLTFDocument();
        
        // Convert geometry
        GLTFMesh gltfMesh = new GLTFMesh();
        for (SLMeshFace face : slMesh.getFaces()) {
            GLTFPrimitive primitive = new GLTFPrimitive();
            
            // Convert vertices
            primitive.setPositions(face.getVertices());
            primitive.setNormals(face.getNormals());
            primitive.setTexCoords(face.getTexCoords());
            
            // Convert material
            SLMaterial slMaterial = face.getMaterial();
            GLTFMaterial gltfMaterial = convertSLMaterialToGLTF(slMaterial);
            primitive.setMaterial(gltfMaterial);
            
            gltfMesh.addPrimitive(primitive);
        }
        
        gltf.addMesh(gltfMesh);
        
        // Add OMI extensions for Second Life compatibility
        addOMIExtensions(gltf, slMesh);
        
        return new GLTFAsset(gltf);
    }
    
    private GLTFMaterial convertSLMaterialToGLTF(SLMaterial slMaterial) {
        GLTFMaterial gltfMaterial = new GLTFMaterial();
        
        // Use PBR metallic-roughness workflow
        PBRMetallicRoughness pbr = new PBRMetallicRoughness();
        
        // Base color from diffuse
        pbr.setBaseColorTexture(slMaterial.getDiffuseTexture());
        pbr.setBaseColorFactor(slMaterial.getDiffuseColor());
        
        // Convert specular to metallic/roughness
        float metallic = calculateMetallic(slMaterial.getSpecularColor());
        float roughness = calculateRoughness(slMaterial.getShininess());
        pbr.setMetallicFactor(metallic);
        pbr.setRoughnessFactor(roughness);
        
        gltfMaterial.setPBRMetallicRoughness(pbr);
        
        // Normal mapping
        if (slMaterial.getNormalTexture() != null) {
            gltfMaterial.setNormalTexture(slMaterial.getNormalTexture());
        }
        
        // Emissive materials
        if (slMaterial.hasGlow()) {
            gltfMaterial.setEmissiveFactor(slMaterial.getGlowColor());
        }
        
        return gltfMaterial;
    }
    
    private BasisUniversalAsset convertSLTextureToBasis(SLTexture slTexture) {
        // Decode JPEG2000 to raw image data
        BufferedImage image = decodeJPEG2000(slTexture.getData());
        
        // Encode to Basis Universal for universal GPU compatibility
        BasisEncoder encoder = new BasisEncoder();
        byte[] basisData = encoder.encode(image, BasisEncoder.Quality.HIGH);
        
        return new BasisUniversalAsset(basisData);
    }
    
    // Import universal assets to Second Life format
    public SLAsset importUniversalAssetToSL(UniversalAsset universalAsset) {
        switch (universalAsset.getType()) {
            case GLTF:
                return convertGLTFToSLMesh((GLTFAsset) universalAsset);
            case VRM:
                return convertVRMToSLAvatar((VRMAsset) universalAsset);
            case BASIS_UNIVERSAL:
                return convertBasisToSLTexture((BasisUniversalAsset) universalAsset);
            default:
                throw new UnsupportedOperationException("Cannot import asset type: " + universalAsset.getType());
        }
    }
}
```

## Performance Optimization

### Mobile-Specific Optimizations

Drawing from Webaverse's mobile optimization techniques:

#### 1. Adaptive Quality System

```java
// New file: app/src/main/java/com/lumiyaviewer/lumiya/performance/AdaptiveQualityManager.java
package com.lumiyaviewer.lumiya.performance;

public class AdaptiveQualityManager {
    private final PerformanceMonitor perfMonitor;
    private final QualitySettings currentSettings;
    private final DeviceProfiler deviceProfiler;
    
    public void updateQualitySettings() {
        PerformanceMetrics metrics = perfMonitor.getCurrentMetrics();
        DeviceCapabilities caps = deviceProfiler.getCapabilities();
        
        if (metrics.getAverageFrameTime() > TARGET_FRAME_TIME) {
            // Performance is suffering, reduce quality
            reduceQuality(metrics, caps);
        } else if (metrics.isStable() && hasPerformanceHeadroom(metrics)) {
            // Performance is good, can increase quality
            increaseQuality(metrics, caps);
        }
    }
    
    private void reduceQuality(PerformanceMetrics metrics, DeviceCapabilities caps) {
        if (currentSettings.textureQuality > QualityLevel.LOW) {
            // Reduce texture resolution
            currentSettings.textureQuality = QualityLevel.LOW;
            textureManager.setMaxTextureSize(512);
        }
        
        if (currentSettings.lodBias < 2.0f) {
            // Increase LOD bias to use lower detail models sooner
            currentSettings.lodBias += 0.5f;
            renderingEngine.setLODBias(currentSettings.lodBias);
        }
        
        if (currentSettings.shadowQuality > QualityLevel.OFF) {
            // Disable shadows if really struggling
            currentSettings.shadowQuality = QualityLevel.OFF;
            renderingEngine.disableShadows();
        }
        
        if (currentSettings.postProcessing) {
            // Disable post-processing effects
            currentSettings.postProcessing = false;
            renderingEngine.disablePostProcessing();
        }
    }
    
    private void increaseQuality(PerformanceMetrics metrics, DeviceCapabilities caps) {
        if (caps.isHighEnd() && currentSettings.textureQuality < QualityLevel.HIGH) {
            currentSettings.textureQuality = QualityLevel.HIGH;
            textureManager.setMaxTextureSize(2048);
        }
        
        if (currentSettings.lodBias > 0.0f) {
            currentSettings.lodBias -= 0.25f;
            renderingEngine.setLODBias(currentSettings.lodBias);
        }
        
        if (caps.supportsAdvancedShaders() && !currentSettings.postProcessing) {
            currentSettings.postProcessing = true;
            renderingEngine.enablePostProcessing();
        }
    }
}
```

#### 2. Bandwidth-Aware Asset Streaming

```java
// New file: app/src/main/java/com/lumiyaviewer/lumiya/performance/BandwidthAwareStreaming.java
package com.lumiyaviewer.lumiya.performance;

public class BandwidthAwareStreaming {
    private final NetworkSpeedMonitor networkMonitor;
    private final AssetPriorityQueue assetQueue;
    private final CompressionManager compressionManager;
    
    public void updateStreamingStrategy() {
        NetworkSpeed speed = networkMonitor.getCurrentSpeed();
        ConnectionType connection = networkMonitor.getConnectionType();
        
        switch (connection) {
            case WIFI:
                configureForWiFi(speed);
                break;
            case CELLULAR_5G:
                configureFor5G(speed);
                break;
            case CELLULAR_4G:
                configureFor4G(speed);
                break;
            case CELLULAR_3G:
                configureFor3G(speed);
                break;
            default:
                configureForSlowConnection(speed);
                break;
        }
    }
    
    private void configureForWiFi(NetworkSpeed speed) {
        // High bandwidth available
        assetQueue.setMaxConcurrentDownloads(8);
        compressionManager.setCompressionLevel(CompressionLevel.BALANCED);
        
        // Enable high-quality texture streaming
        textureStreamer.setMaxTextureSize(2048);
        textureStreamer.enableMipmapStreaming(true);
    }
    
    private void configureFor4G(NetworkSpeed speed) {
        // Moderate bandwidth, be more conservative
        assetQueue.setMaxConcurrentDownloads(4);
        compressionManager.setCompressionLevel(CompressionLevel.HIGH);
        
        // Limit texture sizes
        textureStreamer.setMaxTextureSize(1024);
        textureStreamer.enableMipmapStreaming(true);
    }
    
    private void configureFor3G(NetworkSpeed speed) {
        // Limited bandwidth, aggressive optimization
        assetQueue.setMaxConcurrentDownloads(2);
        compressionManager.setCompressionLevel(CompressionLevel.MAXIMUM);
        
        // Small textures only
        textureStreamer.setMaxTextureSize(512);
        textureStreamer.enableMipmapStreaming(false);
        
        // Prioritize essential assets only
        assetQueue.setStrictPrioritization(true);
    }
}
```

This comprehensive integration guide provides specific implementation details for modernizing Lumiya Viewer while maintaining Second Life compatibility and leveraging the best practices from the referenced projects.
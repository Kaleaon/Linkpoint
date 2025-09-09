# Linkpoint API Usage Catalog

## Overview
This document provides a comprehensive mapping of all APIs, libraries, and frameworks used in the Linkpoint (Lumiya Viewer) project.

## External APIs and Libraries

### Android Framework APIs
```java
// Core Android Classes
import android.opengl.GLES10;
import android.opengl.GLES11;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.graphics.Bitmap;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
```

**Usage Context:**
- OpenGL ES graphics rendering pipeline
- Matrix mathematics for 3D transformations
- Bitmap handling for texture processing
- Android annotations for API level compatibility

### Google Guava Library
```java
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.primitives.UnsignedBytes;
```

**Usage Context:**
- Utility functions for null-safe operations
- Immutable collections for thread safety
- Primitive data type utilities
- Table data structures for complex lookups

### OkHttp Network Library
```java
import okhttp3.Request.Builder;
import okhttp3.Response;
import com.squareup.okhttp3.okhttp;
```

**Usage Context:**
- HTTP/HTTPS networking for capabilities (CAPS)
- Asset downloading from Second Life servers
- RESTful API communication

### AndroidX Support Libraries
```java
import androidx.appcompat.appcompat;
import androidx.constraintlayout.constraintlayout;
import androidx.test.ext.junit;
import androidx.test.espresso.espresso-core;
```

**Usage Context:**
- Modern Android UI components
- Layout management
- Testing frameworks

## Second Life Protocol Implementation

### LibreMetaverse-Compatible Types

#### Core Mathematical Types
```java
// Vector Mathematics
public class LLVector2 {
    public float x, y;
    // 2D vector operations
}

public class LLVector3 {
    public static final LLVector3 Zero = new LLVector3(0.0f, 0.0f, 0.0f);
    public float x, y, z;
    
    // Cross product for normal calculations
    public static LLVector3 cross(LLVector3 a, LLVector3 b);
    
    // Linear interpolation for animations
    public static LLVector3 lerp(LLVector3 start, LLVector3 end, float t);
    
    // Network data parsing
    public static LLVector3 parseFloatVec(ByteBuffer buffer);
    public static LLVector3 parseU16Vec(ByteBuffer buffer, ...);
    public static LLVector3 parseU8Vec(ByteBuffer buffer, ...);
}

public class LLVector3d {
    public double x, y, z;
    // Double precision for global coordinates
}

public class LLVector4 {
    public float x, y, z, w;
    // RGBA colors and quaternion components
}

public class LLQuaternion {
    public float x, y, z, w;
    
    // Rotation matrix conversion
    public float[] getMatrix();
    public float[] getInverseMatrix();
    
    // Euler angle conversion
    public void setFromEulerAngles(float roll, float pitch, float yaw);
}
```

#### UUID Management
```java
public class LLSDUUID extends LLSDNode {
    private UUID value;
    
    // String-based UUID construction
    public LLSDUUID(String uuidString);
    
    // Binary serialization
    public void serializeXML(XmlSerializer serializer);
    public void serializeBinary(DataOutputStream stream);
}

public class UUIDPool {
    // Memory-efficient UUID recycling
    // Reduces garbage collection pressure
}
```

### Protocol Message System

#### Base Message Infrastructure
```java
public class SLMessage {
    // Binary message parsing and serialization
    // Network byte order handling
    // Message type identification
}

public class SLAgentCircuit extends SLThreadingCircuit {
    // Main protocol handler implementing Second Life's circuit system
    // Handles reliable UDP communication
    // Message acknowledgment and retransmission
    
    public void sendMessage(SLMessage message);
    public void handleMessage(SLMessage message);
}

public class SLConnection {
    // Network connection management
    // Socket handling and error recovery
}
```

#### Message Categories

##### Avatar Management Messages
```java
AvatarAppearance.java          - Avatar visual appearance
AvatarAnimation.java           - Animation state changes  
AgentMovementComplete.java     - Movement acknowledgment
AgentPause.java               - Pause agent updates
AgentResume.java              - Resume agent updates
AgentFOV.java                 - Field of view changes
```

##### Asset Transfer Messages
```java
AssetUploadRequest.java        - Upload asset to server
AssetUploadComplete.java       - Upload completion notification
TransferRequest.java           - Request asset download
TransferPacket.java            - Asset data packet
TransferAbort.java             - Cancel transfer
TransferInfo.java              - Transfer metadata
```

##### Chat and Communication
```java
ChatFromViewer.java            - Send chat message
ChatFromSimulator.java         - Receive chat message
ImprovedInstantMessage.java    - Private messaging
StartLure.java                 - Teleport invitation
ScriptDialog.java              - Object script dialogs
AlertMessage.java              - System notifications
```

##### Object Management
```java
ObjectAdd.java                 - Create new object
ObjectUpdate.java              - Update object properties
ObjectUpdateCompressed.java    - Compressed object updates
ObjectUpdateCached.java        - Cached object updates
ImprovedTerseObjectUpdate.java - Efficient position/rotation updates
KillObject.java                - Remove object from scene
DeRezObject.java               - Return object to inventory
RequestMultipleObjects.java    - Bulk object requests
```

##### Inventory Management
```java
InventoryDescendents.java      - Folder contents
FetchInventory.java            - Request inventory items
MoveInventoryItem.java         - Move items between folders
UpdateInventoryItem.java       - Modify inventory item
CreateInventoryItem.java       - Create new inventory item
RemoveInventoryItem.java       - Delete inventory item
BulkUpdateInventory.java       - Batch inventory operations
```

##### Teleportation System
```java
TeleportRequest.java           - Request teleportation
TeleportLocationRequest.java   - Teleport to coordinates
TeleportProgress.java          - Teleport progress updates
TeleportFinish.java            - Teleport completion
TeleportFailed.java            - Teleport failure notification
TeleportLandmarkRequest.java   - Teleport to landmark
```

### Capabilities (CAPS) System
```java
public class SLCaps {
    // HTTP-based capabilities for modern Second Life features
    // RESTful API endpoints for asset upload/download
    // Event queue management
    
    public String getCapabilityURL(String capability);
    public void requestCapability(String capability, ...);
}

public class SLCapEventQueue {
    // Long-polling HTTP event queue
    // Real-time event delivery from simulator
    // Handles connection failures and reconnection
    
    public interface ICapsEventHandler {
        void handleCapsEvent(CapsEvent event);
    }
}
```

## Graphics and Rendering APIs

### OpenGL ES Rendering Pipeline

#### Core Rendering Context
```java
public class RenderContext {
    // Multi-version OpenGL ES support
    private boolean hasGL11;    // Legacy fixed-function pipeline
    private boolean hasGL20;    // Programmable shaders
    private boolean hasGL30;    // Advanced features
    
    // GPU capabilities detection
    private GPUDetection gpuDetection;
    
    // Shader program management
    private PrimProgram primProgram;
    private AvatarProgram avatarProgram;
    private RiggedMeshProgram riggedMeshProgram;
    private SkyProgram skyProgram;
    private WaterProgram waterProgram;
    private FXAAProgram fxaaProgram;
    
    // Resource management
    private GLResourceManager glResourceManager;
    private GLLoadQueue loadQueue;
}
```

#### Shader Program Architecture
```java
// Base shader program class
public abstract class ShaderProgram {
    protected int programId;
    protected Map<String, Integer> uniforms;
    protected Map<String, Integer> attributes;
    
    // Shader compilation and linking
    public void compileShader(String vertexSource, String fragmentSource);
    public void bindAttributes();
    public void getUniformLocations();
}

// Specialized shader programs
public class PrimProgram extends ShaderProgram {
    // Basic primitive rendering (cubes, spheres, cylinders)
    // Supports texturing and basic lighting
}

public class AvatarProgram extends ShaderProgram {
    // Avatar and character rendering
    // Skinning and bone animation support
}

public class RiggedMeshProgram extends ShaderProgram {
    // Rigged mesh rendering with bone deformation
    // Multi-bone influence per vertex
}

public class SkyProgram extends ShaderProgram {
    // Sky dome rendering with atmospheric scattering
    // Windlight system integration
}

public class WaterProgram extends ShaderProgram {
    // Water surface rendering
    // Reflection and refraction effects
}

public class FXAAProgram extends ShaderProgram {
    // Fast Approximate Anti-Aliasing post-processing
    // Single-pass anti-aliasing for mobile performance
}
```

#### Texture Management
```java
public class GLTexture {
    private int textureId;
    private int width, height;
    private int format;
    
    // Texture creation and management
    public void uploadTexture(Bitmap bitmap);
    public void uploadCompressedTexture(byte[] data);
    public void generateMipmaps();
}

public class TextureCache extends ResourceFileCache<UUID, TextureData> {
    // Texture asset caching with LRU eviction
    // Automatic texture compression and resizing
    // Memory pressure handling
    
    private class TextureDownloadRequest extends ResourceRequest<UUID, TextureData> {
        // Background texture downloading
        // JPEG2000 decompression using OpenJPEG
        // Automatic mipmap generation
    }
}

public class DrawableTextureParams {
    // Texture rendering parameters
    public TextureClass textureClass;
    public TexturePriority priority;
    public boolean enableMipmaps;
    public int maxTextureSize;
}
```

### 3D Scene Management

#### Spatial Organization
```java
public class SpatialTree {
    // Octree-based spatial partitioning
    // Efficient object queries and collision detection
    // Level-of-detail management
    
    public void insert(SpatialListEntry entry);
    public List<SpatialListEntry> query(SpatialBox bounds);
    public void update(SpatialListEntry entry);
}

public class SpatialObjectIndex {
    // Object indexing for fast lookups
    // UUID to spatial node mapping
    // Efficient object removal and updates
}

public class FrustrumPlanes {
    // View frustum culling implementation
    // Plane equation calculations
    // Object visibility testing
    
    public boolean isVisible(SpatialBox bounds);
    public void updateFromMatrix(float[] mvpMatrix);
}

public class DrawList {
    // Render queue management
    // Depth sorting for transparency
    // Batch optimization for similar objects
    
    public void addPrimitive(DrawListPrimEntry entry);
    public void addAvatar(DrawListAvatarEntry entry);
    public void addTerrain(DrawListTerrainEntry entry);
    public void sort();
    public void render(RenderContext context);
}
```

#### Asset Pipeline
```java
public class ResourceManager<K, V> {
    // Central resource coordination
    // Cache hierarchies and fallback strategies
    // Memory management and cleanup
    
    public V getResource(K key);
    public void preloadResource(K key);
    public void evictResource(K key);
}

public class MeshCache extends ResourceFileCache<UUID, MeshData> {
    // 3D mesh asset caching
    // Compressed mesh data handling
    // Level-of-detail mesh variants
    
    private class MeshDownloadRequest implements Runnable {
        // Background mesh downloading from CAPS
        // Mesh decompression and optimization
        // Automatic LOD generation
    }
}

public class AnimationCache extends ResourceFileCache<UUID, AnimationData> {
    // Animation keyframe data caching
    // Efficient animation blending
    // Loop detection and optimization
}

public class GeometryCache extends ResourceFileCache<UUID, GeometryData> {
    // Primitive geometry caching (cubes, spheres, etc.)
    // Procedural geometry generation
    // Vertex buffer optimization
}
```

### Avatar Animation System
```java
public class AvatarSkeleton {
    // Bone hierarchy management
    // Joint transformations and constraints
    // Inverse kinematics support
    
    private Map<SLSkeletonBoneID, Bone> bones;
    public void updateBone(SLSkeletonBoneID boneId, LLVector3 position, LLQuaternion rotation);
    public float[] getBoneMatrix(SLSkeletonBoneID boneId);
}

public class AnimationData {
    // Keyframe animation data
    // Bone transformation sequences
    // Animation blending weights
    
    public void applyToSkeleton(AvatarSkeleton skeleton, float time);
    public float getDuration();
    public boolean isLooping();
}

public class AvatarAnimationList {
    // Multi-animation blending
    // Priority-based animation mixing
    // Smooth transitions between animations
    
    public void addAnimation(UUID animationId, float weight);
    public void removeAnimation(UUID animationId);
    public void updateAnimations(float deltaTime);
}
```

## Asset Format Support

### Image Formats
```java
// JPEG2000 support via OpenJPEG
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;

// Standard formats
// - PNG (via Android Bitmap)
// - JPEG (via Android Bitmap)  
// - TGA (custom implementation)
```

### 3D Model Formats
```java
// Second Life mesh format (compressed binary)
public class MeshData {
    // LOD level support
    // UV mapping data
    // Vertex normals and tangents
    // Rigging information for animated meshes
}

// Primitive shapes (procedurally generated)
// - Cube, Sphere, Cylinder, Cone, Torus
// - Parametric surfaces with configurable detail
```

### Animation Formats
```java
// Second Life animation format (.anim)
public class AnimationData {
    // Keyframe interpolation (linear, bezier, hermite)
    // Bone transformation tracks
    // Priority and blending information
    // Loop points and constraints
}
```

### Audio Formats
```java
// Second Life audio assets
// - Ogg Vorbis (primary format)
// - WAV (uncompressed)
// - Sound effect management and 3D positioning
```

## Network Protocol Details

### UDP Circuit Protocol
```java
public class SLCircuit {
    // Reliable UDP implementation
    // Sequence number tracking
    // Acknowledgment and retransmission
    // Flow control and congestion avoidance
    
    protected void sendReliableMessage(SLMessage message);
    protected void handleAcknowledgment(int sequenceNumber);
    protected void retransmitMessage(SLMessage message);
}

public class SLThreadingCircuit extends SLCircuit {
    // Multi-threaded message processing
    // Separate threads for send/receive/processing
    // Lock-free message queues where possible
}
```

### HTTP Capabilities Protocol
```java
public class SLCaps {
    // RESTful API endpoints
    // JSON/LLSD data serialization
    // Asset upload/download via HTTP POST
    // Event queue via long-polling GET
    
    public void uploadAsset(AssetType type, byte[] data, ...);
    public byte[] downloadAsset(UUID assetId);
    public void pollEventQueue();
}
```

### LLSD Data Serialization
```java
// Linden Lab Structured Data format
public abstract class LLSDNode {
    // XML serialization
    public abstract void serializeXML(XmlSerializer serializer);
    
    // Binary serialization
    public abstract void serializeBinary(DataOutputStream stream);
    
    // JSON serialization (for CAPS)
    public abstract String toJSONString();
}

// Concrete LLSD types
public class LLSDMap extends LLSDNode;     // Key-value mappings
public class LLSDArray extends LLSDNode;   // Ordered lists
public class LLSDString extends LLSDNode;  // Text data
public class LLSDInteger extends LLSDNode; // Integer values
public class LLSDDouble extends LLSDNode;  // Floating point
public class LLSDBinary extends LLSDNode;  // Binary data
public class LLSDUUID extends LLSDNode;    // UUID values
```

## Performance Optimization APIs

### Memory Management
```java
public class UUIDPool {
    // UUID object recycling to reduce GC pressure
    // Thread-local pools for lockless access
}

public class DirectByteBuffer {
    // Native memory allocation
    // Direct access for OpenGL buffer objects
    // Reduced Java heap pressure
}
```

### GPU Resource Management
```java
public class GLResourceManager {
    // OpenGL object lifecycle management
    // Texture, buffer, and shader cleanup
    // Memory leak prevention
    
    public void registerTexture(GLTexture texture);
    public void unregisterTexture(GLTexture texture);
    public void cleanupOrphanedResources();
}

public class GLLoadQueue {
    // Asynchronous GPU resource loading
    // Background thread GL context management
    // Load balancing and priority queues
}
```

### Threading and Concurrency
```java
public class SynchronousExecutor implements Executor {
    // Main thread execution for GL operations
    // Ensures OpenGL calls from correct thread
}

// Background processing executors
// - Asset downloading threads
// - Mesh processing threads  
// - Animation calculation threads
// - Network I/O threads
```

## Development and Debugging APIs

### Debug and Profiling
```java
public class Debug {
    // Conditional logging based on build type
    // Performance timing measurements
    // Memory usage tracking
    
    public static void Printf(String format, Object... args);
    public static void Assert(boolean condition, String message);
}

public class TextureMemoryTracker {
    // GPU memory usage monitoring
    // Texture allocation tracking
    // Memory pressure detection
}
```

### Error Handling
```java
// OpenGL error checking
public class GLError {
    public static void checkGLError(String operation);
    public static String getGLErrorString(int error);
}

// Shader compilation error handling
public class ShaderCompileException extends Exception {
    // Detailed shader compilation error reporting
    // Line number and error type information
}
```

## Platform Integration APIs

### Android-Specific Features
```java
// OpenGL ES context management
import android.opengl.GLSurfaceView;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;

// File system access
import android.content.Context;
import android.os.Environment;

// Network connectivity
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

// Touch and input handling
import android.view.MotionEvent;
import android.view.GestureDetector;
```

### Performance Monitoring
```java
// Android performance APIs
import android.os.Debug;           // Memory profiling
import android.os.SystemClock;     // High-resolution timing
import android.app.ActivityManager; // System resource monitoring
```

## Summary

The Linkpoint project demonstrates comprehensive API usage across multiple domains:

1. **Graphics APIs**: Full OpenGL ES pipeline with modern shader support
2. **Network APIs**: Custom UDP protocol with HTTP capabilities  
3. **Asset APIs**: Multi-format asset loading and caching
4. **Math APIs**: Complete 3D mathematics library
5. **Platform APIs**: Deep Android integration
6. **Performance APIs**: Memory management and optimization

The codebase shows sophisticated understanding of mobile performance constraints while maintaining compatibility with Second Life's complex protocol requirements.
# C++ Reference Documentation (Firestorm)

## Overview

This document describes the C++ reference implementations from Firestorm Second Life viewer that were used to fix and verify the Linkpoint Kotlin implementation.

**Source**: Firestorm Viewer (latest version)  
**Location**: `/workspace/organized-repos/cpp-reference/firestorm/`  
**Purpose**: Production-tested reference for protocol and rendering implementations

---

## Copied Modules

### Module Structure

```
cpp-reference/firestorm/
├── llappearance/    # Avatar appearance system
├── llaudio/         # Audio system
├── llcharacter/     # Character/avatar system  
├── llcommon/        # Common utilities
├── llmessage/       # Message protocol
├── llprimitive/     # Primitive objects
└── llrender/        # Rendering system
```

---

## 1. llappearance - Avatar Appearance

**Files**: 35 files (`.h` and `.cpp`)

**Key Classes**:

### LLPolyMesh

**File**: `llpolymesh.h`, `llpolymesh.cpp`

**Purpose**: Polymesh with morph targets for avatar rendering

**Key Code** (Simplified):

```cpp
class LLPolyMesh {
public:
    // Member variables
    bool mHasWeights;              // Has bone weights
    U32 mNumJointNames;            // Number of joints
    std::string* mJointNames;      // Joint names array
    std::set<LLPolyMorphData*> mMorphData;  // Morph targets
    
    // Methods
    void applyMorph(LLPolyMorphData* morph, F32 weight);
    void applySkeleton(LLAvatarJoint* skeleton);
    
private:
    LLVector4a* mCoords;           // Vertex positions
    LLVector4a* mNormals;          // Vertex normals
    LLVector2* mTexCoords;         // Texture coordinates
    F32* mWeights;                 // Bone weights
    U32* mJointIndices;            // Joint indices
};

// Apply morph target to mesh
void LLPolyMesh::applyMorph(LLPolyMorphData* morph, F32 weight) {
    if (!morph) return;
    
    U32 num_verts = morph->getNumVerts();
    
    for (U32 i = 0; i < num_verts; i++) {
        U32 vert_index = morph->getVertexIndex(i);
        LLVector4a* coords = &mCoords[vert_index];
        LLVector4a* norms = &mNormals[vert_index];
        
        // Apply weighted delta
        coords->add(morph->getCoord(i) * weight);
        norms->add(morph->getNormal(i) * weight);
    }
}

// Apply skeleton transforms to skinned mesh
void LLPolyMesh::applySkeleton(LLAvatarJoint* skeleton) {
    if (!mHasWeights) return;
    
    for (U32 i = 0; i < mNumVertices; i++) {
        U32 joint_idx = mJointIndices[i];
        F32 weight = mWeights[i];
        
        LLAvatarJoint* joint = skeleton->findJoint(joint_idx);
        if (joint) {
            LLMatrix4 transform = joint->getWorldMatrix();
            mCoords[i] = transform * mCoords[i] * weight;
        }
    }
}
```

**Kotlin Equivalent**: `Linkpoint/src/main/kotlin/com/linkpoint/slproto/avatar/SLPolyMesh.kt`

**Migration Notes**:
- `bool mHasWeights` → `var hasWeights: Boolean`
- `U32* mJointIndices` → `var jointIndices: IntArray?`
- `F32* mWeights` → `var weightsBuffer: DirectByteBuffer?`
- `std::set<LLPolyMorphData*>` → `Array<SLPolyMorphData>`

---

### LLAvatarAppearance

**File**: `llavatarappearance.h`, `llavatarappearance.cpp`

**Purpose**: Main avatar appearance class

```cpp
class LLAvatarAppearance {
public:
    // Visual parameters
    typedef std::map<S32, LLVisualParam*> visual_param_map_t;
    visual_param_map_t mVisualParamMap;
    
    // Skeleton
    LLAvatarJoint* mRoot;
    std::vector<LLAvatarJoint*> mSkeleton;
    
    // Meshes
    std::vector<LLPolyMesh*> mMeshes;
    
    // Attachments
    typedef std::map<S32, LLViewerJointAttachment*> attachment_map_t;
    attachment_map_t mAttachmentPoints;
    
    // Methods
    void setVisualParamWeight(S32 param_id, F32 weight);
    void updateVisualParams();
    void addAttachment(S32 joint_id, LLViewerObject* object);
    
private:
    void applyMorphs();
    void updateSkeleton(F32 delta_time);
};

void LLAvatarAppearance::setVisualParamWeight(S32 param_id, F32 weight) {
    LLVisualParam* param = mVisualParamMap[param_id];
    if (param) {
        param->setWeight(weight);
        mVisualParamsChanged = true;
    }
}

void LLAvatarAppearance::updateVisualParams() {
    if (!mVisualParamsChanged) return;
    
    // Apply all morph targets
    for (auto& mesh : mMeshes) {
        mesh->resetVertices();
        
        for (auto& [id, param] : mVisualParamMap) {
            if (param->getWeight() > 0.0f) {
                LLPolyMorphData* morph = param->getMorphData();
                mesh->applyMorph(morph, param->getWeight());
            }
        }
    }
    
    mVisualParamsChanged = false;
}
```

**Kotlin Equivalent**: Avatar system spread across multiple files

**Migration Notes**:
- Visual params use `EnumMap<SLVisualParamID, Float>`
- Skeleton uses custom `AvatarSkeleton` class
- Attachments use `SLAttachmentPoint` enum

---

## 2. llcharacter - Character Animation

**Files**: 46 files

### LLMotion

**File**: `llmotion.h`, `llmotion.cpp`

**Purpose**: Animation motion class

```cpp
class LLMotion {
public:
    enum MotionBlendType {
        NORMAL_BLEND,
        ADDITIVE_BLEND
    };
    
    // Motion state
    bool mActive;
    F32 mActivationTimestamp;
    F32 mStopTimestamp;
    
    // Methods
    virtual bool onActivate() = 0;
    virtual bool onUpdate(F32 time, U8* joint_mask) = 0;
    virtual void onDeactivate() = 0;
    
    void addJointState(LLJointState* joint_state);
    
protected:
    std::list<LLJointState*> mJointStates;
};

class LLKeyframeMotion : public LLMotion {
public:
    struct JointMotion {
        std::vector<LLQuaternion> mRotationKeys;
        std::vector<LLVector3> mPositionKeys;
        std::vector<F32> mTimeKeys;
    };
    
    std::map<U32, JointMotion*> mJointMotions;
    
    bool onUpdate(F32 time, U8* joint_mask) override {
        for (auto& [joint_id, motion] : mJointMotions) {
            // Interpolate between keyframes
            LLQuaternion rotation = interpolateRotation(motion, time);
            LLVector3 position = interpolatePosition(motion, time);
            
            // Apply to joint
            LLJoint* joint = getSkeleton()->findJoint(joint_id);
            if (joint) {
                joint->setRotation(rotation);
                joint->setPosition(position);
            }
        }
        
        return true;
    }
};
```

**Kotlin Equivalent**: `Linkpoint/src/main/kotlin/com/linkpoint/animation/`

---

## 3. llcommon - Common Utilities

**Files**: 309 files

### LLSD

**File**: `llsd.h`, `llsd.cpp`

**Purpose**: Linden Lab Structured Data format

```cpp
class LLSD {
public:
    enum Type {
        TypeUndefined,
        TypeBoolean,
        TypeInteger,
        TypeReal,
        TypeString,
        TypeUUID,
        TypeDate,
        TypeURI,
        TypeBinary,
        TypeMap,
        TypeArray
    };
    
    // Constructors
    LLSD();
    LLSD(bool v);
    LLSD(S32 v);
    LLSD(F64 v);
    LLSD(const std::string& v);
    LLSD(const LLUUID& v);
    
    // Type checking
    Type type() const;
    bool isUndefined() const;
    bool isBoolean() const;
    bool isInteger() const;
    bool isReal() const;
    bool isString() const;
    bool isUUID() const;
    bool isMap() const;
    bool isArray() const;
    
    // Conversions
    bool asBoolean() const;
    S32 asInteger() const;
    F64 asReal() const;
    std::string asString() const;
    LLUUID asUUID() const;
    
    // Map operations
    LLSD& operator[](const std::string& key);
    const LLSD& operator[](const std::string& key) const;
    bool has(const std::string& key) const;
    
    // Array operations
    LLSD& operator[](size_t index);
    const LLSD& operator[](size_t index) const;
    size_t size() const;
    void append(const LLSD& value);
    
private:
    class Impl;
    std::shared_ptr<Impl> mImpl;
};

// XML Parser
class LLSDXMLParser {
public:
    bool parse(std::istream& input, LLSD& data);
    
private:
    void parseMap(XML_Parser parser, LLSD& data);
    void parseArray(XML_Parser parser, LLSD& data);
    void parseValue(XML_Parser parser, LLSD& data, const std::string& type);
};

// Binary Parser
class LLSDBinaryParser {
public:
    bool parse(std::istream& input, LLSD& data);
    
private:
    LLSD parseValue(std::istream& input);
    LLSD parseMap(std::istream& input);
    LLSD parseArray(std::istream& input);
};
```

**Kotlin Equivalent**: `Linkpoint/src/main/kotlin/com/linkpoint/slproto/llsd/LLSD.kt`

**Migration Notes**:
- C++ uses inheritance (polymorphic `Impl`)
- Kotlin uses sealed class (type-safe)
- C++ uses `operator[]` for map/array access
- Kotlin uses proper methods with null safety

---

### LLVector3

**File**: `llmath/v3math.h`, `v3math.cpp`

```cpp
class LLVector3 {
public:
    F32 mV[3];  // x, y, z
    
    // Constructors
    LLVector3() : mV{0, 0, 0} {}
    LLVector3(F32 x, F32 y, F32 z) : mV{x, y, z} {}
    
    // Operators
    LLVector3 operator+(const LLVector3& b) const {
        return LLVector3(mV[0] + b.mV[0], mV[1] + b.mV[1], mV[2] + b.mV[2]);
    }
    
    LLVector3 operator-(const LLVector3& b) const {
        return LLVector3(mV[0] - b.mV[0], mV[1] - b.mV[1], mV[2] - b.mV[2]);
    }
    
    LLVector3 operator*(F32 k) const {
        return LLVector3(mV[0] * k, mV[1] * k, mV[2] * k);
    }
    
    // Methods
    F32 dot(const LLVector3& b) const {
        return mV[0]*b.mV[0] + mV[1]*b.mV[1] + mV[2]*b.mV[2];
    }
    
    LLVector3 cross(const LLVector3& b) const {
        return LLVector3(
            mV[1]*b.mV[2] - mV[2]*b.mV[1],
            mV[2]*b.mV[0] - mV[0]*b.mV[2],
            mV[0]*b.mV[1] - mV[1]*b.mV[0]
        );
    }
    
    F32 length() const {
        return sqrt(lengthSquared());
    }
    
    F32 lengthSquared() const {
        return mV[0]*mV[0] + mV[1]*mV[1] + mV[2]*mV[2];
    }
    
    void normalize() {
        F32 len = length();
        if (len > 0.f) {
            mV[0] /= len;
            mV[1] /= len;
            mV[2] /= len;
        }
    }
};
```

**Kotlin Equivalent**: `Linkpoint/src/main/kotlin/com/linkpoint/slproto/types/LLVector3.kt`

**Migration Notes**:
- C++ uses array: `F32 mV[3]`
- Kotlin uses properties: `var x: Float, var y: Float, var z: Float`
- Both support operator overloading
- Kotlin version is more idiomatic with data class

---

## 4. llmessage - Message Protocol

**Files**: 182 files

### LLMessageSystem

**File**: `llmessagesystem.h`, `llmessagesystem.cpp`

**Purpose**: Main message sending/receiving system

```cpp
class LLMessageSystem {
public:
    // Sending messages
    void newMessage(const char* name);
    void nextBlock(const char* blockname);
    void addUUID(const char* varname, const LLUUID& v);
    void addU32(const char* varname, U32 v);
    void addString(const char* varname, const std::string& v);
    S32 sendReliable(const LLHost& host);
    
    // Receiving messages
    void setMessageHandler(const char* name, 
                          void (*handler)(LLMessageSystem*, void**),
                          void** user_data);
    
    LLUUID getUUID(const char* block, const char* var);
    U32 getU32(const char* block, const char* var);
    std::string getString(const char* block, const char* var);
    
private:
    LLCircuit* mCircuitInfo;
    std::map<U32, LLMessageTemplate*> mMessageTemplates;
};

// Example usage
void send_chat_message(LLMessageSystem* msg, const std::string& text) {
    msg->newMessage("ChatFromViewer");
    
    msg->nextBlock("AgentData");
    msg->addUUID("AgentID", gAgent.getID());
    msg->addUUID("SessionID", gAgent.getSessionID());
    
    msg->nextBlock("ChatData");
    msg->addString("Message", text);
    msg->addU8("Type", CHAT_TYPE_NORMAL);
    msg->addS32("Channel", 0);
    
    msg->sendReliable(gAgent.getRegionHost());
}

// Message handler
void process_chat_from_simulator(LLMessageSystem* msg, void**) {
    LLUUID from_id = msg->getUUID("ChatData", "SourceID");
    std::string from_name = msg->getString("ChatData", "FromName");
    std::string message = msg->getString("ChatData", "Message");
    
    // Display chat
    LLChat chat;
    chat.mFromID = from_id;
    chat.mFromName = from_name;
    chat.mText = message;
    LLFloaterChat::addChat(chat);
}
```

**Kotlin Equivalent**: `Linkpoint/src/main/kotlin/com/linkpoint/slproto/SLCircuitNew.kt`

**Migration Notes**:
- C++ uses builder pattern: `newMessage()` → `nextBlock()` → `add*()`
- Kotlin uses data classes: `SLMessage(name, blocks)`
- C++ uses function pointers for handlers
- Kotlin uses lambdas/callbacks

---

### LLCircuit

**File**: `llcircuit.h`, `llcircuit.cpp`

**Purpose**: UDP circuit management

```cpp
class LLCircuit {
public:
    // Circuit management
    void addCircuitData(const LLHost& host, U32 circuit_code);
    void removeCircuitData(const LLHost& host);
    
    // Packet reliability
    void sendPacket(const LLHost& host, const U8* data, size_t size,
                   bool reliable);
    void ackReliablePacket(const LLHost& host, U32 packet_id);
    
    // Ping/pong
    void updateWatchDog(const LLHost& host);
    F32 getPing(const LLHost& host);
    
private:
    struct CircuitData {
        U32 mCode;
        F32 mPingDelay;
        F32 mLastPing;
        std::queue<ReliablePacket*> mUnackedPackets;
        U32 mNextPacketID;
    };
    
    std::map<LLHost, CircuitData*> mCircuitData;
};

class ReliablePacket {
public:
    U32 mPacketID;
    U8* mData;
    size_t mSize;
    F32 mSentTime;
    U32 mRetries;
};

void LLCircuit::sendPacket(const LLHost& host, const U8* data, 
                           size_t size, bool reliable) {
    CircuitData* cd = mCircuitData[host];
    if (!cd) return;
    
    if (reliable) {
        // Store for retransmission
        ReliablePacket* packet = new ReliablePacket();
        packet->mPacketID = cd->mNextPacketID++;
        packet->mData = new U8[size];
        memcpy(packet->mData, data, size);
        packet->mSize = size;
        packet->mSentTime = getCurrentTime();
        packet->mRetries = 0;
        
        cd->mUnackedPackets.push(packet);
    }
    
    // Actually send via UDP
    mSocket->send(host, data, size);
}

void LLCircuit::ackReliablePacket(const LLHost& host, U32 packet_id) {
    CircuitData* cd = mCircuitData[host];
    if (!cd) return;
    
    // Remove from unacked queue
    auto& queue = cd->mUnackedPackets;
    while (!queue.empty()) {
        ReliablePacket* packet = queue.front();
        if (packet->mPacketID == packet_id) {
            delete[] packet->mData;
            delete packet;
            queue.pop();
            break;
        }
        queue.pop();
    }
}
```

**Kotlin Equivalent**: `Linkpoint/src/main/kotlin/com/linkpoint/slproto/SLCircuitNew.kt`

---

## 5. llprimitive - Primitive Objects

**Files**: 41 files

### LLVolume

**File**: `llvolume.h`, `llvolume.cpp`

**Purpose**: Primitive volume generation

```cpp
class LLVolume {
public:
    enum ProfileType {
        PROFILE_CIRCLE,
        PROFILE_SQUARE,
        PROFILE_TRIANGLE,
        PROFILE_CIRCLE_HALF
    };
    
    enum PathType {
        PATH_LINE,
        PATH_CIRCLE,
        PATH_CIRCLE2,
        PATH_TEST,
        PATH_FLEXIBLE
    };
    
    // Generation
    bool generate(const LLVolumeParams& params);
    
    // Access
    S32 getNumFaces() const { return mVolumeFaces.size(); }
    const LLVolumeFace& getVolumeFace(S32 f) const { return mVolumeFaces[f]; }
    
private:
    std::vector<LLVolumeFace> mVolumeFaces;
    LLVector3 mCenterOfFace;
    
    void generatePath();
    void generateProfile();
    void generateMesh();
};

struct LLVolumeFace {
    std::vector<LLVector4a> mPositions;
    std::vector<LLVector4a> mNormals;
    std::vector<LLVector2> mTexCoords;
    std::vector<U16> mIndices;
    LLVector4a mExtents[2];  // Min, Max
};

bool LLVolume::generate(const LLVolumeParams& params) {
    // Generate path (extrusion curve)
    LLPath path;
    path.generate(params.getPathParams());
    
    // Generate profile (cross-section)
    LLProfile profile;
    profile.generate(params.getProfileParams());
    
    // Sweep profile along path
    for (S32 i = 0; i < path.mPath.size(); i++) {
        const LLPathPt& pt = path.mPath[i];
        
        for (S32 j = 0; j < profile.mProfile.size(); j++) {
            const LLProfilePt& ppt = profile.mProfile[j];
            
            // Transform profile point by path point
            LLVector4a pos(
                ppt.mPos.mV[0] * pt.mScale.mV[0],
                ppt.mPos.mV[1] * pt.mScale.mV[1],
                0.0f,
                1.0f
            );
            
            // Rotate by path rotation
            pos = pt.mRot * pos;
            
            // Translate to path position
            pos += pt.mPos;
            
            mVolumeFaces[0].mPositions.push_back(pos);
        }
    }
    
    return true;
}
```

**Kotlin Equivalent**: Primitive generation in `FilamentPrimGeometry.kt`

---

## 6. llrender - Rendering

**Files**: 55 files

### LLRender

**File**: `llrender.h`, `llrender.cpp`

**Purpose**: OpenGL rendering abstraction

```cpp
class LLRender {
public:
    enum Mode {
        TRIANGLES,
        TRIANGLE_STRIP,
        TRIANGLE_FAN,
        LINES,
        LINE_STRIP,
        POINTS
    };
    
    // Rendering state
    void setColorMask(bool r, bool g, bool b, bool a);
    void setDepthMask(bool mask);
    void setBlendMode(U32 mode);
    
    // Drawing
    void begin(Mode mode);
    void end();
    
    void vertex3f(F32 x, F32 y, F32 z);
    void vertex3fv(const F32* v);
    void normal3f(F32 x, F32 y, F32 z);
    void texCoord2f(F32 s, F32 t);
    void color4f(F32 r, F32 g, F32 b, F32 a);
    
    // Transforms
    void pushMatrix();
    void popMatrix();
    void loadIdentity();
    void translatef(F32 x, F32 y, F32 z);
    void scalef(F32 x, F32 y, F32 z);
    void rotatef(F32 angle, F32 x, F32 y, F32 z);
    void multMatrix(const F32* mat);
    
private:
    std::stack<LLMatrix4> mMatrixStack;
    std::vector<LLVector3> mVertices;
    std::vector<LLVector3> mNormals;
    std::vector<LLVector2> mTexCoords;
    std::vector<LLColor4> mColors;
};

// Example usage
void render_box() {
    gGL.begin(LLRender::TRIANGLES);
    
    // Front face
    gGL.normal3f(0, 0, 1);
    gGL.texCoord2f(0, 0); gGL.vertex3f(-1, -1, 1);
    gGL.texCoord2f(1, 0); gGL.vertex3f( 1, -1, 1);
    gGL.texCoord2f(1, 1); gGL.vertex3f( 1,  1, 1);
    
    gGL.texCoord2f(0, 0); gGL.vertex3f(-1, -1, 1);
    gGL.texCoord2f(1, 1); gGL.vertex3f( 1,  1, 1);
    gGL.texCoord2f(0, 1); gGL.vertex3f(-1,  1, 1);
    
    // ... other faces
    
    gGL.end();
}
```

**Kotlin Equivalent**: Linkpoint uses Filament instead of OpenGL directly

**Migration Notes**:
- Firestorm uses immediate mode OpenGL (legacy)
- Linkpoint uses Filament (modern, retained mode)
- Filament provides better performance on mobile
- Filament has built-in PBR support

---

## Key Differences: C++ vs Kotlin

### Memory Management

**C++**:
```cpp
LLPolyMesh* mesh = new LLPolyMesh();
// ... use mesh
delete mesh;  // Manual cleanup
```

**Kotlin**:
```kotlin
val mesh = SLPolyMesh()
// ... use mesh
// Automatic garbage collection
```

### Null Safety

**C++**:
```cpp
LLPolyMesh* mesh = getMesh();
if (mesh != nullptr) {  // Must check manually
    mesh->apply();
}
```

**Kotlin**:
```kotlin
val mesh: SLPolyMesh? = getMesh()
mesh?.apply()  // Safe call operator
```

### Collections

**C++**:
```cpp
std::vector<LLPolyMesh*> meshes;
meshes.push_back(mesh);
for (auto it = meshes.begin(); it != meshes.end(); ++it) {
    (*it)->render();
}
```

**Kotlin**:
```kotlin
val meshes = mutableListOf<SLPolyMesh>()
meshes.add(mesh)
meshes.forEach { it.render() }
```

### Error Handling

**C++**:
```cpp
try {
    mesh->load(filename);
} catch (std::exception& e) {
    LOG_ERROR("Failed: " << e.what());
}
```

**Kotlin**:
```kotlin
try {
    mesh.load(filename)
} catch (e: Exception) {
    Log.e(TAG, "Failed: ${e.message}")
}
```

---

## Summary

The C++ reference code from Firestorm provides:

✅ **Production-tested** - Used by thousands of users  
✅ **Complete implementation** - All protocol features  
✅ **Well-documented** - Clear class hierarchy  
✅ **Performance-optimized** - Years of refinement  

This reference was essential for:

✅ **Fixing Kotlin syntax** - Understanding proper data structures  
✅ **Verifying behavior** - Matching protocol implementation  
✅ **Understanding algorithms** - Morph targets, skinning, etc.  
✅ **Protocol accuracy** - Ensuring compatibility  

For Kotlin implementation details, see `KOTLIN_GUIDE.md`  
For migration strategies, see `MIGRATION.md`

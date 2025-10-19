# Modern Visual Services and Kotlin Library

This section documents the new modern visual services and Kotlin library additions to the LLSD Java project.

## Modern Visual Services (Java)

### PBR Materials System

The `PBRMaterial` class implements a comprehensive Physically Based Rendering material system:

```java
import lindenlab.llsd.viewer.secondlife.engine.rendering.*;

// Create a PBR material
PBRMaterial material = new PBRMaterial();
material.setBaseColor(new Vector3(0.8, 0.2, 0.1)); // Red base color
material.setMetallic(0.0);                          // Non-metallic
material.setRoughness(0.3);                         // Somewhat smooth
material.setEmissive(new Vector3(0.1, 0.0, 0.0));   // Slight red glow
material.setEmissiveStrength(2.0);

// Set textures
UUID baseColorTexture = UUID.randomUUID();
material.setBaseColorTexture(baseColorTexture);
material.setNormalTexture(normalMapUUID);

// Configure texture transform
TextureTransform uvTransform = new TextureTransform(0.0, 0.0, 2.0, 2.0); // Scale 2x
material.setBaseColorTransform(uvTransform);

// Export to LLSD for Second Life compatibility
Map<String, Object> materialLLSD = material.toLLSD();
```

### Windlight Environmental System

The `WindlightEnvironment` class provides atmospheric and environmental rendering:

```java
// Create Windlight environment
WindlightEnvironment environment = new WindlightEnvironment();

// Configure sky settings
WindlightEnvironment.SkySettings sky = environment.getSkySettings();
sky.setHorizonColor(new Vector3(0.25, 0.25, 0.32));
sky.setSunColor(new Vector3(0.74, 0.65, 0.39));
sky.setHazeDensity(0.7);

// Configure water settings  
WindlightEnvironment.WaterSettings water = environment.getWaterSettings();
water.setWaterColor(new Vector3(0.04, 0.15, 0.20));
water.setWaterFogDensity(10.0);

// Set time of day (0.0 = midnight, 0.5 = noon)
environment.setDayTime(0.75); // Evening

// Get current lighting
Vector3 sunDirection = environment.getCurrentLightDirection();
Vector3 lightColor = environment.getCurrentLightColor();
Vector3 ambientColor = environment.getCurrentAmbientColor();
```

### Advanced Particle System

The `ParticleSystem` class provides modern particle effects:

```java
// Create particle system
ParticleSystem particles = new ParticleSystem("Fire Effect");

// Configure emitter
ParticleSystem.ParticleEmitter emitter = particles.getEmitter();
emitter.setType(ParticleSystem.ParticleEmitter.EmitterType.CONE);
emitter.setSize(new Vector3(1.0, 1.0, 2.0)); // Cone dimensions
emitter.setEmissionRate(50.0); // 50 particles per second

// Configure particle template
ParticleSystem.ParticleProperties template = emitter.getParticleTemplate();
template.life = 3.0;
template.initialSpeed = 2.0;
template.color = new Vector3(1.0, 0.3, 0.1); // Orange
template.size = 0.5;

// Add variation
ParticleSystem.RandomVariation variation = emitter.getVariation();
variation.lifeVariation = 0.5;
variation.speedVariation = 1.0;
variation.colorVariation = new Vector3(0.2, 0.2, 0.1);

// Set global properties
particles.setGlobalAcceleration(new Vector3(0, 0, -9.81)); // Gravity
particles.setRenderMode(ParticleSystem.RenderMode.BILLBOARD);
particles.setBlendMode(ParticleSystem.BlendMode.ADDITIVE);

// Update particle system
particles.update(deltaTime);
```

### Modern Renderer

The `ModernRenderer` class provides OpenGL ES 3.0+ compatible rendering:

```java
// Create and initialize renderer
ModernRenderer renderer = new ModernRenderer();
renderer.initialize();

// Configure rendering settings
ModernRenderer.RenderSettings settings = renderer.getSettings();
settings.enablePBR = true;
settings.enableHDR = true;
settings.enableShadows = true;
settings.shadowTechnique = ModernRenderer.RenderSettings.ShadowTechnique.CASCADE_SHADOW_MAPPING;
settings.enableSSAO = true;
settings.enableBloom = true;

// Add objects to render
ModernRenderer.RenderObject obj = new ModernRenderer.RenderObject(
    objectId, sceneNode, mesh, pbrMaterial);
renderer.addRenderObject(obj);

// Add lighting
ModernRenderer.LightSource sunLight = new ModernRenderer.LightSource(
    ModernRenderer.LightSource.LightType.DIRECTIONAL);
sunLight.setDirection(new Vector3(0.3, -0.7, -0.6));
sunLight.setColor(new Vector3(1.0, 0.9, 0.8));
sunLight.setIntensity(3.0);
sunLight.setCastShadows(true);
renderer.addLightSource(sunLight);

// Set environment
renderer.setEnvironment(windlightEnvironment);

// Render frame
renderer.renderFrame(deltaTime);

// Get performance stats
ModernRenderer.RenderStats stats = renderer.getStats();
System.out.printf("FPS: %.1f, Draw Calls: %d, Triangles: %d\n", 
                  stats.averageFPS, stats.drawCalls, stats.trianglesRendered);
```

## Kotlin LLSD Library

### Type-Safe LLSD Values

The Kotlin library provides a modern, type-safe interface to LLSD:

```kotlin
import lindenlab.llsd.kotlin.*

// Create LLSD values
val name = LLSDValue.String("Alice")
val age = LLSDValue.Integer(30)
val isActive = LLSDValue.Boolean(true)
val uuid = LLSDValue.UUID(UUID.randomUUID())
val timestamp = LLSDValue.Date(Instant.now())

// Safe type extraction
val nameStr = name.asString("Unknown")     // "Alice"
val ageInt = age.asInt(0)                  // 30  
val activeFlag = isActive.asBoolean(false) // true
val invalidAge = name.asInt(-1)            // -1 (default, safe conversion)
```

### DSL Builders

Create LLSD structures using Kotlin DSL:

```kotlin
// Create complex LLSD structure using DSL
val userData = llsdMap {
    "user" to llsdMap {
        "name" to "Alice Smith"
        "age" to 30
        "email" to "alice@example.com"
        "preferences" to llsdMap {
            "theme" to "dark"
            "notifications" to true
            "language" to "en"
        }
    }
    "scores" to llsdArray {
        +95
        +87
        +92
        +88
    }
    "metadata" to llsdMap {
        "created" to Instant.now()
        "version" to 2
        "uuid" to UUID.randomUUID()
    }
}

// Safe navigation
val theme = userData["user", "preferences", "theme"].asString("light")
val firstScore = userData["scores"].asArray()[0].asInt(0)
val userAge = userData.path("user", "age").asInt(0)
```

### Modern Serialization

Kotlin-native serialization with multiple formats:

```kotlin
import lindenlab.llsd.kotlin.serialization.*

// Serialize to different formats
val json = userData.toJson(prettyPrint = true)
val notation = userData.toNotation()
val xml = userData.toXml()
val binary = userData.toBinary() // Base64-encoded binary

// Parse from different formats
val fromJson = """{"name":"Bob","age":25}""".parseLLSDFromJson()
val fromNotation = "{'name':'Bob','age':i25}".parseLLSDFromNotation()

// Auto-detect format and parse
val autoDetected = someString.parseLLSD()

// Stream-based serialization
val serializer = LLSDKotlinSerializer()
val outputStream = FileOutputStream("data.llsd")
serializer.serialize(userData, outputStream, LLSDKotlinSerializer.SerializationOptions(
    format = LLSDKotlinSerializer.Format.BINARY
))
```

### Type-Safe Schema Validation

Create schemas for LLSD validation:

```kotlin
// Define data class
data class User(val name: String, val age: Int, val email: String)

// Create schema
class UserSchema : LLSDSchema<User> {
    override fun validate(value: LLSDValue.Map): User {
        val name = value["name"].asString()
        val age = value["age"].asInt()
        val email = value["email"].asString()
        
        require(name.isNotBlank()) { "Name cannot be blank" }
        require(age >= 0) { "Age must be non-negative" }
        require(email.contains("@")) { "Email must contain @" }
        
        return User(name, age, email)
    }
}

// Use schema for validation
val userSchema = UserSchema()
val result = buildValidatedLLSD(userSchema) {
    "name" to "Alice"
    "age" to 30
    "email" to "alice@example.com"
}

when {
    result.isSuccess -> {
        val validatedMap = result.getOrThrow()
        println("Valid user data: $validatedMap")
    }
    result.isFailure -> {
        println("Validation failed: ${result.exceptionOrNull()?.message}")
    }
}
```

### Utility Functions

Rich set of utility functions for LLSD manipulation:

```kotlin
// Convert various types to LLSD
val fromList = llsdOf(listOf(1, 2, 3, 4, 5))
val fromMap = llsdOf(mapOf("key" to "value", "number" to 42))
val fromNull = llsdOf(null) // LLSDValue.Undefined

// Deep operations
val original = llsdMap { "data" to llsdArray { +1; +2; +3 } }
val copy = original.deepCopy()
val isEqual = original.deepEquals(copy) // true

// Pretty printing
val prettyString = userData.toPrettyString()
println(prettyString)
// Output:
// {
//   "user": {
//     "name": "Alice Smith",
//     "age": 30,
//     ...
//   },
//   "scores": [
//     95,
//     87,
//     92,
//     88
//   ]
// }

// Java interoperability
val javaLLSD = userData.toJavaLLSD()      // Convert to Java LLSD
val backToKotlin = javaLLSD.toKotlinLLSD() // Convert back to Kotlin
```

## Integration Example

Combining visual services with LLSD data:

```java
// Java side - create visual scene with LLSD configuration
public class SceneBuilder {
    public void buildScene(LLSD sceneConfig) {
        LLSDValue kotlinConfig = sceneConfig.toKotlinLLSD();
        
        // Extract configuration using Kotlin DSL
        String sceneName = kotlinConfig.path("scene", "name").asString("Untitled");
        Vector3 sunDirection = extractVector3(kotlinConfig.path("lighting", "sun", "direction"));
        
        // Create Windlight environment
        WindlightEnvironment environment = new WindlightEnvironment();
        environment.setDayTime(kotlinConfig.path("lighting", "time").asDouble(0.5));
        
        // Create PBR materials from config
        LLSDValue.Array materials = kotlinConfig.path("materials").asArray();
        materials.forEach(materialData -> {
            PBRMaterial material = PBRMaterial.fromLLSD(materialData.asMap().toLLSD());
            // Use material...
        });
        
        // Setup renderer
        ModernRenderer renderer = new ModernRenderer();
        renderer.setEnvironment(environment);
        renderer.initialize();
    }
    
    private Vector3 extractVector3(LLSDValue value) {
        LLSDValue.Array array = value.asArray();
        return new Vector3(
            array.get(0).asDouble(0.0),
            array.get(1).asDouble(0.0), 
            array.get(2).asDouble(0.0)
        );
    }
}
```

This implementation provides a comprehensive foundation for modern 3D rendering and data handling in Second Life and Firestorm-compatible applications, with both traditional Java APIs and modern Kotlin DSL support.
/*
 * Particle System - Java implementation of particle effects for Second Life
 *
 * Based on Second Life and Firestorm particle systems
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.engine.rendering;

import lindenlab.llsd.viewer.secondlife.engine.Vector3;
import lindenlab.llsd.viewer.secondlife.engine.Quaternion;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.UUID;

/**
 * Advanced particle effects system for Second Life.
 * 
 * <p>This class provides a comprehensive particle system supporting various
 * effect types, emitters, and modern GPU-accelerated particle rendering
 * compatible with Second Life and enhanced for modern standards.</p>
 * 
 * @since 1.0
 */
public class ParticleSystem {
    
    private final UUID systemId;
    private String name;
    private boolean enabled;
    private double systemAge;
    private double maxAge;
    
    // Emitter properties
    private ParticleEmitter emitter;
    private Vector3 emitterPosition;
    private Quaternion emitterRotation;
    private Vector3 emitterScale;
    
    // Global system properties
    private Vector3 globalAcceleration; // Gravity, wind, etc.
    private double globalDamping;
    private BoundingBox boundingBox;
    private boolean useLocalCoordinates;
    
    // Rendering properties
    private RenderMode renderMode;
    private BlendMode blendMode;
    private UUID texture;
    private boolean depthWrite;
    private boolean depthTest;
    private SortMode sortMode;
    
    // Performance settings
    private int maxParticles;
    private boolean useBatching;
    private boolean useGPUSimulation;
    private int simulationQuality; // 0-3, higher = more accurate
    
    // Active particles
    private final Queue<Particle> activeParticles;
    private final Queue<Particle> deadParticles;
    
    /**
     * Particle emitter types and properties.
     */
    public static class ParticleEmitter {
        private EmitterType type;
        private EmitterShape shape;
        private Vector3 size; // Box size, sphere radius, etc.
        
        // Emission properties
        private double emissionRate; // Particles per second
        private double burstCount; // Particles per burst
        private double burstInterval; // Seconds between bursts
        private boolean continuous; // Continuous or burst emission
        
        // Particle initial properties
        private ParticleProperties particleTemplate;
        private RandomVariation variation;
        
        public enum EmitterType {
            POINT,
            BOX,
            SPHERE,
            CONE,
            CYLINDER,
            MESH,
            TEXTURE // Emit from texture brightness
        }
        
        public enum EmitterShape {
            VOLUME,    // Emit from within volume
            SURFACE,   // Emit from surface
            EDGE       // Emit from edges
        }
        
        public ParticleEmitter(EmitterType type) {
            this.type = type;
            this.shape = EmitterShape.VOLUME;
            this.size = new Vector3(1.0, 1.0, 1.0);
            this.emissionRate = 10.0;
            this.burstCount = 1.0;
            this.burstInterval = 1.0;
            this.continuous = true;
            this.particleTemplate = new ParticleProperties();
            this.variation = new RandomVariation();
        }
        
        // Getters and setters
        public EmitterType getType() { return type; }
        public void setType(EmitterType type) { this.type = type; }
        
        public EmitterShape getShape() { return shape; }
        public void setShape(EmitterShape shape) { this.shape = shape; }
        
        public Vector3 getSize() { return size; }
        public void setSize(Vector3 size) { this.size = size; }
        
        public double getEmissionRate() { return emissionRate; }
        public void setEmissionRate(double emissionRate) { this.emissionRate = Math.max(0.0, emissionRate); }
        
        public double getBurstCount() { return burstCount; }
        public void setBurstCount(double burstCount) { this.burstCount = Math.max(1.0, burstCount); }
        
        public double getBurstInterval() { return burstInterval; }
        public void setBurstInterval(double burstInterval) { this.burstInterval = Math.max(0.01, burstInterval); }
        
        public boolean isContinuous() { return continuous; }
        public void setContinuous(boolean continuous) { this.continuous = continuous; }
        
        public ParticleProperties getParticleTemplate() { return particleTemplate; }
        public void setParticleTemplate(ParticleProperties particleTemplate) { this.particleTemplate = particleTemplate; }
        
        public RandomVariation getVariation() { return variation; }
        public void setVariation(RandomVariation variation) { this.variation = variation; }
        
        /**
         * Generate random position within emitter volume.
         */
        public Vector3 generatePosition(Random random) {
            switch (type) {
                case POINT:
                    return Vector3.ZERO;
                    
                case BOX:
                    if (shape == EmitterShape.VOLUME) {
                        return new Vector3(
                            (random.nextDouble() - 0.5) * size.x,
                            (random.nextDouble() - 0.5) * size.y,
                            (random.nextDouble() - 0.5) * size.z
                        );
                    } else if (shape == EmitterShape.SURFACE) {
                        // Generate on box surface
                        int face = random.nextInt(6);
                        double u = random.nextDouble() - 0.5;
                        double v = random.nextDouble() - 0.5;
                        
                        switch (face) {
                            case 0: return new Vector3(-size.x/2, u * size.y, v * size.z); // -X face
                            case 1: return new Vector3( size.x/2, u * size.y, v * size.z); // +X face
                            case 2: return new Vector3(u * size.x, -size.y/2, v * size.z); // -Y face
                            case 3: return new Vector3(u * size.x,  size.y/2, v * size.z); // +Y face
                            case 4: return new Vector3(u * size.x, v * size.y, -size.z/2); // -Z face
                            case 5: return new Vector3(u * size.x, v * size.y,  size.z/2); // +Z face
                        }
                    }
                    break;
                    
                case SPHERE:
                    double radius = size.x; // Use X component as radius
                    if (shape == EmitterShape.VOLUME) {
                        // Generate within sphere volume
                        double r = radius * Math.cbrt(random.nextDouble());
                        double theta = random.nextDouble() * 2 * Math.PI;
                        double phi = Math.acos(2 * random.nextDouble() - 1);
                        
                        return new Vector3(
                            r * Math.sin(phi) * Math.cos(theta),
                            r * Math.sin(phi) * Math.sin(theta),
                            r * Math.cos(phi)
                        );
                    } else if (shape == EmitterShape.SURFACE) {
                        // Generate on sphere surface
                        double theta = random.nextDouble() * 2 * Math.PI;
                        double phi = Math.acos(2 * random.nextDouble() - 1);
                        
                        return new Vector3(
                            radius * Math.sin(phi) * Math.cos(theta),
                            radius * Math.sin(phi) * Math.sin(theta),
                            radius * Math.cos(phi)
                        );
                    }
                    break;
                    
                case CONE:
                    // Generate within cone
                    double height = size.z;
                    double baseRadius = size.x;
                    double h = random.nextDouble() * height;
                    double r = baseRadius * (height - h) / height; // Cone taper
                    
                    if (shape == EmitterShape.VOLUME) {
                        r *= Math.sqrt(random.nextDouble()); // Uniform distribution in disk
                    }
                    
                    double angle = random.nextDouble() * 2 * Math.PI;
                    
                    return new Vector3(
                        r * Math.cos(angle),
                        r * Math.sin(angle),
                        h - height / 2 // Center at origin
                    );
                    
                default:
                    return Vector3.ZERO;
            }
            return Vector3.ZERO;
        }
        
        /**
         * Generate initial velocity based on emitter type.
         */
        public Vector3 generateVelocity(Vector3 position, Random random) {
            Vector3 direction = Vector3.ZERO;
            
            switch (type) {
                case POINT:
                    // Random direction
                    double theta = random.nextDouble() * 2 * Math.PI;
                    double phi = Math.acos(2 * random.nextDouble() - 1);
                    direction = new Vector3(
                        Math.sin(phi) * Math.cos(theta),
                        Math.sin(phi) * Math.sin(theta),
                        Math.cos(phi)
                    );
                    break;
                    
                case SPHERE:
                    // Radial direction from center
                    direction = position.normalize();
                    break;
                    
                case CONE:
                    // Upward cone direction
                    direction = new Vector3(0, 0, 1);
                    // Add some spread based on cone angle
                    double spread = Math.atan2(size.x, size.z); // Cone half-angle
                    double spreadAngle = (random.nextDouble() - 0.5) * spread;
                    double spreadDir = random.nextDouble() * 2 * Math.PI;
                    
                    Vector3 spreadVec = new Vector3(
                        Math.sin(spreadAngle) * Math.cos(spreadDir),
                        Math.sin(spreadAngle) * Math.sin(spreadDir),
                        Math.cos(spreadAngle)
                    );
                    direction = direction.add(spreadVec).normalize();
                    break;
                    
                default:
                    direction = new Vector3(0, 0, 1); // Default upward
                    break;
            }
            
            double speed = particleTemplate.initialSpeed;
            if (variation.speedVariation > 0) {
                speed += (random.nextGaussian()) * variation.speedVariation;
                speed = Math.max(0, speed);
            }
            
            return direction.multiply(speed);
        }
    }
    
    /**
     * Individual particle properties.
     */
    public static class ParticleProperties {
        public double life; // Total lifetime in seconds
        public double age; // Current age
        
        public Vector3 position;
        public Vector3 velocity;
        public Vector3 acceleration;
        
        public Vector3 color;
        public double alpha;
        public double size;
        public double rotation; // In radians
        public double angularVelocity;
        
        public double initialSpeed;
        public double mass;
        public double drag; // Air resistance
        public double bounce; // Collision restitution
        
        // Animation properties
        public int textureAnimFrames;
        public int currentFrame;
        public double frameTime;
        public double frameRate;
        
        public ParticleProperties() {
            this.life = 5.0;
            this.age = 0.0;
            
            this.position = Vector3.ZERO;
            this.velocity = Vector3.ZERO;
            this.acceleration = Vector3.ZERO;
            
            this.color = new Vector3(1.0, 1.0, 1.0);
            this.alpha = 1.0;
            this.size = 1.0;
            this.rotation = 0.0;
            this.angularVelocity = 0.0;
            
            this.initialSpeed = 1.0;
            this.mass = 1.0;
            this.drag = 0.0;
            this.bounce = 0.0;
            
            this.textureAnimFrames = 1;
            this.currentFrame = 0;
            this.frameTime = 0.0;
            this.frameRate = 1.0;
        }
        
        public boolean isAlive() {
            return age < life;
        }
        
        public double getNormalizedAge() {
            return life > 0 ? age / life : 1.0;
        }
    }
    
    /**
     * Random variation settings for particle properties.
     */
    public static class RandomVariation {
        public double lifeVariation;
        public double speedVariation;
        public Vector3 colorVariation;
        public double alphaVariation;
        public double sizeVariation;
        public double rotationVariation;
        public double angularVelocityVariation;
        
        public RandomVariation() {
            this.lifeVariation = 0.0;
            this.speedVariation = 0.0;
            this.colorVariation = Vector3.ZERO;
            this.alphaVariation = 0.0;
            this.sizeVariation = 0.0;
            this.rotationVariation = 0.0;
            this.angularVelocityVariation = 0.0;
        }
    }
    
    /**
     * Individual particle instance.
     */
    public static class Particle {
        public ParticleProperties properties;
        
        // Animation curves over lifetime
        public AnimationCurve sizeCurve;
        public AnimationCurve colorCurve;
        public AnimationCurve alphaCurve;
        
        public Particle() {
            this.properties = new ParticleProperties();
        }
        
        public Particle(ParticleProperties template) {
            this.properties = new ParticleProperties();
            copyProperties(template, this.properties);
        }
        
        private void copyProperties(ParticleProperties from, ParticleProperties to) {
            to.life = from.life;
            to.position = from.position;
            to.velocity = from.velocity;
            to.acceleration = from.acceleration;
            to.color = from.color;
            to.alpha = from.alpha;
            to.size = from.size;
            to.rotation = from.rotation;
            to.angularVelocity = from.angularVelocity;
            to.initialSpeed = from.initialSpeed;
            to.mass = from.mass;
            to.drag = from.drag;
            to.bounce = from.bounce;
            to.textureAnimFrames = from.textureAnimFrames;
            to.frameRate = from.frameRate;
        }
        
        public void update(double deltaTime, Vector3 globalAcceleration, double globalDamping) {
            if (!properties.isAlive()) return;
            
            properties.age += deltaTime;
            
            // Physics update
            Vector3 totalAcceleration = properties.acceleration.add(globalAcceleration);
            
            // Apply drag
            if (properties.drag > 0) {
                double dragForce = properties.velocity.magnitude() * properties.drag;
                Vector3 dragAccel = properties.velocity.normalize().multiply(-dragForce / properties.mass);
                totalAcceleration = totalAcceleration.add(dragAccel);
            }
            
            // Update velocity and position (Verlet integration)
            properties.velocity = properties.velocity.add(totalAcceleration.multiply(deltaTime));
            properties.velocity = properties.velocity.multiply(Math.pow(globalDamping, deltaTime));
            properties.position = properties.position.add(properties.velocity.multiply(deltaTime));
            
            // Update rotation
            properties.rotation += properties.angularVelocity * deltaTime;
            
            // Update animation
            if (properties.textureAnimFrames > 1) {
                properties.frameTime += deltaTime;
                if (properties.frameTime >= 1.0 / properties.frameRate) {
                    properties.currentFrame = (properties.currentFrame + 1) % properties.textureAnimFrames;
                    properties.frameTime = 0.0;
                }
            }
            
            // Apply animation curves
            double t = properties.getNormalizedAge();
            if (sizeCurve != null) {
                properties.size *= sizeCurve.evaluate(t);
            }
            if (alphaCurve != null) {
                properties.alpha *= alphaCurve.evaluate(t);
            }
            if (colorCurve != null) {
                // Modulate color - this is simplified, real implementation would handle RGB separately
                double colorMod = colorCurve.evaluate(t);
                properties.color = properties.color.multiply(colorMod);
            }
        }
    }
    
    /**
     * Simple animation curve for property interpolation over particle lifetime.
     */
    public static class AnimationCurve {
        private final List<KeyFrame> keyFrames;
        
        public static class KeyFrame {
            public double time; // 0.0 to 1.0 (normalized lifetime)
            public double value;
            public InterpolationMode interpolation;
            
            public enum InterpolationMode {
                LINEAR,
                CUBIC,
                STEP
            }
            
            public KeyFrame(double time, double value) {
                this(time, value, InterpolationMode.LINEAR);
            }
            
            public KeyFrame(double time, double value, InterpolationMode interpolation) {
                this.time = time;
                this.value = value;
                this.interpolation = interpolation;
            }
        }
        
        public AnimationCurve() {
            this.keyFrames = new ArrayList<>();
        }
        
        public void addKeyFrame(double time, double value) {
            addKeyFrame(new KeyFrame(time, value));
        }
        
        public void addKeyFrame(KeyFrame keyFrame) {
            keyFrames.add(keyFrame);
            keyFrames.sort(Comparator.comparing(k -> k.time));
        }
        
        public double evaluate(double time) {
            if (keyFrames.isEmpty()) return 1.0;
            if (keyFrames.size() == 1) return keyFrames.get(0).value;
            
            time = Math.max(0.0, Math.min(1.0, time));
            
            // Find surrounding keyframes
            KeyFrame before = keyFrames.get(0);
            KeyFrame after = keyFrames.get(keyFrames.size() - 1);
            
            for (int i = 0; i < keyFrames.size() - 1; i++) {
                if (time >= keyFrames.get(i).time && time <= keyFrames.get(i + 1).time) {
                    before = keyFrames.get(i);
                    after = keyFrames.get(i + 1);
                    break;
                }
            }
            
            if (before == after) return before.value;
            
            // Interpolate
            double t = (time - before.time) / (after.time - before.time);
            
            switch (before.interpolation) {
                case LINEAR:
                    return before.value + (after.value - before.value) * t;
                    
                case CUBIC:
                    // Simplified cubic interpolation
                    double t2 = t * t;
                    double t3 = t2 * t;
                    return before.value * (2*t3 - 3*t2 + 1) + after.value * (3*t2 - 2*t3);
                    
                case STEP:
                    return before.value;
                    
                default:
                    return before.value + (after.value - before.value) * t;
            }
        }
    }
    
    /**
     * Rendering and blend modes.
     */
    public enum RenderMode {
        BILLBOARD,          // Always face camera
        VELOCITY_ALIGNED,   // Align with velocity direction  
        HORIZONTAL,         // Horizontal billboard
        VERTICAL,           // Vertical billboard
        MESH,              // Use custom mesh
        STREAKS            // Velocity streaks
    }
    
    public enum BlendMode {
        ALPHA,             // Standard alpha blending
        ADDITIVE,          // Additive blending
        MULTIPLY,          // Multiplicative blending
        SUBTRACT,          // Subtractive blending
        SCREEN             // Screen blending
    }
    
    public enum SortMode {
        NONE,              // No sorting
        BACK_TO_FRONT,     // Sort by distance (far to near)
        FRONT_TO_BACK,     // Sort by distance (near to far)
        BY_AGE,            // Sort by particle age
        BY_SIZE            // Sort by particle size
    }
    
    /**
     * Bounding box for particle system.
     */
    public static class BoundingBox {
        public Vector3 min;
        public Vector3 max;
        
        public BoundingBox() {
            this.min = new Vector3(-10, -10, -10);
            this.max = new Vector3(10, 10, 10);
        }
        
        public BoundingBox(Vector3 min, Vector3 max) {
            this.min = min;
            this.max = max;
        }
        
        public boolean contains(Vector3 point) {
            return point.x >= min.x && point.x <= max.x &&
                   point.y >= min.y && point.y <= max.y &&
                   point.z >= min.z && point.z <= max.z;
        }
        
        public Vector3 getCenter() {
            return min.add(max).multiply(0.5);
        }
        
        public Vector3 getSize() {
            return max.subtract(min);
        }
    }
    
    /**
     * Create a new particle system.
     */
    public ParticleSystem(String name) {
        this.systemId = UUID.randomUUID();
        this.name = name;
        this.enabled = true;
        this.systemAge = 0.0;
        this.maxAge = -1.0; // Infinite by default
        
        this.emitter = new ParticleEmitter(ParticleEmitter.EmitterType.POINT);
        this.emitterPosition = Vector3.ZERO;
        this.emitterRotation = Quaternion.IDENTITY;
        this.emitterScale = new Vector3(1.0, 1.0, 1.0);
        
        this.globalAcceleration = new Vector3(0.0, 0.0, -9.81); // Default gravity
        this.globalDamping = 0.99;
        this.boundingBox = new BoundingBox();
        this.useLocalCoordinates = true;
        
        this.renderMode = RenderMode.BILLBOARD;
        this.blendMode = BlendMode.ALPHA;
        this.depthWrite = false;
        this.depthTest = true;
        this.sortMode = SortMode.BACK_TO_FRONT;
        
        this.maxParticles = 1000;
        this.useBatching = true;
        this.useGPUSimulation = false;
        this.simulationQuality = 2;
        
        this.activeParticles = new ConcurrentLinkedQueue<>();
        this.deadParticles = new ConcurrentLinkedQueue<>();
    }
    
    // Getters and setters
    public UUID getSystemId() { return systemId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public double getSystemAge() { return systemAge; }
    public double getMaxAge() { return maxAge; }
    public void setMaxAge(double maxAge) { this.maxAge = maxAge; }
    
    public ParticleEmitter getEmitter() { return emitter; }
    public void setEmitter(ParticleEmitter emitter) { this.emitter = emitter; }
    
    public Vector3 getEmitterPosition() { return emitterPosition; }
    public void setEmitterPosition(Vector3 emitterPosition) { this.emitterPosition = emitterPosition; }
    
    public Quaternion getEmitterRotation() { return emitterRotation; }
    public void setEmitterRotation(Quaternion emitterRotation) { this.emitterRotation = emitterRotation; }
    
    public Vector3 getEmitterScale() { return emitterScale; }
    public void setEmitterScale(Vector3 emitterScale) { this.emitterScale = emitterScale; }
    
    public Vector3 getGlobalAcceleration() { return globalAcceleration; }
    public void setGlobalAcceleration(Vector3 globalAcceleration) { this.globalAcceleration = globalAcceleration; }
    
    public double getGlobalDamping() { return globalDamping; }
    public void setGlobalDamping(double globalDamping) { this.globalDamping = Math.max(0.0, Math.min(1.0, globalDamping)); }
    
    public BoundingBox getBoundingBox() { return boundingBox; }
    public void setBoundingBox(BoundingBox boundingBox) { this.boundingBox = boundingBox; }
    
    public boolean isUseLocalCoordinates() { return useLocalCoordinates; }
    public void setUseLocalCoordinates(boolean useLocalCoordinates) { this.useLocalCoordinates = useLocalCoordinates; }
    
    public RenderMode getRenderMode() { return renderMode; }
    public void setRenderMode(RenderMode renderMode) { this.renderMode = renderMode; }
    
    public BlendMode getBlendMode() { return blendMode; }
    public void setBlendMode(BlendMode blendMode) { this.blendMode = blendMode; }
    
    public UUID getTexture() { return texture; }
    public void setTexture(UUID texture) { this.texture = texture; }
    
    public boolean isDepthWrite() { return depthWrite; }
    public void setDepthWrite(boolean depthWrite) { this.depthWrite = depthWrite; }
    
    public boolean isDepthTest() { return depthTest; }
    public void setDepthTest(boolean depthTest) { this.depthTest = depthTest; }
    
    public SortMode getSortMode() { return sortMode; }
    public void setSortMode(SortMode sortMode) { this.sortMode = sortMode; }
    
    public int getMaxParticles() { return maxParticles; }
    public void setMaxParticles(int maxParticles) { this.maxParticles = Math.max(1, maxParticles); }
    
    public boolean isUseBatching() { return useBatching; }
    public void setUseBatching(boolean useBatching) { this.useBatching = useBatching; }
    
    public boolean isUseGPUSimulation() { return useGPUSimulation; }
    public void setUseGPUSimulation(boolean useGPUSimulation) { this.useGPUSimulation = useGPUSimulation; }
    
    public int getSimulationQuality() { return simulationQuality; }
    public void setSimulationQuality(int simulationQuality) { this.simulationQuality = Math.max(0, Math.min(3, simulationQuality)); }
    
    /**
     * Get current number of active particles.
     */
    public int getActiveParticleCount() {
        return activeParticles.size();
    }
    
    /**
     * Check if system has expired.
     */
    public boolean hasExpired() {
        return maxAge > 0 && systemAge >= maxAge;
    }
    
    /**
     * Update particle system.
     */
    public void update(double deltaTime) {
        if (!enabled || hasExpired()) return;
        
        systemAge += deltaTime;
        
        // Emit new particles
        emitParticles(deltaTime);
        
        // Update existing particles
        updateParticles(deltaTime);
        
        // Remove dead particles
        cleanupDeadParticles();
    }
    
    private void emitParticles(double deltaTime) {
        if (emitter == null) return;
        
        Random random = new Random();
        
        if (emitter.isContinuous()) {
            // Continuous emission
            double particlesToEmit = emitter.getEmissionRate() * deltaTime;
            int wholeParticles = (int) particlesToEmit;
            double fractionalPart = particlesToEmit - wholeParticles;
            
            // Emit whole particles
            for (int i = 0; i < wholeParticles && activeParticles.size() < maxParticles; i++) {
                emitParticle(random);
            }
            
            // Stochastic emission for fractional part
            if (random.nextDouble() < fractionalPart && activeParticles.size() < maxParticles) {
                emitParticle(random);
            }
        } else {
            // Burst emission (simplified - would need burst timing logic)
            if (systemAge > 0 && (systemAge % emitter.getBurstInterval()) < deltaTime) {
                int burstCount = (int) emitter.getBurstCount();
                for (int i = 0; i < burstCount && activeParticles.size() < maxParticles; i++) {
                    emitParticle(random);
                }
            }
        }
    }
    
    private void emitParticle(Random random) {
        Particle particle = getParticleFromPool();
        
        // Generate initial position and velocity
        Vector3 localPos = emitter.generatePosition(random);
        Vector3 localVel = emitter.generateVelocity(localPos, random);
        
        // Transform to world space if needed
        if (useLocalCoordinates) {
            // Apply emitter transform
            localPos = emitterRotation.rotate(localPos.multiply(emitterScale));
            localVel = emitterRotation.rotate(localVel);
            
            particle.properties.position = emitterPosition.add(localPos);
            particle.properties.velocity = localVel;
        } else {
            particle.properties.position = localPos.add(emitterPosition);
            particle.properties.velocity = localVel;
        }
        
        // Apply random variations
        applyRandomVariation(particle, random);
        
        activeParticles.offer(particle);
    }
    
    private void applyRandomVariation(Particle particle, Random random) {
        RandomVariation var = emitter.getVariation();
        
        if (var.lifeVariation > 0) {
            double lifeMod = 1.0 + (random.nextGaussian() * var.lifeVariation);
            particle.properties.life = Math.max(0.1, particle.properties.life * lifeMod);
        }
        
        if (!var.colorVariation.isZero()) {
            Vector3 colorMod = new Vector3(
                1.0 + (random.nextGaussian() * var.colorVariation.x),
                1.0 + (random.nextGaussian() * var.colorVariation.y),
                1.0 + (random.nextGaussian() * var.colorVariation.z)
            );
            particle.properties.color = particle.properties.color.multiply(colorMod);
        }
        
        if (var.sizeVariation > 0) {
            double sizeMod = 1.0 + (random.nextGaussian() * var.sizeVariation);
            particle.properties.size = Math.max(0.01, particle.properties.size * sizeMod);
        }
        
        if (var.rotationVariation > 0) {
            particle.properties.rotation = random.nextGaussian() * var.rotationVariation;
        }
        
        if (var.angularVelocityVariation > 0) {
            particle.properties.angularVelocity = random.nextGaussian() * var.angularVelocityVariation;
        }
    }
    
    private void updateParticles(double deltaTime) {
        for (Particle particle : activeParticles) {
            particle.update(deltaTime, globalAcceleration, globalDamping);
            
            // Check bounds
            if (!boundingBox.contains(particle.properties.position)) {
                // Handle boundary collision or remove particle
                particle.properties.age = particle.properties.life; // Kill particle
            }
        }
    }
    
    private void cleanupDeadParticles() {
        Iterator<Particle> iterator = activeParticles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            if (!particle.properties.isAlive()) {
                iterator.remove();
                returnParticleToPool(particle);
            }
        }
    }
    
    private Particle getParticleFromPool() {
        Particle particle = deadParticles.poll();
        if (particle == null) {
            particle = new Particle(emitter.getParticleTemplate());
        } else {
            // Reset particle properties
            particle.properties.age = 0.0;
        }
        return particle;
    }
    
    private void returnParticleToPool(Particle particle) {
        deadParticles.offer(particle);
    }
    
    /**
     * Get all active particles (for rendering).
     */
    public Collection<Particle> getActiveParticles() {
        return new ArrayList<>(activeParticles);
    }
    
    /**
     * Clear all particles.
     */
    public void clear() {
        while (!activeParticles.isEmpty()) {
            returnParticleToPool(activeParticles.poll());
        }
    }
    
    /**
     * Reset system age.
     */
    public void reset() {
        clear();
        systemAge = 0.0;
    }
    
    /**
     * Create LLSD representation of the particle system.
     */
    public Map<String, Object> toLLSD() {
        Map<String, Object> systemData = new HashMap<>();
        
        systemData.put("SystemID", systemId);
        systemData.put("Name", name);
        systemData.put("Enabled", enabled);
        systemData.put("MaxAge", maxAge);
        systemData.put("MaxParticles", maxParticles);
        
        // Emitter data
        Map<String, Object> emitterData = new HashMap<>();
        emitterData.put("Type", emitter.getType().name());
        emitterData.put("Shape", emitter.getShape().name());
        emitterData.put("Size", Arrays.asList(emitter.getSize().x, emitter.getSize().y, emitter.getSize().z));
        emitterData.put("EmissionRate", emitter.getEmissionRate());
        emitterData.put("BurstCount", emitter.getBurstCount());
        emitterData.put("BurstInterval", emitter.getBurstInterval());
        emitterData.put("Continuous", emitter.isContinuous());
        systemData.put("Emitter", emitterData);
        
        // Transform
        systemData.put("Position", Arrays.asList(emitterPosition.x, emitterPosition.y, emitterPosition.z));
        systemData.put("Rotation", Arrays.asList(emitterRotation.x, emitterRotation.y, emitterRotation.z, emitterRotation.w));
        systemData.put("Scale", Arrays.asList(emitterScale.x, emitterScale.y, emitterScale.z));
        
        // Global properties
        systemData.put("GlobalAcceleration", Arrays.asList(globalAcceleration.x, globalAcceleration.y, globalAcceleration.z));
        systemData.put("GlobalDamping", globalDamping);
        systemData.put("UseLocalCoordinates", useLocalCoordinates);
        
        // Rendering
        systemData.put("RenderMode", renderMode.name());
        systemData.put("BlendMode", blendMode.name());
        if (texture != null) systemData.put("Texture", texture);
        systemData.put("DepthWrite", depthWrite);
        systemData.put("DepthTest", depthTest);
        systemData.put("SortMode", sortMode.name());
        
        // Performance
        systemData.put("UseBatching", useBatching);
        systemData.put("UseGPUSimulation", useGPUSimulation);
        systemData.put("SimulationQuality", simulationQuality);
        
        return systemData;
    }
    
    @Override
    public String toString() {
        return String.format("ParticleSystem[%s, particles=%d/%d, age=%.2f]",
                           name, activeParticles.size(), maxParticles, systemAge);
    }
}
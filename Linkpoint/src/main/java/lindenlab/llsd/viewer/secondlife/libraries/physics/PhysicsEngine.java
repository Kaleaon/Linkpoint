/*
 * Physics Engine - Java abstraction for Second Life physics simulation
 *
 * Based on Second Life viewer physics implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.libraries.physics;

import lindenlab.llsd.viewer.secondlife.engine.Vector3;
import lindenlab.llsd.viewer.secondlife.engine.Quaternion;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Physics engine abstraction for Second Life object simulation.
 * <p>
 * This class provides a unified interface for physics simulation including:
 * <ul>
 *   <li>Rigid body dynamics with collision detection</li>
 *   <li>Soft body simulation for flexible objects</li>
 *   <li>Fluid dynamics for water and particle systems</li>
 *   <li>Character controller for avatar movement</li>
 *   <li>Constraint systems for joints and connections</li>
 *   <li>Optimized collision detection with spatial partitioning</li>
 * </ul>
 *
 * @author LLSD Java Team
 * @since 1.0
 */
public class PhysicsEngine {

    private boolean initialized = false;
    private PhysicsWorld world;
    private final Map<UUID, PhysicsBody> bodies = new ConcurrentHashMap<>();
    private final Map<UUID, PhysicsConstraint> constraints = new ConcurrentHashMap<>();
    private final PhysicsSettings settings = new PhysicsSettings();
    private CollisionDispatcher collisionDispatcher;
    private BroadphaseInterface broadphase;

    // Performance tracking
    private long lastUpdateTime = 0;
    private float averageFrameTime = 0.0f;
    private int simulationSteps = 0;

    /**
     * Physics engine configuration settings.
     */
    public static class PhysicsSettings {
        public float gravity = -9.81f;  // m/s²
        public float timeStep = 1.0f / 60.0f;  // 60 FPS
        public int maxSubSteps = 10;
        public float fixedTimeStep = 1.0f / 240.0f;  // Internal timestep

        // Collision detection settings
        public float worldScale = 1.0f;
        public Vector3 worldMinBounds = new Vector3(-1000, -1000, -1000);
        public Vector3 worldMaxBounds = new Vector3(1000, 1000, 1000);
        public int maxProxies = 32766;
        public float gridCellSize = 10.0f;

        // Solver settings
        public int solverIterations = 10;
        public float erp = 0.2f;  // Error Reduction Parameter
        public float cfm = 0.0f;  // Constraint Force Mixing
        public boolean enableWarmStarting = true;

        // Performance settings
        public boolean enableCCD = true;  // Continuous Collision Detection
        public boolean enableSleeping = true;
        public float sleepingThreshold = 0.8f;
        public float deactivationTime = 2.0f;

        // Second Life specific
        public boolean enablePhantomObjects = true;
        public boolean enableVolumeDetect = true;
        public float maxLinearVelocity = 256.0f;  // m/s
        public float maxAngularVelocity = 12.56f; // rad/s (2π)
    }

    /**
     * Physics world containing all simulated objects.
     */
    public static class PhysicsWorld {
        private final Vector3 gravity;
        private final CollisionConfiguration collisionConfig;
        private final List<PhysicsBody> activeBodies = new ArrayList<>();
        private boolean paused = false;

        public PhysicsWorld(Vector3 gravity, CollisionConfiguration config) {
            this.gravity = gravity;
            this.collisionConfig = config;
        }

        public Vector3 getGravity() { return gravity; }
        public CollisionConfiguration getCollisionConfig() { return collisionConfig; }
        public List<PhysicsBody> getActiveBodies() { return activeBodies; }
        public boolean isPaused() { return paused; }
        public void setPaused(boolean paused) { this.paused = paused; }

        public void addBody(PhysicsBody body) {
            if (!activeBodies.contains(body)) {
                activeBodies.add(body);
            }
        }

        public void removeBody(PhysicsBody body) {
            activeBodies.remove(body);
        }
    }

    /**
     * Collision configuration for the physics world.
     */
    public static class CollisionConfiguration {
        private final Map<String, CollisionAlgorithm> algorithms = new HashMap<>();
        private float defaultContactProcessingThreshold = 1e30f;

        public enum CollisionAlgorithm {
            BOX_BOX, SPHERE_SPHERE, CONVEX_CONVEX, CONVEX_TRIANGLE_MESH,
            COMPOUND_COMPOUND, HEIGHTFIELD
        }

        public void setAlgorithm(String shapeType1, String shapeType2, CollisionAlgorithm algorithm) {
            algorithms.put(shapeType1 + "_" + shapeType2, algorithm);
        }

        public CollisionAlgorithm getAlgorithm(String shapeType1, String shapeType2) {
            return algorithms.get(shapeType1 + "_" + shapeType2);
        }

        public float getDefaultContactProcessingThreshold() { return defaultContactProcessingThreshold; }
        public void setDefaultContactProcessingThreshold(float threshold) {
            this.defaultContactProcessingThreshold = threshold;
        }
    }

    /**
     * Physics body representing a simulated object.
     */
    public static class PhysicsBody {
        private final UUID bodyId;
        private final CollisionShape shape;
        private final MotionState motionState;

        // Physical properties
        private float mass = 1.0f;
        private float restitution = 0.0f;  // Bounciness
        private float friction = 0.5f;
        private float rollingFriction = 0.0f;
        private float spinningFriction = 0.0f;
        private float linearDamping = 0.0f;
        private float angularDamping = 0.0f;

        // State
        private Vector3 linearVelocity = new Vector3(0, 0, 0);
        private Vector3 angularVelocity = new Vector3(0, 0, 0);
        private boolean active = true;
        private boolean kinematic = false;
        private boolean phantom = false;  // Second Life phantom objects
        private boolean volumeDetect = false;  // Second Life volume detect

        // Collision flags
        private int collisionFlags = 0;
        private int collisionGroup = 1;
        private int collisionMask = -1;

        public PhysicsBody(UUID bodyId, CollisionShape shape, MotionState motionState) {
            this.bodyId = bodyId;
            this.shape = shape;
            this.motionState = motionState;
        }

        // Getters and setters
        public UUID getBodyId() { return bodyId; }
        public CollisionShape getShape() { return shape; }
        public MotionState getMotionState() { return motionState; }

        public float getMass() { return mass; }
        public void setMass(float mass) { this.mass = Math.max(0.0f, mass); }

        public float getRestitution() { return restitution; }
        public void setRestitution(float restitution) {
            this.restitution = Math.max(0.0f, Math.min(1.0f, restitution));
        }

        public float getFriction() { return friction; }
        public void setFriction(float friction) { this.friction = Math.max(0.0f, friction); }

        public Vector3 getLinearVelocity() { return linearVelocity; }
        public void setLinearVelocity(Vector3 velocity) { this.linearVelocity = velocity; }

        public Vector3 getAngularVelocity() { return angularVelocity; }
        public void setAngularVelocity(Vector3 velocity) { this.angularVelocity = velocity; }

        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }

        public boolean isKinematic() { return kinematic; }
        public void setKinematic(boolean kinematic) { this.kinematic = kinematic; }

        public boolean isPhantom() { return phantom; }
        public void setPhantom(boolean phantom) { this.phantom = phantom; }

        public boolean isVolumeDetect() { return volumeDetect; }
        public void setVolumeDetect(boolean volumeDetect) { this.volumeDetect = volumeDetect; }

        /**
         * Apply force at the center of mass.
         */
        public void applyForce(Vector3 force) {
            if (!kinematic && active) {
                // F = ma, so acceleration = F/m
                Vector3 acceleration = force.multiply(1.0 / mass);
                linearVelocity = linearVelocity.add(acceleration);
            }
        }

        /**
         * Apply torque to the body.
         */
        public void applyTorque(Vector3 torque) {
            if (!kinematic && active) {
                // Simplified torque application
                angularVelocity = angularVelocity.add(torque.multiply(1.0 / mass));
            }
        }

        /**
         * Calculate the world-space bounding box for this body.
         */
        public BoundingBox getWorldBoundingBox() {
            Vector3 pos = (motionState != null) ? motionState.getPosition() : Vector3.ZERO;
            if (shape == null) {
                return new BoundingBox(pos, pos);
            }
            BoundingBox local = shape.getBoundingBox();
            Vector3 min = local.getMin().add(pos);
            Vector3 max = local.getMax().add(pos);
            return new BoundingBox(min, max);
        }
    }

    /**
     * Motion state tracks the position and orientation of a physics body.
     */
    public static class MotionState {
        private Vector3 position;
        private Quaternion orientation;
        private Vector3 centerOfMassOffset = new Vector3(0, 0, 0);

        public MotionState(Vector3 position, Quaternion orientation) {
            this.position = position;
            this.orientation = orientation;
        }

        public Vector3 getPosition() { return position; }
        public void setPosition(Vector3 position) { this.position = position; }

        public Quaternion getOrientation() { return orientation; }
        public void setOrientation(Quaternion orientation) { this.orientation = orientation; }

        public Vector3 getCenterOfMassOffset() { return centerOfMassOffset; }
        public void setCenterOfMassOffset(Vector3 offset) { this.centerOfMassOffset = offset; }

        /**
         * Get the world transform matrix.
         */
        public Matrix4 getWorldTransform() {
            // Placeholder for actual matrix calculation
            return new Matrix4(position, orientation);
        }
    }

    /**
     * Collision shape defines the geometry for collision detection.
     */
    public static abstract class CollisionShape {
        protected final ShapeType type;
        protected float margin = 0.04f;  // Collision margin

        public enum ShapeType {
            BOX, SPHERE, CYLINDER, CAPSULE, CONE, CONVEX_HULL,
            TRIANGLE_MESH, HEIGHTFIELD, COMPOUND
        }

        protected CollisionShape(ShapeType type) {
            this.type = type;
        }

        public ShapeType getType() { return type; }
        public float getMargin() { return margin; }
        public void setMargin(float margin) { this.margin = Math.max(0.0f, margin); }

        /**
         * Calculate the local inertia tensor for this shape.
         */
        public abstract Vector3 calculateLocalInertia(float mass);

        /**
         * Get the bounding box of this shape.
         */
        public abstract BoundingBox getBoundingBox();
    }

    /**
     * Box collision shape.
     */
    public static class BoxShape extends CollisionShape {
        private final Vector3 halfExtents;

        public BoxShape(Vector3 halfExtents) {
            super(ShapeType.BOX);
            this.halfExtents = halfExtents;
        }

        public Vector3 getHalfExtents() { return halfExtents; }

        @Override
        public Vector3 calculateLocalInertia(float mass) {
            float x2 = (float)(halfExtents.x * halfExtents.x);
            float y2 = (float)(halfExtents.y * halfExtents.y);
            float z2 = (float)(halfExtents.z * halfExtents.z);

            float factor = mass / 3.0f;
            return new Vector3(
                factor * (y2 + z2),
                factor * (x2 + z2),
                factor * (x2 + y2)
            );
        }

        @Override
        public BoundingBox getBoundingBox() {
            return new BoundingBox(halfExtents.negate(), halfExtents);
        }
    }

    /**
     * Sphere collision shape.
     */
    public static class SphereShape extends CollisionShape {
        private final float radius;

        public SphereShape(float radius) {
            super(ShapeType.SPHERE);
            this.radius = radius;
        }

        public float getRadius() { return radius; }

        @Override
        public Vector3 calculateLocalInertia(float mass) {
            float inertia = 0.4f * mass * radius * radius;  // (2/5) * m * r²
            return new Vector3(inertia, inertia, inertia);
        }

        @Override
        public BoundingBox getBoundingBox() {
            Vector3 extent = new Vector3(radius, radius, radius);
            return new BoundingBox(extent.negate(), extent);
        }
    }

    /**
     * Physics constraint connecting two bodies.
     */
    public static abstract class PhysicsConstraint {
        protected final UUID constraintId;
        protected final PhysicsBody bodyA;
        protected final PhysicsBody bodyB;
        protected boolean enabled = true;
        protected float breakingThreshold = Float.MAX_VALUE;

        protected PhysicsConstraint(UUID constraintId, PhysicsBody bodyA, PhysicsBody bodyB) {
            this.constraintId = constraintId;
            this.bodyA = bodyA;
            this.bodyB = bodyB;
        }

        public UUID getConstraintId() { return constraintId; }
        public PhysicsBody getBodyA() { return bodyA; }
        public PhysicsBody getBodyB() { return bodyB; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public float getBreakingThreshold() { return breakingThreshold; }
        public void setBreakingThreshold(float threshold) { this.breakingThreshold = threshold; }
    }

    /**
     * Simple bounding box for collision detection.
     */
    public static class BoundingBox {
        private final Vector3 min;
        private final Vector3 max;

        public BoundingBox(Vector3 min, Vector3 max) {
            this.min = min;
            this.max = max;
        }

        public Vector3 getMin() { return min; }
        public Vector3 getMax() { return max; }

        public boolean intersects(BoundingBox other) {
            return !(max.x < other.min.x || min.x > other.max.x ||
                    max.y < other.min.y || min.y > other.max.y ||
                    max.z < other.min.z || min.z > other.max.z);
        }
    }

    /**
     * Simple 4x4 matrix for transformations (placeholder).
     */
    public static class Matrix4 {
        private final float[][] matrix = new float[4][4];

        public Matrix4(Vector3 position, Quaternion orientation) {
            // Placeholder for matrix construction from position and orientation
            setIdentity();
        }

        private void setIdentity() {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    matrix[i][j] = (i == j) ? 1.0f : 0.0f;
                }
            }
        }
    }

    /**
     * Collision dispatcher interface.
     */
    public interface CollisionDispatcher {
        void processCollision(PhysicsBody bodyA, PhysicsBody bodyB, ContactManifold manifold);
    }

    /**
     * Broadphase collision detection interface.
     */
    public interface BroadphaseInterface {
        List<CollisionPair> detectPotentialCollisions();
    }

    /**
     * Contact manifold containing collision information.
     */
    public static class ContactManifold {
        private final List<ContactPoint> contactPoints = new ArrayList<>();
        private Vector3 normal;

        public List<ContactPoint> getContactPoints() { return contactPoints; }
        public Vector3 getNormal() { return normal; }
        public void setNormal(Vector3 normal) { this.normal = normal; }

        public void addContactPoint(ContactPoint point) {
            contactPoints.add(point);
        }
    }

    /**
     * Single contact point in a collision.
     */
    public static class ContactPoint {
        private Vector3 pointA;
        private Vector3 pointB;
        private Vector3 normal;
        private float distance;

        public ContactPoint(Vector3 pointA, Vector3 pointB, Vector3 normal, float distance) {
            this.pointA = pointA;
            this.pointB = pointB;
            this.normal = normal;
            this.distance = distance;
        }

        // Getters
        public Vector3 getPointA() { return pointA; }
        public Vector3 getPointB() { return pointB; }
        public Vector3 getNormal() { return normal; }
        public float getDistance() { return distance; }
    }

    /**
     * Collision pair for broadphase detection.
     */
    public static class CollisionPair {
        private final PhysicsBody bodyA;
        private final PhysicsBody bodyB;

        public CollisionPair(PhysicsBody bodyA, PhysicsBody bodyB) {
            this.bodyA = bodyA;
            this.bodyB = bodyB;
        }

        public PhysicsBody getBodyA() { return bodyA; }
        public PhysicsBody getBodyB() { return bodyB; }
    }

    /**
     * Initialize the physics engine.
     *
     * @return true if initialization was successful, false otherwise
     */
    public boolean initialize() {
        if (initialized) {
            return true;
        }

        try {
            System.out.println("Initializing physics engine...");

            // Create collision configuration
            CollisionConfiguration config = new CollisionConfiguration();
            setupCollisionAlgorithms(config);

            // Create physics world
            Vector3 gravity = new Vector3(0, settings.gravity, 0);
            world = new PhysicsWorld(gravity, config);

            // Initialize collision dispatcher (placeholder)
            collisionDispatcher = new DefaultCollisionDispatcher();

            // Initialize spatial hash grid broadphase
            broadphase = new SpatialGridBroadphase(world, settings);

            initialized = true;
            lastUpdateTime = System.nanoTime();

            System.out.println("Physics engine initialized successfully");
            System.out.println("Gravity: " + gravity);
            System.out.println("Time step: " + settings.timeStep + "s");

            return true;

        } catch (Exception e) {
            System.err.println("Error initializing physics engine: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create a physics body and add it to the world.
     *
     * @param objectId Unique identifier for the object
     * @param shape Collision shape
     * @param position Initial position
     * @param orientation Initial orientation
     * @param mass Mass of the object (0 for static objects)
     * @return The created physics body
     */
    public PhysicsBody createBody(UUID objectId, CollisionShape shape, Vector3 position,
                                 Quaternion orientation, float mass) {
        if (!initialized) {
            return null;
        }

        MotionState motionState = new MotionState(position, orientation);
        PhysicsBody body = new PhysicsBody(objectId, shape, motionState);
        body.setMass(mass);

        // Add to world and tracking
        world.addBody(body);
        bodies.put(objectId, body);

        System.out.println("Created physics body: " + objectId + " (mass=" + mass + ")");
        return body;
    }

    /**
     * Step the physics simulation forward.
     *
     * @param deltaTime Time elapsed since last step in seconds
     */
    public void stepSimulation(float deltaTime) {
        if (!initialized || world.isPaused()) {
            return;
        }

        long startTime = System.nanoTime();

        // Clamp delta time to prevent instability
        deltaTime = Math.min(deltaTime, settings.timeStep * settings.maxSubSteps);

        int numSteps = (int) Math.ceil(deltaTime / settings.fixedTimeStep);
        float stepSize = deltaTime / numSteps;

        for (int i = 0; i < numSteps; i++) {
            performSimulationStep(stepSize);
        }

        // Update performance metrics
        long endTime = System.nanoTime();
        float frameTime = (endTime - startTime) / 1_000_000.0f; // Convert to milliseconds
        averageFrameTime = (averageFrameTime * 0.9f) + (frameTime * 0.1f);
        simulationSteps++;

        if (simulationSteps % 60 == 0) { // Log every 60 steps
            System.out.println("Physics performance: " + String.format("%.2f", averageFrameTime) + "ms avg");
        }
    }

    /**
     * Get a physics body by its ID.
     *
     * @param objectId The object ID
     * @return The physics body, or null if not found
     */
    public PhysicsBody getBody(UUID objectId) {
        return bodies.get(objectId);
    }

    /**
     * Remove a physics body from the world.
     *
     * @param objectId The object ID
     * @return true if the body was removed, false if not found
     */
    public boolean removeBody(UUID objectId) {
        PhysicsBody body = bodies.remove(objectId);
        if (body != null) {
            world.removeBody(body);
            System.out.println("Removed physics body: " + objectId);
            return true;
        }
        return false;
    }

    /**
     * Get the current physics settings.
     *
     * @return The physics settings object
     */
    public PhysicsSettings getSettings() {
        return settings;
    }

    /**
     * Get the physics world.
     *
     * @return The physics world object
     */
    public PhysicsWorld getWorld() {
        return world;
    }

    /**
     * Get the broadphase algorithm.
     *
     * @return The broadphase interface implementation
     */
    public BroadphaseInterface getBroadphase() {
        return broadphase;
    }

    /**
     * Set the broadphase algorithm.
     *
     * @param broadphase The broadphase interface implementation
     */
    public void setBroadphase(BroadphaseInterface broadphase) {
        this.broadphase = broadphase;
    }

    /**
     * Shutdown the physics engine and cleanup resources.
     */
    public void shutdown() {
        if (!initialized) {
            return;
        }

        System.out.println("Shutting down physics engine...");

        // Clear all bodies and constraints
        bodies.clear();
        constraints.clear();

        // Cleanup world
        if (world != null) {
            world.getActiveBodies().clear();
        }

        world = null;
        collisionDispatcher = null;
        broadphase = null;

        initialized = false;
        System.out.println("Physics engine shutdown complete");
    }

    // Private helper methods

    private void setupCollisionAlgorithms(CollisionConfiguration config) {
        // Setup collision algorithms for different shape combinations
        config.setAlgorithm("BOX", "BOX", CollisionConfiguration.CollisionAlgorithm.BOX_BOX);
        config.setAlgorithm("SPHERE", "SPHERE", CollisionConfiguration.CollisionAlgorithm.SPHERE_SPHERE);
        // ... more algorithm configurations
    }

    private void performSimulationStep(float stepSize) {
        // Apply forces and integrate motion
        for (PhysicsBody body : world.getActiveBodies()) {
            if (body.isActive() && !body.isKinematic()) {
                integrateBody(body, stepSize);
            }
        }

        // Detect collisions (placeholder)
        detectCollisions();

        // Resolve constraints (placeholder)
        resolveConstraints(stepSize);
    }

    private void integrateBody(PhysicsBody body, float stepSize) {
        MotionState motionState = body.getMotionState();

        // Apply gravity
        Vector3 force = new Vector3(0, settings.gravity * body.getMass(), 0);
        body.applyForce(force);

        // Integrate linear motion: position += velocity * dt
        Vector3 newPosition = motionState.getPosition().add(
            body.getLinearVelocity().multiply(stepSize)
        );
        motionState.setPosition(newPosition);

        // Apply damping
        body.setLinearVelocity(body.getLinearVelocity().multiply(1.0 - body.linearDamping * stepSize));
        body.setAngularVelocity(body.getAngularVelocity().multiply(1.0 - body.angularDamping * stepSize));
    }

    private void detectCollisions() {
        // Placeholder collision detection
        List<CollisionPair> pairs = broadphase.detectPotentialCollisions();

        for (CollisionPair pair : pairs) {
            // Perform narrow-phase collision detection
            ContactManifold manifold = performNarrowPhaseDetection(pair);
            if (manifold != null && !manifold.getContactPoints().isEmpty()) {
                collisionDispatcher.processCollision(pair.getBodyA(), pair.getBodyB(), manifold);
            }
        }
    }

    private ContactManifold performNarrowPhaseDetection(CollisionPair pair) {
        // Placeholder narrow-phase collision detection
        // In a real implementation, this would use specific algorithms based on shape types
        return null;
    }

    private void resolveConstraints(float stepSize) {
        // Placeholder constraint resolution
        for (PhysicsConstraint constraint : constraints.values()) {
            if (constraint.isEnabled()) {
                // Resolve constraint forces
            }
        }
    }

    // Default and spatial hash grid broadphase implementations

    private class DefaultCollisionDispatcher implements CollisionDispatcher {
        @Override
        public void processCollision(PhysicsBody bodyA, PhysicsBody bodyB, ContactManifold manifold) {
            // Placeholder collision response
            System.out.println("Collision detected between " + bodyA.getBodyId() + " and " + bodyB.getBodyId());
        }
    }

    public static class DefaultBroadphase implements BroadphaseInterface {
        private final PhysicsWorld world;

        public DefaultBroadphase(PhysicsWorld world) {
            this.world = world;
        }

        public DefaultBroadphase() {
            this.world = null;
        }

        @Override
        public List<CollisionPair> detectPotentialCollisions() {
            List<CollisionPair> pairs = new ArrayList<>();
            if (world == null) {
                return pairs;
            }
            List<PhysicsBody> bodies = world.getActiveBodies();
            if (bodies == null) {
                return pairs;
            }

            for (int i = 0; i < bodies.size(); i++) {
                PhysicsBody bodyA = bodies.get(i);
                if (bodyA == null || !bodyA.isActive() || bodyA.getShape() == null) continue;

                for (int j = i + 1; j < bodies.size(); j++) {
                    PhysicsBody bodyB = bodies.get(j);
                    if (bodyB == null || !bodyB.isActive() || bodyB.getShape() == null) continue;

                    if (bodyA.getWorldBoundingBox().intersects(bodyB.getWorldBoundingBox())) {
                        pairs.add(new CollisionPair(bodyA, bodyB));
                    }
                }
            }

            return pairs;
        }
    }

    /**
     * 3D Spatial Hash Grid broadphase collision detection implementation.
     * Maps bounding boxes into spatial cells using bounds from PhysicsSettings.
     */
    public static class SpatialGridBroadphase implements BroadphaseInterface {
        private final PhysicsWorld world;
        private final PhysicsSettings settings;
        private float cellSize = 10.0f;

        public SpatialGridBroadphase(PhysicsWorld world, PhysicsSettings settings) {
            this.world = world;
            this.settings = settings;
            if (settings != null && settings.gridCellSize > 0.0f) {
                this.cellSize = settings.gridCellSize;
            }
        }

        public SpatialGridBroadphase(PhysicsWorld world, PhysicsSettings settings, float cellSize) {
            this.world = world;
            this.settings = settings;
            this.cellSize = cellSize > 0.0f ? cellSize : 10.0f;
        }

        public SpatialGridBroadphase() {
            this.world = null;
            this.settings = null;
        }

        public float getCellSize() {
            return (settings != null && settings.gridCellSize > 0.0f) ? settings.gridCellSize : cellSize;
        }

        public void setCellSize(float cellSize) {
            if (cellSize > 0.0f) {
                this.cellSize = cellSize;
                if (settings != null) {
                    settings.gridCellSize = cellSize;
                }
            }
        }

        @Override
        public List<CollisionPair> detectPotentialCollisions() {
            List<CollisionPair> pairs = new ArrayList<>();
            if (world == null) {
                return pairs;
            }

            List<PhysicsBody> activeBodies = world.getActiveBodies();
            if (activeBodies == null || activeBodies.size() < 2) {
                return pairs;
            }

            List<PhysicsBody> validBodies = new ArrayList<>(activeBodies.size());
            List<BoundingBox> worldBoxes = new ArrayList<>(activeBodies.size());

            for (PhysicsBody body : activeBodies) {
                if (body != null && body.isActive() && body.getShape() != null) {
                    validBodies.add(body);
                    worldBoxes.add(body.getWorldBoundingBox());
                }
            }

            int n = validBodies.size();
            if (n < 2) {
                return pairs;
            }

            float effCellSize = getCellSize();
            Vector3 minBounds = (settings != null && settings.worldMinBounds != null)
                    ? settings.worldMinBounds
                    : new Vector3(-1000, -1000, -1000);

            Map<CellKey, List<Integer>> grid = new HashMap<>();

            for (int i = 0; i < n; i++) {
                BoundingBox box = worldBoxes.get(i);
                Vector3 min = box.getMin();
                Vector3 max = box.getMax();

                if (Double.isNaN(min.x) || Double.isNaN(min.y) || Double.isNaN(min.z) ||
                    Double.isNaN(max.x) || Double.isNaN(max.y) || Double.isNaN(max.z)) {
                    continue;
                }

                int minX = (int) Math.floor((min.x - minBounds.x) / effCellSize);
                int maxX = (int) Math.floor((max.x - minBounds.x) / effCellSize);
                int minY = (int) Math.floor((min.y - minBounds.y) / effCellSize);
                int maxY = (int) Math.floor((max.y - minBounds.y) / effCellSize);
                int minZ = (int) Math.floor((min.z - minBounds.z) / effCellSize);
                int maxZ = (int) Math.floor((max.z - minBounds.z) / effCellSize);

                for (int x = minX; x <= maxX; x++) {
                    for (int y = minY; y <= maxY; y++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            CellKey key = new CellKey(x, y, z);
                            grid.computeIfAbsent(key, k -> new ArrayList<>()).add(i);
                        }
                    }
                }
            }

            Set<PairKey> testedPairs = new HashSet<>();

            for (List<Integer> cellBodies : grid.values()) {
                int count = cellBodies.size();
                if (count < 2) {
                    continue;
                }

                for (int i = 0; i < count; i++) {
                    int idxA = cellBodies.get(i);
                    PhysicsBody bodyA = validBodies.get(idxA);

                    for (int j = i + 1; j < count; j++) {
                        int idxB = cellBodies.get(j);
                        PhysicsBody bodyB = validBodies.get(idxB);

                        PairKey pairKey = PairKey.of(bodyA, bodyB);
                        if (testedPairs.add(pairKey)) {
                            BoundingBox boxA = worldBoxes.get(idxA);
                            BoundingBox boxB = worldBoxes.get(idxB);

                            if (boxA.intersects(boxB)) {
                                pairs.add(new CollisionPair(bodyA, bodyB));
                            }
                        }
                    }
                }
            }

            return pairs;
        }

        public static class CellKey {
            private final int x;
            private final int y;
            private final int z;

            public CellKey(int x, int y, int z) {
                this.x = x;
                this.y = y;
                this.z = z;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                CellKey cellKey = (CellKey) o;
                return x == cellKey.x && y == cellKey.y && z == cellKey.z;
            }

            @Override
            public int hashCode() {
                return Objects.hash(x, y, z);
            }
        }

        public static class PairKey {
            private final UUID idA;
            private final UUID idB;
            private final int fallbackA;
            private final int fallbackB;

            private PairKey(UUID idA, UUID idB, int fallbackA, int fallbackB) {
                this.idA = idA;
                this.idB = idB;
                this.fallbackA = fallbackA;
                this.fallbackB = fallbackB;
            }

            public static PairKey of(PhysicsBody bodyA, PhysicsBody bodyB) {
                UUID a = bodyA.getBodyId();
                UUID b = bodyB.getBodyId();

                if (a != null && b != null) {
                    if (a.compareTo(b) <= 0) {
                        return new PairKey(a, b, 0, 0);
                    } else {
                        return new PairKey(b, a, 0, 0);
                    }
                } else {
                    int hA = System.identityHashCode(bodyA);
                    int hB = System.identityHashCode(bodyB);
                    if (hA <= hB) {
                        return new PairKey(a, b, hA, hB);
                    } else {
                        return new PairKey(b, a, hB, hA);
                    }
                }
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                PairKey pairKey = (PairKey) o;
                return Objects.equals(idA, pairKey.idA) &&
                       Objects.equals(idB, pairKey.idB) &&
                       fallbackA == pairKey.fallbackA &&
                       fallbackB == pairKey.fallbackB;
            }

            @Override
            public int hashCode() {
                return Objects.hash(idA, idB, fallbackA, fallbackB);
            }
        }
    }
}
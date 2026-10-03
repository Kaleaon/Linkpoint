/*
 * Second Life Scene Node - Java implementation of scene graph node
 *
 * Based on Second Life viewer implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.engine;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

/**
 * Represents a node in a 3D scene graph, a hierarchical structure used to
 * organize objects in a 3D world.
 * <p>
 * Each node has a transform (position, rotation, scale), a parent, and a list
 * of children. This allows for complex scenes to be built from simple parts.
 * The transformation of a child node is relative to its parent, and this class
 * provides methods to calculate both local and world transformations.
 * <p>
 * Nodes can also store arbitrary properties and can be traversed using a
 * visitor pattern.
 */
public class SceneNode {
    
    private static final Map<UUID, SceneNode> nodeRegistry = new ConcurrentHashMap<>();
    
    private final UUID nodeId;
    private final String name;
    private SceneNode parent;
    private final List<SceneNode> children;
    private final Map<String, Object> properties;
    
    // Transform properties
    private Vector3 position;
    private Quaternion rotation;
    private Vector3 scale;
    private boolean visible;
    private boolean enabled;
    
    // Cached transformation matrices
    private double[][] localTransform;
    private double[][] worldTransform;
    private boolean transformDirty;
    
    /**
     * Constructs a new scene node with a given name and a randomly generated UUID.
     *
     * @param name The name of the node, used for identification. If null, a default
     *             name will be generated.
     */
    public SceneNode(String name) {
        this.nodeId = UUID.randomUUID();
        this.name = name != null ? name : "Node_" + nodeId.toString().substring(0, 8);
        this.parent = null;
        this.children = new ArrayList<>();
        this.properties = new HashMap<>();
        
        this.position = Vector3.ZERO;
        this.rotation = Quaternion.IDENTITY;
        this.scale = Vector3.ONE;
        this.visible = true;
        this.enabled = true;
        
        this.transformDirty = true;
        
        // Register the node
        nodeRegistry.put(nodeId, this);
    }
    
    /**
     * Constructs a new scene node with a specific UUID and name.
     * <p>
     * This constructor is typically used when loading a scene from a file or
     * asset, where node IDs are predetermined.
     *
     * @param nodeId The UUID to assign to this node.
     * @param name   The name of the node.
     */
    public SceneNode(UUID nodeId, String name) {
        this.nodeId = nodeId;
        this.name = name != null ? name : "Node_" + nodeId.toString().substring(0, 8);
        this.parent = null;
        this.children = new ArrayList<>();
        this.properties = new HashMap<>();
        
        this.position = Vector3.ZERO;
        this.rotation = Quaternion.IDENTITY;
        this.scale = Vector3.ONE;
        this.visible = true;
        this.enabled = true;
        
        this.transformDirty = true;
        
        // Register the node
        nodeRegistry.put(nodeId, this);
    }
    
    // Getters
    public UUID getNodeId() { return nodeId; }
    public String getName() { return name; }
    public SceneNode getParent() { return parent; }
    public List<SceneNode> getChildren() { return new ArrayList<>(children); }
    public Vector3 getPosition() { return position; }
    public Quaternion getRotation() { return rotation; }
    public Vector3 getScale() { return scale; }
    public boolean isVisible() { return visible; }
    public boolean isEnabled() { return enabled; }
    
    /**
     * Sets the position of this node relative to its parent.
     *
     * @param position The new local position.
     */
    public void setPosition(Vector3 position) {
        this.position = position;
        markTransformDirty();
    }
    
    /**
     * Sets the rotation of this node relative to its parent.
     *
     * @param rotation The new local rotation as a {@link Quaternion}.
     */
    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
        markTransformDirty();
    }
    
    /**
     * Sets the scale of this node relative to its parent.
     *
     * @param scale The new local scale as a {@link Vector3}.
     */
    public void setScale(Vector3 scale) {
        this.scale = scale;
        markTransformDirty();
    }
    
    /**
     * Sets the visibility of this node and its descendants.
     *
     * @param visible {@code true} to make the node visible, {@code false} to hide it.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Sets the enabled state of this node. An inactive node might be skipped
     * during updates or rendering.
     *
     * @param enabled {@code true} to enable the node, {@code false} to disable it.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Adds a child node to this node.
     * <p>
     * If the child node already has a parent, it is first removed from its
     * current parent before being added to this one.
     *
     * @param child The {@code SceneNode} to add as a child.
     * @throws IllegalArgumentException if the child is null or is this node itself.
     */
    public void addChild(SceneNode child) {
        if (child == null || child == this) {
            throw new IllegalArgumentException("Invalid child node");
        }
        
        if (child.parent != null) {
            child.parent.removeChild(child);
        }
        
        children.add(child);
        child.parent = this;
        child.markTransformDirty();
    }
    
    /**
     * Removes a specific child node from this node's list of children.
     *
     * @param child The child node to remove.
     */
    public void removeChild(SceneNode child) {
        if (children.remove(child)) {
            child.parent = null;
            child.markTransformDirty();
        }
    }
    
    /**
     * Detaches this node from its parent, making it a root node in the scene graph.
     */
    public void removeFromParent() {
        if (parent != null) {
            parent.removeChild(this);
        }
    }
    
    /**
     * Finds a direct child of this node by its name.
     *
     * @param name The name of the child to find.
     * @return The {@link SceneNode} if found, otherwise {@code null}.
     */
    public SceneNode findChild(String name) {
        for (SceneNode child : children) {
            if (child.name.equals(name)) {
                return child;
            }
        }
        return null;
    }
    
    /**
     * Finds a direct child of this node by its UUID.
     *
     * @param nodeId The UUID of the child to find.
     * @return The {@link SceneNode} if found, otherwise {@code null}.
     */
    public SceneNode findChild(UUID nodeId) {
        for (SceneNode child : children) {
            if (child.nodeId.equals(nodeId)) {
                return child;
            }
        }
        return null;
    }
    
    /**
     * Recursively finds a descendant of this node by its name.
     * <p>
     * This performs a depth-first search of the subtree rooted at this node.
     *
     * @param name The name of the descendant to find.
     * @return The {@link SceneNode} if found, otherwise {@code null}.
     */
    public SceneNode findDescendant(String name) {
        SceneNode child = findChild(name);
        if (child != null) {
            return child;
        }
        
        for (SceneNode childNode : children) {
            SceneNode descendant = childNode.findDescendant(name);
            if (descendant != null) {
                return descendant;
            }
        }
        
        return null;
    }
    
    /**
     * Gets the value of a custom property associated with this node.
     *
     * @param key The key of the property.
     * @return The property value, or {@code null} if the key is not found.
     */
    public Object getProperty(String key) {
        return properties.get(key);
    }
    
    /**
     * Sets a custom property on this node.
     *
     * @param key   The key of the property.
     * @param value The value of the property.
     */
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    /**
     * Removes a custom property from this node.
     *
     * @param key The key of the property to remove.
     */
    public void removeProperty(String key) {
        properties.remove(key);
    }
    
    /**
     * Checks if this node has a custom property with the given key.
     *
     * @param key The key to check for.
     * @return {@code true} if the property exists, {@code false} otherwise.
     */
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }
    
    /**
     * Gets a copy of the map of all custom properties on this node.
     *
     * @return A new {@link Map} containing the node's properties.
     */
    public Map<String, Object> getProperties() {
        return new HashMap<>(properties);
    }
    
    /**
     * Gets the local transformation matrix of this node.
     * <p>
     * This matrix represents the node's transformation relative to its parent.
     * The matrix is cached and only recalculated if the transform is marked as dirty.
     *
     * @return A 4x4 matrix representing the local transform.
     */
    public double[][] getLocalTransform() {
        if (transformDirty || localTransform == null) {
            updateLocalTransform();
        }
        return cloneMatrix(localTransform);
    }
    
    /**
     * Gets the world transformation matrix of this node.
     * <p>
     * This matrix represents the node's complete transformation from the root of
     * the scene graph. It is calculated by combining this node's local transform
     * with its parent's world transform.
     *
     * @return A 4x4 matrix representing the world transform.
     */
    public double[][] getWorldTransform() {
        if (transformDirty || worldTransform == null) {
            updateWorldTransform();
        }
        return cloneMatrix(worldTransform);
    }
    
    /**
     * Calculates and returns the position of this node in world space.
     *
     * @return A {@link Vector3} representing the world position.
     */
    public Vector3 getWorldPosition() {
        double[][] world = getWorldTransform();
        return new Vector3(world[0][3], world[1][3], world[2][3]);
    }
    
    /**
     * Calculates and returns the rotation of this node in world space.
     *
     * @return A {@link Quaternion} representing the world rotation.
     */
    public Quaternion getWorldRotation() {
        if (parent == null) {
            return rotation;
        }
        
        return parent.getWorldRotation().multiply(rotation);
    }
    
    /**
     * Transforms a point from this node's local space into world space.
     *
     * @param localPoint The point in local space to be transformed.
     * @return A new {@link Vector3} representing the point in world space.
     */
    public Vector3 transformToWorld(Vector3 localPoint) {
        return multiplyMatrixVector(getWorldTransform(), localPoint);
    }
    
    /**
     * Transforms a point from world space into this node's local space.
     *
     * @param worldPoint The point in world space to be transformed.
     * @return A new {@link Vector3} representing the point in local space.
     */
    public Vector3 transformToLocal(Vector3 worldPoint) {
        return multiplyMatrixVector(invertMatrix(getWorldTransform()), worldPoint);
    }
    
    /**
     * Update local transformation matrix.
     */
    private void updateLocalTransform() {
        // Create transformation matrix: T * R * S
        double[][] translation = createTranslationMatrix(position);
        double[][] rotationMatrix = create4x4Matrix(rotation.toMatrix3());
        double[][] scaleMatrix = createScaleMatrix(scale);
        
        localTransform = multiplyMatrices(translation, multiplyMatrices(rotationMatrix, scaleMatrix));
        transformDirty = false;
    }
    
    /**
     * Update world transformation matrix.
     */
    private void updateWorldTransform() {
        if (transformDirty || localTransform == null) {
            updateLocalTransform();
        }
        
        if (parent == null) {
            worldTransform = cloneMatrix(localTransform);
        } else {
            worldTransform = multiplyMatrices(parent.getWorldTransform(), localTransform);
        }
    }
    
    /**
     * Mark this node and all children as having dirty transforms.
     */
    private void markTransformDirty() {
        transformDirty = true;
        for (SceneNode child : children) {
            child.markTransformDirty();
        }
    }
    
    /**
     * Calculates the depth of this node in the scene graph hierarchy.
     * <p>
     * A root node has a depth of 0.
     *
     * @return The depth of the node.
     */
    public int getDepth() {
        int depth = 0;
        SceneNode current = parent;
        while (current != null) {
            depth++;
            current = current.parent;
        }
        return depth;
    }
    
    /**
     * Checks if this node is an ancestor of another specified node.
     *
     * @param node The potential descendant node.
     * @return {@code true} if this node is an ancestor of the given node,
     *         {@code false} otherwise.
     */
    public boolean isAncestorOf(SceneNode node) {
        SceneNode current = node.parent;
        while (current != null) {
            if (current == this) {
                return true;
            }
            current = current.parent;
        }
        return false;
    }
    
    /**
     * Traverses the subtree rooted at this node in a depth-first order, applying
     * a {@link NodeVisitor} to each node.
     *
     * @param visitor The visitor to apply to each node.
     */
    public void visitSubtree(NodeVisitor visitor) {
        visitor.visit(this);
        for (SceneNode child : children) {
            child.visitSubtree(visitor);
        }
    }
    
    /**
     * Traverses the subtree rooted at this node in a breadth-first order, applying
     * a {@link NodeVisitor} to each node.
     *
     * @param visitor The visitor to apply to each node.
     */
    public void visitSubtreeBreadthFirst(NodeVisitor visitor) {
        Queue<SceneNode> queue = new LinkedList<>();
        queue.add(this);
        
        while (!queue.isEmpty()) {
            SceneNode current = queue.poll();
            visitor.visit(current);
            queue.addAll(current.children);
        }
    }
    
    /**
     * Converts this scene node and its entire subtree into an LLSD map representation.
     *
     * @return A {@link Map} that represents the hierarchical structure and properties
     *         of this node and its descendants.
     */
    public Map<String, Object> toLLSD() {
        Map<String, Object> nodeData = new HashMap<>();
        nodeData.put("NodeID", nodeId);
        nodeData.put("Name", name);
        nodeData.put("Position", Arrays.asList(position.x, position.y, position.z));
        nodeData.put("Rotation", Arrays.asList(rotation.x, rotation.y, rotation.z, rotation.w));
        nodeData.put("Scale", Arrays.asList(scale.x, scale.y, scale.z));
        nodeData.put("Visible", visible);
        nodeData.put("Enabled", enabled);
        nodeData.put("Properties", new HashMap<>(properties));
        
        List<Map<String, Object>> childrenData = new ArrayList<>();
        for (SceneNode child : children) {
            childrenData.add(child.toLLSD());
        }
        nodeData.put("Children", childrenData);
        
        return nodeData;
    }
    
    /**
     * Disposes of this node, removing it from its parent and unregistering it
     * from the global node registry. This should be called to ensure proper cleanup.
     */
    public void dispose() {
        // Remove from parent
        removeFromParent();
        
        // Dispose all children
        for (SceneNode child : new ArrayList<>(children)) {
            child.dispose();
        }
        
        // Unregister
        nodeRegistry.remove(nodeId);
    }
    
    // Matrix utility methods
    private static double[][] createTranslationMatrix(Vector3 translation) {
        return new double[][]{
            {1, 0, 0, translation.x},
            {0, 1, 0, translation.y},
            {0, 0, 1, translation.z},
            {0, 0, 0, 1}
        };
    }
    
    private static double[][] createScaleMatrix(Vector3 scale) {
        return new double[][]{
            {scale.x, 0, 0, 0},
            {0, scale.y, 0, 0},
            {0, 0, scale.z, 0},
            {0, 0, 0, 1}
        };
    }
    
    private static double[][] create4x4Matrix(double[][] matrix3) {
        return new double[][]{
            {matrix3[0][0], matrix3[0][1], matrix3[0][2], 0},
            {matrix3[1][0], matrix3[1][1], matrix3[1][2], 0},
            {matrix3[2][0], matrix3[2][1], matrix3[2][2], 0},
            {0, 0, 0, 1}
        };
    }
    
    private static double[][] multiplyMatrices(double[][] a, double[][] b) {
        double[][] result = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = a[i][0] * b[0][j] + a[i][1] * b[1][j] + 
                               a[i][2] * b[2][j] + a[i][3] * b[3][j];
            }
        }
        return result;
    }
    
    private static Vector3 multiplyMatrixVector(double[][] matrix, Vector3 vector) {
        double x = matrix[0][0] * vector.x + matrix[0][1] * vector.y + 
                   matrix[0][2] * vector.z + matrix[0][3];
        double y = matrix[1][0] * vector.x + matrix[1][1] * vector.y + 
                   matrix[1][2] * vector.z + matrix[1][3];
        double z = matrix[2][0] * vector.x + matrix[2][1] * vector.y + 
                   matrix[2][2] * vector.z + matrix[2][3];
        return new Vector3(x, y, z);
    }
    
    private static double[][] cloneMatrix(double[][] matrix) {
        double[][] clone = new double[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            clone[i] = matrix[i].clone();
        }
        return clone;
    }
    
    private static double[][] invertMatrix(double[][] matrix) {
        // Simple 4x4 matrix inversion (for transforms only)
        // This is a simplified version - full implementation would be more robust
        double[][] result = new double[4][4];
        
        // Extract translation
        Vector3 translation = new Vector3(-matrix[0][3], -matrix[1][3], -matrix[2][3]);
        
        // Transpose rotation part
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result[i][j] = matrix[j][i];
            }
        }
        
        // Transform translation
        result[0][3] = result[0][0] * translation.x + result[0][1] * translation.y + result[0][2] * translation.z;
        result[1][3] = result[1][0] * translation.x + result[1][1] * translation.y + result[1][2] * translation.z;
        result[2][3] = result[2][0] * translation.x + result[2][1] * translation.y + result[2][2] * translation.z;
        result[3][3] = 1.0;
        
        return result;
    }
    
    /**
     * Retrieves a {@code SceneNode} from the global registry by its UUID.
     *
     * @param nodeId The UUID of the node to retrieve.
     * @return The {@link SceneNode} if found, otherwise {@code null}.
     */
    public static SceneNode getNode(UUID nodeId) {
        return nodeRegistry.get(nodeId);
    }
    
    /**
     * Gets a collection of all currently registered scene nodes.
     *
     * @return A new {@link Collection} containing all nodes in the registry.
     */
    public static Collection<SceneNode> getAllNodes() {
        return new ArrayList<>(nodeRegistry.values());
    }
    
    /**
     * Clears the global node registry, removing all registered nodes.
     */
    public static void clearRegistry() {
        nodeRegistry.clear();
    }
    
    /**
     * A functional interface for implementing the visitor pattern on a scene graph.
     */
    public interface NodeVisitor {
        /**
         * This method is called for each node during a traversal.
         *
         * @param node The node being visited.
         */
        void visit(SceneNode node);
    }
    
    @Override
    public String toString() {
        return String.format("SceneNode[%s](%s) children=%d", 
                           name, nodeId.toString().substring(0, 8), children.size());
    }
}
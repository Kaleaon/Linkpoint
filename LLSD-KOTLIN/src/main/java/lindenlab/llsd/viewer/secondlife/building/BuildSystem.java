/*
 * Building System - Advanced building tools for Second Life viewer
 *
 * Based on Second Life viewer building interface
 * Copyright (C) 2010, Linden Research, Inc.
 * Java implementation Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.building;

import java.util.*;
import java.util.logging.Logger;

/**
 * Comprehensive building system for Second Life viewer.
 * 
 * Provides advanced building tools including prim manipulation,
 * texture editing, alignment tools, and more.
 * 
 * @since 1.0
 */
public class BuildSystem {
    private static final Logger LOGGER = Logger.getLogger(BuildSystem.class.getName());
    
    // Building configuration
    private boolean buildingEnabled = true;
    private boolean gridSnapEnabled = true;
    private float gridSize = 0.5f;
    private BuildMode currentMode = BuildMode.NORMAL;
    private SelectionMode selectionMode = SelectionMode.SINGLE;
    
    // Current selection and clipboard
    private final Set<BuildObject> selectedObjects = new HashSet<>();
    private final List<BuildObject> clipboard = new ArrayList<>();
    private BuildObject rootObject = null;
    
    public BuildSystem() {
        LOGGER.info("Building System initialized");
    }
    
    public enum BuildMode {
        NORMAL, WIREFRAME, HIGHLIGHT, PRECISION, TEXTURE, PHYSICS
    }
    
    public enum SelectionMode {
        SINGLE, MULTIPLE, FACE, LINKED
    }
    
    /**
     * Create a new primitive object
     */
    public BuildObject createPrimitive(PrimitiveType type, Vector3 position, Vector3 scale, Quaternion rotation) {
        if (!buildingEnabled) {
            throw new IllegalStateException("Building is disabled");
        }
        
        BuildObject object = new BuildObject(
            generateObjectId(),
            type,
            position,
            scale,
            rotation
        );
        
        LOGGER.info("Created primitive: " + type + " at " + position);
        return object;
    }
    
    /**
     * Select an object for editing
     */
    public void selectObject(BuildObject object) {
        if (selectionMode == SelectionMode.SINGLE) {
            selectedObjects.clear();
        }
        
        selectedObjects.add(object);
        
        if (selectionMode == SelectionMode.LINKED && object.isLinked()) {
            selectedObjects.addAll(object.getLinkedObjects());
        }
        
        LOGGER.info("Selected object: " + object.getId() + " (Total selected: " + selectedObjects.size() + ")");
    }
    
    /**
     * Move selected objects by offset
     */
    public void moveSelection(Vector3 offset) {
        if (selectedObjects.isEmpty()) {
            return;
        }
        
        for (BuildObject object : selectedObjects) {
            Vector3 newPosition = object.getPosition().add(offset);
            
            if (gridSnapEnabled) {
                newPosition = snapToGrid(newPosition);
            }
            
            object.setPosition(newPosition);
        }
        
        LOGGER.info("Moved " + selectedObjects.size() + " objects by " + offset);
    }
    
    /**
     * Copy selected objects to clipboard
     */
    public void copySelection() {
        if (selectedObjects.isEmpty()) {
            return;
        }
        
        clipboard.clear();
        for (BuildObject object : selectedObjects) {
            clipboard.add(object.clone());
        }
        
        LOGGER.info("Copied " + selectedObjects.size() + " objects to clipboard");
    }
    
    /**
     * Paste objects from clipboard
     */
    public List<BuildObject> pasteFromClipboard(Vector3 offset) {
        if (clipboard.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<BuildObject> pastedObjects = new ArrayList<>();
        
        for (BuildObject object : clipboard) {
            BuildObject newObject = object.clone();
            newObject.setId(generateObjectId());
            newObject.setPosition(object.getPosition().add(offset));
            
            pastedObjects.add(newObject);
        }
        
        LOGGER.info("Pasted " + pastedObjects.size() + " objects");
        return pastedObjects;
    }
    
    /**
     * Apply texture to selected objects
     */
    public void applyTexture(String textureId, int face) {
        if (selectedObjects.isEmpty()) {
            return;
        }
        
        for (BuildObject object : selectedObjects) {
            if (face == -1) {
                for (int i = 0; i < object.getFaceCount(); i++) {
                    object.setTexture(i, textureId);
                }
            } else {
                object.setTexture(face, textureId);
            }
        }
        
        LOGGER.info("Applied texture " + textureId + " to " + selectedObjects.size() + " objects");
    }
    
    private Vector3 snapToGrid(Vector3 position) {
        if (!gridSnapEnabled) {
            return position;
        }
        
        float x = Math.round(position.x / gridSize) * gridSize;
        float y = Math.round(position.y / gridSize) * gridSize;
        float z = Math.round(position.z / gridSize) * gridSize;
        
        return new Vector3(x, y, z);
    }
    
    private String generateObjectId() {
        return "obj_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }
    
    // Getters and setters
    public boolean isBuildingEnabled() { return buildingEnabled; }
    public void setBuildingEnabled(boolean enabled) { this.buildingEnabled = enabled; }
    
    public boolean isGridSnapEnabled() { return gridSnapEnabled; }
    public void setGridSnapEnabled(boolean enabled) { this.gridSnapEnabled = enabled; }
    
    public float getGridSize() { return gridSize; }
    public void setGridSize(float size) { this.gridSize = Math.max(0.1f, size); }
    
    public BuildMode getCurrentMode() { return currentMode; }
    public void setCurrentMode(BuildMode mode) { this.currentMode = mode; }
    
    public SelectionMode getSelectionMode() { return selectionMode; }
    public void setSelectionMode(SelectionMode mode) { this.selectionMode = mode; }
    
    public Set<BuildObject> getSelectedObjects() { return new HashSet<>(selectedObjects); }
    public int getSelectedCount() { return selectedObjects.size(); }
    public boolean hasSelection() { return !selectedObjects.isEmpty(); }
}

/**
 * Represents a building object (primitive)
 */
class BuildObject {
    private String id;
    private PrimitiveType type;
    private Vector3 position;
    private Vector3 scale;
    private Quaternion rotation;
    private final Map<Integer, String> textures = new HashMap<>();
    private final List<BuildObject> linkedChildren = new ArrayList<>();
    private BuildObject parent = null;
    
    public BuildObject(String id, PrimitiveType type, Vector3 position, Vector3 scale, Quaternion rotation) {
        this.id = id;
        this.type = type;
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
        
        // Initialize default textures
        int faceCount = getFaceCount();
        for (int i = 0; i < faceCount; i++) {
            textures.put(i, "DEFAULT_TEXTURE");
        }
    }
    
    public int getFaceCount() {
        switch (type) {
            case BOX: return 6;
            case CYLINDER: return 3;
            case PRISM: return 5;
            case SPHERE: return 1;
            case TORUS: return 1;
            default: return 1;
        }
    }
    
    public void linkChild(BuildObject child) {
        if (child.parent != null) {
            child.parent.linkedChildren.remove(child);
        }
        
        linkedChildren.add(child);
        child.parent = this;
    }
    
    public List<BuildObject> getLinkedObjects() {
        List<BuildObject> linked = new ArrayList<>();
        linked.add(this);
        linked.addAll(linkedChildren);
        return linked;
    }
    
    public boolean isLinked() {
        return parent != null || !linkedChildren.isEmpty();
    }
    
    public BuildObject clone() {
        BuildObject clone = new BuildObject(id + "_clone", type, position, scale, rotation);
        clone.textures.putAll(textures);
        return clone;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public PrimitiveType getType() { return type; }
    public Vector3 getPosition() { return position; }
    public void setPosition(Vector3 position) { this.position = position; }
    
    public Vector3 getScale() { return scale; }
    public void setScale(Vector3 scale) { this.scale = scale; }
    
    public Quaternion getRotation() { return rotation; }
    public void setRotation(Quaternion rotation) { this.rotation = rotation; }
    
    public void setTexture(int face, String textureId) {
        textures.put(face, textureId);
    }
    
    public String getTexture(int face) {
        return textures.get(face);
    }
}

/**
 * Primitive types available for building
 */
enum PrimitiveType {
    BOX, CYLINDER, PRISM, SPHERE, TORUS, TUBE, RING, SCULPT, MESH
}

// Supporting math classes
class Vector3 {
    public final float x, y, z;
    
    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3 add(Vector3 other) {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }
    
    public Vector3 multiply(Vector3 other) {
        return new Vector3(x * other.x, y * other.y, z * other.z);
    }
    
    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }
    
    @Override
    public String toString() {
        return String.format("<%f, %f, %f>", x, y, z);
    }
}

class Quaternion {
    public final float x, y, z, w;
    
    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Quaternion multiply(Quaternion other) {
        return new Quaternion(
            w * other.x + x * other.w + y * other.z - z * other.y,
            w * other.y - x * other.z + y * other.w + z * other.x,
            w * other.z + x * other.y - y * other.x + z * other.w,
            w * other.w - x * other.x - y * other.y - z * other.z
        );
    }
}
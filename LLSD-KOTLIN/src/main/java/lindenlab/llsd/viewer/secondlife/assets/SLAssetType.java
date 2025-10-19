/*
 * Second Life Asset Types - Java implementation of SL asset type constants
 *
 * Based on Second Life viewer implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.assets;

/**
 * A utility class that defines constants and helper methods related to Second
 * Life asset types.
 * <p>
 * This class provides integer constants for all standard asset types found in
 * the Second Life viewer, from textures and sounds to inventory items and mesh.
 * It also includes utility methods for classifying asset types (e.g., checking
 * if a type is a texture) and for getting human-readable names and MIME types.
 * <p>
 * As a utility class containing only constants and static methods, it is final
 * and cannot be instantiated.
 */
public final class SLAssetType {
    
    // Standard Second Life asset types
    public static final int TEXTURE = 0;
    public static final int SOUND = 1;
    public static final int CALLINGCARD = 2;
    public static final int LANDMARK = 3;
    public static final int SCRIPT = 4;
    public static final int CLOTHING = 5;
    public static final int OBJECT = 6;
    public static final int NOTECARD = 7;
    public static final int CATEGORY = 8;
    public static final int ROOT_CATEGORY = 9;
    public static final int LSL_TEXT = 10;
    public static final int LSL_BYTECODE = 11;
    public static final int TEXTURE_TGA = 12;
    public static final int BODYPART = 13;
    public static final int TRASH = 14;
    public static final int SNAPSHOT_CATEGORY = 15;
    public static final int LOST_AND_FOUND = 16;
    public static final int SOUND_WAV = 17;
    public static final int IMAGE_TGA = 18;
    public static final int IMAGE_JPEG = 19;
    public static final int ANIMATION = 20;
    public static final int GESTURE = 21;
    public static final int SIMSTATE = 22;
    public static final int FAVORITE = 23;
    public static final int LINK = 24;
    public static final int LINK_FOLDER = 25;
    public static final int MARKETPLACE_FOLDER = 26;
    public static final int MESH = 49;
    
    // Extended asset types for streaming
    public static final int AUDIO_STREAM = 100;
    public static final int VIDEO_STREAM = 101;
    public static final int TEXTURE_STREAM = 102;
    public static final int MODEL_STREAM = 103;
    
    private SLAssetType() {
        // Utility class - no instances
    }
    
    /**
     * Checks if a given asset type code corresponds to a texture type.
     *
     * @param assetType The integer asset type code to check.
     * @return {@code true} if the asset type is a texture (e.g., TEXTURE,
     *         IMAGE_JPEG), {@code false} otherwise.
     */
    public static boolean isTextureType(int assetType) {
        return assetType == TEXTURE || 
               assetType == TEXTURE_TGA || 
               assetType == IMAGE_TGA || 
               assetType == IMAGE_JPEG ||
               assetType == TEXTURE_STREAM;
    }
    
    /**
     * Checks if a given asset type code corresponds to a sound type.
     *
     * @param assetType The integer asset type code to check.
     * @return {@code true} if the asset type is a sound (e.g., SOUND, SOUND_WAV),
     *         {@code false} otherwise.
     */
    public static boolean isSoundType(int assetType) {
        return assetType == SOUND || 
               assetType == SOUND_WAV ||
               assetType == AUDIO_STREAM;
    }
    
    /**
     * Checks if a given asset type code corresponds to a streaming type.
     *
     * @param assetType The integer asset type code to check.
     * @return {@code true} if the asset is a stream type (e.g., AUDIO_STREAM,
     *         TEXTURE_STREAM), {@code false} otherwise.
     */
    public static boolean isStreamType(int assetType) {
        return assetType >= AUDIO_STREAM && assetType <= MODEL_STREAM;
    }
    
    /**
     * Gets the human-readable name for a given asset type code.
     *
     * @param assetType The integer asset type code.
     * @return The corresponding name (e.g., "Texture", "Sound"), or a default
     *         string if the type is unknown.
     */
    public static String getTypeName(int assetType) {
        switch (assetType) {
            case TEXTURE: return "Texture";
            case SOUND: return "Sound";
            case CALLINGCARD: return "Calling Card";
            case LANDMARK: return "Landmark";
            case SCRIPT: return "Script";
            case CLOTHING: return "Clothing";
            case OBJECT: return "Object";
            case NOTECARD: return "Notecard";
            case CATEGORY: return "Category";
            case ROOT_CATEGORY: return "Root Category";
            case LSL_TEXT: return "LSL Text";
            case LSL_BYTECODE: return "LSL Bytecode";
            case TEXTURE_TGA: return "Texture TGA";
            case BODYPART: return "Body Part";
            case TRASH: return "Trash";
            case SNAPSHOT_CATEGORY: return "Snapshot Category";
            case LOST_AND_FOUND: return "Lost and Found";
            case SOUND_WAV: return "Sound WAV";
            case IMAGE_TGA: return "Image TGA";
            case IMAGE_JPEG: return "Image JPEG";
            case ANIMATION: return "Animation";
            case GESTURE: return "Gesture";
            case SIMSTATE: return "Sim State";
            case FAVORITE: return "Favorite";
            case LINK: return "Link";
            case LINK_FOLDER: return "Link Folder";
            case MARKETPLACE_FOLDER: return "Marketplace Folder";
            case MESH: return "Mesh";
            case AUDIO_STREAM: return "Audio Stream";
            case VIDEO_STREAM: return "Video Stream";
            case TEXTURE_STREAM: return "Texture Stream";
            case MODEL_STREAM: return "Model Stream";
            default: return "Unknown (" + assetType + ")";
        }
    }
    
    /**
     * Gets the most appropriate MIME type for a given asset type code.
     *
     * @param assetType The integer asset type code.
     * @return The corresponding MIME type string (e.g., "image/x-j2c", "audio/wav").
     *         Returns "application/octet-stream" for unknown or generic types.
     */
    public static String getMimeType(int assetType) {
        switch (assetType) {
            case TEXTURE:
            case TEXTURE_STREAM:
                return "image/x-j2c";
            case TEXTURE_TGA:
            case IMAGE_TGA:
                return "image/tga";
            case IMAGE_JPEG:
                return "image/jpeg";
            case SOUND:
            case SOUND_WAV:
            case AUDIO_STREAM:
                return "audio/wav";
            case NOTECARD:
                return "text/plain";
            case LSL_TEXT:
                return "text/lsl";
            case SCRIPT:
            case LSL_BYTECODE:
                return "application/octet-stream";
            case ANIMATION:
                return "application/x-anim";
            case GESTURE:
                return "application/x-gesture";
            case MESH:
            case MODEL_STREAM:
                return "application/vnd.ll.mesh";
            default:
                return "application/octet-stream";
        }
    }
}
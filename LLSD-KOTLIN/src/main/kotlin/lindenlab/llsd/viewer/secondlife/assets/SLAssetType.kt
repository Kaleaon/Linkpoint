/*
 * Second Life Asset Types - Java implementation of SL asset type constants
 *
 * Based on Second Life viewer implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * Kotlin conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.assets

/**
 * A utility object that defines constants and helper methods related to Second
 * Life asset types.
 *
 * This object provides integer constants for all standard asset types found in
 * the Second Life viewer, from textures and sounds to inventory items and mesh.
 * It also includes utility methods for classifying asset types (e.g., checking
 * if a type is a texture) and for getting human-readable names and MIME types.
 */
object SLAssetType {
    
    // Standard Second Life asset types
    const val TEXTURE = 0
    const val SOUND = 1
    const val CALLINGCARD = 2
    const val LANDMARK = 3
    const val SCRIPT = 4
    const val CLOTHING = 5
    const val OBJECT = 6
    const val NOTECARD = 7
    const val CATEGORY = 8
    const val ROOT_CATEGORY = 9
    const val LSL_TEXT = 10
    const val LSL_BYTECODE = 11
    const val TEXTURE_TGA = 12
    const val BODYPART = 13
    const val TRASH = 14
    const val SNAPSHOT_CATEGORY = 15
    const val LOST_AND_FOUND = 16
    const val SOUND_WAV = 17
    const val IMAGE_TGA = 18
    const val IMAGE_JPEG = 19
    const val ANIMATION = 20
    const val GESTURE = 21
    const val SIMSTATE = 22
    const val FAVORITE = 23
    const val LINK = 24
    const val LINK_FOLDER = 25
    const val MARKETPLACE_FOLDER = 26
    const val MESH = 49
    
    // Extended asset types for streaming
    const val AUDIO_STREAM = 100
    const val VIDEO_STREAM = 101
    const val TEXTURE_STREAM = 102
    const val MODEL_STREAM = 103
    
    /**
     * Checks if a given asset type code corresponds to a texture type.
     *
     * @param assetType The integer asset type code to check.
     * @return `true` if the asset type is a texture (e.g., TEXTURE,
     *         IMAGE_JPEG), `false` otherwise.
     */
    @JvmStatic
    fun isTextureType(assetType: Int): Boolean {
        return assetType == TEXTURE || 
               assetType == TEXTURE_TGA || 
               assetType == IMAGE_TGA || 
               assetType == IMAGE_JPEG ||
               assetType == TEXTURE_STREAM
    }
    
    /**
     * Checks if a given asset type code corresponds to a sound type.
     *
     * @param assetType The integer asset type code to check.
     * @return `true` if the asset type is a sound (e.g., SOUND, SOUND_WAV),
     *         `false` otherwise.
     */
    @JvmStatic
    fun isSoundType(assetType: Int): Boolean {
        return assetType == SOUND || 
               assetType == SOUND_WAV ||
               assetType == AUDIO_STREAM
    }
    
    /**
     * Checks if a given asset type code corresponds to a streaming type.
     *
     * @param assetType The integer asset type code to check.
     * @return `true` if the asset is a stream type (e.g., AUDIO_STREAM,
     *         TEXTURE_STREAM), `false` otherwise.
     */
    @JvmStatic
    fun isStreamType(assetType: Int): Boolean {
        return assetType in AUDIO_STREAM..MODEL_STREAM
    }
    
    /**
     * Gets the human-readable name for a given asset type code.
     *
     * @param assetType The integer asset type code.
     * @return The corresponding name (e.g., "Texture", "Sound"), or a default
     *         string if the type is unknown.
     */
    @JvmStatic
    fun getTypeName(assetType: Int): String {
        return when (assetType) {
            TEXTURE -> "Texture"
            SOUND -> "Sound"
            CALLINGCARD -> "Calling Card"
            LANDMARK -> "Landmark"
            SCRIPT -> "Script"
            CLOTHING -> "Clothing"
            OBJECT -> "Object"
            NOTECARD -> "Notecard"
            CATEGORY -> "Category"
            ROOT_CATEGORY -> "Root Category"
            LSL_TEXT -> "LSL Text"
            LSL_BYTECODE -> "LSL Bytecode"
            TEXTURE_TGA -> "Texture TGA"
            BODYPART -> "Body Part"
            TRASH -> "Trash"
            SNAPSHOT_CATEGORY -> "Snapshot Category"
            LOST_AND_FOUND -> "Lost and Found"
            SOUND_WAV -> "Sound WAV"
            IMAGE_TGA -> "Image TGA"
            IMAGE_JPEG -> "Image JPEG"
            ANIMATION -> "Animation"
            GESTURE -> "Gesture"
            SIMSTATE -> "Sim State"
            FAVORITE -> "Favorite"
            LINK -> "Link"
            LINK_FOLDER -> "Link Folder"
            MARKETPLACE_FOLDER -> "Marketplace Folder"
            MESH -> "Mesh"
            AUDIO_STREAM -> "Audio Stream"
            VIDEO_STREAM -> "Video Stream"
            TEXTURE_STREAM -> "Texture Stream"
            MODEL_STREAM -> "Model Stream"
            else -> "Unknown ($assetType)"
        }
    }
    
    /**
     * Gets the most appropriate MIME type for a given asset type code.
     *
     * @param assetType The integer asset type code.
     * @return The corresponding MIME type string (e.g., "image/x-j2c", "audio/wav").
     *         Returns "application/octet-stream" for unknown or generic types.
     */
    @JvmStatic
    fun getMimeType(assetType: Int): String {
        return when (assetType) {
            TEXTURE, TEXTURE_STREAM -> "image/x-j2c"
            TEXTURE_TGA, IMAGE_TGA -> "image/tga"
            IMAGE_JPEG -> "image/jpeg"
            SOUND, SOUND_WAV, AUDIO_STREAM -> "audio/wav"
            NOTECARD -> "text/plain"
            LSL_TEXT -> "text/lsl"
            SCRIPT, LSL_BYTECODE -> "application/octet-stream"
            ANIMATION -> "application/x-anim"
            GESTURE -> "application/x-gesture"
            MESH, MODEL_STREAM -> "application/vnd.ll.mesh"
            else -> "application/octet-stream"
        }
    }
}

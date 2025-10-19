/*
 * Second Life LLSD Extensions - Java implementation of SL-specific LLSD functionality
 *
 * Based on Second Life viewer implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife;

import lindenlab.llsd.LLSD;
import lindenlab.llsd.LLSDException;
import lindenlab.llsd.viewer.LLSDViewerUtils;
import lindenlab.llsd.viewer.secondlife.assets.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;

/**
 * A utility class providing LLSD (Linden Lab Structured Data) functionality
 * specific to the Second Life platform.
 * <p>
 * This class encapsulates methods for creating and validating LLSD structures
 * that are commonly used in Second Life protocols, such as agent updates, chat
 * messages, and asset data. It provides a higher-level API for constructing
 * these complex data types.
 * <p>
 * As a utility class, it is final and cannot be instantiated.
 */
public final class SecondLifeLLSDUtils {
    
    private SecondLifeLLSDUtils() {
        // Utility class - no instances
    }
    
    /**
     * An enumeration of common message types used in Second Life protocols.
     */
    public enum SLMessageType {
        AGENT_UPDATE,
        CHAT_MESSAGE,
        OBJECT_UPDATE,
        PARCEL_INFO,
        INVENTORY_ITEM,
        ASSET_DATA,
        TELEPORT_REQUEST,
        PHYSICS_UPDATE,
        ANIMATION_DATA,
        TEXTURE_REQUEST
    }
    
    /**
     * Creates an LLSD map representing a Second Life agent's data.
     *
     * @param agentId  The UUID of the agent.
     * @param position A 3-element array for the agent's position (x, y, z).
     * @param rotation A 4-element array for the agent's rotation as a quaternion (x, y, z, w).
     * @param velocity A 3-element array for the agent's velocity (x, y, z).
     * @return A {@link Map} containing the structured agent data.
     * @throws IllegalArgumentException if any of the array arguments have incorrect dimensions.
     */
    public static Map<String, Object> createAgentData(UUID agentId,
                                                      double[] position,
                                                      double[] rotation,
                                                      double[] velocity) {
        if (position.length != 3 || rotation.length != 4 || velocity.length != 3) {
            throw new IllegalArgumentException("Invalid vector dimensions");
        }
        
        Map<String, Object> agentData = new HashMap<>();
        agentData.put("AgentID", agentId);
        agentData.put("Position", Arrays.stream(position).boxed().collect(Collectors.toList()));
        agentData.put("Rotation", Arrays.stream(rotation).boxed().collect(Collectors.toList()));
        agentData.put("Velocity", Arrays.stream(velocity).boxed().collect(Collectors.toList()));
        agentData.put("Timestamp", System.currentTimeMillis() / 1000.0);
        
        return agentData;
    }
    
    /**
     * Creates an LLSD map representing a Second Life in-world object.
     *
     * @param objectId The UUID of the object.
     * @param parentId The UUID of the object's parent (can be null for root objects).
     * @param position A 3-element array for the object's position.
     * @param rotation A 4-element array for the object's rotation (quaternion).
     * @param scale    A 3-element array for the object's scale.
     * @param material A map containing the object's material properties.
     * @return A {@link Map} containing the structured object data.
     * @throws IllegalArgumentException if vector arrays have incorrect dimensions.
     */
    public static Map<String, Object> createObjectData(UUID objectId,
                                                       UUID parentId,
                                                       double[] position,
                                                       double[] rotation,
                                                       double[] scale,
                                                       Map<String, Object> material) {
        if (position.length != 3 || rotation.length != 4 || scale.length != 3) {
            throw new IllegalArgumentException("Invalid vector dimensions");
        }
        
        Map<String, Object> objectData = new HashMap<>();
        objectData.put("ObjectID", objectId);
        objectData.put("ParentID", parentId);
        objectData.put("Position", Arrays.stream(position).boxed().collect(Collectors.toList()));
        objectData.put("Rotation", Arrays.stream(rotation).boxed().collect(Collectors.toList()));
        objectData.put("Scale", Arrays.stream(scale).boxed().collect(Collectors.toList()));
        
        if (material != null) {
            objectData.put("Material", material);
        }
        
        return objectData;
    }
    
    /**
     * Creates an LLSD map representing data for a parcel of land in Second Life.
     *
     * @param parcelId    The UUID of the parcel.
     * @param name        The name of the parcel.
     * @param description A description of the parcel.
     * @param area        The area of the parcel in square meters.
     * @param ownerId     The UUID of the parcel's owner.
     * @param groupId     The UUID of the group the parcel is set to (can be null).
     * @param flags       An integer representing the parcel's flags.
     * @return A {@link Map} containing the structured parcel data.
     */
    public static Map<String, Object> createParcelData(UUID parcelId,
                                                       String name,
                                                       String description,
                                                       int area,
                                                       UUID ownerId,
                                                       UUID groupId,
                                                       int flags) {
        Map<String, Object> parcelData = new HashMap<>();
        parcelData.put("ParcelID", parcelId);
        parcelData.put("Name", name != null ? name : "");
        parcelData.put("Description", description != null ? description : "");
        parcelData.put("Area", area);
        parcelData.put("OwnerID", ownerId);
        parcelData.put("GroupID", groupId);
        parcelData.put("Flags", flags);
        
        return parcelData;
    }
    
    /**
     * Creates an LLSD map representing an item in a Second Life inventory.
     *
     * @param itemId      The UUID of the inventory item.
     * @param parentId    The UUID of the parent folder.
     * @param name        The name of the item.
     * @param description A description of the item.
     * @param type        An integer representing the inventory item type.
     * @param assetId     The UUID of the underlying asset for this item.
     * @param permissions A map defining the item's permissions.
     * @return A {@link Map} containing the structured inventory item data.
     */
    public static Map<String, Object> createInventoryItem(UUID itemId,
                                                          UUID parentId,
                                                          String name,
                                                          String description,
                                                          int type,
                                                          UUID assetId,
                                                          Map<String, Object> permissions) {
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("ItemID", itemId);
        itemData.put("ParentID", parentId);
        itemData.put("Name", name != null ? name : "");
        itemData.put("Description", description != null ? description : "");
        itemData.put("Type", type);
        itemData.put("AssetID", assetId);
        
        if (permissions != null) {
            itemData.put("Permissions", permissions);
        }
        
        itemData.put("CreationDate", new Date());
        
        return itemData;
    }
    
    /**
     * Creates an LLSD map representing an in-world chat message.
     *
     * @param fromId   The UUID of the message sender.
     * @param fromName The name of the sender.
     * @param message  The text of the message.
     * @param channel  The chat channel the message was sent on.
     * @param type     The type of chat (e.g., 0 for say, 1 for shout).
     * @param position A 3-element array of the sender's position.
     * @return A {@link Map} containing the structured chat message.
     */
    public static Map<String, Object> createChatMessage(UUID fromId,
                                                        String fromName,
                                                        String message,
                                                        int channel,
                                                        int type,
                                                        double[] position) {
        Map<String, Object> chatData = new HashMap<>();
        chatData.put("FromID", fromId);
        chatData.put("FromName", fromName != null ? fromName : "");
        chatData.put("Message", message != null ? message : "");
        chatData.put("Channel", channel);
        chatData.put("Type", type);
        chatData.put("Timestamp", System.currentTimeMillis() / 1000.0);
        
        if (position != null && position.length == 3) {
            chatData.put("Position", Arrays.stream(position).boxed().collect(Collectors.toList()));
        }
        
        return chatData;
    }
    
    /**
     * Creates an LLSD map representing a Second Life asset.
     *
     * @param assetId     The UUID of the asset.
     * @param type        An integer representing the asset type.
     * @param name        The name of the asset.
     * @param description A description of the asset.
     * @param data        The raw binary data of the asset.
     * @param temporary   {@code true} if the asset is temporary.
     * @return A {@link Map} containing the structured asset data.
     */
    public static Map<String, Object> createAssetData(UUID assetId,
                                                      int type,
                                                      String name,
                                                      String description,
                                                      byte[] data,
                                                      boolean temporary) {
        Map<String, Object> assetData = new HashMap<>();
        assetData.put("AssetID", assetId);
        assetData.put("Type", type);
        assetData.put("Name", name != null ? name : "");
        assetData.put("Description", description != null ? description : "");
        assetData.put("Data", data != null ? data : new byte[0]);
        assetData.put("Temporary", temporary);
        assetData.put("CreationDate", new Date());
        
        return assetData;
    }
    
    /**
     * Parses a generic Second Life message structure from an LLSD object.
     * <p>
     * This method validates that the input is a map and contains a "MessageType"
     * field that matches the expected type.
     *
     * @param llsdData     The LLSD object to parse.
     * @param expectedType The expected {@link SLMessageType}.
     * @return The parsed message as a {@link Map}.
     * @throws LLSDException if the LLSD data is not a valid SL message of the expected type.
     */
    public static Map<String, Object> parseSLMessage(Object llsdData, SLMessageType expectedType)
            throws LLSDException {
        if (!(llsdData instanceof Map)) {
            throw new LLSDException("SL message must be a map");
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> messageMap = (Map<String, Object>) llsdData;
        
        // Validate common SL message structure
        if (!messageMap.containsKey("MessageType")) {
            throw new LLSDException("SL message missing MessageType");
        }
        
        String messageType = messageMap.get("MessageType").toString();
        if (!messageType.equals(expectedType.name())) {
            throw new LLSDException("Expected " + expectedType + " but got " + messageType);
        }
        
        // Validate timestamp if present
        if (messageMap.containsKey("Timestamp")) {
            Object timestamp = messageMap.get("Timestamp");
            if (!(timestamp instanceof Number)) {
                throw new LLSDException("Invalid timestamp format");
            }
        }
        
        return messageMap;
    }
    
    /**
     * Validates a UUID to ensure it is a valid, non-null Second Life UUID.
     *
     * @param uuid The UUID to validate.
     * @return {@code true} if the UUID is not null and not the null UUID
     *         (0000...-0000), {@code false} otherwise.
     */
    public static boolean isValidSLUUID(UUID uuid) {
        if (uuid == null) {
            return false;
        }
        
        // SL UUIDs should not be null UUID
        return !uuid.equals(new UUID(0L, 0L));
    }
    
    /**
     * Creates a standard LLSD response map, commonly used in Second Life protocols.
     *
     * @param success {@code true} if the operation was successful, {@code false} otherwise.
     * @param message A descriptive message about the outcome of the operation.
     * @param data    An optional LLSD object containing additional data for the response.
     * @return A {@link Map} containing the structured response.
     */
    public static Map<String, Object> createSLResponse(boolean success, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("Success", success);
        response.put("Message", message != null ? message : "");
        response.put("Timestamp", System.currentTimeMillis() / 1000.0);
        
        if (data != null) {
            response.put("Data", data);
        }
        
        return response;
    }
    
    /**
     * Validates an LLSD data structure against a set of predefined rules.
     *
     * @param llsdData The LLSD object to be validated.
     * @param rules    The {@link SLValidationRules} to apply.
     * @return A {@link ValidationResult} object containing any errors found.
     */
    public static ValidationResult validateSLStructure(Object llsdData, SLValidationRules rules) {
        ValidationResult result = new ValidationResult();
        
        if (rules.requiresMap && !(llsdData instanceof Map)) {
            result.addError("Expected Map but got " + (llsdData != null ? llsdData.getClass().getSimpleName() : "null"));
            return result;
        }
        
        if (rules.requiresArray && !(llsdData instanceof List)) {
            result.addError("Expected Array but got " + (llsdData != null ? llsdData.getClass().getSimpleName() : "null"));
            return result;
        }
        
        if (llsdData instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> mapData = (Map<String, Object>) llsdData;
            
            // Check required fields
            for (String required : rules.requiredFields) {
                if (!mapData.containsKey(required)) {
                    result.addError("Missing required field: " + required);
                }
            }
            
            // Check field types
            for (Map.Entry<String, Class<?>> typeCheck : rules.fieldTypes.entrySet()) {
                String field = typeCheck.getKey();
                Class<?> expectedType = typeCheck.getValue();
                
                if (mapData.containsKey(field)) {
                    Object value = mapData.get(field);
                    if (value != null && !expectedType.isAssignableFrom(value.getClass())) {
                        result.addError("Field " + field + " expected " + expectedType.getSimpleName() + 
                                       " but got " + value.getClass().getSimpleName());
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * A builder class for defining a set of validation rules for Second Life
     * LLSD structures.
     * <p>
     * It provides a fluent API for specifying whether the root should be a map
     * or array, and for defining required fields and their expected types.
     */
    public static class SLValidationRules {
        public boolean requiresMap = false;
        public boolean requiresArray = false;
        public Set<String> requiredFields = new HashSet<>();
        public Map<String, Class<?>> fieldTypes = new HashMap<>();
        
        public SLValidationRules requireMap() {
            requiresMap = true;
            return this;
        }
        
        public SLValidationRules requireArray() {
            requiresArray = true;
            return this;
        }
        
        public SLValidationRules requireField(String field) {
            requiredFields.add(field);
            return this;
        }
        
        public SLValidationRules requireField(String field, Class<?> type) {
            requiredFields.add(field);
            fieldTypes.put(field, type);
            return this;
        }
    }
    
    /**
     * Creates an LLSD map representing a texture stream, including processed
     * texture information.
     *
     * @param textureId   The UUID of the texture.
     * @param textureData The raw binary data of the texture.
     * @param format      The format of the texture (e.g., J2C, TGA).
     * @return A {@link Map} containing the structured texture stream data.
     * @throws LLSDException if processing the texture data fails.
     */
    public static Map<String, Object> createTextureStream(UUID textureId,
                                                          byte[] textureData,
                                                          SLTextureProcessor.TextureFormat format)
            throws LLSDException {
        SLTextureProcessor.TextureInfo info = SLTextureProcessor.processTexture(textureId, textureData, format);
        return SLTextureProcessor.createTextureStreamData(textureId, info, textureData);
    }
    
    /**
     * Creates an LLSD map representing a sound stream, including processed audio info.
     *
     * @param soundId   The UUID of the sound asset.
     * @param soundData The raw binary data of the sound.
     * @param format    The format of the audio (e.g., WAV, OGG).
     * @return A {@link Map} containing the structured sound stream data.
     * @throws LLSDException if processing the sound data fails.
     */
    public static Map<String, Object> createSoundStream(UUID soundId,
                                                        byte[] soundData,
                                                        SLSoundProcessor.AudioFormat format)
            throws LLSDException {
        SLSoundProcessor.AudioInfo info = SLSoundProcessor.processSound(soundId, soundData, format);
        return SLSoundProcessor.createSoundStreamData(soundId, info, soundData);
    }
    
    /**
     * Creates an LLSD map representing a generic data stream for asset transfer.
     * <p>
     * This method processes the data, optionally compresses it, splits it into
     * chunks, and packages it into a standard LLSD stream structure.
     *
     * @param assetId           The UUID of the asset.
     * @param assetType         The type of the asset.
     * @param assetData         The raw binary data of the asset.
     * @param enableCompression {@code true} to compress the data before chunking.
     * @return A {@link Map} containing the structured data stream.
     * @throws LLSDException if processing the data fails.
     */
    public static Map<String, Object> createDataStream(UUID assetId,
                                                       int assetType,
                                                       byte[] assetData,
                                                       boolean enableCompression)
            throws LLSDException {
        try {
            SLDataStreamProcessor.DataStreamInfo info = SLDataStreamProcessor.processDataStream(
                assetId, assetType, assetData, enableCompression);
            
            List<SLDataStreamProcessor.StreamChunk> chunks = SLDataStreamProcessor.createStreamChunks(
                assetData, info.getCompression());
            
            return SLDataStreamProcessor.createDataStreamLLSD(info, chunks);
        } catch (Exception e) {
            throw new LLSDException("Failed to create data stream: " + e.getMessage(), e);
        }
    }
    
    /**
     * Processes an incoming LLSD asset stream from Second Life.
     * <p>
     * This method identifies the asset type from the stream data and delegates
     * to the appropriate processor (e.g., for textures or sounds) to validate
     * and extract information from the data.
     *
     * @param streamData The LLSD map representing the incoming stream.
     * @return A map containing the processed asset information and validation status.
     * @throws LLSDException if the stream data is malformed or processing fails.
     */
    public static Map<String, Object> processIncomingStream(Map<String, Object> streamData)
            throws LLSDException {
        if (!streamData.containsKey("AssetType")) {
            throw new LLSDException("Missing AssetType in stream data");
        }
        
        int assetType = (Integer) streamData.get("AssetType");
        UUID assetId = UUID.fromString(streamData.get("AssetID").toString());
        byte[] data = (byte[]) streamData.get("Data");
        
        Map<String, Object> result = new HashMap<>();
        result.put("AssetID", assetId);
        result.put("AssetType", assetType);
        result.put("ProcessedAt", System.currentTimeMillis() / 1000.0);
        
        if (SLAssetType.isTextureType(assetType)) {
            SLTextureProcessor.TextureFormat format = SLTextureProcessor.detectTextureFormat(data);
            SLTextureProcessor.TextureInfo info = SLTextureProcessor.processTexture(assetId, data, format);
            result.put("TextureInfo", createTextureInfoMap(info));
            result.put("Valid", info.isValid());
        } else if (SLAssetType.isSoundType(assetType)) {
            SLSoundProcessor.AudioFormat format = SLSoundProcessor.detectAudioFormat(data);
            SLSoundProcessor.AudioInfo info = SLSoundProcessor.processSound(assetId, data, format);
            result.put("AudioInfo", createAudioInfoMap(info));
            result.put("Valid", info.isValid());
        } else {
            SLDataStreamProcessor.DataStreamInfo info = SLDataStreamProcessor.processDataStream(
                assetId, assetType, data, true);
            result.put("StreamInfo", createStreamInfoMap(info));
            result.put("Valid", info.isValid());
        }
        
        return result;
    }
    
    /**
     * Create texture info map for LLSD.
     */
    private static Map<String, Object> createTextureInfoMap(SLTextureProcessor.TextureInfo info) {
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("Format", info.getFormat().name());
        infoMap.put("Width", info.getWidth());
        infoMap.put("Height", info.getHeight());
        infoMap.put("Levels", info.getLevels());
        infoMap.put("Size", info.getSize());
        infoMap.put("HasAlpha", info.hasAlpha());
        return infoMap;
    }
    
    /**
     * Create audio info map for LLSD.
     */
    private static Map<String, Object> createAudioInfoMap(SLSoundProcessor.AudioInfo info) {
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("Format", info.getFormat().name());
        infoMap.put("SampleRate", info.getSampleRate());
        infoMap.put("Channels", info.getChannels());
        infoMap.put("BitsPerSample", info.getBitsPerSample());
        infoMap.put("Duration", info.getDuration());
        infoMap.put("Size", info.getSize());
        infoMap.put("Compressed", info.isCompressed());
        return infoMap;
    }
    
    /**
     * Create stream info map for LLSD.
     */
    private static Map<String, Object> createStreamInfoMap(SLDataStreamProcessor.DataStreamInfo info) {
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("TotalSize", info.getTotalSize());
        infoMap.put("CompressedSize", info.getCompressedSize());
        infoMap.put("Compression", info.getCompression().getName());
        infoMap.put("ChunkCount", info.getChunkCount());
        infoMap.put("CompressionRatio", info.getCompressionRatio());
        infoMap.put("Metadata", info.getMetadata());
        return infoMap;
    }

    /**
     * A class that holds the results of a validation check performed by
     * {@link #validateSLStructure(Object, SLValidationRules)}.
     * <p>
     * It separates validation issues into errors and warnings.
     */
    public static class ValidationResult {
        private final List<String> errors = new ArrayList<>();
        private final List<String> warnings = new ArrayList<>();
        
        public void addError(String error) {
            errors.add(error);
        }
        
        public void addWarning(String warning) {
            warnings.add(warning);
        }
        
        public boolean isValid() {
            return errors.isEmpty();
        }
        
        public List<String> getErrors() {
            return Collections.unmodifiableList(errors);
        }
        
        public List<String> getWarnings() {
            return Collections.unmodifiableList(warnings);
        }
    }
}
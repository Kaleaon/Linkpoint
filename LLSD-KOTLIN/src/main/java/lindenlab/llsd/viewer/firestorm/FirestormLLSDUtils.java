/*
 * Firestorm LLSD Extensions - Java implementation of Firestorm-specific LLSD functionality
 *
 * Based on Firestorm viewer implementation  
 * Copyright (C) 2010, Linden Research, Inc.
 * Firestorm enhancements Copyright (C) The Phoenix Firestorm Project, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.firestorm;

import lindenlab.llsd.LLSD;
import lindenlab.llsd.LLSDException;
import lindenlab.llsd.viewer.secondlife.SecondLifeLLSDUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A utility class providing LLSD (Linden Lab Structured Data) functionality
 * specific to the Firestorm viewer.
 * <p>
 * This class encapsulates methods for creating and validating LLSD structures
 * that are unique to Firestorm, such as those related to RLV (Restrained Life
 * Viewer), radar, performance monitoring, and the viewer bridge. It builds upon
 * the base functionality provided by the standard Second Life LLSD implementation.
 * <p>
 * As a utility class, it is final and cannot be instantiated.
 */
public final class FirestormLLSDUtils {
    
    private FirestormLLSDUtils() {
        // Utility class - no instances
    }
    
    /**
     * An enumeration of message types that are specific to the Firestorm viewer,
     * extending the base set of Second Life message types.
     */
    public enum FSMessageType {
        // RLV related
        RLV_COMMAND,
        RLV_RESPONSE,
        RLV_STATUS,
        
        // Radar and contacts
        RADAR_UPDATE,
        CONTACT_UPDATE,
        FRIEND_STATUS,
        
        // Bridge communication
        BRIDGE_REQUEST,
        BRIDGE_RESPONSE,
        BRIDGE_STATUS,
        
        // Firestorm UI
        FS_PREFERENCE,
        FS_NOTIFICATION,
        FS_FLOATER_DATA,
        
        // Performance monitoring
        PERF_STATS,
        MEMORY_STATS,
        RENDER_STATS,
        
        // Media and streaming
        MEDIA_STATUS,
        STREAM_DATA,
        PARCEL_MEDIA
    }
    
    /**
     * Represents a command for the RLV (Restrained Life Viewer) system.
     * <p>
     * This class encapsulates the components of an RLV command, including the
     * command itself, a parameter, an option, and the source of the command.
     * It provides methods to convert the command to and from its LLSD representation.
     */
    public static class RLVCommand {
        private final String command;
        private final String param;
        private final String option;
        private final UUID sourceId;
        
        /**
         * Constructs a new RLV command.
         *
         * @param command  The RLV command string (e.g., "@sit"). Must not be null or empty.
         * @param param    The command parameter (e.g., "ground"). Can be null.
         * @param option   The command option (e.g., "=force"). Can be null.
         * @param sourceId The UUID of the object that issued the command. Must not be null.
         * @throws IllegalArgumentException if {@code command} or {@code sourceId} is null.
         */
        public RLVCommand(String command, String param, String option, UUID sourceId) {
            if (command == null || command.trim().isEmpty()) {
                throw new IllegalArgumentException("RLV command cannot be null or empty");
            }
            if (sourceId == null) {
                throw new IllegalArgumentException("RLV sourceId cannot be null");
            }
            
            this.command = command.trim();
            this.param = param != null ? param.trim() : "";
            this.option = option != null ? option.trim() : "";
            this.sourceId = sourceId;
        }
        
        /**
         * Gets the RLV command string.
         * @return the command string, never null or empty
         */
        public String getCommand() {
            return command;
        }
        
        /**
         * Gets the RLV command parameter.
         * @return the parameter string, never null but may be empty
         */
        public String getParam() {
            return param;
        }
        
        /**
         * Gets the RLV command option.
         * @return the option string, never null but may be empty
         */
        public String getOption() {
            return option;
        }
        
        /**
         * Gets the source UUID for this command.
         * @return the source UUID, never null
         */
        public UUID getSourceId() {
            return sourceId;
        }
        
        /**
         * Converts this RLV command into its LLSD map representation.
         *
         * @return A {@link Map} representing the RLV command, suitable for serialization.
         */
        public Map<String, Object> toLLSD() {
            Map<String, Object> rlvData = new HashMap<>();
            rlvData.put("Command", command);
            rlvData.put("Parameter", param);
            rlvData.put("Option", option);
            rlvData.put("SourceID", sourceId.toString());
            rlvData.put("Timestamp", System.currentTimeMillis() / 1000.0);
            return rlvData;
        }
        
        /**
         * Parses an {@code RLVCommand} from its LLSD representation.
         *
         * @param llsdData The LLSD object, which must be a {@link Map} containing
         *                 the RLV command fields.
         * @return A new {@code RLVCommand} instance.
         * @throws LLSDException if the provided LLSD data is not a valid representation
         *                       of an RLV command (e.g., missing required fields).
         */
        public static RLVCommand fromLLSD(Object llsdData) throws LLSDException {
            if (llsdData == null) {
                throw new LLSDException("RLV command data cannot be null");
            }
            if (!(llsdData instanceof Map)) {
                throw new LLSDException("RLV command must be a map, got: " + llsdData.getClass().getSimpleName());
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) llsdData;
            
            // Extract command (required)
            Object commandObj = data.get("Command");
            if (commandObj == null) {
                throw new LLSDException("RLV command missing required 'Command' field");
            }
            String command = commandObj.toString();
            
            // Extract parameter (optional)
            String param = data.containsKey("Parameter") ? 
                          data.get("Parameter").toString() : "";
            
            // Extract option (optional)  
            String option = data.containsKey("Option") ? 
                           data.get("Option").toString() : "";
            
            // Extract sourceId (required)
            Object sourceIdObj = data.get("SourceID");
            UUID sourceId;
            if (sourceIdObj == null) {
                throw new LLSDException("RLV command missing required 'SourceID' field");
            }
            
            try {
                if (sourceIdObj instanceof UUID) {
                    sourceId = (UUID) sourceIdObj;
                } else {
                    sourceId = UUID.fromString(sourceIdObj.toString());
                }
            } catch (IllegalArgumentException e) {
                throw new LLSDException("Invalid SourceID format: " + sourceIdObj, e);
            }
            
            return new RLVCommand(command, param, option, sourceId);
        }
    }
    
    /**
     * Creates an LLSD map representing data for the Firestorm radar system.
     *
     * @param agentId     The UUID of the agent being tracked.
     * @param name        The display name of the agent.
     * @param userName    The username of the agent.
     * @param position    A 3-element array representing the agent's position (x, y, z).
     * @param distance    The distance of the agent from the viewer.
     * @param typing      {@code true} if the agent is currently typing.
     * @param attachments A list of maps, where each map represents an attachment.
     * @return A {@link Map} containing the structured radar data.
     * @throws IllegalArgumentException if {@code agentId} is null, {@code distance} is
     *                                  negative, or {@code position} is not a 3-element array.
     */
    public static Map<String, Object> createRadarData(UUID agentId,
                                                      String name,
                                                      String userName,
                                                      double[] position,
                                                      double distance,
                                                      boolean typing,
                                                      List<Map<String, Object>> attachments) {
        if (agentId == null) {
            throw new IllegalArgumentException("Agent ID cannot be null");
        }
        if (distance < 0) {
            throw new IllegalArgumentException("Distance cannot be negative: " + distance);
        }
        
        Map<String, Object> radarData = new HashMap<>();
        radarData.put("AgentID", agentId.toString());
        radarData.put("DisplayName", name != null ? name : "");
        radarData.put("UserName", userName != null ? userName : "");
        radarData.put("Distance", distance);
        radarData.put("IsTyping", typing);
        radarData.put("LastSeen", System.currentTimeMillis() / 1000.0);
        
        if (position != null) {
            if (position.length != 3) {
                throw new IllegalArgumentException("Position array must have exactly 3 elements (x, y, z)");
            }
            radarData.put("Position", Arrays.stream(position).boxed().toList());
        }
        
        if (attachments != null && !attachments.isEmpty()) {
            radarData.put("Attachments", new ArrayList<>(attachments)); // Defensive copy
        }
        
        return radarData;
    }
    
    /**
     * Creates an LLSD map for a message to be sent over the Firestorm bridge.
     * <p>
     * The bridge is a mechanism for communication between the viewer and external
     * or LSL-based tools.
     *
     * @param command    The command to be executed by the bridge.
     * @param parameters A map of parameters for the command.
     * @param requestId  A unique identifier for the request. If null, one is generated.
     * @param priority   The message priority, which will be clamped to the range 0-3.
     * @return A {@link Map} containing the structured bridge message.
     * @throws IllegalArgumentException if {@code command} is null or empty.
     */
    public static Map<String, Object> createBridgeMessage(String command,
                                                          Map<String, Object> parameters,
                                                          String requestId,
                                                          int priority) {
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("Bridge command cannot be null or empty");
        }
        
        Map<String, Object> bridgeData = new HashMap<>();
        bridgeData.put("Command", command.trim());
        bridgeData.put("RequestID", requestId != null ? requestId : UUID.randomUUID().toString());
        bridgeData.put("Priority", Math.max(0, Math.min(3, priority))); // Clamp to 0-3 range
        bridgeData.put("Timestamp", System.currentTimeMillis() / 1000.0);
        
        if (parameters != null && !parameters.isEmpty()) {
            bridgeData.put("Parameters", new HashMap<>(parameters)); // Defensive copy
        }
        
        return bridgeData;
    }
    
    /**
     * Creates an LLSD map containing performance statistics from the viewer.
     *
     * @param fps        Current frames per second.
     * @param bandwidth  Current network bandwidth usage in bytes/sec.
     * @param memoryUsed Current memory usage in megabytes.
     * @param renderTime Frame render time in milliseconds.
     * @param scriptTime LSL script execution time in milliseconds.
     * @param triangles  Number of triangles rendered in the last frame.
     * @return A {@link Map} containing the performance data.
     * @throws IllegalArgumentException if any of the numeric arguments are negative.
     */
    public static Map<String, Object> createPerformanceStats(double fps,
                                                             double bandwidth,
                                                             double memoryUsed,
                                                             double renderTime,
                                                             double scriptTime,
                                                             int triangles) {
        if (fps < 0) {
            throw new IllegalArgumentException("FPS cannot be negative: " + fps);
        }
        if (bandwidth < 0) {
            throw new IllegalArgumentException("Bandwidth cannot be negative: " + bandwidth);
        }
        if (memoryUsed < 0) {
            throw new IllegalArgumentException("Memory usage cannot be negative: " + memoryUsed);
        }
        if (renderTime < 0) {
            throw new IllegalArgumentException("Render time cannot be negative: " + renderTime);
        }
        if (scriptTime < 0) {
            throw new IllegalArgumentException("Script time cannot be negative: " + scriptTime);
        }
        if (triangles < 0) {
            throw new IllegalArgumentException("Triangle count cannot be negative: " + triangles);
        }
        
        Map<String, Object> perfStats = new HashMap<>();
        perfStats.put("FPS", fps);
        perfStats.put("Bandwidth", bandwidth);
        perfStats.put("MemoryUsed", memoryUsed);
        perfStats.put("RenderTime", renderTime);
        perfStats.put("ScriptTime", scriptTime);
        perfStats.put("Triangles", triangles);
        perfStats.put("Timestamp", System.currentTimeMillis() / 1000.0);
        
        return perfStats;
    }
    
    /**
     * Creates an LLSD map containing settings for parcel media.
     *
     * @param mediaUrl  The URL of the media to be played.
     * @param mediaType The MIME type of the media (e.g., "video/mp4").
     * @param autoPlay  {@code true} if the media should play automatically.
     * @param autoScale {@code true} if the media should scale to fit its surface.
     * @param looping   {@code true} if the media should loop.
     * @param volume    The playback volume, which will be clamped to the range [0.0, 1.0].
     * @return A {@link Map} containing the structured media data.
     */
    public static Map<String, Object> createMediaData(String mediaUrl,
                                                      String mediaType,
                                                      boolean autoPlay,
                                                      boolean autoScale,
                                                      boolean looping,
                                                      double volume) {
        Map<String, Object> mediaData = new HashMap<>();
        mediaData.put("URL", mediaUrl != null ? mediaUrl.trim() : "");
        mediaData.put("Type", mediaType != null ? mediaType.trim() : "");
        mediaData.put("AutoPlay", autoPlay);
        mediaData.put("AutoScale", autoScale);
        mediaData.put("Looping", looping);
        mediaData.put("Volume", Math.max(0.0, Math.min(1.0, volume))); // Clamp to 0.0-1.0 range
        mediaData.put("Timestamp", System.currentTimeMillis() / 1000.0);
        
        return mediaData;
    }
    
    /**
     * Validates an LLSD data structure against a set of Firestorm-specific rules.
     * <p>
     * This method first applies the base Second Life validation rules and then
     * adds Firestorm-specific checks, such as minimum viewer version or RLV status.
     *
     * @param llsdData The LLSD object to validate.
     * @param rules    The {@link FSValidationRules} to apply.
     * @return An {@link FSValidationResult} containing any errors or warnings found.
     */
    public static FSValidationResult validateFSStructure(Object llsdData, FSValidationRules rules) {
        FSValidationResult result = new FSValidationResult();
        
        // Use base SL validation first
        SecondLifeLLSDUtils.ValidationResult baseResult = 
            SecondLifeLLSDUtils.validateSLStructure(llsdData, rules.getBaseRules());
        
        result.addErrors(baseResult.getErrors());
        result.addWarnings(baseResult.getWarnings());
        
        if (!baseResult.isValid()) {
            return result; // Don't continue if base validation failed
        }
        
        // Firestorm-specific validations
        if (llsdData instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> mapData = (Map<String, Object>) llsdData;
            
            // Check Firestorm version requirements
            if (rules.isRequiresFSVersion() && mapData.containsKey("ViewerVersion")) {
                String version = mapData.get("ViewerVersion").toString();
                if (!isCompatibleFSVersion(version, rules.getMinFSVersion())) {
                    result.addError("Incompatible Firestorm version: " + version + 
                                   " (required: " + rules.getMinFSVersion() + "+)");
                }
            }
            
            // Check RLV requirements
            if (rules.isRequiresRLV() && mapData.containsKey("RLVEnabled")) {
                Boolean rlvEnabled = (Boolean) mapData.get("RLVEnabled");
                if (!Boolean.TRUE.equals(rlvEnabled)) {
                    result.addError("RLV support required but not enabled");
                }
            }
            
            // Check bridge requirements
            if (rules.isRequiresBridge() && mapData.containsKey("BridgeConnected")) {
                Boolean bridgeConnected = (Boolean) mapData.get("BridgeConnected");
                if (!Boolean.TRUE.equals(bridgeConnected)) {
                    result.addWarning("Bridge connection recommended for full functionality");
                }
            }
        }
        
        return result;
    }
    
    /**
     * Compares two Firestorm version strings to check for compatibility.
     * <p>
     * This method can parse version strings in the format "major.minor.patch.build"
     * and correctly determine if a given version meets or exceeds a minimum required version.
     *
     * @param version    The version string to check.
     * @param minVersion The minimum required version string.
     * @return {@code true} if {@code version} is greater than or equal to
     *         {@code minVersion}, {@code false} otherwise.
     */
    private static boolean isCompatibleFSVersion(String version, String minVersion) {
        if (version == null || minVersion == null || 
            version.trim().isEmpty() || minVersion.trim().isEmpty()) {
            return false;
        }
        
        // Normalize versions by removing leading/trailing whitespace
        String[] versionParts = version.trim().split("\\.");
        String[] minVersionParts = minVersion.trim().split("\\.");
        
        // Compare version components
        int maxLength = Math.max(versionParts.length, minVersionParts.length);
        for (int i = 0; i < maxLength; i++) {
            int v = parseVersionComponent(versionParts, i);
            int min = parseVersionComponent(minVersionParts, i);
            
            if (v > min) return true;
            if (v < min) return false;
            // If equal, continue to next component
        }
        
        return true; // All components are equal, so it's compatible
    }
    
    /**
     * Parse a version component, handling missing parts and non-numeric values.
     * 
     * @param parts the version parts array
     * @param index the index to parse
     * @return the numeric value, or 0 if missing/invalid
     */
    private static int parseVersionComponent(String[] parts, int index) {
        if (index >= parts.length) {
            return 0; // Missing component defaults to 0
        }
        
        try {
            return Integer.parseInt(parts[index].trim());
        } catch (NumberFormatException e) {
            // For non-numeric parts, use string comparison
            // Return a hash-based number for consistent comparison
            return parts[index].trim().hashCode() & Integer.MAX_VALUE;
        }
    }
    
    /**
     * A builder class for defining a set of validation rules specific to Firestorm LLSD.
     * <p>
     * It provides a fluent API for specifying required fields, types, and
     * Firestorm-specific conditions like a minimum viewer version or RLV status.
     */
    public static class FSValidationRules {
        private final SecondLifeLLSDUtils.SLValidationRules baseRules = new SecondLifeLLSDUtils.SLValidationRules();
        
        private boolean requiresFSVersion = false;
        private String minFSVersion = "6.0.0";
        private boolean requiresRLV = false;
        private boolean requiresBridge = false;
        private final Set<String> fsRequiredFields = new HashSet<>();
        private final Map<String, String> fsFieldValidators = new HashMap<>();
        
        public FSValidationRules requireMap() {
            baseRules.requireMap();
            return this;
        }
        
        public FSValidationRules requireArray() {
            baseRules.requireArray();
            return this;
        }
        
        public FSValidationRules requireField(String field) {
            baseRules.requireField(field);
            return this;
        }
        
        public FSValidationRules requireField(String field, Class<?> type) {
            baseRules.requireField(field, type);
            return this;
        }
        
        public FSValidationRules requireFSVersion(String minVersion) {
            requiresFSVersion = true;
            minFSVersion = minVersion;
            return this;
        }
        
        public FSValidationRules requireRLV() {
            requiresRLV = true;
            return this;
        }
        
        public FSValidationRules requireBridge() {
            requiresBridge = true;
            return this;
        }
        
        public FSValidationRules requireFSField(String field) {
            fsRequiredFields.add(field);
            return this;
        }
        
        public FSValidationRules addFieldValidator(String field, String validatorName) {
            fsFieldValidators.put(field, validatorName);
            return this;
        }
        
        /**
         * Returns the base SL validation rules.
         * @return the underlying SL validation rules
         */
        SecondLifeLLSDUtils.SLValidationRules getBaseRules() {
            return baseRules;
        }
        
        /**
         * Checks if Firestorm version validation is required.
         * @return true if version validation is required
         */
        public boolean isRequiresFSVersion() {
            return requiresFSVersion;
        }
        
        /**
         * Gets the minimum required Firestorm version.
         * @return the minimum version string
         */
        public String getMinFSVersion() {
            return minFSVersion;
        }
        
        /**
         * Checks if RLV support is required.
         * @return true if RLV is required
         */
        public boolean isRequiresRLV() {
            return requiresRLV;
        }
        
        /**
         * Checks if bridge connection is required.
         * @return true if bridge is required
         */
        public boolean isRequiresBridge() {
            return requiresBridge;
        }
        
        /**
         * Gets the set of required Firestorm-specific fields.
         * @return unmodifiable set of required field names
         */
        public Set<String> getFSRequiredFields() {
            return Collections.unmodifiableSet(fsRequiredFields);
        }
        
        /**
         * Gets the map of field validators.
         * @return unmodifiable map of field validators
         */
        public Map<String, String> getFSFieldValidators() {
            return Collections.unmodifiableMap(fsFieldValidators);
        }
    }
    
    /**
     * A class that holds the results of a validation check performed by
     * {@link #validateFSStructure(Object, FSValidationRules)}.
     * <p>
     * It separates validation issues into errors, warnings, and informational
     * messages.
     */
    public static class FSValidationResult {
        private final List<String> errors = new ArrayList<>();
        private final List<String> warnings = new ArrayList<>();
        private final List<String> info = new ArrayList<>();
        
        public void addError(String error) {
            errors.add(error);
        }
        
        public void addWarning(String warning) {
            warnings.add(warning);
        }
        
        public void addInfo(String info) {
            this.info.add(info);
        }
        
        public void addErrors(List<String> errors) {
            this.errors.addAll(errors);
        }
        
        public void addWarnings(List<String> warnings) {
            this.warnings.addAll(warnings);
        }
        
        public boolean isValid() {
            return errors.isEmpty();
        }
        
        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }
        
        public List<String> getErrors() {
            return Collections.unmodifiableList(errors);
        }
        
        public List<String> getWarnings() {
            return Collections.unmodifiableList(warnings);
        }
        
        public List<String> getInfo() {
            return Collections.unmodifiableList(info);
        }
        
        public String getSummary() {
            StringBuilder summary = new StringBuilder();
            
            if (!errors.isEmpty()) {
                summary.append("Errors: ").append(errors.size()).append("\n");
                errors.forEach(error -> summary.append("  - ").append(error).append("\n"));
            }
            
            if (!warnings.isEmpty()) {
                summary.append("Warnings: ").append(warnings.size()).append("\n");
                warnings.forEach(warning -> summary.append("  - ").append(warning).append("\n"));
            }
            
            if (!info.isEmpty()) {
                summary.append("Info: ").append(info.size()).append("\n");
                info.forEach(infoMsg -> summary.append("  - ").append(infoMsg).append("\n"));
            }
            
            return summary.toString();
        }
    }
    
    /**
     * A thread-safe cache designed for high-performance LLSD processing, featuring
     * automatic expiration of entries.
     * <p>
     * This cache uses a {@link ConcurrentHashMap} for thread safety and is suitable
     * for storing frequently accessed LLSD data that has a limited lifetime.
     * Note that some operations, like {@link #size()}, may trigger a cleanup
     * of expired entries and could have a performance impact.
     */
    public static class FSLLSDCache {
        private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();
        private final long maxAge;
        private final ConcurrentHashMap<String, Long> timestamps = new ConcurrentHashMap<>();
        
        /**
         * Creates a new cache with a specified maximum age for its entries.
         *
         * @param maxAgeMillis The maximum time in milliseconds that an entry should
         *                     remain in the cache before it is considered expired.
         * @throws IllegalArgumentException if {@code maxAgeMillis} is not positive.
         */
        public FSLLSDCache(long maxAgeMillis) {
            if (maxAgeMillis <= 0) {
                throw new IllegalArgumentException("Max age must be positive: " + maxAgeMillis);
            }
            this.maxAge = maxAgeMillis;
        }
        
        /**
         * Puts a key-value pair into the cache.
         *
         * @param key   The key for the cache entry. Cannot be null.
         * @param value The value to be stored.
         * @throws IllegalArgumentException if {@code key} is null.
         */
        public void put(String key, Object value) {
            if (key == null) {
                throw new IllegalArgumentException("Cache key cannot be null");
            }
            
            long timestamp = System.currentTimeMillis();
            cache.put(key, value);
            timestamps.put(key, timestamp);
        }
        
        /**
         * Retrieves a value from the cache.
         * <p>
         * If the entry for the given key has expired, it is removed from the cache
         * and this method returns {@code null}.
         *
         * @param key The key of the entry to retrieve. Cannot be null.
         * @return The cached value, or {@code null} if the key is not found or the
         *         entry has expired.
         * @throws IllegalArgumentException if {@code key} is null.
         */
        public Object get(String key) {
            if (key == null) {
                throw new IllegalArgumentException("Cache key cannot be null");
            }
            
            Long timestamp = timestamps.get(key);
            if (timestamp != null && System.currentTimeMillis() - timestamp > maxAge) {
                // Entry is expired - remove it atomically
                remove(key);
                return null;
            }
            return cache.get(key);
        }
        
        /**
         * Checks if the cache contains a non-expired entry for the given key.
         *
         * @param key The key to check.
         * @return {@code true} if a valid, non-expired entry exists for the key.
         */
        public boolean contains(String key) {
            return get(key) != null;
        }
        
        /**
         * Removes a specific key from the cache.
         * 
         * @param key the key to remove
         * @return the previously cached value, or null if not present
         */
        public Object remove(String key) {
            timestamps.remove(key);
            return cache.remove(key);
        }
        
        /**
         * Clears all entries from the cache.
         */
        public void clear() {
            cache.clear();
            timestamps.clear();
        }
        
        /**
         * Returns the number of non-expired entries currently in the cache.
         * <p>
         * <b>Note:</b> This operation first triggers a cleanup of expired entries,
         * which can be expensive. It should be used with caution in
         * performance-sensitive code.
         *
         * @return The number of valid entries in the cache.
         */
        public int size() {
            cleanExpiredEntries();
            return cache.size();
        }
        
        /**
         * Removes all expired entries from the cache.
         * This method is thread-safe but may impact performance.
         */
        public void cleanExpiredEntries() {
            long now = System.currentTimeMillis();
            
            // Find expired keys
            Set<String> expiredKeys = timestamps.entrySet().stream()
                .filter(entry -> now - entry.getValue() > maxAge)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
            
            // Remove expired entries
            expiredKeys.forEach(this::remove);
        }
        
        /**
         * Gets the maximum age setting for cache entries.
         * 
         * @return maximum age in milliseconds
         */
        public long getMaxAge() {
            return maxAge;
        }
        
        /**
         * Gets a map containing statistics about the current state of the cache.
         *
         * @return A map with statistical data like size and max age.
         */
        public Map<String, Object> getStatistics() {
            Map<String, Object> stats = new HashMap<>();
            stats.put("size", cache.size());
            stats.put("timestampEntries", timestamps.size());
            stats.put("maxAge", maxAge);
            stats.put("currentTime", System.currentTimeMillis());
            return stats;
        }
    }
}
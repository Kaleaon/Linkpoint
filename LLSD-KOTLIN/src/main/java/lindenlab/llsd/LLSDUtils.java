/*
 * LLSDJ - LLSD in Java implementation
 *
 * Copyright(C) 2024 - Modernized implementation
 */

package lindenlab.llsd;

import java.util.*;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

/**
 * A utility class providing helper methods for common operations on LLSD data.
 * <p>
 * This final class contains static methods that simplify tasks such as:
 * <ul>
 *   <li>Navigating nested LLSD structures (maps and arrays) using a simple path syntax.</li>
 *   <li>Safely extracting values of a specific type with default fallbacks.</li>
 *   <li>Performing deep copies of LLSD objects.</li>
 *   <li>Merging LLSD maps.</li>
 *   <li>Validating the presence of required fields.</li>
 *   <li>Pretty-printing LLSD data for debugging.</li>
 * </ul>
 * As a utility class, it cannot be instantiated.
 *
 * @see LLSD
 */
public final class LLSDUtils {
    
    /**
     * Shared UUID validation pattern for consistent UUID parsing across all parsers.
     */
    public static final Pattern UUID_PATTERN = Pattern.compile(
        "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", 
        Pattern.CASE_INSENSITIVE
    );
    
    private LLSDUtils() {
        // Utility class - no instances
    }

    /**
     * Safely retrieves a string value from a nested LLSD structure.
     * <p>
     * This method navigates through a series of nested {@link Map} objects using a
     * dot-separated path (e.g., "user.profile.name"). If the path is valid and
     * the final value is a {@link String}, it is returned. Otherwise, the
     * specified default value is returned.
     *
     * @param root         The root of the LLSD data structure (typically a {@link Map}).
     * @param path         The dot-separated path to the desired value.
     * @param defaultValue The value to return if the path is not found or the
     *                     value is not a string.
     * @return The extracted string or the default value.
     */
    public static String getString(Object root, String path, String defaultValue) {
        Object value = navigatePath(root, path);
        return value instanceof String ? (String) value : defaultValue;
    }

    /**
     * Safely retrieves an integer value from a nested LLSD structure.
     *
     * @param root         The root of the LLSD data structure.
     * @param path         The dot-separated path to the desired value.
     * @param defaultValue The value to return if the path is not found or the
     *                     value is not an integer.
     * @return The extracted integer or the default value.
     */
    public static int getInteger(Object root, String path, int defaultValue) {
        Object value = navigatePath(root, path);
        return value instanceof Integer ? (Integer) value : defaultValue;
    }

    /**
     * Safely retrieves a double value from a nested LLSD structure.
     * <p>
     * If the value at the specified path is an integer, it will be safely
     * converted to a double.
     *
     * @param root         The root of the LLSD data structure.
     * @param path         The dot-separated path to the desired value.
     * @param defaultValue The value to return if the path is not found or the
     *                     value is not a number.
     * @return The extracted double or the default value.
     */
    public static double getDouble(Object root, String path, double defaultValue) {
        Object value = navigatePath(root, path);
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        }
        return defaultValue;
    }

    /**
     * Safely retrieves a boolean value from a nested LLSD structure.
     *
     * @param root         The root of the LLSD data structure.
     * @param path         The dot-separated path to the desired value.
     * @param defaultValue The value to return if the path is not found or the
     *                     value is not a boolean.
     * @return The extracted boolean or the default value.
     */
    public static boolean getBoolean(Object root, String path, boolean defaultValue) {
        Object value = navigatePath(root, path);
        return value instanceof Boolean ? (Boolean) value : defaultValue;
    }

    /**
     * Safely retrieves a UUID value from a nested LLSD structure.
     * <p>
     * If the value at the specified path is a string, this method will attempt
     * to parse it as a UUID.
     *
     * @param root         The root of the LLSD data structure.
     * @param path         The dot-separated path to the desired value.
     * @param defaultValue The value to return if the path is not found or the
     *                     value cannot be converted to a UUID.
     * @return The extracted UUID or the default value.
     */
    public static UUID getUUID(Object root, String path, UUID defaultValue) {
        Object value = navigatePath(root, path);
        if (value instanceof UUID) {
            return (UUID) value;
        } else if (value instanceof String) {
            try {
                return UUID.fromString((String) value);
            } catch (IllegalArgumentException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * Safely casts an object to a {@code Map<String, Object>}.
     * <p>
     * If the object is not an instance of {@link Map}, an empty {@link HashMap}
     * is returned to prevent {@link ClassCastException} and null pointer issues.
     *
     * @param obj The object to cast.
     * @return The object as a {@link Map}, or a new empty {@link Map} if the
     *         cast is not possible.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> asMap(Object obj) {
        return obj instanceof Map ? (Map<String, Object>) obj : new HashMap<>();
    }

    /**
     * Safely casts an object to a {@code List<Object>}.
     * <p>
     * If the object is not an instance of {@link List}, an empty {@link ArrayList}
     * is returned.
     *
     * @param obj The object to cast.
     * @return The object as a {@link List}, or a new empty {@link List} if the
     *         cast is not possible.
     */
    @SuppressWarnings("unchecked")
    public static List<Object> asList(Object obj) {
        return obj instanceof List ? (List<Object>) obj : new ArrayList<>();
    }

    /**
     * Checks if an LLSD value is considered "empty".
     * <p>
     * A value is considered empty if it is:
     * <ul>
     *   <li>{@code null}</li>
     *   <li>An instance of {@link LLSDUndefined}</li>
     *   <li>An empty {@link String}, {@link Collection}, or {@link Map}</li>
     *   <li>A zero-length byte array</li>
     * </ul>
     *
     * @param value The LLSD value to check.
     * @return {@code true} if the value is empty, {@code false} otherwise.
     */
    public static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            return ((String) value).isEmpty();
        }
        if (value instanceof Collection) {
            return ((Collection<?>) value).isEmpty();
        }
        if (value instanceof Map) {
            return ((Map<?, ?>) value).isEmpty();
        }
        if (value instanceof LLSDUndefined) {
            return true;
        }
        if (value instanceof byte[]) {
            return ((byte[]) value).length == 0;
        }
        return false;
    }

    /**
     * Creates a deep copy of an LLSD data structure.
     * <p>
     * This method recursively traverses maps and lists to create a completely
     * independent copy of the original structure. Immutable types (like String,
     * Integer, etc.) are copied by reference, while mutable types (Map, List,
     * byte[]) are duplicated.
     *
     * @param obj The LLSD object (e.g., Map, List, or primitive) to copy.
     * @return A deep copy of the provided object.
     */
    @SuppressWarnings("unchecked")
    public static Object deepCopy(Object obj) {
        if (obj == null) {
            return null;
        }
        
        if (obj instanceof Map) {
            Map<String, Object> original = (Map<String, Object>) obj;
            Map<String, Object> copy = new HashMap<>();
            for (Map.Entry<String, Object> entry : original.entrySet()) {
                copy.put(entry.getKey(), deepCopy(entry.getValue()));
            }
            return copy;
        }
        
        if (obj instanceof List) {
            List<Object> original = (List<Object>) obj;
            return original.stream()
                    .map(LLSDUtils::deepCopy)
                    .collect(Collectors.toList());
        }
        
        if (obj instanceof byte[]) {
            byte[] original = (byte[]) obj;
            return Arrays.copyOf(original, original.length);
        }
        
        // Immutable types can be returned as-is
        return obj;
    }

    /**
     * Recursively merges two LLSD maps.
     * <p>
     * This method combines the contents of a {@code source} map into a
     * {@code target} map. If a key exists in both maps and both corresponding
     * values are maps, it recursively merges them. Otherwise, the value from
     * the source map overwrites the value in the target map.
     *
     * @param target The map to merge into. A new map is created if this is null.
     * @param source The map to merge from.
     * @return A new map containing the merged key-value pairs.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> mergeMaps(Map<String, Object> target, Map<String, Object> source) {
        if (target == null) {
            return source != null ? new HashMap<>(source) : new HashMap<>();
        }
        if (source == null) {
            return target;
        }
        
        Map<String, Object> result = new HashMap<>(target);
        
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            Object sourceValue = entry.getValue();
            Object targetValue = result.get(key);
            
            if (targetValue instanceof Map && sourceValue instanceof Map) {
                // Recursively merge nested maps
                result.put(key, mergeMaps((Map<String, Object>) targetValue, (Map<String, Object>) sourceValue));
            } else {
                // Overwrite with source value
                result.put(key, deepCopy(sourceValue));
            }
        }
        
        return result;
    }

    /**
     * Validates that an LLSD data structure contains a set of required fields.
     * <p>
     * It checks for the presence and non-emptiness of each field specified in
     * {@code requiredFields}.
     *
     * @param obj            The LLSD object (typically a Map) to validate.
     * @param requiredFields A varargs array of dot-separated paths that must exist.
     * @return A list of the paths that were missing or empty. An empty list
     *         indicates that all required fields were present.
     */
    public static List<String> validateRequiredFields(Object obj, String... requiredFields) {
        List<String> missing = new ArrayList<>();
        
        for (String field : requiredFields) {
            if (isEmpty(navigatePath(obj, field))) {
                missing.add(field);
            }
        }
        
        return missing;
    }

    /**
     * Converts an LLSD data structure into a formatted, human-readable string.
     *
     * @param obj    The LLSD object to format.
     * @param indent The number of spaces to use for each level of indentation.
     * @return A pretty-printed string representation of the object.
     */
    public static String prettyPrint(Object obj, int indent) {
        StringBuilder sb = new StringBuilder();
        prettyPrintRecursive(obj, indent, 0, sb);
        return sb.toString();
    }

    /**
     * Converts an LLSD data structure into a formatted string with a default
     * indentation of 2 spaces.
     *
     * @param obj The LLSD object to format.
     * @return A pretty-printed string representation of the object.
     */
    public static String prettyPrint(Object obj) {
        return prettyPrint(obj, 2);
    }

    /**
     * Navigates a dot-separated path within a nested LLSD data structure.
     *
     * @param root The root object (should be a Map).
     * @param path The dot-separated path to follow.
     * @return The object at the specified path, or {@code null} if the path is
     *         invalid or does not exist.
     */
    private static Object navigatePath(Object root, String path) {
        if (root == null || path == null || path.isEmpty()) {
            return null;
        }
        
        String[] parts = path.split("\\.");
        Object current = root;
        
        for (String part : parts) {
            if (current instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) current;
                current = map.get(part);
            } else {
                return null; // Path doesn't exist
            }
        }
        
        return current;
    }

    /**
     * The recursive helper method for {@link #prettyPrint(Object, int)}.
     *
     * @param obj          The current object to print.
     * @param indentSize   The number of spaces per indentation level.
     * @param currentLevel The current indentation level.
     * @param sb           The {@link StringBuilder} to append the output to.
     */
    @SuppressWarnings("unchecked")
    private static void prettyPrintRecursive(Object obj, int indentSize, int currentLevel, StringBuilder sb) {
        String indent = " ".repeat(indentSize * currentLevel);
        
        if (obj == null) {
            sb.append("null");
        } else if (obj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) obj;
            sb.append("{\n");
            boolean first = true;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (!first) sb.append(",\n");
                first = false;
                sb.append(indent).append("  \"").append(entry.getKey()).append("\": ");
                prettyPrintRecursive(entry.getValue(), indentSize, currentLevel + 1, sb);
            }
            sb.append("\n").append(indent).append("}");
        } else if (obj instanceof List) {
            List<Object> list = (List<Object>) obj;
            sb.append("[\n");
            boolean first = true;
            for (Object item : list) {
                if (!first) sb.append(",\n");
                first = false;
                sb.append(indent).append("  ");
                prettyPrintRecursive(item, indentSize, currentLevel + 1, sb);
            }
            sb.append("\n").append(indent).append("]");
        } else if (obj instanceof String) {
            sb.append("\"").append(obj).append("\"");
        } else if (obj instanceof byte[]) {
            byte[] bytes = (byte[]) obj;
            sb.append("binary[").append(bytes.length).append(" bytes]");
        } else {
            sb.append(obj.toString());
        }
    }
}
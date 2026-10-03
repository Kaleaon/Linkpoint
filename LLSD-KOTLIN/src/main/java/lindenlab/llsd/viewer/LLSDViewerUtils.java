/*
 * LLSD Viewer Utilities - Java implementation based on Second Life/Firestorm C++ code
 *
 * Converted from indra/llcommon/llsdutil.h/cpp
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer;

import lindenlab.llsd.LLSD;
import lindenlab.llsd.LLSDException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A utility class providing enhanced methods for working with LLSD data, based on
 * the C++ implementation from the Second Life viewer (llsdutil.h).
 * <p>
 * This class offers advanced functionality beyond basic LLSD manipulation, including:
 * <ul>
 *   <li>Specialized conversions for viewer-specific types like unsigned integers and IP addresses.</li>
 *   <li>Template-based filtering and comparison of LLSD structures.</li>
 *   <li>Deep equality checks with floating-point precision control.</li>
 *   <li>Structure validation against a prototype.</li>
 *   <li>Deep and shallow cloning of LLSD objects.</li>
 * </ul>
 * This class is final and cannot be instantiated.
 *
 * @see <a href="https://github.com/secondlife/viewer/blob/main/indra/llcommon/llsdutil.h">llsdutil.h</a>
 */
public final class LLSDViewerUtils {
    
    private LLSDViewerUtils() {
        // Utility class - no instances
    }
    
    /**
     * Creates an LLSD integer from a 32-bit unsigned integer value.
     * <p>
     * The input is a {@code long} to accommodate the full unsigned range.
     *
     * @param value The unsigned 32-bit integer value.
     * @return An {@link Integer} object suitable for use in an LLSD structure.
     * @throws IllegalArgumentException if the value is outside the U32 range.
     */
    public static Object fromU32(long value) {
        if (value < 0 || value > 0xFFFFFFFFL) {
            throw new IllegalArgumentException("Value must be in unsigned 32-bit range: 0 to 4294967295");
        }
        return (int) value;
    }
    
    /**
     * Extracts a 32-bit unsigned integer from an LLSD value.
     *
     * @param sd The LLSD object (should be an Integer or Long).
     * @return The value as a {@code long} to hold the unsigned range.
     * @throws IllegalArgumentException if the object cannot be converted.
     */
    public static long toU32(Object sd) {
        if (sd instanceof Integer) {
            int value = (Integer) sd;
            return value & 0xFFFFFFFFL; // Convert to unsigned
        } else if (sd instanceof Long) {
            long value = (Long) sd;
            if (value < 0 || value > 0xFFFFFFFFL) {
                throw new IllegalArgumentException("Value out of unsigned 32-bit range");
            }
            return value;
        }
        throw new IllegalArgumentException("Cannot convert to U32: " + sd);
    }
    
    /**
     * Creates an LLSD long from a 64-bit unsigned integer value.
     * <p>
     * In Java, {@code long} is signed, so this method effectively just returns the
     * value, relying on the caller to handle the unsigned interpretation if needed.
     *
     * @param value The 64-bit integer value.
     * @return A {@link Long} object.
     */
    public static Object fromU64(long value) {
        return value;
    }
    
    /**
     * Extracts a 64-bit unsigned integer from an LLSD value.
     *
     * @param sd The LLSD object (should be an Integer or Long).
     * @return The value as a {@code long}.
     * @throws IllegalArgumentException if the object cannot be converted.
     */
    public static long toU64(Object sd) {
        if (sd instanceof Integer) {
            return ((Integer) sd).longValue();
        } else if (sd instanceof Long) {
            return (Long) sd;
        }
        throw new IllegalArgumentException("Cannot convert to U64: " + sd);
    }
    
    /**
     * Creates an LLSD integer from a 32-bit integer representing an IP address.
     *
     * @param ipAddress The IP address encoded as an integer.
     * @return An {@link Integer} object.
     */
    public static Object fromIPAddress(int ipAddress) {
        return ipAddress;
    }
    
    /**
     * Extracts a 32-bit integer representing an IP address from an LLSD value.
     *
     * @param sd The LLSD object (should be an Integer).
     * @return The IP address as an integer.
     * @throws IllegalArgumentException if the object is not an Integer.
     */
    public static int toIPAddress(Object sd) {
        if (sd instanceof Integer) {
            return (Integer) sd;
        }
        throw new IllegalArgumentException("Cannot convert to IP address: " + sd);
    }
    
    /**
     * Converts an LLSD binary value into its Base64 string representation.
     *
     * @param sd The LLSD object (must be a byte array).
     * @return A Base64-encoded string.
     * @throws IllegalArgumentException if the object is not a byte array.
     */
    public static String stringFromBinary(Object sd) {
        if (sd instanceof byte[]) {
            return Base64.getEncoder().encodeToString((byte[]) sd);
        }
        throw new IllegalArgumentException("Cannot convert to string from binary: " + sd);
    }
    
    /**
     * Converts an LLSD string value into a byte array.
     * <p>
     * It first attempts to decode the string as Base64. If that fails, it
     * falls back to converting the string using UTF-8 encoding.
     *
     * @param sd The LLSD object (must be a String).
     * @return The resulting byte array.
     * @throws IllegalArgumentException if the object is not a String.
     */
    public static byte[] binaryFromString(Object sd) {
        if (sd instanceof String) {
            // First try direct UTF-8 encoding, then base64 if it looks like base64
            String str = (String) sd;
            if (isBase64String(str)) {
                try {
                    return Base64.getDecoder().decode(str);
                } catch (IllegalArgumentException e) {
                    // If base64 fails, fall back to UTF-8
                    return str.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                }
            } else {
                return str.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            }
        }
        throw new IllegalArgumentException("Cannot convert to binary from string: " + sd);
    }
    
    /**
     * Check if a string looks like base64 data.
     */
    private static boolean isBase64String(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        // Simple heuristic: contains only base64 characters and has reasonable length
        return str.matches("[A-Za-z0-9+/]+(=*)") && str.length() % 4 == 0;
    }
    
    /**
     * Compares an LLSD data structure against a template, populating a result map.
     * <p>
     * This method checks if the {@code testLLSD} structure conforms to the types
     * and structure of the {@code templateLLSD}. If a key from the template exists
     * in the test data, the test data's value is used; otherwise, the template's
     * default value is used.
     *
     * @param testLLSD     The LLSD data to be tested.
     * @param templateLLSD The template structure to compare against.
     * @param resultLLSD   A map that will be populated with the result of the comparison.
     * @return {@code true} if the test data is compatible with the template,
     *         {@code false} otherwise.
     */
    public static boolean compareWithTemplate(Object testLLSD, Object templateLLSD, Map<String, Object> resultLLSD) {
        if (resultLLSD == null) {
            throw new IllegalArgumentException("Result map cannot be null");
        }
        return compareWithTemplateImpl(testLLSD, templateLLSD, resultLLSD, false);
    }
    
    /**
     * Filters an LLSD data structure using a template, with support for wildcards.
     * <p>
     * Similar to {@link #compareWithTemplate}, but with added support for a "*"
     * wildcard key in template maps, which can apply a sub-template to all
     * otherwise unmatched keys in the test data.
     *
     * @param testLLSD     The LLSD data to be filtered.
     * @param templateLLSD The template structure to use for filtering.
     * @param resultLLSD   A map that will be populated with the filtered result.
     * @return {@code true} if the filtering is successful, {@code false} otherwise.
     */
    public static boolean filterWithTemplate(Object testLLSD, Object templateLLSD, Map<String, Object> resultLLSD) {
        if (resultLLSD == null) {
            throw new IllegalArgumentException("Result map cannot be null");
        }
        return compareWithTemplateImpl(testLLSD, templateLLSD, resultLLSD, true);
    }
    
    private static boolean compareWithTemplateImpl(Object testObj, Object templateObj, Map<String, Object> result, boolean useWildcard) {
        // result should not be null - validated by public methods
        
        if (templateObj == null || "".equals(templateObj)) {
            // Undefined template matches anything
            if (testObj != null) {
                if (testObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> testMap = (Map<String, Object>) testObj;
                    result.putAll(testMap);
                } else {
                    // For non-map types, we can't directly merge, so return true
                    return true;
                }
            }
            return true;
        }
        
        if (testObj == null) {
            // Use template default
            if (templateObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> templateMap = (Map<String, Object>) templateObj;
                result.putAll(templateMap);
            }
            return true;
        }
        
        // Type checking - allow compatible types
        if (!areCompatibleTypes(testObj, templateObj)) {
            return false;
        }
        
        if (testObj instanceof Map && templateObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> testMap = (Map<String, Object>) testObj;
            @SuppressWarnings("unchecked")
            Map<String, Object> templateMap = (Map<String, Object>) templateObj;
            
            for (Map.Entry<String, Object> templateEntry : templateMap.entrySet()) {
                String key = templateEntry.getKey();
                Object templateValue = templateEntry.getValue();
                
                if (useWildcard && "*".equals(key)) {
                    // Wildcard key - apply to all unmatched keys in test
                    for (Map.Entry<String, Object> testEntry : testMap.entrySet()) {
                        if (!templateMap.containsKey(testEntry.getKey())) {
                            Map<String, Object> childResult = new HashMap<>();
                            if (compareWithTemplateImpl(testEntry.getValue(), templateValue, childResult, useWildcard)) {
                                if (childResult.isEmpty()) {
                                    result.put(testEntry.getKey(), testEntry.getValue());
                                } else {
                                    result.put(testEntry.getKey(), childResult.size() == 1 ? childResult.values().iterator().next() : childResult);
                                }
                            }
                        }
                    }
                    continue;
                }
                
                if (testMap.containsKey(key)) {
                    if (templateValue == null || "".equals(templateValue)) {
                        // Undefined template value means accept anything
                        result.put(key, testMap.get(key));
                    } else {
                        Map<String, Object> childResult = new HashMap<>();
                        if (compareWithTemplateImpl(testMap.get(key), templateValue, childResult, useWildcard)) {
                            if (childResult.isEmpty()) {
                                result.put(key, testMap.get(key));
                            } else {
                                result.put(key, childResult.size() == 1 ? childResult.values().iterator().next() : childResult);
                            }
                        } else {
                            return false;
                        }
                    }
                } else {
                    // Use template default
                    result.put(key, templateValue);
                }
            }
            return true;
        } else if (testObj instanceof List && templateObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> testArray = (List<Object>) testObj;
            @SuppressWarnings("unchecked")
            List<Object> templateArray = (List<Object>) templateObj;
            
            if (useWildcard && templateArray.size() == 1) {
                // Single template element applies to all test elements
                List<Object> resultArray = new ArrayList<>();
                Object templateElement = templateArray.get(0);
                for (Object testElement : testArray) {
                    Map<String, Object> childResult = new HashMap<>();
                    if (compareWithTemplateImpl(testElement, templateElement, childResult, useWildcard)) {
                        resultArray.add(childResult.isEmpty() ? testElement : childResult);
                    } else {
                        return false;
                    }
                }
                result.put("array", resultArray);
            } else {
                // Regular array comparison
                List<Object> resultArray = new ArrayList<>();
                for (int i = 0; i < Math.min(testArray.size(), templateArray.size()); i++) {
                    Object templateElement = templateArray.get(i);
                    if (templateElement == null || "".equals(templateElement)) {
                        resultArray.add(testArray.get(i));
                    } else {
                        Map<String, Object> childResult = new HashMap<>();
                        if (compareWithTemplateImpl(testArray.get(i), templateElement, childResult, useWildcard)) {
                            resultArray.add(childResult.isEmpty() ? testArray.get(i) : childResult);
                        } else {
                            return false;
                        }
                    }
                }
                result.put("array", resultArray);
            }
            return true;
        } else {
            // Scalar comparison - just store the test value
            return true;
        }
    }
    
    /**
     * Check if two objects have compatible types for template comparison.
     */
    private static boolean areCompatibleTypes(Object test, Object template) {
        if (test == null || template == null) {
            return true;
        }
        
        Class<?> testClass = test.getClass();
        Class<?> templateClass = template.getClass();
        
        // Exact match
        if (testClass.equals(templateClass)) {
            return true;
        }
        
        // Numeric compatibility
        if (isNumericType(testClass) && isNumericType(templateClass)) {
            return true;
        }
        
        // String compatibility with other types
        if (templateClass.equals(String.class) && isStringConvertibleType(testClass)) {
            return true;
        }
        
        return false;
    }
    
    private static boolean isNumericType(Class<?> clazz) {
        return clazz.equals(Integer.class) || clazz.equals(Double.class) || 
               clazz.equals(Float.class) || clazz.equals(Long.class) || 
               clazz.equals(Boolean.class);
    }
    
    private static boolean isStringConvertibleType(Class<?> clazz) {
        return clazz.equals(String.class) || clazz.equals(Integer.class) || 
               clazz.equals(Double.class) || clazz.equals(Boolean.class) ||
               clazz.equals(UUID.class) || clazz.equals(Date.class);
    }
    
    /**
     * Checks if an LLSD data structure matches the structure of a given prototype.
     * <p>
     * This method recursively validates that the {@code data} object has the same
     * structure and compatible types as the {@code prototype}. It's useful for
     * protocol validation.
     *
     * @param prototype The prototype structure to match against.
     * @param data      The data to be validated.
     * @param prefix    A string prefix to be used in the error message for context.
     * @return An empty string if the data matches the prototype, or a descriptive
     *         error message if it does not.
     */
    public static String llsdMatches(Object prototype, Object data, String prefix) {
        if (prefix == null) prefix = "";
        
        if (prototype == null || "".equals(prototype)) {
            return ""; // undefined prototype matches anything
        }
        
        if (data == null) {
            return prefix + "missing data";
        }
        
        Class<?> prototypeClass = prototype.getClass();
        Class<?> dataClass = data.getClass();
        
        // Type compatibility checks
        if (prototype instanceof String) {
            // String prototype accepts string-convertible types
            if (!(data instanceof String || data instanceof Boolean || 
                  data instanceof Integer || data instanceof Double ||
                  data instanceof UUID || data instanceof Date)) {
                return prefix + "expected string-convertible type, got " + dataClass.getSimpleName();
            }
        } else if (prototype instanceof Boolean || prototype instanceof Integer || prototype instanceof Double) {
            // Numeric types are interconvertible
            if (!(data instanceof Boolean || data instanceof Integer || 
                  data instanceof Double || data instanceof String)) {
                return prefix + "expected numeric type, got " + dataClass.getSimpleName();
            }
        } else if (prototype instanceof UUID) {
            if (!(data instanceof UUID || data instanceof String)) {
                return prefix + "expected UUID or String, got " + dataClass.getSimpleName();
            }
        } else if (prototype instanceof Date) {
            if (!(data instanceof Date || data instanceof String)) {
                return prefix + "expected Date or String, got " + dataClass.getSimpleName();
            }
        } else if (prototype instanceof byte[]) {
            if (!(data instanceof byte[])) {
                return prefix + "expected Binary, got " + dataClass.getSimpleName();
            }
        } else if (prototype instanceof List) {
            if (!(data instanceof List)) {
                return prefix + "expected Array, got " + dataClass.getSimpleName();
            }
            
            List<?> prototypeList = (List<?>) prototype;
            List<?> dataList = (List<?>) data;
            
            if (dataList.size() < prototypeList.size()) {
                return prefix + "array too short: expected at least " + prototypeList.size() + ", got " + dataList.size();
            }
            
            for (int i = 0; i < prototypeList.size(); i++) {
                Object prototypeElement = prototypeList.get(i);
                if (prototypeElement != null && !"".equals(prototypeElement)) {
                    String result = llsdMatches(prototypeElement, dataList.get(i), prefix + "[" + i + "].");
                    if (!result.isEmpty()) {
                        return result;
                    }
                }
            }
        } else if (prototype instanceof Map) {
            if (!(data instanceof Map)) {
                return prefix + "expected Map, got " + dataClass.getSimpleName();
            }
            
            Map<?, ?> prototypeMap = (Map<?, ?>) prototype;
            Map<?, ?> dataMap = (Map<?, ?>) data;
            
            for (Map.Entry<?, ?> entry : prototypeMap.entrySet()) {
                String key = entry.getKey().toString();
                Object prototypeValue = entry.getValue();
                
                if (prototypeValue != null && !"".equals(prototypeValue)) {
                    if (!dataMap.containsKey(key)) {
                        return prefix + "missing required key: " + key;
                    }
                    String result = llsdMatches(prototypeValue, dataMap.get(key), prefix + key + ".");
                    if (!result.isEmpty()) {
                        return result;
                    }
                }
            }
        } else if (!prototypeClass.equals(dataClass)) {
            return prefix + "type mismatch: expected " + prototypeClass.getSimpleName() + ", got " + dataClass.getSimpleName();
        }
        
        return "";
    }
    
    /**
     * Performs a deep equality comparison between two LLSD data structures.
     * <p>
     * This method recursively compares maps, lists, and primitive values. It
     * provides an option for approximate comparison of floating-point numbers
     * based on a specified bit precision.
     *
     * @param lhs  The left-hand side object for comparison.
     * @param rhs  The right-hand side object for comparison.
     * @param bits The number of bits of precision for floating-point comparison.
     *             A value of -1 indicates that an exact comparison should be used.
     * @return {@code true} if the objects are deeply equal, {@code false} otherwise.
     */
    public static boolean llsdEquals(Object lhs, Object rhs, int bits) {
        if (lhs == rhs) return true;
        if (lhs == null || rhs == null) return false;
        
        Class<?> lhsClass = lhs.getClass();
        Class<?> rhsClass = rhs.getClass();
        
        if (!lhsClass.equals(rhsClass)) return false;
        
        if (lhs instanceof Double && rhs instanceof Double) {
            if (bits == -1) {
                return Objects.equals(lhs, rhs);
            } else {
                // Approximate equality for floating point
                double diff = Math.abs((Double) lhs - (Double) rhs);
                double epsilon = Math.pow(2, -bits);
                return diff <= epsilon;
            }
        } else if (lhs instanceof List && rhs instanceof List) {
            List<?> lhsList = (List<?>) lhs;
            List<?> rhsList = (List<?>) rhs;
            
            if (lhsList.size() != rhsList.size()) return false;
            
            for (int i = 0; i < lhsList.size(); i++) {
                if (!llsdEquals(lhsList.get(i), rhsList.get(i), bits)) {
                    return false;
                }
            }
            return true;
        } else if (lhs instanceof Map && rhs instanceof Map) {
            Map<?, ?> lhsMap = (Map<?, ?>) lhs;
            Map<?, ?> rhsMap = (Map<?, ?>) rhs;
            
            if (lhsMap.size() != rhsMap.size()) return false;
            
            for (Map.Entry<?, ?> entry : lhsMap.entrySet()) {
                Object key = entry.getKey();
                if (!rhsMap.containsKey(key)) return false;
                if (!llsdEquals(entry.getValue(), rhsMap.get(key), bits)) {
                    return false;
                }
            }
            return true;
        } else if (lhs instanceof byte[] && rhs instanceof byte[]) {
            return Arrays.equals((byte[]) lhs, (byte[]) rhs);
        } else {
            return Objects.equals(lhs, rhs);
        }
    }
    
    /**
     * Performs a deep equality comparison with exact floating-point matching.
     *
     * @param lhs The left-hand side object.
     * @param rhs The right-hand side object.
     * @return {@code true} if the objects are deeply equal.
     */
    public static boolean llsdEquals(Object lhs, Object rhs) {
        return llsdEquals(lhs, rhs, -1);
    }
    
    /**
     * Creates a deep clone of an LLSD data structure, with an optional filter
     * to include or exclude map keys.
     *
     * @param value  The LLSD object to clone.
     * @param filter An optional map where keys are map keys to be filtered, and
     *               values are booleans indicating whether to include them. A
     *               "*" key can be used as a wildcard. Can be {@code null} for no filtering.
     * @return A deep copy of the value.
     */
    public static Object llsdClone(Object value, Map<String, Boolean> filter) {
        if (value == null) return null;
        
        if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> originalMap = (Map<String, Object>) value;
            Map<String, Object> clonedMap = new HashMap<>();
            
            boolean hasWildcard = filter != null && filter.containsKey("*");
            boolean wildcardValue = hasWildcard ? filter.get("*") : true;
            
            for (Map.Entry<String, Object> entry : originalMap.entrySet()) {
                String key = entry.getKey();
                boolean shouldInclude;
                
                if (filter != null && filter.containsKey(key)) {
                    shouldInclude = filter.get(key);
                } else {
                    shouldInclude = wildcardValue;
                }
                
                if (shouldInclude) {
                    clonedMap.put(key, llsdClone(entry.getValue(), filter));
                }
            }
            return clonedMap;
        } else if (value instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> originalList = (List<Object>) value;
            List<Object> clonedList = new ArrayList<>();
            
            for (Object item : originalList) {
                clonedList.add(llsdClone(item, filter));
            }
            return clonedList;
        } else if (value instanceof byte[]) {
            return ((byte[]) value).clone();
        } else {
            // Primitive types are immutable, return as-is
            return value;
        }
    }
    
    /**
     * Creates a deep clone of an LLSD data structure without any filtering.
     *
     * @param value The value to clone.
     * @return A deep copy of the value.
     */
    public static Object llsdClone(Object value) {
        return llsdClone(value, null);
    }
    
    /**
     * Creates a shallow copy of an LLSD map or array, with an optional filter.
     * <p>
     * For maps and lists, this creates a new container but the elements themselves
     * are not copied. For primitive types, it returns the value itself.
     *
     * @param value  The value to copy.
     * @param filter An optional filter for map keys (see {@link #llsdClone(Object, Map)}).
     * @return A shallow copy of the value.
     */
    public static Object llsdShallow(Object value, Map<String, Boolean> filter) {
        if (value == null) return null;
        
        if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> originalMap = (Map<String, Object>) value;
            Map<String, Object> copiedMap = new HashMap<>();
            
            boolean hasWildcard = filter != null && filter.containsKey("*");
            boolean wildcardValue = hasWildcard ? filter.get("*") : true;
            
            for (Map.Entry<String, Object> entry : originalMap.entrySet()) {
                String key = entry.getKey();
                boolean shouldInclude;
                
                if (filter != null && filter.containsKey(key)) {
                    shouldInclude = filter.get(key);
                } else {
                    shouldInclude = wildcardValue;
                }
                
                if (shouldInclude) {
                    copiedMap.put(key, entry.getValue());
                }
            }
            return copiedMap;
        } else if (value instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> originalList = (List<Object>) value;
            return new ArrayList<>(originalList);
        } else {
            return value;
        }
    }
    
    /**
     * Creates a shallow copy of an LLSD map or array without any filtering.
     *
     * @param value The value to copy.
     * @return A shallow copy of the value.
     */
    public static Object llsdShallow(Object value) {
        return llsdShallow(value, null);
    }
}
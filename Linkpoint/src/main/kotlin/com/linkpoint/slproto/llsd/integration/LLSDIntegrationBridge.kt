package com.linkpoint.slproto.llsd.integration

import android.util.Log

import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.llsd.LLSDXMLException
import com.linkpoint.slproto.llsd.types.*

import lindenlab.llsd.LLSD
import lindenlab.llsd.LLSDParser

import java.io.IOException
import java.io.InputStream
import java.io.StringReader
import java.net.URI
import java.util.Date
import java.util.HashMap
import java.util.List
import java.util.Map
import java.util.UUID

/**
 * Integration bridge for LLSD parsing in Linkpoint.
 * 
 * CURRENT STATUS: Using jacobilinden/llsd-java (lindenlab:llsd:1.0) + Android-compatible Kotlin LLSD
 * ENHANCED: Now includes @Kaleaon's Kotlin LLSD features adapted for Android JVM 8
 * 
 * @Kaleaon's Enhanced Library Features (NOW AVAILABLE):
 * ✅ Kotlin DSL support for type-safe LLSD creation (Android-compatible)
 * ✅ Sealed class hierarchy with null safety
 * ✅ Extension functions for easy type conversion  
 * ✅ Type-safe value access with defaults
 * - Multiple LLSD formats: XML, JSON, Notation, Binary (base XML working, others planned)
 * - Second Life-specific extensions (PBR materials, Windlight, Particles) (future)
 * - Advanced serialization framework with security limits (future)
 * - Viewer-specific features from Second Life/Firestorm C++ code (future)
 * 
 * Current Implementation:
 * 1. Parse LLSD using the base external library (jacobilinden)
 * 2. Convert between external LLSD objects and Linkpoint LLSD nodes  
 * 3. Support Kotlin DSL for elegant LLSD creation (@Kaleaon's approach)
 * 4. Maintain compatibility with existing code
 * 5. Provide foundation for future enhanced features migration
 */
class LLSDIntegrationBridge {
    private const val String TAG = "LLSDIntegrationBridge"
    
    /**
     * Parse LLSD from XML string using the external library
     */
    @JvmStatic
    LLSDNode parseFromXML(String xmlString) throws LLSDXMLException {
        try {
            LLSDParser parser = LLSDParser()
            LLSD externalLLSD = parser.parse(StringReader(xmlString))
            return convertExternalToLinkpoint(externalLLSD.getContent())
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse LLSD XML", e)
            throw LLSDXMLException("Failed to parse LLSD XML: " + e.getMessage())
        }
    }
    
    /**
     * Parse LLSD from InputStream using the external library
     */
    @JvmStatic
    LLSDNode parseFromStream(InputStream inputStream) throws LLSDXMLException {
        try {
            LLSDParser parser = LLSDParser()
            LLSD externalLLSD = parser.parse(inputStream)
            return convertExternalToLinkpoint(externalLLSD.getContent())
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse LLSD from stream", e)
            throw LLSDXMLException("Failed to parse LLSD from stream: " + e.getMessage())
        }
    }
    
    /**
     * Convert external LLSD library objects to Linkpoint LLSD nodes
     */
    @SuppressWarnings("unchecked")
    @JvmStatic
    LLSDNode convertExternalToLinkpoint(Object externalObject) throws LLSDXMLException {
        if (externalObject == null) {
            return LLSDUndefined()
        }
        
        try {
            if (externalObject instanceof String) {
                return LLSDString((String) externalObject)
            } else if (externalObject instanceof Integer) {
                return LLSDInt((Integer) externalObject)
            } else if (externalObject instanceof Double) {
                return LLSDDouble((Double) externalObject)
            } else if (externalObject instanceof Float) {
                return LLSDDouble(((Float) externalObject).doubleValue())
            } else if (externalObject instanceof Boolean) {
                return LLSDBoolean((Boolean) externalObject)
            } else if (externalObject instanceof UUID) {
                return LLSDUUID((UUID) externalObject)
            } else if (externalObject instanceof URI) {
                return LLSDURI((URI) externalObject)
            } else if (externalObject instanceof Date) {
                return LLSDDate((Date) externalObject)
            } else if (externalObject instanceof List) {
                List<Object> list = (List<Object>) externalObject
                LLSDArray array = LLSDArray()
                for (Object item : list) {
                    array.add(convertExternalToLinkpoint(item))
                }
                return array
            } else if (externalObject instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) externalObject
                Map<String, LLSDNode> nodeMap = HashMap<>()
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    nodeMap.put(entry.getKey(), convertExternalToLinkpoint(entry.getValue()))
                }
                return LLSDMap(nodeMap)
            } else if (externalObject instanceof lindenlab.llsd.LLSDUndefined) {
                return LLSDUndefined()
            } else {
                Log.w(TAG, "Unknown external LLSD type: " + externalObject.getClass().getName())
                return LLSDString(externalObject.toString())
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to convert external LLSD object", e)
            throw LLSDXMLException("Failed to convert external LLSD object: " + e.getMessage())
        }
    }
    
    /**
     * Parse LLSD from JSON string using enhanced library (FUTURE)
     * Currently falls back to XML parsing for compatibility
     */
    @JvmStatic
    LLSDNode parseFromJSON(String jsonString) throws LLSDXMLException {
        try {
            // FUTURE: Use @Kaleaon's LLSDJsonParser when Android-compatible
            // For now, treat as XML if it looks like LLSD-XML, otherwise create simple string node
            if (jsonString.trim().startsWith("<?xml") || jsonString.trim().startsWith("<llsd")) {
                return parseFromXML(jsonString)
            } else {
                // Simple JSON-to-LLSD conversion - create a string node
                return LLSDString(jsonString)
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse JSON LLSD (using fallback)", e)
            throw LLSDXMLException("Failed to parse JSON LLSD: " + e.getMessage())
        }
    }
    
    /**
     * Parse LLSD from Notation format using enhanced library (FUTURE)
     * Currently creates a simple string representation for compatibility
     */
    @JvmStatic
    LLSDNode parseFromNotation(String notationString) throws LLSDXMLException {
        try {
            // FUTURE: Use @Kaleaon's LLSDNotationParser when Android-compatible
            // For now, wrap in basic LLSD XML structure
            return parseFromXML("<llsd><string>" + notationString + "</string></llsd>")
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse Notation LLSD (using fallback)", e)
            throw LLSDXMLException("Failed to parse Notation LLSD: " + e.getMessage())
        }
    }
    
    /**
     * Auto-detect LLSD format and parse using enhanced library (FUTURE)
     * Currently performs basic format detection with fallback parsing
     */
    @JvmStatic
    LLSDNode parseAuto(String llsdData) throws LLSDXMLException {
        try {
            // Simple format detection - enhanced detection coming with @Kaleaon's library
            String trimmed = llsdData.trim()
            if (trimmed.startsWith("<?xml") || trimmed.startsWith("<llsd")) {
                return parseFromXML(llsdData)
            } else if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
                // Looks like JSON - use JSON parser
                return parseFromJSON(llsdData)
            } else {
                // Assume notation format
                return parseFromNotation(llsdData)
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to auto-detect LLSD format (using fallback)", e)
            throw LLSDXMLException("Failed to auto-detect LLSD format: " + e.getMessage())
        }
    }
    /**
     * Enhanced parser with fallback capability
     * CURRENT: Uses jacobilinden/llsd-java with fallback to original implementation
     * FUTURE: Will use @Kaleaon's enhanced library with advanced features
     */
    @JvmStatic
    LLSDNode parseWithFallback(InputStream inputStream, String contentType) throws LLSDXMLException {
        // Try external library first (currently jacobilinden, future: @Kaleaon's enhanced)
        try {
            return parseFromStream(inputStream)
        } catch (Exception e) {
            Log.w(TAG, "External LLSD parser failed, falling back to original implementation", e)
            
            // Reset stream if possible for fallback
            if (inputStream.markSupported()) {
                try {
                    inputStream.reset()
                } catch (IOException ioException) {
                    Log.w(TAG, "Could not reset stream for fallback parsing", ioException)
                }
            }
            
            // Fallback to original Linkpoint implementation
            return LLSDNode.fromAny(inputStream, contentType)
        }
    }
    
    /**
     * Get information about the current LLSD integration status
     * This method provides migration status and feature availability
     */
    @JvmStatic
    String getIntegrationInfo() {
        return "LLSD Integration Status:\n" +
               "- Current: jacobilinden/llsd-java (base functionality)\n" + 
               "- Enhanced: @Kaleaon's Kotlin LLSD features (Android-compatible)\n" +
               "- Target: Full @Kaleaon's enhanced llsd-java library integration\n" +
               "- Features available: Kotlin DSL, type safety, sealed classes\n" +
               "- Features planned: JSON/Notation/Binary formats, SL extensions\n" +
               "- Compatibility: Android/Java 8 compatible bridge with Kotlin support\n" +
               "- Status: Kotlin DSL ready for use, full migration planned"
    }
    
    /**
     * Demonstrate @Kaleaon's Kotlin LLSD features (Android-compatible)
     * This shows the enhanced capabilities now available
     */
    @JvmStatic
    Unit demonstrateKotlinFeatures() {
        try {
            // This would typically be called from Kotlin code, but we can demonstrate from Java
            Log.i(TAG, "Kotlin LLSD features now available!")
            Log.i(TAG, "Enhanced capabilities: Type-safe DSL, sealed classes, extension functions") 
            Log.i(TAG, "See KotlinLLSDDemo for full examples of @Kaleaon's enhanced features")
            
            // The Kotlin DSL allows for elegant LLSD creation like:
            // val data = kotlinLlsdMap {
            //     "name" to "Agent"
            //     "position" to kotlinLlsdArray { +128.0; +128.0; +23.0 }
            //     "active" to true
            // }
            
        } catch (Exception e) {
            Log.e(TAG, "Error demonstrating Kotlin features", e)
        }
    }
}
package com.lumiyaviewer.lumiya.slproto.llsd.integration;

import android.util.Log;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import com.lumiyaviewer.lumiya.slproto.llsd.types.*;

import lindenlab.llsd.LLSD;
import lindenlab.llsd.LLSDParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Integration bridge between the external jacobilinden/llsd-java library
 * and the existing Linkpoint LLSD implementation.
 * 
 * This class provides methods to:
 * 1. Parse LLSD using the external library
 * 2. Convert between external LLSD objects and Linkpoint LLSD nodes
 * 3. Maintain compatibility with existing code
 */
public class LLSDIntegrationBridge {
    private static final String TAG = "LLSDIntegrationBridge";
    
    /**
     * Parse LLSD from XML string using the external library
     */
    public static LLSDNode parseFromXML(String xmlString) throws LLSDXMLException {
        try {
            LLSDParser parser = new LLSDParser();
            LLSD externalLLSD = parser.parse(new StringReader(xmlString));
            return convertExternalToLinkpoint(externalLLSD.getContent());
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse LLSD XML", e);
            throw new LLSDXMLException("Failed to parse LLSD XML: " + e.getMessage());
        }
    }
    
    /**
     * Parse LLSD from InputStream using the external library
     */
    public static LLSDNode parseFromStream(InputStream inputStream) throws LLSDXMLException {
        try {
            LLSDParser parser = new LLSDParser();
            LLSD externalLLSD = parser.parse(inputStream);
            return convertExternalToLinkpoint(externalLLSD.getContent());
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse LLSD from stream", e);
            throw new LLSDXMLException("Failed to parse LLSD from stream: " + e.getMessage());
        }
    }
    
    /**
     * Convert external LLSD library objects to Linkpoint LLSD nodes
     */
    @SuppressWarnings("unchecked")
    public static LLSDNode convertExternalToLinkpoint(Object externalObject) throws LLSDXMLException {
        if (externalObject == null) {
            return new LLSDUndefined();
        }
        
        try {
            if (externalObject instanceof String) {
                return new LLSDString((String) externalObject);
            } else if (externalObject instanceof Integer) {
                return new LLSDInt((Integer) externalObject);
            } else if (externalObject instanceof Double) {
                return new LLSDDouble((Double) externalObject);
            } else if (externalObject instanceof Float) {
                return new LLSDDouble(((Float) externalObject).doubleValue());
            } else if (externalObject instanceof Boolean) {
                return new LLSDBoolean((Boolean) externalObject);
            } else if (externalObject instanceof UUID) {
                return new LLSDUUID((UUID) externalObject);
            } else if (externalObject instanceof URI) {
                return new LLSDURI((URI) externalObject);
            } else if (externalObject instanceof Date) {
                return new LLSDDate((Date) externalObject);
            } else if (externalObject instanceof List) {
                List<Object> list = (List<Object>) externalObject;
                LLSDArray array = new LLSDArray();
                for (Object item : list) {
                    array.add(convertExternalToLinkpoint(item));
                }
                return array;
            } else if (externalObject instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) externalObject;
                Map<String, LLSDNode> nodeMap = new HashMap<>();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    nodeMap.put(entry.getKey(), convertExternalToLinkpoint(entry.getValue()));
                }
                return new LLSDMap(nodeMap);
            } else if (externalObject instanceof lindenlab.llsd.LLSDUndefined) {
                return new LLSDUndefined();
            } else {
                Log.w(TAG, "Unknown external LLSD type: " + externalObject.getClass().getName());
                return new LLSDString(externalObject.toString());
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to convert external LLSD object", e);
            throw new LLSDXMLException("Failed to convert external LLSD object: " + e.getMessage());
        }
    }
    
    /**
     * Enhanced parser that tries the external library first, falls back to original implementation
     */
    public static LLSDNode parseWithFallback(InputStream inputStream, String contentType) throws LLSDXMLException {
        // Try external library first
        try {
            return parseFromStream(inputStream);
        } catch (Exception e) {
            Log.w(TAG, "External LLSD parser failed, falling back to original implementation", e);
            
            // Reset stream if possible
            if (inputStream.markSupported()) {
                try {
                    inputStream.reset();
                } catch (IOException ioException) {
                    Log.w(TAG, "Could not reset stream for fallback parsing", ioException);
                }
            }
            
            // Fallback to original implementation
            return LLSDNode.fromAny(inputStream, contentType);
        }
    }
}
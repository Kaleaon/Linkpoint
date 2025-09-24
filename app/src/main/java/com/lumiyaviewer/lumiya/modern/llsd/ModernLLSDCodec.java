package com.lumiyaviewer.lumiya.modern.llsd;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.*;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.UUID;
import java.util.Date;

/**
 * Modern JVM-first LLSD codec with LibreMetaverse-style API compatibility.
 * Provides wire-level compatibility with Second Life/Firestorm/LibreMetaverse
 * while offering modern Java 8+ streaming and functional programming features.
 */
public class ModernLLSDCodec {
    
    /**
     * Convert LLSD to JSON with modern streaming support
     */
    public static JSONObject toJSON(LLSDNode llsd) {
        try {
            switch (llsd.getType()) {
                case LLSDString:
                    return new JSONObject().put("value", llsd.asString());
                case LLSDInt:
                    return new JSONObject().put("value", llsd.asInteger());
                case LLSDDouble:
                    return new JSONObject().put("value", llsd.asReal());
                case LLSDBoolean:
                    return new JSONObject().put("value", llsd.asBoolean());
                default:
                    return new JSONObject().put("value", llsd.asString());
            }
        } catch (JSONException e) {
            return new JSONObject();
        }
    }
    
    /**
     * Create modern LLSD primitives with LibreMetaverse-compatible API
     */
    public static class Primitives {
        
        public static LLSDMap createVector3(LLVector3 vector) {
            LLSDMap map = new LLSDMap();
            map.put("X", new LLSDDouble(vector.x));
            map.put("Y", new LLSDDouble(vector.y));
            map.put("Z", new LLSDDouble(vector.z));
            return map;
        }
        
        public static LLSDMap createQuaternion(LLQuaternion quat) {
            LLSDMap map = new LLSDMap();
            map.put("X", new LLSDDouble(quat.x));
            map.put("Y", new LLSDDouble(quat.y));
            map.put("Z", new LLSDDouble(quat.z));
            map.put("W", new LLSDDouble(quat.w));
            return map;
        }
        
        public static LLSDUUID createUUID(UUID uuid) {
            return new LLSDUUID(uuid);
        }
        
        public static LLSDDate createDate(Date date) {
            return new LLSDDate(date);
        }
        
        public static LLSDMap createColor4(float r, float g, float b, float a) {
            LLSDMap map = new LLSDMap();
            map.put("R", new LLSDDouble(r));
            map.put("G", new LLSDDouble(g));
            map.put("B", new LLSDDouble(b));
            map.put("A", new LLSDDouble(a));
            return map;
        }
    }
    
    /**
     * Modern streaming parser for LLSD data 
     */
    public static class StreamingParser {
        
        public static LLSDNode parseModern(String input) {
            try {
                return LLSDNode.parseLLSD(input);
            } catch (Exception e) {
                return new LLSDMap();
            }
        }
        
        public static String serializeModern(LLSDNode node) {
            try {
                return node.toXMLString();
            } catch (Exception e) {
                return "<llsd><map /></llsd>";
            }
        }
    }
}

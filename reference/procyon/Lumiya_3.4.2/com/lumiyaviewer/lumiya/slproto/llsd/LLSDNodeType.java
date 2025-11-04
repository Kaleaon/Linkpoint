// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.llsd;

import java.util.HashMap;
import java.util.Map;

public enum LLSDNodeType
{
    llsdArray("llsdArray", 10, "array"), 
    llsdBinary("llsdBinary", 9, "binary"), 
    llsdBoolean("llsdBoolean", 2, "boolean"), 
    llsdDate("llsdDate", 7, "date"), 
    llsdDouble("llsdDouble", 4, "real"), 
    llsdInteger("llsdInteger", 3, "integer"), 
    llsdKey("llsdKey", 12, "key"), 
    llsdMap("llsdMap", 11, "map"), 
    llsdRoot("llsdRoot", 0, "llsd"), 
    llsdString("llsdString", 6, "string"), 
    llsdURI("llsdURI", 8, "uri"), 
    llsdUUID("llsdUUID", 5, "uuid"), 
    llsdUndef("llsdUndef", 1, "undef");
    
    private static final Map<String, LLSDNodeType> tagMap;
    private final String tagName;
    
    static {
        int i = 0;
        tagMap = new HashMap<String, LLSDNodeType>(values().length * 2);
        for (LLSDNodeType[] values = values(); i < values.length; ++i) {
            final LLSDNodeType llsdNodeType = values[i];
            LLSDNodeType.tagMap.put(llsdNodeType.tagName, llsdNodeType);
        }
    }
    
    private LLSDNodeType(final String name, final int ordinal, final String tagName) {
        this.tagName = tagName;
    }
    
    public static LLSDNodeType byTag(final String s) {
        return LLSDNodeType.tagMap.get(s);
    }
}

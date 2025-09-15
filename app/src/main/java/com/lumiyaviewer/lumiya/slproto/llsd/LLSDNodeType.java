package com.lumiyaviewer.lumiya.slproto.llsd;

import java.util.HashMap;
import java.util.Map;

public enum LLSDNodeType {
    llsdRoot("llsd"),
    llsdUndef("undef"),
    llsdBoolean("boolean"),
    llsdInteger("integer"),
    llsdDouble("real"),
    llsdUUID("uuid"),
    llsdString("string"),
    llsdDate("date"),
    llsdURI("uri"),
    llsdBinary("binary"),
    llsdArray("array"),
    llsdMap("map"),
    llsdKey("key");
    
    private static final Map<String, LLSDNodeType> tagMap = null;
    private final String tagName;

    static {
        tagMap = new HashMap(values().length * 2);
        for (LLSDNodeType lLSDNodeType : values()) {
            tagMap.put(lLSDNodeType.tagName, lLSDNodeType);
        }
    }

    private LLSDNodeType(String str) {
        this.tagName = str;
    }

    public static LLSDNodeType byTag(String str) {
        return tagMap.get(str);
    }
}

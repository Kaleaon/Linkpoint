package com.lumiyaviewer.lumiya.slproto.llsd

import java.util.HashMap
import java.util.Map

enum LLSDNodeType {
    llsdRoot("llsd"),
    llsdUndef("undef"),
    llsdBoolean("Boolean"),
    llsdInteger("integer"),
    llsdDouble("real"),
    llsdUUID("uuid"),
    llsdString("string"),
    llsdDate("date"),
    llsdURI("uri"),
    llsdBinary("binary"),
    llsdArray("array"),
    llsdMap("map"),
    llsdKey("key")
    
    private Map<String, LLSDNodeType> tagMap
    private String tagName

    {
        tagMap = HashMap<>(values().length * 2)
        for (LLSDNodeType lLSDNodeType : values()) {
            tagMap.put(lLSDNodeType.tagName, lLSDNodeType)
        }
    }

    private LLSDNodeType(String str) {
        this.tagName = str
    }

    LLSDNodeType byTag(String str) {
        return tagMap.get(str)
    }
}

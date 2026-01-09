package com.linkpoint.slproto.llsd

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
        tagMap = HashMap<>(values().size * 2)
        for (LLSDNodeType lLSDNodeType : values()) {
            tagMap.put(lLSDNodeType.tagName, lLSDNodeType)
        }
    }

    private LLSDNodeType(String str) {
        this.tagName = str
    }

    fun byTag(String str): LLSDNodeType {
        return tagMap.get(str)
    }
}

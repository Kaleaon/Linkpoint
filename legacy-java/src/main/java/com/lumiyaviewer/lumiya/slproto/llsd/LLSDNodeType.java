package com.lumiyaviewer.lumiya.slproto.llsd;

import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
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

    private static final Map<String, LLSDNodeType> tagMap = new HashMap(valuesCustom().length * 2);
    private final String tagName;

    static {
        for (LLSDNodeType lLSDNodeType : valuesCustom()) {
            tagMap.put(lLSDNodeType.tagName, lLSDNodeType);
        }
    }

    LLSDNodeType(String str) {
        this.tagName = str;
    }

    public static LLSDNodeType byTag(String str) {
        return tagMap.get(str);
    }

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static LLSDNodeType[] valuesCustom() {
        return values();
    }
}

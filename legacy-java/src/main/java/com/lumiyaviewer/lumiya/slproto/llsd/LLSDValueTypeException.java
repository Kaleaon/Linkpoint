package com.lumiyaviewer.lumiya.slproto.llsd;

/* loaded from: classes.dex */
public class LLSDValueTypeException extends LLSDException {
    private static final long serialVersionUID = -1831477542961670453L;

    public LLSDValueTypeException() {
        super("Invalid value type");
    }

    public LLSDValueTypeException(String str, LLSDNode lLSDNode) {
        super("Invalid value type: requested " + str + ", actual " + lLSDNode.getClass().getSimpleName());
    }
}

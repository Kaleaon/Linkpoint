// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.llsd;

public class LLSDValueTypeException extends LLSDException
{
    private static final long serialVersionUID = -1831477542961670453L;
    
    public LLSDValueTypeException() {
        super("Invalid value type");
    }
    
    public LLSDValueTypeException(final String str, final LLSDNode llsdNode) {
        super("Invalid value type: requested " + str + ", actual " + llsdNode.getClass().getSimpleName());
    }
}

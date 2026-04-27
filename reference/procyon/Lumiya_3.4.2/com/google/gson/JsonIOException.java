// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson;

public final class JsonIOException extends JsonParseException
{
    private static final long serialVersionUID = 1L;
    
    public JsonIOException(final String s) {
        super(s);
    }
    
    public JsonIOException(final String s, final Throwable t) {
        super(s, t);
    }
    
    public JsonIOException(final Throwable t) {
        super(t);
    }
}

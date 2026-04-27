// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson;

public final class JsonNull extends JsonElement
{
    public static final JsonNull INSTANCE;
    
    static {
        INSTANCE = new JsonNull();
    }
    
    @Deprecated
    public JsonNull() {
    }
    
    @Override
    JsonNull deepCopy() {
        return JsonNull.INSTANCE;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = false;
        if (this == o || o instanceof JsonNull) {
            b = true;
        }
        return b;
    }
    
    @Override
    public int hashCode() {
        return JsonNull.class.hashCode();
    }
}

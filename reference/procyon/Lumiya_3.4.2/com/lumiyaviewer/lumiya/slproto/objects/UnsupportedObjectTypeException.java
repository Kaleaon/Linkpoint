// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.objects;

public class UnsupportedObjectTypeException extends Exception
{
    public UnsupportedObjectTypeException(final byte b) {
        super(String.format("Unsupported object type: 0x%x", b));
    }
}

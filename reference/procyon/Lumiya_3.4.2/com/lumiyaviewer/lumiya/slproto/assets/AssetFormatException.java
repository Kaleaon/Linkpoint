// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.assets;

public class AssetFormatException extends Exception
{
    private static final long serialVersionUID = -8391424207465457690L;
    
    public AssetFormatException() {
        super("Unsupported asset format");
    }
    
    public AssetFormatException(final String message) {
        super(message);
    }
    
    public AssetFormatException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

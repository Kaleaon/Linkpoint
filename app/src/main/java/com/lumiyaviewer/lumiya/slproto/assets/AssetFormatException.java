package com.lumiyaviewer.lumiya.slproto.assets;

public class AssetFormatException extends Exception {
    private static final long serialVersionUID = -8391424207465457690L;

    public AssetFormatException() {
        super("Unsupported asset format");
    }

    public AssetFormatException(String str) {
        super(str);
    }

    public AssetFormatException(String str, Throwable th) {
        super(str, th);
    }
}

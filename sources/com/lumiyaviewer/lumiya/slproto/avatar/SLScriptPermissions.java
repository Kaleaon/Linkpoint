package com.lumiyaviewer.lumiya.slproto.avatar;

public enum SLScriptPermissions {
    SCRIPT_PERMISSION_DEBIT(2, "take Linden dollars (L$) from you"),
    SCRIPT_PERMISSION_TAKE_CONTROLS(4, "act on your control inputs"),
    SCRIPT_PERMISSION_REMAP_CONTROLS(8, "remap your control inputs"),
    SCRIPT_PERMISSION_TRIGGER_ANIMATION(16, "animate your avatar"),
    SCRIPT_PERMISSION_ATTACH(32, "attach to your avatar"),
    SCRIPT_PERMISSION_RELEASE_OWNERSHIP(64, "release ownership and become public"),
    SCRIPT_PERMISSION_CHANGE_LINKS(128, "link and delink from other objects"),
    SCRIPT_PERMISSION_CHANGE_JOINTS(256, "add and remove joints with other objects"),
    SCRIPT_PERMISSION_CHANGE_PERMISSIONS(512, "change its permissions"),
    SCRIPT_PERMISSION_TRACK_CAMERA(1024, "track your camera"),
    SCRIPT_PERMISSION_CONTROL_CAMERA(2048, "control your camera");
    
    private String message;
    private int permMask;

    private SLScriptPermissions(int i, String str) {
        this.permMask = i;
        this.message = str;
    }

    public String getMessage() {
        return this.message;
    }

    public int getPermMask() {
        return this.permMask;
    }
}

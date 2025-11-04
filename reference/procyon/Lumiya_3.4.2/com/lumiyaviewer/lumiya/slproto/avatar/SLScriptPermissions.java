// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.avatar;

public enum SLScriptPermissions
{
    SCRIPT_PERMISSION_ATTACH("SCRIPT_PERMISSION_ATTACH", 4, 32, "attach to your avatar"), 
    SCRIPT_PERMISSION_CHANGE_JOINTS("SCRIPT_PERMISSION_CHANGE_JOINTS", 7, 256, "add and remove joints with other objects"), 
    SCRIPT_PERMISSION_CHANGE_LINKS("SCRIPT_PERMISSION_CHANGE_LINKS", 6, 128, "link and delink from other objects"), 
    SCRIPT_PERMISSION_CHANGE_PERMISSIONS("SCRIPT_PERMISSION_CHANGE_PERMISSIONS", 8, 512, "change its permissions"), 
    SCRIPT_PERMISSION_CONTROL_CAMERA("SCRIPT_PERMISSION_CONTROL_CAMERA", 10, 2048, "control your camera"), 
    SCRIPT_PERMISSION_DEBIT("SCRIPT_PERMISSION_DEBIT", 0, 2, "take Linden dollars (L$) from you"), 
    SCRIPT_PERMISSION_RELEASE_OWNERSHIP("SCRIPT_PERMISSION_RELEASE_OWNERSHIP", 5, 64, "release ownership and become public"), 
    SCRIPT_PERMISSION_REMAP_CONTROLS("SCRIPT_PERMISSION_REMAP_CONTROLS", 2, 8, "remap your control inputs"), 
    SCRIPT_PERMISSION_TAKE_CONTROLS("SCRIPT_PERMISSION_TAKE_CONTROLS", 1, 4, "act on your control inputs"), 
    SCRIPT_PERMISSION_TRACK_CAMERA("SCRIPT_PERMISSION_TRACK_CAMERA", 9, 1024, "track your camera"), 
    SCRIPT_PERMISSION_TRIGGER_ANIMATION("SCRIPT_PERMISSION_TRIGGER_ANIMATION", 3, 16, "animate your avatar");
    
    private String message;
    private int permMask;
    
    private SLScriptPermissions(final String name, final int ordinal, final int permMask, final String message) {
        this.permMask = permMask;
        this.message = message;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public int getPermMask() {
        return this.permMask;
    }
}

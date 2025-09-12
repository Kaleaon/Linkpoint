// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;


public final class SLScriptPermissions extends Enum
{

    private static final SLScriptPermissions $VALUES[];
    public static final SLScriptPermissions SCRIPT_PERMISSION_ATTACH;
    public static final SLScriptPermissions SCRIPT_PERMISSION_CHANGE_JOINTS;
    public static final SLScriptPermissions SCRIPT_PERMISSION_CHANGE_LINKS;
    public static final SLScriptPermissions SCRIPT_PERMISSION_CHANGE_PERMISSIONS;
    public static final SLScriptPermissions SCRIPT_PERMISSION_CONTROL_CAMERA;
    public static final SLScriptPermissions SCRIPT_PERMISSION_DEBIT;
    public static final SLScriptPermissions SCRIPT_PERMISSION_RELEASE_OWNERSHIP;
    public static final SLScriptPermissions SCRIPT_PERMISSION_REMAP_CONTROLS;
    public static final SLScriptPermissions SCRIPT_PERMISSION_TAKE_CONTROLS;
    public static final SLScriptPermissions SCRIPT_PERMISSION_TRACK_CAMERA;
    public static final SLScriptPermissions SCRIPT_PERMISSION_TRIGGER_ANIMATION;
    private String message;
    private int permMask;

    private SLScriptPermissions(String s, int i, int j, String s1)
    {
        super(s, i);
        permMask = j;
        message = s1;
    }

    public static SLScriptPermissions valueOf(String s)
    {
        return (SLScriptPermissions)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/avatar/SLScriptPermissions, s);
    }

    public static SLScriptPermissions[] values()
    {
        return $VALUES;
    }

    public String getMessage()
    {
        return message;
    }

    public int getPermMask()
    {
        return permMask;
    }

    static 
    {
        SCRIPT_PERMISSION_DEBIT = new SLScriptPermissions("SCRIPT_PERMISSION_DEBIT", 0, 2, "take Linden dollars (L$) from you");
        SCRIPT_PERMISSION_TAKE_CONTROLS = new SLScriptPermissions("SCRIPT_PERMISSION_TAKE_CONTROLS", 1, 4, "act on your control inputs");
        SCRIPT_PERMISSION_REMAP_CONTROLS = new SLScriptPermissions("SCRIPT_PERMISSION_REMAP_CONTROLS", 2, 8, "remap your control inputs");
        SCRIPT_PERMISSION_TRIGGER_ANIMATION = new SLScriptPermissions("SCRIPT_PERMISSION_TRIGGER_ANIMATION", 3, 16, "animate your avatar");
        SCRIPT_PERMISSION_ATTACH = new SLScriptPermissions("SCRIPT_PERMISSION_ATTACH", 4, 32, "attach to your avatar");
        SCRIPT_PERMISSION_RELEASE_OWNERSHIP = new SLScriptPermissions("SCRIPT_PERMISSION_RELEASE_OWNERSHIP", 5, 64, "release ownership and become public");
        SCRIPT_PERMISSION_CHANGE_LINKS = new SLScriptPermissions("SCRIPT_PERMISSION_CHANGE_LINKS", 6, 128, "link and delink from other objects");
        SCRIPT_PERMISSION_CHANGE_JOINTS = new SLScriptPermissions("SCRIPT_PERMISSION_CHANGE_JOINTS", 7, 256, "add and remove joints with other objects");
        SCRIPT_PERMISSION_CHANGE_PERMISSIONS = new SLScriptPermissions("SCRIPT_PERMISSION_CHANGE_PERMISSIONS", 8, 512, "change its permissions");
        SCRIPT_PERMISSION_TRACK_CAMERA = new SLScriptPermissions("SCRIPT_PERMISSION_TRACK_CAMERA", 9, 1024, "track your camera");
        SCRIPT_PERMISSION_CONTROL_CAMERA = new SLScriptPermissions("SCRIPT_PERMISSION_CONTROL_CAMERA", 10, 2048, "control your camera");
        $VALUES = (new SLScriptPermissions[] {
            SCRIPT_PERMISSION_DEBIT, SCRIPT_PERMISSION_TAKE_CONTROLS, SCRIPT_PERMISSION_REMAP_CONTROLS, SCRIPT_PERMISSION_TRIGGER_ANIMATION, SCRIPT_PERMISSION_ATTACH, SCRIPT_PERMISSION_RELEASE_OWNERSHIP, SCRIPT_PERMISSION_CHANGE_LINKS, SCRIPT_PERMISSION_CHANGE_JOINTS, SCRIPT_PERMISSION_CHANGE_PERMISSIONS, SCRIPT_PERMISSION_TRACK_CAMERA, 
            SCRIPT_PERMISSION_CONTROL_CAMERA
        });
    }
}

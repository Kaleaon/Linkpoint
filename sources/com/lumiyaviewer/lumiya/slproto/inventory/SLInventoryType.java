// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import java.util.HashMap;
import java.util.Map;

public final class SLInventoryType extends Enum
{

    private static final SLInventoryType $VALUES[];
    public static final SLInventoryType IT_ANIMATION;
    public static final SLInventoryType IT_ATTACHMENT;
    public static final SLInventoryType IT_CALLINGCARD;
    public static final SLInventoryType IT_CATEGORY;
    public static final SLInventoryType IT_GESTURE;
    public static final SLInventoryType IT_LANDMARK;
    public static final SLInventoryType IT_LSL;
    public static final SLInventoryType IT_MESH;
    public static final SLInventoryType IT_NOTECARD;
    public static final SLInventoryType IT_OBJECT;
    public static final SLInventoryType IT_ROOT_CATEGORY;
    public static final SLInventoryType IT_SNAPSHOT;
    public static final SLInventoryType IT_SOUND;
    public static final SLInventoryType IT_TEXTURE;
    public static final SLInventoryType IT_TRASH;
    public static final SLInventoryType IT_UNKNOWN;
    public static final SLInventoryType IT_WEARABLE;
    public static final SLInventoryType IT_WIDGET;
    private static final Map tagMap;
    private String readableName;
    private String stringCode;
    private int typeCode;

    private SLInventoryType(String s, int i, int j, String s1, String s2)
    {
        super(s, i);
        typeCode = j;
        stringCode = s1;
        readableName = s2;
    }

    public static SLInventoryType getByString(String s)
    {
        SLInventoryType slinventorytype = (SLInventoryType)tagMap.get(s);
        s = slinventorytype;
        if (slinventorytype == null)
        {
            s = IT_UNKNOWN;
        }
        return s;
    }

    public static SLInventoryType getByType(int i)
    {
        SLInventoryType aslinventorytype[] = values();
        int j = 0;
        for (int k = aslinventorytype.length; j < k; j++)
        {
            SLInventoryType slinventorytype = aslinventorytype[j];
            if (slinventorytype.typeCode == i)
            {
                return slinventorytype;
            }
        }

        return IT_UNKNOWN;
    }

    public static SLInventoryType valueOf(String s)
    {
        return (SLInventoryType)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryType, s);
    }

    public static SLInventoryType[] values()
    {
        return $VALUES;
    }

    public String getReadableName()
    {
        return readableName;
    }

    public String getStringCode()
    {
        return stringCode;
    }

    public int getTypeCode()
    {
        return typeCode;
    }

    static 
    {
        int i = 0;
        IT_TEXTURE = new SLInventoryType("IT_TEXTURE", 0, 0, "texture", "Texture");
        IT_SOUND = new SLInventoryType("IT_SOUND", 1, 1, "sound", "Sound");
        IT_CALLINGCARD = new SLInventoryType("IT_CALLINGCARD", 2, 2, "callcard", "Calling card");
        IT_LANDMARK = new SLInventoryType("IT_LANDMARK", 3, 3, "landmark", "Landmark");
        IT_OBJECT = new SLInventoryType("IT_OBJECT", 4, 6, "object", "Object");
        IT_NOTECARD = new SLInventoryType("IT_NOTECARD", 5, 7, "notecard", "Note card");
        IT_CATEGORY = new SLInventoryType("IT_CATEGORY", 6, 8, "category", "Folder");
        IT_ROOT_CATEGORY = new SLInventoryType("IT_ROOT_CATEGORY", 7, 9, "root", "Root folder");
        IT_LSL = new SLInventoryType("IT_LSL", 8, 10, "script", "Script");
        IT_TRASH = new SLInventoryType("IT_TRASH", 9, 14, "trash", "Trash");
        IT_SNAPSHOT = new SLInventoryType("IT_SNAPSHOT", 10, 15, "snapshot", "Snapshot");
        IT_ATTACHMENT = new SLInventoryType("IT_ATTACHMENT", 11, 17, "attach", "Attachment");
        IT_WEARABLE = new SLInventoryType("IT_WEARABLE", 12, 18, "wearable", "Wearable");
        IT_ANIMATION = new SLInventoryType("IT_ANIMATION", 13, 19, "animation", "Animation");
        IT_GESTURE = new SLInventoryType("IT_GESTURE", 14, 20, "gesture", "Gesture");
        IT_MESH = new SLInventoryType("IT_MESH", 15, 22, "mesh", "Mesh");
        IT_WIDGET = new SLInventoryType("IT_WIDGET", 16, 23, "widget", "Widget");
        IT_UNKNOWN = new SLInventoryType("IT_UNKNOWN", 17, -1, "unknown", "Unknown");
        $VALUES = (new SLInventoryType[] {
            IT_TEXTURE, IT_SOUND, IT_CALLINGCARD, IT_LANDMARK, IT_OBJECT, IT_NOTECARD, IT_CATEGORY, IT_ROOT_CATEGORY, IT_LSL, IT_TRASH, 
            IT_SNAPSHOT, IT_ATTACHMENT, IT_WEARABLE, IT_ANIMATION, IT_GESTURE, IT_MESH, IT_WIDGET, IT_UNKNOWN
        });
        tagMap = new HashMap(values().length * 2);
        SLInventoryType aslinventorytype[] = values();
        for (int j = aslinventorytype.length; i < j; i++)
        {
            SLInventoryType slinventorytype = aslinventorytype[i];
            tagMap.put(slinventorytype.stringCode, slinventorytype);
        }

    }
}

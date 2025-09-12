// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import java.util.HashMap;
import java.util.Map;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventoryHTTPFetchRequest

private static final class  extends Enum
{

    private static final toString $VALUES[];
    public static final toString agent_id;
    public static final toString category_id;
    public static final toString folder_id;
    public static final toString name;
    public static final toString parent_id;
    public static final toString preferred_type;
    private static final Map tagMap;
    public static final toString type;
    public static final toString type_default;
    public static final toString version;

    public static  byTag(String s)
    {
        return ()tagMap.get(s);
    }

    public static tagMap valueOf(String s)
    {
        return (tagMap)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$FolderValueKey, s);
    }

    public static tagMap[] values()
    {
        return $VALUES;
    }

    static 
    {
        int i = 0;
        category_id = new <init>("category_id", 0);
        folder_id = new <init>("folder_id", 1);
        agent_id = new <init>("agent_id", 2);
        name = new <init>("name", 3);
        type_default = new <init>("type_default", 4);
        type = new <init>("type", 5);
        version = new <init>("version", 6);
        parent_id = new <init>("parent_id", 7);
        preferred_type = new <init>("preferred_type", 8);
        $VALUES = (new .VALUES[] {
            category_id, folder_id, agent_id, name, type_default, type, version, parent_id, preferred_type
        });
        tagMap = new HashMap(values().length * 2);
        .VALUES avalues[] = values();
        for (int j = avalues.length; i < j; i++)
        {
            .VALUES values1 = avalues[i];
            tagMap.put(values1.toString(), values1);
        }

    }

    private (String s, int i)
    {
        super(s, i);
    }
}

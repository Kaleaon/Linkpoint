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
    public static final toString asset_id;
    public static final toString created_at;
    public static final toString desc;
    public static final toString flags;
    public static final toString inv_type;
    public static final toString item_id;
    public static final toString name;
    public static final toString parent_id;
    private static final Map tagMap;
    public static final toString type;

    public static  byTag(String s)
    {
        return ()tagMap.get(s);
    }

    public static tagMap valueOf(String s)
    {
        return (tagMap)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$ItemValueKey, s);
    }

    public static tagMap[] values()
    {
        return $VALUES;
    }

    static 
    {
        int i = 0;
        item_id = new <init>("item_id", 0);
        name = new <init>("name", 1);
        parent_id = new <init>("parent_id", 2);
        agent_id = new <init>("agent_id", 3);
        type = new <init>("type", 4);
        inv_type = new <init>("inv_type", 5);
        desc = new <init>("desc", 6);
        flags = new <init>("flags", 7);
        created_at = new <init>("created_at", 8);
        asset_id = new <init>("asset_id", 9);
        $VALUES = (new .VALUES[] {
            item_id, name, parent_id, agent_id, type, inv_type, desc, flags, created_at, asset_id
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

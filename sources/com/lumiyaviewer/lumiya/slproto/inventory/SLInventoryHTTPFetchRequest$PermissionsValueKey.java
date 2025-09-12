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
    public static final toString base_mask;
    public static final toString creator_id;
    public static final toString everyone_mask;
    public static final toString group_id;
    public static final toString group_mask;
    public static final toString is_owner_group;
    public static final toString last_owner_id;
    public static final toString next_owner_mask;
    public static final toString owner_id;
    public static final toString owner_mask;
    private static final Map tagMap;

    public static  byTag(String s)
    {
        return ()tagMap.get(s);
    }

    public static tagMap valueOf(String s)
    {
        return (tagMap)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$PermissionsValueKey, s);
    }

    public static tagMap[] values()
    {
        return $VALUES;
    }

    static 
    {
        int i = 0;
        creator_id = new <init>("creator_id", 0);
        group_id = new <init>("group_id", 1);
        owner_id = new <init>("owner_id", 2);
        last_owner_id = new <init>("last_owner_id", 3);
        is_owner_group = new <init>("is_owner_group", 4);
        base_mask = new <init>("base_mask", 5);
        owner_mask = new <init>("owner_mask", 6);
        next_owner_mask = new <init>("next_owner_mask", 7);
        group_mask = new <init>("group_mask", 8);
        everyone_mask = new <init>("everyone_mask", 9);
        $VALUES = (new .VALUES[] {
            creator_id, group_id, owner_id, last_owner_id, is_owner_group, base_mask, owner_mask, next_owner_mask, group_mask, everyone_mask
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

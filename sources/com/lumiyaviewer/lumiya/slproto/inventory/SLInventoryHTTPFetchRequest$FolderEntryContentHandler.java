// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDValueTypeException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventoryHTTPFetchRequest, SLInventoryEntry, SLAssetType, SLInventoryType

private class entry extends com.lumiyaviewer.lumiya.slproto.llsd.dler
{

    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues[];
    final int $SWITCH_TABLE$com$lumiyaviewer$lumiya$slproto$inventory$SLInventoryHTTPFetchRequest$FolderValueKey[];
    private final ey commitThread;
    private final SLInventoryEntry entry = new SLInventoryEntry();
    final SLInventoryHTTPFetchRequest this$0;

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues;
        }
        int ai[] = new int[ues().length];
        try
        {
            ai[ues.ues()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror8) { }
        try
        {
            ai[.()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror7) { }
        try
        {
            ai[.()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror6) { }
        try
        {
            ai[.()] = 4;
        }
        catch (NoSuchFieldError nosuchfielderror5) { }
        try
        {
            ai[.()] = 5;
        }
        catch (NoSuchFieldError nosuchfielderror4) { }
        try
        {
            ai[ype.ype()] = 6;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[ype.ype()] = 7;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[t.t()] = 8;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[t.t()] = 9;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues = ai;
        return ai;
    }

    public void onMapEnd(String s)
        throws LLSDXMLException, InterruptedException
    {
        if (entry.parentUUID == null)
        {
            entry.parentUUID = folderEntry.parentUUID;
            entry.parent_id = folderEntry.getId();
        }
        if (entry.agentUUID == null)
        {
            entry.agentUUID = folderEntry.agentUUID;
        }
        commitThread._mthtry(entry);
    }

    public void onPrimitiveValue(String s, LLSDNode llsdnode)
        throws LLSDXMLException, LLSDValueTypeException
    {
        entry entry1 = entry(s);
        if (entry1 != null)
        {
            switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues()[entry1.Values()])
            {
            case 6: // '\006'
            default:
                return;

            case 1: // '\001'
                entry.agentUUID = llsdnode.asUUID();
                return;

            case 2: // '\002'
                entry.uuid = llsdnode.asUUID();
                return;

            case 3: // '\003'
                entry.uuid = llsdnode.asUUID();
                return;

            case 4: // '\004'
                entry.name = llsdnode.asString();
                return;

            case 5: // '\005'
                entry.parentUUID = llsdnode.asUUID();
                if (entry.parentUUID.equals(folderUUID))
                {
                    entry.parent_id = folderEntry.getId();
                    return;
                }
                s = db.findEntry(entry.parentUUID);
                if (s != null)
                {
                    entry.parent_id = s.getId();
                    return;
                } else
                {
                    entry.parent_id = 0L;
                    return;
                }

            case 7: // '\007'
                if (llsdnode.isInt())
                {
                    entry.typeDefault = llsdnode.asInt();
                    return;
                }
                s = SLAssetType.getByString(llsdnode.asString());
                if (s != SLAssetType.AT_UNKNOWN)
                {
                    entry.typeDefault = s.getInventoryType().getTypeCode();
                    return;
                } else
                {
                    entry.typeDefault = SLInventoryType.getByString(llsdnode.asString()).getTypeCode();
                    return;
                }

            case 8: // '\b'
                entry.typeDefault = llsdnode.asInt();
                return;

            case 9: // '\t'
                entry.version = llsdnode.asInt();
                return;
            }
        } else
        {
            Debug.Printf("InvFetch: Folder unknown key '%s'", new Object[] {
                s
            });
            return;
        }
    }

    ( )
    {
        this$0 = SLInventoryHTTPFetchRequest.this;
        super();
        commitThread = ;
        entry.isFolder = true;
    }
}

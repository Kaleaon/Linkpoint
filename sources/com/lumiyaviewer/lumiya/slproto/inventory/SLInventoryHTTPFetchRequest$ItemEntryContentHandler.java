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
//            SLInventoryHTTPFetchRequest, SLInventoryEntry, SLAssetType, SLInventoryType, 
//            SLSaleType

private class entry extends com.lumiyaviewer.lumiya.slproto.llsd.dler
{

    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues[];
    final int $SWITCH_TABLE$com$lumiyaviewer$lumiya$slproto$inventory$SLInventoryHTTPFetchRequest$ItemValueKey[];
    private final ey commitThread;
    private final SLInventoryEntry entry = new SLInventoryEntry();
    private final com.lumiyaviewer.lumiya.slproto.llsd.d permissionsHandler = new com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDDefaultContentHandler() {

        private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues[];
        final int $SWITCH_TABLE$com$lumiyaviewer$lumiya$slproto$inventory$SLInventoryHTTPFetchRequest$PermissionsValueKey[];
        final SLInventoryHTTPFetchRequest.ItemEntryContentHandler this$1;

        private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues()
        {
            if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues != null)
            {
                return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues;
            }
            int ai[] = new int[SLInventoryHTTPFetchRequest.PermissionsValueKey.values().length];
            try
            {
                ai[SLInventoryHTTPFetchRequest.PermissionsValueKey.base_mask.ordinal()] = 1;
            }
            catch (NoSuchFieldError nosuchfielderror9) { }
            try
            {
                ai[SLInventoryHTTPFetchRequest.PermissionsValueKey.creator_id.ordinal()] = 2;
            }
            catch (NoSuchFieldError nosuchfielderror8) { }
            try
            {
                ai[SLInventoryHTTPFetchRequest.PermissionsValueKey.everyone_mask.ordinal()] = 3;
            }
            catch (NoSuchFieldError nosuchfielderror7) { }
            try
            {
                ai[SLInventoryHTTPFetchRequest.PermissionsValueKey.group_id.ordinal()] = 4;
            }
            catch (NoSuchFieldError nosuchfielderror6) { }
            try
            {
                ai[SLInventoryHTTPFetchRequest.PermissionsValueKey.group_mask.ordinal()] = 5;
            }
            catch (NoSuchFieldError nosuchfielderror5) { }
            try
            {
                ai[SLInventoryHTTPFetchRequest.PermissionsValueKey.is_owner_group.ordinal()] = 6;
            }
            catch (NoSuchFieldError nosuchfielderror4) { }
            try
            {
                ai[SLInventoryHTTPFetchRequest.PermissionsValueKey.last_owner_id.ordinal()] = 7;
            }
            catch (NoSuchFieldError nosuchfielderror3) { }
            try
            {
                ai[SLInventoryHTTPFetchRequest.PermissionsValueKey.next_owner_mask.ordinal()] = 8;
            }
            catch (NoSuchFieldError nosuchfielderror2) { }
            try
            {
                ai[SLInventoryHTTPFetchRequest.PermissionsValueKey.owner_id.ordinal()] = 9;
            }
            catch (NoSuchFieldError nosuchfielderror1) { }
            try
            {
                ai[SLInventoryHTTPFetchRequest.PermissionsValueKey.owner_mask.ordinal()] = 10;
            }
            catch (NoSuchFieldError nosuchfielderror) { }
            _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues = ai;
            return ai;
        }

        public void onPrimitiveValue(String s, LLSDNode llsdnode)
            throws LLSDXMLException, LLSDValueTypeException
        {
            SLInventoryHTTPFetchRequest.PermissionsValueKey permissionsvaluekey = SLInventoryHTTPFetchRequest.PermissionsValueKey.byTag(s);
            if (permissionsvaluekey != null)
            {
                switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues()[permissionsvaluekey.ordinal()])
                {
                default:
                    return;

                case 1: // '\001'
                    SLInventoryHTTPFetchRequest.ItemEntryContentHandler._2D_get0(SLInventoryHTTPFetchRequest.ItemEntryContentHandler.this).baseMask = llsdnode.asInt();
                    return;

                case 3: // '\003'
                    SLInventoryHTTPFetchRequest.ItemEntryContentHandler._2D_get0(SLInventoryHTTPFetchRequest.ItemEntryContentHandler.this).everyoneMask = llsdnode.asInt();
                    return;

                case 5: // '\005'
                    SLInventoryHTTPFetchRequest.ItemEntryContentHandler._2D_get0(SLInventoryHTTPFetchRequest.ItemEntryContentHandler.this).groupMask = llsdnode.asInt();
                    return;

                case 8: // '\b'
                    SLInventoryHTTPFetchRequest.ItemEntryContentHandler._2D_get0(SLInventoryHTTPFetchRequest.ItemEntryContentHandler.this).nextOwnerMask = llsdnode.asInt();
                    return;

                case 10: // '\n'
                    SLInventoryHTTPFetchRequest.ItemEntryContentHandler._2D_get0(SLInventoryHTTPFetchRequest.ItemEntryContentHandler.this).ownerMask = llsdnode.asInt();
                    return;

                case 2: // '\002'
                    SLInventoryHTTPFetchRequest.ItemEntryContentHandler._2D_get0(SLInventoryHTTPFetchRequest.ItemEntryContentHandler.this).creatorUUID = llsdnode.asUUID();
                    return;

                case 4: // '\004'
                    SLInventoryHTTPFetchRequest.ItemEntryContentHandler._2D_get0(SLInventoryHTTPFetchRequest.ItemEntryContentHandler.this).groupUUID = llsdnode.asUUID();
                    return;

                case 7: // '\007'
                    SLInventoryHTTPFetchRequest.ItemEntryContentHandler._2D_get0(SLInventoryHTTPFetchRequest.ItemEntryContentHandler.this).lastOwnerUUID = llsdnode.asUUID();
                    return;

                case 9: // '\t'
                    SLInventoryHTTPFetchRequest.ItemEntryContentHandler._2D_get0(SLInventoryHTTPFetchRequest.ItemEntryContentHandler.this).ownerUUID = llsdnode.asUUID();
                    return;

                case 6: // '\006'
                    SLInventoryHTTPFetchRequest.ItemEntryContentHandler._2D_get0(SLInventoryHTTPFetchRequest.ItemEntryContentHandler.this).isGroupOwned = llsdnode.asBoolean();
                    return;
                }
            } else
            {
                Debug.Printf("InvFetch: Permissions unknown key '%s'", new Object[] {
                    s
                });
                return;
            }
        }

            
            {
                this$1 = SLInventoryHTTPFetchRequest.ItemEntryContentHandler.this;
                super();
            }
    };
    private final com.lumiyaviewer.lumiya.slproto.llsd.dler saleInfoHandler = new com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDDefaultContentHandler() {

        final SLInventoryHTTPFetchRequest.ItemEntryContentHandler this$1;

        public void onPrimitiveValue(String s, LLSDNode llsdnode)
            throws LLSDXMLException, LLSDValueTypeException
        {
            if (s.equals("sale_type"))
            {
                if (llsdnode.isString())
                {
                    SLInventoryHTTPFetchRequest.ItemEntryContentHandler._2D_get0(SLInventoryHTTPFetchRequest.ItemEntryContentHandler.this).saleType = SLSaleType.getByString(llsdnode.asString()).getTypeCode();
                    return;
                } else
                {
                    SLInventoryHTTPFetchRequest.ItemEntryContentHandler._2D_get0(SLInventoryHTTPFetchRequest.ItemEntryContentHandler.this).saleType = llsdnode.asInt();
                    return;
                }
            }
            if (s.equals("sale_price"))
            {
                SLInventoryHTTPFetchRequest.ItemEntryContentHandler._2D_get0(SLInventoryHTTPFetchRequest.ItemEntryContentHandler.this).salePrice = llsdnode.asInt();
                return;
            } else
            {
                Debug.Printf("InvFetch: Sale info unknown key '%s'", new Object[] {
                    s
                });
                return;
            }
        }

            
            {
                this$1 = SLInventoryHTTPFetchRequest.ItemEntryContentHandler.this;
                super();
            }
    };
    final SLInventoryHTTPFetchRequest this$0;

    static SLInventoryEntry _2D_get0(entry entry1)
    {
        return entry1.entry;
    }

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues;
        }
        int ai[] = new int[ues().length];
        try
        {
            ai[ues.ues()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror9) { }
        try
        {
            ai[ues.ues()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror8) { }
        try
        {
            ai[ues.ues()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror7) { }
        try
        {
            ai[ues.ues()] = 4;
        }
        catch (NoSuchFieldError nosuchfielderror6) { }
        try
        {
            ai[ues.ues()] = 5;
        }
        catch (NoSuchFieldError nosuchfielderror5) { }
        try
        {
            ai[ues.ues()] = 6;
        }
        catch (NoSuchFieldError nosuchfielderror4) { }
        try
        {
            ai[ues.ues()] = 7;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[ues.ues()] = 8;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[ues.ues()] = 9;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[ues.ues()] = 10;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues = ai;
        return ai;
    }

    public com.lumiyaviewer.lumiya.slproto.llsd.dler onMapBegin(String s)
        throws LLSDXMLException
    {
        if (s.equals("permissions"))
        {
            return permissionsHandler;
        }
        if (s.equals("sale_info"))
        {
            return saleInfoHandler;
        } else
        {
            return super.egin(s);
        }
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
        commitThread.Entry(entry);
    }

    public void onPrimitiveValue(String s, LLSDNode llsdnode)
        throws LLSDXMLException, LLSDValueTypeException
    {
        entry entry1 = entry(s);
        if (entry1 != null)
        {
            switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues()[entry1.Values()])
            {
            default:
                return;

            case 1: // '\001'
                entry.agentUUID = llsdnode.asUUID();
                return;

            case 7: // '\007'
                entry.uuid = llsdnode.asUUID();
                return;

            case 8: // '\b'
                entry.name = llsdnode.asString();
                return;

            case 2: // '\002'
                entry.assetUUID = llsdnode.asUUID();
                return;

            case 9: // '\t'
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

            case 10: // '\n'
                if (llsdnode.isInt())
                {
                    entry.assetType = llsdnode.asInt();
                    return;
                } else
                {
                    entry.assetType = SLAssetType.getByString(llsdnode.asString()).getTypeCode();
                    return;
                }

            case 6: // '\006'
                if (llsdnode.isInt())
                {
                    entry.invType = llsdnode.asInt();
                    return;
                } else
                {
                    entry.invType = SLInventoryType.getByString(llsdnode.asString()).getTypeCode();
                    return;
                }

            case 4: // '\004'
                entry.description = llsdnode.asString();
                return;

            case 3: // '\003'
                entry.creationDate = llsdnode.asInt();
                return;

            case 5: // '\005'
                entry.flags = llsdnode.asInt();
                return;
            }
        } else
        {
            Debug.Printf("InvFetch: Item unknown key '%s'", new Object[] {
                s
            });
            return;
        }
    }

    _cls2.this._cls1(_cls2.this._cls1 _pcls1)
    {
        this$0 = SLInventoryHTTPFetchRequest.this;
        super();
        commitThread = _pcls1;
        entry.isFolder = false;
    }
}

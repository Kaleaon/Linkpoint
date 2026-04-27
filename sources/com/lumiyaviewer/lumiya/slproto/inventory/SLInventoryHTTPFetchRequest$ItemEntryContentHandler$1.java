// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDValueTypeException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventoryHTTPFetchRequest, SLInventoryEntry

class this._cls1 extends com.lumiyaviewer.lumiya.slproto.llsd.er._cls1
{

    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues[];
    final int $SWITCH_TABLE$com$lumiyaviewer$lumiya$slproto$inventory$SLInventoryHTTPFetchRequest$PermissionsValueKey[];
    final alueKey this$1;

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues;
        }
        int ai[] = new int[().length];
        try
        {
            ai[ask.l()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror9) { }
        try
        {
            ai[r_id.l()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror8) { }
        try
        {
            ai[ne_mask.l()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror7) { }
        try
        {
            ai[id.l()] = 4;
        }
        catch (NoSuchFieldError nosuchfielderror6) { }
        try
        {
            ai[mask.l()] = 5;
        }
        catch (NoSuchFieldError nosuchfielderror5) { }
        try
        {
            ai[er_group.l()] = 6;
        }
        catch (NoSuchFieldError nosuchfielderror4) { }
        try
        {
            ai[wner_id.l()] = 7;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[wner_mask.l()] = 8;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[id.l()] = 9;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[mask.l()] = 10;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues = ai;
        return ai;
    }

    public void onPrimitiveValue(String s, LLSDNode llsdnode)
        throws LLSDXMLException, LLSDValueTypeException
    {
        esValues esvalues = esValues(s);
        if (esvalues != null)
        {
            switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues()[esvalues.l()])
            {
            default:
                return;

            case 1: // '\001'
                et0(this._cls1.this).baseMask = llsdnode.asInt();
                return;

            case 3: // '\003'
                et0(this._cls1.this).everyoneMask = llsdnode.asInt();
                return;

            case 5: // '\005'
                et0(this._cls1.this).groupMask = llsdnode.asInt();
                return;

            case 8: // '\b'
                et0(this._cls1.this).nextOwnerMask = llsdnode.asInt();
                return;

            case 10: // '\n'
                et0(this._cls1.this).ownerMask = llsdnode.asInt();
                return;

            case 2: // '\002'
                et0(this._cls1.this).creatorUUID = llsdnode.asUUID();
                return;

            case 4: // '\004'
                et0(this._cls1.this).groupUUID = llsdnode.asUUID();
                return;

            case 7: // '\007'
                et0(this._cls1.this).lastOwnerUUID = llsdnode.asUUID();
                return;

            case 9: // '\t'
                et0(this._cls1.this).ownerUUID = llsdnode.asUUID();
                return;

            case 6: // '\006'
                et0(this._cls1.this).isGroupOwned = llsdnode.asBoolean();
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

    ()
    {
        this$1 = this._cls1.this;
        super();
    }
}

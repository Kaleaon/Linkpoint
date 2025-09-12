// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDValueTypeException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventoryHTTPFetchRequest, SLSaleType, SLInventoryEntry

class this._cls1 extends com.lumiyaviewer.lumiya.slproto.llsd.er._cls2
{

    final this._cls1 this$1;

    public void onPrimitiveValue(String s, LLSDNode llsdnode)
        throws LLSDXMLException, LLSDValueTypeException
    {
        if (s.equals("sale_type"))
        {
            if (llsdnode.isString())
            {
                et0(this._cls1.this).saleType = SLSaleType.getByString(llsdnode.asString()).getTypeCode();
                return;
            } else
            {
                et0(this._cls1.this).saleType = llsdnode.asInt();
                return;
            }
        }
        if (s.equals("sale_price"))
        {
            et0(this._cls1.this).salePrice = llsdnode.asInt();
            return;
        } else
        {
            Debug.Printf("InvFetch: Sale info unknown key '%s'", new Object[] {
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

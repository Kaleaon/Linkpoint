// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventoryHTTPFetchRequest

class this._cls2 extends com.lumiyaviewer.lumiya.slproto.llsd.._cls1._cls1
{

    final r this$2;

    public com.lumiyaviewer.lumiya.slproto.llsd.._cls1._cls1 onMapBegin(String s)
        throws LLSDXMLException
    {
        return new r(_fld0, _mth0(_fld1), null);
    }

    is._cls1()
    {
        this$2 = this._cls2.this;
        super();
    }

    // Unreferenced inner class com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$RootContentHandler$1

/* anonymous class */
    class SLInventoryHTTPFetchRequest.RootContentHandler._cls1 extends com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDDefaultContentHandler
    {

        final SLInventoryHTTPFetchRequest.RootContentHandler this$1;

        public com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDContentHandler onArrayBegin(String s)
            throws LLSDXMLException
        {
            if (s.equals("folders"))
            {
                return new SLInventoryHTTPFetchRequest.RootContentHandler._cls1._cls1();
            } else
            {
                return super.onArrayBegin(s);
            }
        }

            
            {
                this$1 = SLInventoryHTTPFetchRequest.RootContentHandler.this;
                super();
            }
    }

}

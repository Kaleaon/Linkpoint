// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventoryHTTPFetchRequest

class this._cls1 extends com.lumiyaviewer.lumiya.slproto.llsd.er._cls1
{

    final egin this$1;

    public com.lumiyaviewer.lumiya.slproto.llsd.er._cls1 onArrayBegin(String s)
        throws LLSDXMLException
    {
        if (s.equals("folders"))
        {
            return new com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDDefaultContentHandler() {

                final SLInventoryHTTPFetchRequest.RootContentHandler._cls1 this$2;

                public com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDContentHandler onMapBegin(String s1)
                    throws LLSDXMLException
                {
                    return new SLInventoryHTTPFetchRequest.FolderDataContentHandler(this$0, SLInventoryHTTPFetchRequest.RootContentHandler._2D_get0(this$1), null);
                }

            
            {
                this$2 = SLInventoryHTTPFetchRequest.RootContentHandler._cls1.this;
                super();
            }
            };
        } else
        {
            return super.rrayBegin(s);
        }
    }

    _cls1.this._cls2()
    {
        this$1 = this._cls1.this;
        super();
    }
}

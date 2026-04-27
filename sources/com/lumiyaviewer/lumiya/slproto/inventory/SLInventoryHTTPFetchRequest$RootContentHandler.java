// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventoryHTTPFetchRequest

private class d extends com.lumiyaviewer.lumiya.slproto.llsd.dler
{

    private final d commitThread;
    final SLInventoryHTTPFetchRequest this$0;

    static d _2D_get0(d d)
    {
        return d.commitThread;
    }

    public com.lumiyaviewer.lumiya.slproto.llsd.dler onMapBegin(String s)
        throws LLSDXMLException
    {
        return new com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDDefaultContentHandler() {

            final SLInventoryHTTPFetchRequest.RootContentHandler this$1;

            public com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDContentHandler onArrayBegin(String s1)
                throws LLSDXMLException
            {
                if (s1.equals("folders"))
                {
                    return new com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDDefaultContentHandler() {

                        final _cls1 this$2;

                        public com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDContentHandler onMapBegin(String s)
                            throws LLSDXMLException
                        {
                            return new SLInventoryHTTPFetchRequest.FolderDataContentHandler(this$0, SLInventoryHTTPFetchRequest.RootContentHandler._2D_get0(_fld1), null);
                        }

            
            {
                this$2 = _cls1.this;
                super();
            }
                    };
                } else
                {
                    return super.onArrayBegin(s1);
                }
            }

            
            {
                this$1 = SLInventoryHTTPFetchRequest.RootContentHandler.this;
                super();
            }
        };
    }

    private d(d d)
    {
        this$0 = SLInventoryHTTPFetchRequest.this;
        super();
        commitThread = d;
    }

    d(d d, d d1)
    {
        this(d);
    }
}

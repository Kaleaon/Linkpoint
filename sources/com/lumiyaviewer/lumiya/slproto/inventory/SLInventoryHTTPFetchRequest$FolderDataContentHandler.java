// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDValueTypeException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventoryHTTPFetchRequest, SLInventoryEntry

private class <init> extends com.lumiyaviewer.lumiya.slproto.llsd.dler
{

    private final gotUUID commitThread;
    private UUID gotUUID;
    private int gotVersion;
    final SLInventoryHTTPFetchRequest this$0;

    static <init> _2D_get0(<init> <init>1)
    {
        return <init>1.commitThread;
    }

    public com.lumiyaviewer.lumiya.slproto.llsd.dler onArrayBegin(String s)
        throws LLSDXMLException
    {
        if (s.equals("categories"))
        {
            return new com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDDefaultContentHandler() {

                final SLInventoryHTTPFetchRequest.FolderDataContentHandler this$1;

                public com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDContentHandler onMapBegin(String s1)
                    throws LLSDXMLException
                {
                    return new SLInventoryHTTPFetchRequest.FolderEntryContentHandler(this$0, SLInventoryHTTPFetchRequest.FolderDataContentHandler._2D_get0(SLInventoryHTTPFetchRequest.FolderDataContentHandler.this));
                }

            
            {
                this$1 = SLInventoryHTTPFetchRequest.FolderDataContentHandler.this;
                super();
            }
            };
        }
        if (s.equals("items"))
        {
            return new com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDDefaultContentHandler() {

                final SLInventoryHTTPFetchRequest.FolderDataContentHandler this$1;

                public com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDContentHandler onMapBegin(String s1)
                    throws LLSDXMLException
                {
                    return new SLInventoryHTTPFetchRequest.ItemEntryContentHandler(this$0, SLInventoryHTTPFetchRequest.FolderDataContentHandler._2D_get0(SLInventoryHTTPFetchRequest.FolderDataContentHandler.this));
                }

            
            {
                this$1 = SLInventoryHTTPFetchRequest.FolderDataContentHandler.this;
                super();
            }
            };
        } else
        {
            return super.Begin(s);
        }
    }

    public void onMapEnd(String s)
        throws LLSDXMLException, InterruptedException
    {
        if (gotUUID != null && gotUUID.equals(folderUUID) && gotVersion != folderEntry.version)
        {
            folderEntry.version = gotVersion;
            commitThread.ntry(folderEntry);
        }
    }

    public void onPrimitiveValue(String s, LLSDNode llsdnode)
        throws LLSDXMLException, LLSDValueTypeException
    {
        Debug.Printf("InvFetch: FolderDataContentHandler: key '%s' value '%s'", new Object[] {
            s, llsdnode
        });
        if (s.equals("version"))
        {
            gotVersion = llsdnode.asInt();
        } else
        if (s.equals("folder_id"))
        {
            gotUUID = llsdnode.asUUID();
            return;
        }
    }

    private _cls2.this._cls1(_cls2.this._cls1 _pcls1)
    {
        this$0 = SLInventoryHTTPFetchRequest.this;
        super();
        commitThread = _pcls1;
    }

    commitThread(commitThread committhread, commitThread committhread1)
    {
        this(committhread);
    }
}

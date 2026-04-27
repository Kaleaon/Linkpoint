// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.https.LLSDStreamingXMLRequest;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDArray;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventoryHTTPFetchRequest

class this._cls0
    implements Runnable
{

    final SLInventoryHTTPFetchRequest this$0;

    public void run()
    {
        Object obj;
        Object obj1;
        long l;
        l = System.currentTimeMillis();
        Debug.Printf("InventoryFetcher: Going to fetch folder: %s", new Object[] {
            folderUUID
        });
        obj = new LLSDStreamingXMLRequest();
        obj1 = new LLSDArray();
        ((LLSDArray) (obj1)).add(new LLSDMap(new com.lumiyaviewer.lumiya.slproto.llsd.types.derUUID[] {
            new com.lumiyaviewer.lumiya.slproto.llsd.types.derUUID("folder_id", new LLSDUUID(folderUUID)), new com.lumiyaviewer.lumiya.slproto.llsd.types.derUUID("fetch_folders", new LLSDBoolean(true)), new com.lumiyaviewer.lumiya.slproto.llsd.types.derUUID("fetch_items", new LLSDBoolean(true))
        }));
        obj1 = new LLSDMap(new com.lumiyaviewer.lumiya.slproto.llsd.types.derUUID[] {
            new com.lumiyaviewer.lumiya.slproto.llsd.types.derUUID("folders", ((LLSDNode) (obj1)))
        });
        SLInventoryHTTPFetchRequest._2D_get2(SLInventoryHTTPFetchRequest.this).set(obj);
        int i;
        boolean flag2;
        i = 0;
        flag2 = false;
_L9:
        boolean flag = flag2;
        if (i >= 3) goto _L2; else goto _L1
_L1:
        boolean flag1 = flag2;
        tabaseCommitThread tabasecommitthread = new tabaseCommitThread(SLInventoryHTTPFetchRequest.this, null);
        flag1 = flag2;
        tabasecommitthread.start();
        flag1 = flag2;
        Debug.Printf("InventoryFetcher: Starting HTTP request for folder: %s", new Object[] {
            folderUUID
        });
        flag1 = flag2;
        ((LLSDStreamingXMLRequest) (obj)).PerformRequest(SLInventoryHTTPFetchRequest._2D_get0(SLInventoryHTTPFetchRequest.this), ((LLSDNode) (obj1)), new otContentHandler(SLInventoryHTTPFetchRequest.this, tabasecommitthread, null));
        flag1 = flag2;
        Debug.Printf("InvFetch: done parsing,  waiting for commit thread", new Object[0]);
        flag1 = flag2;
        tabasecommitthread.stopAndWait(true);
        flag1 = flag2;
        Debug.Printf("InvFetch: commit thread finished", new Object[0]);
        flag2 = true;
_L5:
        if (!flag2) goto _L4; else goto _L3
_L3:
        flag = flag2;
_L2:
        flag1 = flag;
        Debug.Printf("InventoryFetcher: Fetched folder: %s (fetch time = %d)", new Object[] {
            folderUUID.toString(), Long.valueOf(System.currentTimeMillis() - l)
        });
        flag1 = flag;
        SLInventoryHTTPFetchRequest._2D_get2(SLInventoryHTTPFetchRequest.this).set(null);
_L6:
        Object obj2;
        boolean flag3;
        if (!Thread.interrupted())
        {
            flag1 = SLInventoryHTTPFetchRequest._2D_get1(SLInventoryHTTPFetchRequest.this).get();
        } else
        {
            flag1 = true;
        }
        obj1 = folderUUID.toString();
        if (flag)
        {
            obj = "true";
        } else
        {
            obj = "false";
        }
        Debug.Printf("InventoryFetcher: done processing folder %s: success %s cancelled %b", new Object[] {
            obj1, obj, Boolean.valueOf(flag1)
        });
        completeFetch(flag, flag1);
        return;
        obj2;
        flag1 = flag2;
        ((IOException) (obj2)).printStackTrace();
          goto _L5
        obj;
_L10:
        Debug.Warning(((Throwable) (obj)));
        flag = flag1;
          goto _L6
        obj2;
        flag1 = flag2;
        ((LLSDXMLException) (obj2)).printStackTrace();
        try
        {
            Debug.Log((new StringBuilder()).append("InventoryFetcher: malformed xml after req = ").append(((LLSDNode) (obj1)).serializeToXML()).toString());
        }
        // Misplaced declaration of an exception variable
        catch (Object obj2) { }
          goto _L5
_L4:
        flag1 = flag2;
        tabasecommitthread.interrupt();
        flag = flag2;
        flag1 = flag2;
        if (Thread.interrupted()) goto _L2; else goto _L7
_L7:
        flag1 = flag2;
        flag3 = SLInventoryHTTPFetchRequest._2D_get1(SLInventoryHTTPFetchRequest.this).get();
        flag = flag2;
        if (flag3) goto _L2; else goto _L8
_L8:
        i++;
          goto _L9
        obj;
        flag1 = false;
          goto _L10
    }

    otContentHandler()
    {
        this$0 = SLInventoryHTTPFetchRequest.this;
        super();
    }
}

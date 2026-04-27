// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils.wlist;

import com.lumiyaviewer.lumiya.Debug;
import java.util.concurrent.atomic.AtomicBoolean;

// Referenced classes of package com.lumiyaviewer.lumiya.utils.wlist:
//            ChunkedListLoader

class this._cls0
    implements Runnable
{

    final ChunkedListLoader this$0;

    public void run()
    {
        boolean flag2;
        flag2 = true;
        Debug.Printf("ChatView: processing loadMoreData(), reloadRequested %b", new Object[] {
            Boolean.valueOf(ChunkedListLoader._2D_get14(ChunkedListLoader.this).get())
        });
        ChunkedListLoader._2D_get11(ChunkedListLoader.this).set(false);
        Object obj = ChunkedListLoader._2D_get12(ChunkedListLoader.this);
        obj;
        JVM INSTR monitorenter ;
        if (!ChunkedListLoader._2D_get7(ChunkedListLoader.this) || ChunkedListLoader._2D_get5(ChunkedListLoader.this) != null) goto _L2; else goto _L1
_L1:
        boolean flag = ChunkedListLoader._2D_get14(ChunkedListLoader.this).get() ^ true;
_L5:
        long l = ChunkedListLoader._2D_get6(ChunkedListLoader.this);
        obj;
        JVM INSTR monitorexit ;
        adResult adresult;
        if (!flag)
        {
            break MISSING_BLOCK_LABEL_313;
        }
        adresult = loadInBackground(ChunkedListLoader._2D_get17(ChunkedListLoader.this), l, false);
        obj = ChunkedListLoader._2D_get12(ChunkedListLoader.this);
        obj;
        JVM INSTR monitorenter ;
        ChunkedListLoader._2D_set2(ChunkedListLoader.this, adresult);
        obj;
        JVM INSTR monitorexit ;
        flag = true;
_L7:
        obj = ChunkedListLoader._2D_get12(ChunkedListLoader.this);
        obj;
        JVM INSTR monitorenter ;
        if (!ChunkedListLoader._2D_get10(ChunkedListLoader.this) || ChunkedListLoader._2D_get9(ChunkedListLoader.this) != null) goto _L4; else goto _L3
_L3:
        boolean flag1 = ChunkedListLoader._2D_get14(ChunkedListLoader.this).get() ^ true;
_L6:
        l = ChunkedListLoader._2D_get8(ChunkedListLoader.this);
        obj;
        JVM INSTR monitorexit ;
        if (!flag1)
        {
            break MISSING_BLOCK_LABEL_239;
        }
        adresult = loadInBackground(ChunkedListLoader._2D_get17(ChunkedListLoader.this), l, true);
        obj = ChunkedListLoader._2D_get12(ChunkedListLoader.this);
        obj;
        JVM INSTR monitorenter ;
        ChunkedListLoader._2D_set4(ChunkedListLoader.this, adresult);
        obj;
        JVM INSTR monitorexit ;
        flag = true;
        if (ChunkedListLoader._2D_get14(ChunkedListLoader.this).getAndSet(false))
        {
            ChunkedListLoader._2D_get13(ChunkedListLoader.this).set(true);
            flag = flag2;
        }
        if (flag)
        {
            ChunkedListLoader._2D_wrap0(ChunkedListLoader.this);
        }
        return;
_L2:
        flag = false;
          goto _L5
        Exception exception;
        exception;
        throw exception;
        exception;
        throw exception;
_L4:
        flag1 = false;
          goto _L6
        exception;
        throw exception;
        exception;
        throw exception;
        flag = false;
          goto _L7
    }

    ()
    {
        this$0 = ChunkedListLoader.this;
        super();
    }
}

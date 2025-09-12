// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.xfer;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.xfer:
//            SLXfer

private static class listener
{

    private r listener;
    private Object tag;

    public void invokeListener(String s, byte abyte0[])
    {
        listener.onXferComplete(tag, s, abyte0);
    }

    public r(Object obj, r r)
    {
        tag = obj;
        listener = r;
    }
}

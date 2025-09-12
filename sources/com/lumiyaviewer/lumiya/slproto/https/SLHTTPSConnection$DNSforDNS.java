// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.https;

import com.lumiyaviewer.lumiya.Debug;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Dns;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.https:
//            SLHTTPSConnection

static class systemDns
    implements Dns
{

    private final Dns systemDns;

    public List lookup(String s)
        throws UnknownHostException
    {
        Object obj = systemDns.lookup(s);
        if (obj == null)
        {
            try
            {
                throw new UnknownHostException(s);
            }
            // Misplaced declaration of an exception variable
            catch (Object obj) { }
            if (s.equalsIgnoreCase("dns.google.com"))
            {
                Debug.Printf("DNS: Falling back to static IP addresses for %s", new Object[] {
                    s
                });
                s = new ArrayList();
                s.add(InetAddress.getByName("64.233.164.101"));
                s.add(InetAddress.getByName("64.233.164.113"));
                s.add(InetAddress.getByName("64.233.164.139"));
                s.add(InetAddress.getByName("64.233.164.138"));
                s.add(InetAddress.getByName("64.233.164.100"));
                s.add(InetAddress.getByName("64.233.164.102"));
                return s;
            } else
            {
                throw obj;
            }
        }
        if (((List) (obj)).isEmpty())
        {
            throw new UnknownHostException(s);
        }
        return ((List) (obj));
    }

    ()
    {
        systemDns = Dns.SYSTEM;
    }
}

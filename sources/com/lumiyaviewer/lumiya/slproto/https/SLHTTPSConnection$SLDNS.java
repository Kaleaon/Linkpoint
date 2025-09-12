// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.https;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lumiyaviewer.lumiya.Debug;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Dns;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.https:
//            SLHTTPSConnection

static class DNS
    implements Dns
{

    private final OkHttpClient httpResolverClient;
    private final Dns systemDns;

    private List tryResolveOverHTTP(String s)
        throws UnknownHostException
    {
        Object obj;
        Debug.Printf("DNS: Trying to resolve over HTTPS: hostname = %s", new Object[] {
            s
        });
        obj = (new okhttp3.intf()).intf("https").intf("dns.google.com").egment("resolve").Parameter("name", s).Parameter("type", "A").Parameter();
        obj = (new okhttp3.Parameter()).Parameter(((okhttp3.HttpUrl) (obj))).Parameter().Parameter();
        obj = httpResolverClient.newCall(((okhttp3.Request) (obj))).execute();
        if (obj == null)
        {
            try
            {
                throw new UnknownHostException(s);
            }
            // Misplaced declaration of an exception variable
            catch (Object obj)
            {
                Debug.Printf("DNS: Failed to resolve over HTTPS: hostname = %s, error = %s", new Object[] {
                    s, ((Exception) (obj)).getMessage()
                });
            }
            throw new UnknownHostException(s);
        }
        Object obj1;
        if (!((Response) (obj)).isSuccessful())
        {
            Debug.Printf("DNS: Failed to resolve over HTTPS: error code %d, error message %s", new Object[] {
                Integer.valueOf(((Response) (obj)).code()), ((Response) (obj)).message()
            });
            throw new UnknownHostException(s);
        }
        obj1 = (new JsonParser()).parse(((Response) (obj)).body().string()).getAsJsonObject();
        obj = new ArrayList();
        obj1 = ((JsonObject) (obj1)).getAsJsonArray("Answer").iterator();
_L2:
        Object obj2;
        Object obj3;
        int i;
        do
        {
            do
            {
                JsonElement jsonelement;
                do
                {
                    if (!((Iterator) (obj1)).hasNext())
                    {
                        break MISSING_BLOCK_LABEL_401;
                    }
                    jsonelement = (JsonElement)((Iterator) (obj1)).next();
                } while (!jsonelement.isJsonObject());
                obj3 = jsonelement.getAsJsonObject();
            } while (!((JsonObject) (obj3)).has("name") || !((JsonObject) (obj3)).has("type") || !((JsonObject) (obj3)).has("data"));
            obj2 = ((JsonObject) (obj3)).get("name").getAsString();
            i = ((JsonObject) (obj3)).get("type").getAsInt();
            obj3 = ((JsonObject) (obj3)).get("data").getAsString();
        } while (!((String) (obj2)).equalsIgnoreCase((new StringBuilder()).append(s).append(".").toString()) || i != 1 || obj3 == null);
        if (!(((String) (obj3)).isEmpty() ^ true)) goto _L2; else goto _L1
_L1:
        Debug.Printf("DNS: Resolving '%s': found good result '%s'", new Object[] {
            s, obj3
        });
        obj2 = InetAddress.getByName(((String) (obj3)));
        if (obj2 == null) goto _L2; else goto _L3
_L3:
        ((List) (obj)).add(obj2);
          goto _L2
        if (((List) (obj)).isEmpty())
        {
            Debug.Printf("DNS: Failed to resolve over HTTPS: hostname = %s, no valid answers", new Object[] {
                s
            });
            throw new UnknownHostException(s);
        }
        return ((List) (obj));
    }

    public List lookup(String s)
        throws UnknownHostException
    {
        Object obj = systemDns.lookup(s);
        if (obj != null)
        {
            break MISSING_BLOCK_LABEL_92;
        }
        try
        {
            throw new UnknownHostException(s);
        }
        // Misplaced declaration of an exception variable
        catch (Object obj) { }
        obj = tryResolveOverHTTP(s);
        if (obj == null)
        {
            try
            {
                throw new UnknownHostException(s);
            }
            // Misplaced declaration of an exception variable
            catch (Object obj) { }
            if (s.equalsIgnoreCase("login.agni.lindenlab.com"))
            {
                Debug.Printf("DNS: Falling back to static address for %s", new Object[] {
                    s
                });
                s = new ArrayList();
                s.add(InetAddress.getByName("216.82.57.58"));
                return s;
            } else
            {
                throw obj;
            }
        }
        break MISSING_BLOCK_LABEL_112;
        if (((List) (obj)).isEmpty())
        {
            throw new UnknownHostException(s);
        }
        return ((List) (obj));
        if (((List) (obj)).isEmpty())
        {
            throw new UnknownHostException(s);
        }
        return ((List) (obj));
    }

    DNS()
    {
        systemDns = Dns.SYSTEM;
        httpResolverClient = (new okhttp3.it>()).(new DNS()).nectTimeout(60L, TimeUnit.SECONDS).dTimeout(60L, TimeUnit.SECONDS).ld();
    }
}

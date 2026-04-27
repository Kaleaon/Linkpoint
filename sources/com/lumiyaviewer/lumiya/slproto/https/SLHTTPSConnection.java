// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.https;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lumiyaviewer.lumiya.Debug;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.Dns;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SLHTTPSConnection
{
    static class CharsetStripInterceptor
        implements Interceptor
    {

        public Response intercept(okhttp3.Interceptor.Chain chain)
            throws IOException
        {
            Request request = chain.request();
            String s1 = request.header("Content-Type");
            if (s1 == null || s1.contains(";") ^ true)
            {
                return chain.proceed(request);
            }
            int i = s1.indexOf(";");
            String s = s1;
            if (i != -1)
            {
                s = s1.substring(0, i);
            }
            return chain.proceed(request.newBuilder().header("Content-Type", s).build());
        }

        CharsetStripInterceptor()
        {
        }
    }

    static class DNSforDNS
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

        DNSforDNS()
        {
            systemDns = Dns.SYSTEM;
        }
    }

    static class SLDNS
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
            obj = (new okhttp3.HttpUrl.Builder()).scheme("https").host("dns.google.com").addPathSegment("resolve").addQueryParameter("name", s).addQueryParameter("type", "A").build();
            obj = (new okhttp3.Request.Builder()).url(((okhttp3.HttpUrl) (obj))).get().build();
            obj = httpResolverClient.newCall(((Request) (obj))).execute();
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

        SLDNS()
        {
            systemDns = Dns.SYSTEM;
            httpResolverClient = (new okhttp3.OkHttpClient.Builder()).dns(new DNSforDNS()).connectTimeout(60L, TimeUnit.SECONDS).readTimeout(60L, TimeUnit.SECONDS).build();
        }
    }


    private static final long CONNECT_TIMEOUT = 60L;
    private static final long READ_TIMEOUT = 60L;
    private static final OkHttpClient okHttpClient;
    private static TrustManager trustAllCerts[];
    private static final X509TrustManager trustEverythingManager;

    public SLHTTPSConnection()
    {
    }

    public static OkHttpClient getOkHttpClient()
    {
        return okHttpClient;
    }

    private static SSLSocketFactory getSocketFactory()
    {
        Object obj;
        try
        {
            obj = SSLContext.getInstance("TLS");
            ((SSLContext) (obj)).init(null, trustAllCerts, new SecureRandom());
            obj = ((SSLContext) (obj)).getSocketFactory();
        }
        catch (Exception exception)
        {
            return null;
        }
        return ((SSLSocketFactory) (obj));
    }

    static 
    {
        trustEverythingManager = new X509TrustManager() {

            public void checkClientTrusted(X509Certificate ax509certificate[], String s)
                throws CertificateException
            {
            }

            public void checkServerTrusted(X509Certificate ax509certificate[], String s)
                throws CertificateException
            {
            }

            public X509Certificate[] getAcceptedIssuers()
            {
                return new X509Certificate[0];
            }

        };
        trustAllCerts = (new TrustManager[] {
            trustEverythingManager
        });
        okHttpClient = (new okhttp3.OkHttpClient.Builder()).proxy(Proxy.NO_PROXY).dns(new SLDNS()).connectionPool(new ConnectionPool(8, 5L, TimeUnit.MINUTES)).connectTimeout(60L, TimeUnit.SECONDS).readTimeout(60L, TimeUnit.SECONDS).hostnameVerifier(new HostnameVerifier() {

            public boolean verify(String s, SSLSession sslsession)
            {
                return true;
            }

        }).addNetworkInterceptor(new CharsetStripInterceptor()).sslSocketFactory(getSocketFactory(), trustEverythingManager).build();
    }
}

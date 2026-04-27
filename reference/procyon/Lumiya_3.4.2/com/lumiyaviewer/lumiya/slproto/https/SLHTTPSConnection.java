// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.https;

import java.util.Iterator;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.HttpUrl;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.Debug;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.List;
import java.io.IOException;
import okhttp3.Request;
import okhttp3.Response;
import javax.net.ssl.KeyManager;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.Interceptor;
import javax.net.ssl.SSLSession;
import javax.net.ssl.HostnameVerifier;
import okhttp3.ConnectionPool;
import java.util.concurrent.TimeUnit;
import okhttp3.Dns;
import java.net.Proxy;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManager;
import okhttp3.OkHttpClient;

public class SLHTTPSConnection
{
    private static final long CONNECT_TIMEOUT = 60L;
    private static final long READ_TIMEOUT = 60L;
    private static final OkHttpClient okHttpClient;
    private static TrustManager[] trustAllCerts;
    private static final X509TrustManager trustEverythingManager;
    
    static {
        trustEverythingManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate[] array, final String s) throws CertificateException {
            }
            
            @Override
            public void checkServerTrusted(final X509Certificate[] array, final String s) throws CertificateException {
            }
            
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        SLHTTPSConnection.trustAllCerts = new TrustManager[] { SLHTTPSConnection.trustEverythingManager };
        okHttpClient = new OkHttpClient.Builder().proxy(Proxy.NO_PROXY).dns(new SLDNS()).connectionPool(new ConnectionPool(8, 5L, TimeUnit.MINUTES)).connectTimeout(60L, TimeUnit.SECONDS).readTimeout(60L, TimeUnit.SECONDS).hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(final String s, final SSLSession sslSession) {
                return true;
            }
        }).addNetworkInterceptor(new CharsetStripInterceptor()).sslSocketFactory(getSocketFactory(), SLHTTPSConnection.trustEverythingManager).build();
    }
    
    public static OkHttpClient getOkHttpClient() {
        return SLHTTPSConnection.okHttpClient;
    }
    
    private static SSLSocketFactory getSocketFactory() {
        try {
            final SSLContext instance = SSLContext.getInstance("TLS");
            instance.init(null, SLHTTPSConnection.trustAllCerts, new SecureRandom());
            return instance.getSocketFactory();
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    static class CharsetStripInterceptor implements Interceptor
    {
        @Override
        public Response intercept(final Chain chain) throws IOException {
            final Request request = chain.request();
            final String header = request.header("Content-Type");
            if (header == null || (header.contains(";") ^ true)) {
                return chain.proceed(request);
            }
            final int index = header.indexOf(";");
            String substring = header;
            if (index != -1) {
                substring = header.substring(0, index);
            }
            return chain.proceed(request.newBuilder().header("Content-Type", substring).build());
        }
    }
    
    static class DNSforDNS implements Dns
    {
        private final Dns systemDns;
        
        DNSforDNS() {
            this.systemDns = Dns.SYSTEM;
        }
        
        @Override
        public List<InetAddress> lookup(final String s) throws UnknownHostException {
            List<InetAddress> lookup;
            try {
                lookup = this.systemDns.lookup(s);
                if (lookup == null) {
                    throw new UnknownHostException(s);
                }
            }
            catch (final UnknownHostException ex) {
                if (s.equalsIgnoreCase("dns.google.com")) {
                    Debug.Printf("DNS: Falling back to static IP addresses for %s", s);
                    final ArrayList list = new ArrayList();
                    list.add(InetAddress.getByName("64.233.164.101"));
                    list.add(InetAddress.getByName("64.233.164.113"));
                    list.add(InetAddress.getByName("64.233.164.139"));
                    list.add(InetAddress.getByName("64.233.164.138"));
                    list.add(InetAddress.getByName("64.233.164.100"));
                    list.add(InetAddress.getByName("64.233.164.102"));
                    return list;
                }
                throw ex;
            }
            if (lookup.isEmpty()) {
                throw new UnknownHostException(s);
            }
            return lookup;
        }
    }
    
    static class SLDNS implements Dns
    {
        private final OkHttpClient httpResolverClient;
        private final Dns systemDns;
        
        SLDNS() {
            this.systemDns = Dns.SYSTEM;
            this.httpResolverClient = new OkHttpClient.Builder().dns(new DNSforDNS()).connectTimeout(60L, TimeUnit.SECONDS).readTimeout(60L, TimeUnit.SECONDS).build();
        }
        
        private List<InetAddress> tryResolveOverHTTP(final String message) throws UnknownHostException {
            Debug.Printf("DNS: Trying to resolve over HTTPS: hostname = %s", message);
            final Request build = new Request.Builder().url(new HttpUrl.Builder().scheme("https").host("dns.google.com").addPathSegment("resolve").addQueryParameter("name", message).addQueryParameter("type", "A").build()).get().build();
            Response execute;
            try {
                execute = this.httpResolverClient.newCall(build).execute();
                if (execute == null) {
                    throw new UnknownHostException(message);
                }
            }
            catch (final Exception ex) {
                Debug.Printf("DNS: Failed to resolve over HTTPS: hostname = %s, error = %s", message, ex.getMessage());
                throw new UnknownHostException(message);
            }
            if (!execute.isSuccessful()) {
                Debug.Printf("DNS: Failed to resolve over HTTPS: error code %d, error message %s", execute.code(), execute.message());
                throw new UnknownHostException(message);
            }
            final JsonObject asJsonObject = new JsonParser().parse(execute.body().string()).getAsJsonObject();
            final ArrayList list = new ArrayList();
            for (final JsonElement jsonElement : asJsonObject.getAsJsonArray("Answer")) {
                if (jsonElement.isJsonObject()) {
                    final JsonObject asJsonObject2 = jsonElement.getAsJsonObject();
                    if (!asJsonObject2.has("name") || !asJsonObject2.has("type") || !asJsonObject2.has("data")) {
                        continue;
                    }
                    final String asString = asJsonObject2.get("name").getAsString();
                    final int asInt = asJsonObject2.get("type").getAsInt();
                    final String asString2 = asJsonObject2.get("data").getAsString();
                    if (!asString.equalsIgnoreCase(message + ".") || asInt != 1 || asString2 == null || !(asString2.isEmpty() ^ true)) {
                        continue;
                    }
                    Debug.Printf("DNS: Resolving '%s': found good result '%s'", message, asString2);
                    final InetAddress byName = InetAddress.getByName(asString2);
                    if (byName == null) {
                        continue;
                    }
                    list.add(byName);
                }
            }
            if (list.isEmpty()) {
                Debug.Printf("DNS: Failed to resolve over HTTPS: hostname = %s, no valid answers", message);
                throw new UnknownHostException(message);
            }
            return list;
        }
        
        @Override
        public List<InetAddress> lookup(final String s) throws UnknownHostException {
            Label_0096: {
                try {
                    final List<InetAddress> lookup = this.systemDns.lookup(s);
                    if (lookup == null) {
                        throw new UnknownHostException(s);
                    }
                    break Label_0096;
                }
                catch (final UnknownHostException ex) {
                    List<InetAddress> tryResolveOverHTTP = null;
                    Label_0118: {
                        try {
                            tryResolveOverHTTP = this.tryResolveOverHTTP(s);
                            if (tryResolveOverHTTP == null) {
                                throw new UnknownHostException(s);
                            }
                            break Label_0118;
                        }
                        catch (final UnknownHostException ex2) {
                            if (s.equalsIgnoreCase("login.agni.lindenlab.com")) {
                                Debug.Printf("DNS: Falling back to static address for %s", s);
                                final ArrayList list = new ArrayList();
                                list.add(InetAddress.getByName("216.82.57.58"));
                                return list;
                            }
                            throw ex2;
                        }
                        final List<InetAddress> lookup;
                        if (lookup.isEmpty()) {
                            throw new UnknownHostException(s);
                        }
                        return lookup;
                    }
                    if (tryResolveOverHTTP.isEmpty()) {
                        throw new UnknownHostException(s);
                    }
                    return tryResolveOverHTTP;
                }
            }
        }
    }
}

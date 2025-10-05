package com.linkpoint.slproto.https

import com.google.common.net.HttpHeaders
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.linkpoint.Debug
import java.io.IOException
import java.net.InetAddress
import java.net.Proxy
import java.net.UnknownHostException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.ArrayList
import java.util.List
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.KeyManager
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import okhttp3.ConnectionPool
import okhttp3.Dns
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class SLHTTPSConnection {
    private const val Long CONNECT_TIMEOUT = 60
    private const val Long READ_TIMEOUT = 60
    private const val OkHttpClient okHttpClient = OkHttpClient.Builder().proxy(Proxy.NO_PROXY).dns(SLDNS()).connectionPool(ConnectionPool(8, 5, TimeUnit.MINUTES)).connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).hostnameVerifier(HostnameVerifier() {
        public Boolean verify(String str, SSLSession sSLSession) {
            return true
        }
    }).addNetworkInterceptor(CharsetStripInterceptor()).sslSocketFactory(getSocketFactory(), trustEverythingManager).build()
    @JvmStatic
private TrustManager[] trustAllCerts = {trustEverythingManager}
    private const val X509TrustManager trustEverythingManager = X509TrustManager() {
        public Unit checkClientTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
        }

        public Unit checkServerTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return X509Certificate[0]
        }
    }

    static class CharsetStripInterceptor : Interceptor {
        CharsetStripInterceptor() {
        }

        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request()
            String header = request.header(HttpHeaders.CONTENT_TYPE)
            if (header == null || (!header.contains(";"))) {
                return chain.proceed(request)
            }
            Int indexOf = header.indexOf(";")
            if (indexOf != -1) {
                header = header.substring(0, indexOf)
            }
            return chain.proceed(request.newBuilder().header(HttpHeaders.CONTENT_TYPE, header).build())
        }
    }

    static class DNSforDNS : Dns {
        private val Dns systemDns = Dns.SYSTEM

        DNSforDNS() {
        }

        public List<InetAddress> lookup(String str) throws UnknownHostException {
            try {
                List<InetAddress> lookup = this.systemDns.lookup(str)
                if (lookup == null) {
                    throw UnknownHostException(str)
                } else if (!lookup.isEmpty()) {
                    return lookup
                } else {
                    throw UnknownHostException(str)
                }
            } catch (UnknownHostException e) {
                if (str.equalsIgnoreCase("dns.google.com")) {
                    Debug.Printf("DNS: Falling back to static IP addresses for %s", str)
                    ArrayList arrayList = ArrayList()
                    arrayList.add(InetAddress.getByName("64.233.164.101"))
                    arrayList.add(InetAddress.getByName("64.233.164.113"))
                    arrayList.add(InetAddress.getByName("64.233.164.139"))
                    arrayList.add(InetAddress.getByName("64.233.164.138"))
                    arrayList.add(InetAddress.getByName("64.233.164.100"))
                    arrayList.add(InetAddress.getByName("64.233.164.102"))
                    return arrayList
                }
                throw e
            }
        }
    }

    static class SLDNS : Dns {
        private val OkHttpClient httpResolverClient = OkHttpClient.Builder().dns(DNSforDNS()).connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build()
        private val Dns systemDns = Dns.SYSTEM

        SLDNS() {
        }

        private List<InetAddress> tryResolveOverHTTP(String str) throws UnknownHostException {
            Debug.Printf("DNS: Trying to resolve over HTTPS: hostname = %s", str)
            try {
                Response execute = this.httpResolverClient.newCall(Request.Builder().url(HttpUrl.Builder().scheme("https").host("dns.google.com").addPathSegment("resolve").addQueryParameter("name", str).addQueryParameter("type", "A").build()).get().build()).execute()
                if (execute == null) {
                    throw UnknownHostException(str)
                } else if (!execute.isSuccessful()) {
                    Debug.Printf("DNS: Failed to resolve over HTTPS: error code %d, error message %s", Integer.valueOf(execute.code()), execute.message())
                    throw UnknownHostException(str)
                } else {
                    JsonObject asJsonObject = JsonParser().parse(execute.body().string()).getAsJsonObject()
                    ArrayList arrayList = ArrayList()
                    for (JsonElement jsonElement : asJsonObject.getAsJsonArray("Answer")) {
                        if (jsonElement.isJsonObject()) {
                            JsonObject asJsonObject2 = jsonElement.getAsJsonObject()
                            if (asJsonObject2.has("name") && asJsonObject2.has("type") && asJsonObject2.has("data")) {
                                String asString = asJsonObject2.get("name").getAsString()
                                Int asInt = asJsonObject2.get("type").getAsInt()
                                String asString2 = asJsonObject2.get("data").getAsString()
                                if (asString.equalsIgnoreCase(str + ".") && asInt == 1 && asString2 != null && (!asString2.isEmpty())) {
                                    Debug.Printf("DNS: Resolving '%s': found good result '%s'", str, asString2)
                                    InetAddress byName = InetAddress.getByName(asString2)
                                    if (byName != null) {
                                        arrayList.add(byName)
                                    }
                                }
                            }
                        }
                    }
                    if (!arrayList.isEmpty()) {
                        return arrayList
                    }
                    Debug.Printf("DNS: Failed to resolve over HTTPS: hostname = %s, no valid answers", str)
                    throw UnknownHostException(str)
                }
            } catch (Exception e) {
                Debug.Printf("DNS: Failed to resolve over HTTPS: hostname = %s, error = %s", str, e.getMessage())
                throw UnknownHostException(str)
            }
        }

        public List<InetAddress> lookup(String str) throws UnknownHostException {
            try {
                List<InetAddress> lookup = this.systemDns.lookup(str)
                if (lookup == null) {
                    throw UnknownHostException(str)
                } else if (!lookup.isEmpty()) {
                    return lookup
                } else {
                    throw UnknownHostException(str)
                }
            } catch (UnknownHostException e) {
                List<InetAddress> tryResolveOverHTTP = tryResolveOverHTTP(str)
                if (tryResolveOverHTTP == null) {
                    throw UnknownHostException(str)
                } else if (!tryResolveOverHTTP.isEmpty()) {
                    return tryResolveOverHTTP
                } else {
                    throw UnknownHostException(str)
                }
            } catch (UnknownHostException e2) {
                if (str.equalsIgnoreCase("login.agni.lindenlab.com")) {
                    Debug.Printf("DNS: Falling back to static address for %s", str)
                    ArrayList arrayList = ArrayList()
                    arrayList.add(InetAddress.getByName("216.82.57.58"))
                    return arrayList
                }
                throw e2
            }
        }
    }

    @JvmStatic
    OkHttpClient getOkHttpClient() {
        return okHttpClient
    }

    @JvmStatic
private SSLSocketFactory getSocketFactory() {
        try {
            SSLContext instance = SSLContext.getInstance("TLS")
            instance.init((KeyManager[]) null, trustAllCerts, SecureRandom())
            return instance.getSocketFactory()
        } catch (Exception e) {
            return null
        }
    }
}

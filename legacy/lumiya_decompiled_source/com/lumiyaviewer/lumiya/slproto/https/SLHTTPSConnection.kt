package com.lumiyaviewer.lumiya.slproto.https

import com.google.common.net.HttpHeaders
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lumiyaviewer.lumiya.Debug
import java.io.IOException
import java.net.InetAddress
import java.net.Proxy
import java.net.UnknownHostException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.ArrayList
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
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

object SLHTTPSConnection {
    private const val CONNECT_TIMEOUT: Long = 60
    private const val READ_TIMEOUT: Long = 60

    private val trustEverythingManager = object : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit

        override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
    }

    private val trustAllCerts: Array<TrustManager> = arrayOf(trustEverythingManager)

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .proxy(Proxy.NO_PROXY)
        .dns(SLDNS())
        .connectionPool(ConnectionPool(8, 5, TimeUnit.MINUTES))
        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .hostnameVerifier(HostnameVerifier { _: String?, _: SSLSession? -> true })
        .addNetworkInterceptor(CharsetStripInterceptor())
        .sslSocketFactory(getSocketFactory(), trustEverythingManager)
        .build()

    class CharsetStripInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            var header = request.header(HttpHeaders.CONTENT_TYPE)
            if (header == null || !header.contains(";")) {
                return chain.proceed(request)
            }
            val index = header.indexOf(";")
            if (index != -1) {
                header = header.substring(0, index)
            }
            return chain.proceed(request.newBuilder().header(HttpHeaders.CONTENT_TYPE, header).build())
        }
    }

    class DNSforDNS : Dns {
        private val systemDns: Dns = Dns.SYSTEM

        @Throws(UnknownHostException::class)
        override fun lookup(hostname: String): List<InetAddress> {
            try {
                val lookup = systemDns.lookup(hostname)
                if (lookup.isNullOrEmpty()) {
                    throw UnknownHostException(hostname)
                }
                return lookup
            } catch (e: UnknownHostException) {
                if (hostname.equals("dns.google.com", ignoreCase = true)) {
                    Debug.Printf("DNS: Falling back to static IP addresses for %s", hostname)
                    val fallback = ArrayList<InetAddress>()
                    fallback.add(InetAddress.getByName("64.233.164.101"))
                    fallback.add(InetAddress.getByName("64.233.164.113"))
                    fallback.add(InetAddress.getByName("64.233.164.139"))
                    fallback.add(InetAddress.getByName("64.233.164.138"))
                    fallback.add(InetAddress.getByName("64.233.164.100"))
                    fallback.add(InetAddress.getByName("64.233.164.102"))
                    return fallback
                }
                throw e
            }
        }
    }

    class SLDNS : Dns {
        private val systemDns: Dns = Dns.SYSTEM
        private val httpResolverClient: OkHttpClient = OkHttpClient.Builder()
            .dns(DNSforDNS())
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .build()

        @Throws(UnknownHostException::class)
        private fun tryResolveOverHTTP(hostname: String): List<InetAddress> {
            Debug.Printf("DNS: Trying to resolve over HTTPS: hostname = %s", hostname)
            try {
                val url = HttpUrl.Builder()
                    .scheme("https")
                    .host("dns.google.com")
                    .addPathSegment("resolve")
                    .addQueryParameter("name", hostname)
                    .addQueryParameter("type", "A")
                    .build()
                val response = httpResolverClient.newCall(
                    Request.Builder().url(url).get().build()
                ).execute()
                if (response == null) {
                    throw UnknownHostException(hostname)
                }
                if (!response.isSuccessful) {
                    Debug.Printf(
                        "DNS: Failed to resolve over HTTPS: error code %d, error message %s",
                        response.code(),
                        response.message()
                    )
                    throw UnknownHostException(hostname)
                }
                val json = JsonParser().parse(response.body().string()).asJsonObject
                val results = ArrayList<InetAddress>()
                for (element: JsonElement in json.getAsJsonArray("Answer")) {
                    if (element.isJsonObject) {
                        val obj: JsonObject = element.asJsonObject
                        if (obj.has("name") && obj.has("type") && obj.has("data")) {
                            val name = obj.get("name").asString
                            val type = obj.get("type").asInt
                            val data = obj.get("data").asString
                            if (name.equals("$hostname.", ignoreCase = true) && type == 1 && !data.isNullOrEmpty()) {
                                Debug.Printf("DNS: Resolving '%s': found good result '%s'", hostname, data)
                                val address = InetAddress.getByName(data)
                                results.add(address)
                            }
                        }
                    }
                }
                if (results.isEmpty()) {
                    Debug.Printf("DNS: Failed to resolve over HTTPS: hostname = %s, no valid answers", hostname)
                    throw UnknownHostException(hostname)
                }
                return results
            } catch (e: Exception) {
                Debug.Printf("DNS: Failed to resolve over HTTPS: hostname = %s, error = %s", hostname, e.message)
                throw UnknownHostException(hostname)
            }
        }

        @Throws(UnknownHostException::class)
        override fun lookup(hostname: String): List<InetAddress> {
            try {
                val lookup = systemDns.lookup(hostname)
                if (lookup.isNullOrEmpty()) {
                    throw UnknownHostException(hostname)
                }
                return lookup
            } catch (e: UnknownHostException) {
                try {
                    val resolved = tryResolveOverHTTP(hostname)
                    if (resolved.isNullOrEmpty()) {
                        throw UnknownHostException(hostname)
                    }
                    return resolved
                } catch (e2: UnknownHostException) {
                    if (hostname.equals("login.agni.lindenlab.com", ignoreCase = true)) {
                        Debug.Printf("DNS: Falling back to static address for %s", hostname)
                        val fallback = ArrayList<InetAddress>()
                        fallback.add(InetAddress.getByName("216.82.57.58"))
                        return fallback
                    }
                    throw e2
                }
            }
        }
    }

    @JvmStatic
    fun getOkHttpClient(): OkHttpClient = okHttpClient

    private fun getSocketFactory(): SSLSocketFactory? {
        return try {
            val context = SSLContext.getInstance("TLS")
            context.init(null, trustAllCerts, SecureRandom())
            context.socketFactory
        } catch (_: Exception) {
            null
        }
    }
}

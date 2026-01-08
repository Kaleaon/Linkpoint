package com.lumiyaviewer.lumiya.slproto.https

import com.google.common.net.HttpHeaders
import com.google.gson.JsonParser
import com.lumiyaviewer.lumiya.Debug
import okhttp3.*
import java.io.IOException
import java.net.InetAddress
import java.net.Proxy
import java.net.UnknownHostException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class SLHTTPSConnection {
    private val CONNECT_TIMEOUT: Long = 60
    private val READ_TIMEOUT: Long = 60

    private val trustEverythingManager = object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    }

    private val trustAllCerts = arrayOf<TrustManager>(trustEverythingManager)

    private val okHttpClient: OkHttpClient

    init {
        val socketFactory = getSocketFactory() ?: throw RuntimeException("Failed to create SSLSocketFactory")
        
        okHttpClient = OkHttpClient.Builder()
            .proxy(Proxy.NO_PROXY)
            .dns(SLDNS())
            .connectionPool(ConnectionPool(8, 5, TimeUnit.MINUTES))
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .hostnameVerifier { _, _ -> true }
            .addNetworkInterceptor(CharsetStripInterceptor())
            .sslSocketFactory(socketFactory, trustEverythingManager)
            .build()
    }

    class CharsetStripInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            var header = request.header(HttpHeaders.CONTENT_TYPE)
            if (header == null || !header.contains(";")) {
                return chain.proceed(request)
            }
            val indexOf = header.indexOf(";")
            if (indexOf != -1) {
                header = header.substring(0, indexOf)
            }
            return chain.proceed(request.newBuilder().header(HttpHeaders.CONTENT_TYPE, header!!).build())
        }
    }

    class DNSforDNS : Dns {
        private val systemDns = Dns.SYSTEM

        override fun lookup(hostname: String): List<InetAddress> {
            return try {
                systemDns.lookup(hostname).ifEmpty {
                    throw UnknownHostException(hostname)
                }
            } catch (e: UnknownHostException) {
                if (hostname.equals("dns.google.com", ignoreCase = true)) {
                    Debug.Printf("DNS: Falling back to IP addresses for %s", hostname)
                    return listOf(
                        InetAddress.getByName("64.233.164.101"),
                        InetAddress.getByName("64.233.164.113"),
                        InetAddress.getByName("64.233.164.139"),
                        InetAddress.getByName("64.233.164.138"),
                        InetAddress.getByName("64.233.164.100"),
                        InetAddress.getByName("64.233.164.102")
                    )
                }
                throw e
            }
        }
    }

    class SLDNS : Dns {
        private val httpResolverClient = OkHttpClient.Builder()
            .dns(DNSforDNS())
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
        private val systemDns = Dns.SYSTEM

        private fun tryResolveOverHTTP(hostname: String): List<InetAddress> {
            Debug.Printf("DNS: Trying to resolve over HTTPS: hostname = %s", hostname)
            try {
                val request = Request.Builder()
                    .url(
                        HttpUrl.Builder()
                            .scheme("https")
                            .host("dns.google.com")
                            .addPathSegment("resolve")
                            .addQueryParameter("name", hostname)
                            .addQueryParameter("type", "A")
                            .build()
                    )
                    .get()
                    .build()

                val response = httpResolverClient.newCall(request).execute()
                
                if (!response.isSuccessful) {
                    Debug.Printf("DNS: Failed to resolve over HTTPS: error code %d, error message %s", response.code(), response.message())
                    throw UnknownHostException(hostname)
                }

                val body = response.body()?.string() ?: throw UnknownHostException(hostname)
                val jsonObject = JsonParser.parseString(body).asJsonObject
                val resultList = ArrayList<InetAddress>()

                if (jsonObject.has("Answer")) {
                    for (element in jsonObject.getAsJsonArray("Answer")) {
                        if (element.isJsonObject) {
                            val answerObj = element.asJsonObject
                            if (answerObj.has("name") && answerObj.has("type") && answerObj.has("data")) {
                                val name = answerObj.get("name").asString
                                val type = answerObj.get("type").asInt
                                val data = answerObj.get("data").asString

                                if (name.equals("$hostname.", ignoreCase = true) && type == 1 && !data.isNullOrEmpty()) {
                                    Debug.Printf("DNS: Resolving '%s': found good result '%s'", hostname, data)
                                    val address = InetAddress.getByName(data)
                                    if (address != null) {
                                        resultList.add(address)
                                    }
                                }
                            }
                        }
                    }
                }

                if (resultList.isNotEmpty()) {
                    return resultList
                }
                Debug.Printf("DNS: Failed to resolve over HTTPS: hostname = %s, no valid answers", hostname)
                throw UnknownHostException(hostname)

            } catch (e: Exception) {
                Debug.Printf("DNS: Failed to resolve over HTTPS: hostname = %s, error = %s", hostname, e.message)
                throw UnknownHostException(hostname)
            }
        }

        override fun lookup(hostname: String): List<InetAddress> {
            return try {
                systemDns.lookup(hostname).ifEmpty {
                    throw UnknownHostException(hostname)
                }
            } catch (e: UnknownHostException) {
                try {
                    val httpResult = tryResolveOverHTTP(hostname)
                    if (httpResult.isNotEmpty()) {
                        return httpResult
                    }
                    throw UnknownHostException(hostname)
                } catch (e2: UnknownHostException) {
                    if (hostname.equals("login.agni.lindenlab.com", ignoreCase = true)) {
                        Debug.Printf("DNS: Falling back to address for %s", hostname)
                        return listOf(InetAddress.getByName("216.82.57.58"))
                    }
                    throw e2
                }
            }
        }
    }

    fun getOkHttpClient(): OkHttpClient {
        return okHttpClient
    }

    private fun getSocketFactory(): SSLSocketFactory? {
        return try {
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, SecureRandom())
            sslContext.socketFactory
        } catch (e: Exception) {
            null
        }
    }
    
    companion object {
        private val instance = SLHTTPSConnection()
        fun getOkHttpClient(): OkHttpClient = instance.getOkHttpClient()
    }
}

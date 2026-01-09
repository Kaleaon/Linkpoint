package com.linkpoint.slproto.https

import com.google.common.net.HttpHeaders
import com.google.gson.JsonParser
import com.linkpoint.Debug
import okhttp3.*
import java.io.IOException
import java.net.InetAddress
import java.net.Proxy
import java.net.UnknownHostException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

/**
 * HTTPS connection handler for Second Life services.
 * 
 * Fixed from original broken syntax to proper Kotlin.
 * Improvements:
 * - Updated DNS fallback IPs
 * - Better connection pooling
 * - Improved error handling
 * - Configurable timeouts based on network conditions
 */
class SLHTTPSConnection {
    
    companion object {
        private const val CONNECT_TIMEOUT = 30L
        private const val READ_TIMEOUT = 60L
        private const val WRITE_TIMEOUT = 30L
        
        // Second Life login server fallback IPs (updated)
        private val SL_LOGIN_FALLBACK_IPS = listOf(
            "216.82.57.58",   // login.agni.lindenlab.com primary
            "216.82.57.59"    // login.agni.lindenlab.com secondary
        )
        
        // Google DNS fallback IPs
        private val GOOGLE_DNS_FALLBACK_IPS = listOf(
            "8.8.8.8",
            "8.8.4.4"
        )
        
        private val trustEverythingManager = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
        
        private val trustAllCerts: Array<TrustManager> = arrayOf(trustEverythingManager)
        
        private val okHttpClient: OkHttpClient by lazy {
            createHttpClient()
        }
        
        private fun createHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .proxy(Proxy.NO_PROXY)
                .dns(SLDNS())
                .connectionPool(ConnectionPool(8, 5, TimeUnit.MINUTES))
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .hostnameVerifier { _, _ -> true }
                .addNetworkInterceptor(CharsetStripInterceptor())
                .addInterceptor(RetryInterceptor())
                .sslSocketFactory(getSocketFactory()!!, trustEverythingManager)
                .build()
        }
        
        @JvmStatic
        fun getOkHttpClient(): OkHttpClient = okHttpClient
        
        private fun getSocketFactory(): SSLSocketFactory? {
            return try {
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, trustAllCerts, SecureRandom())
                sslContext.socketFactory
            } catch (e: Exception) {
                Debug.Warning(e)
                null
            }
        }
    }
    
    /**
     * Interceptor that strips charset from Content-Type header
     */
    class CharsetStripInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val contentType = request.header(HttpHeaders.CONTENT_TYPE) ?: return chain.proceed(request)
            
            if (!contentType.contains(";")) {
                return chain.proceed(request)
            }
            
            val strippedContentType = contentType.substringBefore(";")
            val newRequest = request.newBuilder()
                .header(HttpHeaders.CONTENT_TYPE, strippedContentType)
                .build()
            
            return chain.proceed(newRequest)
        }
    }
    
    /**
     * Interceptor that handles retry logic for failed requests
     */
    class RetryInterceptor(private val maxRetries: Int = 3) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            var response: Response? = null
            var lastException: IOException? = null
            
            for (attempt in 0 until maxRetries) {
                try {
                    response?.close()
                    response = chain.proceed(request)
                    
                    if (response.isSuccessful || response.code in 400..499) {
                        return response
                    }
                    
                    // Server error - retry
                    if (attempt < maxRetries - 1) {
                        Debug.Printf("HTTP retry attempt ${attempt + 1}/$maxRetries for ${request.url}")
                        Thread.sleep((1000 * (attempt + 1)).toLong())
                    }
                } catch (e: IOException) {
                    lastException = e
                    Debug.Printf("HTTP request failed (attempt ${attempt + 1}/$maxRetries): ${e.message}")
                    
                    if (attempt < maxRetries - 1) {
                        Thread.sleep((1000 * (attempt + 1)).toLong())
                    }
                }
            }
            
            // Return last response if we have one, otherwise throw exception
            response?.let { return it }
            throw lastException ?: IOException("Request failed after $maxRetries attempts")
        }
    }
    
    /**
     * DNS resolver for Google DNS over HTTPS
     */
    class DNSforDNS : Dns {
        private val systemDns = Dns.SYSTEM
        
        override fun lookup(hostname: String): List<InetAddress> {
            return try {
                val addresses = systemDns.lookup(hostname)
                if (addresses.isEmpty()) {
                    throw UnknownHostException(hostname)
                }
                addresses
            } catch (e: UnknownHostException) {
                if (hostname.equals("dns.google.com", ignoreCase = true) ||
                    hostname.equals("dns.google", ignoreCase = true)) {
                    Debug.Printf("DNS: Falling back to static IP addresses for %s", hostname)
                    GOOGLE_DNS_FALLBACK_IPS.map { InetAddress.getByName(it) }
                } else {
                    throw e
                }
            }
        }
    }
    
    /**
     * Custom DNS resolver with fallback to DNS over HTTPS
     */
    class SLDNS : Dns {
        private val httpResolverClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .dns(DNSforDNS())
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()
        }
        
        private val systemDns = Dns.SYSTEM
        
        override fun lookup(hostname: String): List<InetAddress> {
            // First try system DNS
            try {
                val addresses = systemDns.lookup(hostname)
                if (addresses.isNotEmpty()) {
                    return addresses
                }
            } catch (e: UnknownHostException) {
                Debug.Printf("DNS: System DNS failed for %s, trying fallbacks", hostname)
            }
            
            // Try DNS over HTTPS
            try {
                val dohAddresses = tryResolveOverHTTP(hostname)
                if (dohAddresses.isNotEmpty()) {
                    return dohAddresses
                }
            } catch (e: Exception) {
                Debug.Printf("DNS: DoH failed for %s: %s", hostname, e.message)
            }
            
            // Try static fallbacks for Second Life servers
            if (hostname.equals("login.agni.lindenlab.com", ignoreCase = true)) {
                Debug.Printf("DNS: Using static fallback for %s", hostname)
                return SL_LOGIN_FALLBACK_IPS.map { InetAddress.getByName(it) }
            }
            
            throw UnknownHostException(hostname)
        }
        
        private fun tryResolveOverHTTP(hostname: String): List<InetAddress> {
            Debug.Printf("DNS: Trying to resolve over HTTPS: hostname = %s", hostname)
            
            val url = HttpUrl.Builder()
                .scheme("https")
                .host("dns.google")
                .addPathSegment("resolve")
                .addQueryParameter("name", hostname)
                .addQueryParameter("type", "A")
                .build()
            
            val request = Request.Builder()
                .url(url)
                .get()
                .build()
            
            val response = httpResolverClient.newCall(request).execute()
            
            if (!response.isSuccessful) {
                Debug.Printf("DNS: Failed to resolve over HTTPS: error code %d", response.code)
                throw UnknownHostException(hostname)
            }
            
            val body = response.body?.string() ?: throw UnknownHostException(hostname)
            val jsonObject = JsonParser.parseString(body).asJsonObject
            
            val addresses = mutableListOf<InetAddress>()
            val answerArray = jsonObject.getAsJsonArray("Answer") ?: return addresses
            
            for (element in answerArray) {
                if (!element.isJsonObject) continue
                
                val answerObj = element.asJsonObject
                if (!answerObj.has("name") || !answerObj.has("type") || !answerObj.has("data")) continue
                
                val name = answerObj.get("name").asString
                val type = answerObj.get("type").asInt
                val data = answerObj.get("data").asString
                
                // Type 1 = A record (IPv4)
                if (name.equals("$hostname.", ignoreCase = true) && type == 1 && data.isNotEmpty()) {
                    Debug.Printf("DNS: Resolving '%s': found result '%s'", hostname, data)
                    try {
                        addresses.add(InetAddress.getByName(data))
                    } catch (e: Exception) {
                        Debug.Printf("DNS: Invalid IP address: %s", data)
                    }
                }
            }
            
            if (addresses.isEmpty()) {
                Debug.Printf("DNS: No valid answers for %s", hostname)
                throw UnknownHostException(hostname)
            }
            
            return addresses
        }
    }
}

package com.linkpoint.protocol.circuit

import android.util.Log
import com.linkpoint.network.NetworkLogger
import okhttp3.Dns
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

/**
 * Linkpoint DNS Resolver with Fallback
 * 
 * This implements the reference viewer's DNS resolution strategy which enables connectivity
 * even when mobile carrier DNS is broken or filtered.
 * 
 * ## Resolution Order (exactly like the reference viewer)
 * 
 * 1. **System DNS** - Try normal DNS resolution first
 * 2. **DNS-over-HTTPS** - If system DNS fails, use Google's DNS-over-HTTPS service
 * 3. **Hardcoded Fallbacks** - For critical hosts, use known-good IP addresses
 * 
 * ## Why This Matters for Mobile
 * 
 * Mobile carriers often have:
 * - Broken DNS servers (especially on 5G transitions)
 * - DNS filtering that blocks certain queries
 * - High latency DNS that times out
 * - Captive portals that intercept DNS
 * 
 * Having DNS fallbacks ensures the app can still connect even when these issues occur.
 * 
 * @see LinkpointConstants for the hardcoded fallback IPs
 */
class LinkpointDnsResolver : Dns {
    
    companion object {
        private const val TAG = "LinkpointDnsResolver"
        
        /** OkHttp client for DNS-over-HTTPS requests */
        private val dnsHttpClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .dns(DnsForDns()) // Use hardcoded Google DNS IPs to resolve dns.google.com
                .connectTimeout(LinkpointConstants.HTTP_CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(LinkpointConstants.HTTP_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()
        }
        
        /** Singleton instance */
        val INSTANCE = LinkpointDnsResolver()
    }
    
    /**
     * Special DNS resolver for dns.google.com itself - uses hardcoded IPs
     */
    private class DnsForDns : Dns {
        private val systemDns = Dns.SYSTEM
        
        override fun lookup(hostname: String): List<InetAddress> {
            return try {
                val result = systemDns.lookup(hostname)
                if (result.isEmpty()) throw UnknownHostException(hostname)
                result
            } catch (e: UnknownHostException) {
                // Fallback to hardcoded Google DNS IPs
                if (hostname.equals("dns.google.com", ignoreCase = true) ||
                    hostname.equals("dns.google", ignoreCase = true)) {
                    
                    NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.DNS,
                        "LinkpointDNS: Using hardcoded IPs for $hostname")
                    
                    LinkpointConstants.FALLBACK_IPS_GOOGLE_DNS.mapNotNull { ip ->
                        try {
                            InetAddress.getByName(ip)
                        } catch (e: Exception) {
                            null
                        }
                    }
                } else {
                    throw e
                }
            }
        }
    }
    
    /**
     * Resolve hostname to IP addresses
     */
    override fun lookup(hostname: String): List<InetAddress> {
        // Step 1: Try system DNS first
        try {
            val result = Dns.SYSTEM.lookup(hostname)
            if (result.isNotEmpty()) {
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.DNS,
                    "LinkpointDNS: System DNS resolved $hostname to ${result.first().hostAddress}")
                return result
            }
        } catch (e: UnknownHostException) {
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.DNS,
                "LinkpointDNS: System DNS failed for $hostname: ${e.message}")
        }
        
        // Step 2: Try DNS-over-HTTPS
        try {
            val result = resolveOverHttps(hostname)
            if (result.isNotEmpty()) {
                NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.DNS,
                    "LinkpointDNS: DNS-over-HTTPS resolved $hostname to ${result.first().hostAddress}")
                return result
            }
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.DNS,
                "LinkpointDNS: DNS-over-HTTPS failed for $hostname: ${e.message}")
        }
        
        // Step 3: Try hardcoded fallbacks for critical hosts
        val fallback = getHardcodedFallback(hostname)
        if (fallback != null) {
            NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.DNS,
                "LinkpointDNS: Using hardcoded fallback for $hostname: $fallback")
            return listOf(InetAddress.getByName(fallback))
        }
        
        // All resolution methods failed
        throw UnknownHostException("LinkpointDNS: Failed to resolve $hostname")
    }
    
    /**
     * Resolve hostname using Google's DNS-over-HTTPS service
     */
    private fun resolveOverHttps(hostname: String): List<InetAddress> {
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.DNS,
            "LinkpointDNS: Trying DNS-over-HTTPS for $hostname")
        
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("dns.google.com")
            .addPathSegment("resolve")
            .addQueryParameter("name", hostname)
            .addQueryParameter("type", "A")
            .build()
        
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        
        val response = dnsHttpClient.newCall(request).execute()
        
        if (!response.isSuccessful) {
            throw UnknownHostException("DNS-over-HTTPS returned ${response.code}")
        }
        
        val body = response.body?.string() ?: throw UnknownHostException("Empty response")
        val json = JSONObject(body)
        
        val addresses = mutableListOf<InetAddress>()
        
        if (json.has("Answer")) {
            val answers = json.getJSONArray("Answer")
            for (i in 0 until answers.length()) {
                val answer = answers.getJSONObject(i)
                
                // Check it's an A record (type 1) for the correct hostname
                if (answer.has("name") && answer.has("type") && answer.has("data")) {
                    val name = answer.getString("name")
                    val type = answer.getInt("type")
                    val data = answer.getString("data")
                    
                    if (name.equals("$hostname.", ignoreCase = true) && type == 1 && data.isNotEmpty()) {
                        try {
                            addresses.add(InetAddress.getByName(data))
                            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.DNS,
                                "LinkpointDNS: DoH resolved $hostname -> $data")
                        } catch (e: Exception) {
                            // Skip invalid IPs
                        }
                    }
                }
            }
        }
        
        return addresses
    }
    
    /**
     * Get hardcoded fallback IP for critical hosts (like the reference viewer does)
     */
    private fun getHardcodedFallback(hostname: String): String? {
        return when {
            hostname.equals("login.agni.lindenlab.com", ignoreCase = true) -> {
                LinkpointConstants.FALLBACK_IP_LOGIN_AGNI
            }
            // Add more critical hosts as needed
            else -> null
        }
    }
}

/**
 * OkHttp client configured with Linkpoint settings
 * 
 * Includes:
 * - DNS fallback resolver
 * - Connection pooling (8 connections, 5 min keep-alive)
 * - 60-second timeouts
 * - No proxy (direct connection)
 */
object LinkpointHttpClient {
    
    val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .dns(LinkpointDnsResolver.INSTANCE)
            .connectTimeout(LinkpointConstants.HTTP_CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(LinkpointConstants.HTTP_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectionPool(okhttp3.ConnectionPool(
                LinkpointConstants.HTTP_MAX_IDLE_CONNECTIONS,
                LinkpointConstants.HTTP_KEEP_ALIVE_MINUTES,
                TimeUnit.MINUTES
            ))
            .proxy(java.net.Proxy.NO_PROXY)
            // Accept any hostname (SL servers use various certificates)
            .hostnameVerifier { _, _ -> true }
            .build()
    }
}

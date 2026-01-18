package com.linkpoint.protocol.translation

import android.util.Log
import java.net.URL

/**
 * Translation Layer between Linkpoint (Kotlin) and Lumiya (legacy Java) communication patterns.
 * 
 * This layer bridges the communication differences between Linkpoint's modern Kotlin implementation
 * and the patterns used by Lumiya's original Java implementation. This is necessary because:
 * 
 * 1. **Capability URL Handling**: Lumiya has specific URL repair logic for the Agni grid that
 *    fixes incomplete hostnames in capability URLs returned by Linden Lab servers.
 *    
 * 2. **Message ID Encoding**: Lumiya uses signed byte/short values for message IDs which affects
 *    how high/medium/low frequency messages are encoded and decoded.
 *    
 * 3. **Protocol Versioning**: Different protocol versions may require different handling of
 *    certain message types or capability responses.
 *    
 * 4. **Grid-Specific Quirks**: The official SL grid (Agni) has specific behaviors that need
 *    special handling compared to OpenSim grids.
 *
 * Based on analysis of Lumiya's decompiled source code:
 * - SLCaps.java: Capability URL repair logic
 * - SLAgentCircuit.java: Message handling patterns
 * - SLMessage.java: Message ID encoding/decoding
 * 
 * @see com.linkpoint.protocol.capabilities.CapabilityManager
 * @see com.linkpoint.protocol.messages.UDPConnection
 */
object LumiyaTranslationLayer {
    
    private const val TAG = "LumiyaTranslation"
    
    // Grid detection patterns
    private const val AGNI_LOGIN_HOST = "login.agni.lindenlab.com"
    private const val AGNI_DOMAIN_SUFFIX = ".agni.lindenlab.com"
    private const val LINDENLAB_DOMAIN = ".lindenlab.com"
    
    // Simulator hostname patterns that need repair
    private const val SIM_HOST_PREFIX = "sim"
    private const val SIMHOST_PREFIX = "simhost-"
    
    /**
     * Grid type enumeration for grid-specific handling.
     */
    enum class GridType {
        /** Official Second Life grid (Agni) */
        AGNI,
        /** Second Life beta grid (Aditi) */
        ADITI,
        /** OpenSim or other third-party grid */
        OPENSIM,
        /** Unknown grid type */
        UNKNOWN
    }
    
    /**
     * Detect grid type from login URL.
     * 
     * This determines which grid-specific behaviors to apply.
     * 
     * @param loginUrl The login URL used for authentication
     * @return The detected grid type
     */
    fun detectGridType(loginUrl: String): GridType {
        return try {
            val url = URL(loginUrl)
            val host = url.host.lowercase()
            
            when {
                host == AGNI_LOGIN_HOST || host.endsWith(AGNI_DOMAIN_SUFFIX) -> GridType.AGNI
                host.contains("aditi") || host.contains("beta") -> GridType.ADITI
                host.endsWith(LINDENLAB_DOMAIN) -> GridType.AGNI
                else -> GridType.OPENSIM
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not parse login URL for grid detection: $loginUrl", e)
            GridType.UNKNOWN
        }
    }
    
    /**
     * Check if the given login URL is for the Agni (main SL) grid.
     * 
     * This matches Lumiya's detection logic in SLCaps.java:
     * ```java
     * z = new URL(str).getHost().equals("login.agni.lindenlab.com");
     * ```
     * 
     * @param loginUrl The login URL
     * @return true if this is the Agni grid
     */
    fun isAgniGrid(loginUrl: String): Boolean {
        return try {
            URL(loginUrl).host.equals(AGNI_LOGIN_HOST, ignoreCase = true)
        } catch (e: Exception) {
            Log.w(TAG, "Error checking Agni grid: ${e.message}")
            false
        }
    }
    
    /**
     * Repair a capability URL for the Agni grid.
     * 
     * Linden Lab's login servers sometimes return incomplete hostnames in capability URLs.
     * For example, a capability URL might contain just "sim1234" instead of the full
     * "sim1234.agni.lindenlab.com" hostname.
     * 
     * This method implements the same repair logic as Lumiya's SLCaps.repairCapabilityURL():
     * ```java
     * if (host.contains(".") || !host.startsWith("sim")) {
     *     return str;
     * }
     * str = str.replace(host, host + ".agni.lindenlab.com");
     * ```
     * 
     * @param isAgni Whether this is the Agni grid
     * @param capabilityUrl The capability URL to repair
     * @return The repaired URL, or the original if no repair was needed
     */
    fun repairCapabilityUrl(isAgni: Boolean, capabilityUrl: String): String {
        if (!isAgni) {
            return capabilityUrl
        }
        
        return try {
            val url = URL(capabilityUrl)
            val host = url.host
            
            // Check if hostname needs repair:
            // 1. Must NOT contain a dot (incomplete hostname)
            // 2. Must start with "sim" (simulator hostname pattern)
            if (host.contains(".") || !host.startsWith(SIM_HOST_PREFIX, ignoreCase = true)) {
                return capabilityUrl
            }
            
            // Repair by appending the Agni domain
            val repairedUrl = capabilityUrl.replace(host, host + AGNI_DOMAIN_SUFFIX)
            
            Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
            Log.i(TAG, "║ CAPABILITY URL REPAIRED (Lumiya compatibility)")
            Log.i(TAG, "║ Original: ${capabilityUrl.take(80)}...")
            Log.i(TAG, "║ Repaired: ${repairedUrl.take(80)}...")
            Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
            
            repairedUrl
        } catch (e: Exception) {
            Log.w(TAG, "Error repairing capability URL: ${e.message}")
            capabilityUrl
        }
    }
    
    /**
     * Repair a URL based on the reference login URL.
     * 
     * This is the public API that combines grid detection and URL repair.
     * Matches Lumiya's SLCaps.repairURL() method.
     * 
     * @param loginUrl The login URL (used to detect grid)
     * @param capabilityUrl The capability URL to potentially repair
     * @return The repaired URL
     */
    fun repairUrl(loginUrl: String, capabilityUrl: String): String {
        return try {
            val loginHost = URL(loginUrl).host
            if (loginHost.endsWith(LINDENLAB_DOMAIN, ignoreCase = true)) {
                repairCapabilityUrl(true, capabilityUrl)
            } else {
                capabilityUrl
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error in repairUrl: ${e.message}")
            capabilityUrl
        }
    }
    
    /**
     * Validate and potentially fix a seed capability URL.
     * 
     * The seed capability URL is critical for fetching all other capabilities.
     * This method ensures the URL is properly formatted for the target grid.
     * 
     * @param loginUrl The login URL used for authentication
     * @param seedCapUrl The seed capability URL from login response
     * @return The validated/repaired seed capability URL
     */
    fun prepareSeedCapability(loginUrl: String, seedCapUrl: String): String {
        val gridType = detectGridType(loginUrl)
        
        Log.d(TAG, "Preparing seed capability for grid type: $gridType")
        Log.d(TAG, "Original seed URL: ${seedCapUrl.take(80)}...")
        
        val repairedUrl = when (gridType) {
            GridType.AGNI, GridType.ADITI -> repairCapabilityUrl(true, seedCapUrl)
            else -> seedCapUrl
        }
        
        // Additional validation for known issues
        if (repairedUrl.isBlank()) {
            Log.e(TAG, "Seed capability URL is blank!")
            return seedCapUrl
        }
        
        // Ensure HTTPS for Linden Lab grids
        val finalUrl = if (gridType == GridType.AGNI || gridType == GridType.ADITI) {
            ensureHttps(repairedUrl)
        } else {
            repairedUrl
        }
        
        if (finalUrl != seedCapUrl) {
            Log.i(TAG, "Seed capability modified: ${finalUrl.take(80)}...")
        }
        
        return finalUrl
    }
    
    /**
     * Ensure a URL uses HTTPS protocol.
     * 
     * Linden Lab grids require HTTPS for capability URLs.
     * 
     * @param url The URL to check
     * @return The URL with HTTPS protocol
     */
    private fun ensureHttps(url: String): String {
        return if (url.startsWith("http://", ignoreCase = true)) {
            url.replaceFirst("http://", "https://", ignoreCase = true).also {
                Log.d(TAG, "Upgraded URL to HTTPS")
            }
        } else {
            url
        }
    }
    
    /**
     * Translate capability names between Linkpoint and Lumiya formats.
     * 
     * Some capability names may differ between implementations.
     * This ensures compatibility when requesting capabilities.
     * 
     * @param linkpointCapName The Linkpoint capability name
     * @return The Lumiya-compatible capability name
     */
    fun translateCapabilityName(linkpointCapName: String): String {
        // Most capability names are the same, but some may need translation
        return when (linkpointCapName) {
            // Add any name translations here if needed
            else -> linkpointCapName
        }
    }
    
    /**
     * Get the complete list of capabilities that Lumiya requests.
     * 
     * This ensures we request all capabilities that Lumiya expects,
     * which may be different from what modern viewers request.
     * 
     * Based on Lumiya's SLCaps.SLCapability enum.
     */
    fun getLumiyaCapabilityNames(): List<String> {
        return listOf(
            // Core capabilities from Lumiya's SLCapability enum
            "EventQueueGet",
            "GetTexture",
            "UploadBakedTexture",
            "FetchInventoryDescendents2",
            "GetDisplayNames",
            "UpdateNotecardAgentInventory",
            "NewFileAgentInventory",
            "CopyInventoryFromNotecard",
            "UpdateAvatarAppearance",
            "GetMesh",
            "UpdateNotecardTaskInventory",
            "UpdateScriptTask",
            "UpdateScriptAgent",
            "GroupMemberData",
            "HomeLocation",
            "ProvisionVoiceAccountRequest",
            "ParcelVoiceInfoRequest",
            "ChatSessionRequest",
            // Additional capabilities requested by modern viewers
            "FetchInventory2",
            "FetchLib2",
            "GetMesh2",
            "ViewerStats",
            "AgentState",
            "UpdateAgentInformation",
            "ObjectMedia",
            "EnvironmentSettings",
            "ExtEnvironment",
            "AvatarPickerSearch",
            "SearchStatRequest",
            "SimulatorFeatures",
            "AgentPreferences",
            "RenderMaterials"
        )
    }
    
    /**
     * Configuration for Lumiya compatibility mode.
     * 
     * These settings control how closely Linkpoint mimics Lumiya's behavior.
     */
    data class CompatibilityConfig(
        /** Enable capability URL repair for Agni grid */
        val repairCapabilityUrls: Boolean = true,
        
        /** Use Lumiya's exact capability list when fetching from seed */
        val useLumiyaCapabilityList: Boolean = true,
        
        /** Enable verbose logging for debugging protocol issues */
        val verboseLogging: Boolean = true,
        
        /** Timeout for seed capability requests (ms) */
        val seedCapabilityTimeoutMs: Long = 30_000,
        
        /** Number of retries for capability requests */
        val capabilityRetries: Int = 3,
        
        /** Enable legacy message ID handling */
        val useLegacyMessageIds: Boolean = true
    )
    
    // Default configuration
    @Volatile
    var config = CompatibilityConfig()
        private set
    
    /**
     * Update the compatibility configuration.
     */
    fun configure(newConfig: CompatibilityConfig) {
        config = newConfig
        Log.i(TAG, "Compatibility configuration updated: $newConfig")
    }
    
    /**
     * Log comprehensive diagnostic information about a capability request.
     */
    fun logCapabilityDiagnostics(
        operation: String,
        loginUrl: String?,
        capabilityUrl: String?,
        response: String?,
        error: String?
    ) {
        if (!config.verboseLogging) return
        
        Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ CAPABILITY DIAGNOSTICS: $operation")
        Log.i(TAG, "╠══════════════════════════════════════════════════════════════════")
        loginUrl?.let { Log.i(TAG, "║ Login URL: ${it.take(60)}...") }
        loginUrl?.let { Log.i(TAG, "║ Grid Type: ${detectGridType(it)}") }
        capabilityUrl?.let { Log.i(TAG, "║ Capability URL: ${it.take(60)}...") }
        response?.let { 
            val preview = it.take(200).replace("\n", " ").replace("\r", "")
            Log.i(TAG, "║ Response preview: $preview...")
        }
        error?.let { Log.e(TAG, "║ Error: $it") }
        Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
    }
}

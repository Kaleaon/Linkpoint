package com.linkpoint.assets.transport

import android.util.Log
import com.linkpoint.assets.AssetType
import com.linkpoint.protocol.capabilities.CapabilityManager
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Orchestrates asset downloads by evaluating registered transport adapters sequentially
 * in priority order until an adapter successfully retrieves the asset.
 */
class AssetFetchStrategyManager(
    initialAdapters: List<AssetFetcherAdapter> = emptyList()
) {
    @PublishedApi
    internal val adapters = CopyOnWriteArrayList<AssetFetcherAdapter>(initialAdapters)

    companion object {
        private const val TAG = "AssetFetchStrategyManager"

        private fun logD(msg: String) {
            try { Log.d(TAG, msg) } catch (_: Throwable) {}
        }

        private fun logW(msg: String) {
            try { Log.w(TAG, msg) } catch (_: Throwable) {}
        }

        /**
         * Creates a strategy manager pre-configured with Capability, REST, and UDP adapters.
         */
        fun createDefault(
            capabilityManager: CapabilityManager? = null,
            assetServerUrl: String? = null,
            packetSender: ((assetId: UUID, discardLevel: Int) -> Unit)? = null
        ): AssetFetchStrategyManager {
            val manager = AssetFetchStrategyManager()
            manager.registerAdapter(CapabilityAssetAdapter(capabilityManager))
            manager.registerAdapter(RestAssetServerAdapter(assetServerUrl))
            manager.registerAdapter(UdpTextureAdapter(packetSender))
            return manager
        }
    }

    fun registerAdapter(adapter: AssetFetcherAdapter) {
        // Prevent duplicate adapters of the same name
        adapters.removeIf { it.name == adapter.name }
        adapters.add(adapter)
        logD("Registered transport adapter '${adapter.name}' with priority ${adapter.priority}")
    }

    fun unregisterAdapter(adapter: AssetFetcherAdapter) {
        adapters.remove(adapter)
        logD("Unregistered transport adapter '${adapter.name}'")
    }

    fun getAdapters(): List<AssetFetcherAdapter> {
        return adapters.sortedBy { it.priority }
    }

    inline fun <reified T : AssetFetcherAdapter> getAdapter(): T? {
        return adapters.filterIsInstance<T>().firstOrNull()
    }

    fun updateAssetServerUrl(url: String?) {
        getAdapter<RestAssetServerAdapter>()?.assetServerUrl = url
        logD("Updated RestAssetServerAdapter url to: $url")
    }

    /**
     * Evaluates registered adapters in priority order (Capability -> REST -> UDP).
     * Returns the raw asset bytes from the first adapter that succeeds, or null if all fail.
     */
    suspend fun fetchAsset(assetId: UUID, assetType: AssetType): ByteArray? {
        val candidateAdapters = adapters
            .filter { it.canFetch(assetType) }
            .sortedBy { it.priority }

        if (candidateAdapters.isEmpty()) {
            logW("No transport adapters capable of fetching $assetType (assetId=$assetId)")
            return null
        }

        for (adapter in candidateAdapters) {
            logD("Trying transport strategy '${adapter.name}' for $assetType $assetId")
            val bytes = adapter.fetchAsset(assetId, assetType)
            if (bytes != null && bytes.isNotEmpty()) {
                logD("Successfully downloaded $assetType $assetId using '${adapter.name}'")
                return bytes
            }
        }

        logW("All transport strategies failed for $assetType $assetId")
        return null
    }
}

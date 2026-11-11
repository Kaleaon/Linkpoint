package com.linkpoint.assets.transport

import com.linkpoint.assets.AssetType
import java.util.UUID

/**
 * Strategy interface for fetching assets across different transport mechanisms
 * (HTTP capabilities, REST asset services, UDP packet streams, etc.).
 */
interface AssetFetcherAdapter {
    /**
     * Descriptive name of the transport adapter implementation.
     */
    val name: String

    /**
     * Priority order for evaluation (lower values indicate higher priority).
     * Recommended order:
     * - CapabilityAssetAdapter: 10
     * - RestAssetServerAdapter: 20
     * - UdpTextureAdapter: 30
     */
    val priority: Int

    /**
     * Checks whether this adapter can attempt to fetch the given asset type.
     */
    fun canFetch(assetType: AssetType): Boolean

    /**
     * Attempts to fetch raw asset bytes for the specified asset UUID and asset type.
     * Returns null if the fetch fails, receives an HTTP 404 response, or the mechanism is unavailable.
     */
    suspend fun fetchAsset(assetId: UUID, assetType: AssetType): ByteArray?
}

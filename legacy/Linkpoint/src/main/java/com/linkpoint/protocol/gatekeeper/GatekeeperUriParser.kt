package com.linkpoint.protocol.gatekeeper

import android.net.Uri
import android.util.Log
import com.linkpoint.world.manifold.ManifoldFrame
import com.linkpoint.world.manifold.TopologyType

/**
 * Parsed Gatekeeper URI target details.
 */
data class GatekeeperTarget(
    val gridUri: String,
    val gatekeeperUrl: String,
    val regionName: String = "Remote Region",
    val x: Int = 128,
    val y: Int = 128,
    val z: Int = 30,
    val manifoldFrame: ManifoldFrame = ManifoldFrame.IDENTITY
)

/**
 * Gatekeeper URI parser for hypergrid addressing with parametric manifold frame parameter extraction.
 */
object GatekeeperUriParser {
    private const val TAG = "GatekeeperUriParser"

    fun parse(uriString: String): GatekeeperTarget? {
        if (uriString.isBlank()) return null

        try {
            val cleanUri = uriString.trim()
            val queryStartIndex = cleanUri.indexOf('?')
            val baseUriStr = if (queryStartIndex != -1) cleanUri.substring(0, queryStartIndex) else cleanUri
            val queryString = if (queryStartIndex != -1) cleanUri.substring(queryStartIndex + 1) else ""

            val queryParams = parseQueryParams(queryString)
            val manifoldFrame = extractManifoldFrame(queryParams)

            var host = ""
            var port = 8002
            var scheme = "http"
            var regionName = "Remote Region"
            var x = 128
            var y = 128
            var z = 30

            val workingStr = when {
                baseUriStr.startsWith("secondlife://") -> {
                    scheme = "http"
                    baseUriStr.substring("secondlife://".length)
                }
                baseUriStr.startsWith("https://") -> {
                    scheme = "https"
                    baseUriStr.substring("https://".length)
                }
                baseUriStr.startsWith("http://") -> {
                    scheme = "http"
                    baseUriStr.substring("http://".length)
                }
                else -> {
                    scheme = "http"
                    baseUriStr
                }
            }

            if (workingStr.contains(":")) {
                val colonParts = workingStr.split(":")
                host = colonParts[0]
                if (colonParts.size >= 2) {
                    val portAndRest = colonParts[1]
                    val portEndIdx = portAndRest.indexOf('/')
                    val portStr = if (portEndIdx != -1) portAndRest.substring(0, portEndIdx) else portAndRest
                    port = portStr.toIntOrNull() ?: 8002
                }
                if (colonParts.size >= 3) {
                    val rest = colonParts.subList(2, colonParts.size).joinToString(":")
                    val pathParts = rest.split("/").filter { it.isNotBlank() }
                    if (pathParts.isNotEmpty()) {
                        regionName = pathParts[0]
                    }
                    if (pathParts.size >= 4) {
                        x = pathParts[1].toIntOrNull() ?: 128
                        y = pathParts[2].toIntOrNull() ?: 128
                        z = pathParts[3].toIntOrNull() ?: 30
                    }
                }
            } else {
                val pathParts = workingStr.split("/").filter { it.isNotBlank() }
                if (pathParts.isNotEmpty()) {
                    host = pathParts[0]
                    if (pathParts.size >= 2 && !pathParts[1].equals("gatekeeper", ignoreCase = true)) {
                        regionName = pathParts[1]
                    }
                    if (pathParts.size >= 5) {
                        x = pathParts[2].toIntOrNull() ?: 128
                        y = pathParts[3].toIntOrNull() ?: 128
                        z = pathParts[4].toIntOrNull() ?: 30
                    }
                }
            }

            if (host.isBlank()) return null

            val gridUri = "$scheme://$host:$port"
            val gatekeeperUrl = "$gridUri/gatekeeper"

            return GatekeeperTarget(
                gridUri = gridUri,
                gatekeeperUrl = gatekeeperUrl,
                regionName = regionName,
                x = x,
                y = y,
                z = z,
                manifoldFrame = manifoldFrame
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse Gatekeeper URI string '$uriString': ${e.message}")
            return null
        }
    }

    private fun parseQueryParams(queryString: String): Map<String, String> {
        if (queryString.isBlank()) return emptyMap()
        val params = mutableMapOf<String, String>()
        for (pair in queryString.split("&")) {
            val keyValues = pair.split("=")
            if (keyValues.isNotEmpty() && keyValues[0].isNotBlank()) {
                val key = keyValues[0].trim().lowercase()
                val value = if (keyValues.size > 1) keyValues[1].trim() else ""
                params[key] = value
            }
        }
        return params
    }

    private fun extractManifoldFrame(queryParams: Map<String, String>): ManifoldFrame {
        val frameId = queryParams["manifold_frame_id"] ?: queryParams["frame_id"] ?: ManifoldFrame.DEFAULT_FRAME_ID
        val topologyType = TopologyType.fromString(queryParams["topology_type"])
        val radius = queryParams["radius"]?.toDoubleOrNull() ?: 0.0
        val curvature = queryParams["curvature"]?.toDoubleOrNull() ?: 0.0
        val originX = queryParams["origin_x"]?.toDoubleOrNull() ?: 0.0
        val originY = queryParams["origin_y"]?.toDoubleOrNull() ?: 0.0
        val originZ = queryParams["origin_z"]?.toDoubleOrNull() ?: 0.0
        val sizeX = queryParams["size_x"]?.toDoubleOrNull() ?: queryParams["circumference"]?.toDoubleOrNull() ?: ManifoldFrame.DEFAULT_SIZE
        val sizeY = queryParams["size_y"]?.toDoubleOrNull() ?: ManifoldFrame.DEFAULT_SIZE

        return ManifoldFrame(
            frameId = frameId,
            topologyType = topologyType,
            radius = radius,
            curvature = curvature,
            originX = originX,
            originY = originY,
            originZ = originZ,
            sizeX = sizeX,
            sizeY = sizeY
        )
    }
}

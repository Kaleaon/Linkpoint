package com.linkpoint.inventory

import kotlinx.coroutines.delay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.UUID
import kotlin.math.min

/**
 * AISv3 HTTP client that supports CRUD operations with bounded exponential backoff.
 */
interface AisOperations {
    suspend fun getSubtree(folderId: UUID): InventorySubtreeData
    suspend fun getItem(itemId: UUID): InventoryItemData
    suspend fun patchItem(itemId: UUID, patch: InventoryItemPatch)
    suspend fun postFolder(request: CreateFolderRequest): UUID
    suspend fun deleteNode(relativePath: String)
}

class AisClient(
    private val endpointProvider: InventoryApiEndpointProvider,
    private val transport: AisTransport = OkHttpAisTransport(),
    private val json: Json = Json { ignoreUnknownKeys = true },
    private val baseDelayMs: Long = 150,
    private val maxDelayMs: Long = 2_000,
    private val maxServerRetries: Int = 3,
    private val sleeper: suspend (Long) -> Unit = { delay(it) }
) : AisOperations {

    override suspend fun getSubtree(folderId: UUID): InventorySubtreeData {
        val response = executeWithRetry("GET", "category/$folderId")
        return json.decodeFromString(InventorySubtreePayload.serializer(), response.body).toDomain()
    }

    override suspend fun getItem(itemId: UUID): InventoryItemData {
        val response = executeWithRetry("GET", "item/$itemId")
        return json.decodeFromString(InventoryItemPayload.serializer(), response.body).toDomain()
    }

    override suspend fun patchItem(itemId: UUID, patch: InventoryItemPatch) {
        executeWithRetry(
            method = "PATCH",
            relativePath = "item/$itemId",
            body = json.encodeToString(InventoryItemPatch.serializer(), patch)
        )
    }

    override suspend fun postFolder(request: CreateFolderRequest): UUID {
        val response = executeWithRetry(
            method = "POST",
            relativePath = "category",
            body = json.encodeToString(CreateFolderRequest.serializer(), request)
        )
        return UUID.fromString(
            json.decodeFromString(CreateFolderResponse.serializer(), response.body).folderId
        )
    }

    override suspend fun deleteNode(relativePath: String) {
        executeWithRetry(method = "DELETE", relativePath = relativePath)
    }

    private suspend fun executeWithRetry(
        method: String,
        relativePath: String,
        body: String? = null
    ): AisHttpResponse {
        var attempt = 0
        var lastIOException: IOException? = null

        while (attempt <= maxServerRetries) {
            try {
                val url = endpointProvider.getInventoryApiBaseUrl()?.trimEnd('/')
                    ?: throw AisCapabilityUnavailableException("InventoryAPIv3 capability unavailable")
                val response = transport.execute(
                    AisHttpRequest(
                        method = method,
                        url = "$url/$relativePath",
                        body = body
                    )
                )

                when {
                    response.code == 404 -> throw AisCapabilityUnavailableException("AIS endpoint returned 404")
                    response.code in 200..299 -> return response
                    response.code in 500..599 -> {
                        if (attempt == maxServerRetries) {
                            throw AisServerException(response.code, "AIS server error after retries")
                        }
                        val delayMs = min(maxDelayMs, baseDelayMs * (1L shl attempt))
                        sleeper(delayMs)
                    }
                    else -> throw AisHttpException(response.code, "AIS request failed with HTTP ${response.code}")
                }
            } catch (io: IOException) {
                lastIOException = io
                if (attempt == maxServerRetries) {
                    throw io
                }
                val delayMs = min(maxDelayMs, baseDelayMs * (1L shl attempt))
                sleeper(delayMs)
            }
            attempt++
        }

        throw lastIOException ?: AisServerException(599, "AIS retries exhausted")
    }
}

interface InventoryApiEndpointProvider {
    fun getInventoryApiBaseUrl(): String?
}

interface AisTransport {
    suspend fun execute(request: AisHttpRequest): AisHttpResponse
}

data class AisHttpRequest(
    val method: String,
    val url: String,
    val body: String? = null
)

data class AisHttpResponse(
    val code: Int,
    val body: String
)

class OkHttpAisTransport(
    private val client: OkHttpClient = OkHttpClient()
) : AisTransport {
    override suspend fun execute(request: AisHttpRequest): AisHttpResponse {
        val builder = Request.Builder().url(request.url)
        val requestBody = request.body?.toRequestBody("application/json".toMediaType())

        when (request.method) {
            "GET" -> builder.get()
            "POST" -> builder.post(requestBody ?: "{}".toRequestBody("application/json".toMediaType()))
            "PATCH" -> builder.patch(requestBody ?: "{}".toRequestBody("application/json".toMediaType()))
            "DELETE" -> if (requestBody != null) builder.delete(requestBody) else builder.delete()
            else -> throw IllegalArgumentException("Unsupported method ${request.method}")
        }

        client.newCall(builder.build()).execute().use { response ->
            return AisHttpResponse(
                code = response.code,
                body = response.body?.string().orEmpty()
            )
        }
    }
}

class AisCapabilityUnavailableException(message: String) : Exception(message)
class AisServerException(val status: Int, message: String) : Exception(message)
class AisHttpException(val status: Int, message: String) : Exception(message)

@Serializable
data class InventorySubtreePayload(
    val folder: InventoryFolderPayload,
    @SerialName("categories") val folders: List<InventoryFolderPayload> = emptyList(),
    val items: List<InventoryItemPayload> = emptyList()
)

@Serializable
data class InventoryFolderPayload(
    @SerialName("folder_id") val folderId: String,
    @SerialName("parent_id") val parentId: String,
    val name: String,
    val version: Int = 0,
    val type: Int = -1
)

@Serializable
data class InventoryItemPayload(
    @SerialName("item_id") val itemId: String,
    @SerialName("parent_id") val parentId: String,
    val name: String,
    @SerialName("asset_id") val assetId: String,
    @SerialName("inv_type") val inventoryType: Int,
    @SerialName("asset_type") val assetType: Int,
    val version: Int = 0
)

@Serializable
data class InventoryItemPatch(
    val name: String? = null,
    @SerialName("parent_id") val parentId: String? = null
)

@Serializable
data class CreateFolderRequest(
    @SerialName("parent_id") val parentId: String,
    val name: String,
    val type: Int = -1
)

@Serializable
data class CreateFolderResponse(
    @SerialName("folder_id") val folderId: String
)

data class InventorySubtreeData(
    val folder: InventoryFolderData,
    val folders: List<InventoryFolderData>,
    val items: List<InventoryItemData>
)

data class InventoryFolderData(
    val folderId: UUID,
    val parentId: UUID,
    val name: String,
    val version: Int,
    val type: Int
)

data class InventoryItemData(
    val itemId: UUID,
    val parentId: UUID,
    val name: String,
    val assetId: UUID,
    val inventoryType: Int,
    val assetType: Int,
    val version: Int
)

private fun InventorySubtreePayload.toDomain(): InventorySubtreeData =
    InventorySubtreeData(
        folder = folder.toDomain(),
        folders = folders.map { it.toDomain() },
        items = items.map { it.toDomain() }
    )

private fun InventoryFolderPayload.toDomain(): InventoryFolderData =
    InventoryFolderData(
        folderId = UUID.fromString(folderId),
        parentId = UUID.fromString(parentId),
        name = name,
        version = version,
        type = type
    )

private fun InventoryItemPayload.toDomain(): InventoryItemData =
    InventoryItemData(
        itemId = UUID.fromString(itemId),
        parentId = UUID.fromString(parentId),
        name = name,
        assetId = UUID.fromString(assetId),
        inventoryType = inventoryType,
        assetType = assetType,
        version = version
    )

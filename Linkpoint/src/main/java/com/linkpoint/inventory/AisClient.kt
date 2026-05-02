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

    // Operations modelled on cinderblocks/libremetaverse
    // `LibreMetaverse.Inventory.InventoryAISClient.cs` — the canonical
    // surface for AISv3 (cap `InventoryAPIv3`). Default implementations
    // throw so existing custom AisClient subclasses keep compiling
    // until they want to opt in.

    /**
     * PATCH a category (folder) — typical use is renaming a folder or
     * reparenting it. URL: `category/${folderId}`.
     */
    suspend fun patchCategory(folderId: UUID, patch: InventoryFolderPatch): Unit =
        throw NotImplementedError("patchCategory not implemented")

    /**
     * Slam (replace) the contents of a folder. URL: `category/${folderId}`,
     * verb PUT, body is the new folder contents. Used by the LL viewer
     * during inventory rebuild and outfit replace.
     */
    suspend fun slamFolder(folderId: UUID, contents: String): Unit =
        throw NotImplementedError("slamFolder not implemented")

    /**
     * Delete every descendant (sub-folders + items) of a category but
     * leave the category itself. URL: `category/${folderId}/children`,
     * verb DELETE.
     */
    suspend fun purgeDescendents(folderId: UUID): Unit =
        throw NotImplementedError("purgeDescendents not implemented")

    /**
     * Copy a category to a new parent. URL: `category/${sourceId}`,
     * verb COPY (a non-standard HTTP method LL inherits from WebDAV),
     * `Destination` header carries the new parent UUID.
     *
     * `copySubfolders` controls whether the operation walks into nested
     * folders (default true matches the LL viewer's user-facing
     * "copy folder" behaviour).
     */
    suspend fun copyCategory(sourceId: UUID, destinationParentId: UUID, copySubfolders: Boolean = true): Unit =
        throw NotImplementedError("copyCategory not implemented")

    /**
     * Move a category to a new parent. URL: `category/${sourceId}`,
     * verb MOVE, `Destination` header carries the new parent UUID.
     * Typical use: drag-and-drop a folder in the inventory UI.
     */
    suspend fun moveCategory(sourceId: UUID, destinationParentId: UUID): Unit =
        throw NotImplementedError("moveCategory not implemented")

    /**
     * POST an inventory item (or link) under the given parent. The
     * `createLink` flag controls whether the response is parsed as
     * an item or a link — the wire shape differs at `_embedded.items`
     * vs `_embedded.links`. Mirrors libremetaverse `CreateInventory`.
     */
    suspend fun postInventory(parentId: UUID, body: String, createLink: Boolean = false): UUID =
        throw NotImplementedError("postInventory not implemented")
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

    override suspend fun patchCategory(folderId: UUID, patch: InventoryFolderPatch) {
        executeWithRetry(
            method = "PATCH",
            relativePath = "category/$folderId",
            body = json.encodeToString(InventoryFolderPatch.serializer(), patch)
        )
    }

    override suspend fun slamFolder(folderId: UUID, contents: String) {
        executeWithRetry(method = "PUT", relativePath = "category/$folderId", body = contents)
    }

    override suspend fun purgeDescendents(folderId: UUID) {
        executeWithRetry(method = "DELETE", relativePath = "category/$folderId/children")
    }

    override suspend fun copyCategory(sourceId: UUID, destinationParentId: UUID, copySubfolders: Boolean) {
        // libremetaverse appends `,depth=0` (sic — the comma matches the
        // LL caps URL convention) to the `tid=` query param when
        // `copySubfolders` is requested. The "depth=0" is misleading —
        // it actually enables a full tree copy on the LL server side.
        val query = if (copySubfolders) "?tid=${UUID.randomUUID()},depth=0" else "?tid=${UUID.randomUUID()}"
        executeWithRetry(
            method = "COPY",
            relativePath = "category/$sourceId$query",
            headers = mapOf("Destination" to destinationParentId.toString())
        )
    }

    override suspend fun moveCategory(sourceId: UUID, destinationParentId: UUID) {
        executeWithRetry(
            method = "MOVE",
            relativePath = "category/$sourceId?tid=${UUID.randomUUID()}",
            headers = mapOf("Destination" to destinationParentId.toString())
        )
    }

    override suspend fun postInventory(parentId: UUID, body: String, createLink: Boolean): UUID {
        val response = executeWithRetry(
            method = "POST",
            relativePath = "category/$parentId?tid=${UUID.randomUUID()}",
            body = body
        )
        // Response shape: {_embedded: {items: {<uuid>: {...}}}} or
        // {_embedded: {links: {<uuid>: {...}}}}. We parse just enough
        // to pluck the first key.
        val key = if (createLink) "links" else "items"
        return parseEmbeddedFirstId(response.body, key)
            ?: throw AisHttpException(0, "AIS create response missing _embedded.$key")
    }

    private fun parseEmbeddedFirstId(body: String, key: String): UUID? {
        val obj = runCatching { json.parseToJsonElement(body) }.getOrNull() ?: return null
        val embedded = obj.jsonObjectOrNull()?.get("_embedded")?.jsonObjectOrNull()
            ?: return null
        val coll = embedded[key]?.jsonObjectOrNull() ?: return null
        val firstKey = coll.keys.firstOrNull() ?: return null
        return runCatching { UUID.fromString(firstKey) }.getOrNull()
    }

    private fun kotlinx.serialization.json.JsonElement.jsonObjectOrNull():
            kotlinx.serialization.json.JsonObject? =
        this as? kotlinx.serialization.json.JsonObject

    private suspend fun executeWithRetry(
        method: String,
        relativePath: String,
        body: String? = null,
        headers: Map<String, String> = emptyMap()
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
                        body = body,
                        headers = headers
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
    val body: String? = null,
    /**
     * Per-request headers. Used by COPY/MOVE which need a `Destination`
     * header carrying the target parent UUID. Empty for the common
     * GET/POST/PATCH/DELETE paths.
     */
    val headers: Map<String, String> = emptyMap()
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
        val mediaType = "application/json".toMediaType()
        val requestBody = request.body?.toRequestBody(mediaType)

        when (request.method) {
            "GET" -> builder.get()
            "POST" -> builder.post(requestBody ?: "{}".toRequestBody(mediaType))
            "PATCH" -> builder.patch(requestBody ?: "{}".toRequestBody(mediaType))
            "PUT" -> builder.put(requestBody ?: "{}".toRequestBody(mediaType))
            "DELETE" -> if (requestBody != null) builder.delete(requestBody) else builder.delete()
            // Custom HTTP verbs from the LL AIS / WebDAV inheritance.
            // OkHttp.method() accepts any verb name; the no-body forms
            // pass null for the request body.
            "COPY", "MOVE" -> builder.method(request.method, requestBody)
            else -> throw IllegalArgumentException("Unsupported method ${request.method}")
        }

        // Per-request headers (e.g. Destination on COPY/MOVE).
        request.headers.forEach { (k, v) -> builder.addHeader(k, v) }

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

/**
 * PATCH body for `/category/{id}`. Mirrors libremetaverse's
 * `UpdateCategory` payload: only fields the caller wants to change
 * are present; the server merges with existing folder state.
 */
@Serializable
data class InventoryFolderPatch(
    val name: String? = null,
    @SerialName("parent_id") val parentId: String? = null,
    /**
     * Folder type code per `LLFolderType`. Pass null to leave
     * unchanged. Type changes are uncommon outside system folder
     * setup; renames are the typical case.
     */
    val type: Int? = null
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

package com.linkpoint.inventory.notecard

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.CapabilityRequester
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDUUID
import com.linkpoint.protocol.transfer.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Notecard Manager - Handles reading and writing notecards.
 * 
 * Based on the reference viewer's SLNotecard.java
 * 
 * Notecards in Second Life contain:
 * - Text content
 * - Embedded inventory items (textures, scripts, objects, etc.)
 * 
 * Notecard format:
 * Linden text version 2
 * {
 *   LLEmbeddedItems version 1
 *   {
 *     count N
 *     { ... embedded item definitions ... }
 *   }
 *   Text length L
 *   <text content>
 * }
 */
class NotecardManager(
    private val transferManager: TransferManager,
    private val capabilityManager: CapabilityManager,
    private val httpClient: OkHttpClient = OkHttpClient(),
    private val capabilityRequest: suspend (String, LLSDMap) -> com.linkpoint.protocol.llsd.LLSDValue? = { capName, body ->
        capabilityManager.request(capName, body)
    }
) {
    companion object {
        private const val TAG = "NotecardManager"
        
        // Notecard format markers
        const val NOTECARD_HEADER = "Linden text version 2"
        const val EMBEDDED_ITEMS_HEADER = "LLEmbeddedItems version 1"
        const val TEXT_MARKER = "Text length"
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Cache of loaded notecards
    private val notecardCache = ConcurrentHashMap<UUID, Notecard>()
    
    // Pending notecard loads
    private val pendingLoads = ConcurrentHashMap<UUID, MutableList<NotecardCallback>>()
    
    /**
     * Load a notecard by asset ID.
     */
    fun loadNotecard(
        assetId: UUID,
        callback: NotecardCallback? = null
    ) {
        // Check cache first
        notecardCache[assetId]?.let { cached ->
            callback?.onNotecardLoaded(cached)
            return
        }
        
        // Add to pending callbacks
        val callbacks = pendingLoads.getOrPut(assetId) { mutableListOf() }
        callback?.let { callbacks.add(it) }
        
        // Only start transfer if this is the first request
        if (callbacks.size == 1) {
            val transfer = transferManager ?: run {
                callback?.onNotecardError("Transfer manager unavailable")
                return
            }
            transfer.requestAssetTransfer(
                assetId = assetId,
                assetType = AssetType.NOTECARD,
                callback = { key, result ->
                    handleTransferResult(assetId, result)
                }
            )
        }
    }
    
    /**
     * Load a notecard from inventory item.
     */
    fun loadNotecardFromItem(
        itemId: UUID,
        assetId: UUID,
        ownerId: UUID,
        taskId: UUID? = null,
        callback: NotecardCallback? = null
    ) {
        // Check cache first
        notecardCache[assetId]?.let { cached ->
            callback?.onNotecardLoaded(cached)
            return
        }
        
        val callbacks = pendingLoads.getOrPut(assetId) { mutableListOf() }
        callback?.let { callbacks.add(it) }
        
        if (callbacks.size == 1) {
            val transfer = transferManager ?: run {
                callback?.onNotecardError("Transfer manager unavailable")
                return
            }
            transfer.requestInventoryItemTransfer(
                itemId = itemId,
                assetId = assetId,
                ownerId = ownerId,
                taskId = taskId,
                assetType = AssetType.NOTECARD,
                callback = { key, result ->
                    handleTransferResult(assetId, result)
                }
            )
        }
    }
    
    private fun handleTransferResult(assetId: UUID, result: TransferResult) {
        val callbacks = pendingLoads.remove(assetId) ?: return
        
        when (result) {
            is TransferResult.Success -> {
                try {
                    val notecard = parseNotecard(assetId, result.data)
                    notecardCache[assetId] = notecard
                    
                    callbacks.forEach { it.onNotecardLoaded(notecard) }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse notecard $assetId", e)
                    callbacks.forEach { it.onNotecardError("Parse error: ${e.message}") }
                }
            }
            is TransferResult.Error -> {
                Log.e(TAG, "Failed to load notecard $assetId: ${result.message}")
                callbacks.forEach { it.onNotecardError(result.message) }
            }
        }
    }
    
    /**
     * Parse notecard data from raw bytes.
     */
    private fun parseNotecard(assetId: UUID, data: ByteArray): Notecard {
        val content = String(data, Charsets.UTF_8)
        val lines = content.lines()
        
        Log.d(TAG, "Parsing notecard: ${data.size} bytes, ${lines.size} lines")
        
        // Validate header
        if (lines.isEmpty() || !lines[0].startsWith("Linden text")) {
            throw IllegalArgumentException("Invalid notecard format: missing header")
        }
        
        val embeddedItems = mutableListOf<EmbeddedItem>()
        var textContent = ""
        var textLength = 0
        
        var i = 0
        while (i < lines.size) {
            val line = lines[i].trim()
            
            when {
                line.startsWith(EMBEDDED_ITEMS_HEADER) -> {
                    // Parse embedded items section
                    i++
                    val itemsResult = parseEmbeddedItems(lines, i)
                    embeddedItems.addAll(itemsResult.first)
                    i = itemsResult.second
                }
                line.startsWith(TEXT_MARKER) -> {
                    // Parse text length
                    val lengthStr = line.substringAfter(TEXT_MARKER).trim()
                    textLength = lengthStr.toIntOrNull() ?: 0
                    
                    // Next line(s) contain the text
                    i++
                    val textBuilder = StringBuilder()
                    while (i < lines.size && textBuilder.length < textLength) {
                        if (textBuilder.isNotEmpty()) textBuilder.append("\n")
                        textBuilder.append(lines[i])
                        i++
                    }
                    textContent = textBuilder.toString()
                }
                else -> i++
            }
        }
        
        return Notecard(
            assetId = assetId,
            text = textContent,
            embeddedItems = embeddedItems
        )
    }
    
    /**
     * Parse embedded items section.
     * Returns list of items and the next line index to process.
     */
    private fun parseEmbeddedItems(lines: List<String>, startIndex: Int): Pair<List<EmbeddedItem>, Int> {
        val items = mutableListOf<EmbeddedItem>()
        var i = startIndex
        var count = 0
        var braceDepth = 0
        
        while (i < lines.size) {
            val line = lines[i].trim()
            
            when {
                line == "{" -> {
                    braceDepth++
                }
                line == "}" -> {
                    braceDepth--
                    if (braceDepth == 0) {
                        i++
                        break
                    }
                }
                line.startsWith("count") -> {
                    count = line.substringAfter("count").trim().toIntOrNull() ?: 0
                }
                line.startsWith("inv_item") -> {
                    // Parse inventory item
                    val itemResult = parseEmbeddedItem(lines, i)
                    items.add(itemResult.first)
                    i = itemResult.second
                    continue
                }
            }
            i++
        }
        
        return Pair(items, i)
    }
    
    /**
     * Parse a single embedded item.
     */
    private fun parseEmbeddedItem(lines: List<String>, startIndex: Int): Pair<EmbeddedItem, Int> {
        var i = startIndex
        var braceDepth = 0
        
        var itemId: UUID? = null
        var assetId: UUID? = null
        var assetType: Int = 0
        var name = ""
        var description = ""
        
        while (i < lines.size) {
            val line = lines[i].trim()
            
            when {
                line == "{" -> braceDepth++
                line == "}" -> {
                    braceDepth--
                    if (braceDepth == 0) {
                        i++
                        break
                    }
                }
                line.startsWith("item_id") -> {
                    itemId = parseUUID(line.substringAfter("item_id").trim())
                }
                line.startsWith("asset_id") -> {
                    assetId = parseUUID(line.substringAfter("asset_id").trim())
                }
                line.startsWith("type") -> {
                    assetType = line.substringAfter("type").trim().toIntOrNull() ?: 0
                }
                line.startsWith("name") -> {
                    name = line.substringAfter("name").trim().removeSurrounding("|")
                }
                line.startsWith("desc") -> {
                    description = line.substringAfter("desc").trim().removeSurrounding("|")
                }
            }
            i++
        }
        
        return Pair(
            EmbeddedItem(
                itemId = itemId ?: UUID(0, 0),
                assetId = assetId ?: UUID(0, 0),
                assetType = AssetType.fromCode(assetType) ?: AssetType.OBJECT,
                name = name,
                description = description
            ),
            i
        )
    }
    
    private fun parseUUID(str: String): UUID? {
        return try {
            UUID.fromString(str.trim())
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Create notecard data from content.
     */
    fun createNotecardData(text: String, embeddedItems: List<EmbeddedItem> = emptyList()): ByteArray {
        val sb = StringBuilder()
        
        // Header
        sb.appendLine(NOTECARD_HEADER)
        sb.appendLine("{")
        
        // Embedded items
        sb.appendLine("$EMBEDDED_ITEMS_HEADER")
        sb.appendLine("{")
        sb.appendLine("count ${embeddedItems.size}")
        
        embeddedItems.forEach { item ->
            sb.appendLine("{")
            sb.appendLine("\tinv_item\t0")
            sb.appendLine("\t{")
            sb.appendLine("\t\titem_id\t${item.itemId}")
            sb.appendLine("\t\tparent_id\t${UUID(0, 0)}")
            sb.appendLine("\t\tpermissions 0")
            sb.appendLine("\t\t{")
            sb.appendLine("\t\t\tbase_mask\t7fffffff")
            sb.appendLine("\t\t\towner_mask\t7fffffff")
            sb.appendLine("\t\t\tgroup_mask\t0")
            sb.appendLine("\t\t\teveryone_mask\t0")
            sb.appendLine("\t\t\tnext_owner_mask\t7fffffff")
            sb.appendLine("\t\t}")
            sb.appendLine("\t\tasset_id\t${item.assetId}")
            sb.appendLine("\t\ttype\t${item.assetType.code}")
            sb.appendLine("\t\tname\t|${item.name}|")
            sb.appendLine("\t\tdesc\t|${item.description}|")
            sb.appendLine("\t}")
            sb.appendLine("}")
        }
        
        sb.appendLine("}")
        
        // Text content
        sb.appendLine("$TEXT_MARKER ${text.length}")
        sb.append(text)
        sb.appendLine("}")
        
        return sb.toString().toByteArray(Charsets.UTF_8)
    }
    
    /**
     * Clear the notecard cache.
     */
    fun clearCache() {
        notecardCache.clear()
    }
    
    /**
     * Get cached notecard.
     */
    fun getCachedNotecard(assetId: UUID): Notecard? = notecardCache[assetId]
    
    /**
     * Fetch a notecard by asset ID (suspend function).
     */
    suspend fun fetchNotecard(assetId: UUID): NotecardData? {
        return withContext(Dispatchers.IO) {
            // Check cache
            notecardCache[assetId]?.let { cached ->
                return@withContext NotecardData(
                    assetId = cached.assetId,
                    text = cached.text,
                    embeddedItems = cached.embeddedItems.map { item ->
                        com.linkpoint.inventory.notecard.EmbeddedItem(
                            itemId = item.itemId,
                            assetId = item.assetId,
                            assetType = item.assetType,
                            name = item.name,
                            description = item.description
                        )
                    }
                )
            }
            
            // Fetch via transfer
            val transfer = transferManager ?: return@withContext null
            val data = transfer.fetchAsset(assetId, AssetType.NOTECARD.code) ?: return@withContext null
            
            try {
                val notecard = parseNotecard(assetId, data)
                notecardCache[assetId] = notecard
                
                NotecardData(
                    assetId = notecard.assetId,
                    text = notecard.text,
                    embeddedItems = notecard.embeddedItems.map { item ->
                        com.linkpoint.inventory.notecard.EmbeddedItem(
                            itemId = item.itemId,
                            assetId = item.assetId,
                            assetType = item.assetType,
                            name = item.name,
                            description = item.description
                        )
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse notecard $assetId", e)
                null
            }
        }
    }
    
    /**
     * Save a notecard (update text content).
     * Note: Full implementation requires UpdateNotecardAgentInventory capability.
     */
    suspend fun saveNotecard(itemId: UUID, newText: String): Boolean {
        return saveNotecard(itemId = itemId, newText = newText, taskId = null)
    }

    /**
     * Save a notecard with optional task context.
     */
    suspend fun saveNotecard(itemId: UUID, newText: String, taskId: UUID?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val notecardData = createNotecardData(newText)
                val isTaskInventory = (taskId != null || objectId != null)
                val capability = if (isTaskInventory) {
                    CapabilityManager.CAP_UPDATE_NOTECARD_TASK
                } else {
                    CapabilityManager.CAP_UPDATE_NOTECARD_AGENT
                }
                val request = LLSDMap().apply {
                    this["item_id"] = LLSDUUID(itemId)
                    taskId?.let { this["task_id"] = LLSDUUID(it) }
                }

                val capability = if (taskId != null) {
                    CapabilityManager.CAP_UPDATE_NOTECARD_TASK
                } else {
                    CapabilityManager.CAP_UPDATE_NOTECARD_AGENT
                }
                val capResponse = capabilityManager.request(
                    capability,
                    request
                ) as? LLSDMap

                if (capResponse == null) {
                    Log.w(TAG, "Notecard save failed: $capability returned no payload")
                    return@withContext false
                }

                val uploaderUrl = capResponse.getString("uploader")?.takeIf { it.isNotBlank() }
                if (uploaderUrl == null) {
                    Log.w(TAG, "Notecard save failed: cap response missing uploader URL")
                    return@withContext false
                }

                val normalizedUploader = if (uploaderUrl.startsWith("http://", ignoreCase = true)) {
                    uploaderUrl.replaceFirst("http://", "https://", ignoreCase = true)
                } else uploaderUrl

                val uploadRequest = Request.Builder()
                    .url(normalizedUploader)
                    .addHeader("Accept", "application/llsd+xml")
                    .post(notecardData.toRequestBody("application/vnd.ll.notecard".toMediaType()))
                    .build()

                httpClient.newCall(uploadRequest).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.w(TAG, "Notecard upload failed for $itemId: HTTP ${response.code}")
                        return@withContext false
                    }

                    val responseBody = response.body?.bytes()
                    if (responseBody == null || responseBody.isEmpty()) {
                        Log.w(TAG, "Notecard upload returned empty response for $itemId")
                        return@withContext false
                    }

                    val uploadResponse = com.linkpoint.protocol.llsd.LLSDParser.parseAuto(
                        responseBody,
                        response.header("Content-Type")
                    ) as? LLSDMap

                    val state = uploadResponse?.getString("state")
                    val completed = state.equals("complete", ignoreCase = true)
                    if (!completed) {
                        val errors = uploadResponse?.getString("errors")
                        Log.w(TAG, "Notecard upload incomplete for $itemId: state=$state errors=$errors")
                        return@withContext false
                    }
                }

                Log.i(TAG, "Notecard $itemId saved successfully (${newText.length} chars, taskInventory=$isTaskInventory)")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save notecard $itemId", e)
                false
            }
        }
    }

    /**
     * Copy an embedded inventory item from notecard contents into destination folder.
     */
    suspend fun copyInventoryFromNotecard(
        notecardItemId: UUID,
        objectId: UUID?,
        destinationFolderId: UUID,
        embeddedItemId: UUID
    ): Boolean = withContext(Dispatchers.IO) {
        val request = LLSDMap().apply {
            this["notecard-id"] = LLSDUUID(notecardItemId)
            this["folder-id"] = LLSDUUID(destinationFolderId)
            this["item-id"] = LLSDUUID(embeddedItemId)
            objectId?.let { this["object-id"] = LLSDUUID(it) }
        }
        capabilityManager.request(CapabilityManager.CAP_COPY_INVENTORY_FROM_NOTECARD, request) != null
    }

    /**
     * Move an inventory item produced from notecard interactions (trash/move endpoint).
     */
    suspend fun moveInventoryItem(itemId: UUID, destinationFolderId: UUID): Boolean = withContext(Dispatchers.IO) {
        val request = LLSDMap().apply {
            this["items"] = com.linkpoint.protocol.llsd.LLSDArray().apply {
                add(LLSDMap().apply {
                    this["item_id"] = LLSDUUID(itemId)
                    this["folder_id"] = LLSDUUID(destinationFolderId)
                })
            }
        }
        capabilityManager.request(CapabilityManager.CAP_MOVE_INVENTORY_ITEM, request) != null
    }
    
    /**
     * Shutdown the manager.
     */
    fun shutdown() {
        scope.cancel()
        notecardCache.clear()
        pendingLoads.clear()
    }
}

/**
 * Represents a parsed notecard.
 */
data class Notecard(
    val assetId: UUID,
    val text: String,
    val embeddedItems: List<EmbeddedItem>
) {
    /**
     * Get text with embedded item markers replaced with item names.
     */
    fun getDisplayText(): String {
        // Embedded items are referenced in text by index like: {0}, {1}, etc.
        var result = text
        embeddedItems.forEachIndexed { index, item ->
            result = result.replace("{$index}", "[${item.name}]")
        }
        return result
    }
}

/**
 * Represents an item embedded in a notecard.
 */
data class EmbeddedItem(
    val itemId: UUID,
    val assetId: UUID,
    val assetType: AssetType,
    val name: String,
    val description: String
) {
    /**
     * Get the type as an integer.
     */
    val type: Int get() = assetType.code
}

/**
 * Notecard data for UI layer.
 */
data class NotecardData(
    val assetId: UUID,
    val text: String,
    val embeddedItems: List<EmbeddedItem>
)

/**
 * Callback for notecard loading.
 */
interface NotecardCallback {
    fun onNotecardLoaded(notecard: Notecard)
    fun onNotecardError(error: String)
}

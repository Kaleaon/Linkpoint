package com.linkpoint.inventory

import com.linkpoint.inventory.persistence.InventoryFolderDao
import com.linkpoint.inventory.persistence.InventoryFolderEntity
import com.linkpoint.inventory.persistence.InventoryFolderVersionDao
import com.linkpoint.inventory.persistence.InventoryFolderVersionEntity
import com.linkpoint.inventory.persistence.InventoryItemDao
import com.linkpoint.inventory.persistence.InventoryItemEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.UUID

class InventoryRepositoryTest {

    @Test
    fun `cold cache fetches from AIS and persists subtree`() = runBlocking {
        val fixture = subtreeFixture(version = 3)
        val ais = RecordingAisClient(fixture)
        val repo = createRepository(aisClient = ais)

        val result = repo.getSubtree(fixture.folder.folderId, knownFolderVersion = 3)

        assertEquals(1, ais.subtreeRequests)
        assertEquals(3, result.folder.version)
        assertEquals(1, result.items.size)
    }

    @Test
    fun `warm cache with matching version avoids network`() = runBlocking {
        val fixture = subtreeFixture(version = 5)
        val ais = RecordingAisClient(fixture)
        val repo = createRepository(aisClient = ais)
        repo.getSubtree(fixture.folder.folderId, knownFolderVersion = 5)

        ais.subtreeRequests = 0
        val cached = repo.getSubtree(fixture.folder.folderId, knownFolderVersion = 5)

        assertEquals(0, ais.subtreeRequests)
        assertEquals(5, cached.folder.version)
    }

    @Test
    fun `version mismatch invalidates warm cache and refetches`() = runBlocking {
        val oldFixture = subtreeFixture(version = 1)
        val newFixture = subtreeFixture(version = 2)
        val ais = RecordingAisClient(oldFixture)
        val repo = createRepository(aisClient = ais)
        repo.getSubtree(oldFixture.folder.folderId, knownFolderVersion = 1)

        ais.fixture = newFixture
        ais.subtreeRequests = 0
        val refreshed = repo.getSubtree(oldFixture.folder.folderId, knownFolderVersion = 2)

        assertEquals(1, ais.subtreeRequests)
        assertEquals(2, refreshed.folder.version)
    }

    @Test
    fun `404 from AIS falls back to UDP FetchInventoryDescendents2`() = runBlocking {
        val folderId = UUID.randomUUID()
        val fallbackFixture = subtreeFixture(folderId = folderId, version = 7)
        val ais = RecordingAisClient(fallbackFixture, throwCapabilityUnavailable = true)
        val udp = RecordingUdpSource(fallbackFixture)
        val repo = createRepository(aisClient = ais, udpSource = udp)

        val result = repo.getSubtree(folderId, knownFolderVersion = 7)

        assertEquals(1, udp.fetchCalls)
        assertEquals(7, result.folder.version)
        val cachedItem = repo.getItem(result.items.first().itemId, folderId)
        assertNotNull(cachedItem)
    }

    private fun createRepository(
        aisClient: RecordingAisClient,
        udpSource: RecordingUdpSource = RecordingUdpSource(subtreeFixture())
    ): InventoryRepository {
        return InventoryRepository(
            aisClient = aisClient,
            folderDao = InMemoryFolderDao(),
            itemDao = InMemoryItemDao(),
            folderVersionDao = InMemoryFolderVersionDao(),
            udpFallback = udpSource
        )
    }

    private fun subtreeFixture(folderId: UUID = UUID.randomUUID(), version: Int = 1): InventorySubtreeData {
        val childFolderId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        return InventorySubtreeData(
            folder = InventoryFolderData(folderId, UUID(0L, 0L), "Root", version, 9),
            folders = listOf(InventoryFolderData(childFolderId, folderId, "Clothes", 1, 5)),
            items = listOf(InventoryItemData(itemId, folderId, "Shirt", UUID.randomUUID(), 18, 5, version))
        )
    }
}

private class RecordingAisClient(
    var fixture: InventorySubtreeData,
    private val throwCapabilityUnavailable: Boolean = false
) : AisOperations {
    var subtreeRequests: Int = 0

    override suspend fun getSubtree(folderId: UUID): InventorySubtreeData {
        subtreeRequests++
        if (throwCapabilityUnavailable) throw AisCapabilityUnavailableException("404")
        return fixture
    }

    override suspend fun getItem(itemId: UUID): InventoryItemData {
        return fixture.items.first { it.itemId == itemId }
    }

    override suspend fun patchItem(itemId: UUID, patch: InventoryItemPatch) = Unit

    override suspend fun postFolder(request: CreateFolderRequest): UUID = UUID.randomUUID()

    override suspend fun deleteNode(relativePath: String) = Unit
}

private class RecordingUdpSource(private val fixture: InventorySubtreeData) : UdpInventoryDataSource {
    var fetchCalls: Int = 0
    override suspend fun fetchInventoryDescendents2(folderId: UUID): InventorySubtreeData {
        fetchCalls++
        return fixture
    }
}

private class InMemoryFolderDao : InventoryFolderDao {
    private val folders = linkedMapOf<String, InventoryFolderEntity>()

    override suspend fun getFolder(folderId: String): InventoryFolderEntity? = folders[folderId]
    override suspend fun getFoldersByParent(parentId: String): List<InventoryFolderEntity> =
        folders.values.filter { it.parentId == parentId }

    override suspend fun upsert(entity: InventoryFolderEntity) {
        folders[entity.folderId] = entity
    }

    override suspend fun upsertAll(entities: List<InventoryFolderEntity>) {
        entities.forEach { upsert(it) }
    }

    override suspend fun delete(folderId: String) {
        folders.remove(folderId)
    }

    override suspend fun deleteChildrenOfParent(parentId: String) {
        val keys = folders.values.filter { it.parentId == parentId }.map { it.folderId }
        keys.forEach { folders.remove(it) }
    }
}

private class InMemoryItemDao : InventoryItemDao {
    private val items = linkedMapOf<String, InventoryItemEntity>()

    override suspend fun getItem(itemId: String): InventoryItemEntity? = items[itemId]
    override suspend fun getItemsByParent(parentId: String): List<InventoryItemEntity> =
        items.values.filter { it.parentId == parentId }

    override suspend fun upsert(entity: InventoryItemEntity) {
        items[entity.itemId] = entity
    }

    override suspend fun upsertAll(entities: List<InventoryItemEntity>) {
        entities.forEach { upsert(it) }
    }

    override suspend fun delete(itemId: String) {
        items.remove(itemId)
    }

    override suspend fun deleteByParentId(parentId: String) {
        val keys = items.values.filter { it.parentId == parentId }.map { it.itemId }
        keys.forEach { items.remove(it) }
    }
}

private class InMemoryFolderVersionDao : InventoryFolderVersionDao {
    private val versions = mutableMapOf<String, InventoryFolderVersionEntity>()

    override suspend fun getVersion(folderId: String): InventoryFolderVersionEntity? = versions[folderId]

    override suspend fun upsert(entity: InventoryFolderVersionEntity) {
        versions[entity.folderId] = entity
    }

    override suspend fun delete(folderId: String) {
        versions.remove(folderId)
    }
}

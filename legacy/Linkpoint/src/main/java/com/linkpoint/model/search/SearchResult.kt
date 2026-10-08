package com.linkpoint.model.search

import java.util.UUID

/**
 * Unified polymorphic search result interface contract.
 */
sealed interface SearchResult {
    val id: UUID
    val name: String
    val description: String
}

data class PersonResult(
    val agentId: UUID,
    val displayName: String,
    val userName: String,
    val isOnline: Boolean = false
) : SearchResult {
    override val id: UUID get() = agentId
    override val name: String get() = displayName.ifEmpty { userName }
    override val description: String get() = userName
}

data class PlaceResult(
    val parcelId: UUID,
    override val name: String,
    override val description: String = "",
    val region: String = "",
    val category: String = "",
    val traffic: Float = 0f,
    val area: Int = 0,
    val location: String = ""
) : SearchResult {
    override val id: UUID get() = parcelId
    val slurl: String get() = if (region.isNotEmpty()) "secondlife://$region/$location" else ""
}

data class GroupResult(
    val groupId: UUID,
    override val name: String,
    val charter: String = "",
    val memberCount: Int = 0,
    val isOpen: Boolean = true,
    val insigniaId: UUID? = null
) : SearchResult {
    override val id: UUID get() = groupId
    override val description: String get() = charter
}

data class EventResult(
    val eventId: Int,
    override val name: String,
    override val description: String = "",
    val category: String = "",
    val dateUtc: Long = 0L,
    val duration: Int = 0,
    val region: String = "",
    val coverCharge: Int = 0
) : SearchResult {
    override val id: UUID get() = UUID(0L, eventId.toLong())
    val location: String get() = region
    val dateTime: String get() = if (dateUtc > 0) dateUtc.toString() else ""
}

data class LandResult(
    val parcelId: UUID,
    override val name: String,
    override val description: String = "",
    val region: String = "",
    val area: Int = 0,
    val salePrice: Int = 0,
    val pricePerMeter: Float = 0f,
    val saleType: LandSaleType = LandSaleType.ALL,
    val isAuction: Boolean = false
) : SearchResult {
    override val id: UUID get() = parcelId
}

data class DestinationResult(
    override val name: String,
    override val description: String = "",
    val category: String = "",
    val region: String = "",
    val location: String = "",
    val imageUrl: String = "",
    val rating: Float = 0f,
    val destinationId: UUID = UUID.nameUUIDFromBytes(name.toByteArray())
) : SearchResult {
    override val id: UUID get() = destinationId
}

enum class LandSaleType {
    ALL, FOR_SALE, FOR_AUCTION, MAINLAND, ESTATE
}

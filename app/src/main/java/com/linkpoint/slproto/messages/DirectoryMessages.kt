package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.UUID

/**
 * Directory find query message (0xFFFF001F)
 */
class DirFindQueryMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var queryId: UUID = UUID.randomUUID()
    var queryText: String = ""
    var queryFlags: Int = 0
    var queryStart: Int = 0

    private val charset: Charset = Charsets.UTF_8

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, queryId)
        packVariable(buffer, queryText.toByteArray(charset), 1)
        packInt(buffer, queryFlags)
        packInt(buffer, queryStart)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        queryText = String(unpackVariable(buffer, 1), charset)
        queryFlags = unpackInt(buffer)
        queryStart = unpackInt(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DIR_FIND_QUERY

    override fun getMessageName(): String = "DirFindQuery"
}

/**
 * Directory find query backend message (0xFFFF0020)
 */
class DirFindQueryBackendMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var queryId: UUID = UUID.randomUUID()
    var queryText: String = ""
    var queryFlags: Int = 0
    var queryStart: Int = 0
    var estateId: Int = 0
    var godlike: Boolean = false

    private val charset: Charset = Charsets.UTF_8

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, queryId)
        packVariable(buffer, queryText.toByteArray(charset), 1)
        packInt(buffer, queryFlags)
        packInt(buffer, queryStart)
        packInt(buffer, estateId)
        packBoolean(buffer, godlike)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        queryText = String(unpackVariable(buffer, 1), charset)
        queryFlags = unpackInt(buffer)
        queryStart = unpackInt(buffer)
        estateId = unpackInt(buffer)
        godlike = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DIR_FIND_QUERY_BACKEND

    override fun getMessageName(): String = "DirFindQueryBackend"
}

/**
 * Directory places query message (0xFFFF0021)
 */
class DirPlacesQueryMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var queryId: UUID = UUID.randomUUID()
    var queryText: String = ""
    var queryFlags: Int = 0
    var category: Int = 0
    var simName: String = ""
    var queryStart: Int = 0

    private val charset: Charset = Charsets.UTF_8

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, queryId)
        packVariable(buffer, queryText.toByteArray(charset), 1)
        packInt(buffer, queryFlags)
        require(category in 0..0xFF) { "category out of range ($category)" }
        packByte(buffer, category)
        packVariable(buffer, simName.toByteArray(charset), 1)
        packInt(buffer, queryStart)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        queryText = String(unpackVariable(buffer, 1), charset)
        queryFlags = unpackInt(buffer)
        category = unpackByte(buffer)
        simName = String(unpackVariable(buffer, 1), charset)
        queryStart = unpackInt(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DIR_PLACES_QUERY

    override fun getMessageName(): String = "DirPlacesQuery"
}

/**
 * Directory places query backend (0xFFFF0022)
 */
class DirPlacesQueryBackendMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var queryId: UUID = UUID.randomUUID()
    var queryText: String = ""
    var queryFlags: Int = 0
    var category: Int = 0
    var simName: String = ""
    var estateId: Int = 0
    var godlike: Boolean = false
    var queryStart: Int = 0

    private val charset: Charset = Charsets.UTF_8

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, queryId)
        packVariable(buffer, queryText.toByteArray(charset), 1)
        packInt(buffer, queryFlags)
        require(category in 0..0xFF) { "category out of range ($category)" }
        packByte(buffer, category)
        packVariable(buffer, simName.toByteArray(charset), 1)
        packInt(buffer, estateId)
        packBoolean(buffer, godlike)
        packInt(buffer, queryStart)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        queryText = String(unpackVariable(buffer, 1), charset)
        queryFlags = unpackInt(buffer)
        category = unpackByte(buffer)
        simName = String(unpackVariable(buffer, 1), charset)
        estateId = unpackInt(buffer)
        godlike = unpackBoolean(buffer)
        queryStart = unpackInt(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DIR_PLACES_QUERY_BACKEND

    override fun getMessageName(): String = "DirPlacesQueryBackend"
}

/**
 * Directory places reply (0xFFFF0023)
 */
class DirPlacesReplyMessage : SLMessage() {
    data class PlaceEntry(
        var parcelId: UUID = UUID.randomUUID(),
        var name: String = "",
        var forSale: Boolean = false,
        var auction: Boolean = false,
        var dwell: Float = 0f,
    )

    var agentId: UUID = UUID.randomUUID()
    var queryId: UUID = UUID.randomUUID()
    val places: MutableList<PlaceEntry> = mutableListOf()
    val statusCodes: MutableList<Int> = mutableListOf()

    private val charset: Charset = Charsets.UTF_8

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, queryId)
        require(places.size <= 0xFF) { "Place list too large (${places.size})" }
        packByte(buffer, places.size)
        places.forEach { entry ->
            packUUID(buffer, entry.parcelId)
            packVariable(buffer, entry.name.toByteArray(charset), 1)
            packBoolean(buffer, entry.forSale)
            packBoolean(buffer, entry.auction)
            packFloat(buffer, entry.dwell)
        }
        require(statusCodes.size <= 0xFF) { "Status list too large (${statusCodes.size})" }
        packByte(buffer, statusCodes.size)
        statusCodes.forEach { packInt(buffer, it) }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        val placeCount = unpackByte(buffer)
        places.clear()
        repeat(placeCount) {
            val parcelId = unpackUUID(buffer)
            val name = String(unpackVariable(buffer, 1), charset)
            val forSale = unpackBoolean(buffer)
            val auction = unpackBoolean(buffer)
            val dwell = unpackFloat(buffer)
            places += PlaceEntry(parcelId, name, forSale, auction, dwell)
        }
        val statusCount = unpackByte(buffer)
        statusCodes.clear()
        repeat(statusCount) {
            statusCodes += unpackInt(buffer)
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DIR_PLACES_REPLY

    override fun getMessageName(): String = "DirPlacesReply"
}

/**
 * Directory people reply (0xFFFF0024)
 */
class DirPeopleReplyMessage : SLMessage() {
    data class PersonEntry(
        var agentId: UUID = UUID.randomUUID(),
        var firstName: String = "",
        var lastName: String = "",
        var group: String = "",
        var online: Boolean = false,
        var reputation: Int = 0,
    )

    var agentId: UUID = UUID.randomUUID()
    var queryId: UUID = UUID.randomUUID()
    val people: MutableList<PersonEntry> = mutableListOf()

    private val charset: Charset = Charsets.UTF_8

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, queryId)
        require(people.size <= 0xFF) { "People list too large (${people.size})" }
        packByte(buffer, people.size)
        people.forEach { entry ->
            packUUID(buffer, entry.agentId)
            packVariable(buffer, entry.firstName.toByteArray(charset), 1)
            packVariable(buffer, entry.lastName.toByteArray(charset), 1)
            packVariable(buffer, entry.group.toByteArray(charset), 1)
            packBoolean(buffer, entry.online)
            packInt(buffer, entry.reputation)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        val count = unpackByte(buffer)
        people.clear()
        repeat(count) {
            val personId = unpackUUID(buffer)
            val first = String(unpackVariable(buffer, 1), charset)
            val last = String(unpackVariable(buffer, 1), charset)
            val group = String(unpackVariable(buffer, 1), charset)
            val online = unpackBoolean(buffer)
            val reputation = unpackInt(buffer)
            people += PersonEntry(personId, first, last, group, online, reputation)
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DIR_PEOPLE_REPLY

    override fun getMessageName(): String = "DirPeopleReply"
}

/**
 * Directory events reply (0xFFFF0025)
 */
class DirEventsReplyMessage : SLMessage() {
    data class EventEntry(
        var ownerId: UUID = UUID.randomUUID(),
        var name: String = "",
        var eventId: Int = 0,
        var date: String = "",
        var unixTime: Int = 0,
        var eventFlags: Int = 0,
    )

    var agentId: UUID = UUID.randomUUID()
    var queryId: UUID = UUID.randomUUID()
    val events: MutableList<EventEntry> = mutableListOf()
    val statusCodes: MutableList<Int> = mutableListOf()

    private val charset: Charset = Charsets.UTF_8

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, queryId)
        require(events.size <= 0xFF) { "Event list too large (${events.size})" }
        packByte(buffer, events.size)
        events.forEach { entry ->
            packUUID(buffer, entry.ownerId)
            packVariable(buffer, entry.name.toByteArray(charset), 1)
            packInt(buffer, entry.eventId)
            packVariable(buffer, entry.date.toByteArray(charset), 1)
            packInt(buffer, entry.unixTime)
            packInt(buffer, entry.eventFlags)
        }
        require(statusCodes.size <= 0xFF) { "Status list too large (${statusCodes.size})" }
        packByte(buffer, statusCodes.size)
        statusCodes.forEach { packInt(buffer, it) }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        val eventCount = unpackByte(buffer)
        events.clear()
        repeat(eventCount) {
            val owner = unpackUUID(buffer)
            val name = String(unpackVariable(buffer, 1), charset)
            val eventId = unpackInt(buffer)
            val date = String(unpackVariable(buffer, 1), charset)
            val unixTime = unpackInt(buffer)
            val flags = unpackInt(buffer)
            events += EventEntry(owner, name, eventId, date, unixTime, flags)
        }
        val statusCount = unpackByte(buffer)
        statusCodes.clear()
        repeat(statusCount) {
            statusCodes += unpackInt(buffer)
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DIR_EVENTS_REPLY

    override fun getMessageName(): String = "DirEventsReply"
}

/**
 * Directory groups reply (0xFFFF0026)
 */
class DirGroupsReplyMessage : SLMessage() {
    data class GroupEntry(
        var groupId: UUID = UUID.randomUUID(),
        var groupName: String = "",
        var members: Int = 0,
        var searchOrder: Float = 0f,
    )

    var agentId: UUID = UUID.randomUUID()
    var queryId: UUID = UUID.randomUUID()
    val groups: MutableList<GroupEntry> = mutableListOf()

    private val charset: Charset = Charsets.UTF_8

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, queryId)
        require(groups.size <= 0xFF) { "Group list too large (${groups.size})" }
        packByte(buffer, groups.size)
        groups.forEach { entry ->
            packUUID(buffer, entry.groupId)
            packVariable(buffer, entry.groupName.toByteArray(charset), 1)
            packInt(buffer, entry.members)
            packFloat(buffer, entry.searchOrder)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        val count = unpackByte(buffer)
        groups.clear()
        repeat(count) {
            val id = unpackUUID(buffer)
            val name = String(unpackVariable(buffer, 1), charset)
            val members = unpackInt(buffer)
            val searchOrder = unpackFloat(buffer)
            groups += GroupEntry(id, name, members, searchOrder)
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DIR_GROUPS_REPLY

    override fun getMessageName(): String = "DirGroupsReply"
}

/**
 * Directory classified query (0xFFFF0027)
 */
class DirClassifiedQueryMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var queryId: UUID = UUID.randomUUID()
    var queryText: String = ""
    var queryFlags: Int = 0
    var category: Int = 0
    var queryStart: Int = 0

    private val charset: Charset = Charsets.UTF_8

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, queryId)
        packVariable(buffer, queryText.toByteArray(charset), 1)
        packInt(buffer, queryFlags)
        packInt(buffer, category)
        packInt(buffer, queryStart)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        queryText = String(unpackVariable(buffer, 1), charset)
        queryFlags = unpackInt(buffer)
        category = unpackInt(buffer)
        queryStart = unpackInt(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DIR_CLASSIFIED_QUERY

    override fun getMessageName(): String = "DirClassifiedQuery"
}

/**
 * Directory classified query backend (0xFFFF0028)
 */
class DirClassifiedQueryBackendMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var queryId: UUID = UUID.randomUUID()
    var queryText: String = ""
    var queryFlags: Int = 0
    var category: Int = 0
    var estateId: Int = 0
    var godlike: Boolean = false
    var queryStart: Int = 0

    private val charset: Charset = Charsets.UTF_8

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, queryId)
        packVariable(buffer, queryText.toByteArray(charset), 1)
        packInt(buffer, queryFlags)
        packInt(buffer, category)
        packInt(buffer, estateId)
        packBoolean(buffer, godlike)
        packInt(buffer, queryStart)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        queryText = String(unpackVariable(buffer, 1), charset)
        queryFlags = unpackInt(buffer)
        category = unpackInt(buffer)
        estateId = unpackInt(buffer)
        godlike = unpackBoolean(buffer)
        queryStart = unpackInt(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DIR_CLASSIFIED_QUERY_BACKEND

    override fun getMessageName(): String = "DirClassifiedQueryBackend"
}

/**
 * Directory classified reply (0xFFFF0029)
 */
class DirClassifiedReplyMessage : SLMessage() {
    data class ClassifiedEntry(
        var classifiedId: UUID = UUID.randomUUID(),
        var name: String = "",
        var classifiedFlags: Int = 0,
        var creationDateSec: Int = 0,
        var expirationDateSec: Int = 0,
        var priceForListing: Int = 0,
    )

    var agentId: UUID = UUID.randomUUID()
    var queryId: UUID = UUID.randomUUID()
    val classifieds: MutableList<ClassifiedEntry> = mutableListOf()
    val statusCodes: MutableList<Int> = mutableListOf()

    private val charset: Charset = Charsets.UTF_8

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, queryId)
        require(classifieds.size <= 0xFF) { "Classified list too large (${classifieds.size})" }
        packByte(buffer, classifieds.size)
        classifieds.forEach { entry ->
            packUUID(buffer, entry.classifiedId)
            packVariable(buffer, entry.name.toByteArray(charset), 1)
            packByte(buffer, entry.classifiedFlags and 0xFF)
            packInt(buffer, entry.creationDateSec)
            packInt(buffer, entry.expirationDateSec)
            packInt(buffer, entry.priceForListing)
        }
        require(statusCodes.size <= 0xFF) { "Status list too large (${statusCodes.size})" }
        packByte(buffer, statusCodes.size)
        statusCodes.forEach { packInt(buffer, it) }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        val classifiedCount = unpackByte(buffer)
        classifieds.clear()
        repeat(classifiedCount) {
            val id = unpackUUID(buffer)
            val name = String(unpackVariable(buffer, 1), charset)
            val flags = unpackByte(buffer)
            val creation = unpackInt(buffer)
            val expiration = unpackInt(buffer)
            val price = unpackInt(buffer)
            classifieds += ClassifiedEntry(id, name, flags, creation, expiration, price)
        }
        val statusCount = unpackByte(buffer)
        statusCodes.clear()
        repeat(statusCount) {
            statusCodes += unpackInt(buffer)
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DIR_CLASSIFIED_REPLY

    override fun getMessageName(): String = "DirClassifiedReply"
}

/**
 * Directory land query (0xFFFF0030)
 */
class DirLandQueryMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var queryId: UUID = UUID.randomUUID()
    var queryFlags: Int = 0
    var searchType: Int = 0
    var price: Int = 0
    var area: Int = 0
    var queryStart: Int = 0

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, queryId)
        packInt(buffer, queryFlags)
        packInt(buffer, searchType)
        packInt(buffer, price)
        packInt(buffer, area)
        packInt(buffer, queryStart)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        queryFlags = unpackInt(buffer)
        searchType = unpackInt(buffer)
        price = unpackInt(buffer)
        area = unpackInt(buffer)
        queryStart = unpackInt(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DIR_LAND_QUERY

    override fun getMessageName(): String = "DirLandQuery"
}

/**
 * Directory land query backend (0xFFFF0031)
 */
class DirLandQueryBackendMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var queryId: UUID = UUID.randomUUID()
    var queryFlags: Int = 0
    var searchType: Int = 0
    var price: Int = 0
    var area: Int = 0
    var queryStart: Int = 0
    var estateId: Int = 0
    var godlike: Boolean = false

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, queryId)
        packInt(buffer, queryFlags)
        packInt(buffer, searchType)
        packInt(buffer, price)
        packInt(buffer, area)
        packInt(buffer, queryStart)
        packInt(buffer, estateId)
        packBoolean(buffer, godlike)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        queryFlags = unpackInt(buffer)
        searchType = unpackInt(buffer)
        price = unpackInt(buffer)
        area = unpackInt(buffer)
        queryStart = unpackInt(buffer)
        estateId = unpackInt(buffer)
        godlike = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DIR_LAND_QUERY_BACKEND

    override fun getMessageName(): String = "DirLandQueryBackend"
}

/**
 * Directory land reply (0xFFFF0032)
 */
class DirLandReplyMessage : SLMessage() {
    data class LandEntry(
        var parcelId: UUID = UUID.randomUUID(),
        var name: String = "",
        var auction: Boolean = false,
        var forSale: Boolean = false,
        var salePrice: Int = 0,
        var actualArea: Int = 0,
    )

    var agentId: UUID = UUID.randomUUID()
    var queryId: UUID = UUID.randomUUID()
    val parcels: MutableList<LandEntry> = mutableListOf()

    private val charset: Charset = Charsets.UTF_8

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, queryId)
        require(parcels.size <= 0xFF) { "Parcel list too large (${parcels.size})" }
        packByte(buffer, parcels.size)
        parcels.forEach { entry ->
            packUUID(buffer, entry.parcelId)
            packVariable(buffer, entry.name.toByteArray(charset), 1)
            packBoolean(buffer, entry.auction)
            packBoolean(buffer, entry.forSale)
            packInt(buffer, entry.salePrice)
            packInt(buffer, entry.actualArea)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        val count = unpackByte(buffer)
        parcels.clear()
        repeat(count) {
            val id = unpackUUID(buffer)
            val name = String(unpackVariable(buffer, 1), charset)
            val auction = unpackBoolean(buffer)
            val forSale = unpackBoolean(buffer)
            val price = unpackInt(buffer)
            val area = unpackInt(buffer)
            parcels += LandEntry(id, name, auction, forSale, price, area)
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DIR_LAND_REPLY

    override fun getMessageName(): String = "DirLandReply"
}

/**
 * Directory popular query (0xFFFF0033)
 */
class DirPopularQueryMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var queryId: UUID = UUID.randomUUID()
    var queryFlags: Int = 0

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, queryId)
        packInt(buffer, queryFlags)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        queryFlags = unpackInt(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DIR_POPULAR_QUERY

    override fun getMessageName(): String = "DirPopularQuery"
}

/**
 * Directory popular query backend (0xFFFF0034)
 */
class DirPopularQueryBackendMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var queryId: UUID = UUID.randomUUID()
    var queryFlags: Int = 0
    var estateId: Int = 0
    var godlike: Boolean = false

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, queryId)
        packInt(buffer, queryFlags)
        packInt(buffer, estateId)
        packBoolean(buffer, godlike)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        queryFlags = unpackInt(buffer)
        estateId = unpackInt(buffer)
        godlike = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DIR_POPULAR_QUERY_BACKEND

    override fun getMessageName(): String = "DirPopularQueryBackend"
}

/**
 * Directory popular reply (0xFFFF0035)
 */
class DirPopularReplyMessage : SLMessage() {
    data class PopularEntry(
        var parcelId: UUID = UUID.randomUUID(),
        var name: String = "",
        var dwell: Float = 0f,
    )

    var agentId: UUID = UUID.randomUUID()
    var queryId: UUID = UUID.randomUUID()
    val entries: MutableList<PopularEntry> = mutableListOf()

    private val charset: Charset = Charsets.UTF_8

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, queryId)
        require(entries.size <= 0xFF) { "Popular list too large (${entries.size})" }
        packByte(buffer, entries.size)
        entries.forEach { entry ->
            packUUID(buffer, entry.parcelId)
            packVariable(buffer, entry.name.toByteArray(charset), 1)
            packFloat(buffer, entry.dwell)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        queryId = unpackUUID(buffer)
        val count = unpackByte(buffer)
        entries.clear()
        repeat(count) {
            val id = unpackUUID(buffer)
            val name = String(unpackVariable(buffer, 1), charset)
            val dwell = unpackFloat(buffer)
            entries += PopularEntry(id, name, dwell)
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DIR_POPULAR_REPLY

    override fun getMessageName(): String = "DirPopularReply"
}

package com.linkpoint.linden.llmessage

import com.linkpoint.linden.llcommon.LLUUID
import com.linkpoint.linden.llcommon.LLSD

typealias ExperienceGetFn = (LLSD) -> Unit
typealias CapabilityQuery = (String) -> String

object ExperienceCache {
    const val NAME: String = "name"
    const val EXPERIENCE_ID: String = "public_id"
    const val AGENT_ID: String = "agent_id"
    const val GROUP_ID: String = "group_id"
    const val PROPERTIES: String = "properties"
    const val EXPIRES: String = "expiration"
    const val DESCRIPTION: String = "description"
    const val QUOTA: String = "quota"
    const val MATURITY: String = "maturity"
    const val METADATA: String = "extended_metadata"
    const val SLURL: String = "slurl"
    const val MISSING: String = "DoesNotExist"
    const val PRIVATE_KEY: String = "private_id"

    const val PROPERTY_INVALID: Int = 1 shl 0
    const val PROPERTY_PRIVILEGED: Int = 1 shl 3
    const val PROPERTY_GRID: Int = 1 shl 4
    const val PROPERTY_PRIVATE: Int = 1 shl 5
    const val PROPERTY_DISABLED: Int = 1 shl 6
    const val PROPERTY_SUSPENDED: Int = 1 shl 7

    const val DEFAULT_EXPIRATION: Double = 600.0
    const val DEFAULT_QUOTA: Int = 128
    const val SEARCH_PAGE_SIZE: Int = 30

    private val cache: MutableMap<LLUUID, LLSD> = mutableMapOf()
    private val pendingQueue: MutableMap<LLUUID, Double> = mutableMapOf()
    private val requestQueue: MutableSet<LLUUID> = mutableSetOf()
    private val callbacks: MutableMap<LLUUID, MutableList<ExperienceGetFn>> = mutableMapOf()

    private var capabilityQuery: CapabilityQuery? = null
    private var currentGridId: String = ""
    private var isInOpenSim: Boolean = false

    fun setCapabilityQuery(fn: CapabilityQuery) { capabilityQuery = fn }

    fun setCurrentGrid(gridId: String, openSim: Boolean) {
        currentGridId = gridId
        isInOpenSim = openSim
    }

    fun get(experienceId: LLUUID): LLSD? = cache[experienceId]

    fun get(experienceId: LLUUID, callback: ExperienceGetFn) {
        val cached = cache[experienceId]
        if (cached != null) {
            callback(cached)
            return
        }
        callbacks.getOrPut(experienceId) { mutableListOf() }.add(callback)
        fetch(experienceId, refresh = false)
    }

    fun insert(experience: LLSD) {
        val id = LLUUID.fromString(experience[EXPERIENCE_ID].asString()) ?: return
        val now = System.currentTimeMillis() / 1000.0
        val expiration = if (experience[EXPIRES].asReal() == 0.0) now + DEFAULT_EXPIRATION
                         else experience[EXPIRES].asReal()
        val entryMap = (experience as? LLSD.LLSDMap)?.value?.toMutableMap() ?: mutableMapOf()
        entryMap[EXPIRES] = LLSD.of(expiration)
        val entry = LLSD.ofMap(entryMap)
        cache[id] = entry
        pendingQueue.remove(id)
        requestQueue.remove(id)
        callbacks.remove(id)?.forEach { it(entry) }
    }

    fun fetch(experienceId: LLUUID, refresh: Boolean = false): Boolean {
        if (!refresh && cache.containsKey(experienceId)) return true
        if (pendingQueue.containsKey(experienceId)) return false
        requestQueue.add(experienceId)
        return false
    }

    fun erase(experienceId: LLUUID) {
        cache.remove(experienceId)
        pendingQueue.remove(experienceId)
    }

    fun isRequestPending(experienceId: LLUUID): Boolean = pendingQueue.containsKey(experienceId)

    fun addPermission(experienceId: LLUUID, agentId: LLUUID) {
        val existing = (cache[experienceId] as? LLSD.LLSDMap)?.value?.toMutableMap() ?: mutableMapOf()
        existing[AGENT_ID] = LLSD.of(agentId.toString())
        cache[experienceId] = LLSD.ofMap(existing)
    }

    fun eraseExpired() {
        val now = System.currentTimeMillis() / 1000.0
        val expired = cache.entries.filter { it.value[EXPIRES].asReal() < now }.map { it.key }
        expired.forEach { cache.remove(it) }
    }

    fun getExperienceId(privateKey: LLUUID, nullIfNotFound: Boolean = false): LLUUID {
        val found = cache.values.firstOrNull {
            it[PRIVATE_KEY].asString() == privateKey.toString()
        }
        return if (found != null) LLUUID.fromString(found[EXPERIENCE_ID].asString()) ?: LLUUID.NULL
        else if (nullIfNotFound) LLUUID.NULL
        else LLUUID.NULL
    }

    fun cleanup() {
        cache.clear()
        pendingQueue.clear()
        requestQueue.clear()
        callbacks.clear()
    }
}

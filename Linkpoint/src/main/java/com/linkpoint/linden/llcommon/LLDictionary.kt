package com.linkpoint.linden.llcommon

abstract class LLDictionary<Index : Enum<Index>, Entry : Any> {
    private val byIndex: MutableMap<Index, Entry> = linkedMapOf()
    private val byName: MutableMap<String, Entry> = linkedMapOf()

    protected fun addEntry(index: Index, name: String, entry: Entry) {
        byIndex[index] = entry
        byName[name] = entry
    }

    fun lookup(index: Index): Entry? = byIndex[index]
    fun lookup(name: String): Entry? = byName[name]
    fun contains(index: Index): Boolean = index in byIndex
    fun contains(name: String): Boolean = name in byName
    fun size(): Int = byIndex.size
    fun entries(): Map<Index, Entry> = byIndex.toMap()
    fun names(): Set<String> = byName.keys.toSet()
}

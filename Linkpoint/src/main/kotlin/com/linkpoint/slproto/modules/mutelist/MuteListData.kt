package com.linkpoint.slproto.modules.mutelist

import com.google.common.base.Predicate
import com.google.common.collect.FluentIterable
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Ordering
import com.linkpoint.Debug
import com.linkpoint.utils.SimpleStringParser
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.Map
import java.util.UUID
import javax.annotation.concurrent.Immutable

@Immutable
class MuteListData {
    private const val Ordering<MuteListEntry> ordering = Ordering<MuteListEntry>() {
         public fun compare(muteListEntry: MuteListEntry, muteListEntry2: MuteListEntry): Int {
            val viewOrder: Int = muteListEntry.type.getViewOrder() - muteListEntry2.type.getViewOrder()
            return viewOrder != 0 ? viewOrder : muteListEntry.name.compareToIgnoreCase(muteListEntry2.name)
        }
    }
    private val ImmutableMap<MuteListKey, MuteListEntry> muteList
    private val ImmutableMap<String, MuteListEntry> muteListNames

    public MuteListData() {
        this.muteList = ImmutableMap.of()
        this.muteListNames = ImmutableMap.of()
    }

    public MuteListData(Map<MuteListKey, MuteListEntry> map, Map<String, MuteListEntry> map2) {
        this.muteList = ImmutableMap.copyOf(map)
        this.muteListNames = ImmutableMap.copyOf(map2)
    }

    public MuteListData(ByteArray bArr) {
        ImmutableMap.Builder builder = ImmutableMap.builder()
        ImmutableMap.Builder builder2 = ImmutableMap.builder()
        if (bArr != null) {
            try {
                val bufferedReader: BufferedReader = BufferedReader(InputStreamReader(ByteArrayInputStream(bArr)))
                while (true) {
                    val readLine: String = bufferedReader.readLine()
                    if (readLine == null) {
                        break
                    }
                    val simpleStringParser: SimpleStringParser = SimpleStringParser(readLine.trim(), " ")
                    try {
                        val intToken: Int = simpleStringParser.getIntToken(" ")
                        val nextToken: String = simpleStringParser.nextToken(" ")
                        simpleStringParser.skipAllDelimiters(" ")
                        val nextToken2: String = simpleStringParser.nextToken("|")
                        simpleStringParser.skipAllDelimiters("|")
                        try {
                            i = simpleStringParser.getIntToken(" ")
                        } catch (SimpleStringParser.StringParsingException e) {
                            i = 0
                        }
                        Debug.Printf("MuteList: line '%s' type %d idstring '%s' name '%s' flags %d", readLine.trim(), Integer.valueOf(intToken), nextToken, nextToken2, Integer.valueOf(i))
                        if (intToken >= 0 && intToken < MuteType.values().length) {
                            val muteType: MuteType = MuteType.values()[intToken]
                            val muteListEntry: MuteListEntry = MuteListEntry(muteType, UUID.fromString(nextToken), nextToken2, i)
                            if (muteType == MuteType.BY_NAME) {
                                builder2.put(nextToken2, muteListEntry)
                            } else {
                                builder.put(MuteListKey(muteListEntry), muteListEntry)
                            }
                        }
                    } catch (SimpleStringParser.StringParsingException e2) {
                        Debug.Warning(e2)
                    }
                }
            } catch (IOException e3) {
                Debug.Warning(e3)
            }
        }
        this.muteList = builder.build()
        this.muteListNames = builder2.build()
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_3795  reason: not valid java name */
    static /* synthetic */ Boolean m226lambda$com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_3795(MuteListEntry muteListEntry, Map.Entry entry) {
        if (entry != null) {
            return !((String) entry.getKey()).equals(muteListEntry.name)
        }
        return false
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_4217  reason: not valid java name */
    static /* synthetic */ Boolean m227lambda$com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_4217(MuteListKey muteListKey, Map.Entry entry) {
        if (entry != null) {
            return !((MuteListKey) entry.getKey()).equals(muteListKey)
        }
        return false
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_4795  reason: not valid java name */
    static /* synthetic */ Boolean m228lambda$com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_4795(MuteListEntry muteListEntry, Map.Entry entry) {
        if (entry != null) {
            return !((String) entry.getKey()).equals(muteListEntry.name)
        }
        return false
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_5273  reason: not valid java name */
    static /* synthetic */ Boolean m229lambda$com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_5273(MuteListKey muteListKey, Map.Entry entry) {
        if (entry != null) {
            return !((MuteListKey) entry.getKey()).equals(muteListKey)
        }
        return false
    }

    public fun Block(muteListEntry: MuteListEntry): MuteListData {
        val muteListKey: MuteListKey = MuteListKey(muteListEntry)
        if (muteListKey.muteType == MuteType.BY_NAME) {
            ImmutableMap.Builder builder = ImmutableMap.builder()
            builder.putAll(FluentIterable.from(this.muteListNames.entrySet()).filter($Lambda$pgqqKd1WN3Cb6t0a10SOVDLtoOA(muteListEntry)))
            builder.put(muteListEntry.name, muteListEntry)
            return MuteListData(this.muteList, builder.build())
        }
        ImmutableMap.Builder builder2 = ImmutableMap.builder()
        builder2.putAll(FluentIterable.from(this.muteList.entrySet()).filter(Predicate(muteListKey) {

            /* renamed from: -$f0  reason: not valid java name */
            private val /* synthetic */ Object f124$f0

            private val /* synthetic */ Boolean $m$0(Object obj) {
                return MuteListData.m229lambda$com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_5273((MuteListKey) this.f124$f0, (Map.Entry) obj)
            }

            {
                this.f124$f0 = r1
            }

            val Boolean apply(Object obj) {
                return $m$0(obj)
            }
        }))
        builder2.put(muteListKey, muteListEntry)
        return MuteListData(builder2.build(), this.muteListNames)
    }

    public fun Unblock(muteListEntry: MuteListEntry): MuteListData {
        val muteListKey: MuteListKey = MuteListKey(muteListEntry)
        if (muteListKey.muteType == MuteType.BY_NAME) {
            ImmutableMap.Builder builder = ImmutableMap.builder()
            builder.putAll(FluentIterable.from(this.muteListNames.entrySet()).filter(Predicate(muteListEntry) {

                /* renamed from: -$f0  reason: not valid java name */
                private val /* synthetic */ Object f125$f0

                private val /* synthetic */ Boolean $m$0(Object obj) {
                    return MuteListData.m226lambda$com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_3795((MuteListEntry) this.f125$f0, (Map.Entry) obj)
                }

                {
                    this.f125$f0 = r1
                }

                val Boolean apply(Object obj) {
                    return $m$0(obj)
                }
            }))
            return MuteListData(this.muteList, builder.build())
        }
        ImmutableMap.Builder builder2 = ImmutableMap.builder()
        builder2.putAll(FluentIterable.from(this.muteList.entrySet()).filter(Predicate(muteListKey) {

            /* renamed from: -$f0  reason: not valid java name */
            private val /* synthetic */ Object f126$f0

            private val /* synthetic */ Boolean $m$0(Object obj) {
                return MuteListData.m227lambda$com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_4217((MuteListKey) this.f126$f0, (Map.Entry) obj)
            }

            {
                this.f126$f0 = r1
            }

            val Boolean apply(Object obj) {
                return $m$0(obj)
            }
        }))
        return MuteListData(builder2.build(), this.muteListNames)
    }

    public ImmutableList<MuteListEntry> getMuteList() {
        ImmutableList.Builder builder = ImmutableList.builder()
        builder.addAll((Iterable) this.muteList.values())
        builder.addAll((Iterable) this.muteListNames.values())
        return ordering.immutableSortedCopy(builder.build())
    }

     public fun isMuted(uuid: UUID, muteType: MuteType): Boolean {
        return this.muteList.containsKey(MuteListKey(muteType, uuid))
    }

     public fun isMutedByName(str: String): Boolean {
        return this.muteListNames.containsKey(str)
    }
}

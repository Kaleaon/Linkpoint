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
import androidx.annotation.Immutable

@Immutable
class MuteListData {
    private Ordering<MuteListEntry> ordering = Ordering<MuteListEntry>() {
        fun compare(MuteListEntry muteListEntry, MuteListEntry muteListEntry2): Int {
            Int viewOrder = muteListEntry.type.getViewOrder() - muteListEntry2.type.getViewOrder()
            return viewOrder != 0 ? viewOrder : muteListEntry.name.compareToIgnoreCase(muteListEntry2.name)
        }
    }
    private ImmutableMap<MuteListKey, MuteListEntry> muteList
    private ImmutableMap<String, MuteListEntry> muteListNames

    MuteListData() {
        this.muteList = ImmutableMap.of()
        this.muteListNames = ImmutableMap.of()
    }

    MuteListData(Map<MuteListKey, MuteListEntry> map, Map<String, MuteListEntry> map2) {
        this.muteList = ImmutableMap.copyOf(map)
        this.muteListNames = ImmutableMap.copyOf(map2)
    }

    MuteListData(ByteArray bArr) {
        ImmutableMap.Builder builder = ImmutableMap.builder()
        ImmutableMap.Builder builder2 = ImmutableMap.builder()
        if (bArr != null) {
            try {
                BufferedReader bufferedReader = BufferedReader(InputStreamReader(ByteArrayInputStream(bArr)))
                while (true) {
                    String readLine = bufferedReader.readLine()
                    if (readLine == null) {
                        break
                    }
                    SimpleStringParser simpleStringParser = SimpleStringParser(readLine.trim(), " ")
                    try {
                        Int intToken = simpleStringParser.getIntToken(" ")
                        String nextToken = simpleStringParser.nextToken(" ")
                        simpleStringParser.skipAllDelimiters(" ")
                        String nextToken2 = simpleStringParser.nextToken("|")
                        simpleStringParser.skipAllDelimiters("|")
                        try {
                            i = simpleStringParser.getIntToken(" ")
                        } catch (SimpleStringParser.StringParsingException e) {
                            i = 0
                        }
                        Debug.Printf("MuteList: line '%s' type %d idstring '%s' name '%s' flags %d", readLine.trim(), Int.valueOf(intToken), nextToken, nextToken2, Int.valueOf(i))
                        if (intToken >= 0 && intToken < MuteType.values().size) {
                            MuteType muteType = MuteType.values()[intToken]
                            MuteListEntry muteListEntry = MuteListEntry(muteType, UUID.fromString(nextToken), nextToken2, i)
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
    /* synthetic */ Boolean m226lambda$com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_3795(MuteListEntry muteListEntry, Map.Entry entry) {
        if (entry != null) {
            return !((entry as String).getKey()).equals(muteListEntry.name)
        }
        return false
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_4217  reason: not valid java name */
    /* synthetic */ Boolean m227lambda$com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_4217(MuteListKey muteListKey, Map.Entry entry) {
        if (entry != null) {
            return !((entry as MuteListKey).getKey()).equals(muteListKey)
        }
        return false
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_4795  reason: not valid java name */
    /* synthetic */ Boolean m228lambda$com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_4795(MuteListEntry muteListEntry, Map.Entry entry) {
        if (entry != null) {
            return !((entry as String).getKey()).equals(muteListEntry.name)
        }
        return false
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_5273  reason: not valid java name */
    /* synthetic */ Boolean m229lambda$com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_5273(MuteListKey muteListKey, Map.Entry entry) {
        if (entry != null) {
            return !((entry as MuteListKey).getKey()).equals(muteListKey)
        }
        return false
    }

    fun Block(MuteListEntry muteListEntry): MuteListData {
        MuteListKey muteListKey = MuteListKey(muteListEntry)
        if (muteListKey.muteType == MuteType.BY_NAME) {
            ImmutableMap.Builder builder = ImmutableMap.builder()
            builder.putAll(FluentIterable.from(this.muteListNames.entrySet()).filter($Lambda$pgqqKd1WN3Cb6t0a10SOVDLtoOA(muteListEntry)))
            builder.put(muteListEntry.name, muteListEntry)
            return MuteListData(this.muteList, builder.build())
        }
        ImmutableMap.Builder builder2 = ImmutableMap.builder()
        builder2.putAll(FluentIterable.from(this.muteList.entrySet()).filter(Predicate(muteListKey) {

            /* renamed from: -$f0  reason: not valid java name */
            private /* synthetic */ Any f124$f0

            private /* synthetic */ Boolean $m$0(Any obj) {
                return MuteListData.m229lambda$com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_5273((this as MuteListKey).f124$f0, (Map.Entry) obj)
            }

            {
                this.f124$f0 = r1
            }

            fun apply(Any obj): Boolean {
                return $m$0(obj)
            }
        }))
        builder2.put(muteListKey, muteListEntry)
        return MuteListData(builder2.build(), this.muteListNames)
    }

    fun Unblock(MuteListEntry muteListEntry): MuteListData {
        MuteListKey muteListKey = MuteListKey(muteListEntry)
        if (muteListKey.muteType == MuteType.BY_NAME) {
            ImmutableMap.Builder builder = ImmutableMap.builder()
            builder.putAll(FluentIterable.from(this.muteListNames.entrySet()).filter(Predicate(muteListEntry) {

                /* renamed from: -$f0  reason: not valid java name */
                private /* synthetic */ Any f125$f0

                private /* synthetic */ Boolean $m$0(Any obj) {
                    return MuteListData.m226lambda$com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_3795((this as MuteListEntry).f125$f0, (Map.Entry) obj)
                }

                {
                    this.f125$f0 = r1
                }

                fun apply(Any obj): Boolean {
                    return $m$0(obj)
                }
            }))
            return MuteListData(this.muteList, builder.build())
        }
        ImmutableMap.Builder builder2 = ImmutableMap.builder()
        builder2.putAll(FluentIterable.from(this.muteList.entrySet()).filter(Predicate(muteListKey) {

            /* renamed from: -$f0  reason: not valid java name */
            private /* synthetic */ Any f126$f0

            private /* synthetic */ Boolean $m$0(Any obj) {
                return MuteListData.m227lambda$com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_4217((this as MuteListKey).f126$f0, (Map.Entry) obj)
            }

            {
                this.f126$f0 = r1
            }

            fun apply(Any obj): Boolean {
                return $m$0(obj)
            }
        }))
        return MuteListData(builder2.build(), this.muteListNames)
    }

    fun getMuteList(): ImmutableList<MuteListEntry> {
        ImmutableList.Builder builder = ImmutableList.builder()
        builder.addAll((this as Iterable).muteList.values())
        builder.addAll((this as Iterable).muteListNames.values())
        return ordering.immutableSortedCopy(builder.build())
    }

    fun isMuted(UUID uuid, MuteType muteType): Boolean {
        return this.muteList.containsKey(MuteListKey(muteType, uuid))
    }

    fun isMutedByName(String str): Boolean {
        return this.muteListNames.containsKey(str)
    }
}

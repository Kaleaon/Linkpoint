package com.lumiyaviewer.lumiya.slproto.modules.mutelist;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.utils.SimpleStringParser;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.UUID;
import javax.annotation.concurrent.Immutable;

@Immutable
public class MuteListData {
    private static final Ordering<MuteListEntry> ordering = new Ordering<MuteListEntry>() {
        public int compare(MuteListEntry muteListEntry, MuteListEntry muteListEntry2) {
            int viewOrder = muteListEntry.type.getViewOrder() - muteListEntry2.type.getViewOrder();
            return viewOrder != 0 ? viewOrder : muteListEntry.name.compareToIgnoreCase(muteListEntry2.name);
        }
    };
    private final ImmutableMap<MuteListKey, MuteListEntry> muteList;
    private final ImmutableMap<String, MuteListEntry> muteListNames;

    public MuteListData() {
        this.muteList = ImmutableMap.of();
        this.muteListNames = ImmutableMap.of();
    }

    public MuteListData(Map<MuteListKey, MuteListEntry> map, Map<String, MuteListEntry> map2) {
        this.muteList = ImmutableMap.copyOf(map);
        this.muteListNames = ImmutableMap.copyOf(map2);
    }

    public MuteListData(byte[] bArr) {
        int i;
        ImmutableMap.Builder builder = ImmutableMap.builder();
        ImmutableMap.Builder builder2 = ImmutableMap.builder();
        if (bArr != null) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bArr)));
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    }
                    SimpleStringParser simpleStringParser = new SimpleStringParser(readLine.trim(), " ");
                    try {
                        int intToken = simpleStringParser.getIntToken(" ");
                        String nextToken = simpleStringParser.nextToken(" ");
                        simpleStringParser.skipAllDelimiters(" ");
                        String nextToken2 = simpleStringParser.nextToken("|");
                        simpleStringParser.skipAllDelimiters("|");
                        try {
                            i = simpleStringParser.getIntToken(" ");
                        } catch (SimpleStringParser.StringParsingException e) {
                            i = 0;
                        }
                        Debug.Printf("MuteList: line '%s' type %d idstring '%s' name '%s' flags %d", readLine.trim(), Integer.valueOf(intToken), nextToken, nextToken2, Integer.valueOf(i));
                        if (intToken >= 0 && intToken < MuteType.values().length) {
                            MuteType muteType = MuteType.values()[intToken];
                            MuteListEntry muteListEntry = new MuteListEntry(muteType, UUID.fromString(nextToken), nextToken2, i);
                            if (muteType == MuteType.BY_NAME) {
                                builder2.put(nextToken2, muteListEntry);
                            } else {
                                builder.put(new MuteListKey(muteListEntry), muteListEntry);
                            }
                        }
                    } catch (SimpleStringParser.StringParsingException e2) {
                        Debug.Warning(e2);
                    }
                }
            } catch (IOException e3) {
                Debug.Warning(e3);
            }
        }
        this.muteList = builder.build();
        this.muteListNames = builder2.build();
    }

    /* renamed from: isEntryNameNotEqual - checks if mute list entry name doesn't match the given entry key */
    static /* synthetic */ boolean isEntryNameNotEqual(MuteListEntry muteListEntry, Map.Entry entry) {
        if (entry != null) {
            return !((String) entry.getKey()).equals(muteListEntry.name);
        }
        return false;
    }

    /* renamed from: isMuteKeyNotEqual - checks if mute list key doesn't match the given entry key */
    static /* synthetic */ boolean isMuteKeyNotEqual(MuteListKey muteListKey, Map.Entry entry) {
        if (entry != null) {
            return !((MuteListKey) entry.getKey()).equals(muteListKey);
        }
        return false;
    }

    public static boolean m228lambda$com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_4795(MuteListEntry muteListEntry, Map.Entry entry) {
        return isEntryNameNotEqual(muteListEntry, entry);
    }

    public MuteListData Block(MuteListEntry muteListEntry) {
        MuteListKey muteListKey = new MuteListKey(muteListEntry);
        if (muteListKey.muteType == MuteType.BY_NAME) {
            ImmutableMap.Builder builder = ImmutableMap.builder();
            builder.putAll(FluentIterable.from(this.muteListNames.entrySet()).filter(new $Lambda$pgqqKd1WN3Cb6t0a10SOVDLtoOA(muteListEntry)));
            builder.put(muteListEntry.name, muteListEntry);
            return new MuteListData(this.muteList, builder.build());
        }
        ImmutableMap.Builder builder2 = ImmutableMap.builder();
        builder2.putAll(FluentIterable.from(this.muteList.entrySet()).filter(new Predicate(muteListKey) {

            /* renamed from: -$f0  reason: not valid java name */
            private final /* synthetic */ Object f124$f0;

            private final /* synthetic */ boolean $m$0(Object obj) {
                return MuteListData.isMuteKeyNotEqual((MuteListKey) this.f124$f0, (Map.Entry) obj);
            }

            {
                this.f124$f0 = r1;
            }

            public final boolean apply(Object obj) {
                return $m$0(obj);
            }
        }));
        builder2.put(muteListKey, muteListEntry);
        return new MuteListData(builder2.build(), this.muteListNames);
    }

    public MuteListData Unblock(MuteListEntry muteListEntry) {
        MuteListKey muteListKey = new MuteListKey(muteListEntry);
        if (muteListKey.muteType == MuteType.BY_NAME) {
            ImmutableMap.Builder builder = ImmutableMap.builder();
            builder.putAll(FluentIterable.from(this.muteListNames.entrySet()).filter(new Predicate(muteListEntry) {

                /* renamed from: -$f0  reason: not valid java name */
                private final /* synthetic */ Object f125$f0;

                private final /* synthetic */ boolean $m$0(Object obj) {
                    return MuteListData.isEntryNameNotEqual((MuteListEntry) this.f125$f0, (Map.Entry) obj);
                }

                {
                    this.f125$f0 = r1;
                }

                public final boolean apply(Object obj) {
                    return $m$0(obj);
                }
            }));
            return new MuteListData(this.muteList, builder.build());
        }
        ImmutableMap.Builder builder2 = ImmutableMap.builder();
        builder2.putAll(FluentIterable.from(this.muteList.entrySet()).filter(new Predicate(muteListKey) {

            /* renamed from: -$f0  reason: not valid java name */
            private final /* synthetic */ Object f126$f0;

            private final /* synthetic */ boolean $m$0(Object obj) {
                return MuteListData.isMuteKeyNotEqual((MuteListKey) this.f126$f0, (Map.Entry) obj);
            }

            {
                this.f126$f0 = r1;
            }

            public final boolean apply(Object obj) {
                return $m$0(obj);
            }
        }));
        return new MuteListData(builder2.build(), this.muteListNames);
    }

    public ImmutableList<MuteListEntry> getMuteList() {
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll((Iterable) this.muteList.values());
        builder.addAll((Iterable) this.muteListNames.values());
        return ordering.immutableSortedCopy(builder.build());
    }

    public boolean isMuted(UUID uuid, MuteType muteType) {
        return this.muteList.containsKey(new MuteListKey(muteType, uuid));
    }

    public boolean isMutedByName(String str) {
        return this.muteListNames.containsKey(str);
    }
}

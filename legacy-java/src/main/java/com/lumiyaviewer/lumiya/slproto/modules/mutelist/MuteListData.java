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
/* loaded from: classes.dex */
public class MuteListData {
    private static final Ordering<MuteListEntry> ordering = new Ordering<MuteListEntry>() { // from class: com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListData.1
        @Override // com.google.common.collect.Ordering, java.util.Comparator
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
        this.muteList = ImmutableMap.copyOf((Map) map);
        this.muteListNames = ImmutableMap.copyOf((Map) map2);
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
                        if (intToken >= 0 && intToken < MuteType.valuesCustom().length) {
                            MuteType muteType = MuteType.valuesCustom()[intToken];
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

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_3795, reason: not valid java name */
    public static /* synthetic */ boolean m236x1ef0929e(MuteListEntry muteListEntry, Map.Entry entry) {
        if (entry != null) {
            return !((String) entry.getKey()).equals(muteListEntry.name);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_4217, reason: not valid java name */
    public static /* synthetic */ boolean m237x1ef0f342(MuteListKey muteListKey, Map.Entry entry) {
        if (entry != null) {
            return !((MuteListKey) entry.getKey()).equals(muteListKey);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_4795, reason: not valid java name */
    public static /* synthetic */ boolean m238x1ef106fd(MuteListEntry muteListEntry, Map.Entry entry) {
        if (entry != null) {
            return !((String) entry.getKey()).equals(muteListEntry.name);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_5273, reason: not valid java name */
    public static /* synthetic */ boolean m239x1ef16857(MuteListKey muteListKey, Map.Entry entry) {
        if (entry != null) {
            return !((MuteListKey) entry.getKey()).equals(muteListKey);
        }
        return false;
    }

    public MuteListData Block(final MuteListEntry muteListEntry) {
        final MuteListKey muteListKey = new MuteListKey(muteListEntry);
        if (muteListKey.muteType == MuteType.BY_NAME) {
            ImmutableMap.Builder builder = ImmutableMap.builder();
            builder.putAll(FluentIterable.from(this.muteListNames.entrySet()).filter(new Predicate() { // from class: com.lumiyaviewer.lumiya.slproto.modules.mutelist.-$Lambda$pgqqKd1WN3Cb6t0a10SOVDLtoOA
                private final /* synthetic */ boolean $m$0(Object obj) {
                    return MuteListData.m238x1ef106fd((MuteListEntry) muteListEntry, (Map.Entry) obj);
                }

                @Override // com.google.common.base.Predicate
                public final boolean apply(Object obj) {
                    return $m$0(obj);
                }
            }));
            builder.put(muteListEntry.name, muteListEntry);
            return new MuteListData(this.muteList, builder.build());
        }
        ImmutableMap.Builder builder2 = ImmutableMap.builder();
        builder2.putAll(FluentIterable.from(this.muteList.entrySet()).filter(new Predicate() { // from class: com.lumiyaviewer.lumiya.slproto.modules.mutelist.-$Lambda$pgqqKd1WN3Cb6t0a10SOVDLtoOA.1
            private final /* synthetic */ boolean $m$0(Object obj) {
                return MuteListData.m239x1ef16857((MuteListKey) muteListKey, (Map.Entry) obj);
            }

            @Override // com.google.common.base.Predicate
            public final boolean apply(Object obj) {
                return $m$0(obj);
            }
        }));
        builder2.put(muteListKey, muteListEntry);
        return new MuteListData(builder2.build(), this.muteListNames);
    }

    public MuteListData Unblock(final MuteListEntry muteListEntry) {
        final MuteListKey muteListKey = new MuteListKey(muteListEntry);
        if (muteListKey.muteType == MuteType.BY_NAME) {
            ImmutableMap.Builder builder = ImmutableMap.builder();
            builder.putAll(FluentIterable.from(this.muteListNames.entrySet()).filter(new Predicate() { // from class: com.lumiyaviewer.lumiya.slproto.modules.mutelist.-$Lambda$pgqqKd1WN3Cb6t0a10SOVDLtoOA.2
                private final /* synthetic */ boolean $m$0(Object obj) {
                    return MuteListData.m236x1ef0929e((MuteListEntry) muteListEntry, (Map.Entry) obj);
                }

                @Override // com.google.common.base.Predicate
                public final boolean apply(Object obj) {
                    return $m$0(obj);
                }
            }));
            return new MuteListData(this.muteList, builder.build());
        }
        ImmutableMap.Builder builder2 = ImmutableMap.builder();
        builder2.putAll(FluentIterable.from(this.muteList.entrySet()).filter(new Predicate() { // from class: com.lumiyaviewer.lumiya.slproto.modules.mutelist.-$Lambda$pgqqKd1WN3Cb6t0a10SOVDLtoOA.3
            private final /* synthetic */ boolean $m$0(Object obj) {
                return MuteListData.m237x1ef0f342((MuteListKey) muteListKey, (Map.Entry) obj);
            }

            @Override // com.google.common.base.Predicate
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

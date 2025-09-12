// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.mutelist;

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

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.mutelist:
//            MuteType, MuteListEntry, MuteListKey

public class MuteListData
{

    private static final Ordering ordering = new Ordering() {

        public int compare(MuteListEntry mutelistentry, MuteListEntry mutelistentry1)
        {
            int i = mutelistentry.type.getViewOrder() - mutelistentry1.type.getViewOrder();
            if (i != 0)
            {
                return i;
            } else
            {
                return mutelistentry.name.compareToIgnoreCase(mutelistentry1.name);
            }
        }

        public volatile int compare(Object obj, Object obj1)
        {
            return compare((MuteListEntry)obj, (MuteListEntry)obj1);
        }

    };
    private final ImmutableMap muteList;
    private final ImmutableMap muteListNames;

    public MuteListData()
    {
        muteList = ImmutableMap.of();
        muteListNames = ImmutableMap.of();
    }

    public MuteListData(Map map, Map map1)
    {
        muteList = ImmutableMap.copyOf(map);
        muteListNames = ImmutableMap.copyOf(map1);
    }

    public MuteListData(byte abyte0[])
    {
        com.google.common.collect.ImmutableMap.Builder builder;
        com.google.common.collect.ImmutableMap.Builder builder1;
        builder = ImmutableMap.builder();
        builder1 = ImmutableMap.builder();
        if (abyte0 == null) goto _L2; else goto _L1
_L1:
        abyte0 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(abyte0)));
_L5:
        String s1 = abyte0.readLine();
        if (s1 == null) goto _L2; else goto _L3
_L3:
        SimpleStringParser simplestringparser = new SimpleStringParser(s1.trim(), " ");
        String s;
        Object obj;
        int j;
        j = simplestringparser.getIntToken(" ");
        obj = simplestringparser.nextToken(" ");
        simplestringparser.skipAllDelimiters(" ");
        s = simplestringparser.nextToken("|");
        simplestringparser.skipAllDelimiters("|");
        com.lumiyaviewer.lumiya.utils.SimpleStringParser.StringParsingException stringparsingexception;
        int i;
        try
        {
            i = simplestringparser.getIntToken(" ");
        }
        catch (com.lumiyaviewer.lumiya.utils.SimpleStringParser.StringParsingException stringparsingexception1)
        {
            i = 0;
        }
        Debug.Printf("MuteList: line '%s' type %d idstring '%s' name '%s' flags %d", new Object[] {
            s1.trim(), Integer.valueOf(j), obj, s, Integer.valueOf(i)
        });
        if (j < 0) goto _L5; else goto _L4
_L4:
        if (j >= MuteType.values().length) goto _L5; else goto _L6
_L6:
        MuteType mutetype = MuteType.values()[j];
        obj = new MuteListEntry(mutetype, UUID.fromString(((String) (obj))), s, i);
        if (mutetype != MuteType.BY_NAME)
        {
            break MISSING_BLOCK_LABEL_261;
        }
        builder1.put(s, obj);
          goto _L5
        stringparsingexception;
        Debug.Warning(stringparsingexception);
          goto _L5
        abyte0;
        Debug.Warning(abyte0);
_L2:
        muteList = builder.build();
        muteListNames = builder1.build();
        return;
        builder.put(new MuteListKey(((MuteListEntry) (obj))), obj);
          goto _L5
    }

    static boolean lambda$_2D_com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_3795(MuteListEntry mutelistentry, java.util.Map.Entry entry)
    {
        if (entry != null)
        {
            return ((String)entry.getKey()).equals(mutelistentry.name) ^ true;
        } else
        {
            return false;
        }
    }

    static boolean lambda$_2D_com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_4217(MuteListKey mutelistkey, java.util.Map.Entry entry)
    {
        if (entry != null)
        {
            return ((MuteListKey)entry.getKey()).equals(mutelistkey) ^ true;
        } else
        {
            return false;
        }
    }

    static boolean lambda$_2D_com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_4795(MuteListEntry mutelistentry, java.util.Map.Entry entry)
    {
        if (entry != null)
        {
            return ((String)entry.getKey()).equals(mutelistentry.name) ^ true;
        } else
        {
            return false;
        }
    }

    static boolean lambda$_2D_com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_5273(MuteListKey mutelistkey, java.util.Map.Entry entry)
    {
        if (entry != null)
        {
            return ((MuteListKey)entry.getKey()).equals(mutelistkey) ^ true;
        } else
        {
            return false;
        }
    }

    public MuteListData Block(MuteListEntry mutelistentry)
    {
        Object obj = new MuteListKey(mutelistentry);
        if (((MuteListKey) (obj)).muteType == MuteType.BY_NAME)
        {
            obj = ImmutableMap.builder();
            ((com.google.common.collect.ImmutableMap.Builder) (obj)).putAll(FluentIterable.from(muteListNames.entrySet()).filter(new _2D_.Lambda.pgqqKd1WN3Cb6t0a10SOVDLtoOA(mutelistentry)));
            ((com.google.common.collect.ImmutableMap.Builder) (obj)).put(mutelistentry.name, mutelistentry);
            return new MuteListData(muteList, ((com.google.common.collect.ImmutableMap.Builder) (obj)).build());
        } else
        {
            com.google.common.collect.ImmutableMap.Builder builder = ImmutableMap.builder();
            builder.putAll(FluentIterable.from(muteList.entrySet()).filter(new _2D_.Lambda.pgqqKd1WN3Cb6t0a10SOVDLtoOA._cls1(obj)));
            builder.put(obj, mutelistentry);
            return new MuteListData(builder.build(), muteListNames);
        }
    }

    public MuteListData Unblock(MuteListEntry mutelistentry)
    {
        Object obj = new MuteListKey(mutelistentry);
        if (((MuteListKey) (obj)).muteType == MuteType.BY_NAME)
        {
            obj = ImmutableMap.builder();
            ((com.google.common.collect.ImmutableMap.Builder) (obj)).putAll(FluentIterable.from(muteListNames.entrySet()).filter(new _2D_.Lambda.pgqqKd1WN3Cb6t0a10SOVDLtoOA._cls2(mutelistentry)));
            return new MuteListData(muteList, ((com.google.common.collect.ImmutableMap.Builder) (obj)).build());
        } else
        {
            mutelistentry = ImmutableMap.builder();
            mutelistentry.putAll(FluentIterable.from(muteList.entrySet()).filter(new _2D_.Lambda.pgqqKd1WN3Cb6t0a10SOVDLtoOA._cls3(obj)));
            return new MuteListData(mutelistentry.build(), muteListNames);
        }
    }

    public ImmutableList getMuteList()
    {
        com.google.common.collect.ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll(muteList.values());
        builder.addAll(muteListNames.values());
        return ordering.immutableSortedCopy(builder.build());
    }

    public boolean isMuted(UUID uuid, MuteType mutetype)
    {
        uuid = new MuteListKey(mutetype, uuid);
        return muteList.containsKey(uuid);
    }

    public boolean isMutedByName(String s)
    {
        return muteListNames.containsKey(s);
    }

}

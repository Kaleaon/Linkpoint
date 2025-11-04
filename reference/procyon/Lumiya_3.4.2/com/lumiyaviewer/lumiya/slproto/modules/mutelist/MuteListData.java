// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.mutelist;

import java.util.UUID;
import com.google.common.collect.ImmutableList;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import javax.annotation.concurrent.Immutable;

@Immutable
public class MuteListData
{
    private static final Ordering<MuteListEntry> ordering;
    private final ImmutableMap<MuteListKey, MuteListEntry> muteList;
    private final ImmutableMap<String, MuteListEntry> muteListNames;
    
    static {
        ordering = new Ordering<MuteListEntry>() {
            @Override
            public int compare(final MuteListEntry muteListEntry, final MuteListEntry muteListEntry2) {
                final int n = muteListEntry.type.getViewOrder() - muteListEntry2.type.getViewOrder();
                if (n != 0) {
                    return n;
                }
                return muteListEntry.name.compareToIgnoreCase(muteListEntry2.name);
            }
        };
    }
    
    public MuteListData() {
        this.muteList = ImmutableMap.of();
        this.muteListNames = ImmutableMap.of();
    }
    
    public MuteListData(final Map<MuteListKey, MuteListEntry> map, final Map<String, MuteListEntry> map2) {
        this.muteList = ImmutableMap.copyOf((Map<? extends MuteListKey, ? extends MuteListEntry>)map);
        this.muteListNames = ImmutableMap.copyOf((Map<? extends String, ? extends MuteListEntry>)map2);
    }
    
    public MuteListData(final byte[] p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokespecial   java/lang/Object.<init>:()V
        //     4: invokestatic    com/google/common/collect/ImmutableMap.builder:()Lcom/google/common/collect/ImmutableMap$Builder;
        //     7: astore_2       
        //     8: invokestatic    com/google/common/collect/ImmutableMap.builder:()Lcom/google/common/collect/ImmutableMap$Builder;
        //    11: astore_3       
        //    12: aload_1        
        //    13: ifnull          246
        //    16: new             Ljava/io/BufferedReader;
        //    19: astore          4
        //    21: new             Ljava/io/InputStreamReader;
        //    24: astore          5
        //    26: new             Ljava/io/ByteArrayInputStream;
        //    29: astore          6
        //    31: aload           6
        //    33: aload_1        
        //    34: invokespecial   java/io/ByteArrayInputStream.<init>:([B)V
        //    37: aload           5
        //    39: aload           6
        //    41: invokespecial   java/io/InputStreamReader.<init>:(Ljava/io/InputStream;)V
        //    44: aload           4
        //    46: aload           5
        //    48: invokespecial   java/io/BufferedReader.<init>:(Ljava/io/Reader;)V
        //    51: aload           4
        //    53: invokevirtual   java/io/BufferedReader.readLine:()Ljava/lang/String;
        //    56: astore          6
        //    58: aload           6
        //    60: ifnull          246
        //    63: new             Lcom/lumiyaviewer/lumiya/utils/SimpleStringParser;
        //    66: astore          7
        //    68: aload           7
        //    70: aload           6
        //    72: invokevirtual   java/lang/String.trim:()Ljava/lang/String;
        //    75: ldc             " "
        //    77: invokespecial   com/lumiyaviewer/lumiya/utils/SimpleStringParser.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //    80: aload           7
        //    82: ldc             " "
        //    84: invokevirtual   com/lumiyaviewer/lumiya/utils/SimpleStringParser.getIntToken:(Ljava/lang/String;)I
        //    87: istore          8
        //    89: aload           7
        //    91: ldc             " "
        //    93: invokevirtual   com/lumiyaviewer/lumiya/utils/SimpleStringParser.nextToken:(Ljava/lang/String;)Ljava/lang/String;
        //    96: astore_1       
        //    97: aload           7
        //    99: ldc             " "
        //   101: invokevirtual   com/lumiyaviewer/lumiya/utils/SimpleStringParser.skipAllDelimiters:(Ljava/lang/String;)V
        //   104: aload           7
        //   106: ldc             "|"
        //   108: invokevirtual   com/lumiyaviewer/lumiya/utils/SimpleStringParser.nextToken:(Ljava/lang/String;)Ljava/lang/String;
        //   111: astore          5
        //   113: aload           7
        //   115: ldc             "|"
        //   117: invokevirtual   com/lumiyaviewer/lumiya/utils/SimpleStringParser.skipAllDelimiters:(Ljava/lang/String;)V
        //   120: aload           7
        //   122: ldc             " "
        //   124: invokevirtual   com/lumiyaviewer/lumiya/utils/SimpleStringParser.getIntToken:(Ljava/lang/String;)I
        //   127: istore          9
        //   129: ldc             "MuteList: line '%s' type %d idstring '%s' name '%s' flags %d"
        //   131: iconst_5       
        //   132: anewarray       Ljava/lang/Object;
        //   135: dup            
        //   136: iconst_0       
        //   137: aload           6
        //   139: invokevirtual   java/lang/String.trim:()Ljava/lang/String;
        //   142: aastore        
        //   143: dup            
        //   144: iconst_1       
        //   145: iload           8
        //   147: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   150: aastore        
        //   151: dup            
        //   152: iconst_2       
        //   153: aload_1        
        //   154: aastore        
        //   155: dup            
        //   156: iconst_3       
        //   157: aload           5
        //   159: aastore        
        //   160: dup            
        //   161: iconst_4       
        //   162: iload           9
        //   164: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   167: aastore        
        //   168: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   171: iload           8
        //   173: iflt            51
        //   176: iload           8
        //   178: invokestatic    com/lumiyaviewer/lumiya/slproto/modules/mutelist/MuteType.values:()[Lcom/lumiyaviewer/lumiya/slproto/modules/mutelist/MuteType;
        //   181: arraylength    
        //   182: if_icmpge       51
        //   185: invokestatic    com/lumiyaviewer/lumiya/slproto/modules/mutelist/MuteType.values:()[Lcom/lumiyaviewer/lumiya/slproto/modules/mutelist/MuteType;
        //   188: iload           8
        //   190: aaload         
        //   191: astore          7
        //   193: new             Lcom/lumiyaviewer/lumiya/slproto/modules/mutelist/MuteListEntry;
        //   196: astore          6
        //   198: aload           6
        //   200: aload           7
        //   202: aload_1        
        //   203: invokestatic    java/util/UUID.fromString:(Ljava/lang/String;)Ljava/util/UUID;
        //   206: aload           5
        //   208: iload           9
        //   210: invokespecial   com/lumiyaviewer/lumiya/slproto/modules/mutelist/MuteListEntry.<init>:(Lcom/lumiyaviewer/lumiya/slproto/modules/mutelist/MuteType;Ljava/util/UUID;Ljava/lang/String;I)V
        //   213: aload           7
        //   215: getstatic       com/lumiyaviewer/lumiya/slproto/modules/mutelist/MuteType.BY_NAME:Lcom/lumiyaviewer/lumiya/slproto/modules/mutelist/MuteType;
        //   218: if_acmpne       271
        //   221: aload_3        
        //   222: aload           5
        //   224: aload           6
        //   226: invokevirtual   com/google/common/collect/ImmutableMap$Builder.put:(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;
        //   229: pop            
        //   230: goto            51
        //   233: astore_1       
        //   234: aload_1        
        //   235: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
        //   238: goto            51
        //   241: astore_1       
        //   242: aload_1        
        //   243: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
        //   246: aload_0        
        //   247: aload_2        
        //   248: invokevirtual   com/google/common/collect/ImmutableMap$Builder.build:()Lcom/google/common/collect/ImmutableMap;
        //   251: putfield        com/lumiyaviewer/lumiya/slproto/modules/mutelist/MuteListData.muteList:Lcom/google/common/collect/ImmutableMap;
        //   254: aload_0        
        //   255: aload_3        
        //   256: invokevirtual   com/google/common/collect/ImmutableMap$Builder.build:()Lcom/google/common/collect/ImmutableMap;
        //   259: putfield        com/lumiyaviewer/lumiya/slproto/modules/mutelist/MuteListData.muteListNames:Lcom/google/common/collect/ImmutableMap;
        //   262: return         
        //   263: astore          7
        //   265: iconst_0       
        //   266: istore          9
        //   268: goto            129
        //   271: new             Lcom/lumiyaviewer/lumiya/slproto/modules/mutelist/MuteListKey;
        //   274: astore_1       
        //   275: aload_1        
        //   276: aload           6
        //   278: invokespecial   com/lumiyaviewer/lumiya/slproto/modules/mutelist/MuteListKey.<init>:(Lcom/lumiyaviewer/lumiya/slproto/modules/mutelist/MuteListEntry;)V
        //   281: aload_2        
        //   282: aload_1        
        //   283: aload           6
        //   285: invokevirtual   com/google/common/collect/ImmutableMap$Builder.put:(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;
        //   288: pop            
        //   289: goto            51
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                                                     
        //  -----  -----  -----  -----  -------------------------------------------------------------------------
        //  16     51     241    246    Ljava/io/IOException;
        //  51     58     241    246    Ljava/io/IOException;
        //  63     80     241    246    Ljava/io/IOException;
        //  80     120    233    241    Lcom/lumiyaviewer/lumiya/utils/SimpleStringParser$StringParsingException;
        //  80     120    241    246    Ljava/io/IOException;
        //  120    129    263    271    Lcom/lumiyaviewer/lumiya/utils/SimpleStringParser$StringParsingException;
        //  120    129    241    246    Ljava/io/IOException;
        //  129    171    233    241    Lcom/lumiyaviewer/lumiya/utils/SimpleStringParser$StringParsingException;
        //  129    171    241    246    Ljava/io/IOException;
        //  176    230    233    241    Lcom/lumiyaviewer/lumiya/utils/SimpleStringParser$StringParsingException;
        //  176    230    241    246    Ljava/io/IOException;
        //  234    238    241    246    Ljava/io/IOException;
        //  271    289    233    241    Lcom/lumiyaviewer/lumiya/utils/SimpleStringParser$StringParsingException;
        //  271    289    241    246    Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0129:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:799)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:635)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public MuteListData Block(final MuteListEntry muteListEntry) {
        final MuteListKey muteListKey = new MuteListKey(muteListEntry);
        if (muteListKey.muteType == MuteType.BY_NAME) {
            final ImmutableMap.Builder<Object, Object> builder = ImmutableMap.builder();
            builder.putAll(FluentIterable.from(this.muteListNames.entrySet()).filter(new _$Lambda$pgqqKd1WN3Cb6t0a10SOVDLtoOA(muteListEntry)));
            builder.put(muteListEntry.name, muteListEntry);
            return new MuteListData(this.muteList, (Map<String, MuteListEntry>)builder.build());
        }
        final ImmutableMap.Builder<Object, Object> builder2 = ImmutableMap.builder();
        builder2.putAll(FluentIterable.from(this.muteList.entrySet()).filter(new _$Lambda$pgqqKd1WN3Cb6t0a10SOVDLtoOA$1(muteListKey)));
        builder2.put(muteListKey, muteListEntry);
        return new MuteListData((Map<MuteListKey, MuteListEntry>)builder2.build(), this.muteListNames);
    }
    
    public MuteListData Unblock(final MuteListEntry muteListEntry) {
        final MuteListKey muteListKey = new MuteListKey(muteListEntry);
        if (muteListKey.muteType == MuteType.BY_NAME) {
            final ImmutableMap.Builder<Object, Object> builder = ImmutableMap.builder();
            builder.putAll(FluentIterable.from(this.muteListNames.entrySet()).filter(new _$Lambda$pgqqKd1WN3Cb6t0a10SOVDLtoOA$2(muteListEntry)));
            return new MuteListData(this.muteList, (Map<String, MuteListEntry>)builder.build());
        }
        final ImmutableMap.Builder<Object, Object> builder2 = ImmutableMap.builder();
        builder2.putAll(FluentIterable.from(this.muteList.entrySet()).filter(new _$Lambda$pgqqKd1WN3Cb6t0a10SOVDLtoOA$3(muteListKey)));
        return new MuteListData((Map<MuteListKey, MuteListEntry>)builder2.build(), this.muteListNames);
    }
    
    public ImmutableList<MuteListEntry> getMuteList() {
        final ImmutableList.Builder<Object> builder = ImmutableList.builder();
        builder.addAll(this.muteList.values());
        builder.addAll(this.muteListNames.values());
        return MuteListData.ordering.immutableSortedCopy((Iterable<MuteListEntry>)builder.build());
    }
    
    public boolean isMuted(final UUID uuid, final MuteType muteType) {
        return this.muteList.containsKey(new MuteListKey(muteType, uuid));
    }
    
    public boolean isMutedByName(final String s) {
        return this.muteListNames.containsKey(s);
    }
}

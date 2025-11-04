// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.provider;

import android.support.v4.util.Preconditions;
import android.support.annotation.IntRange;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import android.provider.BaseColumns;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.TypefaceCompatUtil;
import java.util.HashMap;
import java.nio.ByteBuffer;
import android.net.Uri;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import android.content.pm.PackageManager;
import android.widget.TextView;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.content.res.Resources;
import android.content.pm.PackageManager$NameNotFoundException;
import android.content.pm.ProviderInfo;
import java.util.Arrays;
import java.util.List;
import android.content.pm.Signature;
import android.support.v4.graphics.TypefaceCompat;
import android.support.annotation.Nullable;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.content.Context;
import android.support.v4.util.LruCache;
import android.support.annotation.GuardedBy;
import android.graphics.Typeface;
import java.util.ArrayList;
import android.support.v4.util.SimpleArrayMap;
import java.util.Comparator;
import android.support.annotation.RestrictTo;

public class FontsContractCompat
{
    private static final int BACKGROUND_THREAD_KEEP_ALIVE_DURATION_MS = 10000;
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public static final String PARCEL_FONT_RESULTS = "font_results";
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public static final int RESULT_CODE_PROVIDER_NOT_FOUND = -1;
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public static final int RESULT_CODE_WRONG_CERTIFICATES = -2;
    private static final String TAG = "FontsContractCompat";
    private static final SelfDestructiveThread sBackgroundThread;
    private static final Comparator<byte[]> sByteArrayComparator;
    private static final Object sLock;
    @GuardedBy("sLock")
    private static final SimpleArrayMap<String, ArrayList<SelfDestructiveThread.ReplyCallback<Typeface>>> sPendingReplies;
    private static final LruCache<String, Typeface> sTypefaceCache;
    
    static {
        sTypefaceCache = new LruCache<String, Typeface>(16);
        sBackgroundThread = new SelfDestructiveThread("fonts", 10, 10000);
        sLock = new Object();
        sPendingReplies = new SimpleArrayMap<String, ArrayList<SelfDestructiveThread.ReplyCallback<Typeface>>>();
        sByteArrayComparator = new Comparator<byte[]>() {
            @Override
            public int compare(final byte[] array, final byte[] array2) {
                if (array.length == array2.length) {
                    for (int i = 0; i < array.length; ++i) {
                        if (array[i] != array2[i]) {
                            return array[i] - array2[i];
                        }
                    }
                    return 0;
                }
                return array.length - array2.length;
            }
        };
    }
    
    private FontsContractCompat() {
    }
    
    public static Typeface buildTypeface(@NonNull final Context context, @Nullable final CancellationSignal cancellationSignal, @NonNull final FontInfo[] array) {
        return TypefaceCompat.createFromFontInfo(context, cancellationSignal, array, 0);
    }
    
    private static List<byte[]> convertToByteArrayList(final Signature[] array) {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < array.length; ++i) {
            list.add(array[i].toByteArray());
        }
        return list;
    }
    
    private static boolean equalsByteArrayList(final List<byte[]> list, final List<byte[]> list2) {
        if (list.size() == list2.size()) {
            for (int i = 0; i < list.size(); ++i) {
                if (!Arrays.equals((byte[])list.get(i), (byte[])list2.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @NonNull
    public static FontFamilyResult fetchFonts(@NonNull final Context context, @Nullable final CancellationSignal cancellationSignal, @NonNull final FontRequest fontRequest) throws PackageManager$NameNotFoundException {
        final ProviderInfo provider = getProvider(context.getPackageManager(), fontRequest, context.getResources());
        if (provider != null) {
            return new FontFamilyResult(0, getFontFromProvider(context, fontRequest, provider.authority, cancellationSignal));
        }
        return new FontFamilyResult(1, null);
    }
    
    private static List<List<byte[]>> getCertificates(final FontRequest fontRequest, final Resources resources) {
        if (fontRequest.getCertificates() == null) {
            return FontResourcesParserCompat.readCerts(resources, fontRequest.getCertificatesArrayResId());
        }
        return fontRequest.getCertificates();
    }
    
    @NonNull
    @VisibleForTesting
    static FontInfo[] getFontFromProvider(final Context p0, final FontRequest p1, final String p2, final CancellationSignal p3) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: dup            
        //     4: invokespecial   java/util/ArrayList.<init>:()V
        //     7: astore          4
        //     9: new             Landroid/net/Uri$Builder;
        //    12: dup            
        //    13: invokespecial   android/net/Uri$Builder.<init>:()V
        //    16: ldc             "content"
        //    18: invokevirtual   android/net/Uri$Builder.scheme:(Ljava/lang/String;)Landroid/net/Uri$Builder;
        //    21: aload_2        
        //    22: invokevirtual   android/net/Uri$Builder.authority:(Ljava/lang/String;)Landroid/net/Uri$Builder;
        //    25: invokevirtual   android/net/Uri$Builder.build:()Landroid/net/Uri;
        //    28: astore          5
        //    30: new             Landroid/net/Uri$Builder;
        //    33: dup            
        //    34: invokespecial   android/net/Uri$Builder.<init>:()V
        //    37: ldc             "content"
        //    39: invokevirtual   android/net/Uri$Builder.scheme:(Ljava/lang/String;)Landroid/net/Uri$Builder;
        //    42: aload_2        
        //    43: invokevirtual   android/net/Uri$Builder.authority:(Ljava/lang/String;)Landroid/net/Uri$Builder;
        //    46: ldc             "file"
        //    48: invokevirtual   android/net/Uri$Builder.appendPath:(Ljava/lang/String;)Landroid/net/Uri$Builder;
        //    51: invokevirtual   android/net/Uri$Builder.build:()Landroid/net/Uri;
        //    54: astore          6
        //    56: getstatic       android/os/Build$VERSION.SDK_INT:I
        //    59: bipush          16
        //    61: if_icmpgt       164
        //    64: aload_0        
        //    65: invokevirtual   android/content/Context.getContentResolver:()Landroid/content/ContentResolver;
        //    68: astore_0       
        //    69: aload_1        
        //    70: invokevirtual   android/support/v4/provider/FontRequest.getQuery:()Ljava/lang/String;
        //    73: astore_1       
        //    74: aload_0        
        //    75: aload           5
        //    77: bipush          7
        //    79: anewarray       Ljava/lang/String;
        //    82: dup            
        //    83: iconst_0       
        //    84: ldc_w           "_id"
        //    87: aastore        
        //    88: dup            
        //    89: iconst_1       
        //    90: ldc_w           "file_id"
        //    93: aastore        
        //    94: dup            
        //    95: iconst_2       
        //    96: ldc_w           "font_ttc_index"
        //    99: aastore        
        //   100: dup            
        //   101: iconst_3       
        //   102: ldc_w           "font_variation_settings"
        //   105: aastore        
        //   106: dup            
        //   107: iconst_4       
        //   108: ldc_w           "font_weight"
        //   111: aastore        
        //   112: dup            
        //   113: iconst_5       
        //   114: ldc_w           "font_italic"
        //   117: aastore        
        //   118: dup            
        //   119: bipush          6
        //   121: ldc_w           "result_code"
        //   124: aastore        
        //   125: ldc_w           "query = ?"
        //   128: iconst_1       
        //   129: anewarray       Ljava/lang/String;
        //   132: dup            
        //   133: iconst_0       
        //   134: aload_1        
        //   135: aastore        
        //   136: aconst_null    
        //   137: invokevirtual   android/content/ContentResolver.query:(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
        //   140: astore_0       
        //   141: aload_0        
        //   142: ifnonnull       245
        //   145: aload           4
        //   147: astore_1       
        //   148: aload_0        
        //   149: ifnonnull       508
        //   152: aload_1        
        //   153: iconst_0       
        //   154: anewarray       Landroid/support/v4/provider/FontsContractCompat$FontInfo;
        //   157: invokevirtual   java/util/ArrayList.toArray:([Ljava/lang/Object;)[Ljava/lang/Object;
        //   160: checkcast       [Landroid/support/v4/provider/FontsContractCompat$FontInfo;
        //   163: areturn        
        //   164: aload_0        
        //   165: invokevirtual   android/content/Context.getContentResolver:()Landroid/content/ContentResolver;
        //   168: astore_0       
        //   169: aload_1        
        //   170: invokevirtual   android/support/v4/provider/FontRequest.getQuery:()Ljava/lang/String;
        //   173: astore_1       
        //   174: aload_0        
        //   175: aload           5
        //   177: bipush          7
        //   179: anewarray       Ljava/lang/String;
        //   182: dup            
        //   183: iconst_0       
        //   184: ldc_w           "_id"
        //   187: aastore        
        //   188: dup            
        //   189: iconst_1       
        //   190: ldc_w           "file_id"
        //   193: aastore        
        //   194: dup            
        //   195: iconst_2       
        //   196: ldc_w           "font_ttc_index"
        //   199: aastore        
        //   200: dup            
        //   201: iconst_3       
        //   202: ldc_w           "font_variation_settings"
        //   205: aastore        
        //   206: dup            
        //   207: iconst_4       
        //   208: ldc_w           "font_weight"
        //   211: aastore        
        //   212: dup            
        //   213: iconst_5       
        //   214: ldc_w           "font_italic"
        //   217: aastore        
        //   218: dup            
        //   219: bipush          6
        //   221: ldc_w           "result_code"
        //   224: aastore        
        //   225: ldc_w           "query = ?"
        //   228: iconst_1       
        //   229: anewarray       Ljava/lang/String;
        //   232: dup            
        //   233: iconst_0       
        //   234: aload_1        
        //   235: aastore        
        //   236: aconst_null    
        //   237: aload_3        
        //   238: invokevirtual   android/content/ContentResolver.query:(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Landroid/os/CancellationSignal;)Landroid/database/Cursor;
        //   241: astore_0       
        //   242: goto            141
        //   245: aload_0        
        //   246: invokeinterface android/database/Cursor.getCount:()I
        //   251: ifle            145
        //   254: aload_0        
        //   255: ldc_w           "result_code"
        //   258: invokeinterface android/database/Cursor.getColumnIndex:(Ljava/lang/String;)I
        //   263: istore          7
        //   265: new             Ljava/util/ArrayList;
        //   268: astore_2       
        //   269: aload_2        
        //   270: invokespecial   java/util/ArrayList.<init>:()V
        //   273: aload_0        
        //   274: ldc_w           "_id"
        //   277: invokeinterface android/database/Cursor.getColumnIndex:(Ljava/lang/String;)I
        //   282: istore          8
        //   284: aload_0        
        //   285: ldc_w           "file_id"
        //   288: invokeinterface android/database/Cursor.getColumnIndex:(Ljava/lang/String;)I
        //   293: istore          9
        //   295: aload_0        
        //   296: ldc_w           "font_ttc_index"
        //   299: invokeinterface android/database/Cursor.getColumnIndex:(Ljava/lang/String;)I
        //   304: istore          10
        //   306: aload_0        
        //   307: ldc_w           "font_weight"
        //   310: invokeinterface android/database/Cursor.getColumnIndex:(Ljava/lang/String;)I
        //   315: istore          11
        //   317: aload_0        
        //   318: ldc_w           "font_italic"
        //   321: invokeinterface android/database/Cursor.getColumnIndex:(Ljava/lang/String;)I
        //   326: istore          12
        //   328: aload_2        
        //   329: astore_1       
        //   330: aload_0        
        //   331: invokeinterface android/database/Cursor.moveToNext:()Z
        //   336: ifeq            148
        //   339: iload           7
        //   341: iconst_m1      
        //   342: if_icmpne       430
        //   345: iconst_0       
        //   346: istore          13
        //   348: iload           10
        //   350: iconst_m1      
        //   351: if_icmpne       443
        //   354: iconst_0       
        //   355: istore          14
        //   357: iload           9
        //   359: iconst_m1      
        //   360: if_icmpeq       456
        //   363: aload           6
        //   365: aload_0        
        //   366: iload           9
        //   368: invokeinterface android/database/Cursor.getLong:(I)J
        //   373: invokestatic    android/content/ContentUris.withAppendedId:(Landroid/net/Uri;J)Landroid/net/Uri;
        //   376: astore_1       
        //   377: iload           11
        //   379: iconst_m1      
        //   380: if_icmpne       473
        //   383: sipush          400
        //   386: istore          15
        //   388: iload           12
        //   390: iconst_m1      
        //   391: if_icmpne       486
        //   394: iconst_0       
        //   395: istore          16
        //   397: new             Landroid/support/v4/provider/FontsContractCompat$FontInfo;
        //   400: astore_3       
        //   401: aload_3        
        //   402: aload_1        
        //   403: iload           14
        //   405: iload           15
        //   407: iload           16
        //   409: iload           13
        //   411: invokespecial   android/support/v4/provider/FontsContractCompat$FontInfo.<init>:(Landroid/net/Uri;IIZI)V
        //   414: aload_2        
        //   415: aload_3        
        //   416: invokevirtual   java/util/ArrayList.add:(Ljava/lang/Object;)Z
        //   419: pop            
        //   420: goto            328
        //   423: astore_1       
        //   424: aload_0        
        //   425: ifnonnull       517
        //   428: aload_1        
        //   429: athrow         
        //   430: aload_0        
        //   431: iload           7
        //   433: invokeinterface android/database/Cursor.getInt:(I)I
        //   438: istore          13
        //   440: goto            348
        //   443: aload_0        
        //   444: iload           10
        //   446: invokeinterface android/database/Cursor.getInt:(I)I
        //   451: istore          14
        //   453: goto            357
        //   456: aload           5
        //   458: aload_0        
        //   459: iload           8
        //   461: invokeinterface android/database/Cursor.getLong:(I)J
        //   466: invokestatic    android/content/ContentUris.withAppendedId:(Landroid/net/Uri;J)Landroid/net/Uri;
        //   469: astore_1       
        //   470: goto            377
        //   473: aload_0        
        //   474: iload           11
        //   476: invokeinterface android/database/Cursor.getInt:(I)I
        //   481: istore          15
        //   483: goto            388
        //   486: aload_0        
        //   487: iload           12
        //   489: invokeinterface android/database/Cursor.getInt:(I)I
        //   494: istore          17
        //   496: iload           17
        //   498: iconst_1       
        //   499: if_icmpne       394
        //   502: iconst_1       
        //   503: istore          16
        //   505: goto            397
        //   508: aload_0        
        //   509: invokeinterface android/database/Cursor.close:()V
        //   514: goto            152
        //   517: aload_0        
        //   518: invokeinterface android/database/Cursor.close:()V
        //   523: goto            428
        //   526: astore_1       
        //   527: aconst_null    
        //   528: astore_0       
        //   529: goto            424
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  56     141    526    532    Any
        //  164    242    526    532    Any
        //  245    328    423    424    Any
        //  330    339    423    424    Any
        //  363    377    423    424    Any
        //  397    420    423    424    Any
        //  430    440    423    424    Any
        //  443    453    423    424    Any
        //  456    470    423    424    Any
        //  473    483    423    424    Any
        //  486    496    423    424    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: cmpeq:boolean(invokeinterface:int(Cursor::getInt, var_0_210:Object[expected:Cursor], var_12_146:int), ldc:int(1))
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
        //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
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
    
    private static Typeface getFontInternal(final Context context, final FontRequest fontRequest, final int n) {
        FontFamilyResult fetchFonts;
        try {
            fetchFonts = fetchFonts(context, null, fontRequest);
            if (fetchFonts.getStatusCode() != 0) {
                return null;
            }
        }
        catch (final PackageManager$NameNotFoundException ex) {
            return null;
        }
        return TypefaceCompat.createFromFontInfo(context, null, fetchFonts.getFonts(), n);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public static Typeface getFontSync(final Context p0, final FontRequest p1, @Nullable final TextView p2, final int p3, final int p4, final int p5) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: dup            
        //     4: invokespecial   java/lang/StringBuilder.<init>:()V
        //     7: aload_1        
        //     8: invokevirtual   android/support/v4/provider/FontRequest.getIdentifier:()Ljava/lang/String;
        //    11: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    14: ldc_w           "-"
        //    17: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    20: iload           5
        //    22: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        //    25: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    28: astore          6
        //    30: getstatic       android/support/v4/provider/FontsContractCompat.sTypefaceCache:Landroid/support/v4/util/LruCache;
        //    33: aload           6
        //    35: invokevirtual   android/support/v4/util/LruCache.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //    38: checkcast       Landroid/graphics/Typeface;
        //    41: astore          7
        //    43: aload           7
        //    45: ifnonnull       158
        //    48: iload_3        
        //    49: ifeq            161
        //    52: iconst_0       
        //    53: istore_3       
        //    54: iload_3        
        //    55: ifne            166
        //    58: new             Landroid/support/v4/provider/FontsContractCompat$1;
        //    61: dup            
        //    62: aload_0        
        //    63: aload_1        
        //    64: iload           5
        //    66: aload           6
        //    68: invokespecial   android/support/v4/provider/FontsContractCompat$1.<init>:(Landroid/content/Context;Landroid/support/v4/provider/FontRequest;ILjava/lang/String;)V
        //    71: astore_1       
        //    72: iload_3        
        //    73: ifne            180
        //    76: new             Landroid/support/v4/provider/FontsContractCompat$2;
        //    79: dup            
        //    80: new             Ljava/lang/ref/WeakReference;
        //    83: dup            
        //    84: aload_2        
        //    85: invokespecial   java/lang/ref/WeakReference.<init>:(Ljava/lang/Object;)V
        //    88: aload_2        
        //    89: iload           5
        //    91: invokespecial   android/support/v4/provider/FontsContractCompat$2.<init>:(Ljava/lang/ref/WeakReference;Landroid/widget/TextView;I)V
        //    94: astore          7
        //    96: getstatic       android/support/v4/provider/FontsContractCompat.sLock:Ljava/lang/Object;
        //    99: astore_0       
        //   100: aload_0        
        //   101: monitorenter   
        //   102: getstatic       android/support/v4/provider/FontsContractCompat.sPendingReplies:Landroid/support/v4/util/SimpleArrayMap;
        //   105: aload           6
        //   107: invokevirtual   android/support/v4/util/SimpleArrayMap.containsKey:(Ljava/lang/Object;)Z
        //   110: ifne            198
        //   113: new             Ljava/util/ArrayList;
        //   116: astore_2       
        //   117: aload_2        
        //   118: invokespecial   java/util/ArrayList.<init>:()V
        //   121: aload_2        
        //   122: aload           7
        //   124: invokevirtual   java/util/ArrayList.add:(Ljava/lang/Object;)Z
        //   127: pop            
        //   128: getstatic       android/support/v4/provider/FontsContractCompat.sPendingReplies:Landroid/support/v4/util/SimpleArrayMap;
        //   131: aload           6
        //   133: aload_2        
        //   134: invokevirtual   android/support/v4/util/SimpleArrayMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   137: pop            
        //   138: aload_0        
        //   139: monitorexit    
        //   140: getstatic       android/support/v4/provider/FontsContractCompat.sBackgroundThread:Landroid/support/v4/provider/SelfDestructiveThread;
        //   143: aload_1        
        //   144: new             Landroid/support/v4/provider/FontsContractCompat$3;
        //   147: dup            
        //   148: aload           6
        //   150: invokespecial   android/support/v4/provider/FontsContractCompat$3.<init>:(Ljava/lang/String;)V
        //   153: invokevirtual   android/support/v4/provider/SelfDestructiveThread.postAndReply:(Ljava/util/concurrent/Callable;Landroid/support/v4/provider/SelfDestructiveThread$ReplyCallback;)V
        //   156: aconst_null    
        //   157: areturn        
        //   158: aload           7
        //   160: areturn        
        //   161: iconst_1       
        //   162: istore_3       
        //   163: goto            54
        //   166: iload           4
        //   168: iconst_m1      
        //   169: if_icmpne       58
        //   172: aload_0        
        //   173: aload_1        
        //   174: iload           5
        //   176: invokestatic    android/support/v4/provider/FontsContractCompat.getFontInternal:(Landroid/content/Context;Landroid/support/v4/provider/FontRequest;I)Landroid/graphics/Typeface;
        //   179: areturn        
        //   180: getstatic       android/support/v4/provider/FontsContractCompat.sBackgroundThread:Landroid/support/v4/provider/SelfDestructiveThread;
        //   183: aload_1        
        //   184: iload           4
        //   186: invokevirtual   android/support/v4/provider/SelfDestructiveThread.postAndWait:(Ljava/util/concurrent/Callable;I)Ljava/lang/Object;
        //   189: checkcast       Landroid/graphics/Typeface;
        //   192: astore_0       
        //   193: aload_0        
        //   194: areturn        
        //   195: astore_0       
        //   196: aconst_null    
        //   197: areturn        
        //   198: getstatic       android/support/v4/provider/FontsContractCompat.sPendingReplies:Landroid/support/v4/util/SimpleArrayMap;
        //   201: aload           6
        //   203: invokevirtual   android/support/v4/util/SimpleArrayMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   206: checkcast       Ljava/util/ArrayList;
        //   209: aload           7
        //   211: invokevirtual   java/util/ArrayList.add:(Ljava/lang/Object;)Z
        //   214: pop            
        //   215: aload_0        
        //   216: monitorexit    
        //   217: aconst_null    
        //   218: areturn        
        //   219: astore_1       
        //   220: aload_0        
        //   221: monitorexit    
        //   222: aload_1        
        //   223: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                            
        //  -----  -----  -----  -----  --------------------------------
        //  102    140    219    224    Any
        //  180    193    195    198    Ljava/lang/InterruptedException;
        //  198    217    219    224    Any
        //  220    222    219    224    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: cmpne:boolean(p4:int, ldc:int(-1))
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
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
    
    @Nullable
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    @VisibleForTesting
    public static ProviderInfo getProvider(@NonNull final PackageManager packageManager, @NonNull final FontRequest fontRequest, @Nullable final Resources resources) throws PackageManager$NameNotFoundException {
        final String providerAuthority = fontRequest.getProviderAuthority();
        final ProviderInfo resolveContentProvider = packageManager.resolveContentProvider(providerAuthority, 0);
        if (resolveContentProvider == null) {
            throw new PackageManager$NameNotFoundException("No package found for authority: " + providerAuthority);
        }
        if (resolveContentProvider.packageName.equals(fontRequest.getProviderPackage())) {
            final List<byte[]> convertToByteArrayList = convertToByteArrayList(packageManager.getPackageInfo(resolveContentProvider.packageName, 64).signatures);
            Collections.sort((List<Object>)convertToByteArrayList, (Comparator<? super Object>)FontsContractCompat.sByteArrayComparator);
            final List<List<byte[]>> certificates = getCertificates(fontRequest, resources);
            for (int i = 0; i < certificates.size(); ++i) {
                final ArrayList list = new ArrayList<byte[]>((Collection<? extends T>)certificates.get(i));
                Collections.sort((List<E>)list, (Comparator<? super E>)FontsContractCompat.sByteArrayComparator);
                if (equalsByteArrayList(convertToByteArrayList, (List<byte[]>)list)) {
                    return resolveContentProvider;
                }
            }
            return null;
        }
        throw new PackageManager$NameNotFoundException("Found content provider " + providerAuthority + ", but package was not " + fontRequest.getProviderPackage());
    }
    
    @RequiresApi(19)
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public static Map<Uri, ByteBuffer> prepareFontData(final Context context, final FontInfo[] array, final CancellationSignal cancellationSignal) {
        int i = 0;
        final HashMap m = new HashMap();
        while (i < array.length) {
            final FontInfo fontInfo = array[i];
            if (fontInfo.getResultCode() == 0) {
                final Uri uri = fontInfo.getUri();
                if (!m.containsKey(uri)) {
                    m.put(uri, TypefaceCompatUtil.mmap(context, cancellationSignal, uri));
                }
            }
            ++i;
        }
        return (Map<Uri, ByteBuffer>)Collections.unmodifiableMap((Map<?, ?>)m);
    }
    
    public static void requestFont(@NonNull final Context context, @NonNull final FontRequest fontRequest, @NonNull final FontRequestCallback fontRequestCallback, @NonNull final Handler handler) {
        handler.post((Runnable)new Runnable() {
            final /* synthetic */ Handler val$callerThreadHandler = new Handler();
            
            @Override
            public void run() {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     1: istore_1       
                //     2: aload_0        
                //     3: getfield        android/support/v4/provider/FontsContractCompat$4.val$context:Landroid/content/Context;
                //     6: aconst_null    
                //     7: aload_0        
                //     8: getfield        android/support/v4/provider/FontsContractCompat$4.val$request:Landroid/support/v4/provider/FontRequest;
                //    11: invokestatic    android/support/v4/provider/FontsContractCompat.fetchFonts:(Landroid/content/Context;Landroid/os/CancellationSignal;Landroid/support/v4/provider/FontRequest;)Landroid/support/v4/provider/FontsContractCompat$FontFamilyResult;
                //    14: astore_2       
                //    15: aload_2        
                //    16: invokevirtual   android/support/v4/provider/FontsContractCompat$FontFamilyResult.getStatusCode:()I
                //    19: ifne            66
                //    22: aload_2        
                //    23: invokevirtual   android/support/v4/provider/FontsContractCompat$FontFamilyResult.getFonts:()[Landroid/support/v4/provider/FontsContractCompat$FontInfo;
                //    26: astore_2       
                //    27: aload_2        
                //    28: ifnonnull       143
                //    31: aload_0        
                //    32: getfield        android/support/v4/provider/FontsContractCompat$4.val$callerThreadHandler:Landroid/os/Handler;
                //    35: new             Landroid/support/v4/provider/FontsContractCompat$4$5;
                //    38: dup            
                //    39: aload_0        
                //    40: invokespecial   android/support/v4/provider/FontsContractCompat$4$5.<init>:(Landroid/support/v4/provider/FontsContractCompat$4;)V
                //    43: invokevirtual   android/os/Handler.post:(Ljava/lang/Runnable;)Z
                //    46: pop            
                //    47: return         
                //    48: astore_2       
                //    49: aload_0        
                //    50: getfield        android/support/v4/provider/FontsContractCompat$4.val$callerThreadHandler:Landroid/os/Handler;
                //    53: new             Landroid/support/v4/provider/FontsContractCompat$4$1;
                //    56: dup            
                //    57: aload_0        
                //    58: invokespecial   android/support/v4/provider/FontsContractCompat$4$1.<init>:(Landroid/support/v4/provider/FontsContractCompat$4;)V
                //    61: invokevirtual   android/os/Handler.post:(Ljava/lang/Runnable;)Z
                //    64: pop            
                //    65: return         
                //    66: aload_2        
                //    67: invokevirtual   android/support/v4/provider/FontsContractCompat$FontFamilyResult.getStatusCode:()I
                //    70: tableswitch {
                //                2: 109
                //                3: 126
                //          default: 92
                //        }
                //    92: aload_0        
                //    93: getfield        android/support/v4/provider/FontsContractCompat$4.val$callerThreadHandler:Landroid/os/Handler;
                //    96: new             Landroid/support/v4/provider/FontsContractCompat$4$4;
                //    99: dup            
                //   100: aload_0        
                //   101: invokespecial   android/support/v4/provider/FontsContractCompat$4$4.<init>:(Landroid/support/v4/provider/FontsContractCompat$4;)V
                //   104: invokevirtual   android/os/Handler.post:(Ljava/lang/Runnable;)Z
                //   107: pop            
                //   108: return         
                //   109: aload_0        
                //   110: getfield        android/support/v4/provider/FontsContractCompat$4.val$callerThreadHandler:Landroid/os/Handler;
                //   113: new             Landroid/support/v4/provider/FontsContractCompat$4$2;
                //   116: dup            
                //   117: aload_0        
                //   118: invokespecial   android/support/v4/provider/FontsContractCompat$4$2.<init>:(Landroid/support/v4/provider/FontsContractCompat$4;)V
                //   121: invokevirtual   android/os/Handler.post:(Ljava/lang/Runnable;)Z
                //   124: pop            
                //   125: return         
                //   126: aload_0        
                //   127: getfield        android/support/v4/provider/FontsContractCompat$4.val$callerThreadHandler:Landroid/os/Handler;
                //   130: new             Landroid/support/v4/provider/FontsContractCompat$4$3;
                //   133: dup            
                //   134: aload_0        
                //   135: invokespecial   android/support/v4/provider/FontsContractCompat$4$3.<init>:(Landroid/support/v4/provider/FontsContractCompat$4;)V
                //   138: invokevirtual   android/os/Handler.post:(Ljava/lang/Runnable;)Z
                //   141: pop            
                //   142: return         
                //   143: aload_2        
                //   144: arraylength    
                //   145: ifeq            31
                //   148: aload_2        
                //   149: arraylength    
                //   150: istore_3       
                //   151: iload_1        
                //   152: iload_3        
                //   153: if_icmplt       188
                //   156: aload_0        
                //   157: getfield        android/support/v4/provider/FontsContractCompat$4.val$context:Landroid/content/Context;
                //   160: aconst_null    
                //   161: aload_2        
                //   162: invokestatic    android/support/v4/provider/FontsContractCompat.buildTypeface:(Landroid/content/Context;Landroid/os/CancellationSignal;[Landroid/support/v4/provider/FontsContractCompat$FontInfo;)Landroid/graphics/Typeface;
                //   165: astore_2       
                //   166: aload_2        
                //   167: ifnull          254
                //   170: aload_0        
                //   171: getfield        android/support/v4/provider/FontsContractCompat$4.val$callerThreadHandler:Landroid/os/Handler;
                //   174: new             Landroid/support/v4/provider/FontsContractCompat$4$9;
                //   177: dup            
                //   178: aload_0        
                //   179: aload_2        
                //   180: invokespecial   android/support/v4/provider/FontsContractCompat$4$9.<init>:(Landroid/support/v4/provider/FontsContractCompat$4;Landroid/graphics/Typeface;)V
                //   183: invokevirtual   android/os/Handler.post:(Ljava/lang/Runnable;)Z
                //   186: pop            
                //   187: return         
                //   188: aload_2        
                //   189: iload_1        
                //   190: aaload         
                //   191: astore          4
                //   193: aload           4
                //   195: invokevirtual   android/support/v4/provider/FontsContractCompat$FontInfo.getResultCode:()I
                //   198: ifne            207
                //   201: iinc            1, 1
                //   204: goto            151
                //   207: aload           4
                //   209: invokevirtual   android/support/v4/provider/FontsContractCompat$FontInfo.getResultCode:()I
                //   212: istore_1       
                //   213: iload_1        
                //   214: iflt            235
                //   217: aload_0        
                //   218: getfield        android/support/v4/provider/FontsContractCompat$4.val$callerThreadHandler:Landroid/os/Handler;
                //   221: new             Landroid/support/v4/provider/FontsContractCompat$4$7;
                //   224: dup            
                //   225: aload_0        
                //   226: iload_1        
                //   227: invokespecial   android/support/v4/provider/FontsContractCompat$4$7.<init>:(Landroid/support/v4/provider/FontsContractCompat$4;I)V
                //   230: invokevirtual   android/os/Handler.post:(Ljava/lang/Runnable;)Z
                //   233: pop            
                //   234: return         
                //   235: aload_0        
                //   236: getfield        android/support/v4/provider/FontsContractCompat$4.val$callerThreadHandler:Landroid/os/Handler;
                //   239: new             Landroid/support/v4/provider/FontsContractCompat$4$6;
                //   242: dup            
                //   243: aload_0        
                //   244: invokespecial   android/support/v4/provider/FontsContractCompat$4$6.<init>:(Landroid/support/v4/provider/FontsContractCompat$4;)V
                //   247: invokevirtual   android/os/Handler.post:(Ljava/lang/Runnable;)Z
                //   250: pop            
                //   251: goto            234
                //   254: aload_0        
                //   255: getfield        android/support/v4/provider/FontsContractCompat$4.val$callerThreadHandler:Landroid/os/Handler;
                //   258: new             Landroid/support/v4/provider/FontsContractCompat$4$8;
                //   261: dup            
                //   262: aload_0        
                //   263: invokespecial   android/support/v4/provider/FontsContractCompat$4$8.<init>:(Landroid/support/v4/provider/FontsContractCompat$4;)V
                //   266: invokevirtual   android/os/Handler.post:(Ljava/lang/Runnable;)Z
                //   269: pop            
                //   270: return         
                //    Exceptions:
                //  Try           Handler
                //  Start  End    Start  End    Type                                                     
                //  -----  -----  -----  -----  ---------------------------------------------------------
                //  2      15     48     66     Landroid/content/pm/PackageManager$NameNotFoundException;
                // 
                // The error that occurred was:
                // 
                // java.lang.IllegalStateException: Expression is linked from several locations: cmpne:boolean(arraylength:int(var_2_1A:FontInfo[]), ldc:int(0))
                //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
                //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
                //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
                //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
                //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformCall(AstMethodBodyBuilder.java:1151)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:993)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:548)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:377)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:318)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:213)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
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
        });
    }
    
    public static final class Columns implements BaseColumns
    {
        public static final String FILE_ID = "file_id";
        public static final String ITALIC = "font_italic";
        public static final String RESULT_CODE = "result_code";
        public static final int RESULT_CODE_FONT_NOT_FOUND = 1;
        public static final int RESULT_CODE_FONT_UNAVAILABLE = 2;
        public static final int RESULT_CODE_MALFORMED_QUERY = 3;
        public static final int RESULT_CODE_OK = 0;
        public static final String TTC_INDEX = "font_ttc_index";
        public static final String VARIATION_SETTINGS = "font_variation_settings";
        public static final String WEIGHT = "font_weight";
    }
    
    public static class FontFamilyResult
    {
        public static final int STATUS_OK = 0;
        public static final int STATUS_UNEXPECTED_DATA_PROVIDED = 2;
        public static final int STATUS_WRONG_CERTIFICATES = 1;
        private final FontInfo[] mFonts;
        private final int mStatusCode;
        
        @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
        public FontFamilyResult(final int mStatusCode, @Nullable final FontInfo[] mFonts) {
            this.mStatusCode = mStatusCode;
            this.mFonts = mFonts;
        }
        
        public FontInfo[] getFonts() {
            return this.mFonts;
        }
        
        public int getStatusCode() {
            return this.mStatusCode;
        }
        
        @Retention(RetentionPolicy.SOURCE)
        @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
        @interface FontResultStatus {
        }
    }
    
    public static class FontInfo
    {
        private final boolean mItalic;
        private final int mResultCode;
        private final int mTtcIndex;
        private final Uri mUri;
        private final int mWeight;
        
        @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
        public FontInfo(@NonNull final Uri uri, @IntRange(from = 0L) final int mTtcIndex, @IntRange(from = 1L, to = 1000L) final int mWeight, final boolean mItalic, final int mResultCode) {
            this.mUri = Preconditions.checkNotNull(uri);
            this.mTtcIndex = mTtcIndex;
            this.mWeight = mWeight;
            this.mItalic = mItalic;
            this.mResultCode = mResultCode;
        }
        
        public int getResultCode() {
            return this.mResultCode;
        }
        
        @IntRange(from = 0L)
        public int getTtcIndex() {
            return this.mTtcIndex;
        }
        
        @NonNull
        public Uri getUri() {
            return this.mUri;
        }
        
        @IntRange(from = 1L, to = 1000L)
        public int getWeight() {
            return this.mWeight;
        }
        
        public boolean isItalic() {
            return this.mItalic;
        }
    }
    
    public static class FontRequestCallback
    {
        public static final int FAIL_REASON_FONT_LOAD_ERROR = -3;
        public static final int FAIL_REASON_FONT_NOT_FOUND = 1;
        public static final int FAIL_REASON_FONT_UNAVAILABLE = 2;
        public static final int FAIL_REASON_MALFORMED_QUERY = 3;
        public static final int FAIL_REASON_PROVIDER_NOT_FOUND = -1;
        public static final int FAIL_REASON_WRONG_CERTIFICATES = -2;
        
        public void onTypefaceRequestFailed(final int n) {
        }
        
        public void onTypefaceRetrieved(final Typeface typeface) {
        }
        
        @Retention(RetentionPolicy.SOURCE)
        @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
        @interface FontRequestFailReason {
        }
    }
}

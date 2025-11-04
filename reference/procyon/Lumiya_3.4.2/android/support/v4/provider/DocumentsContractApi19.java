// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.provider;

import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.net.Uri;
import android.content.Context;
import android.support.annotation.RequiresApi;

@RequiresApi(19)
class DocumentsContractApi19
{
    private static final int FLAG_VIRTUAL_DOCUMENT = 512;
    private static final String TAG = "DocumentFile";
    
    public static boolean canRead(final Context context, final Uri uri) {
        return context.checkCallingOrSelfUriPermission(uri, 1) == 0 && !TextUtils.isEmpty((CharSequence)getRawType(context, uri));
    }
    
    public static boolean canWrite(final Context context, final Uri uri) {
        if (context.checkCallingOrSelfUriPermission(uri, 2) == 0) {
            final String rawType = getRawType(context, uri);
            final int queryForInt = queryForInt(context, uri, "flags", 0);
            return !TextUtils.isEmpty((CharSequence)rawType) && ((queryForInt & 0x4) != 0x0 || ("vnd.android.document/directory".equals(rawType) && (queryForInt & 0x8) != 0x0) || (!TextUtils.isEmpty((CharSequence)rawType) && (queryForInt & 0x2) != 0x0));
        }
        return false;
    }
    
    private static void closeQuietly(final AutoCloseable autoCloseable) {
        if (autoCloseable != null) {
            try {
                autoCloseable.close();
            }
            catch (final RuntimeException ex) {
                throw ex;
            }
            catch (final Exception ex2) {}
        }
    }
    
    public static boolean delete(final Context context, final Uri uri) {
        try {
            return DocumentsContract.deleteDocument(context.getContentResolver(), uri);
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    public static boolean exists(final Context p0, final Uri p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokevirtual   android/content/Context.getContentResolver:()Landroid/content/ContentResolver;
        //     4: astore_0       
        //     5: aload_0        
        //     6: aload_1        
        //     7: iconst_1       
        //     8: anewarray       Ljava/lang/String;
        //    11: dup            
        //    12: iconst_0       
        //    13: ldc             "document_id"
        //    15: aastore        
        //    16: aconst_null    
        //    17: aconst_null    
        //    18: aconst_null    
        //    19: invokevirtual   android/content/ContentResolver.query:(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
        //    22: astore_1       
        //    23: aload_1        
        //    24: astore_0       
        //    25: aload_1        
        //    26: invokeinterface android/database/Cursor.getCount:()I
        //    31: istore_2       
        //    32: iload_2        
        //    33: ifgt            44
        //    36: iconst_0       
        //    37: istore_3       
        //    38: aload_1        
        //    39: invokestatic    android/support/v4/provider/DocumentsContractApi19.closeQuietly:(Ljava/lang/AutoCloseable;)V
        //    42: iload_3        
        //    43: ireturn        
        //    44: iconst_1       
        //    45: istore_3       
        //    46: goto            38
        //    49: astore          4
        //    51: aconst_null    
        //    52: astore_1       
        //    53: aload_1        
        //    54: astore_0       
        //    55: new             Ljava/lang/StringBuilder;
        //    58: astore          5
        //    60: aload_1        
        //    61: astore_0       
        //    62: aload           5
        //    64: invokespecial   java/lang/StringBuilder.<init>:()V
        //    67: aload_1        
        //    68: astore_0       
        //    69: ldc             "DocumentFile"
        //    71: aload           5
        //    73: ldc             "Failed query: "
        //    75: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    78: aload           4
        //    80: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //    83: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    86: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
        //    89: pop            
        //    90: aload_1        
        //    91: invokestatic    android/support/v4/provider/DocumentsContractApi19.closeQuietly:(Ljava/lang/AutoCloseable;)V
        //    94: iconst_0       
        //    95: ireturn        
        //    96: astore_1       
        //    97: aconst_null    
        //    98: astore_0       
        //    99: aload_0        
        //   100: invokestatic    android/support/v4/provider/DocumentsContractApi19.closeQuietly:(Ljava/lang/AutoCloseable;)V
        //   103: aload_1        
        //   104: athrow         
        //   105: astore_1       
        //   106: goto            99
        //   109: astore          4
        //   111: goto            53
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  5      23     49     53     Ljava/lang/Exception;
        //  5      23     96     99     Any
        //  25     32     109    114    Ljava/lang/Exception;
        //  25     32     105    109    Any
        //  55     60     105    109    Any
        //  62     67     105    109    Any
        //  69     90     105    109    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0038:
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
    
    public static long getFlags(final Context context, final Uri uri) {
        return queryForLong(context, uri, "flags", 0L);
    }
    
    public static String getName(final Context context, final Uri uri) {
        return queryForString(context, uri, "_display_name", null);
    }
    
    private static String getRawType(final Context context, final Uri uri) {
        return queryForString(context, uri, "mime_type", null);
    }
    
    public static String getType(final Context context, final Uri uri) {
        final String rawType = getRawType(context, uri);
        if (!"vnd.android.document/directory".equals(rawType)) {
            return rawType;
        }
        return null;
    }
    
    public static boolean isDirectory(final Context context, final Uri uri) {
        return "vnd.android.document/directory".equals(getRawType(context, uri));
    }
    
    public static boolean isDocumentUri(final Context context, final Uri uri) {
        return DocumentsContract.isDocumentUri(context, uri);
    }
    
    public static boolean isFile(final Context context, final Uri uri) {
        final String rawType = getRawType(context, uri);
        return !"vnd.android.document/directory".equals(rawType) && !TextUtils.isEmpty((CharSequence)rawType);
    }
    
    public static boolean isVirtual(final Context context, final Uri uri) {
        boolean b = false;
        if (isDocumentUri(context, uri)) {
            if ((getFlags(context, uri) & 0x200L) != 0x0L) {
                b = true;
            }
            return b;
        }
        return false;
    }
    
    public static long lastModified(final Context context, final Uri uri) {
        return queryForLong(context, uri, "last_modified", 0L);
    }
    
    public static long length(final Context context, final Uri uri) {
        return queryForLong(context, uri, "_size", 0L);
    }
    
    private static int queryForInt(final Context context, final Uri uri, final String s, final int n) {
        return (int)queryForLong(context, uri, s, n);
    }
    
    private static long queryForLong(final Context p0, final Uri p1, final String p2, final long p3) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokevirtual   android/content/Context.getContentResolver:()Landroid/content/ContentResolver;
        //     4: astore_0       
        //     5: aload_0        
        //     6: aload_1        
        //     7: iconst_1       
        //     8: anewarray       Ljava/lang/String;
        //    11: dup            
        //    12: iconst_0       
        //    13: aload_2        
        //    14: aastore        
        //    15: aconst_null    
        //    16: aconst_null    
        //    17: aconst_null    
        //    18: invokevirtual   android/content/ContentResolver.query:(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
        //    21: astore_1       
        //    22: aload_1        
        //    23: astore_0       
        //    24: aload_1        
        //    25: invokeinterface android/database/Cursor.moveToFirst:()Z
        //    30: istore          5
        //    32: iload           5
        //    34: ifne            43
        //    37: aload_1        
        //    38: invokestatic    android/support/v4/provider/DocumentsContractApi19.closeQuietly:(Ljava/lang/AutoCloseable;)V
        //    41: lload_3        
        //    42: lreturn        
        //    43: aload_1        
        //    44: astore_0       
        //    45: aload_1        
        //    46: iconst_0       
        //    47: invokeinterface android/database/Cursor.isNull:(I)Z
        //    52: ifne            37
        //    55: aload_1        
        //    56: astore_0       
        //    57: aload_1        
        //    58: iconst_0       
        //    59: invokeinterface android/database/Cursor.getLong:(I)J
        //    64: lstore          6
        //    66: aload_1        
        //    67: invokestatic    android/support/v4/provider/DocumentsContractApi19.closeQuietly:(Ljava/lang/AutoCloseable;)V
        //    70: lload           6
        //    72: lreturn        
        //    73: astore_2       
        //    74: aconst_null    
        //    75: astore_1       
        //    76: aload_1        
        //    77: astore_0       
        //    78: new             Ljava/lang/StringBuilder;
        //    81: astore          8
        //    83: aload_1        
        //    84: astore_0       
        //    85: aload           8
        //    87: invokespecial   java/lang/StringBuilder.<init>:()V
        //    90: aload_1        
        //    91: astore_0       
        //    92: ldc             "DocumentFile"
        //    94: aload           8
        //    96: ldc             "Failed query: "
        //    98: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   101: aload_2        
        //   102: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   105: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   108: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
        //   111: pop            
        //   112: aload_1        
        //   113: invokestatic    android/support/v4/provider/DocumentsContractApi19.closeQuietly:(Ljava/lang/AutoCloseable;)V
        //   116: lload_3        
        //   117: lreturn        
        //   118: astore_1       
        //   119: aconst_null    
        //   120: astore_0       
        //   121: aload_0        
        //   122: invokestatic    android/support/v4/provider/DocumentsContractApi19.closeQuietly:(Ljava/lang/AutoCloseable;)V
        //   125: aload_1        
        //   126: athrow         
        //   127: astore_1       
        //   128: goto            121
        //   131: astore_2       
        //   132: goto            76
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  5      22     73     76     Ljava/lang/Exception;
        //  5      22     118    121    Any
        //  24     32     131    135    Ljava/lang/Exception;
        //  24     32     127    131    Any
        //  45     55     131    135    Ljava/lang/Exception;
        //  45     55     127    131    Any
        //  57     66     131    135    Ljava/lang/Exception;
        //  57     66     127    131    Any
        //  78     83     127    131    Any
        //  85     90     127    131    Any
        //  92     112    127    131    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0037:
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
    
    private static String queryForString(final Context p0, final Uri p1, final String p2, final String p3) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokevirtual   android/content/Context.getContentResolver:()Landroid/content/ContentResolver;
        //     4: astore_0       
        //     5: aload_0        
        //     6: aload_1        
        //     7: iconst_1       
        //     8: anewarray       Ljava/lang/String;
        //    11: dup            
        //    12: iconst_0       
        //    13: aload_2        
        //    14: aastore        
        //    15: aconst_null    
        //    16: aconst_null    
        //    17: aconst_null    
        //    18: invokevirtual   android/content/ContentResolver.query:(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
        //    21: astore_1       
        //    22: aload_1        
        //    23: astore_0       
        //    24: aload_1        
        //    25: invokeinterface android/database/Cursor.moveToFirst:()Z
        //    30: istore          4
        //    32: iload           4
        //    34: ifne            43
        //    37: aload_1        
        //    38: invokestatic    android/support/v4/provider/DocumentsContractApi19.closeQuietly:(Ljava/lang/AutoCloseable;)V
        //    41: aload_3        
        //    42: areturn        
        //    43: aload_1        
        //    44: astore_0       
        //    45: aload_1        
        //    46: iconst_0       
        //    47: invokeinterface android/database/Cursor.isNull:(I)Z
        //    52: ifne            37
        //    55: aload_1        
        //    56: astore_0       
        //    57: aload_1        
        //    58: iconst_0       
        //    59: invokeinterface android/database/Cursor.getString:(I)Ljava/lang/String;
        //    64: astore_2       
        //    65: aload_1        
        //    66: invokestatic    android/support/v4/provider/DocumentsContractApi19.closeQuietly:(Ljava/lang/AutoCloseable;)V
        //    69: aload_2        
        //    70: areturn        
        //    71: astore_2       
        //    72: aconst_null    
        //    73: astore_1       
        //    74: aload_1        
        //    75: astore_0       
        //    76: new             Ljava/lang/StringBuilder;
        //    79: astore          5
        //    81: aload_1        
        //    82: astore_0       
        //    83: aload           5
        //    85: invokespecial   java/lang/StringBuilder.<init>:()V
        //    88: aload_1        
        //    89: astore_0       
        //    90: ldc             "DocumentFile"
        //    92: aload           5
        //    94: ldc             "Failed query: "
        //    96: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    99: aload_2        
        //   100: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   103: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   106: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
        //   109: pop            
        //   110: aload_1        
        //   111: invokestatic    android/support/v4/provider/DocumentsContractApi19.closeQuietly:(Ljava/lang/AutoCloseable;)V
        //   114: aload_3        
        //   115: areturn        
        //   116: astore_1       
        //   117: aconst_null    
        //   118: astore_0       
        //   119: aload_0        
        //   120: invokestatic    android/support/v4/provider/DocumentsContractApi19.closeQuietly:(Ljava/lang/AutoCloseable;)V
        //   123: aload_1        
        //   124: athrow         
        //   125: astore_1       
        //   126: goto            119
        //   129: astore_2       
        //   130: goto            74
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  5      22     71     74     Ljava/lang/Exception;
        //  5      22     116    119    Any
        //  24     32     129    133    Ljava/lang/Exception;
        //  24     32     125    129    Any
        //  45     55     129    133    Ljava/lang/Exception;
        //  45     55     125    129    Any
        //  57     65     129    133    Ljava/lang/Exception;
        //  57     65     125    129    Any
        //  76     81     125    129    Any
        //  83     88     125    129    Any
        //  90     110    125    129    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0037:
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
}

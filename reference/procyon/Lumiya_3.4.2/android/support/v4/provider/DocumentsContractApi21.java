// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.provider;

import android.provider.DocumentsContract;
import android.net.Uri;
import android.content.Context;
import android.support.annotation.RequiresApi;

@RequiresApi(21)
class DocumentsContractApi21
{
    private static final String TAG = "DocumentFile";
    
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
    
    public static Uri createDirectory(final Context context, final Uri uri, final String s) {
        return createFile(context, uri, "vnd.android.document/directory", s);
    }
    
    public static Uri createFile(final Context context, final Uri uri, final String s, final String s2) {
        try {
            return DocumentsContract.createDocument(context.getContentResolver(), uri, s, s2);
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public static Uri[] listFiles(final Context p0, final Uri p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokevirtual   android/content/Context.getContentResolver:()Landroid/content/ContentResolver;
        //     4: astore_0       
        //     5: aload_1        
        //     6: aload_1        
        //     7: invokestatic    android/provider/DocumentsContract.getDocumentId:(Landroid/net/Uri;)Ljava/lang/String;
        //    10: invokestatic    android/provider/DocumentsContract.buildChildDocumentsUriUsingTree:(Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;
        //    13: astore_2       
        //    14: new             Ljava/util/ArrayList;
        //    17: dup            
        //    18: invokespecial   java/util/ArrayList.<init>:()V
        //    21: astore_3       
        //    22: aload_0        
        //    23: aload_2        
        //    24: iconst_1       
        //    25: anewarray       Ljava/lang/String;
        //    28: dup            
        //    29: iconst_0       
        //    30: ldc             "document_id"
        //    32: aastore        
        //    33: aconst_null    
        //    34: aconst_null    
        //    35: aconst_null    
        //    36: invokevirtual   android/content/ContentResolver.query:(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
        //    39: astore_2       
        //    40: aload_2        
        //    41: astore_0       
        //    42: aload_2        
        //    43: invokeinterface android/database/Cursor.moveToNext:()Z
        //    48: istore          4
        //    50: iload           4
        //    52: ifne            74
        //    55: aload_2        
        //    56: invokestatic    android/support/v4/provider/DocumentsContractApi21.closeQuietly:(Ljava/lang/AutoCloseable;)V
        //    59: aload_3        
        //    60: aload_3        
        //    61: invokevirtual   java/util/ArrayList.size:()I
        //    64: anewarray       Landroid/net/Uri;
        //    67: invokevirtual   java/util/ArrayList.toArray:([Ljava/lang/Object;)[Ljava/lang/Object;
        //    70: checkcast       [Landroid/net/Uri;
        //    73: areturn        
        //    74: aload_2        
        //    75: astore_0       
        //    76: aload_3        
        //    77: aload_1        
        //    78: aload_2        
        //    79: iconst_0       
        //    80: invokeinterface android/database/Cursor.getString:(I)Ljava/lang/String;
        //    85: invokestatic    android/provider/DocumentsContract.buildDocumentUriUsingTree:(Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;
        //    88: invokevirtual   java/util/ArrayList.add:(Ljava/lang/Object;)Z
        //    91: pop            
        //    92: goto            40
        //    95: astore_0       
        //    96: aload_2        
        //    97: astore_1       
        //    98: aload_0        
        //    99: astore_2       
        //   100: aload_1        
        //   101: astore_0       
        //   102: new             Ljava/lang/StringBuilder;
        //   105: astore          5
        //   107: aload_1        
        //   108: astore_0       
        //   109: aload           5
        //   111: invokespecial   java/lang/StringBuilder.<init>:()V
        //   114: aload_1        
        //   115: astore_0       
        //   116: ldc             "DocumentFile"
        //   118: aload           5
        //   120: ldc             "Failed query: "
        //   122: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   125: aload_2        
        //   126: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   129: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   132: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
        //   135: pop            
        //   136: aload_1        
        //   137: invokestatic    android/support/v4/provider/DocumentsContractApi21.closeQuietly:(Ljava/lang/AutoCloseable;)V
        //   140: goto            59
        //   143: astore_1       
        //   144: aconst_null    
        //   145: astore_0       
        //   146: aload_0        
        //   147: invokestatic    android/support/v4/provider/DocumentsContractApi21.closeQuietly:(Ljava/lang/AutoCloseable;)V
        //   150: aload_1        
        //   151: athrow         
        //   152: astore_1       
        //   153: goto            146
        //   156: astore_2       
        //   157: aconst_null    
        //   158: astore_1       
        //   159: goto            100
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  22     40     156    162    Ljava/lang/Exception;
        //  22     40     143    146    Any
        //  42     50     95     100    Ljava/lang/Exception;
        //  42     50     152    156    Any
        //  76     92     95     100    Ljava/lang/Exception;
        //  76     92     152    156    Any
        //  102    107    152    156    Any
        //  109    114    152    156    Any
        //  116    136    152    156    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0059:
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
    
    public static Uri prepareTreeUri(final Uri uri) {
        return DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri));
    }
    
    public static Uri renameTo(final Context context, final Uri uri, final String s) {
        try {
            return DocumentsContract.renameDocument(context.getContentResolver(), uri, s);
        }
        catch (final Exception ex) {
            return null;
        }
    }
}

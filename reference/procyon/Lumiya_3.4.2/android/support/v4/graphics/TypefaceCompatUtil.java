// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.graphics;

import android.net.Uri;
import android.os.CancellationSignal;
import android.os.Process;
import java.io.InputStream;
import android.support.annotation.RequiresApi;
import java.io.File;
import java.nio.ByteBuffer;
import android.content.res.Resources;
import android.content.Context;
import java.io.IOException;
import java.io.Closeable;
import android.support.annotation.RestrictTo;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class TypefaceCompatUtil
{
    private static final String CACHE_FILE_PREFIX = ".font";
    private static final String TAG = "TypefaceCompatUtil";
    
    private TypefaceCompatUtil() {
    }
    
    public static void closeQuietly(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    @RequiresApi(19)
    public static ByteBuffer copyToDirectBuffer(Context tempFile, final Resources resources, final int n) {
        tempFile = (Context)getTempFile(tempFile);
        Label_0030: {
            if (tempFile == null) {
                break Label_0030;
            }
            try {
                if (copyToFile((File)tempFile, resources, n)) {
                    return mmap((File)tempFile);
                }
                return null;
                return null;
            }
            finally {
                ((File)tempFile).delete();
            }
        }
    }
    
    public static boolean copyToFile(final File file, final Resources resources, final int n) {
        Closeable openRawResource = null;
        try {
            return copyToFile(file, (InputStream)(openRawResource = resources.openRawResource(n)));
        }
        finally {
            closeQuietly(openRawResource);
        }
    }
    
    public static boolean copyToFile(final File p0, final InputStream p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: astore_2       
        //     4: aload_2        
        //     5: aload_0        
        //     6: iconst_0       
        //     7: invokespecial   java/io/FileOutputStream.<init>:(Ljava/io/File;Z)V
        //    10: aload_2        
        //    11: astore_0       
        //    12: sipush          1024
        //    15: newarray        B
        //    17: astore_3       
        //    18: aload_2        
        //    19: astore_0       
        //    20: aload_1        
        //    21: aload_3        
        //    22: invokevirtual   java/io/InputStream.read:([B)I
        //    25: istore          4
        //    27: iload           4
        //    29: iconst_m1      
        //    30: if_icmpne       39
        //    33: aload_2        
        //    34: invokestatic    android/support/v4/graphics/TypefaceCompatUtil.closeQuietly:(Ljava/io/Closeable;)V
        //    37: iconst_1       
        //    38: ireturn        
        //    39: aload_2        
        //    40: astore_0       
        //    41: aload_2        
        //    42: aload_3        
        //    43: iconst_0       
        //    44: iload           4
        //    46: invokevirtual   java/io/FileOutputStream.write:([BII)V
        //    49: goto            18
        //    52: astore_0       
        //    53: aload_2        
        //    54: astore_1       
        //    55: aload_0        
        //    56: astore_2       
        //    57: aload_1        
        //    58: astore_0       
        //    59: new             Ljava/lang/StringBuilder;
        //    62: astore_3       
        //    63: aload_1        
        //    64: astore_0       
        //    65: aload_3        
        //    66: invokespecial   java/lang/StringBuilder.<init>:()V
        //    69: aload_1        
        //    70: astore_0       
        //    71: ldc             "TypefaceCompatUtil"
        //    73: aload_3        
        //    74: ldc             "Error copying resource contents to temp file: "
        //    76: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    79: aload_2        
        //    80: invokevirtual   java/io/IOException.getMessage:()Ljava/lang/String;
        //    83: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    86: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    89: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;)I
        //    92: pop            
        //    93: aload_1        
        //    94: invokestatic    android/support/v4/graphics/TypefaceCompatUtil.closeQuietly:(Ljava/io/Closeable;)V
        //    97: iconst_0       
        //    98: ireturn        
        //    99: astore_1       
        //   100: aconst_null    
        //   101: astore_0       
        //   102: aload_0        
        //   103: invokestatic    android/support/v4/graphics/TypefaceCompatUtil.closeQuietly:(Ljava/io/Closeable;)V
        //   106: aload_1        
        //   107: athrow         
        //   108: astore_1       
        //   109: goto            102
        //   112: astore_2       
        //   113: aconst_null    
        //   114: astore_1       
        //   115: goto            57
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  0      10     112    118    Ljava/io/IOException;
        //  0      10     99     102    Any
        //  12     18     52     57     Ljava/io/IOException;
        //  12     18     108    112    Any
        //  20     27     52     57     Ljava/io/IOException;
        //  20     27     108    112    Any
        //  41     49     52     57     Ljava/io/IOException;
        //  41     49     108    112    Any
        //  59     63     108    112    Any
        //  65     69     108    112    Any
        //  71     93     108    112    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0018:
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
    
    public static File getTempFile(final Context context) {
        int i = 0;
        final String string = ".font" + Process.myPid() + "-" + Process.myTid() + "-";
        while (i < 100) {
            final File file = new File(context.getCacheDir(), string + i);
            while (true) {
                try {
                    if (file.createNewFile()) {
                        return file;
                    }
                    ++i;
                }
                catch (final IOException ex) {
                    continue;
                }
                break;
            }
        }
        return null;
    }
    
    @RequiresApi(19)
    public static ByteBuffer mmap(final Context p0, final CancellationSignal p1, final Uri p2) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokevirtual   android/content/Context.getContentResolver:()Landroid/content/ContentResolver;
        //     4: astore_0       
        //     5: aload_0        
        //     6: aload_2        
        //     7: ldc             "r"
        //     9: aload_1        
        //    10: invokevirtual   android/content/ContentResolver.openFileDescriptor:(Landroid/net/Uri;Ljava/lang/String;Landroid/os/CancellationSignal;)Landroid/os/ParcelFileDescriptor;
        //    13: astore_2       
        //    14: new             Ljava/io/FileInputStream;
        //    17: astore_3       
        //    18: aload_3        
        //    19: aload_2        
        //    20: invokevirtual   android/os/ParcelFileDescriptor.getFileDescriptor:()Ljava/io/FileDescriptor;
        //    23: invokespecial   java/io/FileInputStream.<init>:(Ljava/io/FileDescriptor;)V
        //    26: aload_3        
        //    27: invokevirtual   java/io/FileInputStream.getChannel:()Ljava/nio/channels/FileChannel;
        //    30: astore_0       
        //    31: aload_0        
        //    32: invokevirtual   java/nio/channels/FileChannel.size:()J
        //    35: lstore          4
        //    37: aload_0        
        //    38: getstatic       java/nio/channels/FileChannel$MapMode.READ_ONLY:Ljava/nio/channels/FileChannel$MapMode;
        //    41: lconst_0       
        //    42: lload           4
        //    44: invokevirtual   java/nio/channels/FileChannel.map:(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
        //    47: astore_0       
        //    48: aload_3        
        //    49: ifnonnull       58
        //    52: aload_2        
        //    53: ifnonnull       104
        //    56: aload_0        
        //    57: areturn        
        //    58: iconst_0       
        //    59: ifne            82
        //    62: aload_3        
        //    63: invokevirtual   java/io/FileInputStream.close:()V
        //    66: goto            52
        //    69: astore_0       
        //    70: aload_0        
        //    71: athrow         
        //    72: astore_1       
        //    73: aload_2        
        //    74: ifnonnull       168
        //    77: aload_1        
        //    78: athrow         
        //    79: astore_0       
        //    80: aconst_null    
        //    81: areturn        
        //    82: aload_3        
        //    83: invokevirtual   java/io/FileInputStream.close:()V
        //    86: goto            52
        //    89: astore_0       
        //    90: new             Ljava/lang/NullPointerException;
        //    93: dup            
        //    94: invokespecial   java/lang/NullPointerException.<init>:()V
        //    97: athrow         
        //    98: astore_1       
        //    99: aconst_null    
        //   100: astore_0       
        //   101: goto            73
        //   104: iconst_0       
        //   105: ifne            115
        //   108: aload_2        
        //   109: invokevirtual   android/os/ParcelFileDescriptor.close:()V
        //   112: goto            56
        //   115: aload_2        
        //   116: invokevirtual   android/os/ParcelFileDescriptor.close:()V
        //   119: goto            56
        //   122: astore_0       
        //   123: new             Ljava/lang/NullPointerException;
        //   126: dup            
        //   127: invokespecial   java/lang/NullPointerException.<init>:()V
        //   130: athrow         
        //   131: astore_0       
        //   132: aload_0        
        //   133: athrow         
        //   134: astore_1       
        //   135: aload_3        
        //   136: ifnonnull       141
        //   139: aload_1        
        //   140: athrow         
        //   141: aload_0        
        //   142: ifnonnull       152
        //   145: aload_3        
        //   146: invokevirtual   java/io/FileInputStream.close:()V
        //   149: goto            139
        //   152: aload_3        
        //   153: invokevirtual   java/io/FileInputStream.close:()V
        //   156: goto            139
        //   159: astore_3       
        //   160: aload_0        
        //   161: aload_3        
        //   162: invokevirtual   java/lang/Throwable.addSuppressed:(Ljava/lang/Throwable;)V
        //   165: goto            139
        //   168: aload_0        
        //   169: ifnonnull       179
        //   172: aload_2        
        //   173: invokevirtual   android/os/ParcelFileDescriptor.close:()V
        //   176: goto            77
        //   179: aload_2        
        //   180: invokevirtual   android/os/ParcelFileDescriptor.close:()V
        //   183: goto            77
        //   186: astore_2       
        //   187: aload_0        
        //   188: aload_2        
        //   189: invokevirtual   java/lang/Throwable.addSuppressed:(Ljava/lang/Throwable;)V
        //   192: goto            77
        //   195: astore_1       
        //   196: aconst_null    
        //   197: astore_0       
        //   198: goto            135
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  5      14     79     82     Ljava/io/IOException;
        //  14     26     69     73     Ljava/lang/Throwable;
        //  14     26     98     104    Any
        //  26     48     131    135    Ljava/lang/Throwable;
        //  26     48     195    201    Any
        //  62     66     69     73     Ljava/lang/Throwable;
        //  62     66     98     104    Any
        //  70     72     72     73     Any
        //  77     79     79     82     Ljava/io/IOException;
        //  82     86     89     98     Ljava/lang/Throwable;
        //  82     86     69     73     Ljava/lang/Throwable;
        //  82     86     98     104    Any
        //  90     98     69     73     Ljava/lang/Throwable;
        //  90     98     98     104    Any
        //  108    112    79     82     Ljava/io/IOException;
        //  115    119    122    131    Ljava/lang/Throwable;
        //  115    119    79     82     Ljava/io/IOException;
        //  123    131    79     82     Ljava/io/IOException;
        //  132    134    134    135    Any
        //  139    141    69     73     Ljava/lang/Throwable;
        //  139    141    98     104    Any
        //  145    149    69     73     Ljava/lang/Throwable;
        //  145    149    98     104    Any
        //  152    156    159    168    Ljava/lang/Throwable;
        //  152    156    69     73     Ljava/lang/Throwable;
        //  152    156    98     104    Any
        //  160    165    69     73     Ljava/lang/Throwable;
        //  160    165    98     104    Any
        //  172    176    79     82     Ljava/io/IOException;
        //  179    183    186    195    Ljava/lang/Throwable;
        //  179    183    79     82     Ljava/io/IOException;
        //  187    192    79     82     Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0052:
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
    
    @RequiresApi(19)
    private static ByteBuffer mmap(final File p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: astore_1       
        //     4: aload_1        
        //     5: aload_0        
        //     6: invokespecial   java/io/FileInputStream.<init>:(Ljava/io/File;)V
        //     9: aload_1        
        //    10: invokevirtual   java/io/FileInputStream.getChannel:()Ljava/nio/channels/FileChannel;
        //    13: astore_0       
        //    14: aload_0        
        //    15: invokevirtual   java/nio/channels/FileChannel.size:()J
        //    18: lstore_2       
        //    19: aload_0        
        //    20: getstatic       java/nio/channels/FileChannel$MapMode.READ_ONLY:Ljava/nio/channels/FileChannel$MapMode;
        //    23: lconst_0       
        //    24: lload_2        
        //    25: invokevirtual   java/nio/channels/FileChannel.map:(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
        //    28: astore_0       
        //    29: aload_1        
        //    30: ifnonnull       35
        //    33: aload_0        
        //    34: areturn        
        //    35: iconst_0       
        //    36: ifne            49
        //    39: aload_1        
        //    40: invokevirtual   java/io/FileInputStream.close:()V
        //    43: goto            33
        //    46: astore_0       
        //    47: aconst_null    
        //    48: areturn        
        //    49: aload_1        
        //    50: invokevirtual   java/io/FileInputStream.close:()V
        //    53: goto            33
        //    56: astore_0       
        //    57: new             Ljava/lang/NullPointerException;
        //    60: dup            
        //    61: invokespecial   java/lang/NullPointerException.<init>:()V
        //    64: athrow         
        //    65: astore_0       
        //    66: aload_0        
        //    67: athrow         
        //    68: astore          4
        //    70: aload_1        
        //    71: ifnonnull       77
        //    74: aload           4
        //    76: athrow         
        //    77: aload_0        
        //    78: ifnonnull       88
        //    81: aload_1        
        //    82: invokevirtual   java/io/FileInputStream.close:()V
        //    85: goto            74
        //    88: aload_1        
        //    89: invokevirtual   java/io/FileInputStream.close:()V
        //    92: goto            74
        //    95: astore_1       
        //    96: aload_0        
        //    97: aload_1        
        //    98: invokevirtual   java/lang/Throwable.addSuppressed:(Ljava/lang/Throwable;)V
        //   101: goto            74
        //   104: astore          4
        //   106: aconst_null    
        //   107: astore_0       
        //   108: goto            70
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  0      9      46     49     Ljava/io/IOException;
        //  9      29     65     70     Ljava/lang/Throwable;
        //  9      29     104    111    Any
        //  39     43     46     49     Ljava/io/IOException;
        //  49     53     56     65     Ljava/lang/Throwable;
        //  49     53     46     49     Ljava/io/IOException;
        //  57     65     46     49     Ljava/io/IOException;
        //  66     68     68     70     Any
        //  74     77     46     49     Ljava/io/IOException;
        //  81     85     46     49     Ljava/io/IOException;
        //  88     92     95     104    Ljava/lang/Throwable;
        //  88     92     46     49     Ljava/io/IOException;
        //  96     101    46     49     Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0033:
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

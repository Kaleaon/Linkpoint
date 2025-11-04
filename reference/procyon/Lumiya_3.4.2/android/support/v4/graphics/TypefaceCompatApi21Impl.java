// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.graphics;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.provider.FontsContractCompat;
import android.os.CancellationSignal;
import android.content.Context;
import android.system.ErrnoException;
import android.system.OsConstants;
import android.system.Os;
import java.io.File;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RestrictTo;
import android.support.annotation.RequiresApi;

@RequiresApi(21)
@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
class TypefaceCompatApi21Impl extends TypefaceCompatBaseImpl
{
    private static final String TAG = "TypefaceCompatApi21Impl";
    
    private File getFile(final ParcelFileDescriptor parcelFileDescriptor) {
        try {
            final String readlink = Os.readlink("/proc/self/fd/" + parcelFileDescriptor.getFd());
            if (!OsConstants.S_ISREG(Os.stat(readlink).st_mode)) {
                return null;
            }
            return new File(readlink);
        }
        catch (final ErrnoException ex) {
            return null;
        }
    }
    
    @Override
    public Typeface createFromFontInfo(final Context p0, final CancellationSignal p1, @NonNull final FontsContractCompat.FontInfo[] p2, final int p3) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: arraylength    
        //     2: iconst_1       
        //     3: if_icmplt       76
        //     6: aload_0        
        //     7: aload_3        
        //     8: iload           4
        //    10: invokevirtual   android/support/v4/graphics/TypefaceCompatApi21Impl.findBestInfo:([Landroid/support/v4/provider/FontsContractCompat$FontInfo;I)Landroid/support/v4/provider/FontsContractCompat$FontInfo;
        //    13: astore          5
        //    15: aload_1        
        //    16: invokevirtual   android/content/Context.getContentResolver:()Landroid/content/ContentResolver;
        //    19: astore_3       
        //    20: aload_3        
        //    21: aload           5
        //    23: invokevirtual   android/support/v4/provider/FontsContractCompat$FontInfo.getUri:()Landroid/net/Uri;
        //    26: ldc             "r"
        //    28: aload_2        
        //    29: invokevirtual   android/content/ContentResolver.openFileDescriptor:(Landroid/net/Uri;Ljava/lang/String;Landroid/os/CancellationSignal;)Landroid/os/ParcelFileDescriptor;
        //    32: astore_3       
        //    33: aload_0        
        //    34: aload_3        
        //    35: invokespecial   android/support/v4/graphics/TypefaceCompatApi21Impl.getFile:(Landroid/os/ParcelFileDescriptor;)Ljava/io/File;
        //    38: astore_2       
        //    39: aload_2        
        //    40: ifnonnull       78
        //    43: new             Ljava/io/FileInputStream;
        //    46: astore          5
        //    48: aload           5
        //    50: aload_3        
        //    51: invokevirtual   android/os/ParcelFileDescriptor.getFileDescriptor:()Ljava/io/FileDescriptor;
        //    54: invokespecial   java/io/FileInputStream.<init>:(Ljava/io/FileDescriptor;)V
        //    57: aload_0        
        //    58: aload_1        
        //    59: aload           5
        //    61: invokespecial   android/support/v4/graphics/TypefaceCompatBaseImpl.createFromInputStream:(Landroid/content/Context;Ljava/io/InputStream;)Landroid/graphics/Typeface;
        //    64: astore_1       
        //    65: aload           5
        //    67: ifnonnull       96
        //    70: aload_3        
        //    71: ifnonnull       144
        //    74: aload_1        
        //    75: areturn        
        //    76: aconst_null    
        //    77: areturn        
        //    78: aload_2        
        //    79: invokevirtual   java/io/File.canRead:()Z
        //    82: ifeq            43
        //    85: aload_2        
        //    86: invokestatic    android/graphics/Typeface.createFromFile:(Ljava/io/File;)Landroid/graphics/Typeface;
        //    89: astore_1       
        //    90: aload_3        
        //    91: ifnonnull       213
        //    94: aload_1        
        //    95: areturn        
        //    96: iconst_0       
        //    97: ifne            121
        //   100: aload           5
        //   102: invokevirtual   java/io/FileInputStream.close:()V
        //   105: goto            70
        //   108: astore_2       
        //   109: aload_2        
        //   110: athrow         
        //   111: astore_1       
        //   112: aload_3        
        //   113: ifnonnull       240
        //   116: aload_1        
        //   117: athrow         
        //   118: astore_1       
        //   119: aconst_null    
        //   120: areturn        
        //   121: aload           5
        //   123: invokevirtual   java/io/FileInputStream.close:()V
        //   126: goto            70
        //   129: astore_1       
        //   130: new             Ljava/lang/NullPointerException;
        //   133: dup            
        //   134: invokespecial   java/lang/NullPointerException.<init>:()V
        //   137: athrow         
        //   138: astore_1       
        //   139: aconst_null    
        //   140: astore_2       
        //   141: goto            112
        //   144: iconst_0       
        //   145: ifne            155
        //   148: aload_3        
        //   149: invokevirtual   android/os/ParcelFileDescriptor.close:()V
        //   152: goto            74
        //   155: aload_3        
        //   156: invokevirtual   android/os/ParcelFileDescriptor.close:()V
        //   159: goto            74
        //   162: astore_1       
        //   163: new             Ljava/lang/NullPointerException;
        //   166: dup            
        //   167: invokespecial   java/lang/NullPointerException.<init>:()V
        //   170: athrow         
        //   171: astore_2       
        //   172: aload_2        
        //   173: athrow         
        //   174: astore_1       
        //   175: aload           5
        //   177: ifnonnull       182
        //   180: aload_1        
        //   181: athrow         
        //   182: aload_2        
        //   183: ifnonnull       194
        //   186: aload           5
        //   188: invokevirtual   java/io/FileInputStream.close:()V
        //   191: goto            180
        //   194: aload           5
        //   196: invokevirtual   java/io/FileInputStream.close:()V
        //   199: goto            180
        //   202: astore          5
        //   204: aload_2        
        //   205: aload           5
        //   207: invokevirtual   java/lang/Throwable.addSuppressed:(Ljava/lang/Throwable;)V
        //   210: goto            180
        //   213: iconst_0       
        //   214: ifne            224
        //   217: aload_3        
        //   218: invokevirtual   android/os/ParcelFileDescriptor.close:()V
        //   221: goto            94
        //   224: aload_3        
        //   225: invokevirtual   android/os/ParcelFileDescriptor.close:()V
        //   228: goto            94
        //   231: astore_1       
        //   232: new             Ljava/lang/NullPointerException;
        //   235: dup            
        //   236: invokespecial   java/lang/NullPointerException.<init>:()V
        //   239: athrow         
        //   240: aload_2        
        //   241: ifnonnull       251
        //   244: aload_3        
        //   245: invokevirtual   android/os/ParcelFileDescriptor.close:()V
        //   248: goto            116
        //   251: aload_3        
        //   252: invokevirtual   android/os/ParcelFileDescriptor.close:()V
        //   255: goto            116
        //   258: astore_3       
        //   259: aload_2        
        //   260: aload_3        
        //   261: invokevirtual   java/lang/Throwable.addSuppressed:(Ljava/lang/Throwable;)V
        //   264: goto            116
        //   267: astore_1       
        //   268: aconst_null    
        //   269: astore_2       
        //   270: goto            175
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  20     33     118    121    Ljava/io/IOException;
        //  33     39     108    112    Ljava/lang/Throwable;
        //  33     39     138    144    Any
        //  43     57     108    112    Ljava/lang/Throwable;
        //  43     57     138    144    Any
        //  57     65     171    175    Ljava/lang/Throwable;
        //  57     65     267    273    Any
        //  78     90     108    112    Ljava/lang/Throwable;
        //  78     90     138    144    Any
        //  100    105    108    112    Ljava/lang/Throwable;
        //  100    105    138    144    Any
        //  109    111    111    112    Any
        //  116    118    118    121    Ljava/io/IOException;
        //  121    126    129    138    Ljava/lang/Throwable;
        //  121    126    108    112    Ljava/lang/Throwable;
        //  121    126    138    144    Any
        //  130    138    108    112    Ljava/lang/Throwable;
        //  130    138    138    144    Any
        //  148    152    118    121    Ljava/io/IOException;
        //  155    159    162    171    Ljava/lang/Throwable;
        //  155    159    118    121    Ljava/io/IOException;
        //  163    171    118    121    Ljava/io/IOException;
        //  172    174    174    175    Any
        //  180    182    108    112    Ljava/lang/Throwable;
        //  180    182    138    144    Any
        //  186    191    108    112    Ljava/lang/Throwable;
        //  186    191    138    144    Any
        //  194    199    202    213    Ljava/lang/Throwable;
        //  194    199    108    112    Ljava/lang/Throwable;
        //  194    199    138    144    Any
        //  204    210    108    112    Ljava/lang/Throwable;
        //  204    210    138    144    Any
        //  217    221    118    121    Ljava/io/IOException;
        //  224    228    231    240    Ljava/lang/Throwable;
        //  224    228    118    121    Ljava/io/IOException;
        //  232    240    118    121    Ljava/io/IOException;
        //  244    248    118    121    Ljava/io/IOException;
        //  251    255    258    267    Ljava/lang/Throwable;
        //  251    255    118    121    Ljava/io/IOException;
        //  259    264    118    121    Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0070:
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

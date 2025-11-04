// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.graphics;

import java.io.File;
import java.io.InputStream;
import android.support.annotation.NonNull;
import android.support.v4.provider.FontsContractCompat;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;
import android.graphics.Typeface;
import android.content.res.Resources;
import android.content.Context;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.support.annotation.RestrictTo;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
class TypefaceCompatBaseImpl implements TypefaceCompatImpl
{
    private static final String CACHE_FILE_PREFIX = "cached_font_";
    private static final String TAG = "TypefaceCompatBaseImpl";
    
    private FontResourcesParserCompat.FontFileResourceEntry findBestEntry(final FontResourcesParserCompat.FontFamilyFilesResourceEntry fontFamilyFilesResourceEntry, final int n) {
        return findBestFont(fontFamilyFilesResourceEntry.getEntries(), n, (StyleExtractor<FontResourcesParserCompat.FontFileResourceEntry>)new StyleExtractor<FontResourcesParserCompat.FontFileResourceEntry>() {
            public int getWeight(final FontResourcesParserCompat.FontFileResourceEntry fontFileResourceEntry) {
                return fontFileResourceEntry.getWeight();
            }
            
            public boolean isItalic(final FontResourcesParserCompat.FontFileResourceEntry fontFileResourceEntry) {
                return fontFileResourceEntry.isItalic();
            }
        });
    }
    
    private static <T> T findBestFont(final T[] array, int n, final StyleExtractor<T> styleExtractor) {
        T t = null;
        int n2;
        if ((n & 0x1) != 0x0) {
            n2 = 700;
        }
        else {
            n2 = 400;
        }
        final boolean b = (n & 0x2) != 0x0;
        n = Integer.MAX_VALUE;
        for (final T t2 : array) {
            final int abs = Math.abs(styleExtractor.getWeight(t2) - n2);
            int n3;
            if (styleExtractor.isItalic(t2) != b) {
                n3 = 1;
            }
            else {
                n3 = 0;
            }
            final int n4 = n3 + abs * 2;
            if (t == null || n > n4) {
                t = t2;
                n = n4;
            }
        }
        return t;
    }
    
    @Nullable
    @Override
    public Typeface createFromFontFamilyFilesResourceEntry(final Context context, final FontResourcesParserCompat.FontFamilyFilesResourceEntry fontFamilyFilesResourceEntry, final Resources resources, final int n) {
        final FontResourcesParserCompat.FontFileResourceEntry bestEntry = this.findBestEntry(fontFamilyFilesResourceEntry, n);
        if (bestEntry != null) {
            return TypefaceCompat.createFromResourcesFontFile(context, resources, bestEntry.getResourceId(), bestEntry.getFileName(), n);
        }
        return null;
    }
    
    @Override
    public Typeface createFromFontInfo(final Context p0, @Nullable final CancellationSignal p1, @NonNull final FontsContractCompat.FontInfo[] p2, final int p3) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: astore_2       
        //     2: aload_3        
        //     3: arraylength    
        //     4: iconst_1       
        //     5: if_icmplt       43
        //     8: aload_0        
        //     9: aload_3        
        //    10: iload           4
        //    12: invokevirtual   android/support/v4/graphics/TypefaceCompatBaseImpl.findBestInfo:([Landroid/support/v4/provider/FontsContractCompat$FontInfo;I)Landroid/support/v4/provider/FontsContractCompat$FontInfo;
        //    15: astore_3       
        //    16: aload_1        
        //    17: invokevirtual   android/content/Context.getContentResolver:()Landroid/content/ContentResolver;
        //    20: aload_3        
        //    21: invokevirtual   android/support/v4/provider/FontsContractCompat$FontInfo.getUri:()Landroid/net/Uri;
        //    24: invokevirtual   android/content/ContentResolver.openInputStream:(Landroid/net/Uri;)Ljava/io/InputStream;
        //    27: astore_3       
        //    28: aload_3        
        //    29: astore_2       
        //    30: aload_0        
        //    31: aload_1        
        //    32: aload_2        
        //    33: invokevirtual   android/support/v4/graphics/TypefaceCompatBaseImpl.createFromInputStream:(Landroid/content/Context;Ljava/io/InputStream;)Landroid/graphics/Typeface;
        //    36: astore_1       
        //    37: aload_2        
        //    38: invokestatic    android/support/v4/graphics/TypefaceCompatUtil.closeQuietly:(Ljava/io/Closeable;)V
        //    41: aload_1        
        //    42: areturn        
        //    43: aconst_null    
        //    44: areturn        
        //    45: astore_1       
        //    46: aconst_null    
        //    47: astore_2       
        //    48: aload_2        
        //    49: invokestatic    android/support/v4/graphics/TypefaceCompatUtil.closeQuietly:(Ljava/io/Closeable;)V
        //    52: aconst_null    
        //    53: areturn        
        //    54: astore_1       
        //    55: aload_2        
        //    56: invokestatic    android/support/v4/graphics/TypefaceCompatUtil.closeQuietly:(Ljava/io/Closeable;)V
        //    59: aload_1        
        //    60: athrow         
        //    61: astore_1       
        //    62: goto            55
        //    65: astore_1       
        //    66: goto            48
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  16     28     45     48     Ljava/io/IOException;
        //  16     28     54     55     Any
        //  30     37     65     69     Ljava/io/IOException;
        //  30     37     61     65     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0043:
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
    
    protected Typeface createFromInputStream(Context tempFile, final InputStream inputStream) {
        tempFile = (Context)TypefaceCompatUtil.getTempFile(tempFile);
        Label_0032: {
            if (tempFile == null) {
                break Label_0032;
            }
            try {
                if (TypefaceCompatUtil.copyToFile((File)tempFile, inputStream)) {
                    return Typeface.createFromFile(((File)tempFile).getPath());
                }
                return null;
                return null;
            }
            catch (final RuntimeException ex) {
                return null;
            }
            finally {
                ((File)tempFile).delete();
            }
        }
    }
    
    @Nullable
    @Override
    public Typeface createFromResourcesFontFile(Context tempFile, final Resources resources, final int n, final String s, final int n2) {
        tempFile = (Context)TypefaceCompatUtil.getTempFile(tempFile);
        Label_0033: {
            if (tempFile == null) {
                break Label_0033;
            }
            try {
                if (TypefaceCompatUtil.copyToFile((File)tempFile, resources, n)) {
                    return Typeface.createFromFile(((File)tempFile).getPath());
                }
                return null;
                return null;
            }
            catch (final RuntimeException ex) {
                return null;
            }
            finally {
                ((File)tempFile).delete();
            }
        }
    }
    
    protected FontsContractCompat.FontInfo findBestInfo(final FontsContractCompat.FontInfo[] array, final int n) {
        return findBestFont(array, n, (StyleExtractor<FontsContractCompat.FontInfo>)new StyleExtractor<FontsContractCompat.FontInfo>() {
            public int getWeight(final FontsContractCompat.FontInfo fontInfo) {
                return fontInfo.getWeight();
            }
            
            public boolean isItalic(final FontsContractCompat.FontInfo fontInfo) {
                return fontInfo.isItalic();
            }
        });
    }
    
    private interface StyleExtractor<T>
    {
        int getWeight(final T p0);
        
        boolean isItalic(final T p0);
    }
}

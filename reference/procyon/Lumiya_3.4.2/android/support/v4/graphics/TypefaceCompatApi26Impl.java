// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.graphics;

import android.support.annotation.NonNull;
import android.support.v4.provider.FontsContractCompat;
import android.support.annotation.Nullable;
import android.os.CancellationSignal;
import android.content.res.Resources;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.content.Context;
import java.lang.reflect.InvocationTargetException;
import android.util.Log;
import java.lang.reflect.Array;
import android.graphics.Typeface;
import java.nio.ByteBuffer;
import android.graphics.fonts.FontVariationAxis;
import android.content.res.AssetManager;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import android.support.annotation.RestrictTo;
import android.support.annotation.RequiresApi;

@RequiresApi(26)
@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class TypefaceCompatApi26Impl extends TypefaceCompatApi21Impl
{
    private static final String ABORT_CREATION_METHOD = "abortCreation";
    private static final String ADD_FONT_FROM_ASSET_MANAGER_METHOD = "addFontFromAssetManager";
    private static final String ADD_FONT_FROM_BUFFER_METHOD = "addFontFromBuffer";
    private static final String CREATE_FROM_FAMILIES_WITH_DEFAULT_METHOD = "createFromFamiliesWithDefault";
    private static final String FONT_FAMILY_CLASS = "android.graphics.FontFamily";
    private static final String FREEZE_METHOD = "freeze";
    private static final int RESOLVE_BY_FONT_TABLE = -1;
    private static final String TAG = "TypefaceCompatApi26Impl";
    private static final Method sAbortCreation;
    private static final Method sAddFontFromAssetManager;
    private static final Method sAddFontFromBuffer;
    private static final Method sCreateFromFamiliesWithDefault;
    private static final Class sFontFamily;
    private static final Constructor sFontFamilyCtor;
    private static final Method sFreeze;
    
    static {
        Method sAbortCreation2 = null;
        while (true) {
            try {
                final Class<?> forName = Class.forName("android.graphics.FontFamily");
                final Constructor<?> constructor = forName.getConstructor((Class<?>[])new Class[0]);
                final Method method = forName.getMethod("addFontFromAssetManager", AssetManager.class, String.class, Integer.TYPE, Boolean.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, FontVariationAxis[].class);
                final Method method2 = forName.getMethod("addFontFromBuffer", ByteBuffer.class, Integer.TYPE, FontVariationAxis[].class, Integer.TYPE, Integer.TYPE);
                final Method method3 = forName.getMethod("freeze", (Class[])new Class[0]);
                final Method method4 = forName.getMethod("abortCreation", (Class[])new Class[0]);
                final Method declaredMethod = Typeface.class.getDeclaredMethod("createFromFamiliesWithDefault", Array.newInstance(forName, 1).getClass(), Integer.TYPE, Integer.TYPE);
                declaredMethod.setAccessible(true);
                sAbortCreation2 = method4;
                final Method sCreateFromFamiliesWithDefault2 = declaredMethod;
                sFontFamilyCtor = constructor;
                sFontFamily = forName;
                sAddFontFromAssetManager = method;
                sAddFontFromBuffer = method2;
                sFreeze = method3;
                sAbortCreation = sAbortCreation2;
                sCreateFromFamiliesWithDefault = sCreateFromFamiliesWithDefault2;
            }
            catch (final ClassNotFoundException | NoSuchMethodException ex) {
                Log.e("TypefaceCompatApi26Impl", "Unable to collect necessary methods for class " + ex.getClass().getName(), (Throwable)ex);
                final Method sCreateFromFamiliesWithDefault2 = null;
                final Method method3 = null;
                final Method method2 = null;
                final Method method = null;
                final Constructor<?> constructor = null;
                final Class<?> forName = null;
                continue;
            }
            break;
        }
    }
    
    private static boolean abortCreation(final Object obj) {
        try {
            return (boolean)TypefaceCompatApi26Impl.sAbortCreation.invoke(obj, new Object[0]);
        }
        catch (final IllegalAccessException | InvocationTargetException cause) {
            throw new RuntimeException((Throwable)cause);
        }
    }
    
    private static boolean addFontFromAssetManager(final Context context, final Object obj, final String s, final int i, final int j, final int k) {
        try {
            return (boolean)TypefaceCompatApi26Impl.sAddFontFromAssetManager.invoke(obj, context.getAssets(), s, 0, false, i, j, k, null);
        }
        catch (final IllegalAccessException | InvocationTargetException cause) {
            throw new RuntimeException((Throwable)cause);
        }
    }
    
    private static boolean addFontFromBuffer(final Object obj, final ByteBuffer byteBuffer, final int i, final int j, final int k) {
        try {
            return (boolean)TypefaceCompatApi26Impl.sAddFontFromBuffer.invoke(obj, byteBuffer, i, null, j, k);
        }
        catch (final IllegalAccessException | InvocationTargetException cause) {
            throw new RuntimeException((Throwable)cause);
        }
    }
    
    private static Typeface createFromFamiliesWithDefault(final Object o) {
        try {
            final Object instance = Array.newInstance(TypefaceCompatApi26Impl.sFontFamily, 1);
            Array.set(instance, 0, o);
            return (Typeface)TypefaceCompatApi26Impl.sCreateFromFamiliesWithDefault.invoke(null, instance, -1, -1);
        }
        catch (final IllegalAccessException | InvocationTargetException cause) {
            throw new RuntimeException((Throwable)cause);
        }
    }
    
    private static boolean freeze(final Object obj) {
        try {
            return (boolean)TypefaceCompatApi26Impl.sFreeze.invoke(obj, new Object[0]);
        }
        catch (final IllegalAccessException | InvocationTargetException cause) {
            throw new RuntimeException((Throwable)cause);
        }
    }
    
    private static boolean isFontFamilyPrivateAPIAvailable() {
        if (TypefaceCompatApi26Impl.sAddFontFromAssetManager == null) {
            Log.w("TypefaceCompatApi26Impl", "Unable to collect necessary private methods.Fallback to legacy implementation.");
        }
        return TypefaceCompatApi26Impl.sAddFontFromAssetManager != null;
    }
    
    private static Object newFamily() {
        try {
            return TypefaceCompatApi26Impl.sFontFamilyCtor.newInstance(new Object[0]);
        }
        catch (final IllegalAccessException | InstantiationException | InvocationTargetException cause) {
            throw new RuntimeException((Throwable)cause);
        }
    }
    
    @Override
    public Typeface createFromFontFamilyFilesResourceEntry(final Context context, final FontResourcesParserCompat.FontFamilyFilesResourceEntry fontFamilyFilesResourceEntry, final Resources resources, int i) {
        if (!isFontFamilyPrivateAPIAvailable()) {
            return super.createFromFontFamilyFilesResourceEntry(context, fontFamilyFilesResourceEntry, resources, i);
        }
        final Object family = newFamily();
        final FontResourcesParserCompat.FontFileResourceEntry[] entries = fontFamilyFilesResourceEntry.getEntries();
        int length;
        FontResourcesParserCompat.FontFileResourceEntry fontFileResourceEntry;
        String fileName;
        int weight;
        int n;
        for (length = entries.length, i = 0; i < length; ++i) {
            fontFileResourceEntry = entries[i];
            fileName = fontFileResourceEntry.getFileName();
            weight = fontFileResourceEntry.getWeight();
            if (!fontFileResourceEntry.isItalic()) {
                n = 0;
            }
            else {
                n = 1;
            }
            if (!addFontFromAssetManager(context, family, fileName, 0, weight, n)) {
                abortCreation(family);
                return null;
            }
        }
        if (freeze(family)) {
            return createFromFamiliesWithDefault(family);
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
        //     1: arraylength    
        //     2: iconst_1       
        //     3: if_icmplt       58
        //     6: invokestatic    android/support/v4/graphics/TypefaceCompatApi26Impl.isFontFamilyPrivateAPIAvailable:()Z
        //     9: ifeq            60
        //    12: aload_1        
        //    13: aload_3        
        //    14: aload_2        
        //    15: invokestatic    android/support/v4/provider/FontsContractCompat.prepareFontData:(Landroid/content/Context;[Landroid/support/v4/provider/FontsContractCompat$FontInfo;Landroid/os/CancellationSignal;)Ljava/util/Map;
        //    18: astore          5
        //    20: invokestatic    android/support/v4/graphics/TypefaceCompatApi26Impl.newFamily:()Ljava/lang/Object;
        //    23: astore_1       
        //    24: iconst_0       
        //    25: istore          4
        //    27: aload_3        
        //    28: arraylength    
        //    29: istore          6
        //    31: iconst_0       
        //    32: istore          7
        //    34: iload           7
        //    36: iload           6
        //    38: if_icmplt       196
        //    41: iload           4
        //    43: ifeq            286
        //    46: aload_1        
        //    47: invokestatic    android/support/v4/graphics/TypefaceCompatApi26Impl.freeze:(Ljava/lang/Object;)Z
        //    50: ifeq            293
        //    53: aload_1        
        //    54: invokestatic    android/support/v4/graphics/TypefaceCompatApi26Impl.createFromFamiliesWithDefault:(Ljava/lang/Object;)Landroid/graphics/Typeface;
        //    57: areturn        
        //    58: aconst_null    
        //    59: areturn        
        //    60: aload_0        
        //    61: aload_3        
        //    62: iload           4
        //    64: invokevirtual   android/support/v4/graphics/TypefaceCompatApi26Impl.findBestInfo:([Landroid/support/v4/provider/FontsContractCompat$FontInfo;I)Landroid/support/v4/provider/FontsContractCompat$FontInfo;
        //    67: astore          8
        //    69: aload_1        
        //    70: invokevirtual   android/content/Context.getContentResolver:()Landroid/content/ContentResolver;
        //    73: astore_1       
        //    74: aload_1        
        //    75: aload           8
        //    77: invokevirtual   android/support/v4/provider/FontsContractCompat$FontInfo.getUri:()Landroid/net/Uri;
        //    80: ldc_w           "r"
        //    83: aload_2        
        //    84: invokevirtual   android/content/ContentResolver.openFileDescriptor:(Landroid/net/Uri;Ljava/lang/String;Landroid/os/CancellationSignal;)Landroid/os/ParcelFileDescriptor;
        //    87: astore_3       
        //    88: aconst_null    
        //    89: astore_1       
        //    90: new             Landroid/graphics/Typeface$Builder;
        //    93: astore_2       
        //    94: aload_2        
        //    95: aload_3        
        //    96: invokevirtual   android/os/ParcelFileDescriptor.getFileDescriptor:()Ljava/io/FileDescriptor;
        //    99: invokespecial   android/graphics/Typeface$Builder.<init>:(Ljava/io/FileDescriptor;)V
        //   102: aload_2        
        //   103: aload           8
        //   105: invokevirtual   android/support/v4/provider/FontsContractCompat$FontInfo.getWeight:()I
        //   108: invokevirtual   android/graphics/Typeface$Builder.setWeight:(I)Landroid/graphics/Typeface$Builder;
        //   111: aload           8
        //   113: invokevirtual   android/support/v4/provider/FontsContractCompat$FontInfo.isItalic:()Z
        //   116: invokevirtual   android/graphics/Typeface$Builder.setItalic:(Z)Landroid/graphics/Typeface$Builder;
        //   119: invokevirtual   android/graphics/Typeface$Builder.build:()Landroid/graphics/Typeface;
        //   122: astore_2       
        //   123: aload_3        
        //   124: ifnonnull       129
        //   127: aload_2        
        //   128: areturn        
        //   129: iconst_0       
        //   130: ifne            143
        //   133: aload_3        
        //   134: invokevirtual   android/os/ParcelFileDescriptor.close:()V
        //   137: goto            127
        //   140: astore_1       
        //   141: aconst_null    
        //   142: areturn        
        //   143: aload_3        
        //   144: invokevirtual   android/os/ParcelFileDescriptor.close:()V
        //   147: goto            127
        //   150: astore_1       
        //   151: new             Ljava/lang/NullPointerException;
        //   154: dup            
        //   155: invokespecial   java/lang/NullPointerException.<init>:()V
        //   158: athrow         
        //   159: astore_1       
        //   160: aload_1        
        //   161: athrow         
        //   162: astore_2       
        //   163: aload_3        
        //   164: ifnonnull       169
        //   167: aload_2        
        //   168: athrow         
        //   169: aload_1        
        //   170: ifnonnull       180
        //   173: aload_3        
        //   174: invokevirtual   android/os/ParcelFileDescriptor.close:()V
        //   177: goto            167
        //   180: aload_3        
        //   181: invokevirtual   android/os/ParcelFileDescriptor.close:()V
        //   184: goto            167
        //   187: astore_3       
        //   188: aload_1        
        //   189: aload_3        
        //   190: invokevirtual   java/lang/Throwable.addSuppressed:(Ljava/lang/Throwable;)V
        //   193: goto            167
        //   196: aload_3        
        //   197: iload           7
        //   199: aaload         
        //   200: astore          8
        //   202: aload           5
        //   204: aload           8
        //   206: invokevirtual   android/support/v4/provider/FontsContractCompat$FontInfo.getUri:()Landroid/net/Uri;
        //   209: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   214: checkcast       Ljava/nio/ByteBuffer;
        //   217: astore_2       
        //   218: aload_2        
        //   219: ifnull          270
        //   222: aload           8
        //   224: invokevirtual   android/support/v4/provider/FontsContractCompat$FontInfo.getTtcIndex:()I
        //   227: istore          9
        //   229: aload           8
        //   231: invokevirtual   android/support/v4/provider/FontsContractCompat$FontInfo.getWeight:()I
        //   234: istore          10
        //   236: aload           8
        //   238: invokevirtual   android/support/v4/provider/FontsContractCompat$FontInfo.isItalic:()Z
        //   241: ifne            273
        //   244: iconst_0       
        //   245: istore          4
        //   247: aload_1        
        //   248: aload_2        
        //   249: iload           9
        //   251: iload           10
        //   253: iload           4
        //   255: invokestatic    android/support/v4/graphics/TypefaceCompatApi26Impl.addFontFromBuffer:(Ljava/lang/Object;Ljava/nio/ByteBuffer;III)Z
        //   258: ifeq            279
        //   261: iconst_1       
        //   262: istore          4
        //   264: iinc            7, 1
        //   267: goto            34
        //   270: goto            264
        //   273: iconst_1       
        //   274: istore          4
        //   276: goto            247
        //   279: aload_1        
        //   280: invokestatic    android/support/v4/graphics/TypefaceCompatApi26Impl.abortCreation:(Ljava/lang/Object;)Z
        //   283: pop            
        //   284: aconst_null    
        //   285: areturn        
        //   286: aload_1        
        //   287: invokestatic    android/support/v4/graphics/TypefaceCompatApi26Impl.abortCreation:(Ljava/lang/Object;)Z
        //   290: pop            
        //   291: aconst_null    
        //   292: areturn        
        //   293: aconst_null    
        //   294: areturn        
        //   295: astore_2       
        //   296: goto            163
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  74     88     140    143    Ljava/io/IOException;
        //  90     123    159    163    Ljava/lang/Throwable;
        //  90     123    295    299    Any
        //  133    137    140    143    Ljava/io/IOException;
        //  143    147    150    159    Ljava/lang/Throwable;
        //  143    147    140    143    Ljava/io/IOException;
        //  151    159    140    143    Ljava/io/IOException;
        //  160    162    162    163    Any
        //  167    169    140    143    Ljava/io/IOException;
        //  173    177    140    143    Ljava/io/IOException;
        //  180    184    187    196    Ljava/lang/Throwable;
        //  180    184    140    143    Ljava/io/IOException;
        //  188    193    140    143    Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException: Cannot invoke "com.strobel.assembler.metadata.TypeReference.getSimpleType()" because "type" is null
        //     at com.strobel.assembler.ir.StackMappingVisitor.push(StackMappingVisitor.java:290)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.execute(StackMappingVisitor.java:837)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.visit(StackMappingVisitor.java:398)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2086)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
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
    @Override
    public Typeface createFromResourcesFontFile(final Context context, final Resources resources, final int n, final String s, final int n2) {
        if (!isFontFamilyPrivateAPIAvailable()) {
            return super.createFromResourcesFontFile(context, resources, n, s, n2);
        }
        final Object family = newFamily();
        if (!addFontFromAssetManager(context, family, s, 0, -1, -1)) {
            abortCreation(family);
            return null;
        }
        if (freeze(family)) {
            return createFromFamiliesWithDefault(family);
        }
        return null;
    }
}

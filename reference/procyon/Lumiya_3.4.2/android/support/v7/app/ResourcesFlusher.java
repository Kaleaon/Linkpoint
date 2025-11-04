// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.app;

import android.util.LongSparseArray;
import android.support.annotation.RequiresApi;
import java.util.Map;
import android.util.Log;
import android.os.Build$VERSION;
import android.support.annotation.NonNull;
import android.content.res.Resources;
import java.lang.reflect.Field;

class ResourcesFlusher
{
    private static final String TAG = "ResourcesFlusher";
    private static Field sDrawableCacheField;
    private static boolean sDrawableCacheFieldFetched;
    private static Field sResourcesImplField;
    private static boolean sResourcesImplFieldFetched;
    private static Class sThemedResourceCacheClazz;
    private static boolean sThemedResourceCacheClazzFetched;
    private static Field sThemedResourceCache_mUnthemedEntriesField;
    private static boolean sThemedResourceCache_mUnthemedEntriesFieldFetched;
    
    static boolean flush(@NonNull final Resources resources) {
        if (Build$VERSION.SDK_INT >= 24) {
            return flushNougats(resources);
        }
        if (Build$VERSION.SDK_INT < 23) {
            return Build$VERSION.SDK_INT >= 21 && flushLollipops(resources);
        }
        return flushMarshmallows(resources);
    }
    
    @RequiresApi(21)
    private static boolean flushLollipops(@NonNull final Resources obj) {
        Label_0051: {
            Block_2: {
                Label_0006: {
                    if (!ResourcesFlusher.sDrawableCacheFieldFetched) {
                        while (true) {
                            try {
                                (ResourcesFlusher.sDrawableCacheField = Resources.class.getDeclaredField("mDrawableCache")).setAccessible(true);
                                ResourcesFlusher.sDrawableCacheFieldFetched = true;
                                break Label_0006;
                            }
                            catch (final NoSuchFieldException ex) {
                                Log.e("ResourcesFlusher", "Could not retrieve Resources#mDrawableCache field", (Throwable)ex);
                                continue;
                            }
                            break;
                        }
                        break Block_2;
                    }
                }
                if (ResourcesFlusher.sDrawableCacheField != null) {
                    break Label_0051;
                }
                return false;
            }
            while (true) {
                try {
                    final Map map = (Map)ResourcesFlusher.sDrawableCacheField.get(obj);
                    if (map != null) {
                        map.clear();
                        return true;
                    }
                    return false;
                }
                catch (final IllegalAccessException ex2) {
                    Log.e("ResourcesFlusher", "Could not retrieve value from Resources#mDrawableCache", (Throwable)ex2);
                    final Map map = null;
                    continue;
                }
                break;
            }
        }
    }
    
    @RequiresApi(23)
    private static boolean flushMarshmallows(@NonNull final Resources obj) {
        Object value = null;
        Label_0063: {
            Block_4: {
                Label_0006: {
                    if (!ResourcesFlusher.sDrawableCacheFieldFetched) {
                        while (true) {
                            try {
                                (ResourcesFlusher.sDrawableCacheField = Resources.class.getDeclaredField("mDrawableCache")).setAccessible(true);
                                ResourcesFlusher.sDrawableCacheFieldFetched = true;
                                break Label_0006;
                            }
                            catch (final NoSuchFieldException ex) {
                                Log.e("ResourcesFlusher", "Could not retrieve Resources#mDrawableCache field", (Throwable)ex);
                                continue;
                            }
                            break;
                        }
                        break Block_4;
                    }
                }
                if (ResourcesFlusher.sDrawableCacheField != null) {
                    break Label_0063;
                }
                value = null;
                return value != null && value != null && flushThemedResourcesCache(value);
            }
            try {
                value = ResourcesFlusher.sDrawableCacheField.get(obj);
            }
            catch (final IllegalAccessException ex2) {
                Log.e("ResourcesFlusher", "Could not retrieve value from Resources#mDrawableCache", (Throwable)ex2);
                value = null;
            }
        }
        return value != null && value != null && flushThemedResourcesCache(value);
    }
    
    @RequiresApi(24)
    private static boolean flushNougats(@NonNull final Resources p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: ifeq            46
        //     6: getstatic       android/support/v7/app/ResourcesFlusher.sResourcesImplField:Ljava/lang/reflect/Field;
        //     9: ifnull          83
        //    12: getstatic       android/support/v7/app/ResourcesFlusher.sResourcesImplField:Ljava/lang/reflect/Field;
        //    15: aload_0        
        //    16: invokevirtual   java/lang/reflect/Field.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //    19: astore_0       
        //    20: aload_0        
        //    21: ifnull          100
        //    24: getstatic       android/support/v7/app/ResourcesFlusher.sDrawableCacheFieldFetched:Z
        //    27: ifeq            102
        //    30: getstatic       android/support/v7/app/ResourcesFlusher.sDrawableCacheField:Ljava/lang/reflect/Field;
        //    33: ifnonnull       141
        //    36: aconst_null    
        //    37: astore_0       
        //    38: aload_0        
        //    39: ifnonnull       167
        //    42: iconst_0       
        //    43: istore_1       
        //    44: iload_1        
        //    45: ireturn        
        //    46: ldc             Landroid/content/res/Resources;.class
        //    48: ldc             "mResourcesImpl"
        //    50: invokevirtual   java/lang/Class.getDeclaredField:(Ljava/lang/String;)Ljava/lang/reflect/Field;
        //    53: putstatic       android/support/v7/app/ResourcesFlusher.sResourcesImplField:Ljava/lang/reflect/Field;
        //    56: getstatic       android/support/v7/app/ResourcesFlusher.sResourcesImplField:Ljava/lang/reflect/Field;
        //    59: iconst_1       
        //    60: invokevirtual   java/lang/reflect/Field.setAccessible:(Z)V
        //    63: iconst_1       
        //    64: putstatic       android/support/v7/app/ResourcesFlusher.sResourcesImplFieldFetched:Z
        //    67: goto            6
        //    70: astore_2       
        //    71: ldc             "ResourcesFlusher"
        //    73: ldc             "Could not retrieve Resources#mResourcesImpl field"
        //    75: aload_2        
        //    76: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //    79: pop            
        //    80: goto            63
        //    83: iconst_0       
        //    84: ireturn        
        //    85: astore_0       
        //    86: ldc             "ResourcesFlusher"
        //    88: ldc             "Could not retrieve value from Resources#mResourcesImpl"
        //    90: aload_0        
        //    91: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //    94: pop            
        //    95: aconst_null    
        //    96: astore_0       
        //    97: goto            20
        //   100: iconst_0       
        //   101: ireturn        
        //   102: aload_0        
        //   103: invokevirtual   java/lang/Object.getClass:()Ljava/lang/Class;
        //   106: ldc             "mDrawableCache"
        //   108: invokevirtual   java/lang/Class.getDeclaredField:(Ljava/lang/String;)Ljava/lang/reflect/Field;
        //   111: putstatic       android/support/v7/app/ResourcesFlusher.sDrawableCacheField:Ljava/lang/reflect/Field;
        //   114: getstatic       android/support/v7/app/ResourcesFlusher.sDrawableCacheField:Ljava/lang/reflect/Field;
        //   117: iconst_1       
        //   118: invokevirtual   java/lang/reflect/Field.setAccessible:(Z)V
        //   121: iconst_1       
        //   122: putstatic       android/support/v7/app/ResourcesFlusher.sDrawableCacheFieldFetched:Z
        //   125: goto            30
        //   128: astore_2       
        //   129: ldc             "ResourcesFlusher"
        //   131: ldc             "Could not retrieve ResourcesImpl#mDrawableCache field"
        //   133: aload_2        
        //   134: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   137: pop            
        //   138: goto            121
        //   141: getstatic       android/support/v7/app/ResourcesFlusher.sDrawableCacheField:Ljava/lang/reflect/Field;
        //   144: aload_0        
        //   145: invokevirtual   java/lang/reflect/Field.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   148: astore_0       
        //   149: goto            38
        //   152: astore_0       
        //   153: ldc             "ResourcesFlusher"
        //   155: ldc             "Could not retrieve value from ResourcesImpl#mDrawableCache"
        //   157: aload_0        
        //   158: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   161: pop            
        //   162: aconst_null    
        //   163: astore_0       
        //   164: goto            38
        //   167: aload_0        
        //   168: invokestatic    android/support/v7/app/ResourcesFlusher.flushThemedResourcesCache:(Ljava/lang/Object;)Z
        //   171: ifeq            42
        //   174: iconst_1       
        //   175: istore_1       
        //   176: goto            44
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                              
        //  -----  -----  -----  -----  ----------------------------------
        //  12     20     85     100    Ljava/lang/IllegalAccessException;
        //  46     63     70     83     Ljava/lang/NoSuchFieldException;
        //  102    121    128    141    Ljava/lang/NoSuchFieldException;
        //  141    149    152    167    Ljava/lang/IllegalAccessException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: invokestatic:boolean(ResourcesFlusher::flushThemedResourcesCache, var_0_25:Object)
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
    
    @RequiresApi(16)
    private static boolean flushThemedResourcesCache(@NonNull final Object obj) {
        LongSparseArray longSparseArray = null;
        Label_0041: {
            if (!ResourcesFlusher.sThemedResourceCacheClazzFetched) {
                break Label_0041;
            }
        Label_0018_Outer:
            while (true) {
                if (ResourcesFlusher.sThemedResourceCacheClazz == null) {
                    return false;
                }
                Label_0071: {
                    if (!ResourcesFlusher.sThemedResourceCache_mUnthemedEntriesFieldFetched) {
                        break Label_0071;
                    }
                Label_0035_Outer:
                    while (true) {
                        if (ResourcesFlusher.sThemedResourceCache_mUnthemedEntriesField == null) {
                            return false;
                        }
                        while (true) {
                            try {
                                longSparseArray = (LongSparseArray)ResourcesFlusher.sThemedResourceCache_mUnthemedEntriesField.get(obj);
                                if (longSparseArray == null) {
                                    return false;
                                }
                                break Label_0041;
                                while (true) {
                                    try {
                                        (ResourcesFlusher.sThemedResourceCache_mUnthemedEntriesField = ResourcesFlusher.sThemedResourceCacheClazz.getDeclaredField("mUnthemedEntries")).setAccessible(true);
                                        ResourcesFlusher.sThemedResourceCache_mUnthemedEntriesFieldFetched = true;
                                        continue Label_0035_Outer;
                                    }
                                    catch (final NoSuchFieldException ex) {
                                        Log.e("ResourcesFlusher", "Could not retrieve ThemedResourceCache#mUnthemedEntries field", (Throwable)ex);
                                        continue;
                                    }
                                    break;
                                }
                                return false;
                                while (true) {
                                    try {
                                        ResourcesFlusher.sThemedResourceCacheClazz = Class.forName("android.content.res.ThemedResourceCache");
                                        ResourcesFlusher.sThemedResourceCacheClazzFetched = true;
                                        continue Label_0018_Outer;
                                    }
                                    catch (final ClassNotFoundException ex2) {
                                        Log.e("ResourcesFlusher", "Could not find ThemedResourceCache class", (Throwable)ex2);
                                        continue;
                                    }
                                    break;
                                }
                                return false;
                            }
                            catch (final IllegalAccessException ex3) {
                                Log.e("ResourcesFlusher", "Could not retrieve value from ThemedResourceCache#mUnthemedEntries", (Throwable)ex3);
                                longSparseArray = null;
                                continue;
                            }
                            break;
                        }
                        break;
                    }
                }
                break;
            }
        }
        longSparseArray.clear();
        return true;
    }
}

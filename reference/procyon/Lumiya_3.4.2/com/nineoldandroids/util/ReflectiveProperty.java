// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

class ReflectiveProperty<T, V> extends Property<T, V>
{
    private static final String PREFIX_GET = "get";
    private static final String PREFIX_IS = "is";
    private static final String PREFIX_SET = "set";
    private Field mField;
    private Method mGetter;
    private Method mSetter;
    
    public ReflectiveProperty(final Class<T> p0, final Class<V> p1, final String p2) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aload_2        
        //     2: aload_3        
        //     3: invokespecial   com/nineoldandroids/util/Property.<init>:(Ljava/lang/Class;Ljava/lang/String;)V
        //     6: aload_3        
        //     7: iconst_0       
        //     8: invokevirtual   java/lang/String.charAt:(I)C
        //    11: invokestatic    java/lang/Character.toUpperCase:(C)C
        //    14: istore          4
        //    16: aload_3        
        //    17: iconst_1       
        //    18: invokevirtual   java/lang/String.substring:(I)Ljava/lang/String;
        //    21: astore          5
        //    23: new             Ljava/lang/StringBuilder;
        //    26: dup            
        //    27: invokespecial   java/lang/StringBuilder.<init>:()V
        //    30: iload           4
        //    32: invokevirtual   java/lang/StringBuilder.append:(C)Ljava/lang/StringBuilder;
        //    35: aload           5
        //    37: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    40: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    43: astore          5
        //    45: new             Ljava/lang/StringBuilder;
        //    48: dup            
        //    49: invokespecial   java/lang/StringBuilder.<init>:()V
        //    52: ldc             "get"
        //    54: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    57: aload           5
        //    59: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    62: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    65: astore          6
        //    67: aload_0        
        //    68: aload_1        
        //    69: aload           6
        //    71: aconst_null    
        //    72: checkcast       [Ljava/lang/Class;
        //    75: invokevirtual   java/lang/Class.getMethod:(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        //    78: putfield        com/nineoldandroids/util/ReflectiveProperty.mGetter:Ljava/lang/reflect/Method;
        //    81: aload_0        
        //    82: getfield        com/nineoldandroids/util/ReflectiveProperty.mGetter:Ljava/lang/reflect/Method;
        //    85: invokevirtual   java/lang/reflect/Method.getReturnType:()Ljava/lang/Class;
        //    88: astore_3       
        //    89: aload_0        
        //    90: aload_2        
        //    91: aload_3        
        //    92: invokespecial   com/nineoldandroids/util/ReflectiveProperty.typesMatch:(Ljava/lang/Class;Ljava/lang/Class;)Z
        //    95: ifeq            353
        //    98: new             Ljava/lang/StringBuilder;
        //   101: dup            
        //   102: invokespecial   java/lang/StringBuilder.<init>:()V
        //   105: ldc             "set"
        //   107: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   110: aload           5
        //   112: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   115: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   118: astore_2       
        //   119: aload_0        
        //   120: aload_1        
        //   121: aload_2        
        //   122: iconst_1       
        //   123: anewarray       Ljava/lang/Class;
        //   126: dup            
        //   127: iconst_0       
        //   128: aload_3        
        //   129: aastore        
        //   130: invokevirtual   java/lang/Class.getDeclaredMethod:(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        //   133: putfield        com/nineoldandroids/util/ReflectiveProperty.mSetter:Ljava/lang/reflect/Method;
        //   136: aload_0        
        //   137: getfield        com/nineoldandroids/util/ReflectiveProperty.mSetter:Ljava/lang/reflect/Method;
        //   140: iconst_1       
        //   141: invokevirtual   java/lang/reflect/Method.setAccessible:(Z)V
        //   144: return         
        //   145: astore          7
        //   147: aload_0        
        //   148: aload_1        
        //   149: aload           6
        //   151: aconst_null    
        //   152: checkcast       [Ljava/lang/Class;
        //   155: invokevirtual   java/lang/Class.getDeclaredMethod:(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        //   158: putfield        com/nineoldandroids/util/ReflectiveProperty.mGetter:Ljava/lang/reflect/Method;
        //   161: aload_0        
        //   162: getfield        com/nineoldandroids/util/ReflectiveProperty.mGetter:Ljava/lang/reflect/Method;
        //   165: iconst_1       
        //   166: invokevirtual   java/lang/reflect/Method.setAccessible:(Z)V
        //   169: goto            81
        //   172: astore          6
        //   174: new             Ljava/lang/StringBuilder;
        //   177: dup            
        //   178: invokespecial   java/lang/StringBuilder.<init>:()V
        //   181: ldc             "is"
        //   183: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   186: aload           5
        //   188: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   191: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   194: astore          6
        //   196: aload_0        
        //   197: aload_1        
        //   198: aload           6
        //   200: aconst_null    
        //   201: checkcast       [Ljava/lang/Class;
        //   204: invokevirtual   java/lang/Class.getMethod:(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        //   207: putfield        com/nineoldandroids/util/ReflectiveProperty.mGetter:Ljava/lang/reflect/Method;
        //   210: goto            81
        //   213: astore          7
        //   215: aload_0        
        //   216: aload_1        
        //   217: aload           6
        //   219: aconst_null    
        //   220: checkcast       [Ljava/lang/Class;
        //   223: invokevirtual   java/lang/Class.getDeclaredMethod:(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        //   226: putfield        com/nineoldandroids/util/ReflectiveProperty.mGetter:Ljava/lang/reflect/Method;
        //   229: aload_0        
        //   230: getfield        com/nineoldandroids/util/ReflectiveProperty.mGetter:Ljava/lang/reflect/Method;
        //   233: iconst_1       
        //   234: invokevirtual   java/lang/reflect/Method.setAccessible:(Z)V
        //   237: goto            81
        //   240: astore          5
        //   242: aload_0        
        //   243: aload_1        
        //   244: aload_3        
        //   245: invokevirtual   java/lang/Class.getField:(Ljava/lang/String;)Ljava/lang/reflect/Field;
        //   248: putfield        com/nineoldandroids/util/ReflectiveProperty.mField:Ljava/lang/reflect/Field;
        //   251: aload_0        
        //   252: getfield        com/nineoldandroids/util/ReflectiveProperty.mField:Ljava/lang/reflect/Field;
        //   255: invokevirtual   java/lang/reflect/Field.getType:()Ljava/lang/Class;
        //   258: astore          5
        //   260: aload_0        
        //   261: aload_2        
        //   262: aload           5
        //   264: invokespecial   com/nineoldandroids/util/ReflectiveProperty.typesMatch:(Ljava/lang/Class;Ljava/lang/Class;)Z
        //   267: ifeq            271
        //   270: return         
        //   271: new             Lcom/nineoldandroids/util/NoSuchPropertyException;
        //   274: astore          6
        //   276: new             Ljava/lang/StringBuilder;
        //   279: astore_1       
        //   280: aload_1        
        //   281: invokespecial   java/lang/StringBuilder.<init>:()V
        //   284: aload           6
        //   286: aload_1        
        //   287: ldc             "Underlying type ("
        //   289: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   292: aload           5
        //   294: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   297: ldc             ") "
        //   299: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   302: ldc             "does not match Property type ("
        //   304: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   307: aload_2        
        //   308: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   311: ldc             ")"
        //   313: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   316: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   319: invokespecial   com/nineoldandroids/util/NoSuchPropertyException.<init>:(Ljava/lang/String;)V
        //   322: aload           6
        //   324: athrow         
        //   325: astore_1       
        //   326: new             Lcom/nineoldandroids/util/NoSuchPropertyException;
        //   329: dup            
        //   330: new             Ljava/lang/StringBuilder;
        //   333: dup            
        //   334: invokespecial   java/lang/StringBuilder.<init>:()V
        //   337: ldc             "No accessor method or field found for property with name "
        //   339: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   342: aload_3        
        //   343: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   346: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   349: invokespecial   com/nineoldandroids/util/NoSuchPropertyException.<init>:(Ljava/lang/String;)V
        //   352: athrow         
        //   353: new             Lcom/nineoldandroids/util/NoSuchPropertyException;
        //   356: dup            
        //   357: new             Ljava/lang/StringBuilder;
        //   360: dup            
        //   361: invokespecial   java/lang/StringBuilder.<init>:()V
        //   364: ldc             "Underlying type ("
        //   366: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   369: aload_3        
        //   370: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   373: ldc             ") "
        //   375: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   378: ldc             "does not match Property type ("
        //   380: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   383: aload_2        
        //   384: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   387: ldc             ")"
        //   389: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   392: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   395: invokespecial   com/nineoldandroids/util/NoSuchPropertyException.<init>:(Ljava/lang/String;)V
        //   398: athrow         
        //   399: astore_1       
        //   400: goto            144
        //    Signature:
        //  (Ljava/lang/Class<TT;>;Ljava/lang/Class<TV;>;Ljava/lang/String;)V
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                             
        //  -----  -----  -----  -----  ---------------------------------
        //  67     81     145    353    Ljava/lang/NoSuchMethodException;
        //  119    144    399    403    Ljava/lang/NoSuchMethodException;
        //  147    169    172    353    Ljava/lang/NoSuchMethodException;
        //  196    210    213    353    Ljava/lang/NoSuchMethodException;
        //  215    237    240    353    Ljava/lang/NoSuchMethodException;
        //  242    270    325    353    Ljava/lang/NoSuchFieldException;
        //  271    325    325    353    Ljava/lang/NoSuchFieldException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0144:
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
    
    private boolean typesMatch(final Class<V> clazz, final Class clazz2) {
        boolean b = false;
        if (clazz2 == clazz) {
            return true;
        }
        if (!clazz2.isPrimitive()) {
            return false;
        }
        if (clazz2 == Float.TYPE && clazz == Float.class) {
            return true;
        }
        if (clazz2 == Integer.TYPE && clazz == Integer.class) {
            return true;
        }
        if (clazz2 == Boolean.TYPE && clazz == Boolean.class) {
            return true;
        }
        if (clazz2 == Long.TYPE && clazz == Long.class) {
            return true;
        }
        if (clazz2 == Double.TYPE && clazz == Double.class) {
            return true;
        }
        if (clazz2 == Short.TYPE && clazz == Short.class) {
            return true;
        }
        if (clazz2 == Byte.TYPE && clazz == Byte.class) {
            return true;
        }
        if (clazz2 == Character.TYPE) {
            if (clazz == Character.class) {
                return true;
            }
        }
        return b;
        b = true;
        return b;
    }
    
    @Override
    public V get(final T t) {
        if (this.mGetter == null) {
            if (this.mField == null) {
                throw new AssertionError();
            }
        }
        else {
            try {
                return (V)this.mGetter.invoke(t, (Object[])null);
            }
            catch (final IllegalAccessException ex) {
                throw new AssertionError();
            }
            catch (final InvocationTargetException ex2) {
                throw new RuntimeException(ex2.getCause());
            }
        }
        try {
            return (V)this.mField.get(t);
        }
        catch (final IllegalAccessException ex3) {
            throw new AssertionError();
        }
    }
    
    @Override
    public boolean isReadOnly() {
        return this.mSetter == null && this.mField == null;
    }
    
    @Override
    public void set(final T t, final V value) {
        Label_0089: {
            if (this.mSetter == null) {
                if (this.mField == null) {
                    throw new UnsupportedOperationException("Property " + this.getName() + " is read-only");
                }
                break Label_0089;
            }
            try {
                this.mSetter.invoke(t, value);
                return;
            }
            catch (final IllegalAccessException ex) {
                throw new AssertionError();
            }
            catch (final InvocationTargetException ex2) {
                throw new RuntimeException(ex2.getCause());
            }
            try {
                this.mField.set(t, value);
            }
            catch (final IllegalAccessException ex3) {
                throw new AssertionError();
            }
        }
    }
}

// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.animation;

import com.nineoldandroids.util.IntProperty;
import com.nineoldandroids.util.FloatProperty;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import android.util.Log;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import com.nineoldandroids.util.Property;
import java.lang.reflect.Method;
import java.util.HashMap;

public class PropertyValuesHolder implements Cloneable
{
    private static Class[] DOUBLE_VARIANTS;
    private static Class[] FLOAT_VARIANTS;
    private static Class[] INTEGER_VARIANTS;
    private static final TypeEvaluator sFloatEvaluator;
    private static final HashMap<Class, HashMap<String, Method>> sGetterPropertyMap;
    private static final TypeEvaluator sIntEvaluator;
    private static final HashMap<Class, HashMap<String, Method>> sSetterPropertyMap;
    private Object mAnimatedValue;
    private TypeEvaluator mEvaluator;
    private Method mGetter;
    KeyframeSet mKeyframeSet;
    protected Property mProperty;
    final ReentrantReadWriteLock mPropertyMapLock;
    String mPropertyName;
    Method mSetter;
    final Object[] mTmpValueArray;
    Class mValueType;
    
    static {
        sIntEvaluator = new IntEvaluator();
        sFloatEvaluator = new FloatEvaluator();
        PropertyValuesHolder.FLOAT_VARIANTS = new Class[] { Float.TYPE, Float.class, Double.TYPE, Integer.TYPE, Double.class, Integer.class };
        PropertyValuesHolder.INTEGER_VARIANTS = new Class[] { Integer.TYPE, Integer.class, Float.TYPE, Double.TYPE, Float.class, Double.class };
        PropertyValuesHolder.DOUBLE_VARIANTS = new Class[] { Double.TYPE, Double.class, Float.TYPE, Integer.TYPE, Float.class, Integer.class };
        sSetterPropertyMap = new HashMap<Class, HashMap<String, Method>>();
        sGetterPropertyMap = new HashMap<Class, HashMap<String, Method>>();
    }
    
    private PropertyValuesHolder(final Property mProperty) {
        this.mSetter = null;
        this.mGetter = null;
        this.mKeyframeSet = null;
        this.mPropertyMapLock = new ReentrantReadWriteLock();
        this.mTmpValueArray = new Object[1];
        this.mProperty = mProperty;
        if (mProperty != null) {
            this.mPropertyName = mProperty.getName();
        }
    }
    
    private PropertyValuesHolder(final String mPropertyName) {
        this.mSetter = null;
        this.mGetter = null;
        this.mKeyframeSet = null;
        this.mPropertyMapLock = new ReentrantReadWriteLock();
        this.mTmpValueArray = new Object[1];
        this.mPropertyName = mPropertyName;
    }
    
    static String getMethodName(final String str, String substring) {
        if (substring != null && substring.length() != 0) {
            final char upperCase = Character.toUpperCase(substring.charAt(0));
            substring = substring.substring(1);
            return str + upperCase + substring;
        }
        return str;
    }
    
    private Method getPropertyFunction(final Class p0, final String p1, final Class p2) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: astore          4
        //     3: aconst_null    
        //     4: astore          5
        //     6: aload_2        
        //     7: aload_0        
        //     8: getfield        com/nineoldandroids/animation/PropertyValuesHolder.mPropertyName:Ljava/lang/String;
        //    11: invokestatic    com/nineoldandroids/animation/PropertyValuesHolder.getMethodName:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //    14: astore          6
        //    16: aload_3        
        //    17: ifnull          133
        //    20: iconst_1       
        //    21: anewarray       Ljava/lang/Class;
        //    24: astore          4
        //    26: aload_0        
        //    27: getfield        com/nineoldandroids/animation/PropertyValuesHolder.mValueType:Ljava/lang/Class;
        //    30: ldc             Ljava/lang/Float;.class
        //    32: invokevirtual   java/lang/Object.equals:(Ljava/lang/Object;)Z
        //    35: ifne            209
        //    38: aload_0        
        //    39: getfield        com/nineoldandroids/animation/PropertyValuesHolder.mValueType:Ljava/lang/Class;
        //    42: ldc             Ljava/lang/Integer;.class
        //    44: invokevirtual   java/lang/Object.equals:(Ljava/lang/Object;)Z
        //    47: ifne            216
        //    50: aload_0        
        //    51: getfield        com/nineoldandroids/animation/PropertyValuesHolder.mValueType:Ljava/lang/Class;
        //    54: ldc             Ljava/lang/Double;.class
        //    56: invokevirtual   java/lang/Object.equals:(Ljava/lang/Object;)Z
        //    59: ifne            223
        //    62: iconst_1       
        //    63: anewarray       Ljava/lang/Class;
        //    66: astore_2       
        //    67: aload_2        
        //    68: iconst_0       
        //    69: aload_0        
        //    70: getfield        com/nineoldandroids/animation/PropertyValuesHolder.mValueType:Ljava/lang/Class;
        //    73: aastore        
        //    74: aload_2        
        //    75: arraylength    
        //    76: istore          7
        //    78: iconst_0       
        //    79: istore          8
        //    81: aload           5
        //    83: astore_3       
        //    84: iload           8
        //    86: iload           7
        //    88: if_icmplt       230
        //    91: ldc             "PropertyValuesHolder"
        //    93: new             Ljava/lang/StringBuilder;
        //    96: dup            
        //    97: invokespecial   java/lang/StringBuilder.<init>:()V
        //   100: ldc             "Couldn't find setter/getter for property "
        //   102: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   105: aload_0        
        //   106: getfield        com/nineoldandroids/animation/PropertyValuesHolder.mPropertyName:Ljava/lang/String;
        //   109: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   112: ldc             " with value type "
        //   114: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   117: aload_0        
        //   118: getfield        com/nineoldandroids/animation/PropertyValuesHolder.mValueType:Ljava/lang/Class;
        //   121: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   124: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   127: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;)I
        //   130: pop            
        //   131: aload_3        
        //   132: areturn        
        //   133: aload_1        
        //   134: aload           6
        //   136: aconst_null    
        //   137: invokevirtual   java/lang/Class.getMethod:(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        //   140: astore_3       
        //   141: goto            131
        //   144: astore_2       
        //   145: aload           4
        //   147: astore_3       
        //   148: aload_1        
        //   149: aload           6
        //   151: aconst_null    
        //   152: invokevirtual   java/lang/Class.getDeclaredMethod:(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        //   155: astore_1       
        //   156: aload_1        
        //   157: astore_3       
        //   158: aload_1        
        //   159: iconst_1       
        //   160: invokevirtual   java/lang/reflect/Method.setAccessible:(Z)V
        //   163: aload_1        
        //   164: astore_3       
        //   165: goto            131
        //   168: astore_1       
        //   169: ldc             "PropertyValuesHolder"
        //   171: new             Ljava/lang/StringBuilder;
        //   174: dup            
        //   175: invokespecial   java/lang/StringBuilder.<init>:()V
        //   178: ldc             "Couldn't find no-arg method for property "
        //   180: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   183: aload_0        
        //   184: getfield        com/nineoldandroids/animation/PropertyValuesHolder.mPropertyName:Ljava/lang/String;
        //   187: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   190: ldc             ": "
        //   192: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   195: aload_2        
        //   196: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   199: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   202: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;)I
        //   205: pop            
        //   206: goto            131
        //   209: getstatic       com/nineoldandroids/animation/PropertyValuesHolder.FLOAT_VARIANTS:[Ljava/lang/Class;
        //   212: astore_2       
        //   213: goto            74
        //   216: getstatic       com/nineoldandroids/animation/PropertyValuesHolder.INTEGER_VARIANTS:[Ljava/lang/Class;
        //   219: astore_2       
        //   220: goto            74
        //   223: getstatic       com/nineoldandroids/animation/PropertyValuesHolder.DOUBLE_VARIANTS:[Ljava/lang/Class;
        //   226: astore_2       
        //   227: goto            74
        //   230: aload_2        
        //   231: iload           8
        //   233: aaload         
        //   234: astore          9
        //   236: aload           4
        //   238: iconst_0       
        //   239: aload           9
        //   241: aastore        
        //   242: aload_1        
        //   243: aload           6
        //   245: aload           4
        //   247: invokevirtual   java/lang/Class.getMethod:(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        //   250: astore          5
        //   252: aload           5
        //   254: astore_3       
        //   255: aload_0        
        //   256: aload           9
        //   258: putfield        com/nineoldandroids/animation/PropertyValuesHolder.mValueType:Ljava/lang/Class;
        //   261: aload           5
        //   263: areturn        
        //   264: astore          5
        //   266: aload_1        
        //   267: aload           6
        //   269: aload           4
        //   271: invokevirtual   java/lang/Class.getDeclaredMethod:(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        //   274: astore          5
        //   276: aload           5
        //   278: astore_3       
        //   279: aload           5
        //   281: iconst_1       
        //   282: invokevirtual   java/lang/reflect/Method.setAccessible:(Z)V
        //   285: aload           5
        //   287: astore_3       
        //   288: aload_0        
        //   289: aload           9
        //   291: putfield        com/nineoldandroids/animation/PropertyValuesHolder.mValueType:Ljava/lang/Class;
        //   294: aload           5
        //   296: areturn        
        //   297: astore          5
        //   299: iinc            8, 1
        //   302: goto            84
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                             
        //  -----  -----  -----  -----  ---------------------------------
        //  133    141    144    209    Ljava/lang/NoSuchMethodException;
        //  148    156    168    209    Ljava/lang/NoSuchMethodException;
        //  158    163    168    209    Ljava/lang/NoSuchMethodException;
        //  242    252    264    305    Ljava/lang/NoSuchMethodException;
        //  255    261    264    305    Ljava/lang/NoSuchMethodException;
        //  266    276    297    305    Ljava/lang/NoSuchMethodException;
        //  279    285    297    305    Ljava/lang/NoSuchMethodException;
        //  288    294    297    305    Ljava/lang/NoSuchMethodException;
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
    
    public static PropertyValuesHolder ofFloat(final Property<?, Float> property, final float... array) {
        return new FloatPropertyValuesHolder(property, array);
    }
    
    public static PropertyValuesHolder ofFloat(final String s, final float... array) {
        return new FloatPropertyValuesHolder(s, array);
    }
    
    public static PropertyValuesHolder ofInt(final Property<?, Integer> property, final int... array) {
        return new IntPropertyValuesHolder(property, array);
    }
    
    public static PropertyValuesHolder ofInt(final String s, final int... array) {
        return new IntPropertyValuesHolder(s, array);
    }
    
    public static PropertyValuesHolder ofKeyframe(final Property property, final Keyframe... array) {
        final KeyframeSet ofKeyframe = KeyframeSet.ofKeyframe(array);
        if (ofKeyframe instanceof IntKeyframeSet) {
            return new IntPropertyValuesHolder(property, (IntKeyframeSet)ofKeyframe);
        }
        if (!(ofKeyframe instanceof FloatKeyframeSet)) {
            final PropertyValuesHolder propertyValuesHolder = new PropertyValuesHolder(property);
            propertyValuesHolder.mKeyframeSet = ofKeyframe;
            propertyValuesHolder.mValueType = array[0].getType();
            return propertyValuesHolder;
        }
        return new FloatPropertyValuesHolder(property, (FloatKeyframeSet)ofKeyframe);
    }
    
    public static PropertyValuesHolder ofKeyframe(final String s, final Keyframe... array) {
        final KeyframeSet ofKeyframe = KeyframeSet.ofKeyframe(array);
        if (ofKeyframe instanceof IntKeyframeSet) {
            return new IntPropertyValuesHolder(s, (IntKeyframeSet)ofKeyframe);
        }
        if (!(ofKeyframe instanceof FloatKeyframeSet)) {
            final PropertyValuesHolder propertyValuesHolder = new PropertyValuesHolder(s);
            propertyValuesHolder.mKeyframeSet = ofKeyframe;
            propertyValuesHolder.mValueType = array[0].getType();
            return propertyValuesHolder;
        }
        return new FloatPropertyValuesHolder(s, (FloatKeyframeSet)ofKeyframe);
    }
    
    public static <V> PropertyValuesHolder ofObject(final Property property, final TypeEvaluator<V> evaluator, final V... objectValues) {
        final PropertyValuesHolder propertyValuesHolder = new PropertyValuesHolder(property);
        propertyValuesHolder.setObjectValues((Object[])objectValues);
        propertyValuesHolder.setEvaluator(evaluator);
        return propertyValuesHolder;
    }
    
    public static PropertyValuesHolder ofObject(final String s, final TypeEvaluator evaluator, final Object... objectValues) {
        final PropertyValuesHolder propertyValuesHolder = new PropertyValuesHolder(s);
        propertyValuesHolder.setObjectValues(objectValues);
        propertyValuesHolder.setEvaluator(evaluator);
        return propertyValuesHolder;
    }
    
    private void setupGetter(final Class clazz) {
        this.mGetter = this.setupSetterOrGetter(clazz, PropertyValuesHolder.sGetterPropertyMap, "get", null);
    }
    
    private Method setupSetterOrGetter(final Class clazz, final HashMap<Class, HashMap<String, Method>> hashMap, final String s, final Class clazz2) {
        Method method = null;
        try {
            this.mPropertyMapLock.writeLock().lock();
            final HashMap hashMap2 = hashMap.get(clazz);
            if (hashMap2 != null) {
                method = (Method)hashMap2.get(this.mPropertyName);
            }
            Method method2;
            if (method != null) {
                method2 = method;
            }
            else {
                final Method propertyFunction = this.getPropertyFunction(clazz, s, clazz2);
                HashMap hashMap3;
                if (hashMap2 != null) {
                    hashMap3 = hashMap2;
                }
                else {
                    final HashMap value = new HashMap();
                    hashMap.put(clazz, value);
                    hashMap3 = value;
                }
                hashMap3.put(this.mPropertyName, propertyFunction);
                method2 = propertyFunction;
            }
            return method2;
        }
        finally {
            this.mPropertyMapLock.writeLock().unlock();
        }
    }
    
    private void setupValue(final Object obj, final Keyframe keyframe) {
        Label_0031: {
            if (this.mProperty != null) {
                break Label_0031;
            }
            try {
                while (true) {
                    if (this.mGetter == null) {
                        this.setupGetter(obj.getClass());
                    }
                    keyframe.setValue(this.mGetter.invoke(obj, new Object[0]));
                    return;
                    keyframe.setValue(this.mProperty.get(obj));
                    continue;
                }
            }
            catch (final InvocationTargetException ex) {
                Log.e("PropertyValuesHolder", ex.toString());
            }
            catch (final IllegalAccessException ex2) {
                Log.e("PropertyValuesHolder", ex2.toString());
            }
        }
    }
    
    void calculateValue(final float n) {
        this.mAnimatedValue = this.mKeyframeSet.getValue(n);
    }
    
    public PropertyValuesHolder clone() {
        try {
            final PropertyValuesHolder propertyValuesHolder = (PropertyValuesHolder)super.clone();
            propertyValuesHolder.mPropertyName = this.mPropertyName;
            propertyValuesHolder.mProperty = this.mProperty;
            propertyValuesHolder.mKeyframeSet = this.mKeyframeSet.clone();
            propertyValuesHolder.mEvaluator = this.mEvaluator;
            return propertyValuesHolder;
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
    
    Object getAnimatedValue() {
        return this.mAnimatedValue;
    }
    
    public String getPropertyName() {
        return this.mPropertyName;
    }
    
    void init() {
        TypeEvaluator mEvaluator = null;
        if (this.mEvaluator == null) {
            if (this.mValueType != Integer.class) {
                if (this.mValueType == Float.class) {
                    mEvaluator = PropertyValuesHolder.sFloatEvaluator;
                }
            }
            else {
                mEvaluator = PropertyValuesHolder.sIntEvaluator;
            }
            this.mEvaluator = mEvaluator;
        }
        if (this.mEvaluator != null) {
            this.mKeyframeSet.setEvaluator(this.mEvaluator);
        }
    }
    
    void setAnimatedValue(final Object obj) {
        if (this.mProperty != null) {
            this.mProperty.set(obj, this.getAnimatedValue());
        }
        if (this.mSetter != null) {
            try {
                this.mTmpValueArray[0] = this.getAnimatedValue();
                this.mSetter.invoke(obj, this.mTmpValueArray);
            }
            catch (final InvocationTargetException ex) {
                Log.e("PropertyValuesHolder", ex.toString());
            }
            catch (final IllegalAccessException ex2) {
                Log.e("PropertyValuesHolder", ex2.toString());
            }
        }
    }
    
    public void setEvaluator(final TypeEvaluator typeEvaluator) {
        this.mEvaluator = typeEvaluator;
        this.mKeyframeSet.setEvaluator(typeEvaluator);
    }
    
    public void setFloatValues(final float... array) {
        this.mValueType = Float.TYPE;
        this.mKeyframeSet = KeyframeSet.ofFloat(array);
    }
    
    public void setIntValues(final int... array) {
        this.mValueType = Integer.TYPE;
        this.mKeyframeSet = KeyframeSet.ofInt(array);
    }
    
    public void setKeyframes(final Keyframe... array) {
        int i = 0;
        final int length = array.length;
        final Keyframe[] array2 = new Keyframe[Math.max(length, 2)];
        this.mValueType = array[0].getType();
        while (i < length) {
            array2[i] = array[i];
            ++i;
        }
        this.mKeyframeSet = new KeyframeSet(array2);
    }
    
    public void setObjectValues(final Object... array) {
        this.mValueType = array[0].getClass();
        this.mKeyframeSet = KeyframeSet.ofObject(array);
    }
    
    public void setProperty(final Property mProperty) {
        this.mProperty = mProperty;
    }
    
    public void setPropertyName(final String mPropertyName) {
        this.mPropertyName = mPropertyName;
    }
    
    void setupEndValue(final Object o) {
        this.setupValue(o, this.mKeyframeSet.mKeyframes.get(this.mKeyframeSet.mKeyframes.size() - 1));
    }
    
    void setupSetter(final Class clazz) {
        this.mSetter = this.setupSetterOrGetter(clazz, PropertyValuesHolder.sSetterPropertyMap, "set", this.mValueType);
    }
    
    void setupSetterAndGetter(final Object o) {
        while (true) {
            final Class<?> class1;
            Label_0159: {
                Label_0007: {
                    if (this.mProperty != null) {
                        try {
                            this.mProperty.get(o);
                            for (final Keyframe keyframe : this.mKeyframeSet.mKeyframes) {
                                if (!keyframe.hasValue()) {
                                    keyframe.setValue(this.mProperty.get(o));
                                }
                            }
                            return;
                        }
                        catch (final ClassCastException ex) {
                            Log.e("PropertyValuesHolder", "No such property (" + this.mProperty.getName() + ") on target object " + o + ". Trying reflection instead");
                            this.mProperty = null;
                            break Label_0007;
                        }
                        break Label_0159;
                    }
                }
                class1 = o.getClass();
                if (this.mSetter == null) {
                    break Label_0159;
                }
                for (final Keyframe keyframe2 : this.mKeyframeSet.mKeyframes) {
                    if (!keyframe2.hasValue()) {
                        if (this.mGetter == null) {
                            goto Label_0229;
                        }
                        try {
                            keyframe2.setValue(this.mGetter.invoke(o, new Object[0]));
                        }
                        catch (final InvocationTargetException ex2) {
                            Log.e("PropertyValuesHolder", ex2.toString());
                        }
                        catch (final IllegalAccessException ex3) {
                            Log.e("PropertyValuesHolder", ex3.toString());
                        }
                    }
                }
                return;
            }
            this.setupSetter(class1);
            continue;
        }
    }
    
    void setupStartValue(final Object o) {
        this.setupValue(o, this.mKeyframeSet.mKeyframes.get(0));
    }
    
    @Override
    public String toString() {
        return this.mPropertyName + ": " + this.mKeyframeSet.toString();
    }
    
    static class FloatPropertyValuesHolder extends PropertyValuesHolder
    {
        float mFloatAnimatedValue;
        FloatKeyframeSet mFloatKeyframeSet;
        private FloatProperty mFloatProperty;
        
        public FloatPropertyValuesHolder(final Property property, final FloatKeyframeSet mKeyframeSet) {
            super(property, null);
            this.mValueType = Float.TYPE;
            this.mKeyframeSet = mKeyframeSet;
            this.mFloatKeyframeSet = (FloatKeyframeSet)this.mKeyframeSet;
            if (property instanceof FloatProperty) {
                this.mFloatProperty = (FloatProperty)this.mProperty;
            }
        }
        
        public FloatPropertyValuesHolder(final Property property, final float... floatValues) {
            super(property, null);
            this.setFloatValues(floatValues);
            if (property instanceof FloatProperty) {
                this.mFloatProperty = (FloatProperty)this.mProperty;
            }
        }
        
        public FloatPropertyValuesHolder(final String s, final FloatKeyframeSet mKeyframeSet) {
            super(s, null);
            this.mValueType = Float.TYPE;
            this.mKeyframeSet = mKeyframeSet;
            this.mFloatKeyframeSet = (FloatKeyframeSet)this.mKeyframeSet;
        }
        
        public FloatPropertyValuesHolder(final String s, final float... floatValues) {
            super(s, null);
            this.setFloatValues(floatValues);
        }
        
        @Override
        void calculateValue(final float n) {
            this.mFloatAnimatedValue = this.mFloatKeyframeSet.getFloatValue(n);
        }
        
        @Override
        public FloatPropertyValuesHolder clone() {
            final FloatPropertyValuesHolder floatPropertyValuesHolder = (FloatPropertyValuesHolder)super.clone();
            floatPropertyValuesHolder.mFloatKeyframeSet = (FloatKeyframeSet)floatPropertyValuesHolder.mKeyframeSet;
            return floatPropertyValuesHolder;
        }
        
        @Override
        Object getAnimatedValue() {
            return this.mFloatAnimatedValue;
        }
        
        @Override
        void setAnimatedValue(final Object obj) {
            if (this.mFloatProperty != null) {
                this.mFloatProperty.setValue(obj, this.mFloatAnimatedValue);
                return;
            }
            if (this.mProperty == null) {
                if (this.mSetter != null) {
                    try {
                        this.mTmpValueArray[0] = this.mFloatAnimatedValue;
                        this.mSetter.invoke(obj, this.mTmpValueArray);
                    }
                    catch (final InvocationTargetException ex) {
                        Log.e("PropertyValuesHolder", ex.toString());
                    }
                    catch (final IllegalAccessException ex2) {
                        Log.e("PropertyValuesHolder", ex2.toString());
                    }
                }
                return;
            }
            this.mProperty.set(obj, this.mFloatAnimatedValue);
        }
        
        @Override
        public void setFloatValues(final float... floatValues) {
            super.setFloatValues(floatValues);
            this.mFloatKeyframeSet = (FloatKeyframeSet)this.mKeyframeSet;
        }
        
        @Override
        void setupSetter(final Class clazz) {
            if (this.mProperty == null) {
                super.setupSetter(clazz);
            }
        }
    }
    
    static class IntPropertyValuesHolder extends PropertyValuesHolder
    {
        int mIntAnimatedValue;
        IntKeyframeSet mIntKeyframeSet;
        private IntProperty mIntProperty;
        
        public IntPropertyValuesHolder(final Property property, final IntKeyframeSet mKeyframeSet) {
            super(property, null);
            this.mValueType = Integer.TYPE;
            this.mKeyframeSet = mKeyframeSet;
            this.mIntKeyframeSet = (IntKeyframeSet)this.mKeyframeSet;
            if (property instanceof IntProperty) {
                this.mIntProperty = (IntProperty)this.mProperty;
            }
        }
        
        public IntPropertyValuesHolder(final Property property, final int... intValues) {
            super(property, null);
            this.setIntValues(intValues);
            if (property instanceof IntProperty) {
                this.mIntProperty = (IntProperty)this.mProperty;
            }
        }
        
        public IntPropertyValuesHolder(final String s, final IntKeyframeSet mKeyframeSet) {
            super(s, null);
            this.mValueType = Integer.TYPE;
            this.mKeyframeSet = mKeyframeSet;
            this.mIntKeyframeSet = (IntKeyframeSet)this.mKeyframeSet;
        }
        
        public IntPropertyValuesHolder(final String s, final int... intValues) {
            super(s, null);
            this.setIntValues(intValues);
        }
        
        @Override
        void calculateValue(final float n) {
            this.mIntAnimatedValue = this.mIntKeyframeSet.getIntValue(n);
        }
        
        @Override
        public IntPropertyValuesHolder clone() {
            final IntPropertyValuesHolder intPropertyValuesHolder = (IntPropertyValuesHolder)super.clone();
            intPropertyValuesHolder.mIntKeyframeSet = (IntKeyframeSet)intPropertyValuesHolder.mKeyframeSet;
            return intPropertyValuesHolder;
        }
        
        @Override
        Object getAnimatedValue() {
            return this.mIntAnimatedValue;
        }
        
        @Override
        void setAnimatedValue(final Object obj) {
            if (this.mIntProperty != null) {
                this.mIntProperty.setValue(obj, this.mIntAnimatedValue);
                return;
            }
            if (this.mProperty == null) {
                if (this.mSetter != null) {
                    try {
                        this.mTmpValueArray[0] = this.mIntAnimatedValue;
                        this.mSetter.invoke(obj, this.mTmpValueArray);
                    }
                    catch (final InvocationTargetException ex) {
                        Log.e("PropertyValuesHolder", ex.toString());
                    }
                    catch (final IllegalAccessException ex2) {
                        Log.e("PropertyValuesHolder", ex2.toString());
                    }
                }
                return;
            }
            this.mProperty.set(obj, this.mIntAnimatedValue);
        }
        
        @Override
        public void setIntValues(final int... intValues) {
            super.setIntValues(intValues);
            this.mIntKeyframeSet = (IntKeyframeSet)this.mKeyframeSet;
        }
        
        @Override
        void setupSetter(final Class clazz) {
            if (this.mProperty == null) {
                super.setupSetter(clazz);
            }
        }
    }
}

// 
// Decompiled by Procyon v0.6.0
// 

package com.google.protobuf.nano;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Collection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class FieldData implements Cloneable
{
    private Extension<?, ?> cachedExtension;
    private List<UnknownFieldData> unknownFieldData;
    private Object value;
    
    FieldData() {
        this.unknownFieldData = new ArrayList<UnknownFieldData>();
    }
    
     <T> FieldData(final Extension<?, T> cachedExtension, final T value) {
        this.cachedExtension = cachedExtension;
        this.value = value;
    }
    
    private byte[] toByteArray() throws IOException {
        final byte[] array = new byte[this.computeSerializedSize()];
        this.writeTo(CodedOutputByteBufferNano.newInstance(array));
        return array;
    }
    
    void addUnknownField(final UnknownFieldData unknownFieldData) {
        this.unknownFieldData.add(unknownFieldData);
    }
    
    public final FieldData clone() {
        while (true) {
            int i = 0;
            final FieldData fieldData = new FieldData();
            Label_0323: {
                Label_0306: {
                    Label_0289: {
                        Label_0272: {
                            Label_0255: {
                                Label_0238: {
                                    Label_0190: {
                                        Label_0173: {
                                            Label_0156: {
                                                try {
                                                    fieldData.cachedExtension = this.cachedExtension;
                                                    if (this.unknownFieldData != null) {
                                                        fieldData.unknownFieldData.addAll(this.unknownFieldData);
                                                    }
                                                    else {
                                                        fieldData.unknownFieldData = null;
                                                    }
                                                    if (this.value != null) {
                                                        if (this.value instanceof MessageNano) {
                                                            break Label_0156;
                                                        }
                                                        if (this.value instanceof byte[]) {
                                                            break Label_0173;
                                                        }
                                                        if (this.value instanceof byte[][]) {
                                                            break Label_0190;
                                                        }
                                                        if (this.value instanceof boolean[]) {
                                                            break Label_0238;
                                                        }
                                                        if (this.value instanceof int[]) {
                                                            break Label_0255;
                                                        }
                                                        if (this.value instanceof long[]) {
                                                            break Label_0272;
                                                        }
                                                        if (this.value instanceof float[]) {
                                                            break Label_0289;
                                                        }
                                                        if (this.value instanceof double[]) {
                                                            break Label_0306;
                                                        }
                                                        if (this.value instanceof MessageNano[]) {
                                                            break Label_0323;
                                                        }
                                                    }
                                                    return fieldData;
                                                }
                                                catch (final CloneNotSupportedException detailMessage) {
                                                    throw new AssertionError((Object)detailMessage);
                                                }
                                            }
                                            fieldData.value = ((MessageNano)this.value).clone();
                                            return fieldData;
                                        }
                                        fieldData.value = this.value.clone();
                                        return fieldData;
                                    }
                                    final byte[][] array = (byte[][])this.value;
                                    final byte[][] value = new byte[array.length][];
                                    fieldData.value = value;
                                    for (int j = 0; j < array.length; ++j) {
                                        value[j] = array[j].clone();
                                    }
                                    return fieldData;
                                }
                                fieldData.value = this.value.clone();
                                return fieldData;
                            }
                            fieldData.value = this.value.clone();
                            return fieldData;
                        }
                        fieldData.value = this.value.clone();
                        return fieldData;
                    }
                    fieldData.value = this.value.clone();
                    return fieldData;
                }
                fieldData.value = this.value.clone();
                return fieldData;
            }
            final MessageNano[] array2 = (MessageNano[])this.value;
            final MessageNano[] value2 = new MessageNano[array2.length];
            fieldData.value = value2;
            while (i < array2.length) {
                value2[i] = array2[i].clone();
                ++i;
            }
            return fieldData;
        }
    }
    
    int computeSerializedSize() {
        int computeSerializedSize;
        if (this.value == null) {
            final Iterator<UnknownFieldData> iterator = this.unknownFieldData.iterator();
            computeSerializedSize = 0;
            while (iterator.hasNext()) {
                computeSerializedSize += iterator.next().computeSerializedSize();
            }
        }
        else {
            computeSerializedSize = this.cachedExtension.computeSerializedSize(this.value);
        }
        return computeSerializedSize;
    }
    
    @Override
    public boolean equals(final Object p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aload_0        
        //     2: if_acmpeq       45
        //     5: aload_1        
        //     6: instanceof      Lcom/google/protobuf/nano/FieldData;
        //     9: ifeq            47
        //    12: aload_1        
        //    13: checkcast       Lcom/google/protobuf/nano/FieldData;
        //    16: astore_1       
        //    17: aload_0        
        //    18: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //    21: ifnonnull       49
        //    24: aload_0        
        //    25: getfield        com/google/protobuf/nano/FieldData.unknownFieldData:Ljava/util/List;
        //    28: ifnonnull       280
        //    31: aload_0        
        //    32: invokespecial   com/google/protobuf/nano/FieldData.toByteArray:()[B
        //    35: aload_1        
        //    36: invokespecial   com/google/protobuf/nano/FieldData.toByteArray:()[B
        //    39: invokestatic    java/util/Arrays.equals:([B[B)Z
        //    42: istore_2       
        //    43: iload_2        
        //    44: ireturn        
        //    45: iconst_1       
        //    46: ireturn        
        //    47: iconst_0       
        //    48: ireturn        
        //    49: aload_1        
        //    50: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //    53: ifnull          24
        //    56: aload_0        
        //    57: getfield        com/google/protobuf/nano/FieldData.cachedExtension:Lcom/google/protobuf/nano/Extension;
        //    60: aload_1        
        //    61: getfield        com/google/protobuf/nano/FieldData.cachedExtension:Lcom/google/protobuf/nano/Extension;
        //    64: if_acmpne       158
        //    67: aload_0        
        //    68: getfield        com/google/protobuf/nano/FieldData.cachedExtension:Lcom/google/protobuf/nano/Extension;
        //    71: getfield        com/google/protobuf/nano/Extension.clazz:Ljava/lang/Class;
        //    74: invokevirtual   java/lang/Class.isArray:()Z
        //    77: ifeq            160
        //    80: aload_0        
        //    81: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //    84: instanceof      [B
        //    87: ifne            172
        //    90: aload_0        
        //    91: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //    94: instanceof      [I
        //    97: ifne            190
        //   100: aload_0        
        //   101: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //   104: instanceof      [J
        //   107: ifne            208
        //   110: aload_0        
        //   111: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //   114: instanceof      [F
        //   117: ifne            226
        //   120: aload_0        
        //   121: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //   124: instanceof      [D
        //   127: ifne            244
        //   130: aload_0        
        //   131: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //   134: instanceof      [Z
        //   137: ifne            262
        //   140: aload_0        
        //   141: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //   144: checkcast       [Ljava/lang/Object;
        //   147: aload_1        
        //   148: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //   151: checkcast       [Ljava/lang/Object;
        //   154: invokestatic    java/util/Arrays.deepEquals:([Ljava/lang/Object;[Ljava/lang/Object;)Z
        //   157: ireturn        
        //   158: iconst_0       
        //   159: ireturn        
        //   160: aload_0        
        //   161: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //   164: aload_1        
        //   165: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //   168: invokevirtual   java/lang/Object.equals:(Ljava/lang/Object;)Z
        //   171: ireturn        
        //   172: aload_0        
        //   173: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //   176: checkcast       [B
        //   179: aload_1        
        //   180: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //   183: checkcast       [B
        //   186: invokestatic    java/util/Arrays.equals:([B[B)Z
        //   189: ireturn        
        //   190: aload_0        
        //   191: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //   194: checkcast       [I
        //   197: aload_1        
        //   198: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //   201: checkcast       [I
        //   204: invokestatic    java/util/Arrays.equals:([I[I)Z
        //   207: ireturn        
        //   208: aload_0        
        //   209: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //   212: checkcast       [J
        //   215: aload_1        
        //   216: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //   219: checkcast       [J
        //   222: invokestatic    java/util/Arrays.equals:([J[J)Z
        //   225: ireturn        
        //   226: aload_0        
        //   227: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //   230: checkcast       [F
        //   233: aload_1        
        //   234: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //   237: checkcast       [F
        //   240: invokestatic    java/util/Arrays.equals:([F[F)Z
        //   243: ireturn        
        //   244: aload_0        
        //   245: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //   248: checkcast       [D
        //   251: aload_1        
        //   252: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //   255: checkcast       [D
        //   258: invokestatic    java/util/Arrays.equals:([D[D)Z
        //   261: ireturn        
        //   262: aload_0        
        //   263: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //   266: checkcast       [Z
        //   269: aload_1        
        //   270: getfield        com/google/protobuf/nano/FieldData.value:Ljava/lang/Object;
        //   273: checkcast       [Z
        //   276: invokestatic    java/util/Arrays.equals:([Z[Z)Z
        //   279: ireturn        
        //   280: aload_1        
        //   281: getfield        com/google/protobuf/nano/FieldData.unknownFieldData:Ljava/util/List;
        //   284: ifnull          31
        //   287: aload_0        
        //   288: getfield        com/google/protobuf/nano/FieldData.unknownFieldData:Ljava/util/List;
        //   291: aload_1        
        //   292: getfield        com/google/protobuf/nano/FieldData.unknownFieldData:Ljava/util/List;
        //   295: invokeinterface java/util/List.equals:(Ljava/lang/Object;)Z
        //   300: ireturn        
        //   301: astore_1       
        //   302: new             Ljava/lang/IllegalStateException;
        //   305: dup            
        //   306: aload_1        
        //   307: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/Throwable;)V
        //   310: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  31     43     301    311    Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: cmpeq:boolean(getfield:Object(FieldData::value, var_1_10:FieldData), aconstnull:Object())
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
    
    UnknownFieldData getUnknownField(final int n) {
        if (this.unknownFieldData == null) {
            return null;
        }
        if (n >= this.unknownFieldData.size()) {
            return null;
        }
        return this.unknownFieldData.get(n);
    }
    
    int getUnknownFieldSize() {
        if (this.unknownFieldData != null) {
            return this.unknownFieldData.size();
        }
        return 0;
    }
    
     <T> T getValue(final Extension<?, T> cachedExtension) {
        if (this.value == null) {
            this.cachedExtension = cachedExtension;
            this.value = cachedExtension.getValueFrom(this.unknownFieldData);
            this.unknownFieldData = null;
        }
        else if (this.cachedExtension != cachedExtension) {
            throw new IllegalStateException("Tried to getExtension with a differernt Extension.");
        }
        return (T)this.value;
    }
    
    @Override
    public int hashCode() {
        try {
            return Arrays.hashCode(this.toByteArray()) + 527;
        }
        catch (final IOException cause) {
            throw new IllegalStateException(cause);
        }
    }
    
     <T> void setValue(final Extension<?, T> cachedExtension, final T value) {
        this.cachedExtension = cachedExtension;
        this.value = value;
        this.unknownFieldData = null;
    }
    
    void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (this.value == null) {
            final Iterator<UnknownFieldData> iterator = this.unknownFieldData.iterator();
            while (iterator.hasNext()) {
                iterator.next().writeTo(codedOutputByteBufferNano);
            }
        }
        else {
            this.cachedExtension.writeTo(this.value, codedOutputByteBufferNano);
        }
    }
}

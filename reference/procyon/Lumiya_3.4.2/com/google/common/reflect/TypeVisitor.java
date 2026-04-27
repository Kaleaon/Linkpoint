// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.reflect;

import java.lang.reflect.WildcardType;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.GenericArrayType;
import com.google.common.collect.Sets;
import java.lang.reflect.Type;
import java.util.Set;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
abstract class TypeVisitor
{
    private final Set<Type> visited;
    
    TypeVisitor() {
        this.visited = (Set<Type>)Sets.newHashSet();
    }
    
    public final void visit(final Type... p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore_2       
        //     2: aload_1        
        //     3: arraylength    
        //     4: istore_3       
        //     5: iload_2        
        //     6: iload_3        
        //     7: if_icmplt       11
        //    10: return         
        //    11: aload_1        
        //    12: iload_2        
        //    13: aaload         
        //    14: astore          4
        //    16: aload           4
        //    18: ifnonnull       27
        //    21: iinc            2, 1
        //    24: goto            5
        //    27: aload_0        
        //    28: getfield        com/google/common/reflect/TypeVisitor.visited:Ljava/util/Set;
        //    31: aload           4
        //    33: invokeinterface java/util/Set.add:(Ljava/lang/Object;)Z
        //    38: ifeq            21
        //    41: aload           4
        //    43: instanceof      Ljava/lang/reflect/TypeVariable;
        //    46: ifne            131
        //    49: aload           4
        //    51: instanceof      Ljava/lang/reflect/WildcardType;
        //    54: ifne            143
        //    57: aload           4
        //    59: instanceof      Ljava/lang/reflect/ParameterizedType;
        //    62: ifne            155
        //    65: aload           4
        //    67: instanceof      Ljava/lang/Class;
        //    70: ifne            167
        //    73: aload           4
        //    75: instanceof      Ljava/lang/reflect/GenericArrayType;
        //    78: ifne            179
        //    81: new             Ljava/lang/AssertionError;
        //    84: astore          5
        //    86: new             Ljava/lang/StringBuilder;
        //    89: astore_1       
        //    90: aload_1        
        //    91: invokespecial   java/lang/StringBuilder.<init>:()V
        //    94: aload           5
        //    96: aload_1        
        //    97: ldc             "Unknown type: "
        //    99: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   102: aload           4
        //   104: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   107: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   110: invokespecial   java/lang/AssertionError.<init>:(Ljava/lang/Object;)V
        //   113: aload           5
        //   115: athrow         
        //   116: astore_1       
        //   117: aload_0        
        //   118: getfield        com/google/common/reflect/TypeVisitor.visited:Ljava/util/Set;
        //   121: aload           4
        //   123: invokeinterface java/util/Set.remove:(Ljava/lang/Object;)Z
        //   128: pop            
        //   129: aload_1        
        //   130: athrow         
        //   131: aload_0        
        //   132: aload           4
        //   134: checkcast       Ljava/lang/reflect/TypeVariable;
        //   137: invokevirtual   com/google/common/reflect/TypeVisitor.visitTypeVariable:(Ljava/lang/reflect/TypeVariable;)V
        //   140: goto            21
        //   143: aload_0        
        //   144: aload           4
        //   146: checkcast       Ljava/lang/reflect/WildcardType;
        //   149: invokevirtual   com/google/common/reflect/TypeVisitor.visitWildcardType:(Ljava/lang/reflect/WildcardType;)V
        //   152: goto            21
        //   155: aload_0        
        //   156: aload           4
        //   158: checkcast       Ljava/lang/reflect/ParameterizedType;
        //   161: invokevirtual   com/google/common/reflect/TypeVisitor.visitParameterizedType:(Ljava/lang/reflect/ParameterizedType;)V
        //   164: goto            21
        //   167: aload_0        
        //   168: aload           4
        //   170: checkcast       Ljava/lang/Class;
        //   173: invokevirtual   com/google/common/reflect/TypeVisitor.visitClass:(Ljava/lang/Class;)V
        //   176: goto            21
        //   179: aload_0        
        //   180: aload           4
        //   182: checkcast       Ljava/lang/reflect/GenericArrayType;
        //   185: invokevirtual   com/google/common/reflect/TypeVisitor.visitGenericArrayType:(Ljava/lang/reflect/GenericArrayType;)V
        //   188: goto            21
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  41     116    116    131    Any
        //  131    140    116    131    Any
        //  143    152    116    131    Any
        //  155    164    116    131    Any
        //  167    176    116    131    Any
        //  179    188    116    131    Any
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
    
    void visitClass(final Class<?> clazz) {
    }
    
    void visitGenericArrayType(final GenericArrayType genericArrayType) {
    }
    
    void visitParameterizedType(final ParameterizedType parameterizedType) {
    }
    
    void visitTypeVariable(final TypeVariable<?> typeVariable) {
    }
    
    void visitWildcardType(final WildcardType wildcardType) {
    }
}

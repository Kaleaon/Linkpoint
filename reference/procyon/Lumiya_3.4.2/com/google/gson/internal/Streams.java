// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.internal;

import java.io.Writer;
import java.io.IOException;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonWriter;
import com.google.gson.JsonParseException;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;

public final class Streams
{
    private Streams() {
        throw new UnsupportedOperationException();
    }
    
    public static JsonElement parse(final JsonReader p0) throws JsonParseException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore_1       
        //     2: aload_0        
        //     3: invokevirtual   com/google/gson/stream/JsonReader.peek:()Lcom/google/gson/stream/JsonToken;
        //     6: pop            
        //     7: getstatic       com/google/gson/internal/bind/TypeAdapters.JSON_ELEMENT:Lcom/google/gson/TypeAdapter;
        //    10: aload_0        
        //    11: invokevirtual   com/google/gson/TypeAdapter.read:(Lcom/google/gson/stream/JsonReader;)Ljava/lang/Object;
        //    14: checkcast       Lcom/google/gson/JsonElement;
        //    17: astore_0       
        //    18: aload_0        
        //    19: areturn        
        //    20: astore_0       
        //    21: iconst_1       
        //    22: istore_1       
        //    23: iload_1        
        //    24: ifne            36
        //    27: new             Lcom/google/gson/JsonSyntaxException;
        //    30: dup            
        //    31: aload_0        
        //    32: invokespecial   com/google/gson/JsonSyntaxException.<init>:(Ljava/lang/Throwable;)V
        //    35: athrow         
        //    36: getstatic       com/google/gson/JsonNull.INSTANCE:Lcom/google/gson/JsonNull;
        //    39: areturn        
        //    40: astore_0       
        //    41: new             Lcom/google/gson/JsonSyntaxException;
        //    44: dup            
        //    45: aload_0        
        //    46: invokespecial   com/google/gson/JsonSyntaxException.<init>:(Ljava/lang/Throwable;)V
        //    49: athrow         
        //    50: astore_0       
        //    51: new             Lcom/google/gson/JsonIOException;
        //    54: dup            
        //    55: aload_0        
        //    56: invokespecial   com/google/gson/JsonIOException.<init>:(Ljava/lang/Throwable;)V
        //    59: athrow         
        //    60: astore_0       
        //    61: new             Lcom/google/gson/JsonSyntaxException;
        //    64: dup            
        //    65: aload_0        
        //    66: invokespecial   com/google/gson/JsonSyntaxException.<init>:(Ljava/lang/Throwable;)V
        //    69: athrow         
        //    70: astore_0       
        //    71: goto            23
        //    Exceptions:
        //  throws com.google.gson.JsonParseException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                           
        //  -----  -----  -----  -----  -----------------------------------------------
        //  2      7      20     23     Ljava/io/EOFException;
        //  2      7      40     50     Lcom/google/gson/stream/MalformedJsonException;
        //  2      7      50     60     Ljava/io/IOException;
        //  2      7      60     70     Ljava/lang/NumberFormatException;
        //  7      18     70     74     Ljava/io/EOFException;
        //  7      18     40     50     Lcom/google/gson/stream/MalformedJsonException;
        //  7      18     50     60     Ljava/io/IOException;
        //  7      18     60     70     Ljava/lang/NumberFormatException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0023:
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
    
    public static void write(final JsonElement jsonElement, final JsonWriter jsonWriter) throws IOException {
        TypeAdapters.JSON_ELEMENT.write(jsonWriter, jsonElement);
    }
    
    public static Writer writerForAppendable(final Appendable appendable) {
        Writer writer;
        if (!(appendable instanceof Writer)) {
            writer = new AppendableWriter(appendable);
        }
        else {
            writer = (Writer)appendable;
        }
        return writer;
    }
    
    private static final class AppendableWriter extends Writer
    {
        private final Appendable appendable;
        private final CurrentWrite currentWrite;
        
        AppendableWriter(final Appendable appendable) {
            this.currentWrite = new CurrentWrite();
            this.appendable = appendable;
        }
        
        @Override
        public void close() {
        }
        
        @Override
        public void flush() {
        }
        
        @Override
        public void write(final int n) throws IOException {
            this.appendable.append((char)n);
        }
        
        @Override
        public void write(final char[] chars, final int n, final int n2) throws IOException {
            this.currentWrite.chars = chars;
            this.appendable.append(this.currentWrite, n, n + n2);
        }
        
        static class CurrentWrite implements CharSequence
        {
            char[] chars;
            
            @Override
            public char charAt(final int n) {
                return this.chars[n];
            }
            
            @Override
            public int length() {
                return this.chars.length;
            }
            
            @Override
            public CharSequence subSequence(final int offset, final int n) {
                return new String(this.chars, offset, n - offset);
            }
        }
    }
}

// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.io;

import com.google.common.base.Ascii;
import com.google.common.collect.AbstractIterator;
import java.util.regex.Pattern;
import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.Collection;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import com.google.common.base.Optional;
import com.google.common.annotations.Beta;
import java.io.Writer;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.Reader;
import com.google.common.collect.ImmutableList;
import java.util.Iterator;

public abstract class CharSource
{
    protected CharSource() {
    }
    
    public static CharSource concat(final Iterable<? extends CharSource> iterable) {
        return new ConcatenatedCharSource(iterable);
    }
    
    public static CharSource concat(final Iterator<? extends CharSource> iterator) {
        return concat((Iterable<? extends CharSource>)ImmutableList.copyOf((Iterator<?>)iterator));
    }
    
    public static CharSource concat(final CharSource... array) {
        return concat(ImmutableList.copyOf(array));
    }
    
    private long countBySkipping(final Reader reader) throws IOException {
        long n = 0L;
        while (true) {
            final long skip = reader.skip(Long.MAX_VALUE);
            if (skip == 0L) {
                break;
            }
            n += skip;
        }
        return n;
    }
    
    public static CharSource empty() {
        return EmptyCharSource.INSTANCE;
    }
    
    public static CharSource wrap(final CharSequence charSequence) {
        return new CharSequenceCharSource(charSequence);
    }
    
    public long copyTo(final CharSink charSink) throws IOException {
        Preconditions.checkNotNull(charSink);
        final Closer create = Closer.create();
        try {
            return CharStreams.copy(create.register(this.openStream()), create.register(charSink.openStream()));
        }
        catch (final Throwable t) {
            throw create.rethrow(t);
        }
        finally {
            create.close();
        }
    }
    
    public long copyTo(final Appendable appendable) throws IOException {
        Preconditions.checkNotNull(appendable);
        final Closer create = Closer.create();
        try {
            return CharStreams.copy(create.register(this.openStream()), appendable);
        }
        catch (final Throwable t) {
            throw create.rethrow(t);
        }
        finally {
            create.close();
        }
    }
    
    public boolean isEmpty() throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokevirtual   com/google/common/io/CharSource.lengthIfKnown:()Lcom/google/common/base/Optional;
        //     4: astore_1       
        //     5: aload_1        
        //     6: invokevirtual   com/google/common/base/Optional.isPresent:()Z
        //     9: ifne            44
        //    12: invokestatic    com/google/common/io/Closer.create:()Lcom/google/common/io/Closer;
        //    15: astore_1       
        //    16: aload_1        
        //    17: aload_0        
        //    18: invokevirtual   com/google/common/io/CharSource.openStream:()Ljava/io/Reader;
        //    21: invokevirtual   com/google/common/io/Closer.register:(Ljava/io/Closeable;)Ljava/io/Closeable;
        //    24: checkcast       Ljava/io/Reader;
        //    27: invokevirtual   java/io/Reader.read:()I
        //    30: istore_2       
        //    31: iload_2        
        //    32: iconst_m1      
        //    33: if_icmpeq       61
        //    36: iconst_0       
        //    37: istore_3       
        //    38: aload_1        
        //    39: invokevirtual   com/google/common/io/Closer.close:()V
        //    42: iload_3        
        //    43: ireturn        
        //    44: aload_1        
        //    45: invokevirtual   com/google/common/base/Optional.get:()Ljava/lang/Object;
        //    48: checkcast       Ljava/lang/Long;
        //    51: invokevirtual   java/lang/Long.longValue:()J
        //    54: lconst_0       
        //    55: lcmp           
        //    56: ifne            12
        //    59: iconst_1       
        //    60: ireturn        
        //    61: iconst_1       
        //    62: istore_3       
        //    63: goto            38
        //    66: astore          4
        //    68: aload_1        
        //    69: aload           4
        //    71: invokevirtual   com/google/common/io/Closer.rethrow:(Ljava/lang/Throwable;)Ljava/lang/RuntimeException;
        //    74: athrow         
        //    75: astore          4
        //    77: aload_1        
        //    78: invokevirtual   com/google/common/io/Closer.close:()V
        //    81: aload           4
        //    83: athrow         
        //    Exceptions:
        //  throws java.io.IOException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  16     31     66     75     Ljava/lang/Throwable;
        //  16     31     75     84     Any
        //  68     75     75     84     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: cmpne:boolean(__lcmp:long(invokevirtual:long(Long::longValue, checkcast:Long(java.lang.Long.class, invokevirtual:Long(Optional<Long>::get, var_1_04:Optional<Long>[expected:Object]))), ldc:long(0L)), ldc:int(0))
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
    
    @Beta
    public long length() throws IOException {
        Object o = this.lengthIfKnown();
        Label_0038: {
            if (((Optional)o).isPresent()) {
                break Label_0038;
            }
            o = Closer.create();
            try {
                return this.countBySkipping(((Closer)o).register(this.openStream()));
                return ((Optional<Long>)o).get();
            }
            catch (final Throwable t) {
                throw ((Closer)o).rethrow(t);
            }
            finally {
                ((Closer)o).close();
            }
        }
    }
    
    @Beta
    public Optional<Long> lengthIfKnown() {
        return Optional.absent();
    }
    
    public BufferedReader openBufferedStream() throws IOException {
        final Reader openStream = this.openStream();
        BufferedReader bufferedReader;
        if (!(openStream instanceof BufferedReader)) {
            bufferedReader = new BufferedReader(openStream);
        }
        else {
            bufferedReader = (BufferedReader)openStream;
        }
        return bufferedReader;
    }
    
    public abstract Reader openStream() throws IOException;
    
    public String read() throws IOException {
        final Closer create = Closer.create();
        try {
            return CharStreams.toString(create.register(this.openStream()));
        }
        catch (final Throwable t) {
            throw create.rethrow(t);
        }
        finally {
            create.close();
        }
    }
    
    @Nullable
    public String readFirstLine() throws IOException {
        final Closer create = Closer.create();
        try {
            return create.register(this.openBufferedStream()).readLine();
        }
        catch (final Throwable t) {
            throw create.rethrow(t);
        }
        finally {
            create.close();
        }
    }
    
    public ImmutableList<String> readLines() throws IOException {
        final Closer create = Closer.create();
        try {
            final BufferedReader bufferedReader = create.register(this.openBufferedStream());
            final ArrayList<Object> arrayList = Lists.newArrayList();
            while (true) {
                final String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                arrayList.add(line);
            }
            return ImmutableList.copyOf((Collection<? extends String>)arrayList);
        }
        catch (final Throwable t) {
            throw create.rethrow(t);
        }
        finally {
            create.close();
        }
    }
    
    @Beta
    public <T> T readLines(final LineProcessor<T> lineProcessor) throws IOException {
        Preconditions.checkNotNull(lineProcessor);
        final Closer create = Closer.create();
        try {
            return CharStreams.readLines(create.register(this.openStream()), lineProcessor);
        }
        catch (final Throwable t) {
            throw create.rethrow(t);
        }
        finally {
            create.close();
        }
    }
    
    private static class CharSequenceCharSource extends CharSource
    {
        private static final Splitter LINE_SPLITTER;
        private final CharSequence seq;
        
        static {
            LINE_SPLITTER = Splitter.on(Pattern.compile("\r\n|\n|\r"));
        }
        
        protected CharSequenceCharSource(final CharSequence charSequence) {
            this.seq = Preconditions.checkNotNull(charSequence);
        }
        
        private Iterable<String> lines() {
            return new Iterable<String>() {
                @Override
                public Iterator<String> iterator() {
                    return new AbstractIterator<String>() {
                        Iterator<String> lines = CharSequenceCharSource.LINE_SPLITTER.split(CharSequenceCharSource.this.seq).iterator();
                        
                        @Override
                        protected String computeNext() {
                            if (this.lines.hasNext()) {
                                final String s = this.lines.next();
                                if (this.lines.hasNext() || !s.isEmpty()) {
                                    return s;
                                }
                            }
                            return this.endOfData();
                        }
                    };
                }
            };
        }
        
        @Override
        public boolean isEmpty() {
            boolean b = false;
            if (this.seq.length() == 0) {
                b = true;
            }
            return b;
        }
        
        @Override
        public long length() {
            return this.seq.length();
        }
        
        @Override
        public Optional<Long> lengthIfKnown() {
            return Optional.of((long)this.seq.length());
        }
        
        @Override
        public Reader openStream() {
            return new CharSequenceReader(this.seq);
        }
        
        @Override
        public String read() {
            return this.seq.toString();
        }
        
        @Override
        public String readFirstLine() {
            final Iterator<String> iterator = this.lines().iterator();
            String s;
            if (!iterator.hasNext()) {
                s = null;
            }
            else {
                s = iterator.next();
            }
            return s;
        }
        
        @Override
        public ImmutableList<String> readLines() {
            return ImmutableList.copyOf((Iterable<? extends String>)this.lines());
        }
        
        @Override
        public <T> T readLines(final LineProcessor<T> lineProcessor) throws IOException {
            final Iterator<String> iterator = this.lines().iterator();
            while (iterator.hasNext() && lineProcessor.processLine(iterator.next())) {}
            return (T)lineProcessor.getResult();
        }
        
        @Override
        public String toString() {
            return "CharSource.wrap(" + Ascii.truncate(this.seq, 30, "...") + ")";
        }
    }
    
    private static final class ConcatenatedCharSource extends CharSource
    {
        private final Iterable<? extends CharSource> sources;
        
        ConcatenatedCharSource(final Iterable<? extends CharSource> iterable) {
            this.sources = Preconditions.checkNotNull(iterable);
        }
        
        @Override
        public boolean isEmpty() throws IOException {
            final Iterator<? extends CharSource> iterator = this.sources.iterator();
            while (iterator.hasNext()) {
                if (!((CharSource)iterator.next()).isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public long length() throws IOException {
            final Iterator<? extends CharSource> iterator = this.sources.iterator();
            long n = 0L;
            while (iterator.hasNext()) {
                n += ((CharSource)iterator.next()).length();
            }
            return n;
        }
        
        @Override
        public Optional<Long> lengthIfKnown() {
            final Iterator<? extends CharSource> iterator = this.sources.iterator();
            long l = 0L;
            while (iterator.hasNext()) {
                final Optional<Long> lengthIfKnown = ((CharSource)iterator.next()).lengthIfKnown();
                if (!lengthIfKnown.isPresent()) {
                    return Optional.absent();
                }
                l += lengthIfKnown.get();
            }
            return Optional.of(l);
        }
        
        @Override
        public Reader openStream() throws IOException {
            return new MultiReader(this.sources.iterator());
        }
        
        @Override
        public String toString() {
            return "CharSource.concat(" + this.sources + ")";
        }
    }
    
    private static final class EmptyCharSource extends CharSequenceCharSource
    {
        private static final EmptyCharSource INSTANCE;
        
        static {
            INSTANCE = new EmptyCharSource();
        }
        
        private EmptyCharSource() {
            super("");
        }
        
        @Override
        public String toString() {
            return "CharSource.empty()";
        }
    }
}

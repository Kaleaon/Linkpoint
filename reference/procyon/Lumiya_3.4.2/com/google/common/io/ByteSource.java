// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.io;

import com.google.common.base.Ascii;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import com.google.common.base.Optional;
import com.google.common.annotations.Beta;
import java.io.BufferedInputStream;
import com.google.common.hash.Hasher;
import com.google.common.hash.PrimitiveSink;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import java.io.OutputStream;
import java.util.Arrays;
import com.google.common.base.Preconditions;
import java.nio.charset.Charset;
import java.io.IOException;
import java.io.InputStream;
import com.google.common.collect.ImmutableList;
import java.util.Iterator;

public abstract class ByteSource
{
    protected ByteSource() {
    }
    
    public static ByteSource concat(final Iterable<? extends ByteSource> iterable) {
        return new ConcatenatedByteSource(iterable);
    }
    
    public static ByteSource concat(final Iterator<? extends ByteSource> iterator) {
        return concat((Iterable<? extends ByteSource>)ImmutableList.copyOf((Iterator<?>)iterator));
    }
    
    public static ByteSource concat(final ByteSource... array) {
        return concat(ImmutableList.copyOf(array));
    }
    
    private long countByReading(final InputStream inputStream) throws IOException {
        long n = 0L;
        while (true) {
            final long n2 = inputStream.read(ByteStreams.skipBuffer);
            if (n2 == -1L) {
                break;
            }
            n += n2;
        }
        return n;
    }
    
    private long countBySkipping(final InputStream inputStream) throws IOException {
        long n = 0L;
        while (true) {
            final long skipUpTo = ByteStreams.skipUpTo(inputStream, 2147483647L);
            int n2;
            if (skipUpTo <= 0L) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 != 0) {
                break;
            }
            n += skipUpTo;
        }
        return n;
    }
    
    public static ByteSource empty() {
        return EmptyByteSource.INSTANCE;
    }
    
    public static ByteSource wrap(final byte[] array) {
        return new ByteArrayByteSource(array);
    }
    
    public CharSource asCharSource(final Charset charset) {
        return new AsCharSource(charset);
    }
    
    public boolean contentEquals(final ByteSource byteSource) throws IOException {
        Preconditions.checkNotNull(byteSource);
        final byte[] a = new byte[8192];
        final byte[] a2 = new byte[8192];
        final Closer create = Closer.create();
        try {
            final InputStream inputStream = create.register(this.openStream());
            final InputStream inputStream2 = create.register(byteSource.openStream());
            int i;
            do {
                i = ByteStreams.read(inputStream, a, 0, 8192);
                if (i == ByteStreams.read(inputStream2, a2, 0, 8192) && Arrays.equals(a, a2)) {
                    continue;
                }
                return false;
            } while (i == 8192);
            return true;
        }
        catch (final Throwable t) {
            throw create.rethrow(t);
        }
        finally {
            create.close();
        }
    }
    
    public long copyTo(final ByteSink byteSink) throws IOException {
        Preconditions.checkNotNull(byteSink);
        final Closer create = Closer.create();
        try {
            return ByteStreams.copy(create.register(this.openStream()), create.register(byteSink.openStream()));
        }
        catch (final Throwable t) {
            throw create.rethrow(t);
        }
        finally {
            create.close();
        }
    }
    
    public long copyTo(final OutputStream outputStream) throws IOException {
        Preconditions.checkNotNull(outputStream);
        final Closer create = Closer.create();
        try {
            return ByteStreams.copy(create.register(this.openStream()), outputStream);
        }
        catch (final Throwable t) {
            throw create.rethrow(t);
        }
        finally {
            create.close();
        }
    }
    
    public HashCode hash(final HashFunction hashFunction) throws IOException {
        final Hasher hasher = hashFunction.newHasher();
        this.copyTo(Funnels.asOutputStream(hasher));
        return hasher.hash();
    }
    
    public boolean isEmpty() throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokevirtual   com/google/common/io/ByteSource.sizeIfKnown:()Lcom/google/common/base/Optional;
        //     4: astore_1       
        //     5: aload_1        
        //     6: invokevirtual   com/google/common/base/Optional.isPresent:()Z
        //     9: ifne            44
        //    12: invokestatic    com/google/common/io/Closer.create:()Lcom/google/common/io/Closer;
        //    15: astore_1       
        //    16: aload_1        
        //    17: aload_0        
        //    18: invokevirtual   com/google/common/io/ByteSource.openStream:()Ljava/io/InputStream;
        //    21: invokevirtual   com/google/common/io/Closer.register:(Ljava/io/Closeable;)Ljava/io/Closeable;
        //    24: checkcast       Ljava/io/InputStream;
        //    27: invokevirtual   java/io/InputStream.read:()I
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
    
    public InputStream openBufferedStream() throws IOException {
        final InputStream openStream = this.openStream();
        BufferedInputStream bufferedInputStream;
        if (!(openStream instanceof BufferedInputStream)) {
            bufferedInputStream = new BufferedInputStream(openStream);
        }
        else {
            bufferedInputStream = (BufferedInputStream)openStream;
        }
        return bufferedInputStream;
    }
    
    public abstract InputStream openStream() throws IOException;
    
    @Beta
    public <T> T read(final ByteProcessor<T> byteProcessor) throws IOException {
        Preconditions.checkNotNull(byteProcessor);
        final Closer create = Closer.create();
        try {
            return ByteStreams.readBytes(create.register(this.openStream()), byteProcessor);
        }
        catch (final Throwable t) {
            throw create.rethrow(t);
        }
        finally {
            create.close();
        }
    }
    
    public byte[] read() throws IOException {
        final Closer create = Closer.create();
        try {
            return ByteStreams.toByteArray(create.register(this.openStream()));
        }
        catch (final Throwable t) {
            throw create.rethrow(t);
        }
        finally {
            create.close();
        }
    }
    
    public long size() throws IOException {
        Object o = this.sizeIfKnown();
        Label_0038: {
            if (((Optional)o).isPresent()) {
                break Label_0038;
            }
            o = Closer.create();
            try {
                return this.countBySkipping(((Closer)o).register(this.openStream()));
                return ((Optional<Long>)o).get();
            }
            catch (final IOException ex) {
                ((Closer)o).close();
                o = Closer.create();
                final ByteSource byteSource = this;
                final Optional<Long> optional = (Optional<Long>)o;
                final ByteSource byteSource2 = this;
                final InputStream inputStream = byteSource2.openStream();
                final InputStream inputStream2 = ((Closer)optional).register(inputStream);
                final InputStream inputStream3 = inputStream2;
                final long countByReading = byteSource.countByReading(inputStream3);
                return countByReading;
            }
            finally {
                ((Closer)o).close();
            }
        }
        try {
            final ByteSource byteSource = this;
            final Optional<Long> optional = (Optional<Long>)o;
            final ByteSource byteSource2 = this;
            final InputStream inputStream = byteSource2.openStream();
            final InputStream inputStream2 = ((Closer)optional).register(inputStream);
            final InputStream inputStream3 = inputStream2;
            final long countByReading2;
            final long countByReading = countByReading2 = byteSource.countByReading(inputStream3);
            return countByReading2;
        }
        catch (final Throwable t) {
            throw ((Closer)o).rethrow(t);
        }
        finally {
            ((Closer)o).close();
        }
    }
    
    @Beta
    public Optional<Long> sizeIfKnown() {
        return Optional.absent();
    }
    
    public ByteSource slice(final long n, final long n2) {
        return new SlicedByteSource(n, n2);
    }
    
    private final class AsCharSource extends CharSource
    {
        private final Charset charset;
        
        private AsCharSource(final Charset charset) {
            this.charset = Preconditions.checkNotNull(charset);
        }
        
        @Override
        public Reader openStream() throws IOException {
            return new InputStreamReader(ByteSource.this.openStream(), this.charset);
        }
        
        @Override
        public String toString() {
            return ByteSource.this.toString() + ".asCharSource(" + this.charset + ")";
        }
    }
    
    private static class ByteArrayByteSource extends ByteSource
    {
        final byte[] bytes;
        final int length;
        final int offset;
        
        ByteArrayByteSource(final byte[] array) {
            this(array, 0, array.length);
        }
        
        ByteArrayByteSource(final byte[] bytes, final int offset, final int length) {
            this.bytes = bytes;
            this.offset = offset;
            this.length = length;
        }
        
        @Override
        public long copyTo(final OutputStream outputStream) throws IOException {
            outputStream.write(this.bytes, this.offset, this.length);
            return this.length;
        }
        
        @Override
        public HashCode hash(final HashFunction hashFunction) throws IOException {
            return hashFunction.hashBytes(this.bytes, this.offset, this.length);
        }
        
        @Override
        public boolean isEmpty() {
            boolean b = false;
            if (this.length == 0) {
                b = true;
            }
            return b;
        }
        
        @Override
        public InputStream openBufferedStream() throws IOException {
            return this.openStream();
        }
        
        @Override
        public InputStream openStream() {
            return new ByteArrayInputStream(this.bytes, this.offset, this.length);
        }
        
        @Override
        public <T> T read(final ByteProcessor<T> byteProcessor) throws IOException {
            byteProcessor.processBytes(this.bytes, this.offset, this.length);
            return byteProcessor.getResult();
        }
        
        @Override
        public byte[] read() {
            return Arrays.copyOfRange(this.bytes, this.offset, this.offset + this.length);
        }
        
        @Override
        public long size() {
            return this.length;
        }
        
        @Override
        public Optional<Long> sizeIfKnown() {
            return Optional.of((long)this.length);
        }
        
        @Override
        public ByteSource slice(long min, long min2) {
            boolean b;
            if (min < 0L) {
                b = true;
            }
            else {
                b = false;
            }
            Preconditions.checkArgument(!b, "offset (%s) may not be negative", min);
            boolean b2;
            if (min2 < 0L) {
                b2 = true;
            }
            else {
                b2 = false;
            }
            Preconditions.checkArgument(!b2, "length (%s) may not be negative", min2);
            min = Math.min(min, this.length);
            min2 = Math.min(min2, this.length - min);
            return new ByteArrayByteSource(this.bytes, (int)min + this.offset, (int)min2);
        }
        
        @Override
        public String toString() {
            return "ByteSource.wrap(" + Ascii.truncate(BaseEncoding.base16().encode(this.bytes, this.offset, this.length), 30, "...") + ")";
        }
    }
    
    private static final class ConcatenatedByteSource extends ByteSource
    {
        final Iterable<? extends ByteSource> sources;
        
        ConcatenatedByteSource(final Iterable<? extends ByteSource> iterable) {
            this.sources = Preconditions.checkNotNull(iterable);
        }
        
        @Override
        public boolean isEmpty() throws IOException {
            final Iterator<? extends ByteSource> iterator = this.sources.iterator();
            while (iterator.hasNext()) {
                if (!((ByteSource)iterator.next()).isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public InputStream openStream() throws IOException {
            return new MultiInputStream(this.sources.iterator());
        }
        
        @Override
        public long size() throws IOException {
            final Iterator<? extends ByteSource> iterator = this.sources.iterator();
            long n = 0L;
            while (iterator.hasNext()) {
                n += ((ByteSource)iterator.next()).size();
            }
            return n;
        }
        
        @Override
        public Optional<Long> sizeIfKnown() {
            final Iterator<? extends ByteSource> iterator = this.sources.iterator();
            long l = 0L;
            while (iterator.hasNext()) {
                final Optional<Long> sizeIfKnown = ((ByteSource)iterator.next()).sizeIfKnown();
                if (!sizeIfKnown.isPresent()) {
                    return Optional.absent();
                }
                l += sizeIfKnown.get();
            }
            return Optional.of(l);
        }
        
        @Override
        public String toString() {
            return "ByteSource.concat(" + this.sources + ")";
        }
    }
    
    private static final class EmptyByteSource extends ByteArrayByteSource
    {
        static final EmptyByteSource INSTANCE;
        
        static {
            INSTANCE = new EmptyByteSource();
        }
        
        EmptyByteSource() {
            super(new byte[0]);
        }
        
        @Override
        public CharSource asCharSource(final Charset charset) {
            Preconditions.checkNotNull(charset);
            return CharSource.empty();
        }
        
        @Override
        public byte[] read() {
            return this.bytes;
        }
        
        @Override
        public String toString() {
            return "ByteSource.empty()";
        }
    }
    
    private final class SlicedByteSource extends ByteSource
    {
        final long length;
        final long offset;
        
        SlicedByteSource(final long n, final long n2) {
            boolean b;
            if (n < 0L) {
                b = true;
            }
            else {
                b = false;
            }
            Preconditions.checkArgument(!b, "offset (%s) may not be negative", n);
            boolean b2;
            if (n2 < 0L) {
                b2 = true;
            }
            else {
                b2 = false;
            }
            Preconditions.checkArgument(!b2, "length (%s) may not be negative", n2);
            this.offset = n;
            this.length = n2;
        }
        
        private InputStream sliceStream(final InputStream inputStream) throws IOException {
            final int n = 1;
            while (true) {
                int n2 = 0;
                Label_0013: {
                    if (this.offset <= 0L) {
                        n2 = 1;
                        break Label_0013;
                    }
                    Label_0058: {
                        break Label_0058;
                        while (true) {
                            while (true) {
                                Label_0093: {
                                    try {
                                        if (ByteStreams.skipUpTo(inputStream, this.offset) < this.offset) {
                                            break Label_0093;
                                        }
                                        final int n3 = n;
                                        if (n3 == 0) {
                                            inputStream.close();
                                            return new ByteArrayInputStream(new byte[0]);
                                        }
                                        return ByteStreams.limit(inputStream, this.length);
                                        n2 = 0;
                                        break;
                                    }
                                    catch (final Throwable t) {
                                        final Closer create = Closer.create();
                                        create.register(inputStream);
                                        try {
                                            throw create.rethrow(t);
                                        }
                                        finally {
                                            create.close();
                                        }
                                    }
                                }
                                final int n3 = 0;
                                continue;
                            }
                        }
                    }
                }
                if (n2 == 0) {
                    continue;
                }
                break;
            }
            return ByteStreams.limit(inputStream, this.length);
        }
        
        @Override
        public boolean isEmpty() throws IOException {
            boolean b = false;
            if (this.length == 0L || super.isEmpty()) {
                b = true;
            }
            return b;
        }
        
        @Override
        public InputStream openBufferedStream() throws IOException {
            return this.sliceStream(ByteSource.this.openBufferedStream());
        }
        
        @Override
        public InputStream openStream() throws IOException {
            return this.sliceStream(ByteSource.this.openStream());
        }
        
        @Override
        public Optional<Long> sizeIfKnown() {
            final Optional<Long> sizeIfKnown = ByteSource.this.sizeIfKnown();
            if (!sizeIfKnown.isPresent()) {
                return Optional.absent();
            }
            final long longValue = sizeIfKnown.get();
            return Optional.of(Math.min(this.length, longValue - Math.min(this.offset, longValue)));
        }
        
        @Override
        public ByteSource slice(final long l, final long n) {
            boolean b;
            if (l < 0L) {
                b = true;
            }
            else {
                b = false;
            }
            Preconditions.checkArgument(!b, "offset (%s) may not be negative", l);
            boolean b2;
            if (n < 0L) {
                b2 = true;
            }
            else {
                b2 = false;
            }
            Preconditions.checkArgument(!b2, "length (%s) may not be negative", n);
            return ByteSource.this.slice(this.offset + l, Math.min(n, this.length - l));
        }
        
        @Override
        public String toString() {
            return ByteSource.this.toString() + ".slice(" + this.offset + ", " + this.length + ")";
        }
    }
}

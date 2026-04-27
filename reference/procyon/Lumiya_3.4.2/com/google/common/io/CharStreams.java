// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.io;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;
import java.io.Writer;
import java.nio.CharBuffer;
import java.io.IOException;
import java.io.Closeable;
import com.google.common.base.Preconditions;
import java.io.Reader;
import com.google.common.annotations.Beta;

@Beta
public final class CharStreams
{
    private static final int BUF_SIZE = 2048;
    
    private CharStreams() {
    }
    
    static Reader asReader(final Readable readable) {
        Preconditions.checkNotNull(readable);
        if (!(readable instanceof Reader)) {
            return new Reader() {
                @Override
                public void close() throws IOException {
                    if (readable instanceof Closeable) {
                        ((Closeable)readable).close();
                    }
                }
                
                @Override
                public int read(final CharBuffer charBuffer) throws IOException {
                    return readable.read(charBuffer);
                }
                
                @Override
                public int read(final char[] array, final int offset, final int length) throws IOException {
                    return this.read(CharBuffer.wrap(array, offset, length));
                }
            };
        }
        return (Reader)readable;
    }
    
    public static Writer asWriter(final Appendable appendable) {
        if (!(appendable instanceof Writer)) {
            return new AppendableWriter(appendable);
        }
        return (Writer)appendable;
    }
    
    public static long copy(final Readable readable, final Appendable appendable) throws IOException {
        Preconditions.checkNotNull(readable);
        Preconditions.checkNotNull(appendable);
        final CharBuffer allocate = CharBuffer.allocate(2048);
        long n = 0L;
        while (readable.read(allocate) != -1) {
            allocate.flip();
            appendable.append(allocate);
            n += allocate.remaining();
            allocate.clear();
        }
        return n;
    }
    
    public static Writer nullWriter() {
        return NullWriter.INSTANCE;
    }
    
    public static <T> T readLines(final Readable readable, final LineProcessor<T> lineProcessor) throws IOException {
        Preconditions.checkNotNull(readable);
        Preconditions.checkNotNull(lineProcessor);
        final LineReader lineReader = new LineReader(readable);
        String line;
        do {
            line = lineReader.readLine();
        } while (line != null && lineProcessor.processLine(line));
        return (T)lineProcessor.getResult();
    }
    
    public static List<String> readLines(final Readable readable) throws IOException {
        final ArrayList list = new ArrayList();
        final LineReader lineReader = new LineReader(readable);
        while (true) {
            final String line = lineReader.readLine();
            if (line == null) {
                break;
            }
            list.add(line);
        }
        return list;
    }
    
    public static void skipFully(final Reader reader, long n) throws IOException {
        Preconditions.checkNotNull(reader);
        while (true) {
            int n2;
            if (n <= 0L) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 != 0) {
                return;
            }
            final long skip = reader.skip(n);
            if (skip == 0L) {
                throw new EOFException();
            }
            n -= skip;
        }
    }
    
    public static String toString(final Readable readable) throws IOException {
        return toStringBuilder(readable).toString();
    }
    
    private static StringBuilder toStringBuilder(final Readable readable) throws IOException {
        final StringBuilder sb = new StringBuilder();
        copy(readable, sb);
        return sb;
    }
    
    private static final class NullWriter extends Writer
    {
        private static final NullWriter INSTANCE;
        
        static {
            INSTANCE = new NullWriter();
        }
        
        @Override
        public Writer append(final char c) {
            return this;
        }
        
        @Override
        public Writer append(final CharSequence charSequence) {
            Preconditions.checkNotNull(charSequence);
            return this;
        }
        
        @Override
        public Writer append(final CharSequence charSequence, final int n, final int n2) {
            Preconditions.checkPositionIndexes(n, n2, charSequence.length());
            return this;
        }
        
        @Override
        public void close() {
        }
        
        @Override
        public void flush() {
        }
        
        @Override
        public String toString() {
            return "CharStreams.nullWriter()";
        }
        
        @Override
        public void write(final int n) {
        }
        
        @Override
        public void write(final String s) {
            Preconditions.checkNotNull(s);
        }
        
        @Override
        public void write(final String s, final int n, final int n2) {
            Preconditions.checkPositionIndexes(n, n + n2, s.length());
        }
        
        @Override
        public void write(final char[] array) {
            Preconditions.checkNotNull(array);
        }
        
        @Override
        public void write(final char[] array, final int n, final int n2) {
            Preconditions.checkPositionIndexes(n, n + n2, array.length);
        }
    }
}

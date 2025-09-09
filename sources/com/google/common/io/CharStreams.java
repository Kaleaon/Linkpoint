package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

@Beta
public final class CharStreams {
    private static final int BUF_SIZE = 2048;

    private static final class NullWriter extends Writer {
        /* access modifiers changed from: private */
        public static final NullWriter INSTANCE = new NullWriter();

        private NullWriter() {
        }

        public Writer append(char c) {
            return this;
        }

        public Writer append(CharSequence charSequence) {
            Preconditions.checkNotNull(charSequence);
            return this;
        }

        public Writer append(CharSequence charSequence, int i, int i2) {
            Preconditions.checkPositionIndexes(i, i2, charSequence.length());
            return this;
        }

        public void close() {
        }

        public void flush() {
        }

        public String toString() {
            return "CharStreams.nullWriter()";
        }

        public void write(int i) {
        }

        public void write(String str) {
            Preconditions.checkNotNull(str);
        }

        public void write(String str, int i, int i2) {
            Preconditions.checkPositionIndexes(i, i + i2, str.length());
        }

        public void write(char[] cArr) {
            Preconditions.checkNotNull(cArr);
        }

        public void write(char[] cArr, int i, int i2) {
            Preconditions.checkPositionIndexes(i, i + i2, cArr.length);
        }
    }

    private CharStreams() {
    }

    static Reader asReader(final Readable readable) {
        Preconditions.checkNotNull(readable);
        return !(readable instanceof Reader) ? new Reader() {
            public void close() throws IOException {
                if (readable instanceof Closeable) {
                    ((Closeable) readable).close();
                }
            }

            public int read(CharBuffer charBuffer) throws IOException {
                return readable.read(charBuffer);
            }

            public int read(char[] cArr, int i, int i2) throws IOException {
                return read(CharBuffer.wrap(cArr, i, i2));
            }
        } : (Reader) readable;
    }

    public static Writer asWriter(Appendable appendable) {
        return !(appendable instanceof Writer) ? new AppendableWriter(appendable) : (Writer) appendable;
    }

    public static long copy(Readable readable, Appendable appendable) throws IOException {
        Preconditions.checkNotNull(readable);
        Preconditions.checkNotNull(appendable);
        CharBuffer allocate = CharBuffer.allocate(2048);
        long j = 0;
        while (readable.read(allocate) != -1) {
            allocate.flip();
            appendable.append(allocate);
            j += (long) allocate.remaining();
            allocate.clear();
        }
        return j;
    }

    public static Writer nullWriter() {
        return NullWriter.INSTANCE;
    }

    public static <T> T readLines(Readable readable, LineProcessor<T> lineProcessor) throws IOException {
        String readLine;
        Preconditions.checkNotNull(readable);
        Preconditions.checkNotNull(lineProcessor);
        LineReader lineReader = new LineReader(readable);
        do {
            readLine = lineReader.readLine();
            if (readLine == null || !lineProcessor.processLine(readLine)) {
            }
            readLine = lineReader.readLine();
            break;
        } while (!lineProcessor.processLine(readLine));
        return lineProcessor.getResult();
    }

    public static List<String> readLines(Readable readable) throws IOException {
        ArrayList arrayList = new ArrayList();
        LineReader lineReader = new LineReader(readable);
        while (true) {
            String readLine = lineReader.readLine();
            if (readLine == null) {
                return arrayList;
            }
            arrayList.add(readLine);
        }
    }

    public static void skipFully(Reader reader, long j) throws IOException {
        Preconditions.checkNotNull(reader);
        while (true) {
            if (!(j <= 0)) {
                long skip = reader.skip(j);
                if (skip == 0) {
                    throw new EOFException();
                }
                j -= skip;
            } else {
                return;
            }
        }
    }

    public static String toString(Readable readable) throws IOException {
        return toStringBuilder(readable).toString();
    }

    private static StringBuilder toStringBuilder(Readable readable) throws IOException {
        StringBuilder sb = new StringBuilder();
        copy(readable, sb);
        return sb;
    }
}

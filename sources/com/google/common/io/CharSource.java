package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Ascii;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public abstract class CharSource {

    private static class CharSequenceCharSource extends CharSource {
        /* access modifiers changed from: private */
        public static final Splitter LINE_SPLITTER = Splitter.on(Pattern.compile("\r\n|\n|\r"));
        /* access modifiers changed from: private */
        public final CharSequence seq;

        protected CharSequenceCharSource(CharSequence charSequence) {
            this.seq = (CharSequence) Preconditions.checkNotNull(charSequence);
        }

        private Iterable<String> lines() {
            return new Iterable<String>() {
                public Iterator<String> iterator() {
                    return new AbstractIterator<String>() {
                        Iterator<String> lines = CharSequenceCharSource.LINE_SPLITTER.split(CharSequenceCharSource.this.seq).iterator();

                        /* access modifiers changed from: protected */
                        public String computeNext() {
                            if (this.lines.hasNext()) {
                                String next = this.lines.next();
                                if (this.lines.hasNext() || !next.isEmpty()) {
                                    return next;
                                }
                            }
                            return (String) endOfData();
                        }
                    };
                }
            };
        }

        public boolean isEmpty() {
            return this.seq.length() == 0;
        }

        public long length() {
            return (long) this.seq.length();
        }

        public Optional<Long> lengthIfKnown() {
            return Optional.of(Long.valueOf((long) this.seq.length()));
        }

        public Reader openStream() {
            return new CharSequenceReader(this.seq);
        }

        public String read() {
            return this.seq.toString();
        }

        public String readFirstLine() {
            Iterator<String> it = lines().iterator();
            if (!it.hasNext()) {
                return null;
            }
            return it.next();
        }

        public ImmutableList<String> readLines() {
            return ImmutableList.copyOf(lines());
        }

        /* JADX WARNING: Removed duplicated region for block: B:1:0x0008 A[LOOP:0: B:1:0x0008->B:6:0x001d, LOOP_START] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public <T> T readLines(com.google.common.io.LineProcessor<T> r3) throws java.io.IOException {
            /*
                r2 = this;
                java.lang.Iterable r0 = r2.lines()
                java.util.Iterator r1 = r0.iterator()
            L_0x0008:
                boolean r0 = r1.hasNext()
                if (r0 != 0) goto L_0x0013
            L_0x000e:
                java.lang.Object r0 = r3.getResult()
                return r0
            L_0x0013:
                java.lang.Object r0 = r1.next()
                java.lang.String r0 = (java.lang.String) r0
                boolean r0 = r3.processLine(r0)
                if (r0 == 0) goto L_0x000e
                goto L_0x0008
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.common.io.CharSource.CharSequenceCharSource.readLines(com.google.common.io.LineProcessor):java.lang.Object");
        }

        public String toString() {
            return "CharSource.wrap(" + Ascii.truncate(this.seq, 30, "...") + ")";
        }
    }

    private static final class ConcatenatedCharSource extends CharSource {
        private final Iterable<? extends CharSource> sources;

        ConcatenatedCharSource(Iterable<? extends CharSource> iterable) {
            this.sources = (Iterable) Preconditions.checkNotNull(iterable);
        }

        public boolean isEmpty() throws IOException {
            for (CharSource isEmpty : this.sources) {
                if (!isEmpty.isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        public long length() throws IOException {
            long j = 0;
            Iterator<? extends CharSource> it = this.sources.iterator();
            while (true) {
                long j2 = j;
                if (!it.hasNext()) {
                    return j2;
                }
                j = ((CharSource) it.next()).length() + j2;
            }
        }

        public Optional<Long> lengthIfKnown() {
            long j = 0;
            Iterator<? extends CharSource> it = this.sources.iterator();
            while (true) {
                long j2 = j;
                if (!it.hasNext()) {
                    return Optional.of(Long.valueOf(j2));
                }
                Optional<Long> lengthIfKnown = ((CharSource) it.next()).lengthIfKnown();
                if (!lengthIfKnown.isPresent()) {
                    return Optional.absent();
                }
                j = lengthIfKnown.get().longValue() + j2;
            }
        }

        public Reader openStream() throws IOException {
            return new MultiReader(this.sources.iterator());
        }

        public String toString() {
            return "CharSource.concat(" + this.sources + ")";
        }
    }

    private static final class EmptyCharSource extends CharSequenceCharSource {
        /* access modifiers changed from: private */
        public static final EmptyCharSource INSTANCE = new EmptyCharSource();

        private EmptyCharSource() {
            super("");
        }

        public String toString() {
            return "CharSource.empty()";
        }
    }

    protected CharSource() {
    }

    public static CharSource concat(Iterable<? extends CharSource> iterable) {
        return new ConcatenatedCharSource(iterable);
    }

    public static CharSource concat(Iterator<? extends CharSource> it) {
        return concat((Iterable<? extends CharSource>) ImmutableList.copyOf(it));
    }

    public static CharSource concat(CharSource... charSourceArr) {
        return concat((Iterable<? extends CharSource>) ImmutableList.copyOf((E[]) charSourceArr));
    }

    private long countBySkipping(Reader reader) throws IOException {
        long j = 0;
        while (true) {
            long skip = reader.skip(Long.MAX_VALUE);
            if (skip == 0) {
                return j;
            }
            j += skip;
        }
    }

    public static CharSource empty() {
        return EmptyCharSource.INSTANCE;
    }

    public static CharSource wrap(CharSequence charSequence) {
        return new CharSequenceCharSource(charSequence);
    }

    public long copyTo(CharSink charSink) throws IOException {
        Preconditions.checkNotNull(charSink);
        Closer create = Closer.create();
        try {
            long copy = CharStreams.copy((Reader) create.register(openStream()), (Writer) create.register(charSink.openStream()));
            create.close();
            return copy;
        } catch (Throwable th) {
            create.close();
            throw th;
        }
    }

    public long copyTo(Appendable appendable) throws IOException {
        Preconditions.checkNotNull(appendable);
        Closer create = Closer.create();
        try {
            long copy = CharStreams.copy((Reader) create.register(openStream()), appendable);
            create.close();
            return copy;
        } catch (Throwable th) {
            create.close();
            throw th;
        }
    }

    public boolean isEmpty() throws IOException {
        Optional<Long> lengthIfKnown = lengthIfKnown();
        if (lengthIfKnown.isPresent() && lengthIfKnown.get().longValue() == 0) {
            return true;
        }
        Closer create = Closer.create();
        try {
            boolean z = ((Reader) create.register(openStream())).read() == -1;
            create.close();
            return z;
        } catch (Throwable th) {
            create.close();
            throw th;
        }
    }

    @Beta
    public long length() throws IOException {
        Optional<Long> lengthIfKnown = lengthIfKnown();
        if (lengthIfKnown.isPresent()) {
            return lengthIfKnown.get().longValue();
        }
        Closer create = Closer.create();
        try {
            long countBySkipping = countBySkipping((Reader) create.register(openStream()));
            create.close();
            return countBySkipping;
        } catch (Throwable th) {
            create.close();
            throw th;
        }
    }

    @Beta
    public Optional<Long> lengthIfKnown() {
        return Optional.absent();
    }

    public BufferedReader openBufferedStream() throws IOException {
        Reader openStream = openStream();
        return !(openStream instanceof BufferedReader) ? new BufferedReader(openStream) : (BufferedReader) openStream;
    }

    public abstract Reader openStream() throws IOException;

    public String read() throws IOException {
        Closer create = Closer.create();
        try {
            String charStreams = CharStreams.toString((Reader) create.register(openStream()));
            create.close();
            return charStreams;
        } catch (Throwable th) {
            create.close();
            throw th;
        }
    }

    @Nullable
    public String readFirstLine() throws IOException {
        Closer create = Closer.create();
        try {
            String readLine = ((BufferedReader) create.register(openBufferedStream())).readLine();
            create.close();
            return readLine;
        } catch (Throwable th) {
            create.close();
            throw th;
        }
    }

    public ImmutableList<String> readLines() throws IOException {
        Closer create = Closer.create();
        try {
            BufferedReader bufferedReader = (BufferedReader) create.register(openBufferedStream());
            ArrayList newArrayList = Lists.newArrayList();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    ImmutableList<String> copyOf = ImmutableList.copyOf(newArrayList);
                    create.close();
                    return copyOf;
                }
                newArrayList.add(readLine);
            }
        } catch (Throwable th) {
            create.close();
            throw th;
        }
    }

    @Beta
    public <T> T readLines(LineProcessor<T> lineProcessor) throws IOException {
        Preconditions.checkNotNull(lineProcessor);
        Closer create = Closer.create();
        try {
            T readLines = CharStreams.readLines((Reader) create.register(openStream()), lineProcessor);
            create.close();
            return readLines;
        } catch (Throwable th) {
            create.close();
            throw th;
        }
    }
}

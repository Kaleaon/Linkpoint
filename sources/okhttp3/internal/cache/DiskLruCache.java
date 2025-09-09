package okhttp3.internal.cache;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Flushable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import okhttp3.internal.Util;
import okhttp3.internal.io.FileSystem;
import okhttp3.internal.platform.Platform;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

public final class DiskLruCache implements Closeable, Flushable {
    static final /* synthetic */ boolean $assertionsDisabled;
    static final long ANY_SEQUENCE_NUMBER = -1;
    private static final String CLEAN = "CLEAN";
    private static final String DIRTY = "DIRTY";
    static final String JOURNAL_FILE = "journal";
    static final String JOURNAL_FILE_BACKUP = "journal.bkp";
    static final String JOURNAL_FILE_TEMP = "journal.tmp";
    static final Pattern LEGAL_KEY_PATTERN = Pattern.compile("[a-z0-9_-]{1,120}");
    static final String MAGIC = "libcore.io.DiskLruCache";
    private static final String READ = "READ";
    private static final String REMOVE = "REMOVE";
    static final String VERSION_1 = "1";
    private final int appVersion;
    private final Runnable cleanupRunnable = new Runnable() {
        public void run() {
            boolean z = false;
            synchronized (DiskLruCache.this) {
                if (!DiskLruCache.this.initialized) {
                    z = true;
                }
                if (!z && !DiskLruCache.this.closed) {
                    try {
                        DiskLruCache.this.trimToSize();
                    } catch (IOException e) {
                        DiskLruCache.this.mostRecentTrimFailed = true;
                    }
                    try {
                        if (DiskLruCache.this.journalRebuildRequired()) {
                            DiskLruCache.this.rebuildJournal();
                            DiskLruCache.this.redundantOpCount = 0;
                        }
                    } catch (IOException e2) {
                        DiskLruCache.this.mostRecentRebuildFailed = true;
                        DiskLruCache.this.journalWriter = Okio.buffer(Okio.blackhole());
                    }
                    return;
                }
                return;
            }
        }
    };
    boolean closed;
    final File directory;
    private final Executor executor;
    final FileSystem fileSystem;
    boolean hasJournalErrors;
    boolean initialized;
    private final File journalFile;
    private final File journalFileBackup;
    private final File journalFileTmp;
    BufferedSink journalWriter;
    final LinkedHashMap<String, Entry> lruEntries = new LinkedHashMap<>(0, 0.75f, true);
    private long maxSize;
    boolean mostRecentRebuildFailed;
    boolean mostRecentTrimFailed;
    private long nextSequenceNumber = 0;
    int redundantOpCount;
    private long size = 0;
    final int valueCount;

    public final class Editor {
        private boolean done;
        final Entry entry;
        final boolean[] written;

        Editor(Entry entry2) {
            this.entry = entry2;
            this.written = !entry2.readable ? new boolean[DiskLruCache.this.valueCount] : null;
        }

        public void abort() throws IOException {
            synchronized (DiskLruCache.this) {
                if (!this.done) {
                    if (this.entry.currentEditor == this) {
                        DiskLruCache.this.completeEdit(this, false);
                    }
                    this.done = true;
                } else {
                    throw new IllegalStateException();
                }
            }
        }

        public void abortUnlessCommitted() {
            synchronized (DiskLruCache.this) {
                if (!this.done && this.entry.currentEditor == this) {
                    try {
                        DiskLruCache.this.completeEdit(this, false);
                    } catch (IOException e) {
                    }
                }
            }
        }

        public void commit() throws IOException {
            synchronized (DiskLruCache.this) {
                if (!this.done) {
                    if (this.entry.currentEditor == this) {
                        DiskLruCache.this.completeEdit(this, true);
                    }
                    this.done = true;
                } else {
                    throw new IllegalStateException();
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void detach() {
            if (this.entry.currentEditor == this) {
                for (int i = 0; i < DiskLruCache.this.valueCount; i++) {
                    try {
                        DiskLruCache.this.fileSystem.delete(this.entry.dirtyFiles[i]);
                    } catch (IOException e) {
                    }
                }
                this.entry.currentEditor = null;
            }
        }

        public Sink newSink(int i) {
            synchronized (DiskLruCache.this) {
                if (this.done) {
                    throw new IllegalStateException();
                } else if (this.entry.currentEditor == this) {
                    if (!this.entry.readable) {
                        this.written[i] = true;
                    }
                    try {
                        AnonymousClass1 r2 = new FaultHidingSink(DiskLruCache.this.fileSystem.sink(this.entry.dirtyFiles[i])) {
                            /* access modifiers changed from: protected */
                            public void onException(IOException iOException) {
                                synchronized (DiskLruCache.this) {
                                    Editor.this.detach();
                                }
                            }
                        };
                        return r2;
                    } catch (FileNotFoundException e) {
                        return Okio.blackhole();
                    }
                } else {
                    Sink blackhole = Okio.blackhole();
                    return blackhole;
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:8:0x000f, code lost:
            return null;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public okio.Source newSource(int r5) {
            /*
                r4 = this;
                r3 = 0
                okhttp3.internal.cache.DiskLruCache r1 = okhttp3.internal.cache.DiskLruCache.this
                monitor-enter(r1)
                boolean r0 = r4.done     // Catch:{ all -> 0x0016 }
                if (r0 != 0) goto L_0x0010
                okhttp3.internal.cache.DiskLruCache$Entry r0 = r4.entry     // Catch:{ all -> 0x0016 }
                boolean r0 = r0.readable     // Catch:{ all -> 0x0016 }
                if (r0 != 0) goto L_0x0019
            L_0x000e:
                monitor-exit(r1)     // Catch:{ all -> 0x0016 }
                return r3
            L_0x0010:
                java.lang.IllegalStateException r0 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0016 }
                r0.<init>()     // Catch:{ all -> 0x0016 }
                throw r0     // Catch:{ all -> 0x0016 }
            L_0x0016:
                r0 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x0016 }
                throw r0
            L_0x0019:
                okhttp3.internal.cache.DiskLruCache$Entry r0 = r4.entry     // Catch:{ all -> 0x0016 }
                okhttp3.internal.cache.DiskLruCache$Editor r0 = r0.currentEditor     // Catch:{ all -> 0x0016 }
                if (r0 != r4) goto L_0x000e
                okhttp3.internal.cache.DiskLruCache r0 = okhttp3.internal.cache.DiskLruCache.this     // Catch:{ FileNotFoundException -> 0x002f }
                okhttp3.internal.io.FileSystem r0 = r0.fileSystem     // Catch:{ FileNotFoundException -> 0x002f }
                okhttp3.internal.cache.DiskLruCache$Entry r2 = r4.entry     // Catch:{ FileNotFoundException -> 0x002f }
                java.io.File[] r2 = r2.cleanFiles     // Catch:{ FileNotFoundException -> 0x002f }
                r2 = r2[r5]     // Catch:{ FileNotFoundException -> 0x002f }
                okio.Source r0 = r0.source(r2)     // Catch:{ FileNotFoundException -> 0x002f }
                monitor-exit(r1)     // Catch:{ all -> 0x0016 }
                return r0
            L_0x002f:
                r0 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x0016 }
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.Editor.newSource(int):okio.Source");
        }
    }

    private final class Entry {
        final File[] cleanFiles;
        Editor currentEditor;
        final File[] dirtyFiles;
        final String key;
        final long[] lengths;
        boolean readable;
        long sequenceNumber;

        Entry(String str) {
            this.key = str;
            this.lengths = new long[DiskLruCache.this.valueCount];
            this.cleanFiles = new File[DiskLruCache.this.valueCount];
            this.dirtyFiles = new File[DiskLruCache.this.valueCount];
            StringBuilder append = new StringBuilder(str).append('.');
            int length = append.length();
            for (int i = 0; i < DiskLruCache.this.valueCount; i++) {
                append.append(i);
                this.cleanFiles[i] = new File(DiskLruCache.this.directory, append.toString());
                append.append(".tmp");
                this.dirtyFiles[i] = new File(DiskLruCache.this.directory, append.toString());
                append.setLength(length);
            }
        }

        private IOException invalidLengths(String[] strArr) throws IOException {
            throw new IOException("unexpected journal line: " + Arrays.toString(strArr));
        }

        /* access modifiers changed from: package-private */
        public void setLengths(String[] strArr) throws IOException {
            if (strArr.length == DiskLruCache.this.valueCount) {
                int i = 0;
                while (i < strArr.length) {
                    try {
                        this.lengths[i] = Long.parseLong(strArr[i]);
                        i++;
                    } catch (NumberFormatException e) {
                        throw invalidLengths(strArr);
                    }
                }
                return;
            }
            throw invalidLengths(strArr);
        }

        /* access modifiers changed from: package-private */
        public Snapshot snapshot() {
            int i = 0;
            if (Thread.holdsLock(DiskLruCache.this)) {
                Source[] sourceArr = new Source[DiskLruCache.this.valueCount];
                long[] jArr = (long[]) this.lengths.clone();
                int i2 = 0;
                while (i2 < DiskLruCache.this.valueCount) {
                    try {
                        sourceArr[i2] = DiskLruCache.this.fileSystem.source(this.cleanFiles[i2]);
                        i2++;
                    } catch (FileNotFoundException e) {
                        while (i < DiskLruCache.this.valueCount && sourceArr[i] != null) {
                            Util.closeQuietly((Closeable) sourceArr[i]);
                            i++;
                        }
                        try {
                            DiskLruCache.this.removeEntry(this);
                        } catch (IOException e2) {
                        }
                        return null;
                    }
                }
                return new Snapshot(this.key, this.sequenceNumber, sourceArr, jArr);
            }
            throw new AssertionError();
        }

        /* access modifiers changed from: package-private */
        public void writeLengths(BufferedSink bufferedSink) throws IOException {
            for (long writeDecimalLong : this.lengths) {
                bufferedSink.writeByte(32).writeDecimalLong(writeDecimalLong);
            }
        }
    }

    public final class Snapshot implements Closeable {
        /* access modifiers changed from: private */
        public final String key;
        private final long[] lengths;
        private final long sequenceNumber;
        private final Source[] sources;

        Snapshot(String str, long j, Source[] sourceArr, long[] jArr) {
            this.key = str;
            this.sequenceNumber = j;
            this.sources = sourceArr;
            this.lengths = jArr;
        }

        public void close() {
            for (Source closeQuietly : this.sources) {
                Util.closeQuietly((Closeable) closeQuietly);
            }
        }

        public Editor edit() throws IOException {
            return DiskLruCache.this.edit(this.key, this.sequenceNumber);
        }

        public long getLength(int i) {
            return this.lengths[i];
        }

        public Source getSource(int i) {
            return this.sources[i];
        }

        public String key() {
            return this.key;
        }
    }

    static {
        boolean z = false;
        if (!DiskLruCache.class.desiredAssertionStatus()) {
            z = true;
        }
        $assertionsDisabled = z;
    }

    DiskLruCache(FileSystem fileSystem2, File file, int i, int i2, long j, Executor executor2) {
        this.fileSystem = fileSystem2;
        this.directory = file;
        this.appVersion = i;
        this.journalFile = new File(file, JOURNAL_FILE);
        this.journalFileTmp = new File(file, JOURNAL_FILE_TEMP);
        this.journalFileBackup = new File(file, JOURNAL_FILE_BACKUP);
        this.valueCount = i2;
        this.maxSize = j;
        this.executor = executor2;
    }

    private synchronized void checkNotClosed() {
        if (isClosed()) {
            throw new IllegalStateException("cache is closed");
        }
    }

    public static DiskLruCache create(FileSystem fileSystem2, File file, int i, int i2, long j) {
        if (!(j > 0)) {
            throw new IllegalArgumentException("maxSize <= 0");
        } else if (i2 > 0) {
            return new DiskLruCache(fileSystem2, file, i, i2, j, new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue(), Util.threadFactory("OkHttp DiskLruCache", true)));
        } else {
            throw new IllegalArgumentException("valueCount <= 0");
        }
    }

    private BufferedSink newJournalWriter() throws FileNotFoundException {
        return Okio.buffer((Sink) new FaultHidingSink(this.fileSystem.appendingSink(this.journalFile)) {
            static final /* synthetic */ boolean $assertionsDisabled;

            static {
                boolean z = false;
                if (!DiskLruCache.class.desiredAssertionStatus()) {
                    z = true;
                }
                $assertionsDisabled = z;
            }

            /* access modifiers changed from: protected */
            public void onException(IOException iOException) {
                if (!$assertionsDisabled && !Thread.holdsLock(DiskLruCache.this)) {
                    throw new AssertionError();
                }
                DiskLruCache.this.hasJournalErrors = true;
            }
        });
    }

    private void processJournal() throws IOException {
        this.fileSystem.delete(this.journalFileTmp);
        Iterator<Entry> it = this.lruEntries.values().iterator();
        while (it.hasNext()) {
            Entry next = it.next();
            if (next.currentEditor != null) {
                next.currentEditor = null;
                for (int i = 0; i < this.valueCount; i++) {
                    this.fileSystem.delete(next.cleanFiles[i]);
                    this.fileSystem.delete(next.dirtyFiles[i]);
                }
                it.remove();
            } else {
                for (int i2 = 0; i2 < this.valueCount; i2++) {
                    this.size += next.lengths[i2];
                }
            }
        }
    }

    private void readJournal() throws IOException {
        int i = 0;
        BufferedSource buffer = Okio.buffer(this.fileSystem.source(this.journalFile));
        try {
            String readUtf8LineStrict = buffer.readUtf8LineStrict();
            String readUtf8LineStrict2 = buffer.readUtf8LineStrict();
            String readUtf8LineStrict3 = buffer.readUtf8LineStrict();
            String readUtf8LineStrict4 = buffer.readUtf8LineStrict();
            String readUtf8LineStrict5 = buffer.readUtf8LineStrict();
            if (MAGIC.equals(readUtf8LineStrict)) {
                if (VERSION_1.equals(readUtf8LineStrict2) && Integer.toString(this.appVersion).equals(readUtf8LineStrict3) && Integer.toString(this.valueCount).equals(readUtf8LineStrict4) && "".equals(readUtf8LineStrict5)) {
                    while (true) {
                        readJournalLine(buffer.readUtf8LineStrict());
                        i++;
                    }
                }
            }
            throw new IOException("unexpected journal header: [" + readUtf8LineStrict + ", " + readUtf8LineStrict2 + ", " + readUtf8LineStrict4 + ", " + readUtf8LineStrict5 + "]");
        } catch (EOFException e) {
            this.redundantOpCount = i - this.lruEntries.size();
            if (buffer.exhausted()) {
                this.journalWriter = newJournalWriter();
            } else {
                rebuildJournal();
            }
            Util.closeQuietly((Closeable) buffer);
        } catch (Throwable th) {
            Util.closeQuietly((Closeable) buffer);
            throw th;
        }
    }

    private void readJournalLine(String str) throws IOException {
        String str2;
        int indexOf = str.indexOf(32);
        if (indexOf != -1) {
            int i = indexOf + 1;
            int indexOf2 = str.indexOf(32, i);
            if (indexOf2 != -1) {
                str2 = str.substring(i, indexOf2);
            } else {
                String substring = str.substring(i);
                if (indexOf == REMOVE.length() && str.startsWith(REMOVE)) {
                    this.lruEntries.remove(substring);
                    return;
                }
                str2 = substring;
            }
            Entry entry = this.lruEntries.get(str2);
            if (entry == null) {
                entry = new Entry(str2);
                this.lruEntries.put(str2, entry);
            }
            if (indexOf2 != -1 && indexOf == CLEAN.length() && str.startsWith(CLEAN)) {
                String[] split = str.substring(indexOf2 + 1).split(" ");
                entry.readable = true;
                entry.currentEditor = null;
                entry.setLengths(split);
            } else if (indexOf2 == -1 && indexOf == DIRTY.length() && str.startsWith(DIRTY)) {
                entry.currentEditor = new Editor(entry);
            } else if (indexOf2 != -1 || indexOf != READ.length() || !str.startsWith(READ)) {
                throw new IOException("unexpected journal line: " + str);
            }
        } else {
            throw new IOException("unexpected journal line: " + str);
        }
    }

    private void validateKey(String str) {
        if (!LEGAL_KEY_PATTERN.matcher(str).matches()) {
            throw new IllegalArgumentException("keys must match regex [a-z0-9_-]{1,120}: \"" + str + "\"");
        }
    }

    public synchronized void close() throws IOException {
        synchronized (this) {
            if (this.initialized) {
                if (!this.closed) {
                    for (Entry entry : (Entry[]) this.lruEntries.values().toArray(new Entry[this.lruEntries.size()])) {
                        if (entry.currentEditor != null) {
                            entry.currentEditor.abort();
                        }
                    }
                    trimToSize();
                    this.journalWriter.close();
                    this.journalWriter = null;
                    this.closed = true;
                    return;
                }
            }
            this.closed = true;
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0057, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void completeEdit(okhttp3.internal.cache.DiskLruCache.Editor r11, boolean r12) throws java.io.IOException {
        /*
            r10 = this;
            r0 = 1
            r1 = 0
            monitor-enter(r10)
            okhttp3.internal.cache.DiskLruCache$Entry r3 = r11.entry     // Catch:{ all -> 0x005e }
            okhttp3.internal.cache.DiskLruCache$Editor r2 = r3.currentEditor     // Catch:{ all -> 0x005e }
            if (r2 != r11) goto L_0x0058
            if (r12 != 0) goto L_0x0061
        L_0x000b:
            r2 = r1
        L_0x000c:
            int r4 = r10.valueCount     // Catch:{ all -> 0x005e }
            if (r2 < r4) goto L_0x00a1
            int r2 = r10.redundantOpCount     // Catch:{ all -> 0x005e }
            int r2 = r2 + 1
            r10.redundantOpCount = r2     // Catch:{ all -> 0x005e }
            r2 = 0
            r3.currentEditor = r2     // Catch:{ all -> 0x005e }
            boolean r2 = r3.readable     // Catch:{ all -> 0x005e }
            r2 = r2 | r12
            if (r2 != 0) goto L_0x00d7
            java.util.LinkedHashMap<java.lang.String, okhttp3.internal.cache.DiskLruCache$Entry> r2 = r10.lruEntries     // Catch:{ all -> 0x005e }
            java.lang.String r4 = r3.key     // Catch:{ all -> 0x005e }
            r2.remove(r4)     // Catch:{ all -> 0x005e }
            okio.BufferedSink r2 = r10.journalWriter     // Catch:{ all -> 0x005e }
            java.lang.String r4 = "REMOVE"
            okio.BufferedSink r2 = r2.writeUtf8(r4)     // Catch:{ all -> 0x005e }
            r4 = 32
            r2.writeByte(r4)     // Catch:{ all -> 0x005e }
            okio.BufferedSink r2 = r10.journalWriter     // Catch:{ all -> 0x005e }
            java.lang.String r3 = r3.key     // Catch:{ all -> 0x005e }
            r2.writeUtf8(r3)     // Catch:{ all -> 0x005e }
            okio.BufferedSink r2 = r10.journalWriter     // Catch:{ all -> 0x005e }
            r3 = 10
            r2.writeByte(r3)     // Catch:{ all -> 0x005e }
        L_0x0041:
            okio.BufferedSink r2 = r10.journalWriter     // Catch:{ all -> 0x005e }
            r2.flush()     // Catch:{ all -> 0x005e }
            long r2 = r10.size     // Catch:{ all -> 0x005e }
            long r4 = r10.maxSize     // Catch:{ all -> 0x005e }
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 <= 0) goto L_0x0108
        L_0x004e:
            if (r0 != 0) goto L_0x010b
            boolean r0 = r10.journalRebuildRequired()     // Catch:{ all -> 0x005e }
            if (r0 != 0) goto L_0x010b
        L_0x0056:
            monitor-exit(r10)
            return
        L_0x0058:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException     // Catch:{ all -> 0x005e }
            r0.<init>()     // Catch:{ all -> 0x005e }
            throw r0     // Catch:{ all -> 0x005e }
        L_0x005e:
            r0 = move-exception
            monitor-exit(r10)
            throw r0
        L_0x0061:
            boolean r2 = r3.readable     // Catch:{ all -> 0x005e }
            if (r2 != 0) goto L_0x000b
            r2 = r1
        L_0x0066:
            int r4 = r10.valueCount     // Catch:{ all -> 0x005e }
            if (r2 >= r4) goto L_0x000b
            boolean[] r4 = r11.written     // Catch:{ all -> 0x005e }
            boolean r4 = r4[r2]     // Catch:{ all -> 0x005e }
            if (r4 == 0) goto L_0x007f
            okhttp3.internal.io.FileSystem r4 = r10.fileSystem     // Catch:{ all -> 0x005e }
            java.io.File[] r5 = r3.dirtyFiles     // Catch:{ all -> 0x005e }
            r5 = r5[r2]     // Catch:{ all -> 0x005e }
            boolean r4 = r4.exists(r5)     // Catch:{ all -> 0x005e }
            if (r4 == 0) goto L_0x009c
            int r2 = r2 + 1
            goto L_0x0066
        L_0x007f:
            r11.abort()     // Catch:{ all -> 0x005e }
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException     // Catch:{ all -> 0x005e }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x005e }
            r1.<init>()     // Catch:{ all -> 0x005e }
            java.lang.String r3 = "Newly created entry didn't create value for index "
            java.lang.StringBuilder r1 = r1.append(r3)     // Catch:{ all -> 0x005e }
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x005e }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x005e }
            r0.<init>(r1)     // Catch:{ all -> 0x005e }
            throw r0     // Catch:{ all -> 0x005e }
        L_0x009c:
            r11.abort()     // Catch:{ all -> 0x005e }
            monitor-exit(r10)
            return
        L_0x00a1:
            java.io.File[] r4 = r3.dirtyFiles     // Catch:{ all -> 0x005e }
            r4 = r4[r2]     // Catch:{ all -> 0x005e }
            if (r12 != 0) goto L_0x00b0
            okhttp3.internal.io.FileSystem r5 = r10.fileSystem     // Catch:{ all -> 0x005e }
            r5.delete(r4)     // Catch:{ all -> 0x005e }
        L_0x00ac:
            int r2 = r2 + 1
            goto L_0x000c
        L_0x00b0:
            okhttp3.internal.io.FileSystem r5 = r10.fileSystem     // Catch:{ all -> 0x005e }
            boolean r5 = r5.exists(r4)     // Catch:{ all -> 0x005e }
            if (r5 == 0) goto L_0x00ac
            java.io.File[] r5 = r3.cleanFiles     // Catch:{ all -> 0x005e }
            r5 = r5[r2]     // Catch:{ all -> 0x005e }
            okhttp3.internal.io.FileSystem r6 = r10.fileSystem     // Catch:{ all -> 0x005e }
            r6.rename(r4, r5)     // Catch:{ all -> 0x005e }
            long[] r4 = r3.lengths     // Catch:{ all -> 0x005e }
            r6 = r4[r2]     // Catch:{ all -> 0x005e }
            okhttp3.internal.io.FileSystem r4 = r10.fileSystem     // Catch:{ all -> 0x005e }
            long r4 = r4.size(r5)     // Catch:{ all -> 0x005e }
            long[] r8 = r3.lengths     // Catch:{ all -> 0x005e }
            r8[r2] = r4     // Catch:{ all -> 0x005e }
            long r8 = r10.size     // Catch:{ all -> 0x005e }
            long r6 = r8 - r6
            long r4 = r4 + r6
            r10.size = r4     // Catch:{ all -> 0x005e }
            goto L_0x00ac
        L_0x00d7:
            r2 = 1
            r3.readable = r2     // Catch:{ all -> 0x005e }
            okio.BufferedSink r2 = r10.journalWriter     // Catch:{ all -> 0x005e }
            java.lang.String r4 = "CLEAN"
            okio.BufferedSink r2 = r2.writeUtf8(r4)     // Catch:{ all -> 0x005e }
            r4 = 32
            r2.writeByte(r4)     // Catch:{ all -> 0x005e }
            okio.BufferedSink r2 = r10.journalWriter     // Catch:{ all -> 0x005e }
            java.lang.String r4 = r3.key     // Catch:{ all -> 0x005e }
            r2.writeUtf8(r4)     // Catch:{ all -> 0x005e }
            okio.BufferedSink r2 = r10.journalWriter     // Catch:{ all -> 0x005e }
            r3.writeLengths(r2)     // Catch:{ all -> 0x005e }
            okio.BufferedSink r2 = r10.journalWriter     // Catch:{ all -> 0x005e }
            r4 = 10
            r2.writeByte(r4)     // Catch:{ all -> 0x005e }
            if (r12 == 0) goto L_0x0041
            long r4 = r10.nextSequenceNumber     // Catch:{ all -> 0x005e }
            r6 = 1
            long r6 = r6 + r4
            r10.nextSequenceNumber = r6     // Catch:{ all -> 0x005e }
            r3.sequenceNumber = r4     // Catch:{ all -> 0x005e }
            goto L_0x0041
        L_0x0108:
            r0 = r1
            goto L_0x004e
        L_0x010b:
            java.util.concurrent.Executor r0 = r10.executor     // Catch:{ all -> 0x005e }
            java.lang.Runnable r1 = r10.cleanupRunnable     // Catch:{ all -> 0x005e }
            r0.execute(r1)     // Catch:{ all -> 0x005e }
            goto L_0x0056
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.completeEdit(okhttp3.internal.cache.DiskLruCache$Editor, boolean):void");
    }

    public void delete() throws IOException {
        close();
        this.fileSystem.deleteContents(this.directory);
    }

    public Editor edit(String str) throws IOException {
        return edit(str, -1);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0021, code lost:
        if (r0.sequenceNumber == r8) goto L_0x0023;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x001c, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized okhttp3.internal.cache.DiskLruCache.Editor edit(java.lang.String r7, long r8) throws java.io.IOException {
        /*
            r6 = this;
            r4 = 0
            monitor-enter(r6)
            r6.initialize()     // Catch:{ all -> 0x0075 }
            r6.checkNotClosed()     // Catch:{ all -> 0x0075 }
            r6.validateKey(r7)     // Catch:{ all -> 0x0075 }
            java.util.LinkedHashMap<java.lang.String, okhttp3.internal.cache.DiskLruCache$Entry> r0 = r6.lruEntries     // Catch:{ all -> 0x0075 }
            java.lang.Object r0 = r0.get(r7)     // Catch:{ all -> 0x0075 }
            okhttp3.internal.cache.DiskLruCache$Entry r0 = (okhttp3.internal.cache.DiskLruCache.Entry) r0     // Catch:{ all -> 0x0075 }
            r2 = -1
            int r1 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x0023
            if (r0 != 0) goto L_0x001d
        L_0x001b:
            monitor-exit(r6)
            return r4
        L_0x001d:
            long r2 = r0.sequenceNumber     // Catch:{ all -> 0x0075 }
            int r1 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r1 != 0) goto L_0x001b
        L_0x0023:
            if (r0 != 0) goto L_0x0032
        L_0x0025:
            boolean r1 = r6.mostRecentTrimFailed     // Catch:{ all -> 0x0075 }
            if (r1 == 0) goto L_0x0038
        L_0x0029:
            java.util.concurrent.Executor r0 = r6.executor     // Catch:{ all -> 0x0075 }
            java.lang.Runnable r1 = r6.cleanupRunnable     // Catch:{ all -> 0x0075 }
            r0.execute(r1)     // Catch:{ all -> 0x0075 }
            monitor-exit(r6)
            return r4
        L_0x0032:
            okhttp3.internal.cache.DiskLruCache$Editor r1 = r0.currentEditor     // Catch:{ all -> 0x0075 }
            if (r1 == 0) goto L_0x0025
            monitor-exit(r6)
            return r4
        L_0x0038:
            boolean r1 = r6.mostRecentRebuildFailed     // Catch:{ all -> 0x0075 }
            if (r1 != 0) goto L_0x0029
            okio.BufferedSink r1 = r6.journalWriter     // Catch:{ all -> 0x0075 }
            java.lang.String r2 = "DIRTY"
            okio.BufferedSink r1 = r1.writeUtf8(r2)     // Catch:{ all -> 0x0075 }
            r2 = 32
            okio.BufferedSink r1 = r1.writeByte(r2)     // Catch:{ all -> 0x0075 }
            okio.BufferedSink r1 = r1.writeUtf8(r7)     // Catch:{ all -> 0x0075 }
            r2 = 10
            r1.writeByte(r2)     // Catch:{ all -> 0x0075 }
            okio.BufferedSink r1 = r6.journalWriter     // Catch:{ all -> 0x0075 }
            r1.flush()     // Catch:{ all -> 0x0075 }
            boolean r1 = r6.hasJournalErrors     // Catch:{ all -> 0x0075 }
            if (r1 != 0) goto L_0x0068
            if (r0 == 0) goto L_0x006a
        L_0x005f:
            okhttp3.internal.cache.DiskLruCache$Editor r1 = new okhttp3.internal.cache.DiskLruCache$Editor     // Catch:{ all -> 0x0075 }
            r1.<init>(r0)     // Catch:{ all -> 0x0075 }
            r0.currentEditor = r1     // Catch:{ all -> 0x0075 }
            monitor-exit(r6)
            return r1
        L_0x0068:
            monitor-exit(r6)
            return r4
        L_0x006a:
            okhttp3.internal.cache.DiskLruCache$Entry r0 = new okhttp3.internal.cache.DiskLruCache$Entry     // Catch:{ all -> 0x0075 }
            r0.<init>(r7)     // Catch:{ all -> 0x0075 }
            java.util.LinkedHashMap<java.lang.String, okhttp3.internal.cache.DiskLruCache$Entry> r1 = r6.lruEntries     // Catch:{ all -> 0x0075 }
            r1.put(r7, r0)     // Catch:{ all -> 0x0075 }
            goto L_0x005f
        L_0x0075:
            r0 = move-exception
            monitor-exit(r6)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.edit(java.lang.String, long):okhttp3.internal.cache.DiskLruCache$Editor");
    }

    public synchronized void evictAll() throws IOException {
        synchronized (this) {
            initialize();
            for (Entry removeEntry : (Entry[]) this.lruEntries.values().toArray(new Entry[this.lruEntries.size()])) {
                removeEntry(removeEntry);
            }
            this.mostRecentTrimFailed = false;
        }
    }

    public synchronized void flush() throws IOException {
        if (this.initialized) {
            checkNotClosed();
            trimToSize();
            this.journalWriter.flush();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0046, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0016, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized okhttp3.internal.cache.DiskLruCache.Snapshot get(java.lang.String r4) throws java.io.IOException {
        /*
            r3 = this;
            r2 = 0
            monitor-enter(r3)
            r3.initialize()     // Catch:{ all -> 0x0051 }
            r3.checkNotClosed()     // Catch:{ all -> 0x0051 }
            r3.validateKey(r4)     // Catch:{ all -> 0x0051 }
            java.util.LinkedHashMap<java.lang.String, okhttp3.internal.cache.DiskLruCache$Entry> r0 = r3.lruEntries     // Catch:{ all -> 0x0051 }
            java.lang.Object r0 = r0.get(r4)     // Catch:{ all -> 0x0051 }
            okhttp3.internal.cache.DiskLruCache$Entry r0 = (okhttp3.internal.cache.DiskLruCache.Entry) r0     // Catch:{ all -> 0x0051 }
            if (r0 != 0) goto L_0x0017
        L_0x0015:
            monitor-exit(r3)
            return r2
        L_0x0017:
            boolean r1 = r0.readable     // Catch:{ all -> 0x0051 }
            if (r1 == 0) goto L_0x0015
            okhttp3.internal.cache.DiskLruCache$Snapshot r0 = r0.snapshot()     // Catch:{ all -> 0x0051 }
            if (r0 == 0) goto L_0x0047
            int r1 = r3.redundantOpCount     // Catch:{ all -> 0x0051 }
            int r1 = r1 + 1
            r3.redundantOpCount = r1     // Catch:{ all -> 0x0051 }
            okio.BufferedSink r1 = r3.journalWriter     // Catch:{ all -> 0x0051 }
            java.lang.String r2 = "READ"
            okio.BufferedSink r1 = r1.writeUtf8(r2)     // Catch:{ all -> 0x0051 }
            r2 = 32
            okio.BufferedSink r1 = r1.writeByte(r2)     // Catch:{ all -> 0x0051 }
            okio.BufferedSink r1 = r1.writeUtf8(r4)     // Catch:{ all -> 0x0051 }
            r2 = 10
            r1.writeByte(r2)     // Catch:{ all -> 0x0051 }
            boolean r1 = r3.journalRebuildRequired()     // Catch:{ all -> 0x0051 }
            if (r1 != 0) goto L_0x0049
        L_0x0045:
            monitor-exit(r3)
            return r0
        L_0x0047:
            monitor-exit(r3)
            return r2
        L_0x0049:
            java.util.concurrent.Executor r1 = r3.executor     // Catch:{ all -> 0x0051 }
            java.lang.Runnable r2 = r3.cleanupRunnable     // Catch:{ all -> 0x0051 }
            r1.execute(r2)     // Catch:{ all -> 0x0051 }
            goto L_0x0045
        L_0x0051:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.get(java.lang.String):okhttp3.internal.cache.DiskLruCache$Snapshot");
    }

    public File getDirectory() {
        return this.directory;
    }

    public synchronized long getMaxSize() {
        return this.maxSize;
    }

    public synchronized void initialize() throws IOException {
        if (!$assertionsDisabled) {
            if (!Thread.holdsLock(this)) {
                throw new AssertionError();
            }
        }
        if (!this.initialized) {
            if (this.fileSystem.exists(this.journalFileBackup)) {
                if (!this.fileSystem.exists(this.journalFile)) {
                    this.fileSystem.rename(this.journalFileBackup, this.journalFile);
                } else {
                    this.fileSystem.delete(this.journalFileBackup);
                }
            }
            if (this.fileSystem.exists(this.journalFile)) {
                try {
                    readJournal();
                    processJournal();
                    this.initialized = true;
                    return;
                } catch (IOException e) {
                    Platform.get().log(5, "DiskLruCache " + this.directory + " is corrupt: " + e.getMessage() + ", removing", e);
                    delete();
                    this.closed = false;
                }
            }
            rebuildJournal();
            this.initialized = true;
            return;
        }
        return;
    }

    public synchronized boolean isClosed() {
        return this.closed;
    }

    /* access modifiers changed from: package-private */
    public boolean journalRebuildRequired() {
        return this.redundantOpCount >= 2000 && this.redundantOpCount >= this.lruEntries.size();
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: package-private */
    public synchronized void rebuildJournal() throws IOException {
        if (this.journalWriter != null) {
            this.journalWriter.close();
        }
        BufferedSink buffer = Okio.buffer(this.fileSystem.sink(this.journalFileTmp));
        try {
            buffer.writeUtf8(MAGIC).writeByte(10);
            buffer.writeUtf8(VERSION_1).writeByte(10);
            buffer.writeDecimalLong((long) this.appVersion).writeByte(10);
            buffer.writeDecimalLong((long) this.valueCount).writeByte(10);
            buffer.writeByte(10);
            for (Entry next : this.lruEntries.values()) {
                if (next.currentEditor == null) {
                    buffer.writeUtf8(CLEAN).writeByte(32);
                    buffer.writeUtf8(next.key);
                    next.writeLengths(buffer);
                    buffer.writeByte(10);
                } else {
                    buffer.writeUtf8(DIRTY).writeByte(32);
                    buffer.writeUtf8(next.key);
                    buffer.writeByte(10);
                }
            }
            buffer.close();
            if (this.fileSystem.exists(this.journalFile)) {
                this.fileSystem.rename(this.journalFile, this.journalFileBackup);
            }
            this.fileSystem.rename(this.journalFileTmp, this.journalFile);
            this.fileSystem.delete(this.journalFileBackup);
            this.journalWriter = newJournalWriter();
            this.hasJournalErrors = false;
            this.mostRecentRebuildFailed = false;
        } catch (Throwable th) {
            buffer.close();
            throw th;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x001c, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean remove(java.lang.String r9) throws java.io.IOException {
        /*
            r8 = this;
            r1 = 0
            monitor-enter(r8)
            r8.initialize()     // Catch:{ all -> 0x002e }
            r8.checkNotClosed()     // Catch:{ all -> 0x002e }
            r8.validateKey(r9)     // Catch:{ all -> 0x002e }
            java.util.LinkedHashMap<java.lang.String, okhttp3.internal.cache.DiskLruCache$Entry> r0 = r8.lruEntries     // Catch:{ all -> 0x002e }
            java.lang.Object r0 = r0.get(r9)     // Catch:{ all -> 0x002e }
            okhttp3.internal.cache.DiskLruCache$Entry r0 = (okhttp3.internal.cache.DiskLruCache.Entry) r0     // Catch:{ all -> 0x002e }
            if (r0 == 0) goto L_0x001d
            boolean r2 = r8.removeEntry(r0)     // Catch:{ all -> 0x002e }
            if (r2 != 0) goto L_0x001f
        L_0x001b:
            monitor-exit(r8)
            return r2
        L_0x001d:
            monitor-exit(r8)
            return r1
        L_0x001f:
            long r4 = r8.size     // Catch:{ all -> 0x002e }
            long r6 = r8.maxSize     // Catch:{ all -> 0x002e }
            int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r0 <= 0) goto L_0x0031
            r0 = 1
        L_0x0028:
            if (r0 != 0) goto L_0x001b
            r0 = 0
            r8.mostRecentTrimFailed = r0     // Catch:{ all -> 0x002e }
            goto L_0x001b
        L_0x002e:
            r0 = move-exception
            monitor-exit(r8)
            throw r0
        L_0x0031:
            r0 = r1
            goto L_0x0028
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.remove(java.lang.String):boolean");
    }

    /* access modifiers changed from: package-private */
    public boolean removeEntry(Entry entry) throws IOException {
        if (entry.currentEditor != null) {
            entry.currentEditor.detach();
        }
        for (int i = 0; i < this.valueCount; i++) {
            this.fileSystem.delete(entry.cleanFiles[i]);
            this.size -= entry.lengths[i];
            entry.lengths[i] = 0;
        }
        this.redundantOpCount++;
        this.journalWriter.writeUtf8(REMOVE).writeByte(32).writeUtf8(entry.key).writeByte(10);
        this.lruEntries.remove(entry.key);
        if (!journalRebuildRequired()) {
            return true;
        }
        this.executor.execute(this.cleanupRunnable);
        return true;
    }

    public synchronized void setMaxSize(long j) {
        this.maxSize = j;
        if (this.initialized) {
            this.executor.execute(this.cleanupRunnable);
        }
    }

    public synchronized long size() throws IOException {
        initialize();
        return this.size;
    }

    public synchronized Iterator<Snapshot> snapshots() throws IOException {
        initialize();
        return new Iterator<Snapshot>() {
            final Iterator<Entry> delegate = new ArrayList(DiskLruCache.this.lruEntries.values()).iterator();
            Snapshot nextSnapshot;
            Snapshot removeSnapshot;

            public boolean hasNext() {
                if (this.nextSnapshot != null) {
                    return true;
                }
                synchronized (DiskLruCache.this) {
                    if (DiskLruCache.this.closed) {
                        return false;
                    }
                    while (this.delegate.hasNext()) {
                        Snapshot snapshot = this.delegate.next().snapshot();
                        if (snapshot != null) {
                            this.nextSnapshot = snapshot;
                            return true;
                        }
                    }
                    return false;
                }
            }

            public Snapshot next() {
                if (hasNext()) {
                    this.removeSnapshot = this.nextSnapshot;
                    this.nextSnapshot = null;
                    return this.removeSnapshot;
                }
                throw new NoSuchElementException();
            }

            public void remove() {
                if (this.removeSnapshot != null) {
                    try {
                        DiskLruCache.this.remove(this.removeSnapshot.key);
                    } catch (IOException e) {
                    } catch (Throwable th) {
                        this.removeSnapshot = null;
                        throw th;
                    }
                    this.removeSnapshot = null;
                    return;
                }
                throw new IllegalStateException("remove() before next()");
            }
        };
    }

    /* access modifiers changed from: package-private */
    public void trimToSize() throws IOException {
        while (true) {
            if (!(this.size <= this.maxSize)) {
                removeEntry(this.lruEntries.values().iterator().next());
            } else {
                this.mostRecentTrimFailed = false;
                return;
            }
        }
    }
}
